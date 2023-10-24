package me.zane.grassware.features.gui.alt;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import me.zane.grassware.mixin.mixins.IMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.util.UUID;

public class Alt {
    private final String login, password;
    private final AltType altType;
    private Session altSession;

    public Alt(final String altLogin, final String altPassword, final AltType altType) {
        this.login = altLogin;
        this.password = altPassword;
        this.altType = altType;
    }

    public void login() {
        if (altSession == null) {
            switch (getAltType()) {
                case MICROSOFT:
                    MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                    try {
                        MicrosoftAuthResult result = authenticator.loginWithCredentials(login, password);

                        altSession = new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "legacy");
                    } catch (MicrosoftAuthenticationException e) {
                        e.printStackTrace();
                    }
                    break;

                case CRACKED:
                    altSession = new Session(getLogin(), UUID.randomUUID().toString(), "", "legacy");
                    break;
            }
        }

        if (altSession != null) {
            ((IMinecraft) Minecraft.getMinecraft()).setSession(altSession);
        }
    }

    public AltType getAltType() {
        return altType;
    }


    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Session getAltSession() {
        return altSession;
    }

    public enum AltType {
        MICROSOFT,
        CRACKED
    }
}