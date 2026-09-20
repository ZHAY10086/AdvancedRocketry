package zmaster587.advancedRocketry.integration.jei.solarGenerator;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.integration.jei.PowerGenerationJeiHelper;
import zmaster587.advancedRocketry.tile.TileSolarPanel;

import java.util.Collections;
import java.util.List;

public class SolarGeneratorWrapper implements IRecipeWrapper {

    @Override
    public void getIngredients(IIngredients ingredients) {}

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        int earthOutput = TileSolarPanel.getPowerForInsolation(DimensionManager.overworldProperties.getPeakInsolationMultiplier());

        PowerGenerationJeiHelper.drawEarthAndMax(minecraft, earthOutput, TileSolarPanel.MAX_POWER_PER_TICK);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (PowerGenerationJeiHelper.isMouseOverMax(minecraft, mouseX, mouseY, TileSolarPanel.MAX_POWER_PER_TICK)) {
            return Collections.singletonList(I18n.format("jei.powergeneration.solar.max.tooltip"));
        }

        return Collections.emptyList();
    }
}