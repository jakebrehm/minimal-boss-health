package com.minimalbosshealth;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.NpcInfo;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Minimal Boss Health",
	description = "Provides minimal alternatives to the built-in boss health bars",
	tags = {"hp", "health", "bar", "boss", "fixed"}
)
public class MinimalBossHealthPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private MinimalBossHealthConfig config;

	@Inject
	private BarBossHealthOverlay overlay;

	BossRegistry bossRegistry;
	NPC currentBoss;  // TODO: Make dataclass combining BossModel, NPC, etc.

	@Override
	protected void startUp() throws Exception
	{
		bossRegistry = BossRegistryFactory.build();
		overlayManager.add(overlay);
		// Manually enabling the plugin does not happen on the main thread
		clientThread.invokeLater(() -> {
			if (client.getGameState() == GameState.LOGGED_IN) {
				client.getTopLevelWorldView().npcs().forEach(this::trySetBoss);
			}
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		currentBoss = null;
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event) {
		trySetBoss(event.getNpc());
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event) {
		if (event.getNpc() == currentBoss) {
			currentBoss = null;
		}
	}

	private void trySetBoss(NPC npc) {
		BossModel boss = bossRegistry.getBoss(npc.getId()).orElse(null);

		// Guard against undetermined boss
		if (boss == null) return;

		// Player must be in an instance if the boss fight takes place in an instance
		boolean isInInstance = client.getTopLevelWorldView().getScene().isInstance();
		if (boss.isInstanceRequired() && !isInInstance) return;

		// Player must be in one of the boss's valid regions if any were set
		boolean hasValidRegions = !boss.getValidRegions().isEmpty();
		int currentRegionID = client.getLocalPlayer().getWorldLocation().getRegionID();
		boolean playerInValidRegion = boss.getValidRegions().contains(currentRegionID);
		if (hasValidRegions && !playerInValidRegion) return;

		currentBoss = npc;
	}

	@Provides
	MinimalBossHealthConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MinimalBossHealthConfig.class);
	}
}
