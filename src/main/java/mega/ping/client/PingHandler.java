package mega.ping.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import mega.ping.data.PingAction;
import mega.ping.data.PingWrapper;
import mega.ping.network.packet.ServerBroadcastPing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmillerw
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PingHandler {
    public static final PingHandler INSTANCE = new PingHandler();

    public static void register() {
        FMLCommonHandler.instance().bus().register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    private RenderBlocks renderBlocks;

    private final List<PingWrapper> activePings = new ArrayList<>();

    public void onPingPacket(ServerBroadcastPing packet) {
        val mc = Minecraft.getMinecraft();
        val player = mc.thePlayer;

        val ping = packet.ping;

        val posX = ping.posX();
        val posY = ping.posY();
        val posZ = ping.posZ();

        val distance = player.getDistance(posX, posY, posZ);
        if (distance > PingConfig.General.MAX_PING_DISTANCE)
            return;

        if (PingConfig.General.PLAY_PING_SOUND) {
            val pingAction = ping.action();
            val pingResource = PingResource.values()[pingAction.ordinal()];
            val sound = pingResource.appearSound();
            WorldSound.playSound(sound, posX, posY, posZ);
        }

        packet.ping.timer(PingConfig.General.PING_DURATION_TICKS);
        activePings.add(packet.ping);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity renderEntity = mc.renderViewEntity;
        double interpX = renderEntity.prevPosX + (renderEntity.posX - renderEntity.prevPosX) * event.partialTicks;
        double interpY = renderEntity.prevPosY + (renderEntity.posY - renderEntity.prevPosY) * event.partialTicks;
        double interpZ = renderEntity.prevPosZ + (renderEntity.posZ - renderEntity.prevPosZ) * event.partialTicks;

        Frustrum camera = new Frustrum();
        camera.setPosition(interpX, interpY, interpZ);

        for (PingWrapper ping : activePings) {
            val posX = ping.posX();
            val posY = ping.posY();
            val posZ = ping.posZ();

            double px = posX + 0.5 - interpX;
            double py = posY + 0.5 - interpY;
            double pz = posZ + 0.5 - interpZ;

            if (camera.isBoundingBoxInFrustum(ping.getAABB())) {
                ping.isOffscreen = false;

                if (PingConfig.Visual.RENDER_BOX_OVERLAY) {
                    this.renderPingOverlay(posX - TileEntityRendererDispatcher.staticPlayerX,
                                           posY - TileEntityRendererDispatcher.staticPlayerY,
                                           posZ - TileEntityRendererDispatcher.staticPlayerZ,
                                           ping,
                                           event.partialTicks);
                }
                renderPing(px, py, pz, renderEntity, ping);
            } else {
                ping.isOffscreen = true;
                translatePingCoordinates(px, py, pz, ping);
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT)
            return;

        for (PingWrapper ping : activePings) {
            if (!ping.isOffscreen)
                continue;

            int width = mc.displayWidth;
            int height = mc.displayHeight;

            int x1 = -(width / 2) + 32;
            int y1 = -(height / 2) + 32;
            int x2 = (width / 2) - 32;
            int y2 = (height / 2) - 32;

            double pingX = ping.screenX;
            double pingY = ping.screenY;

            pingX -= width / 2;
            pingY -= height / 2;

            double angle = Math.atan2(pingY, pingX);
            angle += (Math.toRadians(90));
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double m = cos / sin;

            if (cos > 0) {
                pingX = y2 / m;
                pingY = y2;
            } else {
                pingX = y1 / m;
                pingY = y1;
            }

            if (pingX > x2) {
                pingX = x2;
                pingY = x2 * m;
            } else if (pingX < x1) {
                pingX = x1;
                pingY = x1 * m;
            }

            pingX += width / 2f;
            pingY += height / 2f;

            GL11.glPushMatrix();

            val pingAction = ping.action();
            val pingResource = PingResource.values()[pingAction.ordinal()];

            Minecraft.getMinecraft().renderEngine.bindTexture(pingResource.backgroundTexture());

            Tessellator tessellator = Tessellator.instance;

            tessellator.setTranslation(pingX / 2, pingY / 2, 0);

            float min = -8;
            float max = 8;

            // Background
            tessellator.startDrawingQuads();
            tessellator.setColorOpaque_I(ping.color());
            tessellator.addVertexWithUV(min, max, 0, 0F, 1F);
            tessellator.addVertexWithUV(max, max, 0, 1F, 1F);
            tessellator.addVertexWithUV(max, min, 0, 1F, 0F);
            tessellator.addVertexWithUV(min, min, 0, 0F, 0F);
            tessellator.draw();

            Minecraft.getMinecraft().renderEngine.bindTexture(pingResource.foregroundTexture());

            // Icon
            tessellator.startDrawingQuads();
            tessellator.setColorOpaque_F(1, 1, 1);
            tessellator.addVertexWithUV(min, max, 0, 0F, 1F);
            tessellator.addVertexWithUV(max, max, 0, 1F, 1F);
            tessellator.addVertexWithUV(max, min, 0, 1F, 0F);
            tessellator.addVertexWithUV(min, min, 0, 0F, 0F);
            tessellator.draw();

            tessellator.setTranslation(0, 0, 0);

            GL11.glPopMatrix();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        val it = activePings.iterator();
        while (it.hasNext()) {
            val ping = it.next();
            if (ping.animationTimer > 0)
                ping.animationTimer -= 5;

            val timer = ping.timer() - 1;
            ping.timer(timer);
            if (timer > 0)
                continue;

            if (PingConfig.General.PLAY_PING_SOUND) {
                val posX = ping.posX();
                val posY = ping.posY();
                val posZ = ping.posZ();

                val pingAction = ping.action();
                val pingResource = PingResource.values()[pingAction.ordinal()];
                val sound = pingResource.disappearSound();
                WorldSound.playSound(sound, posX, posY, posZ);
            }

            it.remove();
        }
    }

    private static void renderPing(double px, double py, double pz, Entity renderEntity, PingWrapper ping) {
        val pingAction = ping.action();
        val pingResource = PingResource.values()[pingAction.ordinal()];

        GL11.glPushMatrix();

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        GL11.glTranslated(px, py, pz);

        GL11.glRotatef(-renderEntity.rotationYaw, 0, 1, 0);
        GL11.glRotatef(renderEntity.rotationPitch, 1, 0, 0);
        GL11.glRotated(180, 0, 0, 1);

        Minecraft.getMinecraft().renderEngine.bindTexture(pingResource.backgroundTexture());

        Tessellator tessellator = Tessellator.instance;

        float min = -0.25F - (0.25F * (float) ping.animationTimer / 20F);
        float max = 0.25F + (0.25F * (float) ping.animationTimer / 20F);

        // Background
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(ping.color());
        tessellator.addVertexWithUV(min, max, 0, 0F, 1F);
        tessellator.addVertexWithUV(max, max, 0, 1F, 1F);
        tessellator.addVertexWithUV(max, min, 0, 1F, 0F);
        tessellator.addVertexWithUV(min, min, 0, 0F, 0F);
        tessellator.draw();

        Minecraft.getMinecraft().renderEngine.bindTexture(pingResource.foregroundTexture());

        // Icon
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(min, max, 0, 0F, 1F);
        tessellator.addVertexWithUV(max, max, 0, 1F, 1F);
        tessellator.addVertexWithUV(max, min, 0, 1F, 0F);
        tessellator.addVertexWithUV(min, min, 0, 0F, 0F);
        tessellator.draw();

        GL11.glPopAttrib();

        GL11.glPopMatrix();
    }

    public void renderPingOverlay(double x, double y, double z, PingWrapper ping, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        if (renderBlocks == null || renderBlocks.blockAccess != mc.theWorld) {
            renderBlocks = new RenderBlocks(mc.theWorld);
        }

        IIcon icon = Blocks.stained_glass.getIcon(0, 0);

        float padding = 0F + (0.20F * (float) ping.animationTimer / (float) 20);
        float box = 1 + padding + padding;

        int alpha = ping.action() == PingAction.ALERT ? (int) (100 * (1 + Math.sin(mc.theWorld.getTotalWorldTime()))) : 25;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        Tessellator.instance.setTranslation(x + 0.5, y + 0.5, z + 0.5);

        if (ping.action() == PingAction.GOTO) {
            renderBeamShaft(mc.theWorld, x, y, z, partialTicks);
        }

        val width = box;
        val height = box;
        val length = box;

        Tessellator tessellator = Tessellator.instance;

        tessellator.startDrawingQuads();

        tessellator.setColorRGBA_I(ping.color(), 150 + alpha);

        Minecraft.getMinecraft().getTextureManager().bindTexture(PingResource.values()[ping.action().ordinal()].boxTexture());

        tessellator.setBrightness(Integer.MAX_VALUE);

        // TOP
        tessellator.addVertexWithUV(-(width / 2), (height / 2), -(length / 2), 0, 0);
        tessellator.addVertexWithUV((width / 2), (height / 2), -(length / 2), 1, 0);
        tessellator.addVertexWithUV((width / 2), (height / 2), (length / 2), 1, 1);
        tessellator.addVertexWithUV(-(width / 2), (height / 2), (length / 2), 0, 1);

        // BOTTOM
        tessellator.addVertexWithUV(-(width / 2), -(height / 2), (length / 2), 0, 1);
        tessellator.addVertexWithUV((width / 2), -(height / 2), (length / 2), 1, 1);
        tessellator.addVertexWithUV((width / 2), -(height / 2), -(length / 2), 1, 0);
        tessellator.addVertexWithUV(-(width / 2), -(height / 2), -(length / 2), 0, 0);

        // NORTH
        tessellator.addVertexWithUV(-(width / 2), (height / 2), (length / 2), 0, 1);
        tessellator.addVertexWithUV((width / 2), (height / 2), (length / 2), 1, 1);
        tessellator.addVertexWithUV((width / 2), -(height / 2), (length / 2), 1, 0);
        tessellator.addVertexWithUV(-(width / 2), -(height / 2), (length / 2), 0, 0);

        // SOUTH
        tessellator.addVertexWithUV(-(width / 2), -(height / 2), -(length / 2), 0, 0);
        tessellator.addVertexWithUV((width / 2), -(height / 2), -(length / 2), 1, 0);
        tessellator.addVertexWithUV((width / 2), (height / 2), -(length / 2), 1, 1);
        tessellator.addVertexWithUV(-(width / 2), (height / 2), -(length / 2), 0, 1);

        // EAST
        tessellator.addVertexWithUV(-(width / 2), (height / 2), -(length / 2), 0, 1);
        tessellator.addVertexWithUV(-(width / 2), (height / 2), (length / 2), 1, 1);
        tessellator.addVertexWithUV(-(width / 2), -(height / 2), (length / 2), 1, 0);
        tessellator.addVertexWithUV(-(width / 2), -(height / 2), -(length / 2), 0, 0);

        // WEST
        tessellator.addVertexWithUV((width / 2), -(height / 2), -(length / 2), 0, 0);
        tessellator.addVertexWithUV((width / 2), -(height / 2), (length / 2), 1, 0);
        tessellator.addVertexWithUV((width / 2), (height / 2), (length / 2), 1, 1);
        tessellator.addVertexWithUV((width / 2), (height / 2), -(length / 2), 0, 1);

        tessellator.draw();

        Tessellator.instance.setTranslation(0, 0, 0);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    private final void translatePingCoordinates(double px, double py, double pz, PingWrapper ping) {
        val screenCoords = BufferUtils.createFloatBuffer(4);
        val viewport = BufferUtils.createIntBuffer(16);
        val modelView = BufferUtils.createFloatBuffer(16);
        val projection = BufferUtils.createFloatBuffer(16);

        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);

        if (!GLU.gluProject((float) px, (float) py, (float) pz, modelView, projection, viewport, screenCoords))
            return;

        ping.screenX = screenCoords.get(0);
        ping.screenY = screenCoords.get(1);
        //TODO Rotation sometimes fucks this up
    }

    private static void renderBeamShaft(World world, double posX, double posY, double posZ, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

        Tessellator tessellator = Tessellator.instance;
        Minecraft.getMinecraft().renderEngine.bindTexture(PingResource.beamTexture());

        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        OpenGlHelper.glBlendFunc(770, 1, 1, 0);

        float beamHeight = 1F;

        float time = (float) world.getTotalWorldTime() + partialTicks;
        float f3 = -time * 0.2F - (float) MathHelper.floor_float(-time * 0.1F);

        double d3 = (double) time * 0.025D * (1.0D - 2.5D);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 255, 255, 32);
        double d5 = 0.2D;
        double d7 = 0.5D + Math.cos(d3 + 2.356194490192345D) * d5;
        double d9 = 0.5D + Math.sin(d3 + 2.356194490192345D) * d5;
        double d11 = 0.5D + Math.cos(d3 + (Math.PI / 4D)) * d5;
        double d13 = 0.5D + Math.sin(d3 + (Math.PI / 4D)) * d5;
        double d15 = 0.5D + Math.cos(d3 + 3.9269908169872414D) * d5;
        double d17 = 0.5D + Math.sin(d3 + 3.9269908169872414D) * d5;
        double d19 = 0.5D + Math.cos(d3 + 5.497787143782138D) * d5;
        double d21 = 0.5D + Math.sin(d3 + 5.497787143782138D) * d5;
        double d23 = 256.0F * beamHeight;
        double d25 = 0.0D;
        double d27 = 1.0D;
        double d28 = -1.0F + f3;
        double d29 = (double) (256.0F * beamHeight) * (0.5D / d5) + d28;
        tessellator.addVertexWithUV(posX + d7, posY + d23, posZ + d9, d27, d29);
        tessellator.addVertexWithUV(posX + d7, posY, posZ + d9, d27, d28);
        tessellator.addVertexWithUV(posX + d11, posY, posZ + d13, d25, d28);
        tessellator.addVertexWithUV(posX + d11, posY + d23, posZ + d13, d25, d29);
        tessellator.addVertexWithUV(posX + d19, posY + d23, posZ + d21, d27, d29);
        tessellator.addVertexWithUV(posX + d19, posY, posZ + d21, d27, d28);
        tessellator.addVertexWithUV(posX + d15, posY, posZ + d17, d25, d28);
        tessellator.addVertexWithUV(posX + d15, posY + d23, posZ + d17, d25, d29);
        tessellator.addVertexWithUV(posX + d11, posY + d23, posZ + d13, d27, d29);
        tessellator.addVertexWithUV(posX + d11, posY, posZ + d13, d27, d28);
        tessellator.addVertexWithUV(posX + d19, posY, posZ + d21, d25, d28);
        tessellator.addVertexWithUV(posX + d19, posY + d23, posZ + d21, d25, d29);
        tessellator.addVertexWithUV(posX + d15, posY + d23, posZ + d17, d27, d29);
        tessellator.addVertexWithUV(posX + d15, posY, posZ + d17, d27, d28);
        tessellator.addVertexWithUV(posX + d7, posY, posZ + d9, d25, d28);
        tessellator.addVertexWithUV(posX + d7, posY + d23, posZ + d9, d25, d29);
        tessellator.draw();
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDepthMask(false);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 255, 255, 32);
        double d30 = 0.2D;
        double d4 = 0.2D;
        double d6 = 0.8D;
        double d8 = 0.2D;
        double d10 = 0.2D;
        double d12 = 0.8D;
        double d14 = 0.8D;
        double d16 = 0.8D;
        double d18 = 256.0F * beamHeight;
        double d20 = 0.0D;
        double d22 = 1.0D;
        double d24 = -1.0F + f3;
        double d26 = (double) (256.0F * beamHeight) + d24;
        tessellator.addVertexWithUV(posX + d30, posY + d18, posZ + d4, d22, d26);
        tessellator.addVertexWithUV(posX + d30, posY, posZ + d4, d22, d24);
        tessellator.addVertexWithUV(posX + d6, posY, posZ + d8, d20, d24);
        tessellator.addVertexWithUV(posX + d6, posY + d18, posZ + d8, d20, d26);
        tessellator.addVertexWithUV(posX + d14, posY + d18, posZ + d16, d22, d26);
        tessellator.addVertexWithUV(posX + d14, posY, posZ + d16, d22, d24);
        tessellator.addVertexWithUV(posX + d10, posY, posZ + d12, d20, d24);
        tessellator.addVertexWithUV(posX + d10, posY + d18, posZ + d12, d20, d26);
        tessellator.addVertexWithUV(posX + d6, posY + d18, posZ + d8, d22, d26);
        tessellator.addVertexWithUV(posX + d6, posY, posZ + d8, d22, d24);
        tessellator.addVertexWithUV(posX + d14, posY, posZ + d16, d20, d24);
        tessellator.addVertexWithUV(posX + d14, posY + d18, posZ + d16, d20, d26);
        tessellator.addVertexWithUV(posX + d10, posY + d18, posZ + d12, d22, d26);
        tessellator.addVertexWithUV(posX + d10, posY, posZ + d12, d22, d24);
        tessellator.addVertexWithUV(posX + d30, posY, posZ + d4, d20, d24);
        tessellator.addVertexWithUV(posX + d30, posY + d18, posZ + d4, d20, d26);
        tessellator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }
}
