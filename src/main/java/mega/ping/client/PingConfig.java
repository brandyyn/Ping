package mega.ping.client;

import com.falsepattern.lib.config.Config;
import com.falsepattern.lib.config.ConfigurationManager;
import lombok.experimental.UtilityClass;
import mega.ping.Tags;

@UtilityClass
public final class PingConfig {
    @UtilityClass
    @Config(modid = Tags.MODID)
    public static final class General {
        @Config.Comment("Whether to play a sound when a Ping is received")
        @Config.DefaultBoolean(true)
        @Config.LangKey("megaping.config.general.sound")
        public static boolean PLAY_PING_SOUND;

        @Config.Comment("Maximum distance a Ping can be from you and still be received")
        @Config.DefaultDouble(256)
        @Config.RangeDouble(min = 0, max = 256)
        @Config.LangKey("megaping.config.general.maxDistance")
        public static double MAX_PING_DISTANCE;

        @Config.Comment("How long a Ping should remain active before disappearing (ticks)")
        @Config.DefaultInt(100)
        @Config.RangeInt(min = 0, max = 5000)
        @Config.LangKey("megaping.config.general.pingDuration")
        public static int PING_DURATION_TICKS;

        @Config.Comment("Deadzone radius for ignoring a ping selection")
        @Config.DefaultDouble(50)
        @Config.RangeDouble(min = 0)
        @Config.LangKey("megaping.config.general.deadzone")
        public static double DEADZONE_RADIUS_PIXELS;

        static {
            ConfigurationManager.selfInit();
        }

        //Needed to force the config to load
        private static void poke() {}
    }

    @UtilityClass
    @Config(modid = Tags.MODID, category = "visual")
    public static class Visual {
        @Config.DefaultInt(0)
        @Config.RangeInt(min = 0, max = 255)
        @Config.LangKey("megaping.config.visual.red")
        public static int PING_OVERLAY_RED;

        @Config.DefaultInt(0)
        @Config.RangeInt(min = 0, max = 255)
        @Config.LangKey("megaping.config.visual.green")
        public static int PING_OVERLAY_GREEN;

        @Config.DefaultInt(100)
        @Config.RangeInt(min = 0, max = 255)
        @Config.LangKey("megaping.config.visual.blue")
        public static int PING_OVERLAY_BLUE;

        @Config.Comment("Whether to render a colored overlay on the Pinged block")
        @Config.DefaultBoolean(true)
        @Config.LangKey("megaping.config.visual.blockOverlay")
        public static boolean RENDER_BOX_OVERLAY;

        static {
            ConfigurationManager.selfInit();
        }

        //Needed to force the config to load
        private static void poke() {}
    }

    //Needed to force the config to load
    public static void poke() {
        General.poke();
        Visual.poke();
    }
}
