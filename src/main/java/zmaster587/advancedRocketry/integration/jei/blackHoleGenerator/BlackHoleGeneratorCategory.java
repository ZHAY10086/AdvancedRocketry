package zmaster587.advancedRocketry.integration.jei.blackHoleGenerator;

import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.integration.jei.PowerGenerationCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;

public class BlackHoleGeneratorCategory extends PowerGenerationCategoryTemplate<BlackHoleGeneratorWrapper> {

    public static final String UID = "zmaster587.AR.blackHoleGenerator";

    public BlackHoleGeneratorCategory(IGuiHelper guiHelper) {
        super(guiHelper, true, TextureResources.blackHoleGeneratorProgressBar);
    }

    @Override
    public String getUid() {return UID;}

    @Override
    public String getTitle() {return new ItemStack(AdvancedRocketryBlocks.blockBlackHoleGenerator).getDisplayName();}

    @Override
    public String getModName() {return "Advanced Rocketry";}
}