package com.zom.conqol;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ScriptPostFired;
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
	@Inject
	ClientThread clientThread;
	@Inject
	private Client client;
	@Inject
	private ConQolConfig config;

	private int counter = 0;
	@Override
	protected void startUp() throws Exception
	{
		log.info("Construction QOL plugin has started!");
		counter = 1;
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Construction QOL plugin has shutdown!");
		counter = 1;
	}

	@Subscribe
	void onScriptPostFired(ScriptPostFired ev)
	{
		switch (ev.getScriptId())
		{
			// build menu script
			case 1404:
			{
				new ConstructionMenu()
					.constructionWidget(client.getScriptActiveWidget())
					.spot(counter)
					.checkForSwap();
				counter++;
				break;
			}
			// reset counter to 1
			case 1406:
			{
				counter = 1;
			}
		}
	}

	@Provides
	ConQolConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ConQolConfig.class);
	}

	@NoArgsConstructor
	@Accessors(fluent = true, chain = true)
	class ConstructionMenu
	{
		@Setter
		Widget constructionWidget;
		@Setter
		int spot;
		public void checkForSwap()
		{
			if (spot == config.input())
			{
				int conMenuAsKeyCodeOutput = config.output() + 48;
				String output = String.valueOf((char) conMenuAsKeyCodeOutput);

				clientThread.invokeLater(() -> {
					int id = constructionWidget.getId();
					swapOnKeyListener(id, output);
				});
			}
			else if (spot == config.output())
			{
				int conMenuAsKeyCodeInput = config.input() + 48;
				String input = String.valueOf((char) conMenuAsKeyCodeInput);

				clientThread.invokeLater(() -> {
					int id = constructionWidget.getId();
					swapOnKeyListener(id, input);
				});
			}
		}

		void swapOnKeyListener(int widgetId, String str)
		{
			Widget w = client.getWidget(widgetId);

			if (w == null) return;
			Object[] onKeyListener = w.getOnKeyListener();
			if (onKeyListener == null)
			{
				// it should only be null when the hot key listener for the item isn't populated due to not having all amterials
				log.debug("swap was null for {}", widgetId);
				return;
			}

			onKeyListener[4] = str;
			// replace hotkey listener from output to the input
			w.setOnKeyListener(onKeyListener);
			w.revalidate();
		}
	}
}
