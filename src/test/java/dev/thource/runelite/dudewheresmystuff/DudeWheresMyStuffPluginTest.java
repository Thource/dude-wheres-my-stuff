package dev.thource.runelite.dudewheresmystuff;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class DudeWheresMyStuffPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(DudeWheresMyStuffPlugin.class);
		RuneLite.main(args);
	}
}