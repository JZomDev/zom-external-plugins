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
import net.runelite.api.Player;
import net.runelite.api.Varbits;
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
			if ((Sounds.PETS.contains(soundId) || Sounds.PET_THUMP.contains(soundId)) && annoyanceMuteConfig.mutePetSounds())
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
			else if (Sounds.WOODCUTTING_CHOP.contains(soundId) && annoyanceMuteConfig.muteWoodcutting())
			{
				areaSoundEffectPlayed.consume();
			}
		}
		else if (source != client.getLocalPlayer() && source instanceof Player)
		{
			if (annoyanceMuteConfig.muteOthersAreaSounds())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (Sounds.WOODCUTTING_CHOP.contains(soundId) && annoyanceMuteConfig.muteWoodcutting())
			{
				areaSoundEffectPlayed.consume();
			}
		}
		else if (source == null)
		{
			if (Sounds.PET_THUMP.contains(soundId) && annoyanceMuteConfig.mutePetSounds())
			{
				if (client.getVar(Varbits.IN_RAID) == 1)
				{
					return;
				}
				else
				{
					areaSoundEffectPlayed.consume();
				}
			}

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
			else if (Sounds.MOO_MOO.contains(soundId) && annoyanceMuteConfig.muteCows())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (Sounds.HEAL_OTHER.contains(soundId) && annoyanceMuteConfig.muteHealOther())
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
		else if (Sounds.PICKPOCKET.contains(soundId) && annoyanceMuteConfig.mutePickpocket())
		{
			soundEffectPlayed.consume();
		}
		else if (Sounds.NPC_CONTACT.contains(soundId) && annoyanceMuteConfig.muteNPCContact())
		{
			soundEffectPlayed.consume();
		}
		else if (Sounds.STRING_JEWELERY.contains(soundId) && annoyanceMuteConfig.muteStringJewellery())
		{
			soundEffectPlayed.consume();
		}
		else if (Sounds.CAVE_HORROR.contains(soundId) && annoyanceMuteConfig.muteCaveHorrors())
		{
			soundEffectPlayed.consume();
		}
		else if (Sounds.FOSSIL_ISLAND_WYVERN.contains(soundId) && annoyanceMuteConfig.muteWyverns())
		{
			soundEffectPlayed.consume();
		}

		// Prayers
		else if ((Sounds.THICK_SKIN.contains(soundId) && annoyanceMuteConfig.muteThickSkin()) ||
			(Sounds.BURST_OF_STRENGTH.contains(soundId) && annoyanceMuteConfig.muteBurstofStrength()) ||
			(Sounds.CLARITY_OF_THOUGHT.contains(soundId) && annoyanceMuteConfig.muteClarityOfThought()) ||
			(Sounds.ROCK_SKIN.contains(soundId) && annoyanceMuteConfig.muteRockSkin()) ||
			(Sounds.SUPERHUMAN_STRENGTH.contains(soundId) && annoyanceMuteConfig.muteSuperhumanStrength()) ||
			(Sounds.IMPROVED_REFLEXES.contains(soundId) && annoyanceMuteConfig.muteImprovedReflexes()) ||
			(Sounds.RAPID_HEAL.contains(soundId) && annoyanceMuteConfig.muteRapidHeal()) ||
			(Sounds.PROTECT_ITEM.contains(soundId) && annoyanceMuteConfig.muteProtectItem()) ||
			(Sounds.HAWK_EYE.contains(soundId) && annoyanceMuteConfig.muteHawkEye()) ||
			(Sounds.MYSTIC_LORE.contains(soundId) && annoyanceMuteConfig.muteMysticLore()) ||
			(Sounds.STEEL_SKIN.contains(soundId) && annoyanceMuteConfig.muteSteelSkin()) ||
			(Sounds.ULTIMATE_STRENGTH.contains(soundId) && annoyanceMuteConfig.muteUltimateStrength()) ||
			(Sounds.INCREDIBLE_REFLEXES.contains(soundId) && annoyanceMuteConfig.muteIncredibleReflexes()) ||
			(Sounds.PROTECT_FROM_MAGIC.contains(soundId) && annoyanceMuteConfig.muteProtectFromMagic()) ||
			(Sounds.PROTECT_FROM_RANGE.contains(soundId) && annoyanceMuteConfig.muteProtectFromRange()) ||
			(Sounds.PROTECT_FROM_MELEE.contains(soundId) && annoyanceMuteConfig.muteProtectFromMelee()) ||
			(Sounds.EAGLE_EYE.contains(soundId) && annoyanceMuteConfig.muteEagleEye()) ||
			(Sounds.MYSTIC_MIGHT.contains(soundId) && annoyanceMuteConfig.muteMysticMight()) ||
			(Sounds.RETRIBUTION.contains(soundId) && annoyanceMuteConfig.muteRetribution()) ||
			(Sounds.REDEMPTION.contains(soundId) && annoyanceMuteConfig.muteRedemption()) ||
			(Sounds.SMITE.contains(soundId) && annoyanceMuteConfig.muteSmite()) ||
			(Sounds.PRESERVE.contains(soundId) && annoyanceMuteConfig.mutePreserve()) ||
			(Sounds.CHIVALRY.contains(soundId) && annoyanceMuteConfig.muteChivalry()) ||
			(Sounds.PIETY.contains(soundId) && annoyanceMuteConfig.mutePiety()) ||
			(Sounds.RIGOUR.contains(soundId) && annoyanceMuteConfig.muteRigour()) ||
			(Sounds.AUGURY.contains(soundId) && annoyanceMuteConfig.muteAugury()) ||
			(Sounds.DEACTIVATE_PRAYER.contains(soundId) && annoyanceMuteConfig.muteDeactivatePrayer()))
		{
			soundEffectPlayed.consume();
		}

	}
}
