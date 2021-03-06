package com.denizenscript.denizen.events.player;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Collection;

public class PlayerReceivesCommandsScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player receives commands
    //
    // @Regex ^on player receives commands$
    //
    // @Triggers when the list of available server commands is sent to the player for tab completion.
    //
    // @Context
    // <context.commands> returns a ListTag of received commands.
    //
    // @Determine
    // ListTag to set the player's available commands. NOTE: It is not possible to add entries to the command list, only remove them.
    //
    // -->

    public PlayerReceivesCommandsScriptEvent() {
        instance = this;
    }

    public static PlayerReceivesCommandsScriptEvent instance;
    private Collection<String> commands;
    public PlayerCommandSendEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).startsWith("player receives commands");
    }

    @Override
    public boolean matches(ScriptPath path) {
        return true;
    }

    @Override
    public String getName() {
        return "PlayerReceivesCommands";
    }

    @Override
    public boolean applyDetermination(ScriptPath path, ObjectTag determinationObj) {
        String determination = determinationObj.toString();
        if (determination.length() > 0 && !isDefaultDetermination(determinationObj)) {
            commands.clear();
            commands.addAll(ListTag.getListFor(determinationObj));
            return true;
        }
        return super.applyDetermination(path, determinationObj);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(new PlayerTag(event.getPlayer()), null);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("commands")) {
            ListTag list = new ListTag();
            list.addAll(commands);
            return list;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        if (EntityTag.isNPC(event.getPlayer())) {
            return;
        }
        commands = event.getCommands();
        this.event = event;
        fire(event);
    }
}
