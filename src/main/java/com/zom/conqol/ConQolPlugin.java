package com.zom.conqol;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Construction QOL"
)
public class ConQolPlugin extends Plugin
{
	private final int CONSTRUCTION_WIDGET = 458;
	private final int CONSTRUCTION_MENU_WIDGET = 30015490;
	private final int CONSTRUCTION_BUILDABLE_MENU_WIDGET = 30015491;
	@Inject
	ClientThread clientThread;
	@Inject
	private Client client;
	@Inject
	private ConQolConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Construction QOL plugin has started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Construction QOL plugin has shutdown!");
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		// construction menu loaded
		if (widgetLoaded.getGroupId() == CONSTRUCTION_WIDGET)
		{
			// run a client tick later because the onOpStuff isn't set the moment 458 loaded
			clientThread.invokeLater(() -> {
				Widget furnitureCreationMenuWidget = client.getWidget(CONSTRUCTION_MENU_WIDGET);
				if (furnitureCreationMenuWidget != null)
				{
					for (Widget widget : furnitureCreationMenuWidget.getStaticChildren())
					{
						if (widget.getId() == CONSTRUCTION_BUILDABLE_MENU_WIDGET)
						{
							Widget[] constructionWidgets = client.getWidget(CONSTRUCTION_BUILDABLE_MENU_WIDGET).getStaticChildren();

							if (constructionWidgets != null && constructionWidgets.length > 0)
							{
								modifyWidget(constructionWidgets);
							}
						}
					}
				}
			});
		}
	}

	// modify construction menu widgets
	// swap the input key with the output key and output key with the input key
	private void modifyWidget(Widget[] constructionWidgets)
	{
		int firstSwappedConstructionWidget = -1;
		// loop through the widget list first time to assign output to the input key
		for (Widget constructionWidget : constructionWidgets)
		{
			Object[] onOpListener = constructionWidget.getOnOpListener();
			Object[] onKeyListener = constructionWidget.getOnKeyListener();

			// we know we're on the correct stuff due to the lengths
			if (onOpListener != null && onKeyListener != null && onOpListener.length == 6 && onKeyListener.length == 7)
			{
				String onOp = onKeyListener[4].toString();
				int conMenuAsKeyCodeInput = config.input() + 48;
				char c = (char) conMenuAsKeyCodeInput;
				String input = String.valueOf(c);

				// if onOpKeyListener's key is the same as the input key, switch it to the output key
				if (onOp.equals(input))
				{
					int conMenuAsKeyCodeOutput = config.output() + 48;
					onKeyListener[4] = String.valueOf((char) conMenuAsKeyCodeOutput);

					// set the widget found to not undo it in the next loop
					firstSwappedConstructionWidget = constructionWidget.getId();
				}

				// replace hotkey listener from input to the output
				constructionWidget.setOnKeyListener(onKeyListener);
			}
			constructionWidget.revalidate();
		}

		// loop through the widget list second time to assign input to the output key
		for (Widget constructionWidget : constructionWidgets)
		{
			// skip the original swap
			if (constructionWidget.getId() == firstSwappedConstructionWidget)
			{
				continue;
			}
			Object[] onOpListener = constructionWidget.getOnOpListener();
			Object[] onKeyListener = constructionWidget.getOnKeyListener();

			// we know we're on the correct stuff due to the lengths
			if (onOpListener != null && onKeyListener != null && onOpListener.length == 6 && onKeyListener.length == 7)
			{
				String onOp = onKeyListener[4].toString();

				int conMenuAsKeyCodeOutput = config.output() + 48;
				String output = String.valueOf((char) conMenuAsKeyCodeOutput);

				// if onOpKeyListener's key is the same as the output key, switch it to the input key
				if (onOp.equals(output))
				{
					int conMenuAsKeyCodeInput = config.input() + 48;

					onKeyListener[4] = String.valueOf((char) conMenuAsKeyCodeInput);
				}
				// replace hotkey listener from output to the input
				constructionWidget.setOnKeyListener(onKeyListener);

			}
			constructionWidget.revalidate();
		}
	}

	@Provides
	ConQolConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ConQolConfig.class);
	}
}
