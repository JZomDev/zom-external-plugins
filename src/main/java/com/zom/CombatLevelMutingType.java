package com.zom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CombatLevelMutingType
{
	OVERHEAD("Overhead"),
	CHATBOX("Chat Box"),
	BOTH("Both");

	private final String type;

	@Override
	public String toString()
	{
		return type;
	}
}
