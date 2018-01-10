package com.sdl.dxa.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.sdl.dxa.javadoc.Constants.PUBLIC_API_TAG;

/**
 * Doclet that supports custom Javadoc tag {@code dxa.publicApi} to decide whether the element should be included in JavaDoc.
 * <p>The following list of elements is included:</p>
 * <ul>
 * <li>{@link RootDoc} always</li>
 * <li>Any element with {@code @dxa.publicApi [optional text]} tag set in its javadoc</li>
 * <li>Any package that contains an included element</li>
 * <li>Any class that contains an included element</li>
 * </ul>
 * <p>All the transitive inclusions happen only upstream, meaning e.g. that class is included if method is included, but
 * method is not included simply because class is. This has been done intentionally to motivate to include as less as possible,
 * and kind of force to write comments to each inclusion.</p>
 *
 * @see <a href="https://maven.apache.org/plugins/maven-javadoc-plugin/examples/alternate-doclet.html">How to use this Doclet in Maven</a>
 */
public class PublicApiDoclet {

    private static final String COM_SUN_PACKAGE = "com.sun.";

    private PublicApiDoclet() {
        // empty
    }

    @SuppressWarnings("unused")
    public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
        return Standard.validOptions(options, reporter);
    }

    @SuppressWarnings("unused")
    public static int optionLength(String option) {
        return Standard.optionLength(option);
    }

    @SuppressWarnings("unused")
    public static boolean start(RootDoc root) {
        return Standard.start((RootDoc) process(root, RootDoc.class, new HashSet<>(), new HashSet<>()));
    }

    public static boolean isPublicApiDoc(Doc doc, Set<String> parentPackages, Set<String> parentClasses) {
        return _isPublicApi(doc, parentPackages, parentClasses) && _memorizeParents(doc, parentPackages, parentClasses);
    }

    private static boolean _isPublicApi(Doc doc, Set<String> parentPackages, Set<String> parentClasses) {
        if (doc instanceof RootDoc) {
            return true;
        }
        if (_isDeclaredPublicApi(doc)) {
            return true;
        }
        if (doc instanceof PackageDoc && parentPackages.contains(doc.name())) {
            return true;
        }
        if (doc instanceof ClassDoc) {
            if (parentClasses.contains(doc.name())) {
                return true;
            }

            ClassDoc classDoc = (ClassDoc) doc;
            if (classDoc.methods() != null) {
                Optional<MethodDoc> methodDoc = Arrays.stream(classDoc.methods())
                        .filter(PublicApiDoclet::_isDeclaredPublicApi)
                        .findAny();
                if (methodDoc.isPresent()) {
                    return _memorizeParents(methodDoc.get(), parentPackages, parentClasses);
                }
            }
        }
        if (doc instanceof MethodDoc) {
            //let's check if this is a method of an interface that's a part of public API
            ClassDoc containingClass = ((MethodDoc) doc).containingClass();
            //we can decide for interfaces or final util classes
            boolean possibleToGuess = Utils.forcesChildrenToBePublic(containingClass);
            return possibleToGuess && _isDeclaredPublicApi(containingClass) &&
                    _memorizeParents(doc, parentPackages, parentClasses);
        }
        return false;
    }

    private static boolean _isDeclaredPublicApi(Doc doc) {
        return doc.tags(PUBLIC_API_TAG).length > 0;
    }

    private static boolean _memorizeParents(Doc doc, Set<String> parentPackages, Set<String> parentClasses) {
        if (doc instanceof ProgramElementDoc) {
            ProgramElementDoc elementDoc = (ProgramElementDoc) doc;
            parentPackages.add(elementDoc.containingPackage().name());

            if (elementDoc.containingClass() != null) {
                parentClasses.add(elementDoc.containingClass().name());
            }
        }
        return true;
    }

    private static Object process(Object obj, Class expect, Set<String> parentPackages, Set<String> parentClasses) {
        if (obj == null) {
            return null;
        }


        Class cls = obj.getClass();
        if (cls.getName().startsWith(COM_SUN_PACKAGE)) {
            return Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), new ExcludeHandler(obj, parentPackages, parentClasses));
        }


        if (obj instanceof Object[]) {
            Class<?> componentType = expect.getComponentType();
            if (componentType == null) {
                return null;
            }

            Object[] array = (Object[]) obj;
            List<Object> list = new ArrayList<>(array.length);
            for (Object entry : array) {
                if (!(entry instanceof Doc) || isPublicApiDoc((Doc) entry, parentPackages, parentClasses)) {
                    list.add(process(entry, componentType, parentPackages, parentClasses));
                }
            }

            return list.toArray((Object[]) Array.newInstance(componentType, list.size()));
        }

        return obj;
    }

    private static class ExcludeHandler implements InvocationHandler {

        private Object target;

        private Set<String> parentPackages;

        private Set<String> parentClasses;

        ExcludeHandler(Object target, Set<String> parentPackages, Set<String> parentClasses) {
            this.target = target;
            this.parentPackages = parentPackages;
            this.parentClasses = parentClasses;
        }

        private static Object unwrap(Object proxy) {
            return proxy instanceof Proxy ? ((ExcludeHandler) Proxy.getInvocationHandler(proxy)).target : proxy;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (args != null) {
                String methodName = method.getName();
                if ("compareTo".equals(methodName) || "equals".equals(methodName) || "overrides".equals(methodName) || "subclassOf".equals(methodName)) {
                    args[0] = unwrap(args[0]);
                }
            }
            return process(method.invoke(target, args), method.getReturnType(), parentPackages, parentClasses);
        }
    }
}
