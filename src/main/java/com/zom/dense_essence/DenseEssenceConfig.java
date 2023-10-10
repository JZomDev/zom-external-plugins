package com.zom.dense_essence;

import java.awt.Color;
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

	@ConfigItem(
		keyName = "showDenseRunestoneClickboxAvailable",
		name = "Available Color",
		description = "Configures Color of available dense runestone",
		position = 3
	)
	default Color showDenseRunestoneClickboxAvailable()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		keyName = "showDenseRunestoneClickboxUnavailable",
		name = "Unavailable Color",
		description = "Configures color of unavailable dense runestone",
		position = 4
	)
	default Color showDenseRunestoneClickboxUnavailable()
	{
		return Color.RED;
	}

	@ConfigItem(
			keyName = "highlightAltarClickbox",
			name = "Highlight Altars",
			description = "Highlights Blood/Soul Altars when there are dark essence fragments/blocks in inventory",
			position = 5
	)
	default boolean highlightAltarClickbox()
	{
		return true;
	}
}
