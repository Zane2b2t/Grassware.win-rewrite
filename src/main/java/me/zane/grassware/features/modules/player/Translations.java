/*package me.zane.grassware.features.modules.player;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.features.modules.Module;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;

import com.google.cloud.translate.TranslateOptions;
import net.minecraft.util.text.TextComponentString;

public class Translations extends Module {            //gradle stuff. ive done it but it completly broke the client
                                                      (talking about google)
    private Translate translate;

    public Translations() {
        translate = TranslateOptions.getDefaultInstance().getService();
    }

    @EventListener
    public void onChat(ClientChatReceivedEvent event) {
        String originalText = event.getMessage().getUnformattedText();
        Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage("ko"));
        String translatedText = translation.getTranslatedText();
        event.setMessage(new TextComponentString(translatedText));
    }
}

 */
