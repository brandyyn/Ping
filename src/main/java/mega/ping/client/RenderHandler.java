package mega.ping.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lombok.val;
import mega.ping.client.gui.GuiPingSelect;
import mega.ping.data.PingAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class RenderHandler {

    private static final float Z_LEVEL = 0.05F;

    public static final int ITEM_PADDING = 10;
    public static final int ITEM_SIZE = 32;

    public static void register() {
        RenderHandler clientTickHandler = new RenderHandler();
        FMLCommonHandler.instance().bus().register(clientTickHandler);
        MinecraftForge.EVENT_BUS.register(clientTickHandler);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getMinecraft();
            if ((mc.theWorld == null || mc.isGamePaused()) && GuiPingSelect.active) {
                GuiPingSelect.deactivate();
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent event) {
        if (!(event instanceof RenderGameOverlayEvent.Post) || event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null && !mc.gameSettings.hideGUI && !mc.isGamePaused() && GuiPingSelect.active) {
            renderGui(event.resolution, Z_LEVEL);
        }
    }

    private void renderGui(ScaledResolution resolution, double zLevel) {
        Minecraft mc = Minecraft.getMinecraft();
        Tessellator tessellator = Tessellator.instance;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        final int mouseX = Mouse.getX() * width / mc.displayWidth;
        final int mouseY = height - Mouse.getY() * height / mc.displayHeight - 1;

        float centerX = width / 2f;
        float centerY = height / 2f;

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);

        float intro = (float) ((System.nanoTime() - GuiPingSelect.activatedAt) / 1000000L) / 1000F;
        intro *= 10;
        intro = Math.min(intro, 1);

        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        val pingActionValues = PingAction.values();
        val pingResourceValues = PingResource.values();
        val length = pingActionValues.length;
        for (int i = 0; i < length; i++) {
            val pingAction = pingActionValues[i];
            val pingResource = pingResourceValues[i];

            float radialPosition = (float) (((float) i / length) * Math.PI * 2);
            float posSin = (float) Math.sin(radialPosition);
            float posCos = (float) Math.cos(radialPosition);

            float drawX = centerX + posCos * ITEM_SIZE * intro;
            float drawY = centerY + posSin * ITEM_SIZE * intro;

            float min = -ITEM_SIZE / 2f * intro;
            float max = ITEM_SIZE / 2f * intro;

            boolean mouseIn = GuiPingSelect.isHoveringOn(mouseX, mouseY, centerX, centerY, i, length, false);

            if (mouseIn) {
                String localized = I18n.format(pingAction.unlocalizedName());
                GL11.glPushMatrix();
                GL11.glColor4f(1, 1, 1, 1);
                mc.fontRenderer.drawString(localized,
                                           (int) (drawX - mc.fontRenderer.getStringWidth(localized) / 2),
                                           (int) (drawY + max),
                                           0xFFFFFF);
                GL11.glPopMatrix();
            }


            Minecraft.getMinecraft().getTextureManager().bindTexture(pingResource.backgroundTexture());

            tessellator.startDrawingQuads();
            if (mouseIn) {
                tessellator.setColorRGBA(PingConfig.Visual.PING_OVERLAY_RED, PingConfig.Visual.PING_OVERLAY_GREEN, PingConfig.Visual.PING_OVERLAY_BLUE, (int) (intro * 255));
            } else {
                tessellator.setColorRGBA_F(1, 1, 1, intro);
            }
            tessellator.addVertexWithUV(drawX + min, drawY + max, 0, 0, 1);
            tessellator.addVertexWithUV(drawX + max, drawY + max, 0, 1, 1);
            tessellator.addVertexWithUV(drawX + max, drawY + min, 0, 1, 0);
            tessellator.addVertexWithUV(drawX + min, drawY + min, 0, 0, 0);
            tessellator.draw();

            Minecraft.getMinecraft().getTextureManager().bindTexture(pingResource.foregroundTexture());

            // Icon
            tessellator.setColorOpaque_F(1, 1, 1);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(drawX + min, drawY + max, 0, 0, 1);
            tessellator.addVertexWithUV(drawX + max, drawY + max, 0, 1, 1);
            tessellator.addVertexWithUV(drawX + max, drawY + min, 0, 1, 0);
            tessellator.addVertexWithUV(drawX + min, drawY + min, 0, 0, 0);
            tessellator.draw();
        }
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

}
