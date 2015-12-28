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

package org.dd4t.core.util;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.Schema;
import org.dd4t.core.request.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public class TridionUtils {

    private TridionUtils () {

    }

    public static final String TCM_REGEX = "(tcm:[0-9]+-[0-9]+(-[0-9]+)?)";
    public static final String PREVIEW_SESSION_TOKEN = "preview-session-token";

    public static int extractPublicationIdFromTcmUri (String tcmUri) throws ParseException {
        TCMURI fullTcmUri = new TCMURI(tcmUri);
        return fullTcmUri.getItemId();
    }

    public static String constructFullTcmPublicationUri (int id) {
        return constructFullTcmPublicationUri(String.valueOf(id));
    }

    public static String constructFullTcmPublicationUri (String id) {
        return String.format("tcm:0-%s-1", id);
    }

    /*
    Looks up the Preview Session token from the cookie in the request
    */
    public static String getSessionPreviewToken () {
        return HttpUtils.getSessionPreviewToken(HttpUtils.getCurrentRequest());
    }

    /*
    Looks up the Preview Session token from the cookie in the request
     */
    public static String getSessionPreviewToken (RequestContext context) {
        if (context == null) {
            return null;
        }

        return HttpUtils.getSessionPreviewToken((HttpServletRequest) context.getRequest());
    }

    public static String getRootElementName (final Component component) {
        Schema schema = component.getSchema();
        return (null != component.getMultimedia()) ? schema.getTitle() : schema.getRootElement();
    }
}
