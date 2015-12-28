package org.dd4t.mvc.utils;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * dd4t-parent
 *
 * @author M.Cadariu, R. Kempees
 */
public class XPMRenderer {

    private static final XPMRenderer INSTANCE = new XPMRenderer();
    private static final Logger LOG = LoggerFactory.getLogger(XPMRenderer.class);

    private boolean enabled;
    private String cmsURL;

    private XPMRenderer () {
    }

    public static XPMRenderer getInstance () {
        return INSTANCE;
    }

    public static boolean isXPMEnabled () {
        return INSTANCE.enabled;
    }

    public static boolean isXPMActive () {
        // for now we don't have a good server-side check if xpm editing is really active.
        return isXPMEnabled();
    }

    public void init (final boolean enabled, final String cmsURL) {
        this.enabled = enabled;
        this.cmsURL = cmsURL;
    }

    /**
     * Removes the type identifier from the id if present
     */
    private static String stripTypeIdentifier (String componentId) {
        if (componentId.endsWith("-16")) {
            return componentId.substring(0, componentId.length() - 3);
        } else {
            return componentId;
        }
    }

    /**
     * <!-- Start Component Presentation: {
     * "ComponentID": "tcm:2-635",
     * "ComponentModified": "2011-01-22T11:25:12",
     * "ComponentTemplateID": "tcm:2-599-32",
     * "IsRepositoryPublished": true
     * } -->
     */
    public String componentPresentation (String componentId, DateTime lastModified, String componentTemplateId, boolean isDynamic) {
        if (isXPMEnabled()) {
            return "<!-- Start Component Presentation: {\n" +
                    "\"ComponentID\": \"" + stripTypeIdentifier(componentId) + "\",\n" +
                    "\"ComponentModified\": \"" + getXMLDateAsString(lastModified) + "\",\n" +
                    "\"ComponentTemplateID\": \"" + componentTemplateId + "\",\n" +
                    "\"IsRepositoryPublished\": " + isDynamic + "\n" +
                    "} -->";
        }
        return "";
    }

    /**
     * <!-- Start Region: {
     * "title" : "My Region",
     * "allowedComponentTypes : [{
     * "schema" : "tcm:2-26-8",
     * "template" : "tcm:2-32-32"
     * },{
     * "schema" : "tcm:2-27-8",
     * "template" : "tcm:2-32-32"
     * }],
     * "minOccurs" : 1,
     * "maxOccurs" : 0
     * } -->
     */
    public String region (final String title, Map<String, String> allowedComponentTypes, int minOccurs, int maxOccurs) {
        if (isXPMEnabled()) {
            return "<!-- Start Region: {\n" +
                    "\"title\": \"" + title + "\",\n" +
                    "\"allowedComponentTypes\": " +
                    formatAllowedComponentTypes(allowedComponentTypes) +
                    ",\n" +
                    "\"minOccurs\": " + minOccurs + ",\n" +
                    "\"maxOccurs\": " + maxOccurs + "\n" +
                    "} -->";
        }

        return "";
    }

    /**
     * <!-- Start Component Field: {
     * "XPath": "tcm:Content/custom:Content/custom:NewField[1]",
     * "IsMultiValued": false
     * } -->
     */
    public String componentField (String xPath, boolean multiValued, int index) {
        if (isXPMEnabled()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<!-- Start Component Field: {\n");

            if (xPath.endsWith("]")) {
                stringBuilder.append(String.format("\"XPath\": \"%s\"", xPath));
            } else {
                if (index == 0) {
                    index = 1;
                }
                stringBuilder.append(String.format("\"XPath\": \"%s[%d]\"", xPath, index));
            }
            if (multiValued) {
                stringBuilder.append(",\n\"IsMultiValued\": true");
            }
            stringBuilder.append("\n} -->");

            return stringBuilder.toString();
        }

        return "";
    }

    /**
     * <!-- Page Settings: {
     * "PageID": "tcm:2-19-64",
     * "PageModified": "2011-01-22T11:25:12",
     * "PageTemplateID": "tcm:2-374-128"
     * } -->
     * <div id="xpm-bootstap" data-src="http://UIDOMAIN/WebUI/Editors/SiteEdit/Views/Bootstrap/Bootstrap.aspx?mode=js"/>
     */
    public String initPage (String pageId, DateTime revisionDate, String pageTemplateId) {
        if (isXPMEnabled()) {
            return "<!-- Page Settings: {\n" +
                    "\"PageID\": \"" + pageId + "\",\n" +
                    "\"PageModified\": \"" + getXMLDateAsString(revisionDate) + "\",\n" +
                    "\"PageTemplateID\": \"" + pageTemplateId + "\"\n" +
                    "} -->\n" +
                    getTag();
        }
        return "";
    }

    public String getTag () {
        return "<div id=\"script-xpm\" data-src=\"" + cmsURL + "WebUI/Editors/SiteEdit/Views/Bootstrap/Bootstrap.aspx?mode=js\"></div>\n";
    }

    private String formatAllowedComponentTypes (Map<String, String> allowedComponentTypes) {
        final String format = "{ \"schema\": \"%s\", \"template\": \"%s\" }";
        if (null != allowedComponentTypes) {
            String[] allowed = new String[allowedComponentTypes.keySet().size()];
            int i = 0;
            for (Map.Entry<String, String> entry : allowedComponentTypes.entrySet()) {
                allowed[i++] = String.format(format, entry.getKey(), entry.getValue());
            }
            return Arrays.toString(allowed);
        }

        return "";
    }

    public String getXMLDateAsString (DateTime date) {
        try {
            GregorianCalendar c = date.toGregorianCalendar();
            XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            xmlDate.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
            xmlDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            return xmlDate.toXMLFormat();
        } catch (DatatypeConfigurationException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        return "";
    }

    @Required
    public void setEnabled (boolean enabled) {
        this.enabled = enabled;
    }

    @Required
    public void setCmsUrl (String cmsURL) {
        this.cmsURL = cmsURL;
    }
}
