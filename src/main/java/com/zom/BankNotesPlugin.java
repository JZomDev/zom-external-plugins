package com.zom;

import com.google.gson.Gson;
import java.util.function.Consumer;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import static net.runelite.client.plugins.banktags.tabs.TabInterface.FILTERED_CHARS;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

@Slf4j
@PluginDescriptor(
	name = "Bank Notes",
	tags = "bank,merching,merch,item,history,price,notes"
)
public class BankNotesPlugin extends Plugin
{

	static String CONFIG_GROUP = "itemnotesplugin";
	static String CONFIG_KEY = "itemnotesjson";
	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ChatboxPanelManager chatboxPanelManager;

	@Inject
	Gson gson;

	@Inject
	private ClientThread clientThread;

	@Inject
	private TooltipManager tooltipManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BankItemNotesOverlay overlay;

	boolean addEditPriceHistory = true;
	@Getter
	boolean showPriceHistory = true;

	@Inject
	BankItemNotesManager itemPriceHistoryManager;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		itemPriceHistoryManager.refresh();
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (addEditPriceHistory && event.getActionParam1() == WidgetInfo.BANK_ITEM_CONTAINER.getId()
			&& event.getOption().equals("Examine"))
		{
			String text = "Edit Item Notes";

			client.createMenuEntry(-1)
				.setParam0(event.getActionParam0())
				.setParam1(event.getActionParam1())
				.setTarget(event.getTarget())
				.setOption(text)
				.setType(MenuAction.RUNELITE)
				.setIdentifier(event.getIdentifier())
				.onClick(this::editTags);
		}
	}

	private void editTags(MenuEntry entry)
	{
		int inventoryIndex = entry.getParam0();
		ItemContainer bankContainer = client.getItemContainer(InventoryID.BANK);
		if (bankContainer == null)
		{
			return;
		}
		Item[] items = bankContainer.getItems();
		if (inventoryIndex < 0 || inventoryIndex >= items.length)
		{
			return;
		}
		Item item = bankContainer.getItems()[inventoryIndex];
		if (item == null)
		{
			return;
		}

		int itemId = item.getId();
		ItemComposition itemComposition = itemManager.getItemComposition(itemId);
		String name = itemComposition.getName();

		String initialValue = itemPriceHistoryManager.getItemNote(itemId);

		chatboxPanelManager.openTextInput(name + ": ")
			.addCharValidator(FILTERED_CHARS)
			.value(initialValue)
			.onDone((Consumer<String>) (newValue) ->
				clientThread.invoke(() ->
				{
					itemPriceHistoryManager.addItem(itemId, newValue);
				}))
			.build();
	}
}
