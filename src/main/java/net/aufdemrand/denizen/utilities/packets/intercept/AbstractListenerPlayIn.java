package net.aufdemrand.denizen.utilities.packets.intercept;

import net.minecraft.server.v1_8_R3.*;

public abstract class AbstractListenerPlayIn extends PlayerConnection {

    protected final PlayerConnection oldListener;

    public AbstractListenerPlayIn(NetworkManager networkManager, EntityPlayer entityPlayer) {
        super(MinecraftServer.getServer(), networkManager, entityPlayer);
        this.oldListener = entityPlayer.playerConnection;
    }

    @Override
    public void a(PacketPlayInArmAnimation packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInChat packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInTabComplete packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInClientCommand packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInSettings packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInTransaction packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInEnchantItem packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInWindowClick packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInCloseWindow packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInCustomPayload packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInUseEntity packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInKeepAlive packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInFlying packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInAbilities packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInBlockDig packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInEntityAction packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInSteerVehicle packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInHeldItemSlot packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInSetCreativeSlot packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInUpdateSign packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInBlockPlace packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInSpectate packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(PacketPlayInResourcePackStatus packet) {
        oldListener.a(packet);
    }

    @Override
    public void a(IChatBaseComponent iChatBaseComponent) {
        oldListener.a(iChatBaseComponent);
    }
}
