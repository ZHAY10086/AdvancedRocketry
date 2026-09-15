package zmaster587.advancedRocketry.inventory.modules;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.libVulpes.inventory.modules.ModuleText;

import java.util.Arrays;
import java.util.List;

public class ModuleTextTooltip extends ModuleText {

    private final List<String> tooltip;

    public ModuleTextTooltip(int offsetX, int offsetY, String text, int color, String... tooltip) {
        super(offsetX, offsetY, text, color);
        this.tooltip = Arrays.asList(tooltip);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderToolTip(int guiOffsetX, int guiOffsetY, int mouseX, int mouseY,
                              float zLevel, GuiContainer gui, FontRenderer font) {
        int relativeX = mouseX - offsetX;
        int relativeY = mouseY - offsetY;

        if (relativeX >= 0
                && relativeX <= font.getStringWidth(getText())
                && relativeY >= 0
                && relativeY <= font.FONT_HEIGHT) {
            drawTooltip(gui, tooltip, mouseX, mouseY, zLevel, font);
        }
    }
}