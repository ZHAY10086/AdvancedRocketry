package zmaster587.advancedRocketry.integration.jei.blackHoleGenerator;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.integration.jei.PowerGenerationJeiHelper;

public class BlackHoleGeneratorWrapper implements IRecipeWrapper {

    private final ItemStack input;
    private final int burnTime;
    private final int defaultBurnTime;
    private final boolean infoOnly;
    private final int powerPerTick;

    public BlackHoleGeneratorWrapper(ItemStack input, int burnTime, int defaultBurnTime, boolean infoOnly, int powerPerTick) {
        this.input = input;
        this.burnTime = burnTime;
        this.defaultBurnTime = defaultBurnTime;
        this.infoOnly = infoOnly;
        this.powerPerTick = powerPerTick;
    }

    public ItemStack getInput() {return input;}
    public boolean isInfoOnly() {return infoOnly;}

    @Override
    public void getIngredients(IIngredients ingredients) {
        if (!infoOnly && !input.isEmpty()) {
            ingredients.setInput(ItemStack.class, input);
        }
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        int effectiveBurnTime = infoOnly ? defaultBurnTime : burnTime;

        if (infoOnly) {
            minecraft.fontRenderer.drawString(I18n.format("jei.powergeneration.anyother"), PowerGenerationJeiHelper.FUEL_X, PowerGenerationJeiHelper.INPUT_LABEL_Y, 0x404040);
        }

        PowerGenerationJeiHelper.drawTotalPower(minecraft, (long) powerPerTick * effectiveBurnTime);
        PowerGenerationJeiHelper.drawBurnTime(minecraft, effectiveBurnTime);
        PowerGenerationJeiHelper.drawPowerPerTick(minecraft, powerPerTick);
    }
}