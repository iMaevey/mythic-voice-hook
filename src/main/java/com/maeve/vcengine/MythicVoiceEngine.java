package com.maeve.vcengine;

import com.maeve.vcengine.command.MvhCommand;
import com.maeve.vcengine.mythic.VoiceMobNotifier;
import com.maeve.vcengine.mythic.VoiceTriggers;
import com.maeve.vcengine.voice.VoiceDetectionListener;
import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class MythicVoiceEngine extends JavaPlugin {
    private VoiceDetectionListener voiceDetectionListener;
    private VoiceMobNotifier mobNotifier;
    private volatile boolean voiceEnabled;
    private volatile boolean debugEnabled;
    private boolean mobTriggersEnabled;
    private double normalRadius;
    private double whisperRadius;
    private long debugCooldownMillis;
    private long packetProcessingIntervalMillis;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        voiceEnabled = getConfig().getBoolean("voice.enabled", true);
        mobTriggersEnabled = getConfig().getBoolean("voice.trigger-mobs", true);
        debugEnabled = getConfig().getBoolean("voice.debug", false);
        normalRadius = Math.max(0.0, getConfig().getDouble("voice.normal-radius", 16.0));
        whisperRadius = Math.max(0.0, getConfig().getDouble("voice.whisper-radius", 5.0));
        debugCooldownMillis = Math.max(0L, getConfig().getLong("voice.debug-cooldown-ms", 500L));
        packetProcessingIntervalMillis = Math.max(50L, getConfig().getLong("voice.packet-processing-interval-ms", 250L));

        mobNotifier = new VoiceMobNotifier();
        voiceDetectionListener = new VoiceDetectionListener(this);
        BukkitVoicechatService voicechatService = getServer().getServicesManager().load(BukkitVoicechatService.class);

        if (voicechatService == null) {
            getLogger().severe("Simple Voice Chat API service is unavailable. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        VoiceTriggers.register();
        voicechatService.registerPlugin(voiceDetectionListener);

        PluginCommand command = getCommand("mvh");
        if (command != null) {
            command.setExecutor(new MvhCommand(this));
        }

        getLogger().info("[MVE] Successfully enabled!!! Voice detection is " + (isVoiceEnabled() ? "enabled" : "disabled") + ".");
    }

    public boolean isVoiceEnabled() {
        return voiceEnabled;
    }

    public boolean isMobTriggersEnabled() {
        return mobTriggersEnabled;
    }

    public VoiceMobNotifier getMobNotifier() {
        return mobNotifier;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
        getConfig().set("voice.debug", enabled);
        saveConfig();
    }

    public double getVoiceRadius(boolean whispering) {
        return whispering ? whisperRadius : normalRadius;
    }

    public long getDebugCooldownMillis() {
        return debugCooldownMillis;
    }
    public long getPacketProcessingIntervalMillis() {
        return packetProcessingIntervalMillis;
    }
}

