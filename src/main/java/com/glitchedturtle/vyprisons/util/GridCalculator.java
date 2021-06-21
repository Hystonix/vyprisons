package com.glitchedturtle.vyprisons.util;

import com.glitchedturtle.vyprisons.configuration.Conf;

public class GridCalculator {

    private int _moduleSize;
    private int _sizeSq;
    private double _logCoef;

    public GridCalculator(int moduleSize) {

        _moduleSize = moduleSize;
        _sizeSq = (int) Math.pow(_moduleSize, 2);
        _logCoef = Math.log(_sizeSq);

    }

    public int[] getGridPosition(int id) {

        int order = this.calcOrder(id);
        int[] superPosition;
        if(order > 0) {

            int d = (int) Math.pow(_sizeSq, order);

            int superId = id / d;
            id %= d;

            superPosition = this.getGridPosition(superId);

            superPosition[0] *= _moduleSize;
            superPosition[1] *= _moduleSize;

        } else superPosition = new int[] { 0, 0 };

        superPosition[0] += id % _moduleSize;
        superPosition[1] += id / _moduleSize;

        return superPosition;

    }

    private int calcOrder(int id) {
        return (int) Math.floor(Math.log(id) / _logCoef);
    }

    private static GridCalculator GRID_INSTANCE = null;
    public static GridCalculator getDefaultGrid() {

        if(GRID_INSTANCE != null)
            return GRID_INSTANCE;

        GRID_INSTANCE = new GridCalculator(Conf.MINE_BLOCK_MODULE_SIZE);
        return GRID_INSTANCE;

    }

}
