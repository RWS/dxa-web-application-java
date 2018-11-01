package com.sdl.dxa.tridion.common;

import com.sdl.dxa.common.dto.ClaimHolder;
import com.sdl.web.pca.client.contentmodel.ContextData;
import com.sdl.web.pca.client.contentmodel.generated.ClaimValue;
import com.sdl.web.pca.client.contentmodel.generated.ClaimValueType;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.sdl.dxa.tridion.common.ContextDataCreator.convertClaimHolderToClaimValue;
import static com.sdl.dxa.tridion.common.ContextDataCreator.createContextData;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ContextDataCreatorTest {

    private static final String STRING = "string";
    private static final String CLAIM_URI = "claim:uri";
    private static final String CLAIM_VALUE = "claim:value";

    @Test(expected = IllegalArgumentException.class)
    public void testConvertClaimHolderToClaimValueException() {
        ClaimHolder holder = new ClaimHolder();

        convertClaimHolderToClaimValue(holder);
    }

    @Test
    public void testCreateContextDataEmpty() {
        Map<String, ClaimHolder> claims = new HashMap<>();

        ContextData contextData = createContextData(claims);

        assertTrue(contextData.getClaimValues().isEmpty());
    }

    @Test
    public void testCreateContextData() {
        Map<String, ClaimHolder> claims = new HashMap<>();
        claims.put(CLAIM_URI, createClaimHolder());

        ContextData contextData = createContextData(claims);

        assertFalse(contextData.getClaimValues().isEmpty());
        assertEquals(CLAIM_URI, contextData.getClaimValues().get(0).getUri());
        assertEquals(CLAIM_VALUE, contextData.getClaimValues().get(0).getValue());
        assertEquals(ClaimValueType.STRING, contextData.getClaimValues().get(0).getType());
    }

    @Test
    public void testConvertClaimHolderToClaimValue() {
        ClaimHolder holder = createClaimHolder();

        ClaimValue claimValue = convertClaimHolderToClaimValue(holder);

        assertEquals(ClaimValueType.STRING, claimValue.getType());
        assertEquals(CLAIM_URI, claimValue.getUri());
        assertEquals(CLAIM_VALUE, claimValue.getValue());
    }

    @NotNull
    private ClaimHolder createClaimHolder() {
        ClaimHolder holder = new ClaimHolder();
        holder.setClaimType(STRING);
        holder.setUri(CLAIM_URI);
        holder.setValue(CLAIM_VALUE);
        return holder;
    }
}