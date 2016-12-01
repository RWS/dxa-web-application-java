package com.sdl.webapp.common.api.mapping.semantic.config;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

@Getter
@ToString
@EqualsAndHashCode
@Slf4j
public final class SemanticField {

    private final String name;

    private final FieldPath path;

    private final boolean multiValue;

    private final Map<FieldSemantics, SemanticField> embeddedFields;

    public SemanticField(String name, String path, boolean multiValue,
                         Map<FieldSemantics, SemanticField> embeddedFields) {
        this.name = name;
        this.path = new FieldPath(path);
        this.multiValue = multiValue;
        this.embeddedFields = ImmutableMap.copyOf(embeddedFields);
    }

    /**
     * Generates XPath for this semantic field respecting the context XPath and type of the field (metadata or content).
     *
     * @param contextXPath the current context XPath, optional, may be {@code null}
     * @return generated XPath
     */
    @NotNull
    public String getXPath(@Nullable String contextXPath) {
        FieldPath pathCopy = path;

        StringBuilder builder = new StringBuilder(pathCopy.isMetadata() ? "tcm:Metadata" : "tcm:Content");

        while (pathCopy.hasTail()) {
            pathCopy = pathCopy.getTail();
            builder.append("/custom:").append(pathCopy.getHead());
        }

        String xPath = builder.toString();
        String contextPathWithoutPredicate = null;
        if (!isNullOrEmpty(contextXPath)) {
            contextPathWithoutPredicate = contextXPath.split("\\[")[0];

            if (!xPath.startsWith(contextPathWithoutPredicate)) {
                // This should not happen, but if it happens, we just stick with the original XPath.
                log.warn("Semantic field's XPath ('{}}') does not match context XPath '{}'.", xPath, contextXPath);
            }
        }

        return contextPathWithoutPredicate == null ? xPath : xPath.replaceFirst(contextPathWithoutPredicate, contextXPath);
    }
}
