package com.brooklyn.annoyancemute;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AnnoyanceMutePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(AnnoyanceMutePlugin.class);
		RuneLite.main(args);
	}
}