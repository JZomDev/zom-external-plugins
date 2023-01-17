package com.zom;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
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
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "AFK GOTR"
)
public class AFKGuardiansPlugin extends Plugin
{
	@Inject
	private Client client;

	private static final Set<Integer> GUARDIAN_IDS = ImmutableSet.of(43704, 43707, 43708);
	private static final int MINIGAME_MAIN_REGION = 14484;
	private static final int PARENT_WIDGET_ID = 48889857;
	private static final int GUARDIAN_ACTIVE_ANIM = 9363;
	private boolean hasBeenNotified;
	private Instant stopAFK;

	private final Set<GameObject> activeGuardians = new HashSet<>();
	private final Set<GameObject> guardians = new HashSet<>();

	public static final String CONFIG_GROUP = "zomafkgotr";

	@Inject
	private AFKGuardiansConfig config;

	@Inject
	private Notifier notifier;

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
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (!checkInMinigame()) return;

		activeGuardians.removeIf(ag -> {
			Animation anim = ((DynamicObject)ag.getRenderable()).getAnimation();
			return anim == null || anim.getId() != GUARDIAN_ACTIVE_ANIM;
		});

		for(GameObject guardian : guardians){
			Animation animation = ((DynamicObject) guardian.getRenderable()).getAnimation();
			if(animation != null && animation.getId() == GUARDIAN_ACTIVE_ANIM) {
				activeGuardians.add(guardian);
			}
		}

		if (stopAFK != null && 1350 > ChronoUnit.MILLIS.between(Instant.now(), stopAFK)) {
			notifier.notify("Stop afking! Time to to make Guardian Essence!");
			stopAFK = null;
			hasBeenNotified = false;
		}

		if (config.alertOnRed() && activeGuardians.size() > 0 && !hasBeenNotified) {
			notifier.notify("Go craft runes at an available RED altar!");
			hasBeenNotified = true;
		}
	}


	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		String msg = chatMessage.getMessage().toLowerCase();
		if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE) return;

		if (msg.contains("creatures from the abyss will attack in 120 seconds"))
		{
			notifier.notify("Start mining!");
			stopAFK = Instant.now().plusSeconds(config.timeWasting());
		}
	}

	private boolean checkInMainRegion()
	{
		int[] currentMapRegions = client.getMapRegions();
		return Arrays.stream(currentMapRegions).anyMatch(x -> x == MINIGAME_MAIN_REGION);
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

	private boolean checkInMinigame() {
		GameState gameState = client.getGameState();
		if (gameState != GameState.LOGGED_IN
			&& gameState != GameState.LOADING)
		{
			return false;
		}

		Widget elementalRuneWidget = client.getWidget(PARENT_WIDGET_ID);
		return elementalRuneWidget != null;
	}


	@Provides
	AFKGuardiansConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AFKGuardiansConfig.class);
	}
}
