package com.sdl.webapp.tridion.mapping.smarttarget;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.smarttarget.SmartTargetPageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmartTargetPageBuilder extends AbstractSmartTargetPageBuilder {
    @Override
    protected void processQueryAndPromotions(Localization localization, SmartTargetPageModel stPageModel, String promotionViewName) {
        // does nothing
        log.warn("SmartTarget functionality is not yet implemented for Web8 environment.");
    }

    @Override
    protected boolean isImplemented() {
        return false;
    }
}
