package com.zom.conqol;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
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
	@Inject
	ClientThread clientThread;
	@Inject
	private Client client;
	@Inject
	private ConQolConfig config;

	private final int CONSTRUCTION_WIDGET = 458;
	private final int DIGIT_OFFSET = 48;

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
	void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() != CONSTRUCTION_WIDGET)
		{
			return;
		}

		// index 3 is the specific window containing the constructable items
		Widget furnitureCreationMenuWidget = client.getWidget(CONSTRUCTION_WIDGET, 3);
		if (furnitureCreationMenuWidget != null)
		{
			clientThread.invokeLater(() ->
			{
				int i = 1;
				for (Widget constuctableItemWidget : furnitureCreationMenuWidget.getStaticChildren())
				{

					String name = constuctableItemWidget.getName();
					if (name == null || name.isEmpty())
					{
						continue;
					}

					new ConstructionMenuItem()
						.constructionWidget(constuctableItemWidget)
						.hotKey(DIGIT_OFFSET + i)
						.checkHotKeySwap();
					i++;
				}
			});
		}
	}

	@Provides
	ConQolConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ConQolConfig.class);
	}

	@NoArgsConstructor
	@Accessors(fluent = true, chain = true)
	class ConstructionMenuItem
	{
		@Setter
		Widget constructionWidget;
		@Setter
		int hotKey;

		public void checkHotKeySwap()
		{
			int inputKeyCode = config.input() + DIGIT_OFFSET;
			int outputKeyCode = config.output() + DIGIT_OFFSET;
			if (hotKey == inputKeyCode)
			{
				hotKey = outputKeyCode;
			} else if (hotKey == outputKeyCode)
			{
				hotKey = inputKeyCode;
			}
			setOnKeyListener(hotKey);
		}

		void setOnKeyListener(int keyCode)
		{
			Object[] listener = constructionWidget.getOnKeyListener();

			if (listener == null)
			{
				return;
			}

			listener[4] = String.valueOf((char) keyCode);
			constructionWidget.setOnKeyListener(listener);
			constructionWidget.revalidate();
		}
	}
}
