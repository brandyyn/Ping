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
