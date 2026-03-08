package com.zom.conqol;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.PostMenuSort;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import static net.runelite.api.gameval.InterfaceID.POH_FURNITURE_CREATION;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import static net.runelite.api.gameval.VarbitID.RAIDS_CLIENT_INDUNGEON;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Construction QOL"
)
public class ConQolPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private ConQolConfig config;
	@Inject
	public ConfigManager configManager;

	private final int CONSTRUCTION_WIDGET = POH_FURNITURE_CREATION;
	private final int DIGIT_OFFSET = 48;
	private final Set<Integer> FORESTRY_KIT = ImmutableSet.of(ItemID.FORESTRY_BASKET_CLOSED, ItemID.FORESTRY_BASKET_OPEN, ItemID.FORESTRY_KIT);
	private final Set<Integer> LOG_BASKET = ImmutableSet.of(ItemID.LOG_BASKET_CLOSED, ItemID.LOG_BASKET_OPEN);
	private Menu cacheOptionMenu;
	private final ArrayListMultimap<String, Integer> cacheOptionIndexes = ArrayListMultimap.create();

	private boolean doSwap = false;

	@Override
	protected void startUp()
	{
		doSwap = false;
	}

	@Override
	protected void shutDown()
	{
		doSwap = true;
	}

	@Subscribe
	void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() != CONSTRUCTION_WIDGET)
		{
			return;
		}
		doSwap = true;
	}

	@Subscribe
	void onWidgetClosed(WidgetClosed event)
	{
		if (event.getGroupId() != CONSTRUCTION_WIDGET)
		{
			return;
		}
		doSwap = false;
	}

	@Subscribe
	void onClientTick(ClientTick e)
	{
		if (!doSwap)
		{
			return;
		}
		// index 3 is the specific window containing the constructable items
		Widget furnitureCreationMenuWidget = client.getWidget(CONSTRUCTION_WIDGET, 3);
		// don't swap when in COX raid
		if (furnitureCreationMenuWidget != null && client.getVarbitValue(RAIDS_CLIENT_INDUNGEON) != 1)
		{
			int i = 1;
			for (Widget constuctableItemWidget : furnitureCreationMenuWidget.getStaticChildren())
			{

				String name = constuctableItemWidget.getName();
				if (name == null || name.isEmpty())
				{
					continue;
				}

				new ConstructionMenuItem()
					.constructionWidget(constuctableItemWidget)
					.hotKey(DIGIT_OFFSET + i)
					.checkHotKeySwap();
				i++;
			}
		}
		doSwap = false;
	}

	@Provides
	ConQolConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ConQolConfig.class);
	}

	@NoArgsConstructor
	@Accessors(fluent = true, chain = true)
	class ConstructionMenuItem
	{
		@Setter
		Widget constructionWidget;
		@Setter
		int hotKey;

		public void checkHotKeySwap()
		{
			int inputKeyCode = config.input() + DIGIT_OFFSET;
			int outputKeyCode = config.output() + DIGIT_OFFSET;
			if (hotKey == inputKeyCode)
			{
				hotKey = outputKeyCode;
			}
			else if (hotKey == outputKeyCode)
			{
				hotKey = inputKeyCode;
			}
			setOnKeyListener(hotKey);
		}

		void setOnKeyListener(int keyCode)
		{
			Object[] listener = constructionWidget.getOnKeyListener();

			if (listener == null)
			{
				return;
			}

			listener[4] = String.valueOf((char) keyCode);
			constructionWidget.setOnKeyListener(listener);
			constructionWidget.revalidate();
		}
	}

	private void swap(Menu menu, MenuEntry[] menuEntries, String option, String target, int index)
	{
		// find option to swap with
		int optionIdx = findIndex(menu, menuEntries, index, option, target);

		if (optionIdx >= 0)
		{
			swap(menu, menuEntries, optionIdx, index);
		}
	}

	private void swap(Menu menu, MenuEntry[] entries, int index1, int index2)
	{
		if (index1 == index2)
		{
			return;
		}

		MenuEntry entry1 = entries[index1],
			entry2 = entries[index2];

		entries[index1] = entry2;
		entries[index2] = entry1;

		// Item op4 and op5 are CC_OP_LOW_PRIORITY so they get added underneath Use,
		// but this also makes them right-click only. Change them to CC_OP to avoid this.
		if (entry1.getType() == MenuAction.CC_OP_LOW_PRIORITY)
		{
			entry1.setType(MenuAction.CC_OP);
		}
		if (entry2.getType() == MenuAction.CC_OP_LOW_PRIORITY)
		{
			entry2.setType(MenuAction.CC_OP);
		}

		menu.setMenuEntries(entries);

		// Update optionIndexes
		if (cacheOptionMenu == menu)
		{
			String option1 = Text.removeTags(entry1.getOption()).toLowerCase(),
				option2 = Text.removeTags(entry2.getOption()).toLowerCase();

			List<Integer> list1 = cacheOptionIndexes.get(option1),
				list2 = cacheOptionIndexes.get(option2);

			// call remove(Object) instead of remove(int)
			list1.remove((Integer) index1);
			list2.remove((Integer) index2);

			sortedInsert(list1, index2);
			sortedInsert(list2, index1);
		}
	}

	private static <T extends Comparable<? super T>> void sortedInsert(List<T> list, T value)
	{
		int idx = Collections.binarySearch(list, value);
		list.add(idx < 0 ? -idx - 1 : idx, value);
	}


	private int findIndex(Menu menu, MenuEntry[] entries, int limit, String option, String target)
	{
		// Without strict matching we have to iterate all entries up to the current limit...
		for (int i = limit - 1; i >= 0; i--)
		{
			MenuEntry entry = entries[i];
			String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
			String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

			if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target))
			{
				return i;
			}
		}

		return -1;
	}

	private boolean hasLogBasket()
	{
		if (client.getLocalPlayer() == null)
		{
			return false;
		}
		if (client.getItemContainer(InventoryID.INV) == null)
		{
			return false;
		}
		for (Integer item : LOG_BASKET)
		{
			if (client.getItemContainer(InventoryID.INV).contains(item))
			{
				return true;
			}
		}
		for (Integer item : FORESTRY_KIT)
		{
			if (client.getItemContainer(InventoryID.INV).contains(item))
			{
				return true;
			}
		}
		return false;
	}

	private static final String SHIFTCLICK_CONFIG_GROUP = "shiftclick";
	private static final String ITEM_KEY_PREFIX = "item_";

	private Integer getItemSwapConfig(int itemId)
	{
		itemId = ItemVariationMapping.map(itemId);
		String config = configManager.getConfiguration(SHIFTCLICK_CONFIG_GROUP, ITEM_KEY_PREFIX + itemId);
		if (config == null || config.isEmpty())
		{
			return null;
		}

		return Integer.parseInt(config);
	}

	private void swapMenuEntry(MenuEntry parent, Menu menu, MenuEntry[] menuEntries, int index, MenuEntry menuEntry)
	{
		Menu sub = menuEntry.getSubMenu();
		if (sub != null)
		{
			int subidx = 0;
			MenuEntry[] subEntries = sub.getMenuEntries();
			for (MenuEntry subEntry : subEntries)
			{
				swapMenuEntry(menuEntry, sub, subEntries, subidx++, subEntry);
			}
		}

		final String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();

		final Widget w = parent != null ? parent.getWidget() : menuEntry.getWidget();
		// Custom item swap
		if (w != null && WidgetUtil.componentToInterface(w.getId()) == InterfaceID.INVENTORY)
		{
			// empty is in a submenu and has a swap index of 67081517
			Integer swapIndex = getItemSwapConfig(w.getItemId());
			if (swapIndex != null)
			{
				if (swapIndex == -1)
				{
					swap(menu, menuEntries, "use", target, index);
				}
				else if (swapIndex == menuEntry.getItemOp())
				{
					swap(menu, menuEntries, index, menuEntries.length - 1);
				}
				// Submenu swap. The swapIndex is actually the option hashCode.
				else if (parent != null && menuEntry.getOption().hashCode() == swapIndex)
				{
					// Since it isn't possible to reparent the menu to the top level, just copy it
					client.getMenu().createMenuEntry(-1)
						.setOption(menuEntry.getOption())
						.setTarget(menuEntry.getTarget())
						.setIdentifier(menuEntry.getIdentifier())
						.setType(menuEntry.getType() == MenuAction.CC_OP_LOW_PRIORITY ? MenuAction.CC_OP : menuEntry.getType())
						.setItemId(menuEntry.getItemId())
						.setParam0(menuEntry.getParam0())
						.setParam1(menuEntry.getParam1())
						.onClick(menuEntry.onClick());
				}
			}
		}
	}

	private boolean itemIsLogBasket(int itemID)
	{
		return FORESTRY_KIT.contains(itemID) || LOG_BASKET.contains(itemID);
	}

	@Subscribe(priority = -2)
	public void onPostMenuSort(PostMenuSort postMenuSort)
	{
		if (!config.swapLogBasket())
		{
			return;
		}
		// The menu is not rebuilt when it is open, so don't swap or else it will
		// repeatedly swap entries
		if (client.isMenuOpen())
		{
			return;
		}

		if (hasLogBasket() && isNearSawmill())
		{
			Menu root = client.getMenu();
			MenuEntry[] menuEntries = root.getMenuEntries();

			// Perform swaps
			int idx = 0;
			for (MenuEntry entry : menuEntries)
			{
				if (!itemIsLogBasket(entry.getItemId()))
				{
					continue;
				}
				swapMenuEntry(null, root, menuEntries, idx++, entry);
			}

			// invalidate option index cache
			cacheOptionIndexes.clear();
			cacheOptionMenu = null;
		}
	}

	private boolean isNearSawmill()
	{
		if (client.getLocalPlayer() == null)
		{
			return false;
		}
		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

		WorldPoint auburnvaleSawmill = new WorldPoint(1395, 3368, 0);
		WorldPoint varrockSawmill = new WorldPoint(3302, 3491, 0);
		WorldPoint prifddinasSawmill = new WorldPoint(3315, 6116, 0);
		WorldPoint woodcuttingGuildSawmille = new WorldPoint(1624, 3500, 0);

		if (playerLocation.distanceTo(auburnvaleSawmill) <= 1)
		{
			return true;
		}
		if (playerLocation.distanceTo(varrockSawmill) <= 1)
		{
			return true;
		}
		if (playerLocation.distanceTo(prifddinasSawmill) <= 2)
		{
			return true;
		}
		if (playerLocation.distanceTo(woodcuttingGuildSawmille) <= 1)
		{
			return true;
		}
		return false;
	}
}
