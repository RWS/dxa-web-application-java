package com.sdl.dxa.builder.javadoc

import com.sun.javadoc.*
import org.junit.Test

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class PublicApiDocletTest {

    @Test
    void shouldIncludeMethod() {
        //given
        //when
        def include = PublicApiDoclet.include(mockDocElementAsPublicApi(MethodDoc.class))
        def exclude = PublicApiDoclet.include(mockDocElementNoPublicApi(MethodDoc.class))

        //then
        assertTrue(include)
        assertFalse(exclude)
    }

    @Test
    void shouldIncludeClass() {
        //given
        //when
        def include = PublicApiDoclet.include(mockDocElementAsPublicApi(ClassDoc.class))
        def exclude = PublicApiDoclet.include(mockDocElementNoPublicApi(ClassDoc.class))

        //then
        assertTrue(include)
        assertFalse(exclude)
    }

    @Test
    void shouldIncludeConstructor() {
        //given
        //when
        def include = PublicApiDoclet.include(mockDocElementAsPublicApi(ConstructorDoc.class))
        def exclude = PublicApiDoclet.include(mockDocElementNoPublicApi(ConstructorDoc.class))

        //then
        assertTrue(include)
        assertFalse(exclude)
    }

    @Test
    void shouldIncludePackage() {
        //given
        //when
        def include = PublicApiDoclet.include(mockDocElementAsPublicApi(PackageDoc.class))
        def exclude = PublicApiDoclet.include(mockDocElementNoPublicApi(PackageDoc.class))

        //then
        assertTrue(include)
        assertFalse(exclude)
    }

    @Test
    void shouldIncludeElementIfAnyParentIsIncluded_Method_Pkg() {
        //given
        def method = mockDocElementNoPublicApi(MethodDoc.class)
        def pkg = mockDocElementAsPublicApi(PackageDoc.class)
        when(method.containingPackage()).thenReturn(pkg)

        //when
        def include = PublicApiDoclet.include(method)

        //then
        assertTrue(include)
    }

    @Test
    void shouldIncludeElementIfAnyParentIsIncluded_Method_Cls() {
        //given
        def method = mockDocElementNoPublicApi(MethodDoc.class)
        def cls = mockDocElementAsPublicApi(ClassDoc.class)
        when(method.containingClass()).thenReturn(cls)

        //when
        def include = PublicApiDoclet.include(method)

        //then
        assertTrue(include)
    }

    @Test
    void shouldIncludeElementIfAnyParentIsIncluded_Cls_Pkg() {
        //given
        def cls = mockDocElementNoPublicApi(ClassDoc.class)
        def pkg = mockDocElementAsPublicApi(PackageDoc.class)
        when(cls.containingPackage()).thenReturn(pkg)

        //when
        def include = PublicApiDoclet.include(cls)

        //then
        assertTrue(include)
    }

    @Test
    void shouldIncludeElementIfAnyParentIsIncluded_Cls_Cls() {
        //given
        def cls = mockDocElementNoPublicApi(ClassDoc.class)
        def cls2 = mockDocElementAsPublicApi(ClassDoc.class)
        when(cls.containingClass()).thenReturn(cls2)

        //when
        def include = PublicApiDoclet.include(cls)

        //then
        assertTrue(include)
    }

    private static <T> T mockDocElementAsPublicApi(Class<T> aClass) {
        mockDocElement(aClass, [mock(Tag.class)] as Tag[])
    }

    private static <T> T mockDocElementNoPublicApi(Class<T> aClass) {
        mockDocElement(aClass, [] as Tag[])
    }

    private static <T> T mockDocElement(Class<T> aClass, Tag[] tags) {
        def packageDoc = mock(PackageDoc)
        when(packageDoc.tags(eq(Constants.PUBLIC_API_TAG))).thenReturn([] as Tag[])
        def classDoc = mock(ClassDoc)
        when(classDoc.tags(eq(Constants.PUBLIC_API_TAG))).thenReturn([] as Tag[])

        def docElem = mock(aClass)
        if (docElem instanceof ProgramElementDoc) {
            when(docElem.containingPackage()).thenReturn(packageDoc)
            when(docElem.containingClass()).thenReturn(classDoc)
        }

        when(docElem.tags(eq(Constants.PUBLIC_API_TAG))).thenReturn(tags)
        docElem
    }
}
