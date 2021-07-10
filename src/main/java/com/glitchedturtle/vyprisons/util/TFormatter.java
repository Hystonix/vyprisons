package com.glitchedturtle.vyprisons.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;

public class TFormatter {

    private static DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#.#");

    private static DecimalFormat LARGE_FORMAT = new DecimalFormat("###,###,###,###");

    public static String formatPercentage(double percentage) {

        if(percentage == 0)
            return "0%";
        if(percentage < 0.001)
            return "Less than 0.1%";

        return DECIMAL_FORMATTER.format(percentage * 100) + "%";
    }

    public static String formatLargeNumber(long num) {
        return LARGE_FORMAT.format(num);
    }

    public static String formatMs(long time) {
        return TFormatter.formatTime(time / 1000.0d);
    }

    public static String formatTime(double time) {

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

    public static String formatMaterial(Material mat) {

        String[] elems = mat.toString().split("_");
        return StringUtils.capitalize(
                Arrays.stream(elems)
                        .map(String::toLowerCase)
                        .collect(Collectors.joining(" "))
        );

    }

}
