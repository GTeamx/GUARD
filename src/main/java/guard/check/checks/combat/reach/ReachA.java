package guard.check.checks.combat.reach;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.BoundingBox;
import guard.utils.RayTrace;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

import java.util.Optional;

@GuardCheckInfo(name = "Reach A", category = GuardCategory.Combat, state = GuardCheckState.Coding, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class ReachA extends GuardCheck {



    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity useEntity = new WrappedPacketInUseEntity(packet.getNMSPacket());
            if(useEntity.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                if(gp.target != null) {
                    BoundingBox playerBox = new BoundingBox(gp.player);
                    double lowestIsCollided = 2222;
                    for(BoundingBox target : gp.targetBoundingBoxes) {
                        if((target.isCollided(new RayTrace(gp.player), 3.05, 0) - 0.3f) < lowestIsCollided) {
                            lowestIsCollided = (target.isCollided(new RayTrace(gp.player), 3.05, 0) - 0.3f);
                        }
                    }
                    boolean bb = gp.targetBoundingBoxes.stream().map(boundingBox -> boundingBox.expand(3, 3, 3).isCollided(playerBox)).findFirst().orElse(true);
                    if(lowestIsCollided > 3.02 && !bb) {
                        buffer++;
                        if(buffer > 2) {
                            debug("collided=" + bb + " isCollided=" + lowestIsCollided);
                        }
                    } else {
                        if(buffer > 0) buffer -= 1;
                    }
                }
            }
        }
    }
}
