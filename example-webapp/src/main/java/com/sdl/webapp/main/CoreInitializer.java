package com.sdl.webapp.main;

import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.*;
import com.sdl.webapp.common.api.model.page.PageModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CoreInitializer {
    private final ViewModelRegistry viewModelRegistry;

    // TODO: Why is the core initializer not in the core-module? (NiC)
    // Seems a bit fragmented to have the views in another module and the initialization part in another one...

    @Autowired
    public CoreInitializer(ViewModelRegistry viewModelRegistry) {
        this.viewModelRegistry = viewModelRegistry;
    }

    @PostConstruct
    public void registerViewModelEntityClasses() {
        try {
            viewModelRegistry.registerViewEntityClass("Article", Article.class);
            viewModelRegistry.registerViewEntityClass("Breadcrumb", NavigationLinks.class);
            viewModelRegistry.registerViewEntityClass("Carousel", ItemList.class);
            viewModelRegistry.registerViewEntityClass("CookieNotificationBar", Notification.class);
            viewModelRegistry.registerViewEntityClass("Download", Download.class);
            viewModelRegistry.registerViewEntityClass("FooterLinkGroup", LinkList.class);
            viewModelRegistry.registerViewEntityClass("FooterLinks", LinkList.class);
            viewModelRegistry.registerViewEntityClass("HeaderLinks", LinkList.class);
            viewModelRegistry.registerViewEntityClass("HeaderLogo", Teaser.class);
            viewModelRegistry.registerViewEntityClass("Image", Image.class);
            viewModelRegistry.registerViewEntityClass("LeftNavigation", NavigationLinks.class);
            viewModelRegistry.registerViewEntityClass("LanguageSelector", Configuration.class);
            viewModelRegistry.registerViewEntityClass("List", ContentList.class);
            viewModelRegistry.registerViewEntityClass("OldBrowserNotificationBar", Notification.class);
            viewModelRegistry.registerViewEntityClass("PagedList", ContentList.class);
            viewModelRegistry.registerViewEntityClass("Place", Place.class);
            viewModelRegistry.registerViewEntityClass("SiteMap", SitemapItem.class);
            viewModelRegistry.registerViewEntityClass("SiteMapXml", SitemapItem.class);
            viewModelRegistry.registerViewEntityClass("SocialLinks", TagLinkList.class);
            viewModelRegistry.registerViewEntityClass("SocialSharing", TagLinkList.class);
            viewModelRegistry.registerViewEntityClass("Tab", ItemList.class);
            viewModelRegistry.registerViewEntityClass("Teaser-ImageOverlay", Teaser.class);
            viewModelRegistry.registerViewEntityClass("Teaser", Teaser.class);
            viewModelRegistry.registerViewEntityClass("TeaserColored", Teaser.class);
            viewModelRegistry.registerViewEntityClass("TeaserHero-ImageOverlay", Teaser.class);
            viewModelRegistry.registerViewEntityClass("TeaserMap", Teaser.class);
            viewModelRegistry.registerViewEntityClass("ThumbnailList", ContentList.class);
            viewModelRegistry.registerViewEntityClass("TopNavigation", NavigationLinks.class);
            viewModelRegistry.registerViewEntityClass("YouTubeVideo", YouTubeVideo.class);

            viewModelRegistry.registerPageViewModel("GeneralPage", PageModelImpl.class);
            viewModelRegistry.registerPageViewModel("IncludePage", PageModelImpl.class);
            viewModelRegistry.registerPageViewModel("RedirectPage", PageModelImpl.class);

            viewModelRegistry.registerRegionViewModel("2-Column", RegionModelImpl.class);
            viewModelRegistry.registerRegionViewModel("3-Column", RegionModelImpl.class);
            viewModelRegistry.registerRegionViewModel("4-Column", RegionModelImpl.class);
            viewModelRegistry.registerRegionViewModel("Hero", RegionModelImpl.class);
            viewModelRegistry.registerRegionViewModel("Info", RegionModelImpl.class);
            viewModelRegistry.registerRegionViewModel("Left", RegionModelImpl.class);
            viewModelRegistry.registerRegionViewModel("Links", RegionModelImpl.class);
            viewModelRegistry.registerRegionViewModel("Logo", RegionModelImpl.class);
            viewModelRegistry.registerRegionViewModel("Main", RegionModelImpl.class);
            viewModelRegistry.registerRegionViewModel("Nav", RegionModelImpl.class);
            viewModelRegistry.registerRegionViewModel("Tools", RegionModelImpl.class);


            viewModelRegistry.registerRegionViewModel("Header", RegionModelImpl.class);
            viewModelRegistry.registerRegionViewModel("Footer", RegionModelImpl.class);
            viewModelRegistry.registerRegionViewModel("Left Navigation", RegionModelImpl.class);
            viewModelRegistry.registerRegionViewModel("Content Tools", RegionModelImpl.class);

        } catch (DxaException e) {
            e.printStackTrace();
        }
    }
}

