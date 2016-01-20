package com.sdl.webapp.tridion.mapping.smarttarget;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.smarttarget.SmartTargetItem;
import com.sdl.webapp.common.api.model.entity.smarttarget.SmartTargetPageModel;
import com.sdl.webapp.common.api.model.entity.smarttarget.SmartTargetPromotion;
import com.sdl.webapp.common.api.model.entity.smarttarget.SmartTargetRegion;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.tridion.smarttarget.SmartTargetException;
import com.tridion.smarttarget.analytics.tracking.ExperimentDimensions;
import com.tridion.smarttarget.query.ExperimentCookie;
import com.tridion.smarttarget.query.Item;
import com.tridion.smarttarget.query.Promotion;
import com.tridion.smarttarget.query.ResultSet;
import com.tridion.smarttarget.query.ResultSetImpl;
import com.tridion.smarttarget.utils.CookieProcessor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dd4t.core.util.TCMURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static com.sdl.webapp.util.dd4t.TcmUtils.buildTcmUri;

@Component
@Order(1000)
@Slf4j
public class SmartTargetPageBuilder extends AbstractSmartTargetPageBuilder {

    @Autowired
    private WebRequestContext webRequestContext;

    @Override
    protected boolean isImplemented() {
        return true;
    }

    @Override
    protected void processQueryAndPromotions(Localization localization, SmartTargetPageModel stPageModel, String promotionViewName) {
        @NonNull final ResultSet resultSet = executeSmartTargetQuery(stPageModel, localization);
        @NonNull final List<Promotion> promotions = resultSet.getPromotions() == null ?
                Collections.<Promotion>emptyList() : resultSet.getPromotions();
        log.debug("SmartTarget query returned {} Promotions.", promotions.size());

        // Filter the Promotions for each SmartTargetRegion
        filterPromotionsForRegion(localization, stPageModel, promotions, promotionViewName);
    }

    private void filterPromotionsForRegion(Localization localization, SmartTargetPageModel stPageModel,
                                           final List<Promotion> promotions, String promotionViewName) {
        // TODO: we shouldn't access ServletRequest in a Model Builder.
        Map<String, ExperimentCookie> existingExperimentCookies = CookieProcessor.getExperimentCookies(webRequestContext.getServletRequest());

        for (final SmartTargetRegion smartTargetRegion : stPageModel.getRegions().get(SmartTargetRegion.class)) {
            final String currentRegionName = smartTargetRegion.getName();

            Map<String, ExperimentCookie> newExperimentCookies = new HashMap<>();
            ExperimentDimensions experimentDimensions = new ExperimentDimensions();
            List<String> itemsOutputInRegion = new ArrayList<>();
            List<String> itemsAlreadyOnPage = new ArrayList<>();
            try {
                ResultSetImpl.filterPromotions(promotions, currentRegionName, smartTargetRegion.getMaxItems(), stPageModel.isAllowDuplicates(),
                        itemsOutputInRegion, itemsAlreadyOnPage, existingExperimentCookies, newExperimentCookies, experimentDimensions);
            } catch (SmartTargetException e) {
                log.error("Smart target exception", e);
                //todo do something more adequate
                continue;
            }

            setXpmMetadataForStaging(localization, promotions, smartTargetRegion, currentRegionName);

            // Create SmartTargetPromotion Entity Models for visible Promotions in the current SmartTargetRegion.
            // It seems that ResultSet.FilterPromotions doesn't really filter on Region name, so we do post-filtering here.
            for (Promotion promotion : filter(promotions, new Predicate<Promotion>() {
                @Override
                public boolean apply(Promotion input) {
                    return input.isVisible() && input.supportsRegion(currentRegionName);
                }
            })) {
                SmartTargetPromotion smartTargetPromotion;
                try {
                    smartTargetPromotion = createPromotionEntity(promotion, promotionViewName, currentRegionName, localization);
                } catch (SmartTargetException e) {
                    log.error("Smart target exception", e);
                    //todo do something more adequate
                    continue;
                }

                if (!smartTargetRegion.isWithSmartTargetContent()) {
                    // Discard any fallback content coming from Content Manager
                    smartTargetRegion.getEntities().clear();
                    smartTargetRegion.setWithSmartTargetContent(true);
                }

                smartTargetRegion.addEntity(smartTargetPromotion);
            }
        }
    }

    private void setXpmMetadataForStaging(Localization localization, final List<Promotion> promotions,
                                          final SmartTargetRegion smartTargetRegion, final String currentRegionName) {
        if (localization.isStaging()) {
            // The SmartTarget API provides the entire XPM markup tag; put it in XpmMetadata["Query"]
            smartTargetRegion.setXpmMetadata(new HashMap<String, String>() {{
                put("Query", ResultSetImpl.getExperienceManagerMarkup(currentRegionName, smartTargetRegion.getMaxItems(), promotions));
            }});
        }
    }

    private SmartTargetPromotion createPromotionEntity(final Promotion promotion, final String promotionViewName,
                                                       final String regionName, final Localization localization) throws SmartTargetException {
        return new SmartTargetPromotion() {{
            setMvcData(MvcDataCreator.creator().fromQualifiedName(promotionViewName).create());

            setXpmMetadata(new HashMap<String, String>() {{
                put("PromotionID", promotion.getPromotionId());
                put("RegionID", regionName);
            }});

            setTitle(promotion.getTitle());
            setSlogan(promotion.getSlogan());


            Collection<Item> filteredItems = filter(promotion.getItems(), new Predicate<Item>() {
                @Override
                public boolean apply(Item input) {
                    return input.isVisible();
                }
            });

            List<SmartTargetItem> items = new ArrayList<>(
                    transform(filteredItems, new Function<Item, SmartTargetItem>() {
                        @Override
                        public SmartTargetItem apply(Item input) {
                            String id = null;
                            try {
                                id = String.format("%s-%s", input.getId(), input.getTemplateUri().getItemId());
                            } catch (SmartTargetException e) {
                                log.error("Smart target exception", e);
                                //todo do something more adequate
                            }
                            return new SmartTargetItem(id, localization);
                        }
                    }));
            setItems(items);
        }};
    }

    @SneakyThrows(ParseException.class)
    private ResultSet executeSmartTargetQuery(SmartTargetPageModel stPageModel, Localization localization) {
        int publicationId = Integer.parseInt(localization.getId());
        TCMURI pageUri = new TCMURI(buildTcmUri(publicationId, Integer.parseInt(stPageModel.getId()), 64));
        TCMURI publicationUri = new TCMURI(buildTcmUri(0, publicationId, 1));

        //todo implement


        return null;
    }
}
