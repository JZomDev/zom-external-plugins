package com.zom;

import static com.zom.AFKGuardiansPlugin.*;
import java.util.ArrayList;
import java.util.Arrays;

public enum AFKAlertTier
{
	White(AIR, MIND, BODY),
	Air(AIR),
	Mind(MIND),
	Body(BODY),
	Blue(WATER, COSMIC, CHAOS),
	Water(WATER),
	Cosmic(COSMIC),
	Chaos(CHAOS),
	Green(EARTH, LAW, NATURE),
	Earth(EARTH),
	Law(LAW),
	Nature(NATURE),
	Red(FIRE, DEATH, BLOOD),
	Fire(FIRE),
	Death(DEATH),
	Blood(BLOOD);

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
