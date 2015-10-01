package com.sdl.webapp.common.api.formats;

/**
 * Created by Administrator on 9/30/2015.
 */
public abstract class BaseDataFormatter implements DataFormatter {
    public static double getScoreFromAcceptString(String type)
    {
        double res = 1.0;
        int pos = type.indexOf("q=");
        if (pos > 0)
        {
            return Double.parseDouble(type.substring(pos + 2));
        }
        return res;
    }
}
