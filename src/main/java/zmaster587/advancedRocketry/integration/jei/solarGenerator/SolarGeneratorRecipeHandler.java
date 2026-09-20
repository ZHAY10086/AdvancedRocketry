package zmaster587.advancedRocketry.integration.jei.solarGenerator;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class SolarGeneratorRecipeHandler implements IRecipeHandler<SolarGeneratorWrapper> {

    @Override
    public Class<SolarGeneratorWrapper> getRecipeClass() {
        return SolarGeneratorWrapper.class;
    }

    @Override
    public String getRecipeCategoryUid(SolarGeneratorWrapper recipe) {
        return SolarGeneratorCategory.UID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(SolarGeneratorWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(SolarGeneratorWrapper recipe) {
        return true;
    }
}