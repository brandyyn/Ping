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

package mega.ping.proxy;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mega.ping.client.KeyHandler;
import mega.ping.client.PingConfig;
import mega.ping.client.PingHandler;
import mega.ping.client.RenderHandler;
import mega.ping.client.gui.GuiPingSelect;
import mega.ping.data.PingType;
import mega.ping.data.PingWrapper;
import mega.ping.helper.RaytraceHelper;
import mega.ping.network.PacketHandler;
import mega.ping.network.packet.ClientSendPing;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.awt.*;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy {
    public static void sendPing(PingType type) {
        MovingObjectPosition mob = RaytraceHelper.raytrace(Minecraft.getMinecraft().thePlayer, 256);
        if (mob != null && mob.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            sendPing(mob, new Color(PingConfig.Visual.red, PingConfig.Visual.green, PingConfig.Visual.blue).getRGB(), type);
        }
    }

    public static void sendPing(MovingObjectPosition mob, int color, PingType type) {
        PacketHandler.INSTANCE.sendToServer(new ClientSendPing(new PingWrapper(mob.blockX, mob.blockY, mob.blockZ, color, type)));
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        PingHandler.register();
        KeyHandler.register();
        RenderHandler.register();
    }
}
