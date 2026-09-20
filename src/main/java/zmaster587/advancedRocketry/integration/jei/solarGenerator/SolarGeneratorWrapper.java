package zmaster587.advancedRocketry.integration.jei.solarGenerator;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.integration.jei.PowerGenerationJeiHelper;
import zmaster587.advancedRocketry.tile.TileSolarPanel;

public class SolarGeneratorWrapper implements IRecipeWrapper {

    @Override
    public void getIngredients(IIngredients ingredients) {}

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        int earthOutput = TileSolarPanel.getPowerForInsolation(DimensionManager.overworldProperties.getPeakInsolationMultiplier());

        PowerGenerationJeiHelper.drawEarthAndMax(minecraft, earthOutput, TileSolarPanel.MAX_POWER_PER_TICK);
    }
}