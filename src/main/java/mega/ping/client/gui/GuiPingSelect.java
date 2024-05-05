package mega.ping.client.gui;

import mega.ping.client.KeyHandler;
import mega.ping.client.PingConfig;
import mega.ping.data.PingAction;
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

        float centerX = resolution.getScaledWidth() / 2f;
        float centerY = resolution.getScaledHeight() / 2f;

        double distSquared = (mouseX - centerX) * (mouseX - centerX) + (mouseY - centerY) * (mouseY - centerY);
        double threshold = PingConfig.General.DEADZONE_RADIUS_PIXELS;
        if (distSquared < threshold) {
            return;
        }

        int numOfItems = PingAction.values().length;
        for (int i=0; i<numOfItems; i++) {
            PingAction type = PingAction.values()[i];

            if (isHoveringOn(mouseX, mouseY, centerX, centerY, i, numOfItems, true)) {
                ClientProxy.doPing(type);
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
