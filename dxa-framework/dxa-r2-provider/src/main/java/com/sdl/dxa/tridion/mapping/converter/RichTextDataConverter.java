package com.sdl.dxa.tridion.mapping.converter;

import com.google.common.base.Strings;
import com.sdl.dxa.R2;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.RichTextData;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.RichTextFragment;
import com.sdl.webapp.common.api.model.RichTextFragmentImpl;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
@R2
public class RichTextDataConverter implements SourceConverter<RichTextData> {

    private static final Pattern START_LINK =
            // <p>Text <a data="1" href="tcm:1-2" data2="2">link text</a><!--CompLink tcm:1-2--> after text</p>
            // beforeWithLink: <p>Text <a data="1" href=
            // before: <p>Text
            // tcmUri: tcm:1-2
            // afterWithLink: " data2="2">link text</a><!--CompLink tcm:1-2--> after text</p>
            // after: link text</a><!--CompLink tcm:1-2--> after text</p>
            Pattern.compile("(?<beforeWithLink>(?<before>.*?)<a[^>]+href=\")(?<tcmUri>tcm:\\d+-\\d+)(?<afterWithLink>\"[^>]*>(?<after>.*))",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern END_LINK =
            // <p>Text <a data="1" href="resolved-link" data2="2">link text</a><!--CompLink tcm:1-2--> after text</p>
            // beforeWithLink: <p>Text <a data="1" href="resolved-link" data2="2">link text</a>
            // before: <p>Text <a data="1" href="resolved-link" data2="2">link text
            // tcmUri: tcm:1-2
            // after: after text</p>
            Pattern.compile("(?<beforeWithLink>(?<before>.*?)</a>)<!--CompLink\\s(?<tcmUri>tcm:\\d+-\\d+)-->(?<after>.*)",
                    Pattern.CASE_INSENSITIVE);

    private final LinkResolver linkResolver;

    private final WebRequestContext webRequestContext;

    private final ModelBuilderPipeline modelBuilderPipeline;

    @Autowired
    public RichTextDataConverter(LinkResolver linkResolver, WebRequestContext webRequestContext, ModelBuilderPipeline modelBuilderPipeline) {
        this.linkResolver = linkResolver;
        this.webRequestContext = webRequestContext;
        this.modelBuilderPipeline = modelBuilderPipeline;
    }

    @Override
    public List<Class<? extends RichTextData>> getTypes() {
        return Collections.singletonList(RichTextData.class);
    }

    @Override
    public Object convert(RichTextData toConvert, TypeDescriptor targetType, SemanticField semanticField, DefaultSemanticFieldDataProvider dataProvider) {
        Class<?> objectType = targetType.getObjectType();

        Set<String> linksNotResolved = new HashSet<>();
        RichText richText = new RichText(toConvert.getValues().stream()
                .map(fragment -> {
                    if (fragment instanceof String) {
                        String stringFragment = (String) fragment;
                        stringFragment = processStartLinks(stringFragment, linksNotResolved);
                        stringFragment = processEndLinks(stringFragment, linksNotResolved);
                        return new RichTextFragmentImpl(stringFragment);
                    } else {
                        log.debug("Fragment {} is a not a string but perhaps EntityModelData, skipping link resolving");
                        MediaItem mediaItem = modelBuilderPipeline.createEntityModel((EntityModelData) fragment,
                                MediaItem.class);
                        mediaItem.setEmbedded(true);
                        if (mediaItem.getMvcData() == null) {
                            mediaItem.setMvcData(mediaItem.getDefaultMvcData());
                        }
                        return mediaItem;
                    }
                })
                .map(RichTextFragment.class::cast)
                .collect(Collectors.toList()));

        return objectType == String.class ? richText.toString() : richText;
    }

    @NotNull
    private String processStartLinks(@NotNull String stringFragment, @NotNull Set<String> linksNotResolved) {
        String fragment = stringFragment;
        Matcher startMatcher = START_LINK.matcher(fragment);

        while (startMatcher.matches()) {
            String tcmUri = startMatcher.group("tcmUri");
            // TODO TSI-1267: Component Links resolving should be done in Model Service.
            String link = linkResolver.resolveLink(tcmUri, webRequestContext.getLocalization().getId());
            if (Strings.isNullOrEmpty(link)) {
                log.info("Cannot resolve link to {}, suppressing link", tcmUri);
                fragment = startMatcher.group("before") + startMatcher.group("after");
                linksNotResolved.add(tcmUri);
            } else {
                log.debug("Resolved link to {} as {}", tcmUri, link);
                fragment = startMatcher.group("beforeWithLink") + link + startMatcher.group("afterWithLink");
            }

            startMatcher = START_LINK.matcher(fragment);
        }
        return fragment;
    }

    @NotNull
    private String processEndLinks(@NotNull String stringFragment, @NotNull Set<String> linksNotResolved) {
        String fragment = stringFragment;
        Matcher endMatcher = END_LINK.matcher(fragment);
        while (endMatcher.matches()) {
            String tcmUri = endMatcher.group("tcmUri");
            if (linksNotResolved.contains(tcmUri)) {
                log.trace("Tcm Uri {} was not resolved, removing end </a> with marker");
                fragment = endMatcher.group("before") + endMatcher.group("after");
            } else {
                log.trace("Tcm Uri {} was resolved, removing only marker, leaving </a>");
                fragment = endMatcher.group("beforeWithLink") + endMatcher.group("after");
            }

            endMatcher = END_LINK.matcher(fragment);
        }
        return fragment;
    }
}
