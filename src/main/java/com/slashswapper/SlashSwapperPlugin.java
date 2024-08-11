package com.slashswapper;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(name = "Slash Swapper")
public class SlashSwapperPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SlashSwapperConfig config;

	private boolean swapGuestChat = false;

	private static final int FRIEND_CHAT = 9;
	private static final int CLAN_OR_GIM_CHAT = 41;
	private static final int GUEST_CHAT = 44;

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

		// the check to see if a user typed /, // or /// from non fc/cc/gc selected channels
		int slashCount = intStack[intStackCount - 1];

		// the ID to see what channel the message is routed into
		int targetChannelCode = intStack[intStackCount - 2];

		// message is going to friends chat because user typed /, swap it to guest or clan chat
		if (slashCount == 1 && targetChannelCode == FRIEND_CHAT)
		{
			intStack[intStackCount - 2] = swapGuestChat ? GUEST_CHAT : CLAN_OR_GIM_CHAT;
		}

		// message is going to clan chat because user typed //, swap it to friends chat
		if (slashCount == 2 && targetChannelCode == CLAN_OR_GIM_CHAT)
		{
			intStack[intStackCount - 2] = FRIEND_CHAT;
		}

		// message is going to guest chat because user typed ///, swap it to clan chat
		if (slashCount == 3 && targetChannelCode == GUEST_CHAT && swapGuestChat)
		{
			intStack[intStackCount - 2] = CLAN_OR_GIM_CHAT;
		}
	}
}
