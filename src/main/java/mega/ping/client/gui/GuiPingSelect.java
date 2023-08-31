package mega.ping.client.gui;

import mega.ping.client.KeyHandler;
import mega.ping.data.PingType;
import mega.ping.proxy.ClientProxy;

import net.minecraft.client.Minecraft;

/**
 * @author dmillerw
 */
public class GuiPingSelect {

    public static boolean active = false;

    public static long activatedAt = 0;

    public static void activate() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.inGameHasFocus = false;
        mc.mouseHelper.ungrabMouseCursor();
        active = true;
        activatedAt = System.nanoTime();
    }

    public static void deactivate() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null) {
            mc.inGameHasFocus = true;
            mc.mouseHelper.grabMouseCursor();
        }
        active = false;
    }

    public static void sendPing(int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getMinecraft();
        CompatibleScaledResolution resolution = new CompatibleScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int numOfItems = PingType.values().length - 1;

        float centerX = resolution.getScaledWidth() / 2f;
        float centerY = resolution.getScaledHeight() / 2f;
        for (int i=0; i<numOfItems; i++) {
            PingType type = PingType.values()[i + 1];

            if (isHoveringOn(mouseX, mouseY, centerX, centerY, i, numOfItems, true)) {
                ClientProxy.sendPing(type);
                KeyHandler.ignoreNextRelease = true;
                return;
            }
        }

    }

    public static boolean isHoveringOn(float mouseX, float mouseY, float centerX, float centerY, int index, int count, boolean includeCenter) {

        float mouseOffsetX = mouseX - centerX;
        float mouseRadialY = mouseY - centerY;

        boolean mouseIn;
        if (mouseOffsetX * mouseOffsetX + mouseRadialY * mouseRadialY > 64) {

            float angle = (float) (Math.atan2(-mouseRadialY, -mouseOffsetX) / Math.PI / 2 + 0.5f);

            int radialIndex = Math.round(angle * count) % count;
            mouseIn = radialIndex == index;
        } else {
            mouseIn = includeCenter && index == 0;
        }
        return mouseIn;
    }
}
