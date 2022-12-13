package me.zane.grassware.features.modules;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.GrassWare;
import me.zane.grassware.features.Feature;
import me.zane.grassware.features.command.Command;
import me.zane.grassware.features.setting.impl.BindSetting;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.modules.client.ModuleTools;
import me.zane.grassware.event.events.ClientEvent;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.event.events.Render2DEvent;


public class Module extends Feature {
    public float anim = 0.0f;
    public Category category;
    public BooleanSetting enabled = register("Enabled", false);
    public BooleanSetting drawn = register("Drawn", true);
    public BindSetting bind = register("Keybind", -1);
    public String info;

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onToggle() {
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            enable();
        } else {
            disable();
        }
    }

    public void enable() {
        if (enabled.getValue()) {
            GrassWare.eventBus.registerListener(this);
            onToggle();
            onDisable();
                case FUTURE: {
                    return new TextComponentString(ChatFormatting.RED + "[Future] " + ChatFormatting.GRAY + this.getDisplayName() + " toggled " + ChatFormatting.GREEN + "on" + ChatFormatting.GRAY + ".");
                }
                case DOTGOD: {
                    return new TextComponentString(ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "DotGod.CC" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.DARK_AQUA + this.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " was " + ChatFormatting.GREEN + "enabled.");

                }
                case PHOBOS: {
                    return new TextComponentString((HUD.getInstance().getCommandMessage()) + ChatFormatting.BOLD + this.getDisplayName() + ChatFormatting.RESET + ChatFormatting.GREEN + " enabled.");

                }
                            
                case SYNCCLIENT: {
                     return new TextComponentString(ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "SyncClient" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.DARK_AQUA + this.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " turned " + ChatFormatting.GREEN + "ON.");

                }       
                case TROLLGOD: {
                    return new TextComponentString((HUD.getInstance().getCommandMessage()) + ChatFormatting.DARK_PURPLE + this.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " was " + ChatFormatting.GREEN + "enabled.");
                }
            }
        }
        return new TextComponentString(HUD.getInstance().getCommandMessage() + ChatFormatting.GREEN + this.getDisplayName() + " toggled on.");
    }

    public void disable() {
        if (enabled.getValue()) {
            GrassWare.eventBus.unregisterListener(this);
            onToggle();
            onDisable();
                case FUTURE: {
                    return new TextComponentString(ChatFormatting.RED + "[Future] " + ChatFormatting.GRAY + this.getDisplayName() + " toggled " + ChatFormatting.RED + "off" + ChatFormatting.GRAY + ".");
                }
                case DOTGOD: {
                    return new TextComponentString(ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "DotGod.CC" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.DARK_AQUA + this.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " was " + ChatFormatting.RED + "disabled.");

                }
                case PHOBOS: {
                    return new TextComponentString((HUD.getInstance().getCommandMessage()) + ChatFormatting.BOLD + this.getDisplayName() + ChatFormatting.RESET + ChatFormatting.RED + " disabled.");

                }
                case SYNCCLIENT: {
                     return new TextComponentString(ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "SyncClient" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.DARK_AQUA + this.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " turned " + ChatFormatting.RED + "OFF.");       
                }
                case TROLLGOD: {
                    return new TextComponentString((HUD.getInstance().getCommandMessage()) + ChatFormatting.DARK_PURPLE + this.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " was " + ChatFormatting.RED + "disabled.");
                }
            }
        }
        return new TextComponentString(HUD.getInstance().getCommandMessage() + ChatFormatting.RED + this.getDisplayName() + " toggled off.");
    }
    
    public void enable() {
        if (!enabled.getValue()) {
            GrassWare.eventBus.registerListener(this);
            onToggle();
            onEnable();
            Command.sendRemovableMessage(GrassWare.commandManager.getClientMessage() + " " + ChatFormatting.WHITE + name + " Turned " + ChatFormatting.GREEN + "ON", 1);
            enabled.invokeValue(true);
        }
    }

    public void disable() {
        if (enabled.getValue()) {
            GrassWare.eventBus.unregisterListener(this);
            onToggle();
            onDisable();
            Command.sendRemovableMessage(GrassWare.commandManager.getClientMessage() + " " + ChatFormatting.WHITE + name + " Turned " + ChatFormatting.RED + "OFF", 1);
            enabled.invokeValue(false);
        }
    }

    public void toggle() {
        setEnabled(!isEnabled());
        if (isEnabled()) {
            enable();
        } else {
            disable();
        }
    }


    public Module setName(final String name) {
        this.name = name;
        return this;
    }

    public Module setCategory(final Category category) {
        this.category = category;
        return this;
    }

    public float totalStringWidth() {
        return stringWidth() + infoWidth();
    }

    public float stringWidth() {
        return -GrassWare.textManager.stringWidth(name + (info == null || info.equals("") ? "" : " "));
    }

    public Category getCategory() {
        return category;
    }

    public int getBind() {
        return bind.getValue();
    }

    public float infoWidth() {
        return -GrassWare.textManager.stringWidth(getInfo());
    }

    public String getInfo() {
        return info;
    }

    public void invokeInfo(final String info) {
        this.info = info;
    }

    public enum Category {
        COMBAT("Combat"),
        MISC("Misc"),
        RENDER("Render"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        CLIENT("Client");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

