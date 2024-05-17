package com.zom;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.List;
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
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Boss Kicker",
	description = "Kick bosses awake"
)
public class BossKicker extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private BossKickerConfig config;

	private LocalPoint supportedKick;
	private NPC genericNPC;

	List<String> kickOptions = new ArrayList<>();
	List<String> bossesToKick = new ArrayList<>();

	@Override
	public void startUp()
	{
		configSet();
	}

	@Override
	public void shutDown()
	{
		kickOptions = null;
		bossesToKick = null;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (configChanged.getGroup().equals(BossKickerConfig.CONFIG_GROUP))
		{
			configSet();
		}
	}

	@Provides
	BossKickerConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BossKickerConfig.class);
	}

	private void configSet()
	{
		kickOptions = Text.fromCSV(config.bossWakeUpOptions());
		bossesToKick = Text.fromCSV(config.bossNames());

		if (kickOptions.size() != bossesToKick.size()) {
			log.info("Bosses to kick and kick options are not equal in length");

			bossesToKick = null;
			kickOptions = null;
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked e)
	{
		if (kickOptions != null && bossesToKick != null)
		{
			return;
		}
		// set the supportedKick value to a non null when you interact with the NPC that is supported
		String target = Text.removeFormattingTags(e.getMenuTarget());
		int index = bossesToKick.indexOf(target);

		if (index != -1)
		{
			String option = Text.removeFormattingTags(e.getMenuOption());
			if (kickOptions.get(index).equals(option))
			{
				supportedKick = LocalPoint.fromScene(e.getParam0(), e.getParam1());
			}
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
		String npcName = event.getNpc().getName();

		if (bossesToKick.contains(npcName)) {
			genericNPC = event.getNpc();
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		String npcName = event.getNpc().getName();

		if (bossesToKick.contains(npcName)) {
			genericNPC = null;
		}
	}

	@Subscribe
	public void onClientTick(ClientTick e)
	{
		if (genericNPC != null && supportedKick != null)
		{
			Player local = client.getLocalPlayer();

			if (genericNPC.getWorldArea().distanceTo(local.getWorldArea()) == 1)
			{
				local.setAnimation(423);
				local.setActionFrame(0);
				supportedKick = null;
			}
		}
	}
}
