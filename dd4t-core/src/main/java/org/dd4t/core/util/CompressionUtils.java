package org.dd4t.core.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.dd4t.core.exceptions.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Utility class for serializing and deserializing objects using GZip and Base64.
 *
 * @author Rai, Mihai
 */
public class CompressionUtils {

    private final static Logger LOG = LoggerFactory.getLogger(CompressionUtils.class);

    /**
     * Compresses a given object to a GZipped byte array.
     *
     * @param object the object to encode
     * @return byte[] representing the compressed object bytes
     * @throws SerializationException if something goes wrong with the streams
     */
    public static <T> byte[] compressGZipGeneric(T object) throws SerializationException {
        ByteArrayOutputStream baos = null;
        GZIPOutputStream gos = null;
        ObjectOutputStream oos = null;

        try {
            baos = new ByteArrayOutputStream();
            gos = new GZIPOutputStream(baos);
            oos = new ObjectOutputStream(gos);

            oos.writeObject(object);
            gos.close();

            return baos.toByteArray();
        } catch (IOException ioe) {
            LOG.error("Compression failed.", ioe);
            throw new SerializationException("Failed to compres object", ioe);
        } finally {
            IOUtils.closeQuietly(oos);
            IOUtils.closeQuietly(gos);
            IOUtils.closeQuietly(baos);
        }
    }

    /**
     * Compresses a given content to a GZipped byte array.
     *
     * @param content the content to encode
     * @return byte[] representing the compressed content bytes
     * @throws SerializationException if something goes wrong with the streams
     */
    public static byte[] compressGZip(String content) throws SerializationException {
        ByteArrayOutputStream baos = null;
        GZIPOutputStream gos = null;

        try {
            baos = new ByteArrayOutputStream();
            gos = new GZIPOutputStream(baos);

            gos.write(content.getBytes("UTF-8"));
            gos.close();

            return baos.toByteArray();
        } catch (IOException ioe) {
            LOG.error("String compression failed.", ioe);
            throw new SerializationException("Failed to compress String", ioe);
        } finally {
            IOUtils.closeQuietly(gos);
            IOUtils.closeQuietly(baos);
        }
    }

    /**
     * Dcompresses a byte array representing a GZip-compressed object into an object of the given class type.
     *
     * @param bytes the byte array to decompress
     * @param <T>   the class type to deserialize the byte array into
     * @return the deserialized object of the given class type
     * @throws SerializationException if something goes wrong with the streams
     */
    public static <T> T decompressGZipGeneric(byte[] bytes) throws SerializationException {
        T result = null;
        ByteArrayInputStream bais = null;
        GZIPInputStream gis = null;
        ObjectInputStream ois = null;

        try {
            bais = new ByteArrayInputStream(bytes);
            gis = new GZIPInputStream(bais);
            ois = new ObjectInputStream(gis);

            result = (T) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            LOG.error("Decompression failed.", e);
            throw new SerializationException("Object failed decompression", e);
        } finally {
            IOUtils.closeQuietly(ois);
            IOUtils.closeQuietly(gis);
            IOUtils.closeQuietly(bais);
        }

        return result;
    }

    /**
     * Dcompresses a byte array representing a GZip-compressed string back into a String.
     *
     * @param bytes the byte array to decompress
     * @return the deserialized object of the given class type
     * @throws SerializationException if something goes wrong with the streams
     */
    public static String decompressGZip(byte[] bytes) throws SerializationException {
        String result = null;
        ByteArrayInputStream bais = null;
        GZIPInputStream gis = null;

        try {
            bais = new ByteArrayInputStream(bytes);
            gis = new GZIPInputStream(bais);

            result = (String) IOUtils.toString(gis);
        } catch (IOException ioe) {
            LOG.error("Decompression failed.", ioe);
            throw new SerializationException("Failed to decompress byte array", ioe);
        } finally {
            IOUtils.closeQuietly(gis);
            IOUtils.closeQuietly(bais);
        }

        return result;
    }

    /**
     * Encodes the given byte array to Base64.
     *
     * @param byteArray the byte array to encode
     * @return String representing the encoded array
     */
    public static String encodeBase64(byte[] byteArray) {
        return Base64.encodeBase64String(byteArray);
    }

    /**
     * Encodes the given String to Base64.
     *
     * @param message the byte array to encode
     * @return String representing the encoded array
     */
    public static String encodeBase64(String message) {
        if (message == null) {
            return null;
        } else {
            return encodeBase64(message.getBytes(Charset.forName("UTF-8")));
        }
    }

    /**
     * Decodes the given string using Base64 algorithm.
     *
     * @param message String representing the message to decode
     * @return byte[] representing the decoded array
     */
    public static byte[] decodeBase64(String message) {
        if (Base64.isBase64(message)) {
            return Base64.decodeBase64(message);
        }

        byte[] result;
        try {
            result = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException uee) {
            LOG.warn("Unsupported Encoding Exception: " + uee.getMessage());
            result = message.getBytes();
        }
        return result;
    }
}
