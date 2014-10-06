package net.aufdemrand.denizen.scripts.commands.entity;

import java.util.List;

import net.aufdemrand.denizencore.exceptions.CommandExecutionException;
import net.aufdemrand.denizencore.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizen.objects.aH;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dList;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.scripts.ScriptEntry;
import net.aufdemrand.denizen.scripts.commands.AbstractCommand;
import net.aufdemrand.denizen.utilities.debugging.dB;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LeashHitch;

/**
 * Leashes a list of entities to another entity.
 *
 * @author Alain Blanquet, mcmonkey
 */

public class LeashCommand extends AbstractCommand {

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        for (aH.Argument arg : aH.interpret(scriptEntry.getArguments())) {

            if (!scriptEntry.hasObject("cancel")
                && arg.matches("cancel", "stop")) {
                scriptEntry.addObject("cancel", "");
            }

            else if (!scriptEntry.hasObject("entities")
                     && arg.matchesArgumentList(dEntity.class)) {
                scriptEntry.addObject("entities", arg.asType(dList.class).filter(dEntity.class));
            }

            else if (!scriptEntry.hasObject("holder")
                     && arg.matchesPrefix("holder", "h")) {

                if (arg.matchesArgumentType(dEntity.class))
                    scriptEntry.addObject("holder", arg.asType(dEntity.class));
                else if (arg.matchesArgumentType(dLocation.class))
                    scriptEntry.addObject("holder", arg.asType(dLocation.class));
            }

            else arg.reportUnhandled();
        }

        // Check to make sure required arguments have been filled
        if (!scriptEntry.hasObject("entities"))
            throw new InvalidArgumentsException("Must specify entity/entities!");

        if (!scriptEntry.hasObject("cancel")) {

            scriptEntry.defaultObject("holder",
                    scriptEntry.hasNPC() ? scriptEntry.getNPC().getDenizenEntity() : null,
                    scriptEntry.hasPlayer() ? scriptEntry.getPlayer().getDenizenEntity() : null);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(final ScriptEntry scriptEntry) throws CommandExecutionException {

        // Get objects
        List<dEntity> entities = (List<dEntity>) scriptEntry.getObject("entities");
        dEntity holder = null;
        dLocation holderLoc = null;
        Entity Holder = null;
        Object holderObject = scriptEntry.getObject("holder");
        if (holderObject instanceof dEntity) {
            holder = (dEntity) scriptEntry.getObject("holder");
            Holder = holder.getBukkitEntity();
        }
        else if (holderObject instanceof dLocation) {
            holderLoc = ((dLocation)scriptEntry.getObject("holder"));
            if (holderLoc.getBlock().getType() == Material.FENCE || holderLoc.getBlock().getType() == Material.NETHER_FENCE)
                Holder = holderLoc.getWorld().spawn(holderLoc, LeashHitch.class);
            else {
                dB.echoError(scriptEntry.getResidingQueue(), "Bad holder location specified - only fences are permitted!");
                return;
            }
        }
        Boolean cancel = scriptEntry.hasObject("cancel");

        // Report to dB
        dB.report(scriptEntry, getName(), (cancel ? aH.debugObj("cancel", cancel) : "") +
                             aH.debugObj("entities", entities.toString()) +
                             (holder != null ? aH.debugObj("holder", holder) : aH.debugObj("holder", holderLoc)));

        // Go through all the entities and leash/unleash them
        for (dEntity entity : entities) {
            if (entity.isSpawned() && entity.isLivingEntity()) {

                if (cancel) {
                    entity.getLivingEntity().setLeashHolder(null);
                }
                else {
                    entity.getLivingEntity().setLeashHolder(Holder);
                }
            }
        }
    }
}
