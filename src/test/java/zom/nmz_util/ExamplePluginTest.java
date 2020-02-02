package zom.nmz_util;

import com.zom.nmz_util.NMZUtilPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(NMZUtilPlugin.class);
		RuneLite.main(args);
	}
}