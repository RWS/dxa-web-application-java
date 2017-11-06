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

    @Test
    public void shouldCreateAllFoldersForNonExisting() throws ContentProviderException {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(false);

        File parent = mock(File.class);
        when(file.getParentFile()).thenReturn(parent);
        when(parent.exists()).thenReturn(true);

        //when
        //file.exists = false, parent.exists = true
        boolean notExist = FileUtils.isToBeRefreshed(file, 1000L);

        //then
        assertTrue(notExist);
    }

    @Test
    public void shouldSayFalseForFreshFile() throws ContentProviderException {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.lastModified()).thenReturn(1000L);
        File parent = mock(File.class);
        when(file.getParentFile()).thenReturn(parent);
        when(parent.exists()).thenReturn(false);
        when(parent.mkdirs()).thenReturn(true);

        //when
        //file.exists = false, parent.exists = false, parent.mkdirs = true
        boolean newFile = FileUtils.isToBeRefreshed(file, 500L);

        //then
        assertFalse(newFile);
    }

    @Test(expected = ContentProviderException.class)
    public void shouldFailForProblemsWithDirs() throws ContentProviderException {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(false);
        when(file.lastModified()).thenReturn(1000L);
        File parent = mock(File.class);
        when(file.getParentFile()).thenReturn(parent);
        when(parent.exists()).thenReturn(false);
        when(parent.mkdirs()).thenReturn(false);

        //when
        //file.exists = false, parent.exists = false, parent.mkdirs = false
        FileUtils.isToBeRefreshed(file, 500L);
        //then
        //exception
    }

    @Test
    public void shouldFindTheOldFileForRefreshFolderAction() throws ContentProviderException {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.lastModified()).thenReturn(1000L);
        File parent = mock(File.class);
        when(file.getParentFile()).thenReturn(parent);
        when(parent.exists()).thenReturn(true);

        //when
        //file.exists = true, parent.exists = true
        boolean oldFile = FileUtils.isToBeRefreshed(file, 1500L);

        //then
        verify(parent, never()).mkdirs();
        assertTrue(oldFile);
    }

    @Test
    public void shouldFindFavicon() {
        assertFalse(FileUtils.isFavicon("/"));
        assertFalse(FileUtils.isFavicon("/not-favicon.ico"));
        assertTrue(FileUtils.isFavicon("/favicon.ico"));
    }

    @Test
    public void shouldDetectBascifConfigurationFiles() {
        assertFalse(FileUtils.isEssentialConfiguration("/", "/"));
        assertFalse(FileUtils.isEssentialConfiguration("/media/file.png", "/"));
        assertFalse(FileUtils.isEssentialConfiguration("/somepath/somefile.ext", "/"));
        assertFalse(FileUtils.isEssentialConfiguration("/system", "/"));

        assertTrue(FileUtils.isEssentialConfiguration("/system/assets/config.json", "/"));
        assertTrue(FileUtils.isEssentialConfiguration("/system/folder/config.txt", "/"));
        assertTrue(FileUtils.isEssentialConfiguration("/system/system/config.txt", "/"));
        assertTrue(FileUtils.isEssentialConfiguration("/system/v32/system/config.txt", "/"));

        assertFalse(FileUtils.isEssentialConfiguration("/abc", "/abc"));
        assertFalse(FileUtils.isEssentialConfiguration("/abc/media/file.png", "/abc"));
        assertFalse(FileUtils.isEssentialConfiguration("/abc/somepath/somefile.ext", "/abc"));
        assertFalse(FileUtils.isEssentialConfiguration("/abc/system", "/"));

        assertTrue(FileUtils.isEssentialConfiguration("/abc/system/assets/config.json", "/abc"));
        assertTrue(FileUtils.isEssentialConfiguration("/abc/system/folder/config.txt", "/abc"));
        assertTrue(FileUtils.isEssentialConfiguration("/abc/system/system/config.txt", "/abc"));
        assertTrue(FileUtils.isEssentialConfiguration("/abc/system/v32/system/config.txt", "/abc"));
    }

}