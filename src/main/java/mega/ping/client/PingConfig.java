package mega.ping.client;

import com.falsepattern.lib.config.Config;
import com.falsepattern.lib.config.ConfigurationManager;
import mega.ping.Tags;

public class PingConfig {
    @Config(modid = Tags.MODID)
    public static class General {
        @Config.Comment("Whether to play a sound when a Ping is received")
        @Config.DefaultBoolean(true)
        public static boolean sound;

        @Config.Comment("Maximum distance a Ping can be from you and still be received")
        @Config.DefaultDouble(256)
        @Config.RangeDouble(min = 0, max = 256)
        public static double pingAcceptDistance;

        @Config.Comment("How long a Ping should remain active before disappearing (ticks)")
        @Config.DefaultInt(100)
        @Config.RangeInt(min = 0, max = 5000)
        public static int pingDuration;

        static {
            ConfigurationManager.selfInit();
        }
    }

    @Config(modid = Tags.MODID,
            category = "visual")
    public static class Visual {
        @Config.DefaultInt(0)
        @Config.RangeInt(min = 0, max = 255)
        public static int red;

        @Config.DefaultInt(0)
        @Config.RangeInt(min = 0, max = 255)
        public static int green;

        @Config.DefaultInt(100)
        @Config.RangeInt(min = 0, max = 255)
        public static int blue;

        @Config.Comment("Whether to render a colored overlay on the Pinged block")
        @Config.DefaultBoolean(true)
        public static boolean blockOverlay;

        static {
            ConfigurationManager.selfInit();
        }
    }
}
