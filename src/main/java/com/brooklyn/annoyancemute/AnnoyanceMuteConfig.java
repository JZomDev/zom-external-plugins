/*
 * Copyright (c) 2020, Brooklyn <https://github.com/Broooklyn>
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
package com.brooklyn.annoyancemute;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("annoyancemute")
public interface AnnoyanceMuteConfig extends Config
{
	@ConfigItem(
		keyName = "mutePetSounds",
		name = "Pet sounds",
		description = "Mutes the sounds of noise-making pets"
	)
	default boolean mutePetSounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteAreaOfEffectSpells",
		name = "Humidify",
		description = "Mutes humidify spell sound"
	)
	default boolean muteAOESounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteSnowballs",
		name = "Snowballs",
		description = "Mutes the sounds of snowballs being thrown"
	)
	default boolean muteSnowballSounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteWhack",
		name = "Whack",
		description = "Mutes the Rubber chicken and Stale baguette whack sound"
	)
	default boolean muteRubberChickenSounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteCrier",
		name = "Town Crier",
		description = "Mutes the sounds of the Town Crier"
	)
	default boolean muteTownCrierSounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteCannon",
		name = "Cannon spin",
		description = "Mutes the sounds of a cannon spinning"
	)
	default boolean muteCannon()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteREEEE",
		name = "Armadyl Crossbow",
		description = "Mutes the REEEEE of the ACB spec"
	)
	default boolean muteREEEE()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteSire",
		name = "Sire Spawns",
		description = "Mutes the sounds of the Abyssal Sire's spawns"
	)
	default boolean muteSire()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteObelisk",
		name = "Wilderness Obelisk",
		description = "Mutes the sounds of the Wilderness Obelisk"
	)
	default boolean muteObelisk()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteRandoms",
		name = "Random Events",
		description = "Mutes the sounds produced by random events"
	)
	default boolean muteRandoms()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteTekton",
		name = "Tekton meteors",
		description = "Mutes the sound of Tekton's meteor attack"
	)
	default boolean muteTekton()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteDenseEssence",
		name = "Dense Essence",
		description = "Mutes the sound of chiseling Dense Essence"
	)
	default boolean muteDenseEssence()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteChopChop",
		name = "Chop Chop!",
		description = "Mutes the sound of the Dragon axe special"
	)
	default boolean muteChopChop()
	{
		return true;
	}
}
