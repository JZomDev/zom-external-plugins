package com.zom;

import java.util.Objects;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Boss Kicker",
	description = "Kick bosses awake"
)
public class BossKicker extends Plugin
{
	@Inject
	private Client client;

	private LocalPoint supportedKick;
	private NPC genericNPC;

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked e)
	{
		// set the supportedKick value to a non null when you interact with the NPC that is supported
		if (e.getMenuOption().equals("Disturb") && e.getMenuTarget().endsWith("Odd Figure"))
		{
			supportedKick = LocalPoint.fromScene(e.getParam0(), e.getParam1());
		}
		else if (e.getMenuOption().equals("Poke") && e.getMenuTarget().endsWith("Vorkath"))
		{
			supportedKick = LocalPoint.fromScene(e.getParam0(), e.getParam1());
		}
		else
		{
			supportedKick = null;
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		// assign the genericNPC to the boss that is spawned so that we can compare our distance to it
		if (Objects.equals(event.getNpc().getName(), "Vorkath"))
		{
			genericNPC = event.getNpc();
		}

		if (Objects.equals(event.getNpc().getName(), "Odd Figure"))
		{
			genericNPC = event.getNpc();
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		// unassign the genericNPC if we leave or it dies or waht ever
		if (Objects.equals(event.getNpc().getName(), "Vorkath"))
		{
			genericNPC = null;
		}

		if (Objects.equals(event.getNpc().getName(), "Odd Figure"))
		{
			genericNPC = null;
		}
	}

	@Subscribe
	public void onClientTick(ClientTick e)
	{
		if (genericNPC != null && supportedKick != null)
		{
			int distance;
			switch (Objects.requireNonNull(genericNPC.getName())) {
				case "Odd Figure":
				case "Vorkath":
					distance = 1;
					break;
				default:
					distance = 1;
			}

			Player local = client.getLocalPlayer();

			if (genericNPC.getWorldArea().distanceTo(local.getWorldArea()) == distance)
			{
				local.setAnimation(423);
				local.setActionFrame(0);
				supportedKick = null;
			}
		}
	}
}