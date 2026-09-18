package zmaster587.advancedRocketry.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import zmaster587.advancedRocketry.block.BlockFuelTank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Structural data only. Never serialize StorageChunk or its tile entities into an item. */
public final class RocketBlueprint {
    public static final int MAX_XZ = 16, MAX_Y = 128, MAX_CELLS = MAX_XZ * MAX_Y * MAX_XZ;
    private static final int VERSION = 1;
    public final int sizeX, sizeY, sizeZ;
    public final IBlockState[] palette;
    public final int[] cells; // sorted pairs: (y << 8 | x << 4 | z), palette index

    private RocketBlueprint(int x, int y, int z, IBlockState[] palette, int[] cells) {
        sizeX = x; sizeY = y; sizeZ = z; this.palette = palette; this.cells = cells;
    }

    public static RocketBlueprint fromRocket(StorageChunk storage) {
        if (storage == null || !validSize(storage.getSizeX(), storage.getSizeY(), storage.getSizeZ())) return null;
        List<IBlockState> states = new ArrayList<>();
        Map<IBlockState, Integer> indices = new HashMap<>();
        int[] entries = new int[2 * storage.getSizeX() * storage.getSizeY() * storage.getSizeZ()];
        int count = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        try {
            for (int y = 0; y < storage.getSizeY(); y++) for (int x = 0; x < storage.getSizeX(); x++) for (int z = 0; z < storage.getSizeZ(); z++) {
                pos.setPos(x, y, z);
                IBlockState state = storage.getBlockState(pos);
                Block block = state.getBlock();
                if (block == Blocks.AIR) continue;
                if (block.getRegistryName() == null) return null;
                int meta = block.getMetaFromState(state);
                if (meta < 0 || meta > 15 || !block.getStateFromMeta(meta).equals(state)) return null;
                Integer index = indices.get(state);
                if (index == null) { index = states.size(); states.add(state); indices.put(state, index); }
                entries[count++] = y << 8 | x << 4 | z;
                entries[count++] = index;
            }
        } catch (RuntimeException exception) { return null; }
        if (count == 0) return null;
        return new RocketBlueprint(storage.getSizeX(), storage.getSizeY(), storage.getSizeZ(), states.toArray(new IBlockState[0]), Arrays.copyOf(entries, count));
    }

    public static RocketBlueprint read(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTagCompound() || !stack.getTagCompound().hasKey("RocketBlueprint", 10)) return null;
        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("RocketBlueprint");
        int x = tag.getInteger("X"), y = tag.getInteger("Y"), z = tag.getInteger("Z");
        if (tag.getInteger("Version") != VERSION || !validSize(x, y, z)) return null;
        NBTTagList list = tag.getTagList("Palette", 10);
        int[] cells = tag.getIntArray("Cells");
        if (cells.length < 2 || cells.length > 2 * x * y * z || (cells.length & 1) != 0 || list.tagCount() == 0 || list.tagCount() > cells.length / 2) return null;
        IBlockState[] states = new IBlockState[list.tagCount()];
        try {
            for (int i = 0; i < states.length; i++) {
                NBTTagCompound entry = list.getCompoundTagAt(i);
                ResourceLocation name = new ResourceLocation(entry.getString("Block"));
                if (!ForgeRegistries.BLOCKS.containsKey(name)) return null;
                Block block = ForgeRegistries.BLOCKS.getValue(name);
                int meta = entry.getInteger("Meta");
                if (block == null || block == Blocks.AIR || meta < 0 || meta > 15) return null;
                states[i] = block.getStateFromMeta(meta);
                if (block.getMetaFromState(states[i]) != meta) return null;
            }
            int last = -1;
            for (int i = 0; i < cells.length; i += 2) {
                int packed = cells[i];
                if (packed <= last || packed >>> 15 != 0 || (packed & 15) >= z || (packed >> 4 & 15) >= x || (packed >> 8) >= y || cells[i + 1] < 0 || cells[i + 1] >= states.length) return null;
                last = packed;
            }
        } catch (RuntimeException exception) { return null; }
        return new RocketBlueprint(x, y, z, states, cells);
    }

    public void write(ItemStack stack) {
        NBTTagCompound root = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Version", VERSION);
        tag.setInteger("X", sizeX); tag.setInteger("Y", sizeY); tag.setInteger("Z", sizeZ);
        NBTTagList list = new NBTTagList();
        for (IBlockState state : palette) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setString("Block", state.getBlock().getRegistryName().toString());
            entry.setInteger("Meta", state.getBlock().getMetaFromState(state));
            list.appendTag(entry);
        }
        tag.setTag("Palette", list);
        tag.setIntArray("Cells", cells);
        root.setTag("RocketBlueprint", tag);
        stack.setTagCompound(root);
    }

    /** A block item may pay for orientation, but never for another block variant. */
    public static ItemStack material(IBlockState state) {
        Block block = state.getBlock();
        Item item = Item.getItemFromBlock(block);
        if (!(item instanceof ItemBlock) || ((ItemBlock) item).getBlock() != block) return ItemStack.EMPTY;
        try {
            ItemStack stack = new ItemStack(item, 1, block.damageDropped(state));
            IBlockState obtainable = block.getStateFromMeta(((ItemBlock) item).getMetadata(stack.getMetadata()));
            for (net.minecraft.block.properties.IProperty<?> property : state.getPropertyKeys()) {
                if (!net.minecraft.util.EnumFacing.class.equals(property.getValueClass())
                        && !(block instanceof BlockFuelTank && property == BlockFuelTank.TANKSTATES)
                        && !state.getProperties().get(property).equals(obtainable.getProperties().get(property))) return ItemStack.EMPTY;
            }
            return stack;
        } catch (RuntimeException exception) { return ItemStack.EMPTY; }
    }

    private static boolean validSize(int x, int y, int z) {
        return x > 0 && x <= MAX_XZ && y > 0 && y <= MAX_Y && z > 0 && z <= MAX_XZ;
    }
}