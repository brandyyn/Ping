package mega.ping.api;

import net.minecraft.util.ResourceLocation;

public interface PingAction {
    String defaultName();

    String unlocalizedName();

    int durationTicks();



    ResourceLocation outlineTexture();

    ResourceLocation backgroundTexture();

    ResourceLocation iconTexture();

    boolean renderBeam();

    ResourceLocation appearSound();

    ResourceLocation disappearSound();
}
