package com.zom.molemanmode;

import com.google.inject.Provides;
import java.util.HashSet;
import java.util.List;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Moleman Mode"
)
public class MolemanModePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private MolemanModeConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private MolemanModeOverlay overlay;

	private final HashSet<Integer> tutorialIslandRegionIds = new HashSet<Integer>();
	private final HashSet<Integer> whiteListRegionIds = new HashSet<Integer>();
	private final HashSet<Integer> blackListRegionIds = new HashSet<Integer>();

	@Getter
	private long timeAvailable;
	private String ticksSpentAboveGroundStr;
	@Getter
	private long ticksSpentAboveGround;
	@Getter
	private int warningCount;
	@Getter
	private boolean aboveGround;
	@Getter
	private boolean formatTicksAsTime;
	@Getter
	private boolean showRegion;
	@Getter
	private boolean showAboveGroundState;

	private int xpUntilNextThreshold;
	private boolean prevState;
	private boolean enabled;
	private WorldPoint lastPlayerLocation;


	@Override
	protected void startUp() throws Exception
	{
		// tutorial island
		whiteListRegionIds.add(12079);
		whiteListRegionIds.add(12080);
		whiteListRegionIds.add(12335);
		whiteListRegionIds.add(12336);
		whiteListRegionIds.add(12592);

		enabled = config.manualToggle();
		warningCount = config.timeWarningThreshold();
		formatTicksAsTime = config.formatTimer();
		showRegion = config.showRegion();
		showAboveGroundState = config.showAboveGround();

		aboveGround = false;
		prevState = false;

		updateWhitelist();
		updateBlackList();

		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		enabled = false;
		aboveGround = false;
		prevState = false;
		warningCount = -1;
		formatTicksAsTime = false;
		showRegion = false;
		showAboveGroundState = false;
		whiteListRegionIds.clear();
		blackListRegionIds.clear();
		overlayManager.remove(overlay);
		lastPlayerLocation = null;
	}

	@Provides
	MolemanModeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MolemanModeConfig.class);
	}

	@Subscribe
	public void onGameTick(GameTick gameTick) {
		if (client.getGameState() != GameState.LOGGED_IN) return;
		if (client.getLocalPlayer() == null) return;

		evaluateIfMoleMan();

		if (aboveGround) {
			ticksSpentAboveGround++;
		}

		updateTime();
	}

	boolean loggingIn;

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		switch (event.getGameState())
		{
			case LOGIN_SCREEN:
				aboveGround = false;
				if (lastPlayerLocation != null)
				{
					saveConfig();
				}
				ticksSpentAboveGround = 0;
				lastPlayerLocation = null;
				break;
			case LOGGING_IN:
				loggingIn = true;
				break;
			case LOGGED_IN:
				if (loggingIn)
				{
					loggingIn = false;
					onLogin();
				}
				break;
		}
	}

	private void onLogin()
	{
		loadConfig();
		WorldPoint newLocation = client.getLocalPlayer().getWorldLocation();
		assert newLocation != null;

		if (lastPlayerLocation == null) {
			lastPlayerLocation = newLocation;
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {

		if (MolemanModeConfig.MOLEMAN_MODE_CONFIGGROUP.equals(event.getGroup()))
		{
			String key = event.getKey();
			switch (key)
			{
				case "manualToggle":
					enabled = config.manualToggle();
				case "whiteList":
					updateWhitelist();
					break;
				case "blackList":
					updateBlackList();
					break;
				case "showRegion":
					showRegion = config.showRegion();
					break;
				case "formatTime":
					formatTicksAsTime = config.formatTimer();
					break;
				case "showAboveGround":
					showAboveGroundState = config.showAboveGround();
					break;
				case "timeWarning":
					warningCount = config.timeWarningThreshold();
					break;
			}
		}
	}

	private void saveConfig()
	{
		ticksSpentAboveGroundStr = String.valueOf(ticksSpentAboveGround);
		configManager.setConfiguration(MolemanModeConfig.MOLEMAN_MODE_CONFIGGROUP, MolemanModeConfig.spentTicks, ticksSpentAboveGroundStr);
	}

	private void loadConfig()
	{
		ticksSpentAboveGroundStr = configManager.getConfiguration(MolemanModeConfig.MOLEMAN_MODE_CONFIGGROUP, MolemanModeConfig.spentTicks);
		if (ticksSpentAboveGroundStr == null) {
			ticksSpentAboveGround = 0;
			log.info("Failed to load ticks above. Setting it to 0.");

		} else {
			ticksSpentAboveGround = Long.parseLong(ticksSpentAboveGroundStr);
			log.info("Loaded ticks spent above. Setting it to {}", ticksSpentAboveGroundStr);

		}
	}

	public void updateTime() {
		// start with this offset, we always get this
		int earnedSeconds = config.bonusTime();
		earnedSeconds += config.timeEarnedPerThreshold() * (client.getOverallExperience() / config.xpThreshold());
		timeAvailable = earnedSeconds - ticksSpentAboveGround;
	}

	public void evaluateIfMoleMan() {
		final WorldPoint playerPos = client.getLocalPlayer().getWorldLocation();
		if (playerPos == null) return;
		final LocalPoint playerPosLocal = LocalPoint.fromWorld(client, playerPos);
		if (playerPosLocal == null) return;

		if (enabled)
		{
			aboveGround = false;
			return;
		}

		if (blackListRegionIds.contains(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			aboveGround = true;
			log.debug("We are manually" + (aboveGround ? " above ground." : " below ground.") );
			return;
		}

		if (whiteListRegionIds.contains(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			aboveGround = false;
			log.debug("We are manually" + (aboveGround ? " above ground." : " below ground.") );
			return;
		}

		if (tutorialIslandRegionIds.contains(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			aboveGround = false;
			log.debug("We are manually" + (aboveGround ? " above ground." : " below ground.") );
			return;
		}

		int cutOff = 4000;
		int currentY = playerPos.getY();

		boolean aboveCutoff = cutOff > currentY;

		aboveGround = aboveCutoff;

		boolean isEqual = aboveGround == prevState;

		if (!isEqual)  {
			log.debug("We are" + (aboveGround ? " above ground." : " below ground.") );
		}

		prevState = aboveGround;
	}


	private void updateWhitelist()
	{
		whiteListRegionIds.clear();
		List<String> text = Text.fromCSV(config.manualSafeAreas());
		for (String id: text)
		{
			whiteListRegionIds.add(Integer.valueOf(id));
		}
	}

	private void updateBlackList()
	{
		blackListRegionIds.clear();
		List<String> text = Text.fromCSV(config.manualCountDownAreas());
		for (String id: text)
		{
			blackListRegionIds.add(Integer.valueOf(id));
		}
	}

	private boolean tryParseInt(String intToTry) {
		try
		{
			Integer.parseInt(intToTry);
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
}
