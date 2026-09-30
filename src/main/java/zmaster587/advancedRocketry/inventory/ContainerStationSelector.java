package zmaster587.advancedRocketry.inventory;

import net.minecraft.entity.player.EntityPlayer;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;

import java.util.List;

public class ContainerStationSelector extends ContainerModular {
    private final int rocketEntityId;

    public ContainerStationSelector(EntityPlayer player, List<ModuleBase> modules,
                                    IModularInventory inventory, int rocketEntityId) {
        super(player, modules, inventory, false, false);
        this.rocketEntityId = rocketEntityId;
    }

    public int getRocketEntityId() {
        return rocketEntityId;
    }
}
