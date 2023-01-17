package com.example;

import com.zom.AFKGuardiansPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(AFKGuardiansPlugin.class);
		RuneLite.main(args);
	}
}