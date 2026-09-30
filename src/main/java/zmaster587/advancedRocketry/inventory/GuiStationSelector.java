package zmaster587.advancedRocketry.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.inventory.modules.ModuleStationSelectorList;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.network.PacketStationSelectorRefresh;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.util.StationLandingLocation;
import zmaster587.libVulpes.inventory.GuiModularFullScreen;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleTextBox;
import zmaster587.libVulpes.network.PacketHandler;

import java.util.List;
import java.util.Objects;

/** The existing LV full-screen renderer, with station-specific list refresh. */
public class GuiStationSelector extends GuiModularFullScreen {
    private final EntityPlayer player;
    private final IModularInventory inventory;
    private final int guiId;
    private final int rocketEntityId;
    private final List<ModuleBase> modules;
    private long lastPadFingerprint = Long.MIN_VALUE;
    private BlockPos lastSelectedPosition;
    private int refreshTicks;
    private boolean chipActionPending;

    public GuiStationSelector(EntityPlayer player, IModularInventory inventory, int guiId,
                              int rocketEntityId) {
        this(player, inventory, guiId, rocketEntityId, inventory.getModules(guiId, player));
    }

    private GuiStationSelector(EntityPlayer player, IModularInventory inventory, int guiId,
                               int rocketEntityId, List<ModuleBase> modules) {
        super(player, modules, inventory, false, false, "");
        this.player = player;
        this.inventory = inventory;
        this.guiId = guiId;
        this.rocketEntityId = rocketEntityId;
        this.modules = modules;
    }

    @Override
    public void initGui() {
        // LV does not rebuild module positions when the display scale changes.
        if (this.width > 0 && this.height > 0) rebuildModules(false);
        buttonList.clear();
        super.initGui();
    }

    private void rebuildModules(boolean preserveNameEditor) {
        int scrollY = 0;
        ModuleTextBox nameEditor = null;
        for (ModuleBase module : modules) {
            if (module instanceof ModuleStationSelectorList)
                scrollY = ((ModuleStationSelectorList) module).getScrollY();
            if (preserveNameEditor && module instanceof ModuleTextBox)
                nameEditor = (ModuleTextBox) module;
        }

        modules.clear();
        modules.addAll(inventory.getModules(guiId, player));
        if (nameEditor != null) {
            // Keep focus, cursor and selection while a delayed response updates the list.
            for (int i = 0; i < modules.size(); i++) {
                if (modules.get(i) instanceof ModuleTextBox) {
                    modules.set(i, nameEditor);
                    break;
                }
            }
        }
        for (ModuleBase module : modules)
            if (module instanceof ModuleStationSelectorList) {
                ModuleStationSelectorList list = (ModuleStationSelectorList) module;
                list.setOffset2(Math.min(-scrollY, list.getMaxScroll()));
            }
    }

    /** Apply an authoritative chip update without closing this screen or moving the cursor. */
    public void refreshChip() {
        if (!(inventory instanceof ItemStationChip)) return;
        rebuildModules(true);
        buttonList.clear();
        super.initGui();
        chipActionPending = false;
    }

    /** Serialize handheld actions until their matching server response updates this screen. */
    public boolean beginChipAction() {
        if (!isHandheld() || chipActionPending) return false;
        chipActionPending = true;
        return true;
    }

    public boolean isHandheld() {
        return inventory instanceof ItemStationChip;
    }

    // LV's packet handlers already execute these callbacks on the client game thread.
    public static void onStationUpdated(int stationId) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiStationSelector)
            ((GuiStationSelector) Minecraft.getMinecraft().currentScreen).refreshStation(stationId);
    }

    public static void onSelectionUpdated(EntityRocket rocket) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiStationSelector) {
            GuiStationSelector gui = (GuiStationSelector) Minecraft.getMinecraft().currentScreen;
            if (gui.inventory == rocket) gui.refreshRocketSelection();
        }
    }

    private void refreshRocketSelection() {
        if (!(inventory instanceof EntityRocket)) return;
        EntityRocket rocket = (EntityRocket) inventory;
        if (rocket.storage == null || rocket.storage.getGuidanceComputer() == null) return;
        ItemStack chip = rocket.storage.getGuidanceComputer().getStackInSlot(0);
        if (!chip.isEmpty() && chip.getItem() instanceof ItemStationChip)
            refreshStation(ItemStationChip.getUUID(chip));
    }

    /** Reconcile packet updates and integrated-server changes on the client game thread. */
    private void refreshStation(int updatedStationId) {
        if (!(inventory instanceof EntityRocket)) return;
        EntityRocket rocket = (EntityRocket) inventory;
        if (rocket.isDead || rocket.storage == null || rocket.storage.getGuidanceComputer() == null) return;

        ItemStack chip = rocket.storage.getGuidanceComputer().getStackInSlot(0);
        if (chip.isEmpty() || !(chip.getItem() instanceof ItemStationChip)) return;
        int stationId = ItemStationChip.getUUID(chip);
        if (stationId != updatedStationId) return;
        ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStation(stationId);
        SpaceStationObject station = object instanceof SpaceStationObject
                ? (SpaceStationObject) object : null;
        long fingerprint = StationSelectorLayout.fingerprint(station);
        StationLandingLocation selected = station == null ? null
                : rocket.storage.getGuidanceComputer().getLandingLocation(stationId);
        BlockPos selectedPosition = selected == null ? null : selected.getPos().getBlockPos();
        if (fingerprint != lastPadFingerprint || !Objects.equals(selectedPosition, lastSelectedPosition)) {
            lastPadFingerprint = fingerprint;
            lastSelectedPosition = selectedPosition;
            rebuildModules(false);
            buttonList.clear();
            super.initGui();
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (!(inventory instanceof EntityRocket) || ++refreshTicks < 20) return;
        refreshTicks = 0;
        EntityRocket rocket = (EntityRocket) inventory;
        if (rocket.isDead || rocket.storage == null || rocket.storage.getGuidanceComputer() == null) return;
        ItemStack chip = rocket.storage.getGuidanceComputer().getStackInSlot(0);
        if (chip.isEmpty() || !(chip.getItem() instanceof ItemStationChip)) return;
        int stationId = ItemStationChip.getUUID(chip);
        // Singleplayer shares the station registry, so a change may not produce a packet.
        refreshStation(stationId);
        PacketHandler.sendToServer(new PacketStationSelectorRefresh(
                inventorySlots.windowId, rocketEntityId, stationId, lastPadFingerprint));
    }
}
