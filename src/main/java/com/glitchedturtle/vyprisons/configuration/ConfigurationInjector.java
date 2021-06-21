package com.glitchedturtle.vyprisons.configuration;

import com.glitchedturtle.common.util.SafeLocation;
import com.glitchedturtle.common.util.SafeWorld;
import com.glitchedturtle.common.util.StringParser;
import com.glitchedturtle.vyprisons.PluginStartException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigurationInjector {

    public void initialize(FileConfiguration conf) throws PluginStartException {

        Class clazz = Conf.class;
        for(Field f : clazz.getFields()) {

            if(!f.isAnnotationPresent(ConfKey.class))
                continue;
            ConfKey annot = f.getAnnotationsByType(ConfKey.class)[0];
            Object value = conf.get(annot.value());

            if(value == null) {

                if(annot.required()) {
                    throw new PluginStartException("Configuration - Missing Property",
                                "The configuration (config.yml) is missing the required property '" + annot.value() + "'"
                            );
                }

                continue;

            }

            if(annot.translateColourCodes()) {

                if (f.getType() == String.class) {

                    if(!(value instanceof String)) {

                        throw new PluginStartException("Configuration - Malformed Property",
                                "The key '" + annot.value() + "' has an invalid value (Needs to be String)"
                        );

                    }

                    value = ChatColor.translateAlternateColorCodes('&', (String) value);

                }
                if (f.getGenericType().getTypeName().equals("java.util.List<java.lang.String>")) {

                    if(!(value instanceof List)) {

                        throw new PluginStartException("Configuration - Malformed Property",
                                    "The key '" + annot.value() + "' has an invalid value (Needs to be List)"
                                );

                    }

                    Function<String, String> mapper = StringParser.chatColorMapper('&');
                    value = ((List<String>) value).stream().map(mapper).collect(Collectors.toList());

                }

            }

            if(f.getType() == Map.class) {

                if(!(value instanceof MemorySection)) {

                    throw new PluginStartException("Configuration - Malformed Property",
                            "The key '" + annot.value() + "' has an invalid value (Needs to be Map)"
                    );

                }

                Map<Object, Object> converted = new HashMap<>();
                MemorySection section = (MemorySection) value;

                for(String k2 : section.getKeys(true))
                    converted.put(k2, section.get(k2));

                value = converted;

            }
            if(f.getType() == SafeLocation.class) {

                if(!(value instanceof String)) {

                    throw new PluginStartException("Configuration - Malformed Property",
                            "The key '" + annot.value() + "' has an invalid value (Needs to be String)"
                    );

                }

                value = StringParser.parsePosition((String) value);
            }

            if(f.getType() == SafeWorld.class) {

                if(!(value instanceof String) || Bukkit.getWorld(((String) value)) == null) {

                    throw new PluginStartException("Configuration - Malformed Property",
                            "The key '" + annot.value() + "' has an invalid value (Needs to be the name of a World)"
                    );

                }

                value = new SafeWorld((String) value);

            }

            if(f.getType().isEnum()) {

                if(!(value instanceof String)) {

                    throw new PluginStartException("Configuration - Malformed Property",
                            "The key '" + annot.value() + "' has an invalid value (Needs to be String)"
                    );

                }

                value = Enum.valueOf((Class) f.getType(), (String) value);

            }

            try {
              f.set(null, value);
            } catch(IllegalArgumentException ex) {
                throw new PluginStartException(ex,
                        "Configuration - Malformed Property",
                        "Can not inject value in configuration '" + annot.value() + "' ("
                                + value.getClass().toString() + " => " + f.getType().toString() + ")"
                );
            } catch(IllegalAccessException ex) {
                ex.printStackTrace();
            }

        }

    }

}
