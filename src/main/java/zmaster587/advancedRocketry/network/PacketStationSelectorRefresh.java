package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.inventory.StationSelectorLayout;
import zmaster587.advancedRocketry.inventory.ContainerStationSelector;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.libVulpes.network.BasePacket;
import zmaster587.libVulpes.network.PacketHandler;

/** Small conditional request; station NBT is sent only when landing pads changed. */
public class PacketStationSelectorRefresh extends BasePacket {
    private int windowId;
    private int rocketEntityId;
    private int stationId;
    private long clientFingerprint;

    public PacketStationSelectorRefresh() {}

    public PacketStationSelectorRefresh(int windowId, int rocketEntityId, int stationId,
                                        long clientFingerprint) {
        this.windowId = windowId;
        this.rocketEntityId = rocketEntityId;
        this.stationId = stationId;
        this.clientFingerprint = clientFingerprint;
    }

    @Override
    public void write(ByteBuf out) {
        out.writeInt(windowId);
        out.writeInt(rocketEntityId);
        out.writeInt(stationId);
        out.writeLong(clientFingerprint);
    }

    @Override
    public void read(ByteBuf in) {
        windowId = in.readInt();
        rocketEntityId = in.readInt();
        stationId = in.readInt();
        clientFingerprint = in.readLong();
    }

    @Override
    public void readClient(ByteBuf in) {
        read(in);
    }

    @Override
    public void executeClient(EntityPlayer player) {
        // Serverbound only.
    }

    @Override
    public void executeServer(EntityPlayerMP player) {
        if (!(player.openContainer instanceof ContainerStationSelector)
                || player.openContainer.windowId != windowId
                || ((ContainerStationSelector) player.openContainer).getRocketEntityId()
                != rocketEntityId) return;
        Entity entity = player.world.getEntityByID(rocketEntityId);
        if (!(entity instanceof EntityRocket) || entity.isDead
                || player.getDistanceSq(entity) > 4096) return;

        EntityRocket rocket = (EntityRocket) entity;
        if (rocket.storage == null || rocket.storage.getGuidanceComputer() == null) return;
        ItemStack chip = rocket.storage.getGuidanceComputer().getStackInSlot(0);
        if (chip.isEmpty() || !(chip.getItem() instanceof ItemStationChip)
                || ItemStationChip.getUUID(chip) != stationId) return;

        ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStation(stationId);
        if (!(object instanceof SpaceStationObject)) return;
        SpaceStationObject station = (SpaceStationObject) object;
        if (StationSelectorLayout.fingerprint(station) != clientFingerprint)
            PacketHandler.sendToPlayer(new PacketSpaceStationInfo(stationId, station), player);
    }
}
