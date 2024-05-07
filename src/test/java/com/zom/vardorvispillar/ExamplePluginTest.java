package com.zom.vardorvispillar;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(VardorvisPillarHiderPlugin.class);
		RuneLite.main(args);
	}
}