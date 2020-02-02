package com.zom.nmz_util;

import com.google.common.collect.ArrayListMultimap;
import com.google.inject.Provides;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.Skill;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "NMZ Utilties"
)
public class NMZUtilPlugin extends Plugin
{
	private static final int[] NMZ_MAP_REGION = {9033};

	@Inject
	private Client client;

	@Inject
	private Notifier notifier;

	@Inject
	private NMZUtilConfig config;

	// This starts are false since you need to reach the threshold first before you can be notified your health is too high.
	private boolean hitPointsReduced = false;
	private boolean hitpointsNotificationSend = true;
	private final ArrayListMultimap<String, Integer> optionIndexes = ArrayListMultimap.create();

	@Subscribe
	private void onGameTick(GameTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN || client.isMenuOpen())
		{
			return;
		}

		if (!isInNightmareZone())
		{
			if (!hitpointsNotificationSend)
			{
				hitpointsNotificationSend = true;
			}
		}
		if (config.hitPointsNotification())
		{
			checkHealth();
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		MenuEntry[] menuEntries = client.getMenuEntries();

		// Build option map for quick lookup in findIndex
		int idx = 0;
		optionIndexes.clear();
		for (MenuEntry entry : menuEntries)
		{
			String option = Text.removeTags(entry.getOption()).toLowerCase();
			optionIndexes.put(option, idx++);
		}

		idx = 0;
		for (MenuEntry entry : menuEntries)
		{
			swapMenuEntry(idx++, entry);
		}
	}

	private void checkHealth()
	{
		final int realHealth = client.getRealSkillLevel(Skill.HITPOINTS);
		final int currentHealth = client.getBoostedSkillLevel(Skill.HITPOINTS);

		if (config.hitPointThreshold() >= realHealth || config.hitPointThreshold() == 0)
		{
			return;
		}

		if (!hitpointsNotificationSend && hitPointsReduced)
		{
			if (currentHealth > config.hitPointThreshold())
			{
				notifier.notify("Hitpoints are above: " + config.hitPointThreshold());
				hitpointsNotificationSend = true;
			}
		}
		else
		{
			if (currentHealth <= config.hitPointThreshold())
			{
				hitPointsReduced = true;
				hitpointsNotificationSend = false;
			}
		}
	}

	private void swapMenuEntry(int index, MenuEntry menuEntry)
	{
		final String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
		final String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();

		if (config.rockCake() && target.equals("dwarven rock cake") && option.equals("eat")) {
			swap("guzzle", option, target, index);
		}
	}

	private void swap(String optionA, String optionB, String target, int index)
	{
		swap(optionA, optionB, target, index, true);
	}

	private void swap(String optionA, String optionB, String target, int index, boolean strict)
	{
		MenuEntry[] menuEntries = client.getMenuEntries();

		int thisIndex = findIndex(menuEntries, index, optionB, target, strict);
		int optionIdx = findIndex(menuEntries, thisIndex, optionA, target, strict);

		if (thisIndex >= 0 && optionIdx >= 0)
		{
			swap(optionIndexes, menuEntries, optionIdx, thisIndex);
		}
	}

	private int findIndex(MenuEntry[] entries, int limit, String option, String target, boolean strict)
	{
		if (strict)
		{
			List<Integer> indexes = optionIndexes.get(option);

			// We want the last index which matches the target, as that is what is top-most
			// on the menu
			for (int i = indexes.size() - 1; i >= 0; --i)
			{
				int idx = indexes.get(i);
				MenuEntry entry = entries[idx];
				String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

				// Limit to the last index which is prior to the current entry
				if (idx <= limit && entryTarget.equals(target))
				{
					return idx;
				}
			}
		}
		else
		{
			// Without strict matching we have to iterate all entries up to the current limit...
			for (int i = limit; i >= 0; i--)
			{
				MenuEntry entry = entries[i];
				String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
				String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

				if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target))
				{
					return i;
				}
			}

		}

		return -1;
	}

	private void swap(ArrayListMultimap<String, Integer> optionIndexes, MenuEntry[] entries, int index1, int index2)
	{
		MenuEntry entry = entries[index1];
		entries[index1] = entries[index2];
		entries[index2] = entry;

		client.setMenuEntries(entries);

		// Rebuild option indexes
		optionIndexes.clear();
		int idx = 0;
		for (MenuEntry menuEntry : entries)
		{
			String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
			optionIndexes.put(option, idx++);
		}
	}

	public boolean isInNightmareZone()
	{
		return Arrays.equals(client.getMapRegions(), NMZ_MAP_REGION);
	}

	@Override
	protected void startUp() throws Exception
	{
		hitpointsNotificationSend = true;
	}

	@Override
	protected void shutDown() throws Exception
	{
		hitpointsNotificationSend = true;
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("nmzutil"))
		{
			hitPointsReduced = false;
		}
	}

	@Provides
	NMZUtilConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NMZUtilConfig.class);
	}
}
