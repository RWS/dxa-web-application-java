package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.impl.interceptor.csrf.CsrfUtils;
import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlElement;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.ServletContext;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CsrfTokenTagTest {

    @Test
    public void shouldGenerateTokenTag() throws DxaException {
        CsrfTokenTag csrfTokenTag = new CsrfTokenTag();

        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletContext context = new MockServletContext();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        MockPageContext pageContext = new MockPageContext(context, request);
        csrfTokenTag.setPageContext(pageContext);

        //when
        HtmlElement htmlElement = csrfTokenTag.generateElement();

        //then
        assertEquals("input", htmlElement.getStartTag().getTagName());
        assertTrue(htmlElement.getStartTag().getAttributes().contains(new HtmlAttribute("name", CsrfUtils.CSRF_TOKEN_NAME)));
        assertTrue(htmlElement.getStartTag().getAttributes().contains(new HtmlAttribute("type", "hidden")));
        assertTrue(htmlElement.getStartTag().getAttributes().size() == 3);
        assertNotNull(session.getAttribute(CsrfUtils.CSRF_TOKEN_NAME));
    }
}