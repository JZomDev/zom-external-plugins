/*
 * Copyright (c) 2019, Jordan Zomerlei <https://github.com/JZomerlei>
 * All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.zom.leftclickdrop;

import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.inject.Provides;
import java.util.HashSet;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ClientTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Custom Left Click Drop"
)
public class ZomLeftClickDropper extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ZomLeftClickDropperConfig config;

	private final ArrayListMultimap<String, Integer> optionIndexes = ArrayListMultimap.create();
	private List<String> itemList;

	private HashSet<String> releaseItems;

	private Splitter CONFIG_SPLITTER = Splitter
		.onPattern("([,\n])")
		.omitEmptyStrings()
		.trimResults();

	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		// The menu is not rebuilt when it is open, so don't swap or else it will
		// repeatedly swap entries
		if (client.getGameState() != GameState.LOGGED_IN || client.isMenuOpen())
		{
			return;
		}

		MenuEntry[] menuEntries = client.getMenuEntries();

		// Build option map for quick lookup in findIndex
		int idx = 0;
		optionIndexes.clear();
		for (MenuEntry entry : menuEntries)
		{
			String option = Text.removeTags(entry.getOption()).toLowerCase();
			optionIndexes.put(option, idx++);
		}

		swapMenuEntry(menuEntries);
	}

	private void swapMenuEntry(MenuEntry[] menuEntry)
	{
		try
		{
			if (itemList == null || menuEntry == null|| itemList.size() == 0)
			{
				return;
			}

			// menuEntry.length - 1 is the default left click option. Use,Wear,Wield,Break, etc.
			final String option = Text.removeTags(menuEntry[menuEntry.length - 1].getOption()).toLowerCase();
			final String target = Text.removeTags(menuEntry[menuEntry.length - 1].getTarget()).toLowerCase();

			for (String item : itemList)
			{
				if (item.equals(target))
				{

					// salamanders are the exception to the rule below
					if (option.equals("wield") && releaseItems.contains(target))
					{
						swap("release", option, target, true);
					}
					// swap first option with drop
					else
					{
						swap("drop", option, target, true);
					}
				}
			}

		} catch (Exception ignored) {
			// ignored
		}
	}

	private void swap(String optionA, String optionB, String target, boolean strict)
	{
		MenuEntry[] entries = client.getMenuEntries();

		int idxA = searchIndex(entries, optionA, target, strict);
		int idxB = searchIndex(entries, optionB, target, strict);

		if (idxA != idxB)
		{
			MenuEntry entry1 = entries[idxB];
			MenuEntry entry2 = entries[idxA];
			entries[idxA] = entry1;
			entries[idxB] = entry2;

			// Item op4 and op5 are CC_OP_LOW_PRIORITY so they get added underneath Use,
			// but this also causes them to get sorted after client tick. Change them to
			// CC_OP to avoid this.
			if (entry1.isItemOp() && entry1.getType() == MenuAction.CC_OP_LOW_PRIORITY)
			{
				entry1.setType(MenuAction.CC_OP);
			}
			if (entry2.isItemOp() && entry2.getType() == MenuAction.CC_OP_LOW_PRIORITY)
			{
				entry2.setType(MenuAction.CC_OP);
			}

			client.setMenuEntries(entries);
		}
	}

	private int searchIndex(MenuEntry[] entries, String option, String target, boolean strict)
	{
		for (int i = entries.length - 1; i >= 0; i--)
		{
			MenuEntry entry = entries[i];
			String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
			String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

			if (strict)
			{
				if (entryOption.equals(option) && entryTarget.equals(target))
				{
					return i;
				}
			}
			else
			{
				if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target))
				{
					return i;
				}
			}
		}
		return -1;
	}

	@Provides
	ZomLeftClickDropperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ZomLeftClickDropperConfig.class);
	}

	@Override
	protected void startUp()
	{
		itemList = CONFIG_SPLITTER.splitToList(config.itemList().toLowerCase());
		releaseItems = new HashSet<>();
		releaseItems.add("black salamander");
		releaseItems.add("orange salamander");
		releaseItems.add("red salamander");
		releaseItems.add("swamp lizard");
	}

	@Override
	protected void shutDown()
	{
		releaseItems = null;
		itemList = null;
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("leftclickdrop"))
		{
			itemList = CONFIG_SPLITTER.splitToList(config.itemList().toLowerCase());
		}
	}
}
