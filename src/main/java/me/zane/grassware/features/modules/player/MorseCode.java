package me.zane.grassware.features.modules.player;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.features.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.client.event.ClientChatEvent;

import java.util.HashMap;

public class MorseCode extends Module {

    static HashMap<Character, String> morseCode = new HashMap<>();
    static {
        morseCode.put('A', ".-");
        morseCode.put('B', "-...");
        morseCode.put('C', "-.-.");
        morseCode.put('D', "-..");
        morseCode.put('E', ".");
        morseCode.put('F', "..-.");
        morseCode.put('G', "--.");
        morseCode.put('H', "....");
        morseCode.put('I', "..");
        morseCode.put('J', ".---");
        morseCode.put('K', "-.-");
        morseCode.put('L', ".-..");
        morseCode.put('M', "--");
        morseCode.put('N', "-.");
        morseCode.put('O', "---");
        morseCode.put('P', ".--.");
        morseCode.put('Q', "--.-");
        morseCode.put('R', ".-.");
        morseCode.put('S', "...");
        morseCode.put('T', "-");
        morseCode.put('U', "..-");
        morseCode.put('V', "...-");
        morseCode.put('W', ".--");
        morseCode.put('X', "-..-");
        morseCode.put('Y', "-.--");
        morseCode.put('Z', "--..");
        morseCode.put('0', "-----");
        morseCode.put('1', ".----");
        morseCode.put('2', "..---");
        morseCode.put('3', "...--");
        morseCode.put('4', "....-");
        morseCode.put('5', ".....");
        morseCode.put('6', "-....");
        morseCode.put('7', "--...");
        morseCode.put('8', "---..");
        morseCode.put('9', "----.");
        morseCode.put('.', ".-.-.-");
        morseCode.put(',', "--..--");
        morseCode.put('?', "..--..");
        morseCode.put('!', "-.-.--");
    }

    public static void sendToMorse(String message) {
        message = message.toUpperCase();
        StringBuilder morseMessage = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char letter = message.charAt(i);
            String morseLetter = morseCode.get(letter);
            if (morseLetter != null) {
                morseMessage.append(morseLetter).append(" ");
            }
        }

        Minecraft.getMinecraft().getConnection().sendPacket(new CPacketChatMessage(morseMessage.toString()));
    }
    @EventListener
    public void onChat(ClientChatEvent event) {
        String message = event.getMessage();
        MorseCode.sendToMorse(event.getMessage());
    }
    public void sendChatMessage(String message) {
        // Intercept the chat message and pass it to the MorseCode module
        MorseCode.sendToMorse(message);
    }
}
