package com.glitchedturtle.vyprisons.player.mine;

import com.glitchedturtle.vyprisons.PluginStartException;
import com.glitchedturtle.vyprisons.util.RangeSorter;
import com.glitchedturtle.vyprisons.util.TFormatter;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MineTierManager {

    public class CompositionTier {

        private RangeMap<Double, Material> _probabilityTree = TreeRangeMap.create();
        private List<String> _compositionDisplay = null;

        private double _prev = 0;

        private double _purchasePrice = 1000;

        public void addType(Material mat, double relativeProbability) {
            _probabilityTree.put(Range.openClosed(_prev, _prev += relativeProbability), mat);
        }

        public Material randomType() {
            return _probabilityTree.get(Math.random() * _prev);
        }

        public double getPurchasePrice() {
            return _purchasePrice;
        }

        public List<String> getCompositionDisplay() {

            if(_compositionDisplay == null) {

                _compositionDisplay = _probabilityTree.asMapOfRanges().entrySet().stream()
                        .sorted((a, b) -> RangeSorter.sortByMagnitude(a.getKey(), b.getKey()))
                        .map(this::serializeEntry)
                        .collect(Collectors.toList());

            }

            return _compositionDisplay;

        }

        private String serializeEntry(Map.Entry<Range<Double>, Material> entry) {

            Range<Double> range = entry.getKey();
            double prob = (range.upperEndpoint() - range.lowerEndpoint())/_prev;

            return ChatColor.GRAY + "- " + ChatColor.YELLOW + TFormatter.formatPercentage(prob)
                    + ChatColor.GRAY + " chance of "
                    + ChatColor.YELLOW + TFormatter.formatMaterial(entry.getValue());
        }

    }

    private Map<Integer, CompositionTier> _tierMap = new HashMap<>();
    private int _maxTier = -1;

    public Material randomType(int tier) {

        CompositionTier compositionTier = _tierMap.get(tier);
        if(compositionTier == null)
            return Material.BARRIER;

        return compositionTier.randomType();

    }

    public int getMaximumTier() {
        return _maxTier;
    }

    public CompositionTier getTier(int tier) {
        return _tierMap.get(tier);
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
            if(tier > _maxTier)
                _maxTier = tier;

        }

    }

}
