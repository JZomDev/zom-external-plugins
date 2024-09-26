package com.zom.bananapicker;

import static com.zom.bananapicker.BananaPickerConfig.CONFIG_GROUP;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(CONFIG_GROUP)
public interface BananaPickerConfig extends Config
{
	String CONFIG_GROUP = "bananapicker";

	@ConfigItem(
		keyName = "bananaCount",
		name = "Banana Count",
		description = "Update this if it some how desyncs"
	)
	default int bananaCount()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "bananaCount",
		name = "",
		description = ""
	)
	void setBananaCount(int value);
}
