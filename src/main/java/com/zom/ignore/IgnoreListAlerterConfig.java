package com.zom.ignore;

import java.time.temporal.ChronoUnit;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(IgnoreListAlerterConfig.GROUP)
public interface IgnoreListAlerterConfig extends Config
{

	String GROUP = "zomignorelistalerter";

	@ConfigItem(
		keyName = "alertTimeOut",
		name = "Alert Timeout",
		description = "Alert Timeout to prevent spam",
		position = 1
	)
	@Range(max = 30)
	@Units(Units.MINUTES)
	default int alertTimeOut()
	{
		return 0;
	}
}
