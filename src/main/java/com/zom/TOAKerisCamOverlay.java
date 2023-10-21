package com.zom;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MainBufferProvider;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class TOAKerisCamOverlay extends Overlay
{
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM. dd, yyyy");
	private static final int REPORT_BUTTON_X_OFFSET = 437;

	private final Client client;
	private final DrawManager drawManager;
	private final TOAKerisCamPlugin plugin;

	private final Queue<Consumer<Image>> consumers = new ConcurrentLinkedQueue<>();

	@Inject
	private TOAKerisCamOverlay(Client client, DrawManager drawManager, TOAKerisCamPlugin plugin)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.client = client;
		this.drawManager = drawManager;
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (consumers.isEmpty())
		{
			return null;
		}

		final MainBufferProvider bufferProvider = (MainBufferProvider) client.getBufferProvider();
		final int imageHeight = ((BufferedImage) bufferProvider.getImage()).getHeight();
		final int y = imageHeight - plugin.getReportButton().getHeight() - 1;

		graphics.drawImage(plugin.getReportButton(), REPORT_BUTTON_X_OFFSET, y, null);

		graphics.setFont(FontManager.getRunescapeSmallFont());
		FontMetrics fontMetrics = graphics.getFontMetrics();

		String date = DATE_FORMAT.format(new Date());
		final int dateWidth = fontMetrics.stringWidth(date);
		final int dateHeight = fontMetrics.getHeight();

		final int textX = REPORT_BUTTON_X_OFFSET + plugin.getReportButton().getWidth() / 2 - dateWidth / 2;
		final int textY = y + plugin.getReportButton().getHeight() / 2 + dateHeight / 2;

		graphics.setColor(Color.BLACK);
		graphics.drawString(date, textX + 1, textY + 1);

		graphics.setColor(Color.WHITE);
		graphics.drawString(date, textX, textY);

		// Request the queued screenshots to be taken,
		// now that the timestamp is visible.
		Consumer<Image> consumer;
		while ((consumer = consumers.poll()) != null)
		{
			drawManager.requestNextFrameListener(consumer);
		}

		return null;
	}

	void queueForTimestamp(Consumer<Image> screenshotConsumer)
	{
		if (plugin.getReportButton() == null)
		{
			return;
		}

		consumers.add(screenshotConsumer);
	}
}
