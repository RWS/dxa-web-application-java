package org.dd4t.contentmodel.impl;

import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldType;

public class XhtmlField extends TextField implements Field {

    public XhtmlField() {
        setFieldType(FieldType.XHTML);
    }
}