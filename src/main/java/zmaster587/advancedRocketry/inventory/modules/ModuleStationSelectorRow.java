package zmaster587.advancedRocketry.inventory.modules;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.advancedRocketry.inventory.TextureResources;

/** An LV button with three separately aligned pieces of destination information. */
public class ModuleStationSelectorRow extends ModuleStationSelectorActionButton {
    private final String name;
    private final String coordinates;
    private final String status;
    private final boolean selected;
    private final boolean occupied;

    public ModuleStationSelectorRow(int y, int buttonId, int width, String name, String coordinates,
                                    String status, boolean selected, boolean occupied, IButtonInventory owner) {
        super(0, y, buttonId, "", owner, TextureResources.buttonGeneric, width, 22);
        this.name = name == null || name.isEmpty()
                ? LibVulpes.proxy.getLocalizedString("msg.stationselector.unnamed") : name;
        this.coordinates = coordinates;
        this.status = status;
        this.selected = selected;
        this.occupied = occupied;
        setToolTipText(this.name + (coordinates.isEmpty() ? "" : "\n" + coordinates)
                + "\n" + status);
        setEnabled(!occupied);
    }

    @Override
    public void renderForeground(int guiOffsetX, int guiOffsetY, int mouseX, int mouseY,
                                 float zLevel, GuiContainer gui, FontRenderer font) {
        int firstWidth = Math.max(56, sizeX * 41 / 100 - 10);
        int coordinateX = offsetX + sizeX * 41 / 100;
        int statusX = offsetX + sizeX * 73 / 100;
        int color = occupied ? 0xA8A8A8 : selected ? 0xFFFF55 : 0xF0F0F0;
        font.drawString(font.trimStringToWidth(name, firstWidth), offsetX + 7, offsetY + 7, color);
        font.drawString(font.trimStringToWidth(coordinates, statusX - coordinateX - 5),
                coordinateX, offsetY + 7, 0xD5DCE5);
        int statusWidth = sizeX - (statusX - offsetX) - 4;
        font.drawString(font.trimStringToWidth(status, statusWidth), statusX,
                offsetY + (selected && occupied ? 2 : 7),
                occupied ? 0xFF8888 : selected ? 0xFFFF55 : 0xD5DCE5);
        if (selected && occupied) {
            font.drawString(font.trimStringToWidth(
                    LibVulpes.proxy.getLocalizedString("msg.stationselector.occupied"), statusWidth),
                    statusX, offsetY + 12, 0xFF8888);
        }
    }
}
