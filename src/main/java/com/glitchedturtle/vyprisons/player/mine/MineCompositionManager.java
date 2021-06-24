package com.glitchedturtle.vyprisons.player.mine;

import com.glitchedturtle.vyprisons.PluginStartException;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MineCompositionManager {

    class CompositionTier {

        private RangeMap<Double, Material> _probabilityTree = TreeRangeMap.create();
        private double _prev = 0;

        public void addType(Material mat, double relativeProbability) {
            _probabilityTree.put(Range.openClosed(_prev, _prev += relativeProbability), mat);
        }

        public Material randomType() {
            return _probabilityTree.get(Math.random() * _prev);
        }

    }

    private Map<Integer, CompositionTier> _tierMap = new HashMap<>();

    public Material randomType(int tier) {

        CompositionTier compositionTier = _tierMap.get(tier);
        if(compositionTier == null)
            return Material.BARRIER;

        return compositionTier.randomType();

    }

    public void loadConfiguration(ConfigurationSection sect) throws PluginStartException {

        for(String key : sect.getKeys(false)) {

            int tier = Integer.parseInt(key);
            CompositionTier compTier = new CompositionTier();

            for(Map<?,?> comp : sect.getMapList(key)) {

                compTier.addType(
                        Material.valueOf((String) comp.get("type")),
                        (Double) comp.get("probability")
                );

            }

            _tierMap.put(tier, compTier);

        }

    }

}
