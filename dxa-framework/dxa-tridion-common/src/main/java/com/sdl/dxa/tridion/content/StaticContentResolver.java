package com.sdl.dxa.tridion.content;

import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotFoundException;
import org.jetbrains.annotations.NotNull;

public interface StaticContentResolver {

    /**
     * Resolves static content with a given path in a given publication.
     * <p>Requires localization path to request the content, so resolves it using localization ID if the path is missing.
     * If you already know publication path, providing it in a request would give you a bit better performance. </p>
     * If file is resolved, caches the file locally, so won't download it again unless it needs to be refreshed.
     *
     * @param requestDto request DTO
     * @return requested static file
     * @throws StaticContentNotFoundException if cannot resolve static file for any reason
     * @dxa.publicApi
     */
    StaticContentItem getStaticContent(@NotNull StaticContentRequestDto requestDto) throws ContentProviderException;

}
