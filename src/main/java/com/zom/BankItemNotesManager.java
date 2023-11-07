package com.zom;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemClient;

@Slf4j
@Singleton
public class BankItemNotesManager
{
	Client client;

	ConfigManager configManager;

	ItemClient itemClient;

	private final Gson gson;

	private static final Type typeToken = new TypeToken<List<BankItemNote>>() {}.getType();

	ArrayList<BankItemNote> itemNotesArrayList;

	@Inject
	public BankItemNotesManager(Client client, ItemClient itemClient, Gson gson, ConfigManager configManager)
	{
		this.configManager = configManager;
		this.client = client;
		this.itemClient = itemClient;
		this.gson = gson.newBuilder().create();
	}

	public void refresh()
	{
		String json = configManager.getConfiguration(BankNotesPlugin.CONFIG_GROUP, BankNotesPlugin.CONFIG_KEY);
		if (Strings.isNullOrEmpty(json))
		{
			itemNotesArrayList = new ArrayList<>();
		}
		else
		{

			itemNotesArrayList = gson.fromJson(json, typeToken);
		}
	}

	public void addItem(int itemID, String note)
	{
		// always remove items
		itemNotesArrayList.removeIf(i -> i.getItemid() == itemID);

		// only add items that have a note to be added
		if (!note.equals(""))
		{
			itemNotesArrayList.add(new BankItemNote(itemID, note));
		}

		// save to config
		configManager.setConfiguration(BankNotesPlugin.CONFIG_GROUP, BankNotesPlugin.CONFIG_KEY, gson.toJson(itemNotesArrayList, typeToken));

	}

	public String getItemNote(int itemID)
	{
		Optional<BankItemNote> priceHistoryOptional = itemNotesArrayList.stream().filter(i -> i.getItemid() == itemID).findAny();

		if (priceHistoryOptional.isPresent())
		{
			return priceHistoryOptional.get().getNote();
		}

		return "";
	}
}
