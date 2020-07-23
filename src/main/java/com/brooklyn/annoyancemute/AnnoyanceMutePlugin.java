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

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.events.AreaSoundEffectPlayed;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "Annoyance Mute",
	description = "Mute annoying sounds without muting all area sounds",
	tags = {"sound", "volume", "mute", "hub", "brooklyn", "pet", "stomp"}
)
public class AnnoyanceMutePlugin extends Plugin
{
	private static final Set<Integer> PET_SOUNDS = ImmutableSet.of(
		SoundEffectID.CAT_HISS,
		SoundEffectID.SNAKELING_METAMORPHOSIS,
		SoundEffectID.CLOCKWORK_CAT_CLICK_CLICK,
		SoundEffectID.PET_WALKING_THUMP,
		SoundEffectID.PET_KREEARRA_WING_FLAP,
		SoundEffectID.ELECTRIC_HYDRA_IN,
		SoundEffectID.ELECTRIC_HYDRA_OUT,
		SoundEffectID.IKKLE_HYDRA_RIGHT_FOOT_LETS_STOMP,
		SoundEffectID.IKKLE_HYDRA_LEFT_FOOT_LETS_STOMP
	);

	private static final Set<Integer> WHACK_SOUNDS = ImmutableSet.of(
		SoundEffectID.HUMIDIFY_SOUND,
		SoundEffectID.WHACK
	);

	private static final Set<Integer> SNOWBALL_SOUNDS = ImmutableSet.of(
		SoundEffectID.SNOWBALL_HIT,
		SoundEffectID.SNOWBALL_THROW
	);

	private static final Set<Integer> TOWN_CRIER_SOUNDS = ImmutableSet.of(
		SoundEffectID.TOWN_CRIER_BELL_DING,
		SoundEffectID.TOWN_CRIER_BELL_DONG,
		SoundEffectID.TOWN_CRIER_SHOUT_SQUEAK
	);

	private static final Set<Integer> AOE_SPELL_SOUNDS = ImmutableSet.of(
		SoundEffectID.HUMIDIFY_SOUND
	);

	private static final Set<Integer> CANNON_SOUNDS = ImmutableSet.of(
		SoundEffectID.CANNON_SPIN
	);

	private static final Set<Integer> ACB_REEEE = ImmutableSet.of(
		SoundEffectID.ACB_REEEE
	);

	@Inject
	private ClientThread clientThread;

	@Inject
	private AnnoyanceMuteConfig annoyanceMuteConfig;

	@Provides
	AnnoyanceMuteConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AnnoyanceMuteConfig.class);
	}

	@Subscribe
	public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed areaSoundEffectPlayed)
	{
		Actor source = areaSoundEffectPlayed.getSource();
		int soundId = areaSoundEffectPlayed.getSoundId();
		if (source instanceof NPC)
		{
			if (PET_SOUNDS.contains(soundId) && annoyanceMuteConfig.mutePetSounds())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (TOWN_CRIER_SOUNDS.contains(soundId) && annoyanceMuteConfig.muteTownCrierSounds())
			{
				areaSoundEffectPlayed.consume();
			}
		}
		else if (source == null)
		{
			if (PET_SOUNDS.contains(soundId) && annoyanceMuteConfig.mutePetSounds())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (SNOWBALL_SOUNDS.contains(soundId) && annoyanceMuteConfig.muteSnowballSounds())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (WHACK_SOUNDS.contains(soundId) && annoyanceMuteConfig.muteRubberChickenSounds())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (AOE_SPELL_SOUNDS.contains(soundId) && annoyanceMuteConfig.muteAOESounds())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (CANNON_SOUNDS.contains(soundId) && annoyanceMuteConfig.muteCannon())
			{
				areaSoundEffectPlayed.consume();
			}
		}
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed soundEffectPlayed)
	{
		int soundId = soundEffectPlayed.getSoundId();
		if (SNOWBALL_SOUNDS.contains(soundId) && annoyanceMuteConfig.muteSnowballSounds())
		{
			soundEffectPlayed.consume();
		}
		else if (ACB_REEEE.contains(soundId) && annoyanceMuteConfig.muteREEEE())
		{
			soundEffectPlayed.consume();
		}
	}
}
