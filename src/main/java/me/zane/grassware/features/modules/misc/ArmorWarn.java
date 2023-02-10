import net.minecraftforge.eventbus.api.*;

import net.minecraftforge.event.*;


public class ArmorWarning {

@SubscribeEvent

public static void checkForLowArmor (EntityEquipmentChangeEvent event) {

if (event != null && event instanceof EntityEquipmentChangeEvent){ //Check for correct type of Event

ItemStack armorItem = event .getTo(); //Get our new item that was moved to a slot

int maxDamageOfPiece=armorItem!=null ? armorItem.getMaxDamage() : 0; // get Max Damage value if available

float percentageRemaining=(maxDamageOfPiece>0 && armorItem!=null)?(float)(((float)armorItem.(maxDamage-1)- armorItem))/(maxDamage-1)*100: 0f ; // Calculate the % remaining based on damage taken and max damage points of he piece

System.outWARNING: Your Armor is at "+percentageRemaining+"% Remaining!!\n); /// Send out your warning message !! } }}
