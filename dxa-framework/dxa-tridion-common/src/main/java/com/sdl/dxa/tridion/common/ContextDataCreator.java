package com.sdl.dxa.tridion.common;

import com.google.common.base.Joiner;
import com.sdl.dxa.common.dto.ClaimHolder;
import com.sdl.web.pca.client.contentmodel.ContextData;
import com.sdl.web.pca.client.contentmodel.generated.ClaimValue;
import com.sdl.web.pca.client.contentmodel.generated.ClaimValueType;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;

import java.util.Map;

public class ContextDataCreator {

    @NotNull
    public static ContextData createContextData(Map<String, ClaimHolder> claims) {
        ContextData contextData = new ContextData();
        if (claims.isEmpty()) {
            return contextData;
        }
        for (ClaimHolder holder : claims.values()) {
            contextData.addClaimValule(convertClaimHolderToClaimValue(holder));
        }
        return contextData;
    }

    static ClaimValue convertClaimHolderToClaimValue(ClaimHolder holder) {
        ClaimValue claimValue = new ClaimValue();
        BeanUtils.copyProperties(holder, claimValue);
        String message = "ClaimValueType is not recognized, was used in " +
                holder + ", expected one of " + Joiner.on(";").join(ClaimValueType.values());
        if (holder.getClaimType() == null) throw new IllegalArgumentException(message);
        for (ClaimValueType type : ClaimValueType.values()) {
            if (holder.getClaimType().toUpperCase().equals(type.name())) {
                claimValue.setType(type);
            }
        }
        if (claimValue.getType() == null) throw new IllegalArgumentException(message);
        return claimValue;
    }
}
