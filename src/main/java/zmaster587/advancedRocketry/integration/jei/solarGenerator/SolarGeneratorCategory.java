package zmaster587.advancedRocketry.integration.jei.solarGenerator;

import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.integration.jei.PowerGenerationCategoryTemplate;

public class SolarGeneratorCategory extends PowerGenerationCategoryTemplate<SolarGeneratorWrapper> {

    public static final String UID = "zmaster587.AR.solarGenerator";

    public SolarGeneratorCategory(IGuiHelper guiHelper) {
        super(guiHelper, false, null);
    }

    @Override
    public String getUid() {return UID;}

    @Override
    public String getTitle() {return new ItemStack(AdvancedRocketryBlocks.blockSolarGenerator).getDisplayName();}

    @Override
    public String getModName() {return "Advanced Rocketry";}
}