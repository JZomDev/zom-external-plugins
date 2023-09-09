package com.zom.ignore;

import com.google.inject.Provides;
import static com.zom.ignore.IgnoreListAlerterConfig.GROUP;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
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

	@Inject
	private IgnoreListAlerterConfig config;

	int delay = -1;
	HashMap<String, Instant> delayList;

	@Override
	protected void startUp() throws Exception
	{
		delay = config.alertTimeOut();
		delayList = new HashMap<>();
	}

	@Override
	protected void shutDown() throws Exception
	{
		delay = -1;
		delayList = null;
	}

	@Provides
	public IgnoreListAlerterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(IgnoreListAlerterConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(GROUP))
		{
			delay = config.alertTimeOut();
		}
	}

	@Nullable
	private String getFriendNote(String displayName)
	{
		return configManager.getConfiguration("friendNotes", "note_" + displayName);
	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned playerSpawned)
	{
		String name = playerSpawned.getPlayer().getName();

		if (client.getIgnoreContainer().findByName(name) != null)
		{
			 Instant instant = delayList.getOrDefault(name, Instant.now().minus(1L, ChronoUnit.SECONDS));

			 if (instant.isBefore(Instant.now()))
			 {
			 	String note = getFriendNote(name);
			 	if (note != null)
			 	{
			 		delayList.put(name, Instant.now().plusSeconds(delay * 60L));
			 		alertPlayerWarning(name, note);
			 	}
			 }
		}
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