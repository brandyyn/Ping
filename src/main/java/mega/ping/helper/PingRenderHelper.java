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

package mega.ping.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

/**
 * @author dmillerw
 */
public class PingRenderHelper {

    public static void drawBlockOverlay(float width, float height, float length, IIcon icon, int color, int alpha) {
        Tessellator tessellator = Tessellator.instance;

        tessellator.startDrawingQuads();

        tessellator.setColorRGBA_I(color, alpha);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

        tessellator.setBrightness(Integer.MAX_VALUE);

        // TOP
        tessellator.addVertexWithUV(-(width / 2),  (height / 2), -(length / 2), icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV( (width / 2),  (height / 2), -(length / 2), icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV( (width / 2),  (height / 2),  (length / 2), icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(-(width / 2),  (height / 2),  (length / 2), icon.getMinU(), icon.getMaxV());

        // BOTTOM
        tessellator.addVertexWithUV(-(width / 2), -(height / 2),  (length / 2), icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV( (width / 2), -(height / 2),  (length / 2), icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV( (width / 2), -(height / 2), -(length / 2), icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(-(width / 2), -(height / 2), -(length / 2), icon.getMinU(), icon.getMinV());

        // NORTH
        tessellator.addVertexWithUV(-(width / 2),  (height / 2),  (length / 2), icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV( (width / 2),  (height / 2),  (length / 2), icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV( (width / 2), -(height / 2),  (length / 2), icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(-(width / 2), -(height / 2),  (length / 2), icon.getMinU(), icon.getMinV());

        // SOUTH
        tessellator.addVertexWithUV(-(width / 2), -(height / 2), -(length / 2), icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV( (width / 2), -(height / 2), -(length / 2), icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV( (width / 2),  (height / 2), -(length / 2), icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(-(width / 2),  (height / 2), -(length / 2), icon.getMinU(), icon.getMaxV());

        // EAST
        tessellator.addVertexWithUV(-(width / 2),  (height / 2), -(length / 2), icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(-(width / 2),  (height / 2),  (length / 2), icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(-(width / 2), -(height / 2),  (length / 2), icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(-(width / 2), -(height / 2), -(length / 2), icon.getMinU(), icon.getMinV());

        // WEST
        tessellator.addVertexWithUV( (width / 2), -(height / 2), -(length / 2), icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV( (width / 2), -(height / 2),  (length / 2), icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV( (width / 2),  (height / 2),  (length / 2), icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV( (width / 2),  (height / 2), -(length / 2), icon.getMinU(), icon.getMaxV());

        tessellator.draw();
    }
}
