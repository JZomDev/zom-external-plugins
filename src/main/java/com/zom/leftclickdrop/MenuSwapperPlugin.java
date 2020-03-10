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

import com.google.common.collect.ArrayListMultimap;
import com.google.inject.Provides;
import java.util.HashSet;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ClientTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Custom Left Click Drop"
)
public class MenuSwapperPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private MenuSwapperConfig config;

	@Inject
	private ItemManager itemManager;

	private final ArrayListMultimap<String, Integer> optionIndexes = ArrayListMultimap.create();
	private List<String> itemList;
	private static final int DEFAULT_DELAY = 5;

	private HashSet<String> releaseItems = new HashSet<>();

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
			final Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
			final int if1DraggedItemIndex = client.getIf1DraggedItemIndex();
			final WidgetItem draggedItem = inventoryWidget.getWidgetItem(if1DraggedItemIndex);

			if (itemList == null || itemList.size() == 0)
			{
				client.setInventoryDragDelay(DEFAULT_DELAY); // ensure default delay
				return;
			}

			if (menuEntry == null || !(menuEntry.length == 4 || menuEntry.length == 5))
			{
				if (draggedItem.getId() != -1)
				{
					if (!itemList.contains(getItemName(draggedItem.getId())))
					{
						// ensure default delay only if not currently dragging an item
						// you would set default delay if you dragged an item over top of a non left click droppable item
						client.setInventoryDragDelay(DEFAULT_DELAY);
					}
				}
				return;
			}

			// menuEntry.length - 2 is the default left click option. Use,Wear,Wield,Break, etc.
			final String option = Text.removeTags(menuEntry[menuEntry.length - 2].getOption()).toLowerCase();
			final String target = Text.removeTags(menuEntry[menuEntry.length - 2].getTarget()).toLowerCase();

			for (String item : itemList)
			{
				if (item.equals(target) && isGenericItem())
				{

					if (config.antiDragEnable())
					{
						client.setInventoryDragDelay(config.antiDragDelay());
					}

					// salamanders are the exception to the rule below
					if (option.equals("wield") && releaseItems.contains(target))
					{
						swap("release", option, target, true);
					}
					// swap use with drop, only on items that are a generic item (not equipment, food, teleport tab, etc)
					else if (option.equals("use"))
					{
						swap("drop", option, target, true);
					}
				}
			}

			if (draggedItem.getId() == -1)
			{
				if (!itemList.contains(getItemName(draggedItem.getId())))
				{
					client.setInventoryDragDelay(DEFAULT_DELAY); // ensure default delay only if not currently dragging an item
				}

			}
		} catch (Exception e) {
			client.setInventoryDragDelay(DEFAULT_DELAY); // ensure default delay
		}
	}

	// returns true if the item is a generic item, generic items only have use, drop, examine, cancel.
	private boolean isGenericItem()
	{
		MenuEntry[] entries = client.getMenuEntries();
		boolean destroyAble = true;
		boolean release = false;
		for (MenuEntry entry : entries)
		{
			if (Text.removeTags(entry.getOption().toLowerCase()).equals("drop"))
			{
				destroyAble = false;
			}

			if (Text.removeTags(entry.getOption().toLowerCase()).equals("release"))
			{
				release = true;
			}
		}

		return !destroyAble && entries.length == 4 || release;
	}

	private void swap(String optionA, String optionB, String target, boolean strict)
	{
		MenuEntry[] entries = client.getMenuEntries();

		int idxA = searchIndex(entries, optionA, target, strict);
		int idxB = searchIndex(entries, optionB, target, strict);

		if (idxA >= 0 && idxB >= 0)
		{
			MenuEntry entry = entries[idxA];
			entries[idxA] = entries[idxB];
			entries[idxB] = entry;

			client.setMenuEntries(entries);
		}
	}

	private String getItemName(int id)
	{
		return itemManager.getItemComposition(id).getName().toLowerCase().trim();
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
	MenuSwapperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MenuSwapperConfig.class);
	}

	@Override
	protected void startUp()
	{
		itemList = Text.fromCSV(config.itemList().toLowerCase());
		releaseItems.add("black salamander");
		releaseItems.add("orange salamander");
		releaseItems.add("red salamander");
		releaseItems.add("swamp lizard");
	}

	@Override
	protected void shutDown()
	{
		releaseItems.clear();
		itemList.clear();
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("leftclickdrop"))
		{
			itemList = Text.fromCSV(config.itemList().toLowerCase());
		}
	}
}
