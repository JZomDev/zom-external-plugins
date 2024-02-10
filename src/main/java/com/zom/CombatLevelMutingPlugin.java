package com.zom;

import com.google.inject.Provides;
import java.util.HashMap;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.Player;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Combat Level Muting",
	tags = "mute,chat,filter,noob,annoy,spam,bot"
)
public class CombatLevelMutingPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private CombatLevelMutingConfig config;

	HashMap<String, Integer> playerToLevelMap;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Combat Level Muting started");
		playerToLevelMap = new HashMap<>();
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Combat Level Muting stopped");
		playerToLevelMap = new HashMap<>();
	}

	@Provides
	CombatLevelMutingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CombatLevelMutingConfig.class);
	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned event)
	{
		final Player local = client.getLocalPlayer();
		final Player player = event.getPlayer();

		if (player != local && player.getName() != null)
		{
			playerToLevelMap.put(getGoodName(player.getName()), player.getCombatLevel());
		}
	}

	@Subscribe(priority = -1)
	public void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		if (config.combatLevelHidingType() == CombatLevelMutingType.OVERHEAD)
		{
			return;
		}

		if (!"chatFilterCheck".equals(event.getEventName()))
		{
			return;
		}

		int[] intStack = client.getIntStack();
		int intStackSize = client.getIntStackSize();

		final int messageType = intStack[intStackSize - 2];
		final int messageId = intStack[intStackSize - 1];

		ChatMessageType chatMessageType = ChatMessageType.of(messageType);
		final MessageNode messageNode = client.getMessages().get(messageId);
		final String playerName = getGoodName(messageNode.getName());
		boolean blockMessage = false;

		// Only filter public chat
		switch (chatMessageType)
		{
			case PUBLICCHAT:
			case AUTOTYPER:
			case MODCHAT:
				if (shouldFilterPlayerMessage(playerName))
				{
					blockMessage = true;
				}
		}

		if (blockMessage)
		{
			// Block the message
			intStack[intStackSize - 3] = 0;
		}
	}

	@Subscribe(priority = -1)
	public void onOverheadTextChanged(OverheadTextChanged event)
	{
		if (config.combatLevelHidingType() == CombatLevelMutingType.CHATBOX)
		{
			return;
		}

		if (!(event.getActor() instanceof Player) || event.getActor().getName() == null)
		{
			return;
		}

		String playerName = getGoodName(event.getActor().getName());
		if (shouldFilterPlayerMessage(playerName))
		{
			event.getActor().setOverheadText(" ");
		}
	}

	private String getGoodName(String name)
	{
		return Text.removeTags(Text.toJagexName(name).toLowerCase());
	}

	private boolean shouldFilterPlayerMessage(String playerName)
	{
		return playerToLevelMap.containsKey(playerName) && playerToLevelMap.get(playerName) <= config.combatLevelTextHide();
	}
}
