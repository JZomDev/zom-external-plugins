package com.zom.dense_essence;

import com.google.inject.Provides;
import java.awt.Color;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Dense Runecrafting"
)
public class DenseEssencePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private DenseEssenceConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private DenseRunestoneOverlay denseRunestoneOverlay;

	private static final int DENSE_RUNESTONE_SOUTH_ID = NullObjectID.NULL_10796;
	private static final int DENSE_RUNESTONE_NORTH_ID = NullObjectID.NULL_8981;

	@Getter
	private GameObject denseRunestoneSouth;

	@Getter
	private GameObject denseRunestoneNorth;

	@Getter
	private GameObject soulAltar;

	@Getter
	private GameObject bloodAltar;

	@Getter
	private Color clickboxBorderColorMinable;
	@Getter
	private Color clickboxFillColorMinable;
	@Getter
	private Color clickboxBorderHoverColorMinable;

	@Getter
	private Color clickboxBorderColorDepleted;
	@Getter
	private Color clickboxFillColorDepleted;
	@Getter
	private Color clickboxBorderHoverColorDepleted;
	@Getter
	private boolean hasDarkEssence;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(denseRunestoneOverlay);
		clickboxBorderColorMinable = config.showDenseRunestoneClickboxAvailable();
		clickboxFillColorMinable = new Color(clickboxBorderColorMinable.getRed(), clickboxBorderColorMinable.getGreen(), clickboxBorderColorMinable.getBlue(), 50);
		clickboxBorderHoverColorMinable = clickboxBorderColorMinable.darker();

		clickboxBorderColorDepleted = config.showDenseRunestoneClickboxUnavailable();
		clickboxFillColorDepleted = new Color(clickboxBorderColorDepleted.getRed(), clickboxBorderColorDepleted.getGreen(), clickboxBorderColorDepleted.getBlue(), 50);
		clickboxBorderHoverColorDepleted = clickboxBorderColorDepleted.darker();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("zomDenseEssence"))
		{
			clickboxBorderColorMinable = config.showDenseRunestoneClickboxAvailable();
			clickboxFillColorMinable = new Color(clickboxBorderColorMinable.getRed(), clickboxBorderColorMinable.getGreen(), clickboxBorderColorMinable.getBlue(), 50);
			clickboxBorderHoverColorMinable = clickboxBorderColorMinable.darker();

			clickboxBorderColorDepleted = config.showDenseRunestoneClickboxUnavailable();
			clickboxFillColorDepleted = new Color(clickboxBorderColorDepleted.getRed(), clickboxBorderColorDepleted.getGreen(), clickboxBorderColorDepleted.getBlue(), 50);
			clickboxBorderHoverColorDepleted = clickboxBorderColorDepleted.darker();
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(denseRunestoneOverlay);
		denseRunestoneNorth = null;
		denseRunestoneSouth = null;
		hasDarkEssence = false;

		clickboxFillColorMinable = null;
		clickboxBorderColorMinable = null;
		clickboxBorderHoverColorMinable = null;
		clickboxBorderColorDepleted = null;
		clickboxFillColorDepleted = null;
		clickboxBorderHoverColorDepleted = null;

	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		GameState gameState = event.getGameState();
		switch (gameState)
		{
			case LOGGED_IN:
				hasDarkEssence = checkContainer(client.getItemContainer(InventoryID.INVENTORY));
			case LOADING:
				denseRunestoneNorth = null;
				denseRunestoneSouth = null;
				break;
			case CONNECTION_LOST:
			case HOPPING:
			case LOGIN_SCREEN:
				break;
		}
	}

	@Provides
	DenseEssenceConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DenseEssenceConfig.class);
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject obj = event.getGameObject();
		int id = obj.getId();

		switch (id)
		{
			case DENSE_RUNESTONE_SOUTH_ID:
				denseRunestoneSouth = obj;
				break;
			case DENSE_RUNESTONE_NORTH_ID:
				denseRunestoneNorth = obj;
				break;
			case ObjectID.SOUL_ALTAR:
				soulAltar = obj;
				break;
			case ObjectID.BLOOD_ALTAR:
				bloodAltar = obj;
				break;
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		switch (event.getGameObject().getId())
		{
			case DENSE_RUNESTONE_SOUTH_ID:
				denseRunestoneSouth = null;
				break;
			case DENSE_RUNESTONE_NORTH_ID:
				denseRunestoneNorth = null;
				break;
			case ObjectID.SOUL_ALTAR:
				soulAltar = null;
				break;
			case ObjectID.BLOOD_ALTAR:
				bloodAltar = null;
				break;
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (!config.highlightAltarClickbox())
		{
			return;
		}
		ItemContainer container = event.getItemContainer();

		if (container == client.getItemContainer(InventoryID.INVENTORY))
		{
			hasDarkEssence = checkContainer(container);
		}
	}

	private boolean checkContainer(ItemContainer inventory)
	{
		if (!config.highlightAltarClickbox())
		{
			return false;
		}

		return inventory.contains(ItemID.DARK_ESSENCE_FRAGMENTS) || inventory.contains(ItemID.DARK_ESSENCE_BLOCK);
	}
}
