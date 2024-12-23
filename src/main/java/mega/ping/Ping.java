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

package mega.ping;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import mega.ping.client.PingConfig;
import mega.ping.proxy.CommonProxy;

/**
 * @author dmillerw
 */
@Mod(modid = Tags.MOD_ID,
     name = Tags.MOD_NAME,
     version = Tags.MOD_VERSION,
     acceptedMinecraftVersions="[1.7.10]",
     guiFactory = Tags.ROOT_PKG + ".client.gui.config.PingGuiFactory",
     dependencies = "required-after:falsepatternlib@[1.5.5,);")
public class Ping {
    @SidedProxy(serverSide = Tags.ROOT_PKG + ".proxy.CommonProxy", clientSide = Tags.ROOT_PKG + ".proxy.ClientProxy")
    public static CommonProxy proxy;

    static {
        PingConfig.poke();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }
}
