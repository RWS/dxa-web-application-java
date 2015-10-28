package com.sdl.dxa.core;

import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.*;
import com.sdl.webapp.common.api.model.page.PageModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.impl.AbstractInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CoreInitializer extends AbstractInitializer {

    // todo Seems a bit fragmented to have the views in another module and the initialization part in another one...
    @Autowired
    public CoreInitializer(ViewModelRegistry viewModelRegistry) {
        super(viewModelRegistry, "Core");
    }

    @PostConstruct
    public void registerViewModelEntityClasses() {

            registerViewModel("Article", Article.class);
            registerViewModel("Accordion", ItemList.class);
            registerViewModel("Breadcrumb", NavigationLinks.class);
            registerViewModel("Carousel", ItemList.class);
            registerViewModel("CookieNotificationBar", Notification.class);
            registerViewModel("Download", Download.class);
            registerViewModel("FooterLinkGroup", LinkList.class);
            registerViewModel("FooterLinks", LinkList.class);
            registerViewModel("HeaderLinks", LinkList.class);
            registerViewModel("HeaderLogo", Teaser.class);
            registerViewModel("Image", Image.class);
            registerViewModel("LeftNavigation", NavigationLinks.class);
            registerViewModel("LanguageSelector", Configuration.class);
            registerViewModel("List", ContentList.class);
            registerViewModel("OldBrowserNotificationBar", Notification.class);
            registerViewModel("PagedList", ContentList.class);
            registerViewModel("Place", Place.class);
            registerViewModel("SiteMap", SitemapItem.class);
            registerViewModel("SiteMapXml", SitemapItem.class);
            registerViewModel("SocialLinks", TagLinkList.class);
            registerViewModel("SocialSharing", TagLinkList.class);
            registerViewModel("Tab", ItemList.class);
            registerViewModel("Teaser-ImageOverlay", Teaser.class);
            registerViewModel("Teaser", Teaser.class);
            registerViewModel("TeaserColored", Teaser.class);
            registerViewModel("TeaserHero-ImageOverlay", Teaser.class);
            registerViewModel("TeaserMap", Teaser.class);
            registerViewModel("ThumbnailList", ContentList.class);
            registerViewModel("TopNavigation", NavigationLinks.class);
            registerViewModel("YouTubeVideo", YouTubeVideo.class);

            registerViewModel("GeneralPage", PageModelImpl.class);
            registerViewModel("IncludePage", PageModelImpl.class);
            registerViewModel("RedirectPage", PageModelImpl.class);

            registerViewModel("2-Column", RegionModelImpl.class);
            registerViewModel("3-Column", RegionModelImpl.class);
            registerViewModel("4-Column", RegionModelImpl.class);
            registerViewModel("Hero", RegionModelImpl.class);
            registerViewModel("Info", RegionModelImpl.class);
            registerViewModel("Left", RegionModelImpl.class);
            registerViewModel("Links", RegionModelImpl.class);
            registerViewModel("Logo", RegionModelImpl.class);
            registerViewModel("Main", RegionModelImpl.class);
            registerViewModel("Nav", RegionModelImpl.class);
            registerViewModel("Tools", RegionModelImpl.class);

            registerViewModel("Header", RegionModelImpl.class);
            registerViewModel("Footer", RegionModelImpl.class);
            registerViewModel("Left Navigation", RegionModelImpl.class);
            registerViewModel("Content Tools", RegionModelImpl.class);


    }
}

