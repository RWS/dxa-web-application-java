package com.sdl.webapp.common.api;

import lombok.Getter;

/**
 * @dxa.publicApi
 */
@Getter
public enum ScreenWidth {
    EXTRA_SMALL(480), SMALL(940), MEDIUM(1140), LARGE(Integer.MAX_VALUE);

    /**
     * a point after which the size of bigger than this
     */
    private int breakpoint;

    ScreenWidth(int breakpoint) {
        this.breakpoint = breakpoint;
    }

    public boolean isThisScreenWidth(int width) {
        return width < this.breakpoint;
    }

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
        return this.breakpoint <= expected.breakpoint ? colsIfYes : colsIfNot;
    }
}
