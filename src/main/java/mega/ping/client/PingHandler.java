/*
 * MEGA Ping
 *
 * Copyright (C) 2023-2024 FalsePattern, the MEGA team
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package mega.ping.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mega.ping.data.PingType;
import mega.ping.data.PingWrapper;
import mega.ping.helper.PingRenderHelper;
import mega.ping.network.packet.ServerBroadcastPing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author dmillerw
 */
public class PingHandler {

    public static final PingHandler INSTANCE = new PingHandler();

    public static final ResourceLocation TEXTURE = new ResourceLocation("ping:textures/ping.png");
    public static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("textures/entity/beacon_beam.png");

    private static final ResourceLocation APPEAR_SOUND = new ResourceLocation("ping:appear");
    private static final ResourceLocation DISAPPEAR_SOUND = new ResourceLocation("ping:disappear");

    public static void register() {
        FMLCommonHandler.instance().bus().register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    private RenderBlocks renderBlocks;

    private final List<PingWrapper> activePings = new ArrayList<>();

    public void onPingPacket(ServerBroadcastPing packet) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer.getDistance(packet.ping.x, packet.ping.y, packet.ping.z) <= PingConfig.General.pingAcceptDistance) {
            if (PingConfig.General.sound) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(APPEAR_SOUND, 1.0F));
            }
            packet.ping.timer = PingConfig.General.pingDuration;
            activePings.add(packet.ping);
        }
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
            double px = ping.x + 0.5 - interpX;
            double py = ping.y + 0.5 - interpY;
            double pz = ping.z + 0.5 - interpZ;

            if (camera.isBoundingBoxInFrustum(ping.getAABB())) {
                ping.isOffscreen = false;

                if (PingConfig.Visual.blockOverlay) {
                    this.renderPingOverlay(ping.x - TileEntityRendererDispatcher.staticPlayerX,
                                           ping.y - TileEntityRendererDispatcher.staticPlayerY,
                                           ping.z - TileEntityRendererDispatcher.staticPlayerZ,
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
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            boolean startedDrawing = false;
            Minecraft mc = Minecraft.getMinecraft();
            Tessellator tessellator = Tessellator.instance;
            for (PingWrapper ping : activePings) {
                if (!ping.isOffscreen) {
                    continue;
                }

                double ratio = 2 / (mc.displayWidth / event.resolution.getScaledWidth_double());

                int width = (int) (mc.displayWidth * ratio);
                int height = (int) (mc.displayHeight * ratio);

                int x1 = -(width / 2) + 32;
                int y1 = -(height / 2) + 32;
                int x2 = ((width / 2) - 32);
                int y2 = ((height / 2) - 32);

                double pingX = ping.screenX * ratio;
                double pingY = ping.screenY * ratio;

                pingX -= width / 2f;
                pingY -= height / 2f;

                if (ping.behind) {
                    pingX = -pingX;
                    pingY = -pingY;
                }

                double angle = Math.atan2(pingY, pingX);
                angle += (Math.toRadians(90));
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);
                double m = cos / sin;

                if (cos > 0){
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

                Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

                float min = -8;
                float max =  8;

                // Background
                if (!startedDrawing) {
                    tessellator.startDrawingQuads();
                    startedDrawing = true;
                }
                tessellator.setTranslation(pingX / 2, pingY / 2, 0);
                tessellator.setColorOpaque_I(ping.color);
                tessellator.addVertexWithUV(min, max, 0, PingType.BACKGROUND.minU, PingType.BACKGROUND.maxV);
                tessellator.addVertexWithUV(max, max, 0, PingType.BACKGROUND.maxU, PingType.BACKGROUND.maxV);
                tessellator.addVertexWithUV(max, min, 0, PingType.BACKGROUND.maxU, PingType.BACKGROUND.minV);
                tessellator.addVertexWithUV(min, min, 0, PingType.BACKGROUND.minU, PingType.BACKGROUND.minV);

                // Icon
                tessellator.setColorOpaque_F(1, 1, 1);
                tessellator.addVertexWithUV(min, max, 0, ping.type.minU, ping.type.maxV);
                tessellator.addVertexWithUV(max, max, 0, ping.type.maxU, ping.type.maxV);
                tessellator.addVertexWithUV(max, min, 0, ping.type.maxU, ping.type.minV);
                tessellator.addVertexWithUV(min, min, 0, ping.type.minU, ping.type.minV);

            }
            if (startedDrawing) {
                tessellator.draw();
                tessellator.setTranslation(0, 0, 0);
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Iterator<PingWrapper> iterator = activePings.iterator();
        while (iterator.hasNext()) {
            PingWrapper pingWrapper = iterator.next();
            if (pingWrapper.animationTimer > 0) {
                pingWrapper.animationTimer -= 5;
            }
            pingWrapper.timer--;

            if (pingWrapper.timer <= 0) {
                if (PingConfig.General.sound) {
                    Minecraft mc = Minecraft.getMinecraft();
                    mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(DISAPPEAR_SOUND, 1.0F));
                }
                iterator.remove();
            }
        }
    }

    public void renderPing(double px, double py, double pz, Entity renderEntity, PingWrapper ping) {
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);

        GL11.glTranslated(px, py, pz);

        GL11.glRotatef(-renderEntity.rotationYaw, 0, 1, 0);
        GL11.glRotatef(renderEntity.rotationPitch, 1, 0, 0);
        GL11.glRotated(180, 0, 0, 1);

        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

        Tessellator tessellator = Tessellator.instance;

        float min = -0.25F - (0.25F * (float)ping.animationTimer / 20F);
        float max =  0.25F + (0.25F * (float)ping.animationTimer / 20F);

        // Background
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(ping.color);
        tessellator.addVertexWithUV(min, max, 0, PingType.BACKGROUND.minU, PingType.BACKGROUND.maxV);
        tessellator.addVertexWithUV(max, max, 0, PingType.BACKGROUND.maxU, PingType.BACKGROUND.maxV);
        tessellator.addVertexWithUV(max, min, 0, PingType.BACKGROUND.maxU, PingType.BACKGROUND.minV);
        tessellator.addVertexWithUV(min, min, 0, PingType.BACKGROUND.minU, PingType.BACKGROUND.minV);
        tessellator.draw();

        // Icon
        tessellator.setColorOpaque_F(1, 1, 1);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(min, max, 0, ping.type.minU, ping.type.maxV);
        tessellator.addVertexWithUV(max, max, 0, ping.type.maxU, ping.type.maxV);
        tessellator.addVertexWithUV(max, min, 0, ping.type.maxU, ping.type.minV);
        tessellator.addVertexWithUV(min, min, 0, ping.type.minU, ping.type.minV);
        tessellator.draw();

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }

    public void renderPingOverlay(double x, double y, double z, PingWrapper ping, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        if (renderBlocks == null || renderBlocks.blockAccess != mc.theWorld) {
            renderBlocks = new RenderBlocks(mc.theWorld);
        }

        IIcon icon = Blocks.stained_glass.getIcon(0, 0);

        float padding = 0F + (0.20F * (float)ping.animationTimer / (float)20);
        float box = 1 + padding + padding;

        int alpha = ping.type == PingType.ALERT ? (int)(100 * (1 + Math.sin(mc.theWorld.getTotalWorldTime()))) : 25;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        Tessellator.instance.setTranslation(x + 0.5, y + 0.5, z + 0.5);

        if (ping.type == PingType.GOTO && PingConfig.General.EnableBeam) {
            this.renderBeamShaft(mc.theWorld, x, y, z, partialTicks);
        }

        PingRenderHelper.drawBlockOverlay(box, box, box, icon, ping.color, 150 + alpha);

        Tessellator.instance.setTranslation(0, 0, 0);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    private void translatePingCoordinates(double px, double py, double pz, PingWrapper ping) {
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer(4);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer modelview = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);

        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);


        if (GLU.gluProject((float)px, (float)py, (float)pz, modelview, projection, viewport, screenCoords)) {
            ping.screenX = screenCoords.get(0);
            ping.screenY = screenCoords.get(1);
            ping.behind = screenCoords.get(2) > 1;
        }
    }

    private void renderBeamShaft(World world, double x, double y, double z, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

        Tessellator tessellator = Tessellator.instance;
        Minecraft.getMinecraft().renderEngine.bindTexture(BEAM_TEXTURE);

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
        double d29 = (double)(256.0F * beamHeight) * (0.5D / d5) + d28;
        tessellator.addVertexWithUV(x + d7, y + d23, z + d9, d27, d29);
        tessellator.addVertexWithUV(x + d7, y, z + d9, d27, d28);
        tessellator.addVertexWithUV(x + d11, y, z + d13, d25, d28);
        tessellator.addVertexWithUV(x + d11, y + d23, z + d13, d25, d29);
        tessellator.addVertexWithUV(x + d19, y + d23, z + d21, d27, d29);
        tessellator.addVertexWithUV(x + d19, y, z + d21, d27, d28);
        tessellator.addVertexWithUV(x + d15, y, z + d17, d25, d28);
        tessellator.addVertexWithUV(x + d15, y + d23, z + d17, d25, d29);
        tessellator.addVertexWithUV(x + d11, y + d23, z + d13, d27, d29);
        tessellator.addVertexWithUV(x + d11, y, z + d13, d27, d28);
        tessellator.addVertexWithUV(x + d19, y, z + d21, d25, d28);
        tessellator.addVertexWithUV(x + d19, y + d23, z + d21, d25, d29);
        tessellator.addVertexWithUV(x + d15, y + d23, z + d17, d27, d29);
        tessellator.addVertexWithUV(x + d15, y, z + d17, d27, d28);
        tessellator.addVertexWithUV(x + d7, y, z + d9, d25, d28);
        tessellator.addVertexWithUV(x + d7, y + d23, z + d9, d25, d29);
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
        tessellator.addVertexWithUV(x + d30, y + d18, z + d4, d22, d26);
        tessellator.addVertexWithUV(x + d30, y, z + d4, d22, d24);
        tessellator.addVertexWithUV(x + d6, y, z + d8, d20, d24);
        tessellator.addVertexWithUV(x + d6, y + d18, z + d8, d20, d26);
        tessellator.addVertexWithUV(x + d14, y + d18, z + d16, d22, d26);
        tessellator.addVertexWithUV(x + d14, y, z + d16, d22, d24);
        tessellator.addVertexWithUV(x + d10, y, z + d12, d20, d24);
        tessellator.addVertexWithUV(x + d10, y + d18, z + d12, d20, d26);
        tessellator.addVertexWithUV(x + d6, y + d18, z + d8, d22, d26);
        tessellator.addVertexWithUV(x + d6, y, z + d8, d22, d24);
        tessellator.addVertexWithUV(x + d14, y, z + d16, d20, d24);
        tessellator.addVertexWithUV(x + d14, y + d18, z + d16, d20, d26);
        tessellator.addVertexWithUV(x + d10, y + d18, z + d12, d22, d26);
        tessellator.addVertexWithUV(x + d10, y, z + d12, d22, d24);
        tessellator.addVertexWithUV(x + d30, y, z + d4, d20, d24);
        tessellator.addVertexWithUV(x + d30, y + d18, z + d4, d20, d26);
        tessellator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }
}
