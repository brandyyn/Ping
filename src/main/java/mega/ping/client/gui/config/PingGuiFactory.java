package mega.ping.client.gui.config;

import com.falsepattern.lib.config.SimpleGuiFactory;

import cpw.mods.fml.client.IModGuiFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Set;

/**
 * @author dmillerw
 */
public class PingGuiFactory implements SimpleGuiFactory {

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return PingGuiConfig.class;
    }
}
