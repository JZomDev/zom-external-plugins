package com.zom.dense_essence;

import com.google.inject.Provides;
import java.awt.Color;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
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

	@Getter(AccessLevel.PACKAGE)
	private GameObject denseRunestoneSouth;

	@Getter(AccessLevel.PACKAGE)
	private GameObject denseRunestoneNorth;

	@Getter(AccessLevel.PACKAGE)
	private LocalPoint localPointRunestoneSouth;

	@Getter(AccessLevel.PACKAGE)
	private LocalPoint localPointRunestoneNorth;

	@Getter(AccessLevel.PACKAGE)
	private Color CLICKBOX_BORDER_COLOR_MINABLE;
	@Getter(AccessLevel.PACKAGE)
	private Color CLICKBOX_FILL_COLOR_MINABLE;
	@Getter(AccessLevel.PACKAGE)
	private Color CLICKBOX_BORDER_HOVER_COLOR_MINABLE;

	@Getter(AccessLevel.PACKAGE)
	private Color CLICKBOX_BORDER_COLOR_DEPLETED;
	@Getter(AccessLevel.PACKAGE)
	private Color CLICKBOX_FILL_COLOR_DEPLETED;
	@Getter(AccessLevel.PACKAGE)
	private Color CLICKBOX_BORDER_HOVER_COLOR_DEPLETED;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(denseRunestoneOverlay);
		CLICKBOX_BORDER_COLOR_MINABLE = config.showDenseRunestoneClickboxAvailable();
		CLICKBOX_FILL_COLOR_MINABLE = new Color(CLICKBOX_BORDER_COLOR_MINABLE.getRed(), CLICKBOX_BORDER_COLOR_MINABLE.getGreen(),
			CLICKBOX_BORDER_COLOR_MINABLE.getBlue(), 50);
		CLICKBOX_BORDER_HOVER_COLOR_MINABLE = CLICKBOX_BORDER_COLOR_MINABLE.darker();

		CLICKBOX_BORDER_COLOR_DEPLETED = config.showDenseRunestoneClickboxUnavailable();
		CLICKBOX_FILL_COLOR_DEPLETED = new Color(
			CLICKBOX_BORDER_COLOR_DEPLETED.getRed(),
			CLICKBOX_BORDER_COLOR_DEPLETED.getGreen(),
			CLICKBOX_BORDER_COLOR_DEPLETED.getBlue(), 50);
		CLICKBOX_BORDER_HOVER_COLOR_DEPLETED = CLICKBOX_BORDER_COLOR_DEPLETED.darker();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("zomDenseEssence"))
		{
			CLICKBOX_BORDER_COLOR_MINABLE = config.showDenseRunestoneClickboxAvailable();
			CLICKBOX_FILL_COLOR_MINABLE = new Color(CLICKBOX_BORDER_COLOR_MINABLE.getRed(), CLICKBOX_BORDER_COLOR_MINABLE.getGreen(),
				CLICKBOX_BORDER_COLOR_MINABLE.getBlue(), 50);
			CLICKBOX_BORDER_HOVER_COLOR_MINABLE = CLICKBOX_BORDER_COLOR_MINABLE.darker();

			CLICKBOX_BORDER_COLOR_DEPLETED = config.showDenseRunestoneClickboxUnavailable();
			CLICKBOX_FILL_COLOR_DEPLETED = new Color(
				CLICKBOX_BORDER_COLOR_DEPLETED.getRed(),
				CLICKBOX_BORDER_COLOR_DEPLETED.getGreen(),
				CLICKBOX_BORDER_COLOR_DEPLETED.getBlue(), 50);
			CLICKBOX_BORDER_HOVER_COLOR_DEPLETED = CLICKBOX_BORDER_COLOR_DEPLETED.darker();
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(denseRunestoneOverlay);
		denseRunestoneNorth = null;
		denseRunestoneSouth = null;
		CLICKBOX_FILL_COLOR_MINABLE = new Color(
			CLICKBOX_BORDER_COLOR_MINABLE.getRed(), CLICKBOX_BORDER_COLOR_MINABLE.getGreen(),
			CLICKBOX_BORDER_COLOR_MINABLE.getBlue(), 50);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		GameState gameState = event.getGameState();
		switch (gameState)
		{
			case LOADING:
				denseRunestoneNorth = null;
				denseRunestoneSouth = null;
				localPointRunestoneNorth = null;
				localPointRunestoneSouth = null;
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
				localPointRunestoneSouth = event.getGameObject().getLocalLocation();
				break;
			case DENSE_RUNESTONE_NORTH_ID:
				denseRunestoneNorth = obj;
				localPointRunestoneNorth = event.getGameObject().getLocalLocation();
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
		}
	}
}
