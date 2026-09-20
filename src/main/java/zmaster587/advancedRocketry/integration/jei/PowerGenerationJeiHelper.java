package zmaster587.advancedRocketry.integration.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import zmaster587.libVulpes.gui.CommonResources;

public final class PowerGenerationJeiHelper {

    public static final int WIDTH = 163;
    public static final int HEIGHT = 55;

    public static final int FUEL_X = 8;
    public static final int FUEL_Y = 11;

    public static final int PROGRESS_X = 72;
    public static final int PROGRESS_Y = 12;

    public static final int POWER_BAR_X = 151;
    public static final int POWER_BAR_Y = 1;

    public static final int LEFT_TEXT_X = 0;
    public static final int INPUT_LABEL_Y = 0;
    public static final int STATS_Y = 44;

    private PowerGenerationJeiHelper() {}

    public static void drawPowerBar(Minecraft minecraft) {
        minecraft.getTextureManager().bindTexture(CommonResources.genericBackground);
        minecraft.currentScreen.drawTexturedModalRect(POWER_BAR_X, POWER_BAR_Y, 176, 18, 8, 40);
        minecraft.currentScreen.drawTexturedModalRect(POWER_BAR_X + 2, POWER_BAR_Y + 43, 15, 171, 4, 9);
        minecraft.currentScreen.drawTexturedModalRect(POWER_BAR_X + 1, POWER_BAR_Y + 1, 0, 171, 6, 38);
    }

    public static void drawBurnTime(Minecraft minecraft, int burnTime) {
        FontRenderer fontRenderer = minecraft.fontRenderer;
        String text = formatTime(burnTime);
        int x = PROGRESS_X + 5 - fontRenderer.getStringWidth(text) / 2;
        fontRenderer.drawString(text, x, STATS_Y, 0x404040);
    }

    public static void drawTotalPower(Minecraft minecraft, long totalPower) {
        String text = I18n.format("jei.powergeneration.total", totalPower);
        minecraft.fontRenderer.drawString(text, LEFT_TEXT_X, STATS_Y, 0x404040);
    }

    public static void drawPowerPerTick(Minecraft minecraft, long powerPerTick) {
        FontRenderer fontRenderer = minecraft.fontRenderer;
        String text = I18n.format("jei.powergeneration.output", powerPerTick);
        int x = POWER_BAR_X - 4 - fontRenderer.getStringWidth(text);
        fontRenderer.drawString(text, x, STATS_Y, 0x404040);
    }

    public static String formatTime(int ticks) {
        if (ticks < 20) {
            return I18n.format(ticks == 1 ? "jei.powergeneration.time.tick" : "jei.powergeneration.time.ticks", ticks);
        }

        if (ticks % 20 == 0) {
            return I18n.format("jei.powergeneration.time.seconds", ticks / 20);
        }

        return I18n.format("jei.powergeneration.time.ticks", ticks);
    }
}