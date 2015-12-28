/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.mvc.controllers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.Binary;
import org.dd4t.contentmodel.BinaryData;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.BinaryFactory;
import org.dd4t.core.resolvers.PublicationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

// TODO: Split stuff between the factory and the real controller stuff

/**
 * @author edwin
 *         <p/>
 *         The BinaryController is responsible for serving binary files (images,
 *         pdf documents etc) that are managed by Tridion. These binaries are
 *         stored in the broker database. On first request the binary is read
 *         from the broker database and is stored on file system in a location
 *         specified by <code>binaryRootFolder</code>.
 *         <p/>
 *         The first request will be time consuming as the cached version of the
 *         file needs to be written to filesystem. For every subsequent request
 *         the cached version will be served.
 *         <p/>
 *         The request mapping supports an array of strings, but we need a way
 *         to configure this without modifying source code.
 *         <p/>
 *         Important Note: concrete implementing classes will need to add the
 *         {@literal @RequestMapping} annotations!
 */
@Controller
public class AbstractBinaryController {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractBinaryController.class);
    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    // The time zone
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    // The if-modified-since header
    private static final String IF_MODIFIED_SINCE = "If-Modified-Since";

    // The last-modified header
    private static final String LAST_MODIFIED = "Last-Modified";

    private static final String CONTENT_LENGTH = "Content-Length";

    @Resource
    private PublicationResolver publicationResolver;

    @Resource
    private BinaryFactory binaryFactory;

    // Set's the root folder for temporary binary storage
    private String binaryRootFolder;

    // Do we use the temporary binary storage or serve directly from the
    // data source?
    private boolean useBinaryStorage = true;

    /**
     * Boolean indicating if context path on the binary URL should be removed, defaults to true
     */
    private boolean removeContextPath = false;

    public void getBinary (final HttpServletRequest request, final HttpServletResponse response) throws ItemNotFoundException {
        String binaryPath = getBinaryPath(request);
        LOG.debug(">> {} binary {}", request.getMethod(), binaryPath);

        int resizeToWidth = -1;
        if (request.getParameterMap().containsKey("resizeToWidth")) {
            resizeToWidth = Integer.parseInt(request.getParameter("resizeToWidth"));
        }

        Binary binary;
        int publicationId = publicationResolver.getPublicationId();
        String path = String.format("%s/%d%s", binaryRootFolder, publicationId, binaryPath);
        if (resizeToWidth > -1) {
            path = insertIntoPath(path, request.getParameter("resizeToWidth"));
        }

        try {

            binary = binaryFactory.getBinaryByURL(binaryPath, publicationId);

            if (binary == null) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                LOG.error("Item not found:" + binaryPath);
                return;
            }

            // Check if anything changed, if nothing changed return a 304
            String modifiedHeader = request.getHeader(IF_MODIFIED_SINCE);
            if (StringUtils.isNotEmpty(modifiedHeader) && createDateFormat().format(binary.getLastPublishedDate().toDate()).equals(modifiedHeader)) {
                response.setStatus(HttpStatus.NOT_MODIFIED.value());
                return;
            }

            fillResponse(request, response, binary, path, resizeToWidth);
        } catch (IOException | FactoryException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            LOG.error(e.getMessage(), e);
            throw new ItemNotFoundException(e);
        } finally {
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException ioe) {
                LOG.error("Failed to close servlet output stream", ioe);
            }
        }
    }

    private String insertIntoPath (String path, String toInsert) {
        int i = path.lastIndexOf('.');
        if (i == 0) {
            LOG.warn("path to binary has no extension: " + path);
            return path;
        }
        return path.substring(0, i + 1) + toInsert + path.substring(i);
    }

    private String getImageType (String path) {
        int i = path.lastIndexOf('.');
        if (i == 0) {
            LOG.warn("path to binary has no extension: " + path + "; assuming the type is png");
            return "png";
        }
        return path.substring(i + 1);
    }

    private void fillResponse (final HttpServletRequest request, final HttpServletResponse response, final Binary binary, final String path, int resizeToWidth) throws IOException {
        InputStream content = null;

        try {
            final long contentLength;
            if (isUseBinaryStorage()) {
                // Check last modified dates
                File binaryFile = new File(path);
                if (!binaryFile.exists() || binary.getLastPublishedDate().isAfter(binaryFile.lastModified())) {
                    if (resizeToWidth == -1) {
                        saveBinary(binary, binaryFile);
                    } else {
                        File tempBinary = new File(path + ".tmp");
                        saveBinary(binary, tempBinary);
                        content = new FileInputStream(tempBinary);
                        BufferedImage before = ImageIO.read(content);
                        int w = before.getWidth();
                        int h = before.getHeight();
                        float factor = (float) resizeToWidth / w;
                        int newH = Math.round(factor * h);

                        BufferedImage after = new BufferedImage(resizeToWidth, newH, BufferedImage.TYPE_INT_RGB);
                        Graphics g = after.createGraphics();
                        g.drawImage(before, 0, 0, resizeToWidth, newH, null);
                        g.dispose();

                        ImageIO.write(after, getImageType(path), binaryFile);
                    }
                }
                content = new FileInputStream(binaryFile);
                contentLength = binaryFile.length();
            } else {
                content = binary.getBinaryData().getInputStream();
                contentLength = binary.getBinaryData().getDataSize();
            }

            response.setContentType(getContentType(binary, path, request));
            response.setHeader(CONTENT_LENGTH, Long.toString(contentLength));
            response.setHeader(LAST_MODIFIED, createDateFormat().format(binary.getLastPublishedDate().toDate()));
            response.setStatus(HttpStatus.OK.value());

            // Write binary data to output stream
            byte[] buffer = new byte[response.getBufferSize()];
            int len;
            while ((len = content.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, len);
            }
        } finally {
            if (content != null) {
                try {
                    content.close();
                } catch (IOException e) {
                    LOG.error("Failed to close binary input stream", e);
                }
            }
        }
    }

    private String getContentType (final Binary binary, final String path, final HttpServletRequest request) {
        String mimeType = null;
        ServletContext servletContext = request.getSession().getServletContext();

        try {
            mimeType = binary.getMimeType();
            if (mimeType == null) {
                File binaryFile = new File(path);
                mimeType = servletContext.getMimeType(binaryFile.getName());
            }
        } catch (Exception e) {
            LOG.error("Error occurred getting mime-type", e);
        }

        if (mimeType == null) {
            LOG.warn("Could not identify mime type for binary file '" + path + "'");
        }

        return mimeType;
    }

    protected String getBinaryPath (final HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        if (removeContextPath) {
            String contextPath = request.getContextPath();
            if (StringUtils.isNotEmpty(contextPath)) {
                requestURI = requestURI.substring(contextPath.length());
            }
        }

        return requestURI;
    }

    private void saveBinary (final Binary binary, final File binaryFile) throws IOException {
        BufferedOutputStream bufferedOutput = null;

        try {
            if (!binaryFile.getParentFile().exists()) {
                if (!binaryFile.getParentFile().mkdirs()) {
                    String msg = "Failed to create parent folder(s) for '" + binaryFile.getPath() + "'";
                    LOG.error(msg);
                    throw new IOException(msg);
                } else {
                    LOG.debug("Parent folders created for '{}'", binaryFile.getPath());
                }
            }
            // Start writing to the output stream
            BinaryData binaryData = binary.getBinaryData();
            byte[] bytes = binaryData.getBytes();

            if (bytes.length > 0) {
                // Construct the BufferedOutputStream object
                bufferedOutput = new BufferedOutputStream(new FileOutputStream(binaryFile));
                bufferedOutput.write(bytes);

                LOG.debug("Found binary data with length {}", bytes.length);
            } else {
                LOG.debug("Binary has no bytes, NOT writing to filesystem!");
            }
        } finally {
            // Close the BufferedOutputStream
            try {
                if (bufferedOutput != null) {
                    bufferedOutput.close();
                }
            } catch (IOException ex) {
                LOG.error("Failed to close output stream!", ex);
            }
        }

        LOG.info("Binary is stored in '{}'", binaryFile.getPath());
    }

    public void setBinaryRootFolder (final String binaryRootFolder) {
        this.binaryRootFolder = binaryRootFolder;
    }

    public boolean isUseBinaryStorage () {
        return useBinaryStorage;
    }

    public void setUseBinaryStorage (final boolean useBinaryStorage) {
        this.useBinaryStorage = useBinaryStorage;
    }

    /**
     * @return the removeContextPath
     */
    public boolean isRemoveContextPath () {
        return removeContextPath;
    }

    /**
     * @param removeContextPath the removeContextPath to set
     */
    public void setRemoveContextPath (final boolean removeContextPath) {
        this.removeContextPath = removeContextPath;
    }

    /**
     * For test purposes: clear the temp binary storage.
     */
    void clearBinaryStorage () throws IOException {
        File tempDir = new File(binaryRootFolder);

        if (tempDir.exists()) {
            FileUtils.deleteDirectory(tempDir);
        }
    }

    /**
     * Create Date format for last-modified headers. Note that a constant
     * SimpleDateFormat is not allowed, it's access should be sync-ed.
     */
    private DateFormat createDateFormat () {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        dateFormat.setTimeZone(GMT);

        return dateFormat;
    }
}
