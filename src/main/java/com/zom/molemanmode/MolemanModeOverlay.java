package com.zom.molemanmode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Duration;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

class MolemanModeOverlay extends OverlayPanel
{
	private final Client client;
	private final MolemanModePlugin plugin;

	@Inject
	private MolemanModeConfig config;

	@Inject
	private MolemanModeOverlay(Client client, MolemanModePlugin plugin, MolemanModeConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.MED);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		boolean aboveGround = plugin.isAboveGround();
		long timeSpentAbove = plugin.getTicksSpentAboveGround();
		long timeAvailable = plugin.getTimeAvailable();

		String formattedTimeAbove = plugin.isFormatTicksAsTime() ?
			formatTicksAsTime(timeSpentAbove) :
			addCommasToNumber(timeSpentAbove);

		String formattedTimeAvailable = plugin.isFormatTicksAsTime() ?
			formatTicksAsTime(timeAvailable) :
			addCommasToNumber(timeAvailable);

		if (plugin.isShowAboveGroundState())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Above ground: ")
				.right(aboveGround ? "Yes" : "No")
				.build());
		}

		panelComponent.getChildren().add(LineComponent.builder()
			.left((plugin.isFormatTicksAsTime() ? "Time"  : "Ticks") + " spent above ground: ")
			.right(formattedTimeAbove)
			.build());

		panelComponent.getChildren().add(LineComponent.builder()
			.left((plugin.isFormatTicksAsTime() ? "Time"  : "Ticks") +" available above ground: ")
			.right(formattedTimeAvailable)
			.rightColor(plugin.getWarningCount() > timeAvailable ? Color.RED : Color.WHITE)
			.build());

		if (plugin.isShowRegion())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Region: ")
				.right(String.valueOf(client.getLocalPlayer().getWorldLocation().getRegionID()))
				.build());
		}

		return super.render(graphics);
	}

	private String addCommasToNumber(long number) {
		String input = Long.toString(number);
		StringBuilder output = new StringBuilder();
		for(int x = input.length() - 1; x >= 0; x--) {
			int lastPosition = input.length() - x - 1;
			if(lastPosition != 0 && lastPosition % 3 == 0) {
				output.append(",");
			}
			output.append(input.charAt(x));
		}
		return output.reverse().toString();
	}

	private String formatTicksAsTime(long number) {
		long ticksToTime = (long) (number * 0.6);
		Duration duration = Duration.ofSeconds(ticksToTime);

		long seconds = duration.getSeconds();
		long absSeconds = Math.abs(seconds);
		String positive = String.format(
			"%d:%02d:%02d",
			absSeconds / 3600,
			(absSeconds % 3600) / 60,
			absSeconds % 60);
		return seconds < 0 ? "-" + positive : positive;

	}
}
