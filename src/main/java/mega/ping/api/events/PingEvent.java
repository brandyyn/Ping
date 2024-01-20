package mega.ping.api.events;

import mega.ping.data.PingWrapper;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Event;


public class PingEvent extends Event {
    public final World world;
    public final Block blockPinged;
    public final PingWrapper ping;

    public PingEvent(World world, Block blockPinged, PingWrapper ping) {
        this.world       = world;
        this.blockPinged = blockPinged;
        this.ping        = ping;
    }
}
