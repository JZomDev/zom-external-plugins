package com.zom.dense_essence;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class DenseEssenceTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(DenseEssencePlugin.class);
		RuneLite.main(args);
	}
}