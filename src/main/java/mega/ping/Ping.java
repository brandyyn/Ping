package mega.ping;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import mega.ping.client.PingConfig;
import mega.ping.proxy.CommonProxy;

/**
 * @author dmillerw
 */
@Mod(modid = Tags.MODID,
     name = Tags.MODNAME,
     version = Tags.VERSION,
     acceptedMinecraftVersions="[1.7.10]",
     guiFactory = Tags.GROUPNAME + ".client.gui.config.PingGuiFactory",
     dependencies = "required-after:falsepatternlib@[0.12,);")
public class Ping {
    @SidedProxy(serverSide = Tags.GROUPNAME + ".proxy.CommonProxy", clientSide = Tags.GROUPNAME + ".proxy.ClientProxy")
    public static CommonProxy proxy;

    static {
        PingConfig.poke();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }
}
