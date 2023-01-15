package com.slashswapper;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.eventbus.Subscribe;
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

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent)
	{
		if (!"chatDefaultReturn".equals(scriptCallbackEvent.getEventName()))
		{
			return;
		}

		final int[] intStack = client.getIntStack();
		int intStackCount = client.getIntStackSize();

		// intStack[intStackCount -1] is the check to see if a user typed / or // from non fc/cc selected channels
		// intStack[intStackCount - 2] is the ID to see what channel the message is routed into
		if (intStack[intStackCount - 1] == 1 && intStack[intStackCount - 2] == 9)
		{
			// message is going to friends chat because user typed /
			intStack[intStackCount - 2] = 41; // swap it to clan chat
		}
		else if (intStack[intStackCount - 1] == 2 && intStack[intStackCount - 2] == 41)
		{
			// message is going to clan chat because user typed //
			intStack[intStackCount - 2] = 9; // swap it to friends chat
		}
	}
}
