package zmaster587.advancedRocketry.integration.jei.solarArray;

import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.integration.jei.PowerGenerationCategoryTemplate;

public class SolarArrayCategory extends PowerGenerationCategoryTemplate<SolarArrayWrapper> {

    public static final String UID = "zmaster587.AR.solarArray";

    public SolarArrayCategory(IGuiHelper guiHelper) {
        super(guiHelper, false, null);
    }

    @Override
    public String getUid() {return UID;}

    @Override
    public String getTitle() {return new ItemStack(AdvancedRocketryBlocks.blockSolarArray).getDisplayName();}

    @Override
    public String getModName() {return "Advanced Rocketry";}
}