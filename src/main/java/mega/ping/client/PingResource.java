package mega.ping.client;

import lombok.Getter;
import lombok.experimental.Accessors;
import mega.ping.data.PingAction;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

import static mega.ping.Tags.MODID;

@Getter
@Accessors(fluent = true, chain = false)
public enum PingResource {
    GOTO, LOOK, ALERT, MINE;

    private static final ResourceLocation BEAM_TEXTURE = getTexture("ping_beam");

    private final PingAction action = PingAction.values()[ordinal()];

    private final String internalName = action.internalName();
    private final String langKey = action.unlocalizedName();

    private final ResourceLocation boxTexture = getTexture("overlays/" + internalName + "_box");
    private final ResourceLocation backgroundTexture = getTexture("overlays/" + internalName + "_bg");
    private final ResourceLocation foregroundTexture = getTexture("overlays/" + internalName + "_fg");

    private final ResourceLocation appearSound = getSound(internalName + "_appear");
    private final ResourceLocation disappearSound = getSound(internalName + "_disappear");

    {
        if (!Objects.equals(action.internalName(), internalName))
            throw new AssertionError("I messed up big time.");
    }

    public static ResourceLocation beamTexture() {
        return BEAM_TEXTURE;
    }

    private static ResourceLocation getTexture(String name) {
        return new ResourceLocation(MODID, "textures/" + name + ".png");
    }

    private static ResourceLocation getSound(String name) {
        return new ResourceLocation(MODID, name);
    }
}
