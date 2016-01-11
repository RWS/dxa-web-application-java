package com.sdl.webapp.common.api;

public enum ScreenWidth {
    EXTRA_SMALL, SMALL, MEDIUM, LARGE;

    public int getColsIfExtraSmall(int colsIfYes, int colsIfNot) {
        return getCols(EXTRA_SMALL, colsIfYes, colsIfNot);
    }

    public int getColsIfSmall(int colsIfYes, int colsIfNot) {
        return getCols(SMALL, colsIfYes, colsIfNot);
    }

    public int getColsIfMedium(int colsIfYes, int colsIfNot) {
        return getCols(MEDIUM, colsIfYes, colsIfNot);
    }

    public int getColsIfLarge(int colsIfYes, int colsIfNot) {
        return getCols(LARGE, colsIfYes, colsIfNot);
    }

    private int getCols(ScreenWidth expected, int colsIfYes, int colsIfNot) {
        return this == expected ? colsIfYes : colsIfNot;
    }
}
