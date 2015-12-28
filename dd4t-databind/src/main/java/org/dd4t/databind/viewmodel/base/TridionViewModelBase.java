/*
 * Copyright (c) 2015 Radagio
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

package org.dd4t.databind.viewmodel.base;

import org.dd4t.core.databind.TridionViewModel;
import org.dd4t.core.util.TCMURI;
import org.dd4t.databind.annotations.ViewModel;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

/**
 * Extend your Tridion models from here for
 * XPM support and generic Tridion Item data.
 *
 * @author R. Kempees
 */
public abstract class TridionViewModelBase extends ViewModelBase implements TridionViewModel {

    private TCMURI itemTcmUri;
    private TCMURI templateUri;
    private DateTime lastModifiedDate;
    private DateTime lastPublishDate;
    private boolean setGenericComponentOnComponentPresentation;

    private transient Map<String, XPMInfo> fieldMap = new HashMap<String, XPMInfo>();

    @Override
    protected void setGenericParameters () {
        super.setGenericParameters();
        final ViewModel viewModelAnnotation = this.getClass().getAnnotation(ViewModel.class);
        this.setGenericComponentOnComponentPresentation = viewModelAnnotation.setComponentObject();
    }

    @Override
    public TCMURI getTcmUri () {
        return this.itemTcmUri;
    }

    @Override
    public void setTcmUri (final TCMURI tcmUri) {
        this.itemTcmUri = tcmUri;
    }

    @Override
    public TCMURI getTemplateUri () {
        return this.templateUri;
    }

    @Override
    public void setTemplateUri (final TCMURI tcmUri) {
        this.templateUri = tcmUri;
    }

    @Override
    public DateTime getLastModified () {
        return this.lastModifiedDate;
    }

    @Override
    public void setLastModified (final DateTime lastModified) {
        this.lastModifiedDate = lastModified;
    }

    @Override
    public DateTime getLastPublishDate () {
        return this.lastPublishDate;
    }

    @Override
    public void setLastPublishDate (final DateTime lastPublishDate) {
        this.lastPublishDate = lastPublishDate;
    }

    @Override
    public boolean setGenericComponentOnComponentPresentation () {
        return setGenericComponentOnComponentPresentation;
    }

    @Override
    public boolean isMultiValued (final String fieldName) {
        return getFieldMap().get(fieldName).isMultiValued();
    }

    @Override
    public String getXPath (final String fieldName) {
        XPMInfo xpmInfo = fieldMap.get(fieldName);
        if (xpmInfo != null) {
            return xpmInfo.getXpath();
        } else {
            throw new IllegalArgumentException("Unknown field='" + fieldName + "' in " + getFieldMap().keySet());
        }
    }

    @Override
    public void addXpmEntry (final String fieldName, final String xpath, final boolean multiValued) {
        fieldMap.put(fieldName, new XPMInfo(xpath, multiValued));
    }

    public Map<String, XPMInfo> getFieldMap () {
        return fieldMap;
    }

    public static class XPMInfo {
        private final String xpath;
        private final boolean multiValued;

        XPMInfo (final String xpath, final boolean multiValued) {
            this.xpath = xpath;
            this.multiValued = multiValued;
        }

        public String getXpath () {
            return xpath;
        }

        public boolean isMultiValued () {
            return multiValued;
        }
    }
}
