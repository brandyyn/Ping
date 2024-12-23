/*
 * MEGA Ping
 *
 * Copyright (C) 2023-2024 FalsePattern, the MEGA team
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package mega.ping.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author dmillerw
 */
public class PingWrapper {

    public static PingWrapper readFromBuffer(ByteBuf buffer) {
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        int color = buffer.readInt();
        PingType type = PingType.values()[buffer.readInt()];
        return new PingWrapper(x, y, z, color, type);
    }

    public final int x;
    public final int y;
    public final int z;

    public final int color;

    public final PingType type;

    public boolean isOffscreen = false;

    public float screenX;
    public float screenY;
    public boolean behind;

    public int animationTimer = 20;
    public int timer;

    public PingWrapper(int x, int y, int z, int color, PingType type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.type = type;
    }

    public AxisAlignedBB getAABB() {
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
    }

    public void writeToBuffer(ByteBuf buffer) {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeInt(color);
        buffer.writeInt(type.ordinal());
    }
}
