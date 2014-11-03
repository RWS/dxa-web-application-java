package com.sdl.webapp.common.impl.model;

import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.entity.*;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Implementation of {@code ViewModelRegistry}.
 */
@Component
public class ViewModelRegistryImpl implements ViewModelRegistry {

    private static final Map<String, Class<? extends Entity>> VIEW_ENTITY_CLASS_MAP =
            ImmutableMap.<String, Class<? extends Entity>>builder()
                    .put("Accordion", ItemList.class)
                    .put("Article", Article.class)
                    .put("Breadcrumb", NavigationLinks.class)
                    .put("Carousel", ItemList.class)
                    .put("CookieNotificationBar", Notification.class)
                    .put("Download", Download.class)
                    .put("FooterLinkGroup", LinkList.class)
                    .put("FooterLinks", LinkList.class)
                    .put("HeaderLinks", LinkList.class)
                    .put("HeaderLogo", Teaser.class)
                    .put("LeftNavigation", NavigationLinks.class)
                    .put("LanguageSelector", Configuration.class)
                    .put("List", ContentList.class)
                    .put("OldBrowserNotificationBar", Notification.class)
                    .put("PagedList", ContentList.class)
                    .put("Place", Place.class)
                    .put("SiteMap", SitemapItem.class)
                    .put("SiteMapXml", SitemapItem.class)
                    .put("SocialLinks", TagLinkList.class)
                    .put("SocialSharing", LinkList.class)
                    .put("Tab", ItemList.class)
                    .put("Teaser-ImageOverlay", Teaser.class)
                    .put("Teaser", Teaser.class)
                    .put("TeaserColored", Teaser.class)
                    .put("TeaserHero-ImageOverlay", Teaser.class)
                    .put("TeaserMap", Teaser.class)
                    .put("ThumbnailList", ContentList.class)
                    .put("TopNavigation", NavigationLinks.class)
                    .put("YouTubeVideo", YouTubeVideo.class)
                    .build();

    @Override
    public Class<? extends Entity> getViewEntityClass(String viewName) {
        return VIEW_ENTITY_CLASS_MAP.get(viewName);
    }
}
