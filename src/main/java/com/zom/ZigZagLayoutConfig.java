package com.zom;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import static com.zom.ZigZagLayoutConfig.CONFIG_GROUP;

@ConfigGroup(CONFIG_GROUP)
public interface ZigZagLayoutConfig extends Config
{
    String CONFIG_GROUP = "zomzigzaglayout";

    @ConfigItem(
            keyName = "runePouchPlacement",
            name = "Rune Pouch placement",
            description = "The spot the runes go from rune pouch"
    )
    default RunePouchPlacement runePouchPlacement()
    {
        return RunePouchPlacement.DEFAULT;
    }
}
