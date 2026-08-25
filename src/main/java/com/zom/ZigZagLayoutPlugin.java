package com.zom;

import com.google.common.collect.ImmutableSet;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.banktags.BankTagsPlugin;
import net.runelite.client.plugins.banktags.tabs.AutoLayout;
import net.runelite.client.plugins.banktags.tabs.Layout;
import net.runelite.client.plugins.banktags.tabs.LayoutManager;

import java.util.*;

@Slf4j
@PluginDescriptor(
	name = "ZigZag Layout",
	description = "Adds ZigZag layout option to core Bank Tags"
)
@PluginDependency(BankTagsPlugin.class)
public class ZigZagLayoutPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
    LayoutManager layoutManager;

	@Inject
	ZigZagLayoutConfig config;

	@Inject
	private ItemManager itemManager;

	private static final Set<Integer> DIZANAS_QUIVER_IDS = ImmutableSet.<Integer>builder()
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DIZANAS_QUIVER_CHARGED)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DIZANAS_QUIVER_INFINITE)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.SKILLCAPE_MAX_DIZANAS)))
			.build();

	@Override
	protected void startUp() throws Exception
	{
		register();
	}

	@Override
	protected void shutDown() throws Exception
	{
		layoutManager.unregisterAutoLayout("ZigZag");
	}

	private void register()
	{
		// remove if applicable
		layoutManager.unregisterAutoLayout("ZigZag");
		// add layout
		layoutManager.registerAutoLayout(this, "ZigZag", new AutoLayout() {
			@Override
			public net.runelite.client.plugins.banktags.tabs.Layout generateLayout(net.runelite.client.plugins.banktags.tabs.Layout previous) {


				Layout l = new Layout(previous);
				List<Integer> removed = new ArrayList<>();

				// Equipment
				ItemContainer e = client.getItemContainer(InventoryID.WORN);
				// Inventory
				ItemContainer i = client.getItemContainer(InventoryID.INV);

				int quiverSpot = -1;
				if (e != null)
				{
					int[] format = {
							EquipmentInventorySlot.HEAD.getSlotIdx(),
							EquipmentInventorySlot.CAPE.getSlotIdx(),
							EquipmentInventorySlot.AMULET.getSlotIdx(),
							EquipmentInventorySlot.WEAPON.getSlotIdx(),
							EquipmentInventorySlot.BODY.getSlotIdx(),
							EquipmentInventorySlot.SHIELD.getSlotIdx(),
							EquipmentInventorySlot.ARMS.getSlotIdx(),
							EquipmentInventorySlot.LEGS.getSlotIdx(),
							EquipmentInventorySlot.HAIR.getSlotIdx(),
							EquipmentInventorySlot.GLOVES.getSlotIdx(),
							EquipmentInventorySlot.BOOTS.getSlotIdx(),
							EquipmentInventorySlot.JAW.getSlotIdx(),
							EquipmentInventorySlot.RING.getSlotIdx(),
							EquipmentInventorySlot.AMMO.getSlotIdx()
					};

					// first pass to set up size, because some spots can be naked
					ArrayList<Integer> format2 = new ArrayList<>();
					for (int j : format)
					{
						Item item = e.getItem(j);
						if (item != null)
						{
							format2.add(itemManager.canonicalize(item.getId()));
						}
					}

					for (int pos = 0; pos < 16; ++pos)
					{
						int lPos = (pos % 2 == 0)
								? pos / 2
								: 8 + (pos / 2);

						if (pos < format2.size())
						{
							l.setItemAtPos(format2.get(pos), lPos);
						}
						else
						{
							l.setItemAtPos(-1, lPos);
						}
					}

					// calculate quiver spot
					quiverSpot = (format2.size() % 2 == 0)
							? format2.size() / 2
							: 8 + (format2.size() / 2);
				}

				int lastItem = -1;
				if (i != null)
				{
					int base = 16;
					int secondRow = 16;;
					for (int pos = 0; pos < 32; ++pos)
					{

						int lpos;
						if (pos < secondRow)
						{
							lpos = (pos % 2 == 0)
									? pos / 2
									: 8 + (pos / 2);
						}
						else
						{
							lpos = (pos % 2 == 0)
									? secondRow + (pos - secondRow) / 2
									: 24 + (pos - secondRow) / 2;
						}
						lpos += base;
						int old = l.getItemAtPos(lpos);
						if (old != -1)
						{
							removed.add(old);
						}

						Item item = i.getItem(pos);
						if (item != null)
						{
							l.setItemAtPos(itemManager.canonicalize(item.getId()), lpos);
							if (lpos > lastItem)
							{
								lastItem = lpos;
							}
						}
						else
						{
							l.setItemAtPos(-1, lpos);
						}
					}
				}

				// Rune pouch
				if (i != null && hasRunePouch(i))
				{
					final int[] RUNEPOUCH_RUNES = {
							VarbitID.RUNE_POUCH_TYPE_1, VarbitID.RUNE_POUCH_TYPE_2, VarbitID.RUNE_POUCH_TYPE_3, VarbitID.RUNE_POUCH_TYPE_4
					};
					final EnumComposition runepouchEnum = client.getEnum(EnumID.RUNEPOUCH_RUNE);

					if (config.runePouchPlacement() != RunePouchPlacement.TOP)
					{
						int lpos = RunePouchPlacement.DEFAULT == config.runePouchPlacement() ? 48 : ((lastItem / 8) * 8 + 8);
						for (int idx = 0; idx < RUNEPOUCH_RUNES.length; ++idx, ++lpos)
						{
							int runeId = client.getVarbitValue(RUNEPOUCH_RUNES[idx]);
							if (runeId > 0)
							{
								int itemId = runepouchEnum.getIntValue(runeId);

								int old = l.getItemAtPos(lpos);
								if (old != -1)
								{
									removed.add(old);
								}

								l.setItemAtPos(itemId, lpos);
							}
						}
						// blank the rest of the row
						for (int idx = 0; idx < 4; ++idx, ++lpos)
						{
							int old = l.getItemAtPos(lpos);
							if (old != -1)
							{
								removed.add(old);
							}

							l.setItemAtPos(-1, lpos);
						}
					}

					if (config.runePouchPlacement() == RunePouchPlacement.TOP)
					{
						// top right of the bank
						int[] spots = {6, 7, 14, 15};
						for (int idx = 0; idx < RUNEPOUCH_RUNES.length; ++idx)
						{
							int runeId = client.getVarbitValue(RUNEPOUCH_RUNES[idx]);
							if (runeId > 0)
							{
								int itemId = runepouchEnum.getIntValue(runeId);

								int old = l.getItemAtPos(spots[idx]);
								if (old != -1)
								{
									removed.add(old);
								}

								l.setItemAtPos(itemId, spots[idx]);
							}
						}
					}

				}

				// quiver
				if (hasQuiver(i, e))
				{
					final int quiverAmmo = client.getVarpValue(VarPlayerID.DIZANAS_QUIVER_TEMP_AMMO);
					l.setItemAtPos(quiverAmmo, quiverSpot);
				}


				int pos = 56;
				for (int itemId : removed)
				{
					if (l.count(itemId) == 0)
					{
						if (log.isDebugEnabled())
						{
							log.debug("Adding {} at {}", itemManager.getItemComposition(itemId).getName(), pos);
						}
						l.addItemAfter(itemId, pos++);
					}
				}

				return l;
			}

			private boolean hasQuiver(ItemContainer inv, ItemContainer worn)
			{
				return (inv != null && DIZANAS_QUIVER_IDS.stream().anyMatch(inv::contains))
						|| (worn != null && DIZANAS_QUIVER_IDS.stream().anyMatch(worn::contains));

			}

			private boolean hasRunePouch(ItemContainer inv)
			{
				Collection<Integer> runePouchVariations = ItemVariationMapping.getVariations(net.runelite.api.gameval.ItemID.BH_RUNE_POUCH);
				Collection<Integer> divineRunePouchVariations = ItemVariationMapping.getVariations(ItemID.DIVINE_RUNE_POUCH);
				return runePouchVariations.stream().anyMatch(inv::contains) || divineRunePouchVariations.stream().anyMatch(inv::contains);
			}
		});
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged e)
	{
		if (e.getGroup().equals(ZigZagLayoutConfig.CONFIG_GROUP))
		{
			register();
		}
	}

	@Provides
	ZigZagLayoutConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ZigZagLayoutConfig.class);
	}
}
