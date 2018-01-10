package org.dd4t.test.mocks;

import org.dd4t.providers.LinkProvider;

/**
 * MockLinkProvider.
 */
public class MockLinkProvider implements LinkProvider {

    @Override
    public String resolveComponent(final String targetComponentUri) {
        return "/resolved/" + targetComponentUri;
    }

    @Override
    public String resolveComponentFromPage(final String targetComponentUri, final String sourcePageUri) {
        return "/resolved/" + targetComponentUri;
    }

    @Override
    public String resolveComponent(final String targetComponentUri, final String excludeComponentTemplateUri) {
        return "/resolved/" + targetComponentUri;
    }
}