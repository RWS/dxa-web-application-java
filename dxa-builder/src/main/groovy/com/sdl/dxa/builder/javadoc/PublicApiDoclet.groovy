package com.sdl.dxa.builder.javadoc

import com.sun.javadoc.Doc
import com.sun.javadoc.DocErrorReporter
import com.sun.javadoc.ProgramElementDoc
import com.sun.javadoc.RootDoc
import com.sun.tools.doclets.standard.Standard

import java.lang.reflect.*

import static com.sdl.dxa.builder.javadoc.Constants.PUBLIC_API_TAG

/**
 * Doclet that supports custom Javadoc tag {@code dxa.publicApi}.
 */
class PublicApiDoclet {

    private static final String COM_SUN_PACKAGE = "com.sun."

    static boolean validOptions(String[][] options, DocErrorReporter reporter) throws IOException {
        return Standard.validOptions(options, reporter)
    }

    static int optionLength(String option) {
        return Standard.optionLength(option)
    }

    static boolean start(RootDoc root) throws IOException {
        return Standard.start((RootDoc) process(root, RootDoc.class))
    }

    static boolean include(Doc doc) {

        if (doc instanceof ProgramElementDoc) {
            def elementDoc = (ProgramElementDoc) doc

            if (elementDoc.containingPackage().tags(PUBLIC_API_TAG).length > 0) {
                return true
            }

            if (elementDoc.containingClass().tags(PUBLIC_API_TAG).length > 0) {
                return true
            }
        }

        return doc.tags(PUBLIC_API_TAG).length > 0
    }

    private static Object process(Object obj, Class expect) {
        if (!obj) {
            return null
        }

        def cls = obj.class
        if (cls.name.startsWith(COM_SUN_PACKAGE)) {
            return Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), new ExcludeHandler(obj))
        }

        if (obj instanceof Object[]) {
            def componentType = expect.componentType
            def array = (Object[]) obj
            def list = new ArrayList(array.length)
            for (int i = 0; i < array.length; i++) {
                def entry = array[i]
                if (entry instanceof Doc && include((Doc) entry)) {
                    list.add(process(entry, componentType))
                }
            }
            return list.toArray((Object[]) Array.newInstance(componentType, list.size()))
        }

        return obj
    }

    private static class ExcludeHandler implements InvocationHandler {

        private Object target

        ExcludeHandler(Object target) {
            this.target = target
        }

        Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (args != null) {
                String methodName = method.getName()
                if (methodName == "compareTo" || methodName == "equals" || methodName == "overrides" || methodName == "subclassOf") {
                    args[0] = unwrap(args[0])
                }
            }

            try {
                return process(method.invoke(target, args), method.getReturnType())
            } catch (InvocationTargetException e) {
                throw e.getTargetException()
            }
        }

        private static Object unwrap(Object proxy) {
            return proxy instanceof Proxy ?
                    ((ExcludeHandler) Proxy.getInvocationHandler(proxy)).target :
                    proxy
        }
    }
}
