package com.slashswapper;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import net.runelite.client.plugins.Plugin;

public class SlashSwapperPluginTest
{
	public static void main(String[] args) throws Exception
	{
		@SuppressWarnings("unchecked")
		var plugins = (Class<? extends Plugin>[]) new Class[]{SlashSwapperPlugin.class};
		ExternalPluginManager.loadBuiltin(plugins);
		RuneLite.main(args);
	}
}
