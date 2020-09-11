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
import net.runelite.client.config.ConfigSection;

@ConfigGroup("annoyancemute")
public interface AnnoyanceMuteConfig extends Config
{
	@ConfigSection(
		name = "Combat",
		description = "Combat sounds to mute",
		position = 0
	)
	String combatSection = "Combat";

	@ConfigSection(
		name = "NPCs",
		description = "NPC sounds to mute",
		position = 1
	)
	String npcSection = "NPCs";

	@ConfigSection(
		name = "Skilling",
		description = "Skilling sounds to mute",
		position = 2
	)
	String skillingSection = "Skilling";

	@ConfigSection(
		name = "Miscellaneous",
		description = "Miscellaneous sounds to mute",
		position = 3
	)
	String miscSection = "Miscellaneous";

	@ConfigItem(
		keyName = "mutePetSounds",
		name = "Pets",
		description = "Mutes the sounds of noise-making pets",
		section = npcSection
	)
	default boolean mutePetSounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteAreaOfEffectSpells",
		name = "Humidify",
		description = "Mutes humidify spell sound",
		section = skillingSection
	)
	default boolean muteAOESounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteSnowballs",
		name = "Snowballs",
		description = "Mutes the sounds of snowballs being thrown",
		section = miscSection
	)
	default boolean muteSnowballSounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteWhack",
		name = "Whack",
		description = "Mutes the Rubber chicken and Stale baguette whack sound",
		section = miscSection
	)
	default boolean muteRubberChickenSounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteCrier",
		name = "Town Crier",
		description = "Mutes the sounds of the Town Crier",
		section = npcSection
	)
	default boolean muteTownCrierSounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteCannon",
		name = "Cannon spin",
		description = "Mutes the sounds of a cannon spinning",
		section = combatSection
	)
	default boolean muteCannon()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteREEEE",
		name = "Armadyl Crossbow",
		description = "Mutes the REEEEE of the ACB spec",
		section = combatSection
	)
	default boolean muteREEEE()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteSire",
		name = "Sire Spawns",
		description = "Mutes the sounds of the Abyssal Sire's spawns",
		section = npcSection
	)
	default boolean muteSire()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteObelisk",
		name = "Wilderness Obelisk",
		description = "Mutes the sounds of the Wilderness Obelisk",
		section = miscSection
	)
	default boolean muteObelisk()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteRandoms",
		name = "Random Events",
		description = "Mutes the sounds produced by random events",
		section = npcSection
	)
	default boolean muteRandoms()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteTekton",
		name = "Tekton meteors",
		description = "Mutes the sound of Tekton's meteor attack",
		section = npcSection
	)
	default boolean muteTekton()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteDenseEssence",
		name = "Dense Essence",
		description = "Mutes the sound of chiseling Dense Essence",
		section = skillingSection
	)
	default boolean muteDenseEssence()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteChopChop",
		name = "Chop Chop!",
		description = "Mutes the sound of the Dragon axe special",
		section = skillingSection
	)
	default boolean muteChopChop()
	{
		return true;
	}

	@ConfigItem(
		keyName = "plankMake",
		name = "Plank Make",
		description = "Mutes the sound of Plank Make",
		section = skillingSection
	)
	default boolean mutePlankMake()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteNightmare",
		name = "Nightmare",
		description = "Mutes the sound of the Nightmare's parry",
		section = npcSection
	)
	default boolean muteNightmare()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteScarabs",
		name = "Scarab Swarm",
		description = "Mutes the sound of the Scarab swarm in Pyramid Plunder",
		section = npcSection
	)
	default boolean muteScarabs()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteFishing",
		name = "Fishing",
		description = "Mutes the sound of Fishing",
		section = skillingSection
	)
	default boolean muteFishing()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteAlchemy",
		name = "Alchemy",
		description = "Mutes the sounds of Low and High Alchemy",
		section = skillingSection
	)
	default boolean muteAlchemy()
	{
		return true;
	}
}
