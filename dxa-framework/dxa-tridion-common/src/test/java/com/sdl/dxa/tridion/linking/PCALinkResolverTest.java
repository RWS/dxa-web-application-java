package com.sdl.dxa.tridion.linking;

import com.google.common.base.Strings;
import com.sdl.dxa.common.util.PathUtils;
import com.sdl.dxa.tridion.pcaclient.DefaultPCAClientProvider;
import com.sdl.dxa.tridion.pcaclient.PCAClientConfigurationLoader;
import com.sdl.webapp.tridion.linking.AbstractLinkResolver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PCALinkResolverTest {

    @Mock
    private PCALinkResolver pcaLinkResolver;

    @Before
    public void setup(){
    }

    @Test
    public void resolveLink() {
        Assert.hasText("http://localhost:8882/", pcaLinkResolver.resolveLink("http://localhost:8882/","5",true));
    }
}