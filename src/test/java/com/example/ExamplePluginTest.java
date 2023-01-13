package com.example;

import com.slashswapper.SlashSwapperPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SlashSwapperPlugin.class);
		RuneLite.main(args);
	}
}