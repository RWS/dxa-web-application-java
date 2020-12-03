package com.sdl.webapp.common.util;

import com.google.common.base.Strings;

/**
 * This class provides a way to use encoded paths for querying with GraphQL
 * content service. The reason for that is that all file paths are stored in DB encoded.
 * This is a copy of .NET implementation here: https://stash.sdl.com/projects/TSI/repos/web-application/browse/Sdl.Web.Common/Utils/UrlEncoding.cs#72
 */
public class UrlEncoder {

    /**
     * Performs an encoding of the url path.
     * @param url Unencoded url path
     * @return Encoded url path.
     */
    public static String urlPartialPathEncode(String url)
    {
        if (Strings.isNullOrEmpty(url)) {
            return url;
        }
        int length = url.length();
        StringBuilder urlCopy = new StringBuilder(length);

        for (int i = 0; i < length; i++)
        {
            char c = url.charAt(i);
            switch (c)
            {
                case ' ': urlCopy.append("%20"); break;
                case '!': urlCopy.append("%21"); break;
                case '"': urlCopy.append("%22"); break;
                case '#': urlCopy.append("%23"); break;
                case '$': urlCopy.append("%24"); break;
                case '%': urlCopy.append("%25"); break;
                case '&': urlCopy.append("%26"); break;
                case '\'': urlCopy.append("%27"); break;
                case '(': urlCopy.append("%28"); break;
                case ')': urlCopy.append("%29"); break;
                case '*': urlCopy.append("%2A"); break;
                case ',': urlCopy.append("%2C"); break;
                case ':': urlCopy.append("%3A"); break;
                case ';': urlCopy.append("%3B"); break;
                case '<': urlCopy.append("%3C"); break;
                case '=': urlCopy.append("%3D"); break;
                case '>': urlCopy.append("%3E"); break;
                case '?': urlCopy.append("%3F"); break;
                case '@': urlCopy.append("%40"); break;
                case '[': urlCopy.append("%5B"); break;
                case ']': urlCopy.append("%5D"); break;
                case '^': urlCopy.append("%5E"); break;
                case '{': urlCopy.append("%7B"); break;
                case '|': urlCopy.append("%7C"); break;
                case '}': urlCopy.append("%7D"); break;
                case '~': urlCopy.append("%7E"); break;
                default: urlCopy.append(c); break;
            }
        }

        return urlCopy.toString();
    }
}
