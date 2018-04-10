package com.sdl.dxa.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

public class PublicApiDocletTest {

    private Set<String> parentPackages;

    private Set<String> parentClasses;

    private static <T extends Doc> T publicApi(Class<T> aClass, String name) {
        return mockDocElement(aClass, new Tag[]{Mockito.mock(Tag.class)}, name);
    }

    private static <T extends Doc> T notPublicApi(Class<T> aClass, String name) {
        return mockDocElement(aClass, new Tag[0], name);
    }

    private static <T extends Doc> T mockDocElement(Class<T> aClass, Tag[] tags, String name) {
        PackageDoc packageDoc = Mockito.mock(PackageDoc.class);
        Mockito.when(packageDoc.tags(Matchers.eq(Constants.PUBLIC_API_TAG))).thenReturn(new Tag[0]);
        ClassDoc classDoc = Mockito.mock(ClassDoc.class);
        Mockito.when(classDoc.tags(Matchers.eq(Constants.PUBLIC_API_TAG))).thenReturn(new Tag[0]);

        T docElem = Mockito.mock(aClass);
        if (docElem instanceof ProgramElementDoc) {
            Mockito.when(((ProgramElementDoc) docElem).containingPackage()).thenReturn(packageDoc);
            Mockito.when(((ProgramElementDoc) docElem).containingClass()).thenReturn(classDoc);
        }


        Mockito.when(docElem.tags(Matchers.eq(Constants.PUBLIC_API_TAG))).thenReturn(tags);
        Mockito.when(docElem.name()).thenReturn(name);
        return docElem;
    }

    @Before
    public void init() {
        this.parentPackages = new HashSet<>();
        this.parentClasses = new HashSet<>();
    }

    private boolean isPublicApiDoc(Doc doc) {
        return PublicApiDoclet.isPublicApiDoc(doc, parentPackages, parentClasses);
    }

    @Test
    public void shouldIncludeMethod() {
        //given
        //when
        boolean include = isPublicApiDoc(publicApi(MethodDoc.class, "publicApi"));
        boolean exclude = isPublicApiDoc(notPublicApi(MethodDoc.class, "notPublicApi"));

        //then
        Assert.assertTrue(include);
        Assert.assertFalse(exclude);
    }


    @Test
    public void shouldIncludeClass() {
        //given
        //when
        boolean include = isPublicApiDoc(publicApi(ClassDoc.class, "publicApi"));
        boolean exclude = isPublicApiDoc(notPublicApi(ClassDoc.class, "notPublicApi"));

        //then
        Assert.assertTrue(include);
        Assert.assertFalse(exclude);
    }

    @Test
    public void shouldIncludeConstructor() {
        //given
        //when
        boolean include = isPublicApiDoc(publicApi(ConstructorDoc.class, "publicApi"));
        boolean exclude = isPublicApiDoc(notPublicApi(ConstructorDoc.class, "notPublicApi"));

        //then
        Assert.assertTrue(include);
        Assert.assertFalse(exclude);
    }

    @Test
    public void shouldIncludePackage() {
        //given
        //when
        boolean include = isPublicApiDoc(publicApi(PackageDoc.class, "publicApi"));
        boolean exclude = isPublicApiDoc(notPublicApi(PackageDoc.class, "notPublicApi"));

        //then
        Assert.assertTrue(include);
        Assert.assertFalse(exclude);
    }

    @Test
    public void shouldIncludeParentIfChildIsIncluded_Method_Pkg() {
        //given
        MethodDoc method = publicApi(MethodDoc.class, "publicApi");
        PackageDoc pkg = notPublicApi(PackageDoc.class, "notPublicApi");
        Mockito.when(method.containingPackage()).thenReturn(pkg);

        //when
        boolean include = isPublicApiDoc(method) && isPublicApiDoc(pkg);

        //then
        Assert.assertTrue(include);
    }

    @Test
    public void shouldExcludeChildIfParentIsIncluded_ButChildIsNot_Method_Pkg() {
        //given
        MethodDoc method = notPublicApi(MethodDoc.class, "notPublicApi");
        PackageDoc pkg = publicApi(PackageDoc.class, "publicApi");
        Mockito.when(method.containingPackage()).thenReturn(pkg);

        //when
        boolean include = isPublicApiDoc(pkg);
        boolean exclude = isPublicApiDoc(method);

        //then
        Assert.assertTrue(include);
        Assert.assertFalse(exclude);
    }

    @Test
    public void shouldIncludeParentIfChildIsIncluded_Method_Cls() {
        //given
        MethodDoc method = publicApi(MethodDoc.class, "publicApi");
        ClassDoc cls = notPublicApi(ClassDoc.class, "notPublicApi");
        Mockito.when(method.containingClass()).thenReturn(cls);

        //when
        boolean include = isPublicApiDoc(method) && isPublicApiDoc(cls);

        //then
        Assert.assertTrue(include);
    }

    @Test
    public void shouldExcludeChildIfParentIsIncluded_ButChildIsNot_Method_Cls() {
        //given
        MethodDoc method = notPublicApi(MethodDoc.class, "notPublicApi");
        ClassDoc cls = publicApi(ClassDoc.class, "publicApi");
        Mockito.when(method.containingClass()).thenReturn(cls);

        //when
        boolean include = isPublicApiDoc(cls);
        boolean exclude = isPublicApiDoc(method);

        //then
        Assert.assertTrue(include);
        Assert.assertFalse(exclude);
    }

    @Test
    public void shouldIncludeParentIfChildIsIncluded_Cls_Pkg() {
        //given
        ClassDoc cls = publicApi(ClassDoc.class, "publicApi");
        PackageDoc pkg = notPublicApi(PackageDoc.class, "notPublicApi");
        Mockito.when(cls.containingPackage()).thenReturn(pkg);

        //when
        boolean include = isPublicApiDoc(cls) && isPublicApiDoc(pkg);

        //then
        Assert.assertTrue(include);
    }

    @Test
    public void shouldExcludeChildIfParentIsIncluded_ButChildIsNot_Cls_Pkg() {
        //given
        ClassDoc cls = notPublicApi(ClassDoc.class, "notPublicApi");
        PackageDoc pkg = publicApi(PackageDoc.class, "publicApi");
        Mockito.when(cls.containingPackage()).thenReturn(pkg);

        //when
        boolean include = isPublicApiDoc(pkg);
        boolean exclude = isPublicApiDoc(cls);

        //then
        Assert.assertTrue(include);
        Assert.assertFalse(exclude);
    }

    @Test
    public void shouldIncludeParentIfChildIsIncluded_Cls_Cls() {
        //given
        ClassDoc cls = publicApi(ClassDoc.class, "publicApi");
        ClassDoc cls2 = notPublicApi(ClassDoc.class, "notPublicApi");
        Mockito.when(cls.containingClass()).thenReturn(cls2);

        //when
        boolean include = isPublicApiDoc(cls);

        //then
        Assert.assertTrue(include);
    }

    @Test
    public void shouldExcludeChildIfParentIsIncluded_ButChildIsNot_Cls_Cls() {
        //given
        ClassDoc cls = notPublicApi(ClassDoc.class, "notPublicApi");
        ClassDoc cls2 = publicApi(ClassDoc.class, "publicApi");
        Mockito.when(cls.containingClass()).thenReturn(cls2);

        //when
        boolean include = isPublicApiDoc(cls2);
        boolean exclude = isPublicApiDoc(cls);

        //then
        Assert.assertTrue(include);
        Assert.assertFalse(exclude);
    }
}
