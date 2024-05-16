package com.zom.conqol;

import com.google.inject.Provides;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
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

	private int clientTickCounter = 0;

	private final int CONSTRUCTION_WIDGET = 458;

	@Override
	protected void startUp() throws Exception
	{
		clientTickCounter = 0;
		log.info("Construction QOL plugin has started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientTickCounter = 0;
		log.info("Construction QOL plugin has shutdown!");
	}

	@Subscribe
	void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() != CONSTRUCTION_WIDGET)
		{
			return;
		}
		//

		Widget furnitureCreationMenuWidget123 = client.getWidget(458, 3);
		if (furnitureCreationMenuWidget123 != null)
		{
			log.info("{} 123 furnitureCreationMenuWidget {}", clientTickCounter,furnitureCreationMenuWidget123.getId());

			for (Widget constuctableItemWidget : furnitureCreationMenuWidget123.getStaticChildren())
			{

				log.info("{} 123 constuctableItemWidget {}",clientTickCounter, constuctableItemWidget.getId());
				Object[] listener = constuctableItemWidget.getOnKeyListener();

				if (listener != null)
				{
					log.info("{} 123 constuctableItemWidget keycode {}",clientTickCounter, listener[4]);
				}
			}
		}


		this.clientThread.invokeLater(() ->
		{
			Widget furnitureCreationMenuWidget = client.getWidget(458, 3);
			int i = 1;
			int keyCode = 48;
			if (furnitureCreationMenuWidget != null)
			{
				log.info("{} 321 furnitureCreationMenuWidget {}",clientTickCounter, furnitureCreationMenuWidget.getId());

				for (Widget constuctableItemWidget : furnitureCreationMenuWidget.getStaticChildren())
				{

					log.info("{} 321 constuctableItemWidget {}", clientTickCounter,  constuctableItemWidget.getId());
					Object[] listener = constuctableItemWidget.getOnKeyListener();

					if (listener != null)
					{
						log.info("{} 321 constuctableItemWidget keycode {}",clientTickCounter, listener[4]);
					}


					String name = constuctableItemWidget.getName();
					if (name == null || name.isEmpty()) continue;

					char c = (char) (keyCode + i);
					new ConstructionMenu()
						.constructionWidget(constuctableItemWidget)
						.hotKey(c)
						.build();
					i++;
				}
			}
		});
	}

	@Subscribe
	void onClientTick(ClientTick event)
	{
		Widget furnitureCreationMenuWidget = client.getWidget(458, 3);
		if (furnitureCreationMenuWidget != null)
		{
//			log.info("furnitureCreationMenuWidget {}", furnitureCreationMenuWidget.getId());

			for (Widget constructionWidget : furnitureCreationMenuWidget.getStaticChildren())
			{
				log.info("{} constuctableItemWidget {}", clientTickCounter, constructionWidget.getId());
				Object[] listener = constructionWidget.getOnKeyListener();

				if (listener != null)
				{
					log.info("{} constuctableItemWidget keycode {}", clientTickCounter, listener[4]);
				}
			}
		}

		clientTickCounter++;
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
		char hotKey;

		public void build()
		{
			if ((int) hotKey == config.input() + 48)
			{
				setOnKeyListener(config.output() + 48);
			} else if ((int) hotKey == config.output() + 48)
			{
				setOnKeyListener(config.input() + 48);
			}
		}

		void setOnKeyListener(int keyCode)
		{
			Object[] listener = constructionWidget.getOnKeyListener();

			if (listener == null)
				return;

			listener[4] = String.valueOf((char) keyCode);;
			constructionWidget.setOnKeyListener(listener);
			constructionWidget.revalidate();
		}
	}
}
