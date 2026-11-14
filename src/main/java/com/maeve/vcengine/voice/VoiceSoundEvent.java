package com.maeve.vcengine.voice;

import org.bukkit.Location;

import java.util.UUID;

public record VoiceSoundEvent(
        UUID playerId,
        String playerName,
        Location location,
        boolean whispering,
        double hearingRadius,
        long timestamp
) {
    public VoiceSoundEvent {
        location = location.clone();
    }

    @Override
    public Location location() {
        return location.clone();
    }
}
