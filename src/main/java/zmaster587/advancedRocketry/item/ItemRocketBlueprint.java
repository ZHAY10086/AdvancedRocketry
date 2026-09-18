package zmaster587.advancedRocketry.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.util.RocketBlueprint;
import javax.annotation.Nullable;
import java.util.List;

public class ItemRocketBlueprint extends Item {
    public ItemRocketBlueprint() { setMaxStackSize(1); }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        RocketBlueprint blueprint = RocketBlueprint.read(stack);
        if (blueprint != null) {
            tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.rocketblueprint.design", stack.hasDisplayName() ? stack.getDisplayName() : I18n.format("tooltip.rocketblueprint.rocket")));
            tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.rocketblueprint.size", blueprint.sizeX, blueprint.sizeY, blueprint.sizeZ));
            tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.rocketblueprint.blocks", blueprint.cells.length / 2));
        } else if (stack.hasTagCompound() && stack.getTagCompound().hasKey("RocketBlueprint")) {
            tooltip.add(TextFormatting.RED + I18n.format("tooltip.rocketblueprint.invalid"));
        } else {
            tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.rocketblueprint.blank"));
        }
    }
}
