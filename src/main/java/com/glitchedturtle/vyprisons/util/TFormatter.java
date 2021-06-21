package com.glitchedturtle.vyprisons.util;

import java.text.DecimalFormat;

public class TFormatter {

    private static DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#.#");
    private static DecimalFormat LARGE_FORMAT = new DecimalFormat("###,###,###,###");

    public static String formatPercentage(double percentage) {
        return DECIMAL_FORMATTER.format(percentage * 100) + "%";
    }

    public static String formatLargeNumber(long num) {
        return LARGE_FORMAT.format(num);
    }

    public static String formatTime(long time) {

        if(time < 60)
            return DECIMAL_FORMATTER.format(time) + " seconds";

        time /= 60;
        if(time < 60)
            return DECIMAL_FORMATTER.format(time) + " minutes";

        time /= 60;
        if(time < 24)
            return DECIMAL_FORMATTER.format(time) + " hours";

        time /= 24;
        return DECIMAL_FORMATTER.format(time) + " days";

    }

}
