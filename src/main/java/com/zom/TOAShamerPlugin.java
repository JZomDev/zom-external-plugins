package com.zom;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneScapeProfileType;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.OSType;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Toa Keris Shamer"
)
public class TOAShamerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private TOAShamerConfig config;

	@Inject
	private DrawManager drawManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TOAShamerOverlay screenshotOverlay;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private SpriteManager spriteManager;

	@Getter(AccessLevel.PACKAGE)
	private BufferedImage reportButton;

	@Inject
	private ClientUI clientUi;

	@Inject
	private Notifier notifier;

	private static final Set<Integer> REPORT_BUTTON_TLIS = ImmutableSet.of(
		WidgetID.FIXED_VIEWPORT_GROUP_ID,
		WidgetID.RESIZABLE_VIEWPORT_OLD_SCHOOL_BOX_GROUP_ID,
		WidgetID.RESIZABLE_VIEWPORT_BOTTOM_LINE_GROUP_ID);

	public static final File RUNELITE_DIR = new File(System.getProperty("user.home"), ".runelite");
	public static final File SCREENSHOT_DIR = new File(RUNELITE_DIR, "screenshots");

	public static final String SD_KERIS = "Keris";
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

	private int clientTickCounter;
	HashMap<String, Integer> playersKerised;
	boolean enabled;

	int[] raidRegions = new int[]{15698, 15700, 14162, 14164, 15186, 15188, 14674, 14676, 15184, 15696};

	int currentRegion;
	int previousRegion;

	@Override
	protected void startUp() throws Exception
	{
		currentRegion = -1;
		previousRegion = -1;
		enabled = false;
		clientTickCounter = 0;
		overlayManager.add(screenshotOverlay);
		playersKerised = new HashMap<>();
		spriteManager.getSpriteAsync(SpriteID.CHATBOX_REPORT_BUTTON, 0, s -> reportButton = s);
	}

	@Override
	protected void shutDown() throws Exception
	{
		currentRegion = -1;
		previousRegion = -1;
		enabled = false;
		clientTickCounter = 0;
		playersKerised = null;
		overlayManager.remove(screenshotOverlay);
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (client.getLocalPlayer() == null || event.getActor() != client.getLocalPlayer())
		{
			return;
		}

		if (!enabled)
		{
			return;
		}

		if (event.getActor().getAnimation() == 9546 && !playersKerised.containsKey(event.getActor().getName()))
		{
			playersKerised.put(event.getActor().getName(), clientTickCounter + 30);
		}
	}

	@Subscribe
	protected void onGameTick(GameTick t)
	{
		LocalPoint lp = client.getLocalPlayer().getLocalLocation();
		currentRegion = lp == null ? -1 : WorldPoint.fromLocalInstance(client, lp).getRegionID();

		// not in the raid
		if (Arrays.stream(raidRegions).noneMatch(region -> region == currentRegion))
		{
			enabled = false;
			return;
		}

		if (currentRegion != previousRegion)
		{
			switch (currentRegion)
			{
				case 15698:
					enabled = config.crondis();
					break;
				case 15700:
					enabled = config.zebak();
					break;
				case 14162:
					enabled = config.scabaras();
					break;
				case 14164:
					enabled = config.kephri();
					break;
				case 15186:
					enabled = config.ampeken();
					break;
				case 15188:
					enabled = config.baba();
					break;
				case 14674:
					enabled = config.het();
					break;
				case 14676:
					enabled = config.akkha();
					break;
				case 15184:
				case 15696:
					enabled = config.wardens();
					break;
				default:
					enabled = false;
			}
		}

		previousRegion = currentRegion;
	}

	@Subscribe
	protected void onClientTick(ClientTick t)
	{
		clientTickCounter++;

		if (playersKerised.isEmpty())
		{
			return;
		}

		playersKerised.entrySet().removeIf(p -> tookPicture(p.getKey()));
	}

	private boolean tookPicture(String playerName)
	{
		if (playersKerised.get(playerName) > clientTickCounter)
		{
			return false;
		}
		takeScreenshot("Keris " + playerName, SD_KERIS);

		return true;
	}

	void takeScreenshot(String fileName, String subDir)
	{
		if (client.getGameState() == GameState.LOGIN_SCREEN)
		{
			// Prevent the screenshot from being captured
			log.info("Login screenshot prevented");
			return;
		}

		Consumer<Image> imageCallback = (img) ->
		{
			// This callback is on the game thread, move to executor thread
			executor.submit(() -> takeScreenshot(fileName, subDir, img));
		};

		if (config.displayDate() && REPORT_BUTTON_TLIS.contains(client.getTopLevelInterfaceId()))
		{
			screenshotOverlay.queueForTimestamp(imageCallback);
		}
		else
		{
			drawManager.requestNextFrameListener(imageCallback);
		}
	}

	private static int getScaledValue(final double scale, final int value)
	{
		return (int) (value * scale + .5);
	}

	private void takeScreenshot(String fileName, String subDir, Image image)
	{
		final BufferedImage screenshot;
		if (!config.includeFrame())
		{
			// just simply copy the image
			screenshot = ImageUtil.bufferedImageFromImage(image);
		}
		else
		{
			// create a new image, paint the client ui to it, and then draw the screenshot to that
			final AffineTransform transform = OSType.getOSType() == OSType.MacOS ? new AffineTransform() :
				clientUi.getGraphicsConfiguration().getDefaultTransform();

			// scaled client dimensions
			int clientWidth = getScaledValue(transform.getScaleX(), clientUi.getWidth());
			int clientHeight = getScaledValue(transform.getScaleY(), clientUi.getHeight());

			screenshot = new BufferedImage(clientWidth, clientHeight, BufferedImage.TYPE_INT_ARGB);

			Graphics2D graphics = (Graphics2D) screenshot.getGraphics();
			AffineTransform originalTransform = graphics.getTransform();
			// scale g2d for the paint() call
			graphics.setTransform(transform);

			// Draw the client frame onto the screenshot
			try
			{
				SwingUtilities.invokeAndWait(() -> clientUi.paint(graphics));
			}
			catch (InterruptedException | InvocationTargetException e)
			{
				log.warn("unable to paint client UI on screenshot", e);
			}

			// Find the position of the canvas inside the frame
			final Point canvasOffset = clientUi.getCanvasOffset();
			final int gameOffsetX = getScaledValue(transform.getScaleX(), canvasOffset.getX());
			final int gameOffsetY = getScaledValue(transform.getScaleY(), canvasOffset.getY());

			// Draw the original screenshot onto the new screenshot
			graphics.setTransform(originalTransform); // the original screenshot is already scaled
			graphics.drawImage(image, gameOffsetX, gameOffsetY, null);
			graphics.dispose();
		}

		takeScreenshot(screenshot, fileName, subDir, config.notifyWhenTaken(), config.copyToClipboard());
	}

	/**
	 * Saves a screenshot of the client window to the screenshot folder as a PNG,
	 * and optionally uploads it to an image-hosting service.
	 *
	 * @param screenshot BufferedImage to capture.
	 * @param fileName   Filename to use, without file extension.
	 * @param subDir     Directory within the player screenshots dir to store the captured screenshot to.
	 * @param notify     Send a notification to the system tray when the image is captured.
	 * @param copyImage  which whether to place image in clipboard or not.
	 */
	public void takeScreenshot(BufferedImage screenshot, String fileName, @Nullable String subDir, boolean notify, boolean copyImage)
	{
		if (client.getGameState() == GameState.LOGIN_SCREEN)
		{
			// Prevent the screenshot from being captured
			log.info("Login screenshot prevented");
			return;
		}

		File playerFolder;
		if (client.getLocalPlayer() != null && client.getLocalPlayer().getName() != null)
		{
			String playerDir = client.getLocalPlayer().getName();
			RuneScapeProfileType profileType = RuneScapeProfileType.getCurrent(client);
			if (profileType != RuneScapeProfileType.STANDARD)
			{
				playerDir += "-" + Text.titleCase(profileType);
			}

			if (!Strings.isNullOrEmpty(subDir))
			{
				playerDir += File.separator + subDir;
			}

			playerFolder = new File(SCREENSHOT_DIR, playerDir);
		}
		else
		{
			playerFolder = SCREENSHOT_DIR;
		}

		playerFolder.mkdirs();

		fileName += (fileName.isEmpty() ? "" : " ") + format(new Date());

		try
		{
			File screenshotFile = new File(playerFolder, fileName + ".png");

			// To make sure that screenshots don't get overwritten, check if file exists,
			// and if it does create file with same name and suffix.
			int i = 1;
			while (screenshotFile.exists())
			{
				screenshotFile = new File(playerFolder, fileName + String.format("(%d)", i++) + ".png");
			}

			ImageIO.write(screenshot, "PNG", screenshotFile);

			if (copyImage)
			{
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				TOAShamerTBI transferableBufferedImage = new TOAShamerTBI(screenshot);
				clipboard.setContents(transferableBufferedImage, null);

				if (notify)
				{
					notifier.notify("A screenshot was saved and inserted into your clipboard!", TrayIcon.MessageType.INFO);
				}
			}
			else if (notify)
			{
				notifier.notify("A screenshot was saved to " + screenshotFile, TrayIcon.MessageType.INFO);
			}
		}
		catch (IOException ex)
		{
			log.warn("error writing screenshot", ex);
		}
	}

	private static String format(Date date)
	{
		synchronized (TIME_FORMAT)
		{
			return TIME_FORMAT.format(date);
		}
	}

	@Provides
	TOAShamerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TOAShamerConfig.class);
	}
}
