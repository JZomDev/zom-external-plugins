package com.zom.leftclickdrop;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExternalPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MenuSwapperPlugin.class);
		RuneLite.main(args);
	}
}