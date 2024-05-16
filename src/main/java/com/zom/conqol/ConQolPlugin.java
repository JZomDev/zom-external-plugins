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
public class ConQolPlugin extends Plugin implements KeyListener
{
	@Inject
	ClientThread clientThread;
	@Inject
	private Client client;
	@Inject
	private KeyManager keyManager;
	@Inject
	private ConQolConfig config;

	private final int CONSTRUCTION_WIDGET = 458;
	ArrayList<ConstructionMenu> constructionMenus = new ArrayList<>();

	@Override
	protected void startUp() throws Exception
	{
		keyManager.registerKeyListener(this);
		log.info("Construction QOL plugin has started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		keyManager.unregisterKeyListener(this);
		log.info("Construction QOL plugin has shutdown!");
	}

	@Subscribe
	void onScriptPreFired(ScriptPreFired ev)
	{
		switch (ev.getScriptId())
		{
			// build menu script
			case ScriptID.WIDGET_CLICKER_STATIC:
			{
				Object[] obj = ev.getScriptEvent().getArguments();
//				log.info("{}", ev.getScriptId());
				for (int i = 0; i < obj.length; i++)
				{
//					log.info("{} is type {}", obj[i], obj[i].getClass());
				}
				break;
			}
			case ScriptID.OTHER:
			{
				Object[] obj = ev.getScriptEvent().getArguments();
//				log.info("{}", ev.getScriptId());
				for (int i = 0; i < obj.length; i++)
				{
//					log.info("{} is type {}", obj[i], obj[i].getClass());
				}
				break;
			}
		}
	}

	@Subscribe
	void onScriptPostFired(ScriptPostFired ev)
	{
		switch (ev.getScriptId())
		{
			// build menu script
			case ScriptID.MENU_SETUP:
			{
				new ConstructionMenu()
					.constructionWidget(client.getScriptActiveWidget())
					.opWidget(client.getScriptActiveWidget())
					.resumeWidget(client.getScriptActiveWidget())
					.keyListenerWidget(client.getScriptActiveWidget())
					.build();
				break;
			}
		}
	}

	@Subscribe
	void onWidgetClosed(WidgetClosed ev)
	{
		if (ev.getGroupId() == CONSTRUCTION_WIDGET)
		{
			constructionMenus.clear();
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
		Widget resumeWidget;
		@Setter
		Widget opWidget;
		@Setter
		Widget keyListenerWidget;
		@Setter
		int spot = -1;
		@Setter
		int newSpot = -1;

		public void build()
		{
			if (constructionMenus.size() + 1 == config.input())
			{
				clearKeyListener();
				spot = config.input();
				newSpot = config.output();
			}

			if (constructionMenus.size() + 1 == config.output())
			{
				clearKeyListener();
				spot = config.output();
				newSpot = config.input();
			}

			ArrayList<ConstructionMenu> change = new ArrayList<>(constructionMenus);
			change.add(this);
			constructionMenus = change;
		}

		void clearKeyListener()
		{
			clientThread.invokeLater(() -> {
				keyListenerWidget.setOnKeyListener((Object[]) null);
			});
		}

		void onTrigger()
		{
			resume(resumeWidget);
		}

		private void resume(Widget w)
		{
			assert w.getId() == w.getParentId();
			Widget parent = w.getParent();
			if (parent != null)
			{
//				log.info("Parent's widgetid: " + parent.getId());
				Widget parentParent = parent.getParent();
				if (parentParent != null)
				{
//					log.info("Parent's parent widgetid: " + parentParent.getId());
				}
			}
			if (spot == -1) return;
			int wId = w.getId();
			int wIndex = w.getIndex();
			Widget parentParent = parent.getParent();
//			log.info("wId {}", wId);
//			log.info("parentParent {}", parentParent.getId());
//			log.info("wIndex {}", wIndex);
			// we are abusing this cs2 to just do a cc_find + cc_resume_pausebutton for us
//			client.runScript(ScriptID.WIDGET_CLICKER_DYNAMIC, parentParent.getId(), wIndex);
			client.runScript(ScriptID.WIDGET_CLICKER_STATIC, -2147483639, newSpot, -2147483645, String.valueOf(spot), "", 0);
//			client.runScript(ScriptID.OTHER, -2147483644, newSpot, 8117 ,-2147483645, 4);
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (constructionMenus.isEmpty())
		{
			return;
		}

		for (ConstructionMenu menu : constructionMenus)
		{
			if (e.getKeyCode() == 48 + config.input())
			{
				e.setKeyCode( config.output() + 48);
				log.info("Key code {}", e.getKeyCode());
				if (menu.spot != -1 && menu.spot == config.input())
				{
					clientThread.invokeLater(() -> menu.onTrigger());
				}
			}
			else if (e.getKeyCode() == 48 + config.output() + 1251251)
			{
				if (menu.spot != -1)
				{
					clientThread.invokeLater(() -> menu.onTrigger());
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}
}
