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
import net.runelite.api.Player;
import net.runelite.api.Varbits;
import net.runelite.api.events.AreaSoundEffectPlayed;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import java.util.Collections;
import java.util.List;

@PluginDescriptor(
	name = "Annoyance Mute",
	description = "Selectively mute annoying game sounds",
	tags = {"sound", "volume", "mute", "hub", "brooklyn", "pet", "stomp"}
)
public class AnnoyanceMutePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private AnnoyanceMuteConfig config;

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
		if (source != client.getLocalPlayer() && source instanceof Player)
		{
			if (config.muteOthersAreaSounds())
			{
				areaSoundEffectPlayed.consume();
			}
			else if (shouldMute(soundId))
			{
				areaSoundEffectPlayed.consume();
			}
		}
		else if (source == null)
		{
			if (soundId == SoundEffectID.PET_WALKING_THUMP && client.getVar(Varbits.IN_RAID) == 1)
			{
				return;
			}
			if (soundId == SoundEffectID.SKELETON_THRALL_ATTACK && config.muteThralls())
			{
				areaSoundEffectPlayed.consume();
			}
			if (shouldMute(soundId))
			{
				areaSoundEffectPlayed.consume();
			}
		}
		else if (shouldMute(soundId))
		{
			areaSoundEffectPlayed.consume();
		}
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed soundEffectPlayed)
	{
		int soundId = soundEffectPlayed.getSoundId();
		if (shouldMute(soundId))
		{
			soundEffectPlayed.consume();
		}
	}

	private boolean shouldMute(int soundId)
	{
		if (getSelectedSounds().contains(Integer.toString(soundId)))
		{
			return true;
		}

		switch (soundId)
		{
			// ------- Combat -------

			case SoundEffectID.ACB_REEEE:
				return config.muteREEEE();

			case SoundEffectID.CANNON_SPIN:
				return config.muteCannon();

			case SoundEffectID.ICE_BARRAGE_CAST:
			case SoundEffectID.ICE_BLITZ_CAST:
			case SoundEffectID.ICE_BURST_CAST:
			case SoundEffectID.ICE_SPELL_LAND:
			case SoundEffectID.ICE_RUSH_CAST:
				return config.muteIceSpells();

			case SoundEffectID.WATER_STRIKE_CAST:
			case SoundEffectID.WATER_STRIKE_LAND:
			case SoundEffectID.ZOMBIE_THRALL_ATTACK:
				return config.muteThralls();

			// ------- NPCs -------

			case SoundEffectID.CAVE_HORROR:
				return config.muteCaveHorrors();

			case SoundEffectID.MOO_MOO:
				return config.muteCows();

			case SoundEffectID.GREATER_DEMON_ATTACK:
			case SoundEffectID.GREATER_DEMON_DEATH:
			case SoundEffectID.GREATER_DEMON_PARRY:
			case SoundEffectID.DEMON_ATTACK:
			case SoundEffectID.DEMON_DEATH:
			case SoundEffectID.DEMON_PARRY:
				return config.muteDemons();

			case SoundEffectID.DUST_DEVIL_ATTACK:
			case SoundEffectID.DUST_DEVIL_DEATH:
			case SoundEffectID.DUST_DEVIL_PARRY:
				return config.muteDustDevils();

			case SoundEffectID.FOSSIL_ISLAND_WYVERN_69:
			case SoundEffectID.FOSSIL_ISLAND_WYVERN_71:
			case SoundEffectID.FOSSIL_ISLAND_WYVERN_73:
				return config.muteWyverns();

			case SoundEffectID.JELLY_ATTACK:
			case SoundEffectID.JELLY_DEATH:
			case SoundEffectID.JELLY_PARRY:
				return config.muteJellies();

			case SoundEffectID.NAIL_BEAST_ATTACK:
			case SoundEffectID.NAIL_BEAST_DEATH:
			case SoundEffectID.NAIL_BEAST_PARRY:
				return config.muteNailBeasts();

			case SoundEffectID.NECHRYAEL_ATTACK:
			case SoundEffectID.NECHRYAE_DEATH:
			case SoundEffectID.NECHRYAEL_PARRY:
				return config.muteNechryael();

			case SoundEffectID.NIGHTMARE_SOUND:
				return config.muteNightmare();

			case SoundEffectID.SNAKELING_METAMORPHOSIS:
			case SoundEffectID.CLOCKWORK_CAT_CLICK_CLICK:
			case SoundEffectID.PET_KREEARRA_WING_FLAP:
			case SoundEffectID.ELECTRIC_HYDRA_IN:
			case SoundEffectID.ELECTRIC_HYDRA_OUT:
			case SoundEffectID.IKKLE_HYDRA_RIGHT_FOOT_LETS_STOMP:
			case SoundEffectID.IKKLE_HYDRA_LEFT_FOOT_LETS_STOMP:
			case SoundEffectID.PET_WALKING_THUMP:
			case SoundEffectID.VETION_JR_RIGHT_FOOT_LETS_STOMP:
			case SoundEffectID.VETION_JR_LEFT_FOOT_LETS_STOMP:
				return config.mutePetSounds();

			case SoundEffectID.CAT_HISS:
				// Applicable to both pet sounds and random event sounds
				return config.mutePetSounds() || config.muteRandoms();

			case SoundEffectID.NPC_TELEPORT_WOOSH:
			case SoundEffectID.DRUNKEN_DWARF:
				return config.muteRandoms();

			case SoundEffectID.SCARAB_ATTACK_SOUND:
			case SoundEffectID.SCARAB_SPAWN_SOUND:
				return config.muteScarabs();

			case SoundEffectID.SIRE_SPAWNS:
			case SoundEffectID.SIRE_SPAWNS_DEATH:
				return config.muteSire();

			case SoundEffectID.SPECTRE_ATTACK_SHOOT:
			case SoundEffectID.SPECTRE_ATTACK_HIT:
			case SoundEffectID.SPECTRE_DEATH:
			case SoundEffectID.SPECTRE_PARRY:
				return config.muteSpectres();

			case SoundEffectID.METEOR:
				return config.muteTekton();

			case SoundEffectID.TOWN_CRIER_BELL_DING:
			case SoundEffectID.TOWN_CRIER_BELL_DONG:
			case SoundEffectID.TOWN_CRIER_SHOUT_SQUEAK:
				return config.muteTownCrierSounds();

			// ------- Skilling -------

			case SoundEffectID.HIGH_ALCHEMY:
			case SoundEffectID.LOW_ALCHEMY:
				return config.muteAlchemy();

			case SoundEffectID.CHOP_CHOP:
				return config.muteChopChop();

			case SoundEffectID.CHISEL:
				return config.muteDenseEssence();

			case SoundEffectID.FIREMAKING_LOG_BURN:
			case SoundEffectID.FIREMAKING_LOG_LIGHT:
				return config.muteFiremaking();

			case SoundEffectID.FISHING_SOUND:
				return config.muteFishing();

			case SoundEffectID.FLETCHING_CUT:
			case SoundEffectID.AMETHYST_FLETCHING:
				return config.muteFletching();

			case SoundEffectID.HUMIDIFY_SOUND:
				return config.muteAOESounds();

			case SoundEffectID.PICKPOCKET_PLOP:
				return config.mutePickpocket();

			case SoundEffectID.PICKPOCKET_STUN:
				return config.mutePickpocketStun();

			case SoundEffectID.MINING_PICK_SWING_1:
			case SoundEffectID.MINING_PICK_SWING_2:
				return config.muteMining();

			case SoundEffectID.PLANK_MAKE:
				return config.mutePlankMake();

			case SoundEffectID.STRING_JEWELLERY:
				return config.muteStringJewellery();

			case SoundEffectID.WOODCUTTING_CHOP:
				return config.muteWoodcutting();

			case SoundEffectID.CHARGE_EARTH_ORB:
			case SoundEffectID.CHARGE_AIR_ORB:
			case SoundEffectID.CHARGE_FIRE_ORB:
			case SoundEffectID.CHARGE_WATER_ORB:
				return config.muteChargeOrb();

			// ------- Prayers -------

			case SoundEffectID.THICK_SKIN: return config.muteThickSkin();
			case SoundEffectID.BURST_OF_STRENGTH: return config.muteBurstofStrength();
			case SoundEffectID.CLARITY_OF_THOUGHT: return config.muteClarityOfThought();
			case SoundEffectID.ROCK_SKIN: return config.muteRockSkin();
			case SoundEffectID.SUPERHUMAN_STRENGTH: return config.muteSuperhumanStrength();
			case SoundEffectID.IMPROVED_REFLEXES: return config.muteImprovedReflexes();
			case SoundEffectID.RAPID_HEAL: return config.muteRapidHeal();
			case SoundEffectID.PROTECT_ITEM: return config.muteProtectItem();
			case SoundEffectID.HAWK_EYE: return config.muteHawkEye();
			case SoundEffectID.MYSTIC_LORE: return config.muteMysticLore();
			case SoundEffectID.STEEL_SKIN: return config.muteSteelSkin();
			case SoundEffectID.ULTIMATE_STRENGTH: return config.muteUltimateStrength();
			case SoundEffectID.INCREDIBLE_REFLEXES: return config.muteIncredibleReflexes();
			case SoundEffectID.PROTECT_FROM_MAGIC: return config.muteProtectFromMagic();
			case SoundEffectID.PROTECT_FROM_RANGE: return config.muteProtectFromRange();
			case SoundEffectID.PROTECT_FROM_MELEE: return config.muteProtectFromMelee();
			case SoundEffectID.EAGLE_EYE: return config.muteEagleEye();
			case SoundEffectID.MYSTIC_MIGHT: return config.muteMysticMight();
			case SoundEffectID.RETRIBUTION: return config.muteRetribution();
			case SoundEffectID.REDEMPTION: return config.muteRedemption();
			case SoundEffectID.SMITE: return config.muteSmite();
			case SoundEffectID.PRESERVE: return config.mutePreserve();
			case SoundEffectID.CHIVALRY: return config.muteChivalry();
			case SoundEffectID.PIETY: return config.mutePiety();
			case SoundEffectID.RIGOUR: return config.muteRigour();
			case SoundEffectID.AUGURY: return config.muteAugury();
			case SoundEffectID.DEACTIVATE_PRAYER: return config.muteDeactivatePrayer();

			// ------- Miscellaneous -------

			case SoundEffectID.FISHING_EXPLOSIVE:
				return config.muteFishingExplosive();

			case SoundEffectID.HEAL_OTHER_2:
			case SoundEffectID.HEAL_OTHER_3:
			case SoundEffectID.HEAL_OTHER_4:
			case SoundEffectID.HEAL_OTHER_5:
				return config.muteHealOther();

			case SoundEffectID.ITEM_DROP:
				return config.muteItemDrop();

			case SoundEffectID.LEVEL_UP_1:
			case SoundEffectID.LEVEL_UP_2:
				return config.muteLevelUp();

			case SoundEffectID.NPC_CONTACT:
				return config.muteNPCContact();

			case SoundEffectID.SNOWBALL_HIT:
			case SoundEffectID.SNOWBALL_THROW:
				return config.muteSnowballSounds();

			case SoundEffectID.TELEOTHER:
				return config.muteTeleother();

			case SoundEffectID.TELEPORT_VWOOP:
				return config.muteTeleport() || (config.muteTeleportOthers() && !localTeleport());

			case SoundEffectID.WHACK:
				return config.muteRubberChickenSounds();

			case SoundEffectID.WILDY_OBELISK:
				return config.muteObelisk();

			default:
				return false;
		}
	}

	private boolean localTeleport()
	{
		Player localPlayer = client.getLocalPlayer();
		int animID = localPlayer.getAnimation();

		switch (animID)
		{
			case 714:	// Normal
			case 1816:	// Lunar
			case 3864:	// Scroll
			case 3865:	// Xeric
			case 3867:	// Wilderness
			case 3869:	// Cabbage
			case 3872:	// Ardougne
			case 3874:	// Burgh
				return true;

			default:
				return false;
		}
	}

	List<String> getSelectedSounds()
	{
		final String configSounds = config.soundsToMute().toLowerCase();

		if (configSounds.isEmpty())
		{
			return Collections.emptyList();
		}

		return Text.fromCSV(configSounds);
	}
}
