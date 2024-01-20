package mega.ping.api.events;

import mega.ping.data.PingWrapper;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.eventhandler.Event;


public class PingEvent extends Event {
    public final EntityPlayer player;
    public final Block        blockPinged;
    public final PingWrapper  ping;

    public PingEvent(EntityPlayer player, Block blockPinged, PingWrapper ping) {
        this.player      = player;
        this.blockPinged = blockPinged;
        this.ping        = ping;
    }
}
