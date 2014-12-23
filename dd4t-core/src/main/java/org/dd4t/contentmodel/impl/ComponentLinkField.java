package org.dd4t.contentmodel.impl;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldType;

import java.util.LinkedList;
import java.util.List;

public class ComponentLinkField extends BaseField implements Field {

    public ComponentLinkField() {
        setFieldType(FieldType.COMPONENTLINK);
    }

    @Override
    public List<Object> getValues() {
        List<Component> compValues = getLinkedComponentValues();
        List<Object> l = new LinkedList<Object>();

        for (Component c : compValues) {
            l.add(c);
        }

        return l;
    }
}