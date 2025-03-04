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

package mega.ping.network.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import mega.ping.api.events.PingEvent;
import mega.ping.data.PingWrapper;
import mega.ping.network.PacketHandler;
import io.netty.buffer.ByteBuf;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;

import java.util.WeakHashMap;

/**
 * Sent from the Client, handled on the Server
 * @author dmillerw
 */
public class ClientSendPing implements IMessage, IMessageHandler<ClientSendPing, IMessage> {
    public PingWrapper ping;

    private static final WeakHashMap<EntityPlayerMP, Long> lastPingTime = new WeakHashMap<>();
    private static final long PING_INTERVAL = 100000000L;

    public ClientSendPing() {

    }

    public ClientSendPing(PingWrapper ping) {
        this.ping = ping;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ping.writeToBuffer(buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        ping = PingWrapper.readFromBuffer(buf);
    }

    @Override
    public IMessage onMessage(ClientSendPing message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (lastPingTime.containsKey(player)) {
            long currentTime = System.nanoTime();
            long lastTime = lastPingTime.get(player);
            long diff = currentTime - lastTime;
            if (diff < PING_INTERVAL) {
                return null;
            }
        }
        lastPingTime.put(player, System.nanoTime());

        World world = player.worldObj;
        Block blockPinged = world.getBlock(message.ping.x, message.ping.y, message.ping.z);

        PingEvent event = new PingEvent(player, blockPinged, message.ping);
        MinecraftForge.EVENT_BUS.post(event);

        PacketHandler.INSTANCE.sendToDimension(new ServerBroadcastPing(message.ping), ctx.getServerHandler().playerEntity.worldObj.provider.dimensionId);
        return null;
    }
}
