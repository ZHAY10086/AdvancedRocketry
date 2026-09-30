package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import zmaster587.advancedRocketry.inventory.GuiStationSelector;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.libVulpes.network.BasePacket;

import java.io.IOException;

/** Return the changed chip data to its still-open selector. */
public class PacketStationChipSync extends BasePacket {
    private int windowId;
    private int hotbarSlot;
    private NBTTagCompound tag;

    public PacketStationChipSync() {}

    public PacketStationChipSync(int windowId, int hotbarSlot, NBTTagCompound tag) {
        this.windowId = windowId;
        this.hotbarSlot = hotbarSlot;
        this.tag = tag == null ? null : tag.copy();
    }

    @Override
    public void write(ByteBuf out) {
        out.writeInt(windowId);
        out.writeInt(hotbarSlot);
        new PacketBuffer(out).writeCompoundTag(tag);
    }

    @Override
    public void read(ByteBuf in) {
        readClient(in);
    }

    @Override
    public void readClient(ByteBuf in) {
        windowId = in.readInt();
        hotbarSlot = in.readInt();
        try {
            tag = new PacketBuffer(in).readCompoundTag();
        } catch (IOException e) {
            tag = null;
        }
    }

    @Override
    public void executeClient(EntityPlayer player) {
        if (tag == null || player == null || player.inventory.currentItem != hotbarSlot
                || !(Minecraft.getMinecraft().currentScreen instanceof GuiStationSelector)) return;
        GuiStationSelector gui = (GuiStationSelector) Minecraft.getMinecraft().currentScreen;
        if (!gui.isHandheld() || gui.inventorySlots.windowId != windowId) return;

        ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemStationChip)) return;
        ((ItemStationChip) stack.getItem()).applyServerTagPreservingDraft(stack, tag);
        gui.refreshChip();
    }

    @Override
    public void executeServer(EntityPlayerMP player) {
        // Clientbound only.
    }
}
