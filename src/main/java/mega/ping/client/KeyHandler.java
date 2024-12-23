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

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mega.ping.client.gui.CompatibleScaledResolution;
import mega.ping.client.gui.GuiPingSelect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * @author dmillerw
 */
public class KeyHandler {

    public static final KeyBinding KEY_BINDING = new KeyBinding("ping.key", Keyboard.KEY_F, "key.categories.misc");

    public static void register() {
        ClientRegistry.registerKeyBinding(KEY_BINDING);
        FMLCommonHandler.instance().bus().register(new KeyHandler());
    }

    private static boolean lastKeyState = false;

    public static boolean ignoreNextRelease = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld == null || mc.currentScreen != null) {
            return;
        }

        boolean keyPressed = (KEY_BINDING.getKeyCode() >= 0 ? Keyboard.isKeyDown(KEY_BINDING.getKeyCode()) : Mouse.isButtonDown(KEY_BINDING.getKeyCode() + 100));

        if (keyPressed != lastKeyState) {
            if (keyPressed) {
                GuiPingSelect.activate();
            } else {
                if (!ignoreNextRelease) {
                    final CompatibleScaledResolution scaledresolution = new CompatibleScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                    int i = scaledresolution.getScaledWidth();
                    int j = scaledresolution.getScaledHeight();
                    final int k = Mouse.getX() * i / mc.displayWidth;
                    final int l = j - Mouse.getY() * j / mc.displayHeight - 1;

                    GuiPingSelect.sendPing(k, l);
                }
                ignoreNextRelease = false;
                GuiPingSelect.deactivate();
            }
        } else if (keyPressed) {
            GuiPingSelect.forceUnGrab();
        }
        lastKeyState = keyPressed;
    }
}
