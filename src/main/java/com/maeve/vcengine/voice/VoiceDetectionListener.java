package com.maeve.vcengine.voice;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.maeve.vcengine.MythicVoiceEngine;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class VoiceDetectionListener implements VoicechatPlugin {
    private final MythicVoiceEngine plugin;
    private final Map<UUID, Long> lastDebugOutput = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastScheduled = new ConcurrentHashMap<>();

    public VoiceDetectionListener(MythicVoiceEngine plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPluginId() {
        return "MythicVoiceEngine";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket);
    }

    private void onMicrophonePacket(MicrophonePacketEvent event) {
        if (!plugin.isVoiceEnabled()) {
            return;
        }

        if (event.getSenderConnection() == null || event.getSenderConnection().getPlayer() == null) {
            return;
        }

        UUID playerId = event.getSenderConnection().getPlayer().getUuid();
        long now = System.currentTimeMillis();
        long previous = lastScheduled.getOrDefault(playerId, 0L);
        if (now - previous < plugin.getPacketProcessingIntervalMillis()) {
            return;
        }
        lastScheduled.put(playerId, now);

        boolean whispering = event.getPacket().isWhispering();
        double radius = plugin.getVoiceRadius(whispering);

        Bukkit.getScheduler().runTask(plugin, () -> processVoicePacket(playerId, whispering, radius));
    }

    private void processVoicePacket(UUID playerId, boolean whispering, double radius) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null || !player.isOnline() || player.isDead() || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        Location location = player.getLocation().clone();
        VoiceSoundEvent soundEvent = new VoiceSoundEvent(
                playerId,
                player.getName(),
                location,
                whispering,
                radius,
                System.currentTimeMillis()
        );

        int alerted = plugin.isMobTriggersEnabled() ? plugin.getMobNotifier().alertNearbyMobs(soundEvent, player) : 0;

        if (plugin.isDebugEnabled()) {
            logDetection(soundEvent, alerted);
        }
    }

    private void logDetection(VoiceSoundEvent event, int alerted) {
        long now = event.timestamp();
        long previous = lastDebugOutput.getOrDefault(event.playerId(), 0L);
        if (now - previous < plugin.getDebugCooldownMillis()) {
            return;
        }

        lastDebugOutput.put(event.playerId(), now);
        Location location = event.location();
        plugin.getLogger().info(String.format(
                "Voice detected: player=%s mode=%s location=%s %.2f %.2f %.2f radius=%.1f mobs=%d",
                event.playerName(),
                event.whispering() ? "whisper" : "normal",
                location.getWorld() == null ? "unknown" : location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                event.hearingRadius(),
                alerted
        ));
    }
}
