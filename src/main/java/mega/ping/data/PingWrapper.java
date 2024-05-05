package mega.ping.data;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author dmillerw
 */
@Getter
@RequiredArgsConstructor
@Accessors(fluent = true, chain = false)
public final class PingWrapper {
    private final PingAction action;

    private final int color;

    private final int posX;
    private final int posY;
    private final int posZ;

    public boolean isOffscreen = false;

    public float screenX;
    public float screenY;

    public int animationTimer = 20;
    @Setter
    private int timer;

    public static PingWrapper readFromBuffer(ByteBuf buffer) {
        val action = PingAction.values()[buffer.readInt()];
        val color = buffer.readInt();
        val posX = buffer.readInt();
        val posY = buffer.readInt();
        val posZ = buffer.readInt();
        return new PingWrapper(action, color, posX, posY, posZ);
    }

    public void writeToBuffer(ByteBuf buffer) {
        buffer.writeInt(action.ordinal());
        buffer.writeInt(color);
        buffer.writeInt(posX);
        buffer.writeInt(posY);
        buffer.writeInt(posZ);
    }

    public AxisAlignedBB getAABB() {
        return AxisAlignedBB.getBoundingBox(posX + 0.5, posY + 0.5, posZ + 0.5, posX + 0.5, posY + 0.5, posZ + 0.5);
    }
}
