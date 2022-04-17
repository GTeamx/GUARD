package guard.exempt;

import guard.data.PlayerData;
import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;

import java.util.function.Function;

@Getter
public enum ExemptType {
    TELEPORT(data -> data.isTeleporting || System.currentTimeMillis() - data.joined < 2000L),
    AIR(data -> data.inAir),
    GROUND(data -> data.onSolidGround && data.to.getY() % 0.015625 == 0.0),
    PACKET_GROUND(data -> data.playerGround),
    TPS(data -> data.getTPS() > 18.5D),
    JOINED(data -> System.currentTimeMillis() - data.joined < 5000L),
    TRAPDOOR(data -> data.nearTrapdoor),
    CHUNK(data -> !data.getPlayer().getWorld().isChunkLoaded(data.getPlayer().getLocation().getBlockX() << 4, data.getPlayer().getLocation().getBlockZ() << 4)),
    STEPPED(data -> data.playerGround && data.motionY > 0),
    SLAB(data -> data.isonSlab),
    STAIRS(data -> data.isonStair),
    WEB(data -> data.inweb),
    CLIMBABLE(data -> data.onClimbable),
    SLIME(data -> data.sinceSlimeTicks < 30),
    NEAR_VEHICLE(data -> data.nearboat),
    INSIDE_VEHICLE(data -> System.currentTimeMillis() - data.lastnearboat < 20),
    LIQUID(data -> data.isInLiquid),
    BLOCK_ABOVE(data -> data.blockabove),
    PISTON(data -> data.nearPiston),
    VOID(data -> data.getPlayer().getLocation().getY() < 4),
    DEPTH_STRIDER(data -> data.getDepthStriderLevel() > 0),
    FLYING(data -> System.currentTimeMillis() - data.lastflyingtime < 3000L),
    VELOCITY(data -> System.currentTimeMillis() - data.lastvelocity < 200L);

    private final Function<PlayerData, Boolean> exception;

    ExemptType(final Function<PlayerData, Boolean> exception) {
        this.exception = exception;
    }

}
