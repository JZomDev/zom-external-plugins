package com.slashswapper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("slashswapper")
public interface SlashSwapperConfig extends Config
{
	@ConfigItem(
		keyName = "slashGuestChat",
		name = "Swap Guest Chat",
		description = "Swap CC and GC"
	)
	default boolean slashGuestChat()
	{
		return false;
	}
}
