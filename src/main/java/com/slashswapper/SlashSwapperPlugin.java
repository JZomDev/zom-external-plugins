package com.slashswapper;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Slash Swapper"
)
public class SlashSwapperPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SlashSwapperConfig config;

	private boolean swapGuestChat = false;

	@Provides
	SlashSwapperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SlashSwapperConfig.class);
	}

	@Override
	public void startUp()
	{
		swapGuestChat = config.slashGuestChat();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (configChanged.getGroup().equals("slashswapper"))
		{
			swapGuestChat = config.slashGuestChat();
		}
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent)
	{
		if (!"chatDefaultReturn".equals(scriptCallbackEvent.getEventName()))
		{
			return;
		}

		final int[] intStack = client.getIntStack();
		int intStackCount = client.getIntStackSize();

		// intStack[intStackCount -1] is the check to see if a user typed /,// or /// from non fc/cc/gc selected channels
		// intStack[intStackCount - 2] is the ID to see what channel the message is routed into

		if (intStack[intStackCount - 1] == 1 && intStack[intStackCount - 2] == 9)
		{
			// message is going to friends chat because user typed /
			intStack[intStackCount - 2] = !swapGuestChat ? 41 : 44; // swap it to clan chat or guest chat
		}
		else if (intStack[intStackCount - 1] == 2 && intStack[intStackCount - 2] == 41)
		{
			// message is going to clan chat because user typed //
			intStack[intStackCount - 2] = 9; // swap it to friends chat
		}
		else if (intStack[intStackCount - 1] == 3  && intStack[intStackCount - 2] == 44)
		{
			// message is going to guest chat because user typed ///
			intStack[intStackCount - 2] = !swapGuestChat ? 9 : 41; // swap it to friends chat or guest chat
		}
	}
}
