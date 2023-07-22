package me.zane.grassware.manager;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.zane.grassware.GrassWare;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.LastDamageUpdateEvent;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;

public class LastDamageManager {
    private final HashMap<EntityPlayer, Float> lastDamageMap = new HashMap<>();

    public LastDamageManager() {
        GrassWare.eventBus.registerListener(this);
    }

    public float getLastDamage(EntityPlayer entityPlayer){
        for (Map.Entry<EntityPlayer, Float> entry : lastDamageMap.entrySet()){
            if (entry.getKey().equals(entityPlayer)){
                return entry.getValue();
            }
        }
        return 0.0f;
    }

    @EventListener
    public void onLastDamageUpdate(LastDamageUpdateEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            lastDamageMap.put((EntityPlayer) event.getEntity(), event.getAmount());
        }
    }
}
