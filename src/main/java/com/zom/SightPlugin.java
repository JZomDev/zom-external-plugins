package com.zom;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Sight"
)
public class SightPlugin extends Plugin
{
	@Override
	protected void startUp() throws Exception
	{
		log.debug("Sight started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Sight stopped!");
	}
}
