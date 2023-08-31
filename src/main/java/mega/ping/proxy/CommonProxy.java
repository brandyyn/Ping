package mega.ping.proxy;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mega.ping.network.PacketHandler;

/**
 * @author dmillerw
 */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        PacketHandler.initialize();
    }
}
