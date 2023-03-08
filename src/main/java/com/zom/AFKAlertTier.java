package com.zom;

import static com.zom.AFKGuardiansPlugin.*;
import java.util.ArrayList;
import java.util.Arrays;

public enum AFKAlertTier
{
	White(AIR_GUARDIAN, MIND_GUARDIAN, BODY_GUARDIAN),
	Air(AIR_GUARDIAN),
	Mind(MIND_GUARDIAN),
	Body(BODY_GUARDIAN),
	Blue(WATER_GUARDIAN, COSMIC_GUARDIAN, CHAOS_GUARDIAN),
	Water(WATER_GUARDIAN),
	Cosmic(COSMIC_GUARDIAN),
	Chaos(CHAOS_GUARDIAN),
	Green(EARTH_GUARDIAN, LAW_GUARDIAN, NATURE_GUARDIAN),
	Earth(EARTH_GUARDIAN),
	Law(LAW_GUARDIAN),
	Nature(NATURE_GUARDIAN),
	Red(FIRE_GUARDIAN, DEATH_GUARDIAN, BLOOD_GUARDIAN),
	Fire(FIRE_GUARDIAN),
	Death(DEATH_GUARDIAN),
	Blood(BLOOD_GUARDIAN);

	private Integer[] numbers;

	AFKAlertTier(Integer... numbers)
	{
		this.numbers = numbers;
	}

	public ArrayList<Integer> getTier()
	{
		return new ArrayList<>(Arrays.asList(numbers));
	}

}
