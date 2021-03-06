package com.denizenscript.denizen.paper.events;

import com.denizenscript.denizen.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.DenizenAPI;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class PlayerEquipsArmorScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player (un)equips armor
    // player (un)equips <item>
    // player (un)equips [helmet/chestplate/leggings/boots]
    //
    // @Regex ^on player (un)?equips [^\s]+$
    //
    // @Plugin Paper
    //
    // @Triggers when a player (un)equips armor.
    //
    // @Context
    // <context.new_item> returns the ItemTag that is now in the slot.
    // <context.old_item> returns the ItemTag that used to be in the slot.
    // <context.slot> returns the name of the slot.
    // -->

    public static HashMap<String, PlayerArmorChangeEvent.SlotType> slotsByName = new HashMap<>();
    public static HashMap<PlayerArmorChangeEvent.SlotType, String> namesBySlot = new HashMap<>();

    public static void registerSlot(String name, PlayerArmorChangeEvent.SlotType slot) {
        slotsByName.put(name, slot);
        namesBySlot.put(slot, name);
    }

    public PlayerEquipsArmorScriptEvent() {
        instance = this;
        registerSlot("helmet", PlayerArmorChangeEvent.SlotType.HEAD);
        registerSlot("chestplate", PlayerArmorChangeEvent.SlotType.CHEST);
        registerSlot("leggings", PlayerArmorChangeEvent.SlotType.LEGS);
        registerSlot("boots", PlayerArmorChangeEvent.SlotType.FEET);
    }

    public static PlayerEquipsArmorScriptEvent instance;
    public ItemTag oldItem;
    public ItemTag newItem;
    public PlayerArmorChangeEvent.SlotType slot;
    public PlayerTag player;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        return lower.startsWith("player equips ") || lower.startsWith("player unequips ");
    }

    @Override
    public boolean matches(ScriptPath path) {
        String type = path.eventArgLowerAt(1);
        String itemCompare = path.eventArgLowerAt(2);

        PlayerArmorChangeEvent.SlotType slotType = slotsByName.get(itemCompare);
        if (slotType != null && slot != slotType) {
            return false;
        }

        if (type.equals("equips")) {
            if (slotType != null) {
                if (newItem.getMaterial().getMaterial() == Material.AIR) {
                    return false;
                }
            }
            else if (!itemCompare.equals("armor") && !tryItem(newItem, itemCompare)) {
                return false;
            }
        }
        else { // unequips
            if (slotType != null) {
                if (oldItem.getMaterial().getMaterial() == Material.AIR) {
                    return false;
                }
            }
            else if (!itemCompare.equals("armor") && !tryItem(oldItem, itemCompare)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getName() {
        return "PlayerEquipsArmor";
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(player, null);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("new_item")) {
            return newItem;
        }
        else if (name.equals("old_item")) {
            return oldItem;
        }
        else if (name.equals("slot")) {
            return new ElementTag(namesBySlot.get(slot));
        }
        return super.getContext(name);
    }

    @EventHandler
    public void armorChangeEvent(PlayerArmorChangeEvent event) {
        newItem = new ItemTag(event.getNewItem());
        oldItem = new ItemTag(event.getOldItem());
        slot = event.getSlotType();
        player = new PlayerTag(event.getPlayer());
        fire(event);
    }
}
