package zmaster587.advancedRocketry.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.client.KeyBindings;
import zmaster587.advancedRocketry.client.TooltipInjector;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.render.RenderHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemRocketFinder extends Item implements IArmorComponent {
    private static final String TARGET = "rocketTarget";

    public ItemRocketFinder() {
        setMaxStackSize(1);
    }

    /** Called only by the existing passenger-transfer queue, after arrival. */
    public static void recordRocketArrival(Entity passenger, Entity mount) {
        if (!(passenger instanceof EntityPlayerMP) || !(mount instanceof EntityRocket)
                || passenger.isDead || mount.isDead || passenger.world.isRemote
                || passenger.world != mount.world) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) passenger;
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (recordTarget(helmet, mount.world.provider.getDimension(), mount.posX, mount.posY, mount.posZ)) {
            player.inventory.markDirty();
            // Direct inventory updates also reach the wearer while another container is open.
            player.connection.sendPacket(new SPacketSetSlot(-2,
                    player.inventory.mainInventory.size() + EntityEquipmentSlot.HEAD.getIndex(), helmet));
        }
    }

    static boolean recordTarget(ItemStack helmet, int dimension, double x, double y, double z) {
        if (helmet.isEmpty() || !(helmet.getItem() instanceof IModularArmor)
                || !Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            return false;
        }

        IModularArmor armor = (IModularArmor) helmet.getItem();
        IInventory modules = armor.loadModuleInventory(helmet);
        NBTTagCompound target = new NBTTagCompound();
        target.setInteger("dimid", dimension);
        target.setDouble("x", x);
        target.setDouble("y", y);
        target.setDouble("z", z);
        boolean changed = false;

        for (int slot = 0; slot < modules.getSizeInventory(); slot++) {
            ItemStack component = modules.getStackInSlot(slot);
            if (!component.isEmpty() && component.getItem() instanceof ItemRocketFinder
                    && !target.equals(component.getSubCompound(TARGET))) {
                component.setTagInfo(TARGET, target.copy());
                changed = true;
            }
        }

        if (changed) {
            armor.saveModuleInventory(helmet, modules);
        }
        return changed;
    }

    @Nullable
    static NBTTagCompound getTarget(ItemStack component) {
        NBTTagCompound target = component.getSubCompound(TARGET);
        if (target == null || !target.hasKey("dimid", NBT.TAG_INT)
                || !target.hasKey("x", NBT.TAG_DOUBLE) || !target.hasKey("y", NBT.TAG_DOUBLE)
                || !target.hasKey("z", NBT.TAG_DOUBLE)
                || !Double.isFinite(target.getDouble("x")) || !Double.isFinite(target.getDouble("y"))
                || !Double.isFinite(target.getDouble("z"))) {
            return null;
        }
        return target;
    }

    @Override
    public boolean isAllowedInSlot(@Nonnull ItemStack component, EntityEquipmentSlot slot) {
        return slot == EntityEquipmentSlot.HEAD;
    }

    @Override
    public void onTick(World world, EntityPlayer player, @Nonnull ItemStack armor,
                       IInventory modules, @Nonnull ItemStack component) {
    }

    @Override
    public boolean onComponentAdded(World world, @Nonnull ItemStack armor) {
        return true;
    }

    @Override
    public void onComponentRemoved(World world, @Nonnull ItemStack armor) {
    }

    @Override
    public void onArmorDamaged(EntityLivingBase entity, @Nonnull ItemStack armor,
                               @Nonnull ItemStack component, DamageSource source, int damage) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderScreen(@Nonnull ItemStack component, List<ItemStack> modules,
                             RenderGameOverlayEvent event, Gui gui) {
        Minecraft mc = Minecraft.getMinecraft();
        int stationDimension = ARConfiguration.getCurrentConfig().spaceDimId;
        if (mc.player == null || mc.world == null || mc.player.dimension == stationDimension
                || mc.player.getRidingEntity() instanceof EntityRocket) {
            return;
        }
        // Several installed modules retain their targets, but draw the finder HUD only once.
        for (ItemStack module : modules) {
            if (module.getItem() == this) {
                if (module != component) return;
                break;
            }
        }

        KeyBinding toggleBinding = KeyBindings.getToggleRocketFinder();
        if (mc.currentScreen == null && toggleBinding.getKeyCode() != Keyboard.KEY_NONE) {
            String hint = I18n.format("msg.rocketFinder.toggleHint", toggleBinding.getDisplayName());
            int hintY = event.getResolution().getScaledHeight() - 4 - mc.fontRenderer.FONT_HEIGHT;
            mc.fontRenderer.drawStringWithShadow(hint,
                    event.getResolution().getScaledWidth() - 4 - mc.fontRenderer.getStringWidth(hint),
                    hintY, 0xFFFFFF);
        }

        if (!KeyBindings.isRocketFinderEnabled()) {
            return;
        }
        NBTTagCompound target = getTarget(component);
        if (target != null && target.getInteger("dimid") == stationDimension) {
            return;
        }
        int screenX = event.getResolution().getScaledWidth() / 2;
        if (target == null) {
            gui.drawCenteredString(mc.fontRenderer, I18n.format("msg.rocketFinder.noTarget"), screenX, 32, 0xFFFFFF);
            return;
        }
        int targetDimension = target.getInteger("dimid");
        if (mc.player.dimension != targetDimension) {
            DimensionManager dimensions = DimensionManager.getInstance();
            String name = targetDimension == 0 || dimensions.isDimensionCreated(targetDimension)
                    ? dimensions.getDimensionProperties(targetDimension).getName() : Integer.toString(targetDimension);
            gui.drawCenteredString(mc.fontRenderer, I18n.format("msg.rocketFinder.offWorld", name), screenX, 32, 0xFFFFFF);
            return;
        }

        double deltaX = mc.player.posX - target.getDouble("x");
        double deltaZ = mc.player.posZ - target.getDouble("z");
        double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
        if (!Double.isFinite(distanceSquared)) return;
        gui.drawCenteredString(mc.fontRenderer, I18n.format("msg.rocketFinder.target"), screenX, 16, 0xFFFFFF);
        if (distanceSquared < 64D) {
            gui.drawCenteredString(mc.fontRenderer, I18n.format("msg.rocketFinder.nearby"), screenX, 40, 0xFFFFFF);
        } else {
            double angle = MathHelper.wrapDegrees(MathHelper.atan2(deltaZ, deltaX) * 180D / Math.PI
                    + 90D - mc.player.rotationYawHead);
            GlStateManager.pushMatrix();
            GlStateManager.translate(screenX, 36, 0);
            // The face helper flips the down-arrow texture to point up at zero rotation.
            GlStateManager.rotate((float) angle, 0, 0, 1);
            mc.renderEngine.bindTexture(zmaster587.libVulpes.inventory.TextureResources.buttonDown[0]);
            GlStateManager.color(0.5F, 0.8F, 1F, 1F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            RenderHelper.renderNorthFaceWithUV(buffer, 0, -8, -8, 8, 8, 0, 1, 0, 1);
            tessellator.draw();
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.popMatrix();
        }
        gui.drawCenteredString(mc.fontRenderer, I18n.format("msg.rocketFinder.distance",
                Math.round(Math.sqrt(distanceSquared))), screenX, 54, 0xFFFFFF);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceIcon getComponentIcon(@Nonnull ItemStack component) {
        return new ResourceIcon(KeyBindings.isRocketFinderEnabled()
                ? TextureResources.rocketFinderIconEnabled : TextureResources.rocketFinderIconDisabled);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        TooltipInjector.renderShiftAlt(stack, tooltip, "tooltip.advancedrocketry.rocketfinder",
                TooltipInjector.computeInsertIndex(tooltip, flag.isAdvanced()));
    }
}
