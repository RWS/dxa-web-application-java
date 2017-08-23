package org.dd4t.resolvers;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public class LinkResolverTest {

    private static final String TEST_TEXT="<a href=\"http://dnsdsd%COMPONENTURI%/\">%COMPONENTTITLE%</a>";
    private static final String COMPONENT_URI = "%COMPONENTURI%";
    private static final String COMPONENT_TITLE = "%COMPONENTTITLE%";
    private static final String TCM_URI = "tcm:2-2-16";
    private static final String COMPONENT_TEST = "Component Test";

    @Test
    public void testReplacements() throws UnsupportedEncodingException {

        String outputFromBufferTest = replacePlaceholders(TEST_TEXT, COMPONENT_URI, TCM_URI);
        outputFromBufferTest = replacePlaceholders(outputFromBufferTest, COMPONENT_TITLE, COMPONENT_TEST);

        String outputFromBuilderTest = replacePlaceholdersStringBuilder(TEST_TEXT, COMPONENT_URI, TCM_URI);
        outputFromBuilderTest = replacePlaceholdersStringBuilder(outputFromBuilderTest, COMPONENT_TITLE, COMPONENT_TEST);

        assertEquals(outputFromBufferTest, outputFromBuilderTest);
    }


    private String replacePlaceholders(String resolvedUrl, String placeholder, String replacementText) throws
            UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        if (!StringUtils.isEmpty(replacementText)) {
            Pattern p = Pattern.compile(placeholder);
            Matcher m = p.matcher(resolvedUrl);

            while (m.find()) {
                m.appendReplacement(sb, replacementText);
            }
            m.appendTail(sb);
        }
        return sb.toString();
    }

    private String replacePlaceholdersStringBuilder(String resolvedUrl, String placeholder, String replacementText) throws
            UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder();

        if (!StringUtils.isEmpty(replacementText)) {
            Pattern p = Pattern.compile(placeholder);
            Matcher m = p.matcher(resolvedUrl);


            int pos = 0;
            while(m.find()) {
                sb.append(resolvedUrl, pos, m.start());
                pos = m.end();
                sb.append(replacementText);
            }
            sb.append(resolvedUrl, pos, resolvedUrl.length());
        }
        return sb.toString();
    }
}
