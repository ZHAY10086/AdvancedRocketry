package zmaster587.advancedRocketry.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.util.StationLandingLocation;

/** Coordinates shared by the two existing modular station selectors. */
public final class StationSelectorLayout {
    public final int x;
    public final int y;
    public final int width;
    public final int listHeight;

    private StationSelectorLayout(int x, int y, int width, int listHeight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.listHeight = listHeight;
    }

    public static StationSelectorLayout forPlayer(EntityPlayer player, boolean chipInHand) {
        int screenWidth = 336;
        int screenHeight = 240;
        if (player.world.isRemote) {
            ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
            screenWidth = resolution.getScaledWidth();
            screenHeight = resolution.getScaledHeight();
        }
        int width = Math.min(360, Math.max(120, screenWidth - 16));
        int x = (screenWidth - width) / 2;
        int y = Math.max(8, (screenHeight - 220) / 2);
        int listHeight = 16 + Math.max(45, Math.min(chipInHand ? 116 : 152,
                screenHeight - y - (chipInHand ? 96 : 67)));
        return new StationSelectorLayout(x, y, width, listHeight);
    }

    /** The client compares this with the server once per second while the rocket screen is open. */
    public static long fingerprint(SpaceStationObject station) {
        if (station == null) return 0L;
        long hash = 0xcbf29ce484222325L;
        for (StationLandingLocation pad : station.getLandingPads()) {
            hash = (hash ^ pad.getPos().x) * 0x100000001b3L;
            hash = (hash ^ pad.getPos().z) * 0x100000001b3L;
            hash = (hash ^ (pad.getOccupied() ? 1 : 0)) * 0x100000001b3L;
            String name = pad.getName();
            hash = (hash ^ (name == null ? -1 : name.length())) * 0x100000001b3L;
            if (name != null) {
                for (int i = 0; i < name.length(); i++)
                    hash = (hash ^ name.charAt(i)) * 0x100000001b3L;
            }
        }
        return hash;
    }
}
