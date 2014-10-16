package org.dd4t.contentmodel;

/**
 * @author Mihai Cadariu
 * @since 10.07.2014
 */
public enum FieldType {

    Text(0), MultiLineText(1), Xhtml(2), Keyword(3), Embedded(4), MultimediaLink(5),
    ComponentLink(6), ExternalLink(7), Number(8), Date(9), Unknown(-1);

    private final int value;

    FieldType(int value) {
        this.value = value;
    }

    public static FieldType findByValue(int value) {
        for (FieldType fieldType : values()) {
            if (fieldType.getValue() == value) {
                return fieldType;
            }
        }

        return Unknown;
    }

    public static FieldType findByName(String name) {
        try {
            return FieldType.valueOf(name);
        } catch (IllegalArgumentException iae) {
            try {
                int value = Integer.parseInt(name);
                return findByValue(value);
            } catch (NumberFormatException nfe) {
                return Unknown;
            }
        }
    }

    public int getValue() {
        return value;
    }
}