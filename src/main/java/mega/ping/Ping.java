package mega.ping;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import mega.ping.client.PingConfig;
import mega.ping.proxy.CommonProxy;

/**
 * @author dmillerw
 */
@Mod(modid = Tags.MOD_ID,
     name = Tags.MOD_NAME,
     version = Tags.MOD_VERSION,
     acceptedMinecraftVersions="[1.7.10]",
     guiFactory = Tags.ROOT_PKG + ".client.gui.config.PingGuiFactory",
     dependencies = "required-after:falsepatternlib@[1.5.5,);")
public class Ping {
    @SidedProxy(serverSide = Tags.ROOT_PKG + ".proxy.CommonProxy", clientSide = Tags.ROOT_PKG + ".proxy.ClientProxy")
    public static CommonProxy proxy;

    static {
        PingConfig.poke();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }
}
