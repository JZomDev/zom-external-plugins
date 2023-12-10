package com.zom;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("cmblvltexthiding")
public interface CombatLevelMutingConfig extends Config
{
	@Range(min = 3, max = 126)
	@ConfigItem(
		keyName = "combatLevelTextHide",
		name = "Combat Level",
		description = "Hides the messages of players who are at or below the combat level configured"
	)
	default int combatLevelTextHide()
	{
		return 3;
	}

	@ConfigItem(
		keyName = "combatLevelHidingType",
		name = "Hiding Type",
		description = "Type of messages to hide"
	)
	default CombatLevelMutingType combatLevelHidingType()
	{
		return CombatLevelMutingType.BOTH;
	}
}
