package com.sdl.webapp.common.util;

import com.sdl.webapp.common.api.content.ContentProviderException;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileUtilsTest {

    @Test
    public void shouldSayIfFileShouldBeRefreshed() throws ContentProviderException {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.lastModified()).thenReturn(1000L);

        //when
        boolean toBeRefreshed = FileUtils.isFileOlderThan(file, 2000L);
        boolean notToBeRefreshed = FileUtils.isFileOlderThan(file, 500L);

        //then
        assertTrue(toBeRefreshed);
        assertFalse(notToBeRefreshed);
    }

    @Test
    public void shouldSayThatFileIsOldBecauseDoesntExist() {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(false);

        //when
        boolean toBeRefreshed = FileUtils.isFileOlderThan(file, 2000L);

        //then
        assertTrue(toBeRefreshed);
    }

    @Test
    public void shouldSayIfPathHasExtensionPart() {
        //when
        boolean hasNoExtension = FileUtils.hasExtension("http://url.com/test");
        boolean hasExtension = FileUtils.hasExtension("http://url.com/test.html");

        //then
        assertTrue(hasExtension);
        assertFalse(hasNoExtension);
    }

    @Test
    public void shouldReturnTrueIfFileDoesntExistAndCreateFolders() {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(false);
        File parent = mock(File.class);
        when(file.getParentFile()).thenReturn(parent);
        when(parent.exists()).thenReturn(false);
        when(parent.mkdirs()).thenReturn(true);

        //when
        boolean shouldBeTrue = FileUtils.parentFolderExists(file, true);

        //then
        assertTrue(shouldBeTrue);
    }

    @Test
    public void shouldReturnFalseIfFileDoesntExistAndCreateFoldersIsNotRequested() {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(false);

        //when
        boolean shouldBeFalse = FileUtils.parentFolderExists(file, false);

        //then
        assertFalse(shouldBeFalse);
    }

    @Test
    public void shouldReturnFalseIfParentFileIsNull() {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.getParentFile()).thenReturn(null);

        //when
        boolean shouldBeFalse = FileUtils.parentFolderExists(file, true);

        //then
        assertFalse(shouldBeFalse);
    }

    @Test
    public void shouldFindParentFolderIfExists() {
        //given
        File file = mock(File.class);
        File parent = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.getParentFile()).thenReturn(parent);
        when(parent.exists()).thenReturn(true);

        //when
        boolean folderExists = FileUtils.parentFolderExists(file, true);

        //then
        assertTrue(folderExists);
        verify(parent, never()).mkdir();
        verify(parent, never()).mkdirs();
    }

    @Test
    public void shouldCreateParentFolderIfNotExists() {
        //given
        File file = mock(File.class);
        File parent = mock(File.class);
        when(file.getParentFile()).thenReturn(parent);
        when(file.exists()).thenReturn(true);
        when(parent.exists()).thenReturn(false);
        when(parent.mkdirs()).thenReturn(true);

        //when
        boolean notCreated = FileUtils.parentFolderExists(file, false);

        //then
        verify(parent, never()).mkdirs();
        verify(parent, never()).mkdir();

        //when
        boolean created = FileUtils.parentFolderExists(file, true);

        //then
        verify(parent).mkdirs();

        //then
        assertTrue(created);
        assertFalse(notCreated);
    }
}