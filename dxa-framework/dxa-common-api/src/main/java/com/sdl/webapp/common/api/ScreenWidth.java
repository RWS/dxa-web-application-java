package com.sdl.webapp.common.api;

/**
 * <p>ScreenWidth class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public enum ScreenWidth {
    EXTRA_SMALL, SMALL, MEDIUM, LARGE;

    /**
     * <p>getColsIfExtraSmall.</p>
     *
     * @param colsIfYes a int.
     * @param colsIfNot a int.
     * @return a int.
     */
    public int getColsIfExtraSmall(int colsIfYes, int colsIfNot) {
        return getCols(EXTRA_SMALL, colsIfYes, colsIfNot);
    }

    /**
     * <p>getColsIfSmall.</p>
     *
     * @param colsIfYes a int.
     * @param colsIfNot a int.
     * @return a int.
     */
    public int getColsIfSmall(int colsIfYes, int colsIfNot) {
        return getCols(SMALL, colsIfYes, colsIfNot);
    }

    /**
     * <p>getColsIfMedium.</p>
     *
     * @param colsIfYes a int.
     * @param colsIfNot a int.
     * @return a int.
     */
    public int getColsIfMedium(int colsIfYes, int colsIfNot) {
        return getCols(MEDIUM, colsIfYes, colsIfNot);
    }

    /**
     * <p>getColsIfLarge.</p>
     *
     * @param colsIfYes a int.
     * @param colsIfNot a int.
     * @return a int.
     */
    public int getColsIfLarge(int colsIfYes, int colsIfNot) {
        return getCols(LARGE, colsIfYes, colsIfNot);
    }

    private int getCols(ScreenWidth expected, int colsIfYes, int colsIfNot) {
        return this == expected ? colsIfYes : colsIfNot;
    }
}
