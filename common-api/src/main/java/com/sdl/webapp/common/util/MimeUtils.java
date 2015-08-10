package com.sdl.webapp.common.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * MimeUtils
 *
 * @author nic
 */
public final class MimeUtils {

    private MimeUtils() {}

    static HashMap<String,String> MIME_TYPES = new HashMap<>();

    static {

        // TODO: Complete this list
        MIME_TYPES.put("ai", "application/postscript");
        MIME_TYPES.put("aif", "audio/x-aiff");
        MIME_TYPES.put("aifc", "audio/x-aiff");
        MIME_TYPES.put("aiff", "audio/x-aiff");
        MIME_TYPES.put("asc", "text/plain");
        MIME_TYPES.put("asf", "video/x.ms.asf");
        MIME_TYPES.put("asx", "video/x.ms.asx");
        MIME_TYPES.put("au", "audio/basic");
        MIME_TYPES.put("avi", "video/x-msvideo");
        MIME_TYPES.put("bin", "application/octet-stream");
        MIME_TYPES.put("cab", "application/x-cabinet");
        MIME_TYPES.put("cdf", "application/x-netcdf");
        MIME_TYPES.put("class", "application/java-vm");
        MIME_TYPES.put("cpio", "application/x-cpio");
        MIME_TYPES.put("cpt", "application/mac-compactpro");
        MIME_TYPES.put("crt", "application/x-x509-ca-cert");
        MIME_TYPES.put("csh", "application/x-csh");
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("csv", "text/comma-separated-values");
        MIME_TYPES.put("dcr", "application/x-director");
        MIME_TYPES.put("dir", "application/x-director");
        MIME_TYPES.put("dll", "application/x-msdownload");
        MIME_TYPES.put("dms", "application/octet-stream");
        MIME_TYPES.put("doc", "application/msword");
        MIME_TYPES.put("dtd", "application/xml-dtd");
        MIME_TYPES.put("dvi", "application/x-dvi");
        MIME_TYPES.put("dxr", "application/x-director");
        MIME_TYPES.put("eps", "application/postscript");
        MIME_TYPES.put("etx", "text/x-setext");
        MIME_TYPES.put("exe", "application/octet-stream");
        MIME_TYPES.put("ez", "application/andrew-inset");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("gtar", "application/x-gtar");
        MIME_TYPES.put("gz", "application/gzip");
        MIME_TYPES.put("gzip", "application/gzip");
        MIME_TYPES.put("hdf", "application/x-hdf");
        MIME_TYPES.put("htc", "text/x-component");
        MIME_TYPES.put("hqx", "application/mac-binhex40");
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("htm", "text/html");
        MIME_TYPES.put("ice", "x-conference/x-cooltalk");
        MIME_TYPES.put("ief", "image/ief");
        MIME_TYPES.put("iges", "model/iges");
        MIME_TYPES.put("igs", "model/iges");
        MIME_TYPES.put("jar", "application/java-archive");
        MIME_TYPES.put("java", "text/plain");
        MIME_TYPES.put("jnlp", "application/x-java-jnlp-file");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("jpe", "image/jpeg");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("js", "application/x-javascript");
        MIME_TYPES.put("jsp", "text/plain");
        MIME_TYPES.put("kar", "audio/midi");
        MIME_TYPES.put("latex", "application/x-latex");
        MIME_TYPES.put("lha", "application/octet-stream");
        MIME_TYPES.put("lzh", "application/octet-stream");
        MIME_TYPES.put("man", "application/x-troff-man");
        MIME_TYPES.put("mathml", "application/mathml+xml");
        MIME_TYPES.put("me", "application/x-troff-me");
        MIME_TYPES.put("mesh", "model/mesh");
        MIME_TYPES.put("mid", "audio/midi");
        MIME_TYPES.put("midi", "audio/midi");
        MIME_TYPES.put("mif", "application/vnd.mif");
        MIME_TYPES.put("mol", "chemical/x-mdl-molfile");
        MIME_TYPES.put("movie", "video/x-sgi-movie");
        MIME_TYPES.put("mov", "video/quicktime");
        MIME_TYPES.put("mp2", "audio/mpeg");
        MIME_TYPES.put("mp3", "audio/mpeg");
        MIME_TYPES.put("mpeg", "video/mpeg");
        MIME_TYPES.put("mpe", "video/mpeg");
        MIME_TYPES.put("mpga", "audio/mpeg");
        MIME_TYPES.put("mpg", "video/mpeg");
        MIME_TYPES.put("ms", "application/x-troff-ms");
        MIME_TYPES.put("msh", "model/mesh");
        MIME_TYPES.put("msi", "application/octet-stream");
        MIME_TYPES.put("nc", "application/x-netcdf");
        MIME_TYPES.put("oda", "application/oda");
        MIME_TYPES.put("ogg", "application/ogg");
        MIME_TYPES.put("pbm", "image/x-portable-bitmap");
        MIME_TYPES.put("pdb", "chemical/x-pdb");
        MIME_TYPES.put("pdf", "application/pdf");
        MIME_TYPES.put("pgm", "image/x-portable-graymap");
        MIME_TYPES.put("pgn", "application/x-chess-pgn");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("pnm", "image/x-portable-anymap");
        MIME_TYPES.put("ppm", "image/x-portable-pixmap");
        MIME_TYPES.put("ppt", "application/vnd.ms-powerpoint");
        MIME_TYPES.put("ps", "application/postscript");
        MIME_TYPES.put("qt", "video/quicktime");
        MIME_TYPES.put("ra", "audio/x-pn-realaudio");
        MIME_TYPES.put("ra", "audio/x-realaudio");
        MIME_TYPES.put("ram", "audio/x-pn-realaudio");
        MIME_TYPES.put("ras", "image/x-cmu-raster");
        MIME_TYPES.put("rdf", "application/rdf+xml");
        MIME_TYPES.put("rgb", "image/x-rgb");
        MIME_TYPES.put("rm", "audio/x-pn-realaudio");
        MIME_TYPES.put("roff", "application/x-troff");
        MIME_TYPES.put("rpm", "application/x-rpm");
        MIME_TYPES.put("rpm", "audio/x-pn-realaudio");
        MIME_TYPES.put("rtf", "application/rtf");
        MIME_TYPES.put("rtx", "text/richtext");
        MIME_TYPES.put("ser", "application/java-serialized-object");
        MIME_TYPES.put("sgml", "text/sgml");
        MIME_TYPES.put("sgm", "text/sgml");
        MIME_TYPES.put("sh", "application/x-sh");
        MIME_TYPES.put("shar", "application/x-shar");
        MIME_TYPES.put("silo", "model/mesh");
        MIME_TYPES.put("sit", "application/x-stuffit");
        MIME_TYPES.put("skd", "application/x-koan");
        MIME_TYPES.put("skm", "application/x-koan");
        MIME_TYPES.put("skp", "application/x-koan");
        MIME_TYPES.put("skt", "application/x-koan");
        MIME_TYPES.put("smi", "application/smil");
        MIME_TYPES.put("smil", "application/smil");
        MIME_TYPES.put("snd", "audio/basic");
        MIME_TYPES.put("spl", "application/x-futuresplash");
        MIME_TYPES.put("src", "application/x-wais-source");
        MIME_TYPES.put("sv4cpio", "application/x-sv4cpio");
        MIME_TYPES.put("sv4crc", "application/x-sv4crc");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("swf", "application/x-shockwave-flash");
        MIME_TYPES.put("t", "application/x-troff");
        MIME_TYPES.put("tar", "application/x-tar");
        MIME_TYPES.put("tar.gz", "application/x-gtar");
        MIME_TYPES.put("tcl", "application/x-tcl");
        MIME_TYPES.put("tex", "application/x-tex");
        MIME_TYPES.put("texi", "application/x-texinfo");
        MIME_TYPES.put("texinfo", "application/x-texinfo");
        MIME_TYPES.put("tgz", "application/x-gtar");
        MIME_TYPES.put("tiff", "image/tiff");
        MIME_TYPES.put("tif", "image/tiff");
        MIME_TYPES.put("tr", "application/x-troff");
        MIME_TYPES.put("tsv", "text/tab-separated-values");
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("ustar", "application/x-ustar");
        MIME_TYPES.put("vcd", "application/x-cdlink");
        MIME_TYPES.put("vrml", "model/vrml");
        MIME_TYPES.put("vxml", "application/voicexml+xml");
        MIME_TYPES.put("wav", "audio/x-wav");
        MIME_TYPES.put("wbmp", "image/vnd.wap.wbmp");
        MIME_TYPES.put("wmlc", "application/vnd.wap.wmlc");
        MIME_TYPES.put("wmlsc", "application/vnd.wap.wmlscriptc");
        MIME_TYPES.put("wmls", "text/vnd.wap.wmlscript");
        MIME_TYPES.put("wml", "text/vnd.wap.wml");
        MIME_TYPES.put("wrl", "model/vrml");
        MIME_TYPES.put("wtls-ca-certificate", "application/vnd.wap.wtls-ca-certificate");
        MIME_TYPES.put("xbm", "image/x-xbitmap");
        MIME_TYPES.put("xht", "application/xhtml+xml");
        MIME_TYPES.put("xhtml", "application/xhtml+xml");
        MIME_TYPES.put("xls", "application/vnd.ms-excel");
        MIME_TYPES.put("xml", "application/xml");
        MIME_TYPES.put("xpm", "image/x-xpixmap");
        MIME_TYPES.put("xpm", "image/x-xpixmap");
        MIME_TYPES.put("xsl", "application/xml");
        MIME_TYPES.put("xslt", "application/xslt+xml");
        MIME_TYPES.put("xul", "application/vnd.mozilla.xul+xml");
        MIME_TYPES.put("xwd", "image/x-xwindowdump");
        MIME_TYPES.put("xyz", "chemical/x-xyz");
        MIME_TYPES.put("z", "application/compress");
        MIME_TYPES.put("zip", "application/zip");
    }

    static public String getMimeType(URL url) throws IOException {

        // First try to get MIME type by peeking the input stream
        InputStream is = new BufferedInputStream( url.openStream());
        String mimeType = URLConnection.guessContentTypeFromStream(is);
        is.close();

        // If not found -> fallback using the file suffix
        if ( mimeType == null ) {
            mimeType = getMimeType(url.getFile());
        }

        return mimeType;
    }

    static public String getMimeType(String filename) {
        int suffixIndex = filename.lastIndexOf(".");
        if ( suffixIndex != -1 ) {
            String suffix = filename.substring(suffixIndex+1);
            return MIME_TYPES.get(suffix);
        }
        return null;
    }

}
