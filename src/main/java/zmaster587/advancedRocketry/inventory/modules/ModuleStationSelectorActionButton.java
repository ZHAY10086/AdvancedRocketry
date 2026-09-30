package zmaster587.advancedRocketry.inventory.modules;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.ModuleButton;

import java.util.List;

/** Propagate LV's stored enabled state to the created GUI button. */
public class ModuleStationSelectorActionButton extends ModuleButton {
    public ModuleStationSelectorActionButton(int x, int y, int id, String label,
                                             IButtonInventory owner, ResourceLocation[] texture,
                                             int width, int height) {
        super(x, y, id, label, owner, texture, width, height);
    }

    @Override
    public List<GuiButton> addButtons(int x, int y) {
        List<GuiButton> buttons = super.addButtons(x, y);
        button.enabled = isEnabled();
        return buttons;
    }
}
