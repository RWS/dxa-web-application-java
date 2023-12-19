package org.example.controller;

import org.example.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private AdminController adminController;

    @Test
    public void shouldCallAdminService() {
        //given
        //noinspection unchecked
        Cache<Object, Object> cache = mock(Cache.class);
        when(adminService.refreshLocalization()).thenReturn("/index");
        when(cacheManager.getCacheNames()).thenReturn(Collections.singletonList("cache"));
        when(cacheManager.getCache(eq("cache"))).thenReturn(cache);

        //when
        String redirectString = adminController.handleRefresh();

        //then
        verify(adminService).refreshLocalization();
        assertEquals("redirect:/index", redirectString, "Should redirect to expected path");
        verify(cache).clear();
    }
}