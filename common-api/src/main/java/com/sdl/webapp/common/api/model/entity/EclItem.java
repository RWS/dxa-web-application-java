package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

/**
 * ECL Item
 *
 * @author nic
 */
@SemanticEntity(entityName = "ExternalContentItem", vocabulary = SDL_CORE, prefix = "s")
public class EclItem extends MediaItem {

    private String eclUrl;
    private String itemId;

    public String getEclUrl() {
        return this.eclUrl;
    }

    public void setEclUrl(String eclUrl) {
        this.eclUrl = eclUrl;
        this.itemId = this.extractItemIdFromEclUrl(eclUrl);
    }

    public String getItemId() {
        return this.itemId;
    }

    private static String extractItemIdFromEclUrl(String eclUrl) {

        // TODO: Refactor this by using a regex expression instead

        // Return item ID from the eclURI (format: ecl:[tcm id]-[ecl connector id]-[item id]-[type]-file
        //
        StringTokenizer tokenizer = new StringTokenizer(eclUrl, ":-");
        tokenizer.nextToken(); // ecl
        tokenizer.nextToken(); // tcm id
        tokenizer.nextToken(); // ecl connector id
        String itemId = tokenizer.nextToken();

        try {
            return URLDecoder.decode(itemId.replace("!", "%").replace(";", ""), "UTF8");
        }
        catch ( UnsupportedEncodingException e ) {
            return itemId;
        }
    }

    @Override
    public String toString() {
        return "EclItem{" +
                "tcmUri='" + this.getId() + '\'' +
                ", eclUrl='" + eclUrl + '\'' +
                ", itemId='" + itemId + '\'' +
                '}';
    }
}
