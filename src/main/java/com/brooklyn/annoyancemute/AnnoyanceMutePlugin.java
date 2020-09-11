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

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
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
	@Inject
	private Client client;

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
			if (Sounds.PETS.contains(soundId) && annoyanceMuteConfig.mutePetSounds())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (Sounds.TOWN_CRIER.contains(soundId) && annoyanceMuteConfig.muteTownCrierSounds())
			{
				areaSoundEffectPlayed.consume();
			}
		}
		else if (source == client.getLocalPlayer())
		{
			if (Sounds.DENSE_ESSENCE.contains(soundId) && annoyanceMuteConfig.muteDenseEssence())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (Sounds.FISHING.contains(soundId) && annoyanceMuteConfig.muteFishing())
			{
				areaSoundEffectPlayed.consume();
			}
		}
		else if (source == null)
		{
			if (Sounds.PETS.contains(soundId) && annoyanceMuteConfig.mutePetSounds())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (Sounds.SNOWBALL.contains(soundId) && annoyanceMuteConfig.muteSnowballSounds())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (Sounds.WHACK.contains(soundId) && annoyanceMuteConfig.muteRubberChickenSounds())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (Sounds.AOE_SPELL.contains(soundId) && annoyanceMuteConfig.muteAOESounds())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (Sounds.CANNON.contains(soundId) && annoyanceMuteConfig.muteCannon())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (Sounds.RANDOM_EVENTS.contains(soundId) && annoyanceMuteConfig.muteRandoms())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (Sounds.TEKTON.contains(soundId) && annoyanceMuteConfig.muteTekton())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (Sounds.CHOP_CHOP.contains(soundId) && annoyanceMuteConfig.muteChopChop())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (Sounds.NIGHTMARE.contains(soundId) && annoyanceMuteConfig.muteNightmare())
			{
				areaSoundEffectPlayed.consume();
			}
		}
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed soundEffectPlayed)
	{
		int soundId = soundEffectPlayed.getSoundId();
		if (Sounds.SNOWBALL.contains(soundId) && annoyanceMuteConfig.muteSnowballSounds())
		{
			soundEffectPlayed.consume();
		}
		else if (Sounds.ACB_REEEE.contains(soundId) && annoyanceMuteConfig.muteREEEE())
		{
			soundEffectPlayed.consume();
		}
		else if (Sounds.SIRE.contains(soundId) && annoyanceMuteConfig.muteSire())
		{
			soundEffectPlayed.consume();
		}
		else if (Sounds.OBELISK.contains(soundId) && annoyanceMuteConfig.muteObelisk())
		{
			soundEffectPlayed.consume();
		}
		else if (Sounds.RANDOM_EVENTS.contains(soundId) && annoyanceMuteConfig.muteRandoms())
		{
			soundEffectPlayed.consume();
		}
		else if (Sounds.PLANK_MAKE.contains(soundId) && annoyanceMuteConfig.mutePlankMake())
		{
			soundEffectPlayed.consume();
		}
		else if (Sounds.SCARABS.contains(soundId) && annoyanceMuteConfig.muteScarabs())
		{
			soundEffectPlayed.consume();
		}
		else if (Sounds.ALCHEMY.contains(soundId) && annoyanceMuteConfig.muteAlchemy())
		{
			soundEffectPlayed.consume();
		}
	}
}
