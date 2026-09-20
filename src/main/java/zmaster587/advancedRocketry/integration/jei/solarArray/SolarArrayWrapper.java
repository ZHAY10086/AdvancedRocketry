package zmaster587.advancedRocketry.integration.jei.solarArray;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.integration.jei.PowerGenerationJeiHelper;
import zmaster587.advancedRocketry.tile.TileSolarPanel;

import java.util.Collections;
import java.util.List;

public class SolarArrayWrapper implements IRecipeWrapper {

    @Override
    public void getIngredients(IIngredients ingredients) {}

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        ARConfiguration config = ARConfiguration.getCurrentConfig();

        PowerGenerationJeiHelper.drawEarthAndMax(minecraft, config.solarArrayEarthOutput, config.solarArrayMaxOutput);
    }
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getMinecraft();
        long maxPower = ARConfiguration.getCurrentConfig().solarArrayMaxOutput;

        if (PowerGenerationJeiHelper.isMouseOverMax(minecraft, mouseX, mouseY, maxPower)) {
            return Collections.singletonList(I18n.format("jei.powergeneration.solar.max.tooltip"));
        }

        return Collections.emptyList();
    }
}