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
