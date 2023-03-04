package com.zom;

import com.google.inject.Provides;
import java.awt.Color;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Animation;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.DynamicObject;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@Slf4j
@PluginDescriptor(
	name = "AFK GOTR"
)
public class AFKGuardiansPlugin extends Plugin
{
	public static final String CONFIG_GROUP = "zomafkgotr";

	@Inject
	private Client client;

	@Inject
	private AFKGuardiansConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	// white
	public static final int AIR = 43701;
	public static final int MIND = 43705;
	public static final int BODY = 43709;

	// blue
	public static final int WATER = 43702;
	public static final int COSMIC = 43710;
	public static final int CHAOS = 43706;

	// green
	public static final int EARTH = 43703;
	public static final int LAW = 43712;
	public static final int NATURE = 43711;

	// red
	public static final int FIRE = 43704;
	public static final int DEATH = 43707;
	public static final int BLOOD = 43708;

	private Set<AFKAlertTier> currentAlerts;

	private final Set<GameObject> activeGuardians = new HashSet<>();
	private final Set<GameObject> guardians = new HashSet<>();

	private static final int GUARDIAN_ACTIVE_ANIM = 9363;

	int[] altars = {
		10571, // Earth
		10315, // Fire
		10827, // Water
		11339, // Air
		10059, // Body
		11083, // Mind
		8523,  // Cosmic
		9035,  // Chaos
		9803,  // Law
		9547,  // Nature
		8779,  // Death
		9291, // Wrath
		8508, // Astral
		12875 // Blood
	};

	int[] guardiansArr = {
		AIR,
		MIND,
		BODY,
		WATER,
		LAW,
		NATURE,
		CHAOS,
		DEATH,
		BLOOD,
		EARTH,
		FIRE,
		COSMIC
	};

	// point globals
	private int currentElementalRewardPoints;
	private int currentCatalyticRewardPoints;

	// configured items
	private boolean hasBeenNotified;
	private Instant stopAFK;
	private InfoBox goodToAFKInfoBox;
	private boolean alwaysNotify;
	private int notifiyPercent;
	private boolean alertWithCell;

	private Item[] inv;
	private boolean postAFK;

	@Override
	protected void startUp() throws Exception
	{
		hasBeenNotified = false;
		guardians.clear();
		settings();

		postAFK = false;
		inv = new Item[]{};
	}

	@Override
	protected void shutDown() throws Exception
	{
		hasBeenNotified = false;
		guardians.clear();
		settings();

		postAFK = false;
		disableInfoBox();
		inv = new Item[]{};
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN)
		{
			disableInfoBox();
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		final ItemContainer container = event.getItemContainer();

		if (container != client.getItemContainer(InventoryID.INVENTORY))
		{
			return;
		}

		if (!checkInMainRegion())
		{
			return;
		}
		if (!checkInMinigame())
		{
			return;
		}

		inv = container.getItems();
	}

	private boolean hasGuardianStone()
	{
		for (Item item: inv)
		{
			if (item.getId() == ItemID.CATALYTIC_GUARDIAN_STONE
				|| item.getId() == ItemID.ELEMENTAL_GUARDIAN_STONE
				|| item.getId() == ItemID.POLYELEMENTAL_GUARDIAN_STONE) {
				return true;
			}
		}
		return false;
	}

	private boolean hasCell()
	{
		for (Item item: inv)
		{
			if (item.getId() == ItemID.WEAK_CELL
				|| item.getId() == ItemID.MEDIUM_CELL
				|| item.getId() == ItemID.STRONG_CELL
				|| item.getId() == ItemID.OVERCHARGED_CELL) {
				return true;
			}
		}
		return false;
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (!checkInMainRegion())
		{
			return;
		}
		if (!checkInMinigame())
		{
			return;
		}

		activeGuardians.removeIf(ag -> {
			Animation anim = ((DynamicObject) ag.getRenderable()).getAnimation();
			return anim == null || anim.getId() != GUARDIAN_ACTIVE_ANIM;
		});

		for (GameObject guardian : guardians)
		{
			Animation animation = ((DynamicObject) guardian.getRenderable()).getAnimation();
			if (animation != null && animation.getId() == GUARDIAN_ACTIVE_ANIM)
			{
				for (AFKAlertTier afk : currentAlerts)
				{
					for (int i = 0; i < afk.getTier().size(); i++)
					{
						if (guardian.getId() == afk.getTier().get(i)) {
							activeGuardians.removeIf(g -> g.getId() == guardian.getId());
							activeGuardians.add(guardian);
						}
					}
				}
			}
		}

		// allow for second notification
		if (activeGuardians.size() == 0 && getSum() < 150 && alwaysNotify)
		{
			hasBeenNotified = false;
		}

		if (stopAFK != null &&  Instant.now().compareTo(stopAFK) >= 0)
		{
			notifier.notify("Stop afking! Time to to make Guardian Essence!");
			stopAFK = null;
			hasBeenNotified = false;
			postAFK = true;
		}

		if (getGamePercent() == notifiyPercent && hasBeenNotified)
		{
			hasBeenNotified = false;
			return;
		}

		// if at altar just clear the active guardian list
		if (atAltar())
		{
			activeGuardians.clear();
			return;
		}

		if (activeGuardians.size() > 0 && !hasBeenNotified && getSum() < 150 && (alertWithCell && !hasCell()) && !hasGuardianStone() && postAFK)
		{
			notifier.notify("Go craft runes at available altar!");
			hasBeenNotified = true;
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE) return;
		if (!checkInMainRegion()) return;

		String msg = chatMessage.getMessage().toLowerCase();

		if (msg.contains("creatures from the abyss will attack in 120 seconds"))
		{
			if (config.notifyMining()) notifier.notify("Start mining!");
			stopAFK = config.timeWasting() == 0 ? null : Instant.now().plusSeconds(config.timeWasting());
			postAFK = config.timeWasting() == 0;
			currentElementalRewardPoints = 0;
			currentCatalyticRewardPoints = 0;
			if (config.enableInfoBox())
			{
				createInfoBox();
			}
		}

		if (msg.contains("the great guardian was defeated"))
		{
			stopAFK = null;
			currentElementalRewardPoints = 0;
			currentCatalyticRewardPoints = 0;
			disableInfoBox();
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (!checkInMainRegion()) return;
		currentElementalRewardPoints = client.getVarbitValue(13686);
		currentCatalyticRewardPoints = client.getVarbitValue(13685);

		if (getSum() < 150 && alwaysNotify)
		{
			hasBeenNotified = false;
		}

		if (getSum() >= 150 && config.hideInfoBox())
		{
			disableInfoBox();
		}
	}

	private int getSum()
	{
		return currentElementalRewardPoints + currentCatalyticRewardPoints;
	}

	private void createInfoBox()
	{
		if (goodToAFKInfoBox == null)
		{
			goodToAFKInfoBox = new InfoBox(itemManager.getImage(ItemID.ABYSSAL_LANTERN), this)
			{
				@Override
				public String getText()
				{
					return getSum() + "/150";
				}

				@Override
				public Color getTextColor()
				{
					return getSum() < 150 ? Color.RED : Color.GREEN;
				}
			};
			infoBoxManager.addInfoBox(goodToAFKInfoBox);
		}
	}

	private void disableInfoBox()
	{
		infoBoxManager.removeInfoBox(goodToAFKInfoBox);
		goodToAFKInfoBox = null;
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject gameObject = event.getGameObject();

		for (int id : guardiansArr)
		{
			if (gameObject.getId() == id)
			{
				guardians.removeIf(g -> g.getId() == gameObject.getId());
				guardians.add(gameObject);
			}
		}
	}

	private boolean atAltar()
	{
		WorldPoint playerLoc = client.getLocalPlayer().getWorldLocation();
		for (int altarRegion : altars)
		{
			if (altarRegion == playerLoc.getRegionID())
			{
				return true;
			}
		}
		return false;
	}

	private boolean checkInMainRegion()
	{
		WorldPoint playerLoc = client.getLocalPlayer().getWorldLocation();
		for (int altarRegion : altars)
		{
			if (altarRegion == playerLoc.getRegionID())
			{
				return true;
			}
		}

		int[] currentMapRegions = client.getMapRegions();
		return Arrays.stream(currentMapRegions).anyMatch(x -> x == 14484);
	}

	private boolean checkInMinigame() {
		GameState gameState = client.getGameState();
		if (gameState != GameState.LOGGED_IN
			&& gameState != GameState.LOADING)
		{
			return false;
		}

		Widget elementalRuneWidget = client.getWidget(48889857);
		return elementalRuneWidget != null;
	}

	private int getGamePercent()
	{
		try
		{
			Widget elementalRuneWidget = client.getWidget(48889874);
			if (elementalRuneWidget != null)
			{
				log.debug("Current Guardian Power: {}", Integer.valueOf(elementalRuneWidget.getText().replaceAll("[^0-9]", "")));
				return Integer.valueOf(elementalRuneWidget.getText().replaceAll("[^0-9]", ""));
			}
		} catch (Exception e) {
			return -1;
		}
		return -1;
	}

	@Provides
	AFKGuardiansConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AFKGuardiansConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(CONFIG_GROUP))
		{
			settings();
		}
	}

	private void settings()
	{
		alertWithCell = config.alertWithCell();
		activeGuardians.clear();
		currentAlerts = config.alertOnRed();
		alwaysNotify = config.additionalNotify();
		notifiyPercent = config.additionalPercent();
	}
}
