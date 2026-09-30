package zmaster587.advancedRocketry.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.satellite.SatelliteOreMapping;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.libVulpes.inventory.modules.IModularInventory;

import java.util.Collections;

public class GuiHandler implements IGuiHandler {

    //X coord is entity ID num if entity
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world,
                                      int x, int y, int z) {

        if (ID == guiId.StationChip.ordinal() || ID == guiId.StationLandingPad.ordinal()) {
            IModularInventory inventory = getStationInventory(ID, player, world, x);
            // No slots or module progress values: visual modules belong only on the client.
            return inventory == null ? null : new ContainerStationSelector(player,
                    Collections.emptyList(), inventory,
                    ID == guiId.StationLandingPad.ordinal() ? x : -1);
        }

        if (x == -1 && y < -1) {
            ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

            //If there is latency or some desync odd things can happen so check for that
            if (stack.isEmpty() || !(stack.getItem() instanceof IModularInventory)) {
                return null;
            }
        }

        if (ID == guiId.OreMappingSatellite.ordinal()) {
            SatelliteBase satellite = DimensionManager.getInstance().getSatellite(y);

            if (!(satellite instanceof SatelliteOreMapping) || satellite.getDimensionId() != world.provider.getDimension())
                satellite = null;

            return new ContainerOreMappingSatellite((SatelliteOreMapping) satellite, player.inventory);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world,
                                      int x, int y, int z) {
        if (ID == guiId.StationChip.ordinal() || ID == guiId.StationLandingPad.ordinal()) {
            IModularInventory inventory = getStationInventory(ID, player, world, x);
            return inventory == null ? null : new GuiStationSelector(player, inventory, ID, x);
        }


        if (x == -1 && y < -1) {
            ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

            //If there is latency or some desync odd things can happen so check for that
            if (stack.isEmpty() || !(stack.getItem() instanceof IModularInventory)) {
                return null;
            }
        }

        if (ID == guiId.OreMappingSatellite.ordinal()) {

            SatelliteBase satellite = DimensionManager.getInstance().getSatellite(y);

            if (!(satellite instanceof SatelliteOreMapping) || satellite.getDimensionId() != world.provider.getDimension())
                satellite = null;

            return new GuiOreMappingSatellite((SatelliteOreMapping) satellite, player);
        }
        return null;
    }

    private IModularInventory getStationInventory(int ID, EntityPlayer player, World world, int entityId) {
        if (ID == guiId.StationChip.ordinal()) {
            ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
            return !stack.isEmpty() && stack.getItem() instanceof ItemStationChip
                    ? (ItemStationChip) stack.getItem() : null;
        }
        if (ID == guiId.StationLandingPad.ordinal()) {
            Entity entity = world.getEntityByID(entityId);
            if (entity instanceof EntityRocket) {
                EntityRocket rocket = (EntityRocket) entity;
                if (rocket.storage != null && rocket.storage.getGuidanceComputer() != null) {
                    ItemStack chip = rocket.storage.getGuidanceComputer().getStackInSlot(0);
                    if (!chip.isEmpty() && chip.getItem() instanceof ItemStationChip
                            && ItemStationChip.getUUID(chip) != 0) return rocket;
                }
            }
        }
        return null;
    }

    public enum guiId {
        RocketBuilder,
        BlastFurnace,
        OreMappingSatellite,
        StationChip,
        StationLandingPad
    }
}
