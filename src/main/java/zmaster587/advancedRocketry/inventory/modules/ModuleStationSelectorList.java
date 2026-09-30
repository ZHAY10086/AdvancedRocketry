package zmaster587.advancedRocketry.inventory.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.input.Mouse;
import zmaster587.libVulpes.inventory.GuiModular;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleContainerPanYOnly;

import java.util.Collections;
import java.util.List;

/** LV's vertical list with bounds corrected for a centered full-screen module. */
public class ModuleStationSelectorList extends ModuleContainerPanYOnly {
    private final int maxScroll;

    public ModuleStationSelectorList(int x, int y, int width, int height, List<ModuleBase> rows) {
        super(x, y, rows, Collections.emptyList(), null, width, height, 0, 0,
                width, Math.max(1, rows.size() * 22 - height));
        this.maxScroll = Math.max(0, rows.size() * 22 - height);
    }

    public int getMaxScroll() {
        return maxScroll;
    }

    @Override
    protected void setUpScissor(GuiContainer gui, int x, int y, int width, int height) {
        super.setUpScissor(gui, x, y, width - offsetX, height - offsetY);
    }

    private boolean inside(int x, int y) {
        return x >= offsetX && x < offsetX + screenSizeX
                && y >= offsetY && y < offsetY + screenSizeY;
    }

    private boolean mouseInside() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaled = new ScaledResolution(mc);
        int x = Mouse.getX() * scaled.getScaledWidth() / mc.displayWidth;
        int y = scaled.getScaledHeight() - Mouse.getY() * scaled.getScaledHeight() / mc.displayHeight - 1;
        return inside(x, y);
    }

    @Override
    public void onScroll(int amount) {
        if (mouseInside()) {
            super.onScroll(amount);
            if (currentPosY < -maxScroll)
                moveContainerInterior(-maxScroll - currentPosY);
        }
    }

    @Override
    public void onMouseClicked(GuiModular gui, int x, int y, int button) {
        if (inside(x, y)) super.onMouseClicked(gui, x, y, button);
    }

    @Override
    public void onMouseClickedAndDragged(int x, int y, int button, long elapsed) {
        if (inside(x, y)) {
            super.onMouseClickedAndDragged(x, y, button, elapsed);
            if (currentPosY < -maxScroll)
                moveContainerInterior(-maxScroll - currentPosY);
        }
    }

    @Override
    public void renderToolTip(int guiOffsetX, int guiOffsetY, int mouseX, int mouseY,
                              float zLevel, GuiContainer gui, FontRenderer font) {
        if (inside(mouseX, mouseY))
            super.renderToolTip(guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui, font);
    }
}
