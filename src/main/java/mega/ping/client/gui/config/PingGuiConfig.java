package mega.ping.client.gui.config;


import com.falsepattern.lib.config.ConfigException;
import com.falsepattern.lib.config.SimpleGuiConfig;
import mega.ping.Tags;
import mega.ping.client.PingConfig;

import net.minecraft.client.gui.GuiScreen;


/**
 * @author dmillerw
 */
public class PingGuiConfig extends SimpleGuiConfig {
    public PingGuiConfig(GuiScreen parent) throws ConfigException {
        super(parent, Tags.MOD_ID, Tags.MOD_NAME, PingConfig.General.class, PingConfig.Visual.class);
    }
}
