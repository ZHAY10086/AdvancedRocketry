package zmaster587.advancedRocketry.client.render;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.TileRocketPrinter;
import zmaster587.advancedRocketry.util.IBrokenPartBlock;
import zmaster587.advancedRocketry.util.RocketBlueprint;
import zmaster587.libVulpes.block.BlockFullyRotatable;
import zmaster587.libVulpes.inventory.GuiModular;
import zmaster587.libVulpes.inventory.modules.ModuleBase;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ModuleRocketBlueprintPreview extends ModuleBase {
    private static ItemStack cachedStack = ItemStack.EMPTY;
    private static RocketBlueprint cachedBlueprint;
    private static int displayList = -1;
    private static boolean dirty = true, listenerRegistered;
    private final TileRocketPrinter printer;
    private float yaw = 35, pitch = 25, zoom = 1;
    private int mouseX, mouseY;
    private boolean dragging;

    public ModuleRocketBlueprintPreview(int x, int y, TileRocketPrinter printer) {
        super(x, y);
        this.printer = printer;
        sizeX = 108; sizeY = 150;
        if (!listenerRegistered) {
            ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(manager -> dirty = true);
            listenerRegistered = true;
        }
    }

    private static void rebuild(ItemStack stack, RocketBlueprint blueprint) {
        if (displayList != -1) GLAllocation.deleteDisplayLists(displayList);
        displayList = -1;
        cachedStack = stack;
        cachedBlueprint = blueprint;
        dirty = false;
        if (blueprint == null) return;
        IBlockAccess access = new BlueprintAccess(blueprint);
        Map<Block, WavefrontObject> motors = new HashMap<>();
        Map<Block, Integer> motorTextures = new HashMap<>();
        for (IBlockState state : blueprint.palette) {
            Block block = state.getBlock();
            if (!(block instanceof IBrokenPartBlock) || !(block instanceof BlockFullyRotatable)
                    || !"advancedrocketry".equals(block.getRegistryName().getResourceDomain()) || motors.containsKey(block)) continue;
            String name = block.getUnlocalizedName().substring(block.getUnlocalizedName().indexOf('.') + 1).toLowerCase(Locale.ROOT);
            String domain = block.getRegistryName().getResourceDomain();
            try {
                WavefrontObject model = new WavefrontObject(new net.minecraft.util.ResourceLocation(domain, "models/block/models/" + name + ".obj"));
                net.minecraft.util.ResourceLocation texture = new net.minecraft.util.ResourceLocation(domain, "textures/models/" + name + "_0.png");
                Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
                motors.put(block, model);
                motorTextures.put(block, Minecraft.getMinecraft().getTextureManager().getTexture(texture).getGlTextureId());
            } catch (ModelFormatException | RuntimeException ignored) { /* no structural preview model for this block */ }
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        int atlas = Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).getGlTextureId();
        int list = GLAllocation.generateDisplayLists(1);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glNewList(list, GL11.GL_COMPILE);
        try {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlas);
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            for (BlockRenderLayer layer : BlockRenderLayer.values()) {
                ForgeHooksClient.setRenderLayer(layer);
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                for (int i = 0; i < blueprint.cells.length; i += 2) {
                    int packed = blueprint.cells[i];
                    pos.setPos(packed >> 4 & 15, packed >> 8, packed & 15);
                    IBlockState state = blueprint.palette[blueprint.cells[i + 1]];
                    try {
                        if (state.getRenderType() == net.minecraft.util.EnumBlockRenderType.MODEL && state.getBlock().canRenderInLayer(state, layer)) {
                            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(state, pos, access, buffer);
                        }
                    } catch (RuntimeException ignored) { /* a modded model may require a real world/TE */ }
                }
                Tessellator.getInstance().draw();
            }
            for (int i = 0; i < blueprint.cells.length; i += 2) {
                int packed = blueprint.cells[i];
                pos.setPos(packed >> 4 & 15, packed >> 8, packed & 15);
                IBlockState state = blueprint.palette[blueprint.cells[i + 1]];
                Block block = state.getBlock();
                WavefrontObject model = motors.get(block);
                if (model == null) continue;
                EnumFacing facing = state.getActualState(access, pos).getValue(BlockFullyRotatable.FACING);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, motorTextures.get(block));
                GlStateManager.pushMatrix();
                try {
                    GlStateManager.translate(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5);
                    if (facing == EnumFacing.UP) GlStateManager.rotate(180, 1, 0, 0);
                    if (facing.getAxis() == EnumFacing.Axis.X) GlStateManager.rotate(90, 0, 0, -facing.getFrontOffsetX());
                    if (facing.getAxis() == EnumFacing.Axis.Z) GlStateManager.rotate(90, facing.getFrontOffsetZ(), 0, 0);
                    GlStateManager.translate(-.5, -.5, -.5);
                    model.renderAll();
                } finally { GlStateManager.popMatrix(); }
            }
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlas);
        } finally {
            ForgeHooksClient.setRenderLayer(null);
            GL11.glEndList();
            GL11.glPopAttrib();
        }
        displayList = list;
    }

    @Override
    public void renderBackground(GuiContainer gui, int x, int y, int mx, int my, FontRenderer font) {
        int left = x + offsetX, top = y + offsetY;
        ItemStack stack = printer.getStackInSlot(0);
        if (stack != cachedStack || dirty) rebuild(stack, RocketBlueprint.read(stack));
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GlStateManager.pushMatrix();
        try {
            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();
            GlStateManager.color(.05f, .09f, .14f, 1);
            net.minecraft.client.gui.Gui.drawRect(left, top, left + sizeX, top + sizeY, 0xFF373737);
            net.minecraft.client.gui.Gui.drawRect(left + 1, top + 1, left + sizeX - 1, top + sizeY - 1, 0xFFC6C6C6);
            net.minecraft.client.gui.Gui.drawRect(left + 3, top + 3, left + sizeX - 3, top + sizeY - 3, 0xEF111B26);
            if (displayList == -1) return;
            int factor = new net.minecraft.client.gui.ScaledResolution(gui.mc).getScaleFactor();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor((left + 3) * factor, gui.mc.displayHeight - (top + sizeY - 3) * factor,
                    (sizeX - 6) * factor, (sizeY - 6) * factor);
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.depthFunc(GL11.GL_LEQUAL);
            GlStateManager.color(1f, 1f, 1f, 1f);
            gui.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            RocketBlueprint blueprint = cachedBlueprint;
            if (blueprint == null) return;
            float span = (float) Math.hypot(blueprint.sizeX, blueprint.sizeZ);
            float scale = Math.min((sizeX - 16f) / span, (sizeY - 20f) / (blueprint.sizeY + span)) * zoom;
            GlStateManager.translate(left + sizeX / 2f, top + sizeY / 2f, 200);
            GlStateManager.scale(scale, -scale, scale);
            GlStateManager.rotate(pitch, 1, 0, 0);
            GlStateManager.rotate(yaw, 0, 1, 0);
            GlStateManager.translate(-blueprint.sizeX / 2f, -blueprint.sizeY / 2f, -blueprint.sizeZ / 2f);
            GL11.glCallList(displayList);
        } finally {
            GlStateManager.popMatrix();
            GL11.glPopAttrib();
        }
    }

    @Override
    public void renderForeground(int gx, int gy, int mx, int my, float z, GuiContainer gui, FontRenderer font) {
        if (mx >= offsetX && mx < offsetX + sizeX && my >= offsetY && my < offsetY + sizeY) {
            int wheel = org.lwjgl.input.Mouse.getDWheel();
            if (wheel != 0) zoom = Math.max(.35f, Math.min(3f, zoom * (wheel > 0 ? 1.12f : 1f / 1.12f)));
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) dragging = false;
    }

    @Override
    public void onMouseClicked(GuiModular gui, int x, int y, int button) {
        mouseX = x; mouseY = y;
        dragging = getVisible() && button == 0 && x >= offsetX && x < offsetX + sizeX && y >= offsetY && y < offsetY + sizeY;
    }

    @Override
    public void onMouseClickedAndDragged(int x, int y, int button, long elapsed) {
        if (dragging && button == 0) {
            yaw += (x - mouseX) * .8f;
            pitch = Math.max(-85, Math.min(85, pitch + (y - mouseY) * .8f));
        }
        mouseX = x; mouseY = y;
    }

    private static class BlueprintAccess implements IBlockAccess {
        private final RocketBlueprint blueprint;
        private final IBlockState[] states;
        BlueprintAccess(RocketBlueprint blueprint) {
            this.blueprint = blueprint;
            states = new IBlockState[blueprint.sizeX * blueprint.sizeY * blueprint.sizeZ];
            for (int i = 0; i < blueprint.cells.length; i += 2) {
                int packed = blueprint.cells[i], x = packed >> 4 & 15, y = packed >> 8, z = packed & 15;
                states[(y * blueprint.sizeX + x) * blueprint.sizeZ + z] = blueprint.palette[blueprint.cells[i + 1]];
            }
        }
        @Override public IBlockState getBlockState(BlockPos pos) {
            int x = pos.getX(), y = pos.getY(), z = pos.getZ();
            if (x < 0 || x >= blueprint.sizeX || y < 0 || y >= blueprint.sizeY || z < 0 || z >= blueprint.sizeZ) return Blocks.AIR.getDefaultState();
            IBlockState state = states[(y * blueprint.sizeX + x) * blueprint.sizeZ + z];
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }
        @Override @Nullable public TileEntity getTileEntity(BlockPos pos) { return null; }
        @Override public int getCombinedLight(BlockPos pos, int light) { return 0xF000F0; }
        @Override public boolean isAirBlock(BlockPos pos) { return getBlockState(pos).getBlock() == Blocks.AIR; }
        @Override public Biome getBiome(BlockPos pos) { return Biomes.PLAINS; }
        @Override public int getStrongPower(BlockPos pos, EnumFacing side) { return 0; }
        @Override public WorldType getWorldType() { return WorldType.DEFAULT; }
        @Override public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean defaultValue) { return getBlockState(pos).isSideSolid(this, pos, side); }
    }
}