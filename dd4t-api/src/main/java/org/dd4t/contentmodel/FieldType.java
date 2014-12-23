package org.dd4t.contentmodel;

public enum FieldType {

    TEXT(0), MULTILINETEXT(1), XHTML(2), KEYWORD(3), EMBEDDED(4), MULTIMEDIALINK(5),
    COMPONENTLINK(6), EXTERNALLINK(7), NUMBER(8), DATE(9), UNKNOWN(-1);

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

        return UNKNOWN;
    }

    public static FieldType findByName(String name) {
        try {
            return FieldType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException iae) {
            try {
                int value = Integer.parseInt(name);
                return findByValue(value);
            } catch (NumberFormatException nfe) {
                return UNKNOWN;
            }
        }
    }

    public int getValue() {
        return value;
    }
}