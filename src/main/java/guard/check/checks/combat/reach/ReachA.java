package guard.check.checks.combat.reach;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
@GuardCheckInfo(name = "Reach A", category = GuardCategory.Combat, state = GuardCheckState.UNSTABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class ReachA extends GuardCheck {



    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity useEntity = new WrappedPacketInUseEntity(packet.getNMSPacket());
            if(useEntity.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                if(gp.target != null) {

                }
            }
        }
    }
}
