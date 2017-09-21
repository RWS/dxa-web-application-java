package com.sdl.webapp.common.api.mapping.semantic.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Strings.isNullOrEmpty;

@Getter
@ToString
@EqualsAndHashCode
@Slf4j
public final class FieldPath implements WithXPath {

    private final String head;

    private final FieldPath tail;

    public FieldPath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        final int i = path.indexOf('/');
        if (i < 0) {
            this.head = path;
            this.tail = null;
        } else {
            this.head = path.substring(0, i);
            this.tail = new FieldPath(path.substring(i));
        }
    }

    public boolean hasTail() {
        return tail != null;
    }

    public boolean isMetadata() {
        return "Metadata".equals(head);
    }

    @Override
    @NotNull
    public String getXPath(@Nullable String contextXPath) {
        FieldPath pathCopy = this;

        StringBuilder builder = new StringBuilder(pathCopy.isMetadata() ? "tcm:Metadata" : "tcm:Content");

        while (true) {
            builder.append("/custom:").append(pathCopy.getHead());
            if (!pathCopy.hasTail()) {
                break;
            }
            pathCopy = pathCopy.getTail();
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
