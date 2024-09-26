package com.zom.bananapicker;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@Slf4j
@PluginDescriptor(
	name = "Banana Picker"
)
public class BananaPickerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private BananaPickerConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	int bananasPicked = 0;
	private BananaCounter counterBox;


	@Override
	protected void startUp() throws Exception
	{
		bananasPicked = config.bananaCount();
		updateInfobox(bananasPicked);
	}

	@Override
	protected void shutDown() throws Exception
	{
		infoBoxManager.removeInfoBox(counterBox);
		counterBox = null;
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.SPAM && chatMessage.getMessage().equals("You pick a banana."))
		{
			bananasPicked++;

			configManager.setConfiguration(BananaPickerConfig.CONFIG_GROUP, "bananaCount", bananasPicked);
			updateInfobox(bananasPicked);
		}
	}

	@Provides
	BananaPickerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BananaPickerConfig.class);
	}

	private void updateInfobox(final int bananasPicked)
	{
		removeInfobox();
		final BufferedImage image = itemManager.getImage(ItemID.BANANA, 1, false);
		counterBox = new BananaCounter(this, ItemID.BANANA, bananasPicked, "banana", image);
		infoBoxManager.addInfoBox(counterBox);
	}

	private void removeInfobox()
	{
		infoBoxManager.removeInfoBox(counterBox);
		counterBox = null;
	}
}
