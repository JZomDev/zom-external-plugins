package com.zom;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuEntry;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;

public class BankItemNotesOverlay extends Overlay
{
	private static final int BANK_ITEM_WIDGETID = ComponentID.BANK_ITEM_CONTAINER;
	private final BankNotesPlugin plugin;
	private final Client client;
	private final BankItemNotesManager itemPriceHistoryManager;
	private final TooltipManager tooltipManager;

	@Inject
	BankItemNotesOverlay(BankNotesPlugin plugin, Client client, BankItemNotesManager itemPriceHistoryManager, TooltipManager tooltipManager)
	{
		setPosition(OverlayPosition.DYNAMIC);
		this.plugin = plugin;
		this.client = client;
		this.itemPriceHistoryManager = itemPriceHistoryManager;
		this.tooltipManager = tooltipManager;
	}
	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isShowPriceHistory()) return null;

		if (client.isMenuOpen())
		{
			return null;
		}

		final MenuEntry[] menuEntries = client.getMenuEntries();
		final int last = menuEntries.length - 1;

		if (last < 0)
		{
			return null;
		}

		final MenuEntry menuEntry = menuEntries[last];
		final int widgetId = menuEntry.getParam1();
		final int groupId = WidgetUtil.componentToInterface(widgetId);

		addTooltip(menuEntry, groupId);
		return null;
	}

	private void addTooltip(MenuEntry menuEntry, int groupId)
	{
		if (groupId == InterfaceID.BANK)
		{
			final String text = makeValueTooltip(menuEntry);
			if (text != null)
			{
				// Make tooltip
				tooltipManager.add(new Tooltip(ColorUtil.prependColorTag(text, new Color(238, 238, 238))));
			}
		}
	}

	private String makeValueTooltip(MenuEntry menuEntry)
	{
		final int widgetId = menuEntry.getParam1();
		ItemContainer container = null;

		if (widgetId == BANK_ITEM_WIDGETID)
		{
			container = client.getItemContainer(InventoryID.BANK);
		}

		if (container == null)
		{
			return null;
		}

		// Find the item in the container to get stack size
		final int index = menuEntry.getParam0();
		final Item item = container.getItem(index);

		if (item == null)
		{
			return null;
		}

		// Disabling both disables all value tooltips
		if (!itemPriceHistoryManager.getItemNote(item.getId()).equals(""))
		{
			return "Item note(s): " + itemPriceHistoryManager.getItemNote(item.getId());
		}
		return null;
	}
}
