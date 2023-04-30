package com.zom;

import com.google.inject.Provides;
import java.awt.Color;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
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

	// configured items
	private boolean hasBeenNotified;
	private Instant stopAFK;
	private InfoBox goodToAFKInfoBox;
	private boolean alwaysNotify;
	private int notifiyPercent;
	private boolean alertWithCell;
	private Set<AFKAlertTier> currentAlerts;

	// globals
	private Item[] inv;
	private boolean postAFK;
	private final Set<GameObject> activeGuardians = new HashSet<>();
	private final Set<GameObject> guardians = new HashSet<>();
	private static final int GUARDIAN_ACTIVE_ANIM = 9363;

	// white tier guardians
	public static final int AIR_GUARDIAN = 43701, MIND_GUARDIAN = 43705, BODY_GUARDIAN = 43709;

	// blue tier guardians
	public static final int WATER_GUARDIAN = 43702, COSMIC_GUARDIAN = 43710, CHAOS_GUARDIAN = 43706;

	// green tier guardians
	public static final int EARTH_GUARDIAN = 43703, LAW_GUARDIAN = 43712, NATURE_GUARDIAN = 43711;

	// red tier guardians
	public static final int FIRE_GUARDIAN = 43704, DEATH_GUARDIAN = 43707, BLOOD_GUARDIAN = 43708;

	int[] guardiansArr = {
		AIR_GUARDIAN, MIND_GUARDIAN, BODY_GUARDIAN,
		WATER_GUARDIAN, COSMIC_GUARDIAN, CHAOS_GUARDIAN,
		EARTH_GUARDIAN, LAW_GUARDIAN, NATURE_GUARDIAN,
		FIRE_GUARDIAN, DEATH_GUARDIAN, BLOOD_GUARDIAN
	};

	// white altars
	private static final int AIR_ALTAR = 11339, MIND_ALTAR = 11083, BODY_ALTAR = 10059;
	// blue altars
	private static final int WATER_ALTAR = 10827, COSMIC_ALTAR = 8523, CHAOS_ALTAR = 9035;
	// green altars
	private static final int EARTH_ALTAR = 10571, LAW_ALTAR = 9803, NATURE_ALTAR = 9547;
	// red altars
	private static final int FIRE_ALTAR = 10315, DEATH_ALTAR = 8779, BLOOD_ALTAR = 12875;

	int[] altarsArr = {
		AIR_ALTAR, MIND_ALTAR, BODY_ALTAR,
		WATER_ALTAR, COSMIC_ALTAR, CHAOS_ALTAR,
		EARTH_ALTAR, LAW_ALTAR, NATURE_ALTAR,
		FIRE_ALTAR, DEATH_ALTAR, BLOOD_ALTAR
	};

	// points
	@Getter
	@Setter
	private int currentElementalRewardPoints, currentCatalyticRewardPoints;

	@Override
	protected void startUp() throws Exception
	{
		reset();
	}

	@Override
	protected void shutDown() throws Exception
	{
		reset();
		disableInfoBox();
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

		// remind user to play the game
		if (stopAFK != null &&  Instant.now().compareTo(stopAFK) >= 0)
		{
			notifier.notify("Stop afking! Time to to make Guardian Essence!");
			stopAFK = null;
			hasBeenNotified = false;
			postAFK = true;
		}

		// if game progress reaches target percent, sned another notification
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

		// send notification
		if (activeGuardians.size() > 0 && !hasBeenNotified && getSum() < 150 && !hasGuardianStone() && postAFK && (!hasCell() || alertWithCell))
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
			setCurrentElementalRewardPoints(0);
			setCurrentCatalyticRewardPoints(0);
			if (config.enableInfoBox())
			{
				createInfoBox();
			}
		}

		if (msg.contains("the great guardian was defeated"))
		{
			stopAFK = null;
			setCurrentElementalRewardPoints(0);
			setCurrentCatalyticRewardPoints(0);
			disableInfoBox();
		}
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

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (!checkInMainRegion()) return;

		setCurrentElementalRewardPoints(client.getVarbitValue(13686));
		setCurrentCatalyticRewardPoints(client.getVarbitValue(13685));

		if (getSum() < 150 && alwaysNotify)
		{
			hasBeenNotified = false;
		}

		if (getSum() >= 150 && config.hideInfoBox())
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

		if (!checkInMainRegion() || !checkInMinigame())
		{
			return;
		}

		inv = container.getItems();
	}

	// returns the sum of reward points
	private int getSum()
	{
		return getCurrentElementalRewardPoints() + getCurrentCatalyticRewardPoints();
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

	private boolean atAltar()
	{
		WorldPoint playerLoc = client.getLocalPlayer().getWorldLocation();
		for (int altarRegion : altarsArr)
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
		if (atAltar()) return true;
		return Arrays.stream(client.getMapRegions()).anyMatch(x -> x == 14484);
	}

	private boolean checkInMinigame() {
		GameState gameState = client.getGameState();
		if (gameState != GameState.LOGGED_IN && gameState != GameState.LOADING)
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

	private void reset()
	{
		hasBeenNotified = false;
		guardians.clear();
		settings();
		postAFK = false;
		inv = new Item[]{};
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
