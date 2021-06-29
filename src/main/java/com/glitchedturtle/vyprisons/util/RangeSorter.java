package com.glitchedturtle.vyprisons.util;

import com.google.common.collect.Range;

import java.util.Comparator;

public class RangeSorter {

    public static int sortByMagnitude(Range<Double> a, Range<Double> b) {

        double aMag = a.upperEndpoint() - a.lowerEndpoint();
        double bMag = b.upperEndpoint() - b.lowerEndpoint();

        return aMag > bMag ? 1 : -1;

    }

}
