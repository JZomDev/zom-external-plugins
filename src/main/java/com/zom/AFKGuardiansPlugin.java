package com.zom;

import com.google.common.collect.ImmutableSet;
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
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
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

	private static final Set<Integer> GUARDIAN_IDS = ImmutableSet.of(43704, 43707, 43708);
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

	// point globals
	private int currentElementalRewardPoints;
	private int currentCatalyticRewardPoints;

	// configured items
	private boolean hasBeenNotified;
	private Instant stopAFK;
	private InfoBox goodToAFKInfoBox;

	@Override
	protected void startUp() throws Exception
	{
		hasBeenNotified = false;
		activeGuardians.clear();
		guardians.clear();
	}

	@Override
	protected void shutDown() throws Exception
	{
		hasBeenNotified = false;
		activeGuardians.clear();
		guardians.clear();
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

		activeGuardians.removeIf(ag -> {
			Animation anim = ((DynamicObject) ag.getRenderable()).getAnimation();
			return anim == null || anim.getId() != GUARDIAN_ACTIVE_ANIM;
		});

		for (GameObject guardian : guardians)
		{
			Animation animation = ((DynamicObject) guardian.getRenderable()).getAnimation();
			if (animation != null && animation.getId() == GUARDIAN_ACTIVE_ANIM)
			{
				activeGuardians.add(guardian);
			}
		}

		if (stopAFK != null && 1350 > ChronoUnit.MILLIS.between(Instant.now(), stopAFK))
		{
			notifier.notify("Stop afking! Time to to make Guardian Essence!");
			stopAFK = null;
			hasBeenNotified = false;
		}

		if (config.alertOnRed() && activeGuardians.size() > 0 && !hasBeenNotified)
		{
			notifier.notify("Go craft runes at an available RED altar!");
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
			stopAFK = Instant.now().plusSeconds(config.timeWasting());
			currentElementalRewardPoints = 0;
			currentCatalyticRewardPoints = 0;
			if (config.enableInfoBox())
			{
				createInfoBox();
			}
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (!checkInMainRegion()) return;
		currentElementalRewardPoints = client.getVarbitValue(13686);
		currentCatalyticRewardPoints = client.getVarbitValue(13685);

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
		if (GUARDIAN_IDS.contains(event.getGameObject().getId()))
		{
			guardians.removeIf(g -> g.getId() == gameObject.getId());
			activeGuardians.removeIf(g -> g.getId() == gameObject.getId());
			guardians.add(gameObject);
		}
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

	@Provides
	AFKGuardiansConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AFKGuardiansConfig.class);
	}
}
