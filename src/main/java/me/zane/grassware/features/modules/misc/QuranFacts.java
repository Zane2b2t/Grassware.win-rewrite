package me.zane.grassware.features.modules.misc;

import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.util.Timer2;
import me.zane.grassware.event.events.UpdateEvent;
import me.zane.grassware.features.modules.Module;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class QuranFacts extends Module {

    private final IntSetting delay = register("Delay", 4999, 0, 5000);

    private final BooleanSetting sound = register("Sound", true);
    private final BooleanSetting randomize = register("Randomize", false);

    private final Timer2 delayTimer = new Timer2();
    private final List<String> facts = new LinkedList<>();

    public QuranFacts() {
        facts.add("Quran fact #1 : Islam is the second-largest religion in the world");
        facts.add("Quran fact #2 : The word Islam means submission to the will of the one God.");
        facts.add("Quran fact #3 : Quran is 1,382 years old");
        facts.add("Quran fact #4 : Islam is the last official religion");
        facts.add("Quran fact #5 : Drinking alchol is haram, which would help the usa alot.");
        facts.add("Quran fact #6 : women wear hijab to to maintain modesty and privacy from unrelated males");
        
    }

    private int stage = 0;

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) {
            return;
        }
        if (stage >= facts.size()) {
            stage = 0;
        }
        Random random = new Random();
        if (delayTimer.hasReached(delay.getValue())) {
            if (!randomize.getValue()) {
                mc.player.sendChatMessage(facts.get(stage));
            } else {
                mc.player.sendChatMessage(facts.get(random.ints(0, facts.size()).iterator().nextInt()));
            }
            if (sound.getValue()) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.AMBIENT_CAVE, 1.0F));
            }
            stage++;
            delayTimer.reset();
        }
    }

}
