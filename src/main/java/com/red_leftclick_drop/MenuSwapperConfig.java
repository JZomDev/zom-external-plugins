package com.red_leftclick_drop;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("custom left click drop")
public interface MenuSwapperConfig extends Config
{
	@ConfigItem(
		keyName = "itemList",
		name = "Item list to left click drop",
		description = "Coma delimited list of items you want to left click drop.<br>Only supports full matching names and non equipable items."
	)
	default String itemList()
	{
		return "";
	}
}
