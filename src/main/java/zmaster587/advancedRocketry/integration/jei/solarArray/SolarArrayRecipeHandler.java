package zmaster587.advancedRocketry.integration.jei.solarArray;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class SolarArrayRecipeHandler implements IRecipeHandler<SolarArrayWrapper> {

    @Override
    public Class<SolarArrayWrapper> getRecipeClass() {
        return SolarArrayWrapper.class;
    }

    @Override
    public String getRecipeCategoryUid(SolarArrayWrapper recipe) {
        return SolarArrayCategory.UID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(SolarArrayWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(SolarArrayWrapper recipe) {
        return true;
    }
}