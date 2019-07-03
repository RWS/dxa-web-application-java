package com.sdl.dxa.tridion.common;

import com.google.common.base.Joiner;
import com.sdl.dxa.common.dto.ClaimHolder;
import com.sdl.web.pca.client.contentmodel.ContextData;
import com.sdl.web.pca.client.contentmodel.generated.ClaimValue;
import com.sdl.web.pca.client.contentmodel.generated.ClaimValueType;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class ContextDataCreator {

    private static final String CLAIM_TYPE_NOT_FOUND_ERROR = "ClaimValueType is not recognized, was used in '%s'" +
            ", expected one of " + Joiner.on(";").join(ClaimValueType.values());

    @NotNull
    public static ContextData createContextData(ClaimHolder claims) {
        HashMap<String, ClaimHolder> claimsMap = new HashMap<>();
        claimsMap.put("-", claims);
        return createContextData(claimsMap);
    }

    public static ContextData createContextData(Map<String, ClaimHolder> claims) {
        ContextData contextData = new ContextData();
        if (claims.isEmpty()) {
            return contextData;
        }
        for (ClaimHolder holder : claims.values()) {
            contextData.addClaimValue(convertClaimHolderToClaimValue(holder));
        }
        return contextData;
    }

    public static ClaimValue convertClaimHolderToClaimValue(ClaimHolder holder) {
        ClaimValue claimValue = new ClaimValue();
        BeanUtils.copyProperties(holder, claimValue);
        for (ClaimValueType type : ClaimValueType.values()) {
            if (type.name().equalsIgnoreCase(holder.getClaimType())) {
                claimValue.setType(type);
            }
        }
        if (claimValue.getType() == null) {
            throw new IllegalArgumentException(format(CLAIM_TYPE_NOT_FOUND_ERROR, holder));
        }
        return claimValue;
    }

}
