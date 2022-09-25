package com.zom.ignore;

import java.util.List;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Nameable;
import net.runelite.api.NameableContainer;
import net.runelite.api.Player;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Ignore List Alerter"
)
public class IgnoreListAlerterPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	private int tickCounter;

	@Override
	protected void startUp() throws Exception
	{
		tickCounter = 0;
	}

	@Override
	protected void shutDown() throws Exception
	{
		tickCounter = 0;
	}

	@Nullable
	private String getFriendNote(String displayName)
	{
		return configManager.getConfiguration("friendNotes", "note_" + displayName);
	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned playerSpawned)
	{
		if (tickCounter > 1)
		{
			String name = playerSpawned.getPlayer().getName();

			NameableContainer nc = client.getIgnoreContainer();

			if (client.getIgnoreContainer().findByName(name) != null)
			{
				String note = getFriendNote(name);
				if (note != null)
				{
					alertPlayerWarning(name, note);
				}
			}
		}
	}


	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			tickCounter = 0;
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		tickCounter++;
		if (tickCounter == 2)
		{
			NameableContainer nc = client.getIgnoreContainer();

			for (Nameable ig : nc.getMembers())
			{
				String name = ig.getName();

				Player targetPlayer = findPlayer(name);
				if (targetPlayer == null)
				{
					return;
				}

				String note = getFriendNote(name);
				if (note != null)
				{
					alertPlayerWarning(name, note);
				}
			}
		}
	}

	private Player findPlayer(String name)
	{
		for (Player player : client.getPlayers())
		{
			if (player.getName().equals(name))
			{
				return player;
			}
		}
		return null;
	}


	private void alertPlayerWarning(String rsn, String note)
	{
		ChatMessageBuilder response = new ChatMessageBuilder();
		response.append("Nearby Player ")
			.append(ChatColorType.HIGHLIGHT)
			.append(rsn)
			.append(ChatColorType.NORMAL)
			.append(" is on ignore list for ")
			.append(ChatColorType.HIGHLIGHT)
			.append(note);

		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.CONSOLE)
			.runeLiteFormattedMessage(response.build())
			.build());
	}
}
