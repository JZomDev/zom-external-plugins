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
import java.util.Set;

public final class Sounds
{
	protected static final Set<Integer> PETS = ImmutableSet.of(
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

	protected static final Set<Integer> WHACK = ImmutableSet.of(
		SoundEffectID.HUMIDIFY_SOUND,
		SoundEffectID.WHACK
	);

	protected static final Set<Integer> SNOWBALL = ImmutableSet.of(
		SoundEffectID.SNOWBALL_HIT,
		SoundEffectID.SNOWBALL_THROW
	);

	protected static final Set<Integer> TOWN_CRIER = ImmutableSet.of(
		SoundEffectID.TOWN_CRIER_BELL_DING,
		SoundEffectID.TOWN_CRIER_BELL_DONG,
		SoundEffectID.TOWN_CRIER_SHOUT_SQUEAK
	);

	protected static final Set<Integer> AOE_SPELL = ImmutableSet.of(
		SoundEffectID.HUMIDIFY_SOUND
	);

	protected static final Set<Integer> CANNON = ImmutableSet.of(
		SoundEffectID.CANNON_SPIN
	);

	protected static final Set<Integer> ACB_REEEE = ImmutableSet.of(
		SoundEffectID.ACB_REEEE
	);

	protected static final Set<Integer> SIRE = ImmutableSet.of(
		SoundEffectID.SIRE_SPAWNS,
		SoundEffectID.SIRE_SPAWNS_DEATH
	);

	protected static final Set<Integer> OBELISK = ImmutableSet.of(
		SoundEffectID.WILDY_OBELISK
	);

	protected static final Set<Integer> RANDOM_EVENTS = ImmutableSet.of(
		SoundEffectID.NPC_TELEPORT_WOOSH,
		SoundEffectID.DRUNKEN_DWARF,
		SoundEffectID.EVIL_BOB
	);

	protected static final Set<Integer> TEKTON = ImmutableSet.of(
		SoundEffectID.METEOR
	);

	protected static final Set<Integer> DENSE_ESSENCE = ImmutableSet.of(
		SoundEffectID.CHISEL
	);

	protected static final Set<Integer> CHOP_CHOP = ImmutableSet.of(
		SoundEffectID.CHOP_CHOP
	);

	protected static final Set<Integer> PLANK_MAKE = ImmutableSet.of(
		SoundEffectID.PLANK_MAKE
	);

	protected static final Set<Integer> NIGHTMARE = ImmutableSet.of(
		SoundEffectID.NIGHTMARE_SOUND
	);

	protected static final Set<Integer> SCARABS = ImmutableSet.of(
		SoundEffectID.SCARAB_ATTACK_SOUND,
		SoundEffectID.SCARAB_SPAWN_SOUND
	);

	protected static final Set<Integer> FISHING = ImmutableSet.of(
		SoundEffectID.FISHING_SOUND
	);

	protected static final Set<Integer> ALCHEMY = ImmutableSet.of(
		SoundEffectID.LOW_ALCHEMY,
		SoundEffectID.HIGH_ALCHEMY
	);
}
