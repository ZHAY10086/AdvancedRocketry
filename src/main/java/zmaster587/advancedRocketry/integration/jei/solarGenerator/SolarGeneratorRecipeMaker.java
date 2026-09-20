package zmaster587.advancedRocketry.integration.jei.solarGenerator;

import java.util.Collections;
import java.util.List;

public class SolarGeneratorRecipeMaker {

    private SolarGeneratorRecipeMaker() {}

    public static List<SolarGeneratorWrapper> getRecipes() {
        return Collections.singletonList(new SolarGeneratorWrapper());
    }
}