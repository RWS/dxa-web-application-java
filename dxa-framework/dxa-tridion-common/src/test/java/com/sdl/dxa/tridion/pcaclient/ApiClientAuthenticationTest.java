package com.sdl.dxa.tridion.pcaclient;

import com.sdl.web.client.impl.OAuthTokenProvider;
import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ApiClientAuthenticationTest {

    @Mock
    private OAuthTokenProvider tokenProvider;

    @Spy
    @InjectMocks
    private ApiClientAuthentication authentication = new ApiClientAuthentication();

    @Test
    public void applyManualAuthentication() {
        authentication.setTokenProvider(tokenProvider);
        Mockito.when(tokenProvider.getToken()).thenReturn("12345");
        HttpRequest request = new BasicHttpRequest("GET", "http://service");

        authentication.applyManualAuthentication(request);

        assertEquals("Bearer12345", request.getFirstHeader("Authorization").getValue());
    }
}
