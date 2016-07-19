package org.example.controller;

import org.example.service.AdminService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @Test
    public void shouldCallAdminService() {
        //given
        when(adminService.refreshLocalization()).thenReturn("/index");

        //when
        String redirectString = adminController.handleRefresh();

        //then
        verify(adminService).refreshLocalization();
        assertEquals("Should redirect to expected path", "redirect:/index", redirectString);
    }
}