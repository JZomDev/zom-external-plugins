package com.zom.molemanmode;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(MolemanModeConfig.MOLEMAN_MODE_CONFIGGROUP)
public interface MolemanModeConfig extends Config
{
	String MOLEMAN_MODE_CONFIGGROUP = "molemanmode";
	
	String spentTicks = "spentTicks";

	@ConfigItem(
		keyName = "showOverlay",
		name = "Show Overlay",
		description = "Enable Overlay",
		position = 1
	)
	default boolean showOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "manualToggle",
		name = "Manual Toggle",
		description = "Forces the time tick into the opposite state that it’s currently in",
		position = 2
	)
	default boolean manualToggle()
	{
		return false;
	}

	@ConfigItem(
		keyName = "xpThreshold",
		name = "XP Threshold",
		description = "XP to gain before more time is added",
		position = 3
	)
	default int xpThreshold()
	{
		return 1000;
	}

	@ConfigItem(
		keyName = "timeEarnedPerThreshold",
		name = "XP Earned per Threshold",
		description = "This is the amount of time (in ticks) that the player earns to spend Above Ground",
		position = 4
	)
	default int timeEarnedPerThreshold()
	{
		return 100;
	}

	@ConfigItem(
		keyName = "timeWarning",
		name = "Time Warning Threshold",
		description = "When the player’s available time is below this threshold (in ticks), the available time value text’s colour is changed to red.",
		position = 5
	)
	default int timeWarningThreshold()
	{
		return 180;
	}

	@ConfigItem(
		keyName = "bonusTime",
		name = "Bonus time given",
		description = "This is the amount of time (in ticks) that the player can manually give themselves when starting the plugin for the first time.",
		position = 6
	)
	@Range(min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
	default int bonusTime()
	{
		return 0;
	}

	@ConfigSection(
		name = "Overwrites",
		description = "Overwrites Above/Below ground rules",
		position = 20
	)
	String overwriteSection = "overwrites";

	@ConfigItem(
		keyName = "whiteList",
		name = "Below ground regions",
		description = "Region IDs to not count down in",
		section = overwriteSection,
		position = 1
	)
	default String manualSafeAreas()
	{
		return "";
	}

	@ConfigItem(
		keyName = "blackList",
		name = "Above ground regions",
		description = "Region IDs to count down in",
		section = overwriteSection,
		position = 2

	)
	default String manualCountDownAreas()
	{
		return "";
	}

	@ConfigSection(
		name = "Overlays",
		description = "",
		position = 40
	)
	String overlaySection = "overlays";

	@ConfigItem(
		keyName = "formatTime",
		name = "Format Time",
		description = "Display HH:MM:SS",
		section = overlaySection,
		position = 1
	)
	default boolean formatTimer()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showRegion",
		name = "Show Region in overlay",
		description = "Region IDs display",
		section = overlaySection,
		position = 2

	)
	default boolean showRegion()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAboveGround",
		name = "Show above ground status",
		description = "Show above ground status yes/no",
		section = overlaySection,
		position = 3

	)
	default boolean showAboveGround()
	{
		return true;
	}
}
