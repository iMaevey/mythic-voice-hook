package com.maeve.vcengine.mythic;

import com.maeve.vcengine.voice.VoiceSoundEvent;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class VoiceMobNotifier {
    public int alertNearbyMobs(VoiceSoundEvent event, Player source) {
        Location origin = event.location();
        World world = origin.getWorld();
        double radius = event.hearingRadius();
        if (world == null || radius <= 0.0) {
            return 0;
        }

        SkillTrigger trigger = VoiceTriggers.of(event.whispering());
        AbstractEntity triggerEntity = BukkitAdapter.adapt(source);
        double radiusSquared = radius * radius;
        int alerted = 0;

        for (Entity entity : world.getNearbyEntities(origin, radius, radius, radius)) {
            if (!(entity instanceof LivingEntity living) || living.isDead()) {
                continue;
            }

            if (living.getLocation().distanceSquared(origin) > radiusSquared) {
                continue;
            }

            Optional<ActiveMob> activeMob = MythicBukkit.inst().getMobManager().getActiveMob(living.getUniqueId());
            if (activeMob.isEmpty()) {
                continue;
            }

            MythicBukkit.inst().getSkillManager().getEventBus().processTrigger(trigger, activeMob.get(), triggerEntity, false);
            alerted++;
        }

        return alerted;
    }
}
