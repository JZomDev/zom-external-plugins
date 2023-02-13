package com.zom;

import java.util.Collections;
import java.util.Set;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;


@ConfigGroup(AFKGuardiansPlugin.CONFIG_GROUP)
public interface AFKGuardiansConfig extends Config
{
	@ConfigItem(
		keyName = "timeWasting",
		name = "Time to wait",
		description = "Amount of time before the round starts to notify",
		position = 1
	)
	default int timeWasting()
	{
		return 105;
	}

	@ConfigItem(
		keyName = "alertOnTier",
		name = "Notify on selection	",
		description = "Notify on selection",
		position = 2
	)
	default Set<AFKAlertTier> alertOnRed()
	{
		return Collections.emptySet();
	}

	@ConfigItem(
		keyName = "enableInfoBox",
		name = "Enable info Box",
		description = "Show an infobox of being within xp drop range",
		position = 3
	)
	default boolean enableInfoBox()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hideInfoBox",
		name = "Hide info box when above 150",
		description = "Hide the info box when over 150 points",
		position = 4
	)
	default boolean hideInfoBox()
	{
		return true;
	}

	@ConfigItem(
		keyName = "notifyMining",
		name = "Notify on round start",
		description = "Notifies when the round starts",
		position = 5
	)
	default boolean notifyMining()
	{
		return true;
	}

	@ConfigItem(
		keyName = "additionalNotify",
		name = "Notify when below 150, again.",
		description = "Notifies when specific color is up & below 150",
		position = 6
	)
	default boolean additionalNotify()
	{
		return true;
	}

	@Range(min = -1, max = 99)
	@ConfigItem(
		keyName = "additionalPercent",
		name = "Notify when below 150 at a certain %",
		description = "Notifies when at a specific % when color is up & below 150",
		position = 7
	)
	default int additionalPercent()
	{
		return 40;
	}
}
