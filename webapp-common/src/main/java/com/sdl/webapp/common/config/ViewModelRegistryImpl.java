package com.sdl.webapp.common.config;

import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.model.Entity;
import com.sdl.webapp.common.model.entity.*;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Implementation of {@code ViewModelRegistry}.
 */
@Component
public class ViewModelRegistryImpl implements ViewModelRegistry {

    private static final Map<String, Class<? extends Entity>> ENTITY_VIEW_MODEL_MAP =
            ImmutableMap.<String, Class<? extends Entity>>builder()
                    .put("Article", Article.class)
                    .put("Breadcrumb", NavigationLinks.class)
                    .put("Carousel", ItemList.class)
                    .put("CookieNotificationBar", Notification.class)
                    .put("FooterLinkGroup", LinkList.class)
                    .put("FooterLinks", LinkList.class)
                    .put("HeaderLinks", LinkList.class)
                    .put("HeaderLogo", Teaser.class)
                    .put("LeftNavigation", NavigationLinks.class)
                    .put("LanguageSelector", Configuration.class)
                    .put("List", ContentList.class)
                    .put("OldBrowserNotificationBar", Notification.class)
                    .put("SocialLinks", LinkList.class)
                    .put("SocialSharing", LinkList.class)
                    .put("TeaserMap", Teaser.class)
                    .put("TopNavigation", NavigationLinks.class)
                    .put("YouTubeVideo", YouTubeVideo.class)
                    .build();

    @Override
    public Class<? extends Entity> getEntityViewModelType(String viewName) {
        return ENTITY_VIEW_MODEL_MAP.get(viewName);
    }
}
