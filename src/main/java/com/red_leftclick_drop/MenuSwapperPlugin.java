package com.red_leftclick_drop;

import com.google.common.collect.ArrayListMultimap;
import com.google.inject.Provides;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ClientTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
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

    private final ArrayListMultimap<String, Integer> optionIndexes = ArrayListMultimap.create();

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

        // Perform swaps
        idx = 0;
        for (MenuEntry entry : menuEntries)
        {
            swapMenuEntry(idx++, entry);
        }

    }

    private void swapMenuEntry(int index, MenuEntry menuEntry)
    {
        final String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
        final String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();

        if (config.itemList().length() == 0) {
            return;
        }

        String items[] = config.itemList().toLowerCase().split(",");

        for (String item : items)
        {
            // swap use with drop, only on items that aren't equipable.
            if (!isEquipment(target) && item.equals(target) && option.equals("use"))
            {
                swap("drop", option, target, true);
            }
        }
    }

    // returns true if the item is wieldable or wearable.
    private boolean isEquipment(String target)
    {
        MenuEntry[] entries = client.getMenuEntries();
        int indexOfWield = searchIndex(entries, "wield", target, true);
        int indexOfWear = searchIndex(entries, "wear", target, true);

        return indexOfWield >= 0 || indexOfWear >= 0;
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
}
