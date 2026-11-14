package com.maeve.vcengine.mythic;

import io.lumine.mythic.api.skills.SkillTrigger;

public final class VoiceTriggers {
    public static final SkillTrigger HEAR_VOICE = SkillTrigger.create("HEARVOICE");
    public static final SkillTrigger HEAR_WHISPER = SkillTrigger.create("HEARWHISPER");

    private VoiceTriggers() {
    }

    public static void register() {
        SkillTrigger.register(HEAR_VOICE);
        SkillTrigger.register(HEAR_WHISPER);
    }

    public static SkillTrigger of(boolean whispering) {
        return whispering ? HEAR_WHISPER : HEAR_VOICE;
    }
}
