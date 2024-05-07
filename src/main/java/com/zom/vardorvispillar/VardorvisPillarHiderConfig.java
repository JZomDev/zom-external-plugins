/*
 * Copyright (c) 2024, zom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.zom.vardorvispillar;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(VardorvisPillarHiderConfig.VARDORVIS_PILLARS_CONFIG_GROUP)
public interface VardorvisPillarHiderConfig extends Config
{
	String VARDORVIS_PILLARS_CONFIG_GROUP = "vardorvispillars";

	@ConfigItem(
		position = 1,
		keyName = "drawPillarLocation",
		name = "Draw pillars",
		description = "Display the pillar locations where you can't click"
	)
	default boolean drawPillarLocation()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "boarderColor",
		name = "Boarder color",
		description = "Fill color where the pillars used to be"
	)
	default Color boarderColor()
	{
		return new Color(0x64FFFF00, true);
	}

	@ConfigItem(
		position = 3,
		keyName = "borderWidth",
		name = "Border Width",
		description = "Width of the marked tile border"
	)
	default double borderWidth()
	{
		return 2;
	}

	@ConfigItem(
		position = 4,
		keyName = "fillOpacity",
		name = "Fill Opacity",
		description = "Opacity of the tile fill color"
	)
	@Range(
		max = 255
	)
	default int fillOpacity()
	{
		return 50;
	}
}
