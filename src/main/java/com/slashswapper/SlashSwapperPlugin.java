package com.slashswapper;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.VarClientStr;
import net.runelite.api.clan.ClanID;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.vars.AccountType;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
	name = "Slash Swapper"
)
public class SlashSwapperPlugin extends Plugin
{
	@Inject
	private Client client;

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent)
	{
		if (!"chatDefaultReturn".equals(scriptCallbackEvent.getEventName()))
		{
			return;
		}

		final int[] intStack = client.getIntStack();
		int intStackCount = client.getIntStackSize();

		if (intStack[intStackCount-1] == 1 && intStack[intStackCount-2] == 9) { // message is going to friends chat
			intStack[intStackCount-2] = 41;
		}else if (intStack[intStackCount-1] == 2 && intStack[intStackCount-2] == 41) { // message is going to friends chat
			intStack[intStackCount-2] = 9;
		}
	}
}
