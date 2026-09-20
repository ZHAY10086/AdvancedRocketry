package zmaster587.advancedRocketry.integration.jei.blackHoleGenerator;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class BlackHoleGeneratorRecipeHandler implements IRecipeHandler<BlackHoleGeneratorWrapper> {

    @Override
    public Class<BlackHoleGeneratorWrapper> getRecipeClass() {
        return BlackHoleGeneratorWrapper.class;
    }

    @Override
    public String getRecipeCategoryUid(BlackHoleGeneratorWrapper recipe) {
        return BlackHoleGeneratorCategory.UID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(BlackHoleGeneratorWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(BlackHoleGeneratorWrapper recipe) {
        return true;
    }
}