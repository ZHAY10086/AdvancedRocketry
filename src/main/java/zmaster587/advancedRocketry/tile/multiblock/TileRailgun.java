package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.entity.EntityItemAbducted;
import zmaster587.advancedRocketry.inventory.modules.ModuleLimitedSlotArrayTooltip;
import zmaster587.advancedRocketry.inventory.modules.ModuleTextTooltip;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.advancedRocketry.util.PlanetaryTravelHelper;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.ZUtils;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TileRailgun extends TileMultiPowerConsumer implements IInventory, ILinkableTile, IGuiCallback {
    static final Object[][][] structure = new Object[][][]
            {
                    {
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, "coilCopper", LibVulpesBlocks.blockStructureBlock, "coilCopper", null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null}
                    },
                    {
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, "coilCopper", LibVulpesBlocks.blockStructureBlock, "coilCopper", null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null}
                    },
                    {
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, "coilCopper", LibVulpesBlocks.blockStructureBlock, "coilCopper", null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null}
                    },
                    {
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, "coilCopper", LibVulpesBlocks.blockStructureBlock, "coilCopper", null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null}
                    },
                    {
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, "coilCopper", LibVulpesBlocks.blockStructureBlock, "coilCopper", null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null}
                    },
                    {
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, "coilCopper", LibVulpesBlocks.blockStructureBlock, "coilCopper", null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null}
                    },
                    {
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, "coilCopper", LibVulpesBlocks.blockStructureBlock, "coilCopper", null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null}
                    },
                    {
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, "coilCopper", LibVulpesBlocks.blockStructureBlock, "coilCopper", null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null}
                    },
                    {
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, "coilCopper", LibVulpesBlocks.blockStructureBlock, "coilCopper", null, null, null},
                            {null, null, null, null, "coilCopper", null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null}
                    },
                    {
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, "blockSteel", null, null, null, null},
                            {null, null, null, LibVulpesBlocks.blockAdvStructureBlock, "blockTitanium", LibVulpesBlocks.blockAdvStructureBlock, null, null, null},
                            {null, null, "blockSteel", "blockTitanium", "blockTitanium", "blockTitanium", "blockSteel", null, null},
                            {null, null, null, LibVulpesBlocks.blockAdvStructureBlock, "blockTitanium", LibVulpesBlocks.blockAdvStructureBlock, null, null, null},
                            {null, null, null, null, "blockSteel", null, null, null, null},
                            {null, null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null, null}
                    },
                    {
                            {"blockSteel", null, null, "slab", "slab", "slab", null, null, "blockSteel"},
                            {null, LibVulpesBlocks.blockAdvStructureBlock, "slab", 'I', 'c', 'O', "slab", LibVulpesBlocks.blockAdvStructureBlock, null},
                            {null, "slab", LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, "slab", null},
                            {"slab", "slab", LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, "slab", "slab"},
                            {"slab", "slab", LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.motors, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, "slab", "slab"},
                            {"slab", "slab", LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, "slab", "slab"},
                            {null, "slab", LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, "slab", null},
                            {null, LibVulpesBlocks.blockAdvStructureBlock, "slab", 'P', 'P', 'P', "slab", LibVulpesBlocks.blockAdvStructureBlock, null},
                            {"blockSteel", null, null, "slab", "slab", "slab", null, null, "blockSteel"}
                    }
            };
    public long recoil;
    private EmbeddedInventory inv;
    private Ticket ticket;
    private int minStackTransferSize = 1;
    private ModuleNumericTextbox textBox;
    private RedstoneState state;
    private ModuleRedstoneOutputButton redstoneControl;
    private static final byte STATUS_LINKER_MISSING = 0;
    private static final byte STATUS_READY = 1;
    private static final byte STATUS_FULL = 2;
    private static final byte STATUS_OFFLINE = 3;
    private static final byte STATUS_MISSING = 4;
    private static final byte STATUS_OUT_OF_RANGE = 5;

    private byte destinationStatus = STATUS_LINKER_MISSING;
    private ModuleText destinationStatusText;

    public TileRailgun() {
        inv = new EmbeddedInventory(1);
        powerPerTick = 100000;
        redstoneControl = new ModuleRedstoneOutputButton(174, 4, -1, "", this);
        state = RedstoneState.OFF;
        redstoneControl.setRedstoneState(state);
    }

    @Override
    protected int requiredPowerPerTick() {
        BlockPos pos = getDestPosition();
        if (pos != null) {
            int distance = (int) Math.sqrt(Math.pow(pos.getX() - this.pos.getX(), 2) + Math.pow(pos.getZ() - this.pos.getZ(), 2));
            if (getDestDimId() == this.world.provider.getDimension())
                distance = distance * 10 + 50000;
            return Math.min(distance, super.requiredPowerPerTick());
        }
        return super.requiredPowerPerTick();
    }

    @Override
    public boolean shouldHideBlock(World world, BlockPos pos, IBlockState tile) {
        return true;
    }

    /**
     * @return the destionation DIMID or Constants.INVALID_PLANET if not valid
     */
    private int getDestDimId() {
        ItemStack stack = inv.getStackInSlot(0);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemLinker) {
            return ItemLinker.getDimId(stack);
        }
        return Constants.INVALID_PLANET;
    }

    /**
     * @return the destionation DIMID or null if not valid
     */
    private BlockPos getDestPosition() {
        ItemStack stack = inv.getStackInSlot(0);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemLinker && ItemLinker.isSet(stack)) {
            return ItemLinker.getMasterCoords(stack);
        }
        return null;
    }

    private void updateDestinationStatus() {
        if (world == null || world.isRemote)
            return;

        byte newStatus = STATUS_LINKER_MISSING;

        BlockPos destPos = getDestPosition();
        int destDimId = getDestDimId();

        if (destPos != null && destDimId != Constants.INVALID_PLANET) {
            World destWorld = DimensionManager.getWorld(destDimId);

            if (destWorld == null || !destWorld.isBlockLoaded(destPos)) {
                newStatus = STATUS_OFFLINE;
            } else {
                TileEntity tile = destWorld.getTileEntity(destPos);

                if (tile != null && tile.isInvalid()) {
                    newStatus = STATUS_OFFLINE;
                } else if (!(tile instanceof TileRailgun)) {
                    newStatus = STATUS_MISSING;
                } else {
                    int effectiveDestId =
                            zmaster587.advancedRocketry.dimension.DimensionManager
                                    .getEffectiveDimId(destWorld, destPos).getId();

                    int effectiveSourceId =
                            zmaster587.advancedRocketry.dimension.DimensionManager
                                    .getEffectiveDimId(this.world, this.pos).getId();

                    boolean inRange =
                            PlanetaryTravelHelper.isTravelAnywhereInPlanetarySystem(
                                    this.world.provider.getDimension(),
                                    effectiveDestId)
                                    || effectiveDestId == effectiveSourceId;

                    if (!inRange) {
                        newStatus = STATUS_OUT_OF_RANGE;
                    } else {
                        newStatus = STATUS_READY;

                        TileRailgun destination = (TileRailgun) tile;

                        out:
                        for (IInventory input : itemInPorts) {
                            for (int i = input.getSizeInventory() - 1; i >= 0; i--) {
                                ItemStack stack = input.getStackInSlot(i);

                                if (!stack.isEmpty()
                                        && stack.getCount() >= minStackTransferSize) {
                                    if (!destination.canReceiveCargo(stack))
                                        newStatus = STATUS_FULL;

                                    break out;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (destinationStatus != newStatus) {
            destinationStatus = newStatus;

            PacketHandler.sendToNearby(
                    new PacketMachine(this, (byte) 6),
                    world.provider.getDimension(),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    64d);
        }
    }

    @Override
    public Object[][][] getStructure() {
        return structure;
    }

    @Override
    public List<ModuleBase> getModules(int ID, EntityPlayer player) {
        List<ModuleBase> modules = super.getModules(ID, player);
        modules.removeIf(module -> module instanceof ModuleText);

        if (world.isRemote) {
            textBox = new ModuleNumericTextbox(this, 80, 40, 32, 12, 2);
            textBox.setText(String.valueOf(minStackTransferSize));
            modules.add(new ModuleTextTooltip(60, 25,
                    LibVulpes.proxy.getLocalizedString("msg.railgun.transfermin"), 0x2b2b2b,
                    LibVulpes.proxy.getLocalizedString("msg.railgun.transfermin.tooltip")));
            modules.add(textBox);
            destinationStatusText = new ModuleText(40, 62, getDestinationStatusText(), getDestinationStatusColor());
            modules.add(destinationStatusText);
        }
        modules.add(new ModuleLimitedSlotArrayTooltip(40, 40, this, 0, 1,
                LibVulpes.proxy.getLocalizedString("msg.railgun.linker.tooltip")));
        modules.add(redstoneControl);

        return modules;
    }

    private String getDestinationStatusText() {
        switch (destinationStatus) {
            case STATUS_READY:
                return LibVulpes.proxy.getLocalizedString("msg.railgun.status.ready");
            case STATUS_FULL:
                return LibVulpes.proxy.getLocalizedString("msg.railgun.status.full");
            case STATUS_OFFLINE:
                return LibVulpes.proxy.getLocalizedString("msg.railgun.status.offline");
            case STATUS_MISSING:
                return LibVulpes.proxy.getLocalizedString("msg.railgun.status.missing");
            case STATUS_OUT_OF_RANGE:
                return LibVulpes.proxy.getLocalizedString("msg.railgun.status.outofrange");
            default:
                return LibVulpes.proxy.getLocalizedString("msg.railgun.status.linkermissing");
        }
    }

    private int getDestinationStatusColor() {
        switch (destinationStatus) {
            case STATUS_MISSING:
            case STATUS_OUT_OF_RANGE: return 0xFFFF5555;
            case STATUS_LINKER_MISSING: return 0xFF777777;
            default: return 0xFFFFFF22;
        }
    }

    @Override
    public void onLoad() {
        if (ticket == null) {
            ticket = ForgeChunkManager.requestTicket(AdvancedRocketry.instance, this.world, Type.NORMAL);
            if (ticket != null)
                ForgeChunkManager.forceChunk(ticket, new ChunkPos(getPos().getX() / 16 - (getPos().getX() < 0 ? 1 : 0), getPos().getZ() / 16 - (getPos().getZ() < 0 ? 1 : 0)));
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        ForgeChunkManager.releaseTicket(ticket);
    }

    @Override
    public void onInventoryUpdated() {
        //Needs completion
        if (itemInPorts.isEmpty()) {
            attemptCompleteStructure(world.getBlockState(pos));
        }
    }

    @Override
    public void onInventoryButtonPressed(int buttonId) {
        super.onInventoryButtonPressed(buttonId);

        if (buttonId == -1) {
            state = redstoneControl.getState();
            PacketHandler.sendToServer(new PacketMachine(this, (byte) 5));
        }
    }

    protected void onRunningPoweredTick() {
        if (!world.isRemote) {
            useEnergy(usedPowerPerTick());
        }
    }

    @Override
    public void useEnergy(int amt) {
        if (!world.isRemote && enabled && isRedstoneStateSatisfied() && attemptCargoTransfer())
            super.useEnergy(amt);
    }

    @Override
    public boolean isRunning() {
        return isComplete();
    }

    private boolean isRedstoneStateSatisfied() {
        if (state == RedstoneState.OFF)
            return true;

        boolean powered = world.isBlockIndirectlyGettingPowered(pos) > 0;

        return (state == RedstoneState.ON && powered) || (!powered && state == RedstoneState.INVERTED);
    }

    private boolean attemptCargoTransfer() {
        if (world.isRemote)
            return false;

        ItemStack tfrStack = ItemStack.EMPTY;
        IInventory inv2 = null;
        int index = 0;
        //BlockPos invPos;

        out:
        for (IInventory inv : this.itemInPorts) {
            for (int i = inv.getSizeInventory() - 1; i >= 0; i--) {
                if (!(tfrStack = inv.getStackInSlot(i)).isEmpty() && inv.getStackInSlot(i).getCount() >= minStackTransferSize) {
                    inv2 = inv;
                    index = i;

                    //invPos = ((TileEntity)inv).getPos();

                    break out;
                } else tfrStack = ItemStack.EMPTY;
            }
        }

        if (!tfrStack.isEmpty()) {
            BlockPos pos = getDestPosition();
            if (pos != null) {
                int dimId;

                dimId = getDestDimId();

                if (dimId != Constants.INVALID_PLANET) {
                    World world = DimensionManager.getWorld(dimId);
                    TileEntity tile;

                    if (world != null && (tile = world.getTileEntity(pos)) instanceof TileRailgun && ((TileRailgun) tile).canReceiveCargo(tfrStack) &&
                            (PlanetaryTravelHelper.isTravelAnywhereInPlanetarySystem(this.world.provider.getDimension(),
                                    zmaster587.advancedRocketry.dimension.DimensionManager.getEffectiveDimId(world, pos).getId()) ||
                                    zmaster587.advancedRocketry.dimension.DimensionManager.getEffectiveDimId(world, pos).getId() == zmaster587.advancedRocketry.dimension.DimensionManager.getEffectiveDimId(this.world, this.pos).getId())) {

                        ((TileRailgun) tile).onReceiveCargo(tfrStack);
                        inv2.setInventorySlotContents(index, ItemStack.EMPTY);
                        inv2.markDirty();
                        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);

                        EnumFacing dir = RotatableBlock.getFront(world.getBlockState(pos));

                        EntityItemAbducted ent = new EntityItemAbducted(this.world, this.pos.getX() - 2 * dir.getFrontOffsetX() + 0.5f, this.pos.getY() + 5, this.pos.getZ() - 2 * dir.getFrontOffsetZ() + 0.5f, tfrStack);
                        this.world.spawnEntity(ent);
                        PacketHandler.sendToNearby(new PacketMachine(this, (byte) 3), this.world.provider.getDimension(), this.pos.getX() - dir.getFrontOffsetX(), this.pos.getY() + 5, this.pos.getZ() - dir.getFrontOffsetZ(), 64d);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean canReceiveCargo(@Nonnull ItemStack stack) {
        for (IInventory inv : this.itemOutPorts) {
            if (ZUtils.doesInvHaveRoom(stack, inv))
                return true;
        }
        return false;
    }

    public void onReceiveCargo(@Nonnull ItemStack stack) {
        for (IInventory inv : this.itemOutPorts) {
            if (ZUtils.doesInvHaveRoom(stack, inv)) {
                ZUtils.mergeInventory(stack, inv);
                break;
            }
        }
    }

    @Override
    public String getMachineName() {
        return AdvancedRocketryBlocks.blockRailgun.getLocalizedName();
    }

    @Override
    @Nonnull
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.getX() - 5, this.pos.getY(), this.pos.getZ() - 5, this.pos.getX() + 5, this.pos.getY() + 10, this.pos.getZ() + 5);
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int i) {
        return inv.getStackInSlot(i);
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int i, int j) {
        return inv.decrStackSize(i, j);
    }

    @Override
    public void setInventorySlotContents(int i, @Nonnull ItemStack j) {
        inv.setInventorySlotContents(i, j);
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUsableByPlayer(@Nullable EntityPlayer player) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return inv.isEmpty();
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        return slot == 0
                && stack.getItem() == LibVulpesItems.itemLinker
                && ItemLinker.isSet(stack)
                && ItemLinker.getDimId(stack) != Constants.INVALID_PLANET;
    }

    @Override
    public boolean onLinkStart(@Nonnull ItemStack item, TileEntity entity,
                               EntityPlayer player, World world) {
        ItemLinker.setMasterCoords(item, this.getPos());
        ItemLinker.setDimId(item, world.provider.getDimension());
        if (!world.isRemote)
            player.sendMessage(new TextComponentTranslation("msg.linker.program"));
        return true;
    }

    @Override
    public boolean onLinkComplete(@Nonnull ItemStack item, TileEntity entity, EntityPlayer player, World world) {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        inv.writeToNBT(nbt);
        nbt.setInteger("minTfrSize", minStackTransferSize);
        nbt.setByte("redstoneState", (byte) state.ordinal());
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        inv.readFromNBT(nbt);
        minStackTransferSize = nbt.getInteger("minTfrSize");

        state = RedstoneState.values()[nbt.getByte("redstoneState")];
        redstoneControl.setRedstoneState(state);
    }

    @Override
    public void writeDataToNetwork(ByteBuf out, byte id) {
        if (id == 4)
            out.writeInt(minStackTransferSize);
        else if (id == 5)
            out.writeByte(state.ordinal());
        else if (id == 6)
            out.writeByte(destinationStatus);
        else
            super.writeDataToNetwork(out, id);
    }

    @Override
    public void readDataFromNetwork(ByteBuf in, byte packetId,
                                    NBTTagCompound nbt) {
        if (packetId == 4)
            nbt.setInteger("minTransferSize", in.readInt());
        else if (packetId == 5)
            nbt.setByte("state", in.readByte());
        else if (packetId == 6)
            nbt.setByte("destinationStatus", in.readByte());
        else
            super.readDataFromNetwork(in, packetId, nbt);
    }

    @Override
    public void useNetworkData(EntityPlayer player, Side side, byte id,
                               NBTTagCompound nbt) {
        if (side.isClient()) {
            if (id == 3) {
                EnumFacing dir = RotatableBlock.getFront(world.getBlockState(pos));
                LibVulpes.proxy.playSound(world, pos, AudioRegistry.railgunFire, SoundCategory.BLOCKS, Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.BLOCKS), 0.975f + world.rand.nextFloat() * 0.05f);
                recoil = world.getTotalWorldTime();
            } else if (id == 6) {
                destinationStatus = nbt.getByte("destinationStatus");

                if (destinationStatusText != null) {
                    destinationStatusText.setText(getDestinationStatusText());
                    destinationStatusText.setColor(getDestinationStatusColor());
                }
            }
        } else if (id == 4) {
            minStackTransferSize = nbt.getInteger("minTransferSize");

        } else if (id == 5) {
            state = RedstoneState.values()[nbt.getByte("state")];
        } else
            super.useNetworkData(player, side, id, nbt);
    }

    @Override
    public void onModuleUpdated(ModuleBase module) {
        if (module == textBox) {
            if (textBox.getText().isEmpty())
                minStackTransferSize = 1;
            else
                minStackTransferSize = MathHelper.clamp(Integer.parseInt(textBox.getText()), 1, 64);
            PacketHandler.sendToServer(new PacketMachine(this, (byte) 4));
        }
    }

    @Override
    protected void writeNetworkData(NBTTagCompound nbt) {
        super.writeNetworkData(nbt);
        nbt.setByte("state", (byte) state.ordinal());
        nbt.setInteger("minTfrSize", minStackTransferSize);
        nbt.setByte("destinationStatus", destinationStatus);
    }

    @Override
    protected void readNetworkData(NBTTagCompound nbt) {
        super.readNetworkData(nbt);
        state = RedstoneState.values()[nbt.getByte("redstoneState")];
        redstoneControl.setRedstoneState(state);
        minStackTransferSize = nbt.getInteger("minTfrSize");
        destinationStatus = nbt.getByte("destinationStatus");
    }

    @Override
    @Nonnull
    public String getName() {
        return getMachineName();
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int index) {
        return inv.removeStackFromSlot(index);
    }

    @Override
    public int getField(int id) {
        return inv.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        inv.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return inv.getFieldCount();
    }

    @Override
    public void clear() {
        inv.clear();
    }

    //TODO: MAKE IT CONDITIONAL WHEN GUI IS OPEN
    @Override
    public void update() {
        super.update();

        if (!world.isRemote && world.getTotalWorldTime() % 20L == 0L)
            updateDestinationStatus();
    }
}
