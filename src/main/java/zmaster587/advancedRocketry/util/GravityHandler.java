package zmaster587.advancedRocketry.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.IGravityManager;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;

import java.util.WeakHashMap;

public class GravityHandler implements IGravityManager {

    public static final float LIVING_OFFSET = 0.0755f;
    public static final float FLUID_LIVING_OFFSET = 0.02f;
    public static final float THROWABLE_OFFSET = 0.03f;
    public static final float OTHER_OFFSET = 0.04f;
    public static final float ARROW_OFFSET = 0.05f;
    private static final WeakHashMap<Entity, Double> entityMap = new WeakHashMap<>();

    static {
        AdvancedRocketryAPI.gravityManager = new GravityHandler();
    }

    public static void applyGravity(Entity entity) {
        if (entity.hasNoGravity()) return;
        //Because working gravity on elytra-flying players can cause..... severe problems at lower gravity, it is my utter delight to announce to you elytra are now magic!
        //This totally isn't because Mojang decided for some godforsaken @#@#@#% reason to make ALL WAYS TO SET ELYTRA FLIGHT _protected_
        //With no set methods
        //So I cannot, without much more effort than it's worth, set elytra flight. Therefore, they're magic.
        if ((!(entity instanceof EntityPlayer) && !(entity instanceof EntityFlying)) || (!(entity instanceof EntityFlying) && !(((EntityPlayer) entity).capabilities.isFlying || ((EntityLivingBase) entity).isElytraFlying()))) {
            Double d = entityMap.get(entity);
            if (d != null) {

                double multiplier = (isOtherEntity(entity) || entity instanceof EntityItem) ? OTHER_OFFSET * d : (entity instanceof EntityArrow) ? ARROW_OFFSET * d : (entity instanceof EntityThrowable) ? THROWABLE_OFFSET * d : LIVING_OFFSET * d;

                entity.motionY += multiplier;

            } else if (DimensionManager.getInstance().isDimensionCreated(entity.world.provider.getDimension()) || entity.world.provider instanceof WorldProviderSpace) {
                double gravMult;

                if (entity.world.provider instanceof IPlanetaryProvider)
                    gravMult = ((IPlanetaryProvider) entity.world.provider).getGravitationalMultiplier(entity.getPosition());
                else
                    gravMult = DimensionManager.getInstance().getDimensionProperties(entity.world.provider.getDimension()).gravitationalMultiplier;

                if (entity instanceof EntityItem)
                    entity.motionY -= (gravMult * OTHER_OFFSET - OTHER_OFFSET);
                else if (isOtherEntity(entity))
                    entity.motionY -= (gravMult * OTHER_OFFSET - OTHER_OFFSET);
                else if (entity instanceof EntityThrowable)
                    entity.motionY -= (gravMult * THROWABLE_OFFSET - THROWABLE_OFFSET);
                else if (entity instanceof EntityArrow)
                    entity.motionY -= (gravMult * ARROW_OFFSET - ARROW_OFFSET);
                else if (entity instanceof EntityLivingBase && (entity.isInWater() || entity.isInLava()))
                    entity.motionY -= (gravMult * FLUID_LIVING_OFFSET - FLUID_LIVING_OFFSET);
                else if (entity instanceof EntityLivingBase)
                    entity.motionY -= (gravMult * LIVING_OFFSET - LIVING_OFFSET);
            }
        }
    }

    public static boolean isOtherEntity(Entity entity) {
        return entity instanceof EntityBoat || entity instanceof EntityMinecart || entity instanceof EntityFallingBlock || entity instanceof EntityTNTPrimed;
    }

    @Override
    public void setGravityMultiplier(Entity entity, double multiplier) {
        //TODO: packet handling
        entityMap.put(entity, multiplier);
    }

    @Override
    public void clearGravityEffect(Entity entity) {
        entityMap.remove(entity);
    }
}
