package com.zom.dense_essence;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("zomDenseEssence")
public interface DenseEssenceConfig extends Config
{
	@ConfigItem(
		keyName = "showDenseRunestoneIndicator",
		name = "Show dense runestone indicator",
		description = "Configures whether to display an indicator when dense runestone is ready to be mined",
		position = 1
	)
	default boolean showDenseRunestoneIndicator()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showDenseRunestoneClickbox",
		name = "Show dense runestone click box",
		description = "Configures whether to display a click box when dense runestone is ready to be mined",
		position = 2
	)
	default boolean showDenseRunestoneClickbox()
	{
		return true;
	}
}
