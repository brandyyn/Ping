package mega.ping.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import mega.ping.data.PingWrapper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;


@Getter
@RequiredArgsConstructor
@Accessors(fluent = true, chain = false)
public final class PingEvent extends Event {
    private final EntityPlayer player;
    private final Block blockPinged;
    private final PingWrapper ping;
}
