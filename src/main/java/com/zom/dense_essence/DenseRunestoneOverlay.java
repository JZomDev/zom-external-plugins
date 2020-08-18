package com.zom.dense_essence;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class DenseRunestoneOverlay extends Overlay
{
	private static final int Z_OFFSET = 200;
	private static final int MAX_DISTANCE = 2550;

	private final Client client;
	private final DenseEssencePlugin plugin;
	private final DenseEssenceConfig config;
	private final SkillIconManager skillIconManager;

	@Inject
	private DenseRunestoneOverlay(
		Client client, DenseEssencePlugin plugin, DenseEssenceConfig config, SkillIconManager skillIconManager)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.skillIconManager = skillIconManager;

		setLayer(OverlayLayer.ABOVE_SCENE);
		setPosition(OverlayPosition.DYNAMIC);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		boolean northStoneMineable = client.getVarbitValue(4927) == 0;
		boolean southStoneMineable = client.getVarbitValue(4928) == 0;
		GameObject northStone = plugin.getDenseRunestoneNorth();
		GameObject southStone = plugin.getDenseRunestoneSouth();
		LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();

		if (northStone != null)
		{
			if (plugin.getLocalPointRunestoneNorth().distanceTo(playerLocation) < MAX_DISTANCE)
			{
				renderStone(graphics, northStone, northStoneMineable);
			}
		}

		if (southStone != null)
		{
			if (plugin.getLocalPointRunestoneSouth().distanceTo(playerLocation) < MAX_DISTANCE)
			{
				renderStone(graphics, southStone, southStoneMineable);
			}
		}

		return null;
	}

	private void renderStone(Graphics2D graphics, GameObject gameObject, boolean minable)
	{
		if (config.showDenseRunestoneClickbox())
		{
			Shape clickbox = gameObject.getClickbox();
			Point mousePosition = client.getMouseCanvasPosition();
			if (minable)
			{
				OverlayUtil.renderHoverableArea(
					graphics, clickbox, mousePosition,
					plugin.getCLICKBOX_FILL_COLOR_MINABLE(), plugin.getCLICKBOX_BORDER_COLOR_MINABLE(), plugin.getCLICKBOX_BORDER_HOVER_COLOR_MINABLE());
			}
			else
			{
				OverlayUtil.renderHoverableArea(
					graphics, clickbox, mousePosition,
					plugin.getCLICKBOX_FILL_COLOR_DEPLETED(), plugin.getCLICKBOX_BORDER_COLOR_DEPLETED(), plugin.getCLICKBOX_BORDER_HOVER_COLOR_DEPLETED());
			}
		}
		if (config.showDenseRunestoneIndicator() && minable)
		{
			LocalPoint gameObjectLocation = gameObject.getLocalLocation();
			OverlayUtil.renderImageLocation(
				client, graphics, gameObjectLocation,
				skillIconManager.getSkillImage(Skill.MINING, false), Z_OFFSET);
		}
	}
}
