package com.zom;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Cutscene Widget Hider",
	description = "Hides widgets during cut scenes (like loading poh house)",
	tags = {"widget","hide","cutscene","poh","load"}
)
public class CutsceneWidgetHider extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		// https://oldschool.runescape.wiki/w/RuneScape:Varbit/6719
		if (varbitChanged.getVarbitId() == 6719)
		{
			if (varbitChanged.getValue() == 2)
			{
				hideWidgets(true);
			}

			if (varbitChanged.getValue() == 0)
			{
				hideWidgets(false);
			}
		}
	}

	// this was yoinked from HideWidgetsPlugin by PresNL
	protected void hideWidgets(boolean hide)
	{
		// hiding in fixed mode does not actually hide stuff and might break stuff so let's not do that
		if (hide && !client.isResized())
		{
			hideWidgets(false);
		}
		else
		{
			clientThread.invokeLater(() ->
			{
				// modern resizeable
				Widget modernResizableMinimap = client.getWidget(ComponentID.RESIZABLE_VIEWPORT_BOTTOM_LINE_MINIMAP);
				if (modernResizableMinimap != null)
				{
					Widget modernResizableParent = modernResizableMinimap.getParent();
					if (modernResizableParent != null)
					{
						hideWidgetChildren(modernResizableParent, hide);

						// fix zoom modern resizeable
						// zoom is child widget with the id 2 but if the parent is hidden the child is too
						Widget[] staticChildren = modernResizableParent.getStaticChildren();
						for (int i = 0; i < staticChildren.length; i++)
						{
							if (i == 1)
							{
								Widget zoom = staticChildren[1];
								if (zoom != null)
								{
									zoom.setHidden(false);
								}
							}
						}
					}
				}

				// classic resizeable
				Widget classicResizableMinimap = client.getWidget(ComponentID.RESIZABLE_VIEWPORT_MINIMAP);
				if (classicResizableMinimap != null)
				{
					Widget classicResizableParent = classicResizableMinimap.getParent();
					if (classicResizableParent != null)
					{
						hideWidgetChildren(classicResizableParent, hide);

						// fix zoom classic resizeable
						// zoom is child widget with the id 2 but if the parent is hidden the child is too
						Widget[] staticChildren = classicResizableMinimap.getStaticChildren();
						for (int i = 0; i < staticChildren.length; i++)
						{
							if (i == 1)
							{
								Widget zoom = staticChildren[1];
								if (zoom != null)
								{
									zoom.setHidden(false);
								}
							}
						}
					}
				}
			});
		}
	}

	protected void hideWidgetChildren(Widget root, boolean hide)
	{
		// The normal GetChildren function seems to always return 0 so we get all the different types
		// of other children instead and merge them into one array
		Widget[] rootDynamicChildren = root.getDynamicChildren();
		Widget[] rootNestedChildren = root.getNestedChildren();
		Widget[] rootStaticChildren = root.getStaticChildren();

		Widget[] rootChildren = new Widget[rootDynamicChildren.length + rootNestedChildren.length + rootStaticChildren.length];
		System.arraycopy(rootDynamicChildren, 0, rootChildren, 0, rootDynamicChildren.length);
		System.arraycopy(rootNestedChildren, 0, rootChildren, rootDynamicChildren.length, rootNestedChildren.length);
		System.arraycopy(rootStaticChildren, 0, rootChildren, rootDynamicChildren.length + rootNestedChildren.length, rootStaticChildren.length);

		for (Widget w : rootChildren)
		{
			if (w != null)
			{
				// hiding the widget with content type 1337 prevents the game from rendering so let's not do that
				if (w.getContentType() != 1337)
				{
					// classic resizble needs this?
					if (w.getId() == 10551388)
					{
						continue;
					}
					w.setHidden(hide);
				}
			}
		}
	}
}
