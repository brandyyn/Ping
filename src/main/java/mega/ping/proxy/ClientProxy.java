package mega.ping.proxy;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lombok.val;
import mega.ping.client.KeyHandler;
import mega.ping.client.PingConfig;
import mega.ping.client.PingHandler;
import mega.ping.client.RenderHandler;
import mega.ping.data.PingAction;
import mega.ping.data.PingWrapper;
import mega.ping.helper.RaytraceHelper;
import mega.ping.network.PacketHandler;
import mega.ping.network.packet.ClientSendPing;
import net.minecraft.client.Minecraft;

import java.awt.*;

import static net.minecraft.util.MovingObjectPosition.MovingObjectType;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy {
    private static final int MAX_PING_RANGE = 256;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        PingHandler.register();
        KeyHandler.register();
        RenderHandler.register();
    }

    public static void doPing(PingAction action) {
        val mc = Minecraft.getMinecraft();
        val player = mc.thePlayer;

        val rayHit = RaytraceHelper.raytrace(player, MAX_PING_RANGE);
        if (rayHit == null)
            return;
        if (rayHit.typeOfHit != MovingObjectType.BLOCK)
            return;

        val world = player.worldObj;
        val posX = rayHit.blockX;
        val posY = rayHit.blockY;
        val posZ = rayHit.blockZ;

        if (world.isAirBlock(posX, posY, posZ))
            return;

        val ping = new PingWrapper(action, getPingColor(), posX, posY, posZ);
        val packet = new ClientSendPing(ping);
        PacketHandler.INSTANCE.sendToServer(packet);
    }

    private static int getPingColor() {
        val r = PingConfig.Visual.PING_OVERLAY_RED;
        val g = PingConfig.Visual.PING_OVERLAY_GREEN;
        val b = PingConfig.Visual.PING_OVERLAY_BLUE;
        val color = new Color(r, g, b);
        return color.getRGB();
    }
}
