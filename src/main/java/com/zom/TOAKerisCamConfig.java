package com.zom;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("toa-keris-cam")
public interface TOAKerisCamConfig extends Config
{
	@ConfigItem(
		keyName = "selfie",
		name = "Include own use",
		description = "Take pictures of when you Keris",
		position = 0,
		section = roomSection
	)
	default boolean selfie()
	{
		return false;
	}

	@ConfigSection(
		name = "Rooms",
		description = "Enable Keris screen shots in specific rooms",
		position = 1,
		closedByDefault = true
	)
	String roomSection = "roomSection";

	@ConfigItem(
		keyName = "crondis",
		name = "Crondis",
		description = "Take pictures in the Crondis Room",
		position = 1,
		section = roomSection
	)
	default boolean crondis()
	{
		return true;
	}

	@ConfigItem(
		keyName = "zebak",
		name = "Zebak",
		description = "Take pictures in Zebak's Room",
		position = 2,
		section = roomSection
	)
	default boolean zebak()
	{
		return true;
	}

	@ConfigItem(
		keyName = "scabaras",
		name = "Scabaras",
		description = "Take pictures in the Scabaras Room",
		position = 3,
		section = roomSection
	)
	default boolean scabaras()
	{
		return true;
	}

	@ConfigItem(
		keyName = "kephri",
		name = "Kephri",
		description = "Take pictures in Kephri's Room",
		position = 4,
		section = roomSection
	)
	default boolean kephri()
	{
		return true;
	}

	@ConfigItem(
		keyName = "ampeken",
		name = "Ampeken",
		description = "Take pictures in the Ampeken Room",
		position = 5,
		section = roomSection
	)
	default boolean ampeken()
	{
		return true;
	}

	@ConfigItem(
		keyName = "baba",
		name = "Baba",
		description = "Take pictures in Baba's Room",
		position = 6,
		section = roomSection
	)
	default boolean baba()
	{
		return true;
	}

	@ConfigItem(
		keyName = "het",
		name = "Het",
		description = "Take pictures in the Het Room",
		position = 7,
		section = roomSection
	)
	default boolean het()
	{
		return true;
	}

	@ConfigItem(
		keyName = "akkha",
		name = "Akkha",
		description = "Take pictures in Akkha's Room",
		position = 8,
		section = roomSection
	)
	default boolean akkha()
	{
		return true;
	}

	@ConfigItem(
		keyName = "wardens",
		name = "Wardens",
		description = "Take pictures at Wardens",
		position = 9,
		section = roomSection
	)
	default boolean wardens()
	{
		return true;
	}

	@ConfigItem(
		keyName = "includeFrame",
		name = "Include Client Frame",
		description = "Configures whether or not the client frame is included in screenshots",
		position = 5
	)
	default boolean includeFrame()
	{
		return true;
	}

	@ConfigItem(
		keyName = "displayDate",
		name = "Display Date",
		description = "Configures whether or not the report button shows the date the screenshot was taken",
		position = 6
	)
	default boolean displayDate()
	{
		return true;
	}

	@ConfigItem(
		keyName = "notifyWhenTaken",
		name = "Notify When Taken",
		description = "Configures whether or not you are notified when a screenshot has been taken",
		position = 7
	)
	default boolean notifyWhenTaken()
	{
		return true;
	}

	@ConfigItem(
		keyName = "copyScreenshot",
		name = "Copy",
		description = "Configures whether or not screenshots are placed into your clipboard",
		position = 8
	)
	default boolean copyToClipboard()
	{
		return false;
	}
}
