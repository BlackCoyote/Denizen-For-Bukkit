package net.aufdemrand.denizen.scripts.commands.item;

import net.aufdemrand.denizen.objects.*;
import net.aufdemrand.denizen.scripts.ScriptEntry;
import net.aufdemrand.denizen.scripts.commands.AbstractCommand;
import net.aufdemrand.denizen.scripts.containers.core.MapScriptContainer;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizen.utilities.maps.DenizenMapManager;
import net.aufdemrand.denizen.utilities.maps.DenizenMapRenderer;
import net.aufdemrand.denizen.utilities.maps.MapAnimatedImage;
import net.aufdemrand.denizen.utilities.maps.MapImage;
import net.aufdemrand.denizencore.exceptions.CommandExecutionException;
import net.aufdemrand.denizencore.exceptions.InvalidArgumentsException;
import org.bukkit.Bukkit;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MapCommand extends AbstractCommand {

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        for (aH.Argument arg : aH.interpret(scriptEntry.getArguments())) {

            if (!scriptEntry.hasObject("new")
                    && arg.matchesPrefix("new")
                    && arg.matchesArgumentType(dWorld.class)) {
                scriptEntry.addObject("new", arg.asType(dWorld.class));
            }

            else if (!scriptEntry.hasObject("reset-loc")
                    && arg.matchesPrefix("r", "reset")
                    && arg.matchesArgumentType(dLocation.class)) {
                scriptEntry.addObject("reset-loc", arg.asType(dLocation.class));
                scriptEntry.addObject("reset", Element.TRUE);
            }

            else if (!scriptEntry.hasObject("reset")
                    && arg.matches("reset")) {
                scriptEntry.addObject("reset", Element.TRUE);
            }

            else if (!scriptEntry.hasObject("image")
                    && arg.matchesPrefix("i", "img","image")) {
                scriptEntry.addObject("image", arg.asElement());
            }

            else if (!scriptEntry.hasObject("resize")
                    && arg.matches("resize")) {
                scriptEntry.addObject("resize", Element.TRUE);
            }

            else if (!scriptEntry.hasObject("script")
                    && arg.matchesPrefix("s", "script")
                    && arg.matchesArgumentType(dScript.class)) {
                scriptEntry.addObject("script", arg.asType(dScript.class));
            }

            else if (!scriptEntry.hasObject("x-value")
                    && arg.matchesPrefix("x")
                    && arg.matchesPrimitive(aH.PrimitiveType.Integer)) {
                scriptEntry.addObject("x-value", arg.asElement());
            }

            else if (!scriptEntry.hasObject("y-value")
                    && arg.matchesPrefix("y")
                    && arg.matchesPrimitive(aH.PrimitiveType.Integer)) {
                scriptEntry.addObject("y-value", arg.asElement());
            }

            else if (!scriptEntry.hasObject("map-id")
                    && arg.matchesPrimitive(aH.PrimitiveType.Integer)) {
                scriptEntry.addObject("map-id", arg.asElement());
            }

        }

        if (!scriptEntry.hasObject("map-id") && !scriptEntry.hasObject("new"))
            throw new InvalidArgumentsException("Must specify a map ID or create a new map!");

        if (!scriptEntry.hasObject("reset")
                && !scriptEntry.hasObject("reset-loc")
                && !scriptEntry.hasObject("image")
                && !scriptEntry.hasObject("script"))
            throw new InvalidArgumentsException("Must specify a valid action to perform!");

        scriptEntry.defaultObject("reset", Element.FALSE).defaultObject("resize", Element.FALSE)
                .defaultObject("x-value", new Element(0)).defaultObject("y-value", new Element(0));

    }

    @Override
    public void execute(ScriptEntry scriptEntry) throws CommandExecutionException {

        Element id = scriptEntry.getElement("map-id");
        dWorld create = scriptEntry.getdObject("new");
        Element reset = scriptEntry.getElement("reset");
        dLocation resetLoc = scriptEntry.getdObject("reset-loc");
        Element image = scriptEntry.getElement("image");
        dScript script = scriptEntry.getdObject("script");
        Element resize = scriptEntry.getElement("resize");
        Element x = scriptEntry.getElement("x-value");
        Element y = scriptEntry.getElement("y-value");

        dB.report(scriptEntry, getName(), (id != null ? id.debug() : "") + (create != null ? create.debug() : "")
                + reset.debug() + (resetLoc != null ? resetLoc.debug() : "") + (image != null ? image.debug() : "")
                + (script != null ? script.debug() : "") + resize.debug() + x.debug() + y.debug());

        MapView map = null;
        if (create != null) {
            map = Bukkit.getServer().createMap(create.getWorld());
            scriptEntry.addObject("created_map", new Element(map.getId()));
        }
        else if (id != null) {
            map = Bukkit.getServer().getMap((short) id.asInt());
            if (map == null)
                throw new CommandExecutionException("No map found for ID '" + id.asInt() + "'!");
        }
        else {
            throw new CommandExecutionException("The map command failed somehow! Report this to a developer!");
        }

        if (reset.asBoolean()) {
            for (MapRenderer renderer : map.getRenderers()) {
                if (renderer instanceof DenizenMapRenderer) {
                    map.removeRenderer(renderer);
                    for (MapRenderer oldRenderer : ((DenizenMapRenderer) renderer).getOldRenderers())
                        map.addRenderer(oldRenderer);
                    if (resetLoc != null) {
                        map.setCenterX(resetLoc.getBlockX());
                        map.setCenterZ(resetLoc.getBlockZ());
                        map.setWorld(resetLoc.getWorld());
                    }
                }
            }
        }
        else if (script != null) {
            ((MapScriptContainer) script.getContainer()).applyTo(map);
        }
        else {
            DenizenMapRenderer dmr = DenizenMapManager.getDenizenRenderer(map);
            if (image != null) {
                if (image.asString().toLowerCase().endsWith(".gif"))
                    dmr.addObject(new MapAnimatedImage(x.asString(), y.asString(), "true", false, image.asString(),
                            resize.asBoolean() ? 128 : 0, resize.asBoolean() ? 128 : 0));
                else
                    dmr.addObject(new MapImage(x.asString(), y.asString(), "true", false, image.asString(),
                        resize.asBoolean() ? 128 : 0, resize.asBoolean() ? 128 : 0));
            }
        }

    }
}