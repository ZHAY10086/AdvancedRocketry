package zmaster587.advancedRocketry.integration.jei.solarArray;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.integration.jei.PowerGenerationJeiHelper;

public class SolarArrayWrapper implements IRecipeWrapper {

    @Override
    public void getIngredients(IIngredients ingredients) {}

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        ARConfiguration config = ARConfiguration.getCurrentConfig();

        PowerGenerationJeiHelper.drawEarthAndMax(minecraft, config.solarArrayEarthOutput, config.solarArrayMaxOutput);
    }
}