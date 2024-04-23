package com.zom.conqol;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("zom-con-qol")
public interface ConQolConfig extends Config
{
	@Range(min = 1, max = 9)
	@ConfigItem(
		keyName = "input",
		name = "Input Key",
		description = "What key is used"
	)
	default int input()
	{
		return 1;
	}

	@Range(min = 1, max = 9)
	@ConfigItem(
		keyName = "output",
		name = "Output Key",
		description = "What key it should become"
	)
	default int output()
	{
		return 3;
	}
}
