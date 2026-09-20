package zmaster587.advancedRocketry.integration.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import zmaster587.advancedRocketry.dimension.DimensionManager;
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
        minecraft.currentScreen.drawTexturedModalRect(POWER_BAR_X + 1, POWER_BAR_Y + 1, 0, 171, 6, 38);
    }

    public static void drawBurnTime(Minecraft minecraft, int burnTime) {
        FontRenderer fontRenderer = minecraft.fontRenderer;
        String text = formatTime(burnTime);
        int x = PROGRESS_X + 5 - fontRenderer.getStringWidth(text) / 2;
        fontRenderer.drawString(text, x, STATS_Y, 0x404040);
    }

    public static void drawTotalPower(Minecraft minecraft, long totalPower) {
        String text = I18n.format("jei.powergeneration.total", formatCompactNumber(totalPower));
        minecraft.fontRenderer.drawString(text, LEFT_TEXT_X, STATS_Y, 0x404040);
    }
    public static void drawEarthAndMax(Minecraft minecraft, long earthPower, long maxPower) {
        FontRenderer fontRenderer = minecraft.fontRenderer;
        String overworldName = DimensionManager.getInstance().getDimensionProperties(0).getName();

        String earthText = I18n.format("jei.powergeneration.planetmax", overworldName, formatCompactNumber(earthPower));
        int earthY = (HEIGHT - fontRenderer.FONT_HEIGHT) / 2;
        fontRenderer.drawString(earthText, LEFT_TEXT_X, earthY, 0x404040);

        String maxText = I18n.format("jei.powergeneration.max", formatCompactNumber(maxPower));
        int x = WIDTH - fontRenderer.getStringWidth(maxText);
        fontRenderer.drawString(maxText, x, STATS_Y, 0x404040);
    }
    public static void drawPowerPerTick(Minecraft minecraft, long powerPerTick) {
        FontRenderer fontRenderer = minecraft.fontRenderer;
        String text = I18n.format("jei.powergeneration.output", formatCompactNumber(powerPerTick));
        int x = WIDTH - fontRenderer.getStringWidth(text);
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

    public static String formatCompactNumber(long value) {
        if (Math.abs(value) < 1000) {
            return Long.toString(value);
        }

        String[] suffixes = {"", "K", "M", "B", "T"};
        double scaled = value;
        int suffix = 0;

        while (Math.abs(scaled) >= 999.5d && suffix < suffixes.length - 1) {
            scaled /= 1000d;
            suffix++;
        }
        long roundedTenths = Math.round(scaled * 10d);
        if (roundedTenths % 10 == 0) {return (roundedTenths / 10) + suffixes[suffix];}
        return (roundedTenths / 10) + "." + Math.abs(roundedTenths % 10) + suffixes[suffix];
    }

    public static boolean isMouseOverMax(Minecraft minecraft, int mouseX, int mouseY, long maxPower) {
        FontRenderer fontRenderer = minecraft.fontRenderer;
        String maxText = I18n.format("jei.powergeneration.max", formatCompactNumber(maxPower));
        int x = WIDTH - fontRenderer.getStringWidth(maxText);

        return mouseX >= x && mouseX < WIDTH
                && mouseY >= STATS_Y
                && mouseY < STATS_Y + fontRenderer.FONT_HEIGHT;
    }
}