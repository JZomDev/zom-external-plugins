package com.zom.conqol;

public enum ConstuctionMenu
{
	ONE(1),
	TWO(2),
	THREE(3),
	FOUR(4),
	FIVE(5),
	SIX(6),
	SEVEN(7),
	EIGHT(8),
	NINE(9);

	private int id;

	ConstuctionMenu(int id)
	{
		this.id = id;
	}

	public int getNumber()
	{
		return id;
	}
}