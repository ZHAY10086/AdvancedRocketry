package zmaster587.advancedRocketry.integration.jei.blackHoleGenerator;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.api.ARConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlackHoleGeneratorRecipeMaker {

    private static final int BASE_POWER_PER_TICK = 500;
    private BlackHoleGeneratorRecipeMaker() {}

    public static List<BlackHoleGeneratorWrapper> getRecipes() {
        ARConfiguration config = ARConfiguration.getCurrentConfig();
        int powerPerTick = (int) (BASE_POWER_PER_TICK * config.blackHolePowerMultiplier);
        List<Map.Entry<ItemStack, Integer>> entries = new ArrayList<>(config.blackHoleGeneratorBlocks.entrySet());

        // The runtime table is a HashMap. Give JEI a deterministic
        // presentation order without changing gameplay semantics.
        entries.sort((left, right) -> {
            int registryCompare = registryName(left.getKey())
                    .compareTo(registryName(right.getKey()));

            if (registryCompare != 0) {
                return registryCompare;
            }

            return Integer.compare(
                    left.getKey().getMetadata(),
                    right.getKey().getMetadata()
            );
        });

        List<BlackHoleGeneratorWrapper> recipes = new ArrayList<>(entries.size() + 1);
        for (Map.Entry<ItemStack, Integer> entry : entries) {
            recipes.add(new BlackHoleGeneratorWrapper(entry.getKey().copy(), entry.getValue(), config.defaultItemTimeBlackHole, false, powerPerTick));
        }
        if (config.defaultItemTimeBlackHole > 0) {
            recipes.add(new BlackHoleGeneratorWrapper(ItemStack.EMPTY, 0, config.defaultItemTimeBlackHole, true, powerPerTick));
        }
        return recipes;
    }

    private static String registryName(ItemStack stack) {
        ResourceLocation name = stack.getItem().getRegistryName();

        return name == null ? "" : name.toString();
    }
}