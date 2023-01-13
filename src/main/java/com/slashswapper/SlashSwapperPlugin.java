package com.slashswapper;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.clan.ClanID;
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

	private static final int FRIENDS_CHANNEL = 4;
	private static final int CLAN_CHANNEL = 5;

	private static final int CLAN_CHAT = 3;
	private static final int FRIENDS_CHAT = 2;

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		if (event.getScriptId() != ScriptID.CHAT_SEND)
		{
			return;
		}

		if (client.getVarbitValue(4394) == 1)
		{
			return;
		}

		// I cannot test for group irons, so removing entirely for them
		if (client.getAccountType().equals(AccountType.GROUP_IRONMAN))
		{
			return;
		}

		// varbit13805 == 1 not chatting in clan or has no clan
		if (client.getVarbitValue(13805) == 1 || client.getClanChannel(ClanID.CLAN) == null)
		{
			return;
		}

		final String[] stringStack = client.getStringStack();
		final int[] intStack = client.getIntStack();
		int stringStackCount = client.getStringStackSize();
		int intStackCount = client.getIntStackSize();

		String msg = stringStack[stringStackCount - 1];
		if (msg.isEmpty())
		{
			return;
		}

		int channelSelected = client.getVarcIntValue(41); // Selected channel button
		// 0 = all, 1 = game, 2 = public, 3 = private, 4 = channel, 5 = clan, 6 = trade
		int target = intStack[intStackCount - 4]; // 3 is CLAN CHAT, 2 is FRIENDS CHAT

		if (target == CLAN_CHAT && channelSelected != CLAN_CHANNEL)
		{
			intStack[intStackCount - 4] = 2;
			intStack[intStackCount - 3] = -1;
		}
		else if (target == FRIENDS_CHAT && channelSelected != FRIENDS_CHANNEL)
		{
			intStack[intStackCount - 4] = 3;
			intStack[intStackCount - 3] = 0;
		}
	}
}
