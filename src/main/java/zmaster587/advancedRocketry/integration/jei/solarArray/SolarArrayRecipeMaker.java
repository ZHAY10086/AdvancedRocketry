package zmaster587.advancedRocketry.integration.jei.solarArray;

import java.util.Collections;
import java.util.List;

public class SolarArrayRecipeMaker {

    private SolarArrayRecipeMaker() {}

    public static List<SolarArrayWrapper> getRecipes() {
        return Collections.singletonList(new SolarArrayWrapper());
    }
}