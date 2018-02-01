package com.sdl.dxa.api.datamodel.model.unknown;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class UnknownModelDataDeserializer extends JsonDeserializer<UnknownModelData> {

    @Override
    public UnknownModelData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        StringBuilder builder = new StringBuilder();
        deserializeFromStructure(p, ctxt, builder);
        return new UnknownModelData(builder.toString());
    }

    private void deserializeFromStructure(JsonParser p, DeserializationContext ctxt, StringBuilder builder) throws IOException {
        // ok, start parsing a structure
        while (p.hasCurrentToken()) {
            JsonToken token = p.currentToken();

            // if the structure is finished, just return, no need to write end symbol which is added by caller
            if (token.isStructEnd()) {
                return;
            }

            // if we found a nested structure, let's parse it
            if (token.isStructStart()) {
                // but first we need to write its start token since this time we're a caller
                deserializeFromAny(p, builder, token);
                deserializeFromStructure(p, ctxt, builder);
            }

            // and of course we parse the rest (fields, values and end symbols of nested structures)
            deserializeFromAny(p, builder, token);
        }
    }

    private void deserializeFromAny(JsonParser p, StringBuilder builder, JsonToken token) throws IOException {
        // JSON always has quotes for field names and string constants
        boolean needToWrapWithQuotes = token == JsonToken.VALUE_STRING || token == JsonToken.FIELD_NAME;
        if (needToWrapWithQuotes) {
            builder.append("\"");
        }

        // whatever we got here, just write it as text
        builder.append(p.getText());

        if (needToWrapWithQuotes) {
            builder.append("\"");
        }

        // if we just parsed a field name, we always expect value after, so we need ":"
        if (token == JsonToken.FIELD_NAME) {
            builder.append(":");
        }

        // and we move the token to the next
        _nextToken(p, builder);
    }

    private void _nextToken(JsonParser p, StringBuilder builder) throws IOException {
        // check is we need a comma between two tokens
        if (isCommaNeeded(p.currentToken(), p.nextToken())) {
            builder.append(",");
        }
    }

    private boolean isCommaNeeded(JsonToken previousToken, JsonToken nextToken) {
        // we need comma if we are in the middle of an object and next will be another field name
        if (!previousToken.isStructStart() && nextToken == JsonToken.FIELD_NAME) {
            return true;
        }

        // or if we are in array in the middle of values
        if (isValueToken(previousToken) && isValueToken(nextToken)) {
            return true;
        }

        // or we are in array in the middle of objects
        if (previousToken.isStructEnd() && nextToken.isStructStart()) {
            return true;
        }

        // or even the case when we're in array between a value and object (can happen for Object[] since Objects are polymorphic)
        //noinspection RedundantIfStatement
        if (isValueToken(previousToken) && nextToken.isStructStart()) {
            return true;
        }

        return false;
    }

    // no structure start/end and not a field
    private boolean isValueToken(JsonToken previousToken) {
        return !(previousToken.isStructEnd() || previousToken.isStructStart() || previousToken == JsonToken.FIELD_NAME);
    }
}
