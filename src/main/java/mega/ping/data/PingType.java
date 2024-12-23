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

/**
 * @author dmillerw
 */
public enum PingType {

    BACKGROUND,
    LOOK,
    MINE,
    ALERT,
    GOTO;


    public final String langKey = "ping.action." + name().toLowerCase();
    public final float minU;
    public final float minV;
    public final float maxU;
    public final float maxV;

    PingType() {
        int x = 32 * ordinal();
        int y = 0;
        float f = (float) (0.009999999776482582D / (double) 256);
        float f1 = (float) (0.009999999776482582D / (double) 256);
        this.minU = (float) x / (float) ((double) 256) + f;
        this.maxU = (float) (x + 32) / (float) ((double) 256) - f;
        this.minV = (float) y / (float) 256 + f1;
        this.maxV = (float) (y + 32) / (float) 256 - f1;
    }
}
