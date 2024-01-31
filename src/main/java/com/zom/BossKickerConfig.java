package com.zom;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import static com.zom.BossKickerConfig.CONFIG_GROUP;


@ConfigGroup(CONFIG_GROUP)
public interface BossKickerConfig extends Config
{
	String CONFIG_GROUP = "BossKicker";
	@ConfigItem(
		keyName = "bossNames",
		name = "Boss names",
		description = "List of bosses in a comma delimited format"
	)
	default String bossNames()
	{
		return "Odd Figure,Vorkath";
	}

	@ConfigItem(
		keyName = "wakeUpOptions",
		name = "Wake up options",
		description = "List of wake up options in a comma delimited format"
	)
	default String bossWakeUpOptions()
	{
		return "Disturb,Poke";
	}
}
