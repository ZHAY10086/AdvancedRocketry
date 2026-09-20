package zmaster587.advancedRocketry.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.client.util.ProgressBarImage;

import java.util.List;

public abstract class PowerGenerationCategoryTemplate<T extends IRecipeWrapper> implements IRecipeCategory<T> {

    private final IDrawable background;
    private final IDrawable slot;
    private final boolean showInput;
    private final ProgressBarImage generationProgress;

    protected PowerGenerationCategoryTemplate(IGuiHelper helper, boolean showInput, ProgressBarImage generationProgress) {
        background = helper.createBlankDrawable(PowerGenerationJeiHelper.WIDTH, PowerGenerationJeiHelper.HEIGHT);
        slot = helper.getSlotDrawable();
        this.showInput = showInput;
        this.generationProgress = generationProgress;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        if (showInput) {
            slot.draw(minecraft, PowerGenerationJeiHelper.FUEL_X, PowerGenerationJeiHelper.FUEL_Y);
        }

        minecraft.getTextureManager().bindTexture(TextureResources.progressBars);
        generationProgress.renderProgressBar(PowerGenerationJeiHelper.PROGRESS_X, PowerGenerationJeiHelper.PROGRESS_Y, (Minecraft.getSystemTime() % 3000) / 3000f, minecraft.currentScreen);

        PowerGenerationJeiHelper.drawPowerBar(minecraft);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, T recipeWrapper, IIngredients ingredients) {
        if (!showInput) {
            return;
        }

        List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
        if (!inputs.isEmpty() && !inputs.get(0).isEmpty()) {
            IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
            itemStacks.init(0, true, PowerGenerationJeiHelper.FUEL_X, PowerGenerationJeiHelper.FUEL_Y);
            itemStacks.set(0, inputs.get(0));
        }
    }
}