package mega.ping.client;

import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;

public final class WorldSound extends PositionedSound {
    private static final float VOLUME = 1F;
    private static final float PITCH = 1F;
    private static final AttenuationType ATTENUATION_TYPE = AttenuationType.LINEAR;

    private WorldSound(ResourceLocation sound, int posX, int posY, int posZ) {
        super(sound);
        this.volume = VOLUME;
        this.field_147663_c = PITCH;
        this.xPosF = posX;
        this.yPosF = posY;
        this.zPosF = posZ;
        this.field_147666_i = ATTENUATION_TYPE;
    }

    public static void playSound(ResourceLocation sound, int posX, int posY, int posZ) {
        val positionedSound = new WorldSound(sound, posX, posY, posZ);

        val mc = Minecraft.getMinecraft();
        val soundHandler = mc.getSoundHandler();
        soundHandler.playSound(positionedSound);
    }
}
