package com.minimalbosshealth;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class MinimalBossHealthPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MinimalBossHealthPlugin.class);
		RuneLite.main(args);
	}
}