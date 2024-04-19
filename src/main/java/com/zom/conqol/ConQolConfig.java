package com.zom.conqol;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("zom-con-qol")
public interface ConQolConfig extends Config
{
	@ConfigItem(
		keyName = "input",
		name = "input",
		description = "What key is used"
	)
	default ConstuctionMenu input()
	{
		return ConstuctionMenu.ONE;
	}

	@ConfigItem(
		keyName = "output",
		name = "output",
		description = "What key it should become"
	)
	default ConstuctionMenu output()
	{
		return ConstuctionMenu.THREE;
	}
}
