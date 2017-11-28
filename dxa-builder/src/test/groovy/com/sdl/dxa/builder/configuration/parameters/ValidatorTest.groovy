package com.sdl.dxa.builder.configuration.parameters

import org.junit.Assert
import org.junit.Test

class ValidatorTest {

    @Test
    void shouldValidateDomainNames() {
        //given
        def validator = Validator.domainName()

        //when, then
        Assert.assertTrue(validator.validate("sdl.com"))
        Assert.assertTrue(validator.validate("sdl.com/path"))
        Assert.assertTrue(validator.validate("toplevel.sdl.com"))
        Assert.assertTrue(validator.validate("toplevel.sdl.com/path/test"))


        Assert.assertFalse(validator.validate("http://sdl.com"))
        Assert.assertFalse(validator.validate("http://sdl.com/path"))
        Assert.assertFalse(validator.validate("sdl.com:8080"))
        Assert.assertFalse(validator.validate("sdl.com:8080/[ath"))
    }

    @Test
    void shouldValidateDomainNamesWithPort() {
        //given
        def validator = Validator.domainName(true)

        //when, then
        Assert.assertTrue(validator.validate("sdl.com"))
        Assert.assertTrue(validator.validate("sdl.com/path"))
        Assert.assertTrue(validator.validate("toplevel.sdl.com"))
        Assert.assertTrue(validator.validate("toplevel.sdl.com/path/text"))
        Assert.assertTrue(validator.validate("sdl.com:8080"))
        Assert.assertTrue(validator.validate("sdl.com:8080/path"))


        Assert.assertFalse(validator.validate("http://sdl.com"))
        Assert.assertFalse(validator.validate("http://sdl.com/path"))
        Assert.assertFalse(validator.validate("http://sdl.com:8080"))
        Assert.assertFalse(validator.validate("http://sdl.com:8080/path"))
        Assert.assertFalse(validator.validate("sdl.com:8080:80"))
    }

    @Test
    void shouldValidateInternalURLs() {
        //given
        def validator = Validator.url()

        //when, then
        Assert.assertFalse(validator.validate("http://uadevadosenko.sdl.cor:8083/discovery.svc")) // cor
        Assert.assertTrue(validator.validate("http://uadevadosenko.sdl.corp:8083/discovery.svc")) // corp
    }

    @Test
    void shouldValidateIPs() {
        //given
        def validator = Validator.ip()

        //when, then
        Assert.assertTrue(validator.validate("127.0.0.1"))

        //then
        Assert.assertFalse(validator.validate("http://sdl.com"))
    }

    @Test
    void shouldBeAwareOfCaseSensitivity() {
        Assert.assertTrue(Validator.valueInList("abc", "zxc", "qwe").setCaseSensitive(true).validate("qwe"))
        Assert.assertTrue(Validator.valueInList("abc", "zxc", "qwe").setCaseSensitive(false).validate("QWE"))
        Assert.assertTrue(Validator.valueInList("abc", "zxc", "QwE").setCaseSensitive(false).validate("qwe"))

        Assert.assertFalse(Validator.valueInList("abc", "zxc", "qwe").setCaseSensitive(true).validate("QWE"))

        Assert.assertTrue(Validator.commaInputFromList(["abc", "zxc", "qwe"]).setCaseSensitive(true).validate("qwe, zxc"))
        Assert.assertTrue(Validator.commaInputFromList(["abc", "zxc", "qwe"]).setCaseSensitive(false).validate("QWE, ZXC"))
        Assert.assertTrue(Validator.commaInputFromList(["abc", "ZxC", "QwE"]).setCaseSensitive(false).validate("qwe, zxc"))

        Assert.assertFalse(Validator.commaInputFromList(["abc", "zxc", "qwe"]).setCaseSensitive(true).validate("QWE, ZXC"))
    }
}
