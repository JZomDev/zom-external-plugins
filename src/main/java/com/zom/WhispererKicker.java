package com.zom;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Whisperer Kick",
	description = "Kick Whisperer awake"
)
public class WhispererKicker extends Plugin
{
	@Inject
	private Client client;

	private LocalPoint whispererLocation;

	private static int DISTANCE = 8248;

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked e)
	{
		if (e.getMenuOption().equals("Disturb") && e.getMenuTarget().endsWith("Odd Figure"))
		{
			whispererLocation = LocalPoint.fromScene(e.getParam0(), e.getParam1());
		}
		else
		{
			whispererLocation = null;
		}
	}

	@Subscribe
	public void onClientTick(ClientTick e)
	{
		if (whispererLocation != null)
		{
			Player local = client.getLocalPlayer();

			if (whispererLocation.distanceTo(local.getLocalLocation()) <= DISTANCE)
			{
				local.setAnimation(423);
				local.setActionFrame(0);
				whispererLocation = null;
			}
		}
	}
}