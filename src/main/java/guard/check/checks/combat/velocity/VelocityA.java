package guard.check.checks.combat.velocity;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.out.entityvelocity.WrappedPacketOutEntityVelocity;
import io.github.retrooper.packetevents.utils.vector.Vector3d;

@GuardCheckInfo(name = "Velocity A", category = GuardCategory.Combat, state = GuardCheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 1, maxBuffer = 3)
public class VelocityA extends GuardCheck {

    Vector3d veloVector;
    public int ticks;
    boolean hit;
    boolean wasNotZero;
    boolean checkedPlayer;
    int lastTicks;

    public void onPacketSend(PacketPlaySendEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Server.ENTITY_VELOCITY) {
            WrappedPacketOutEntityVelocity velo = new WrappedPacketOutEntityVelocity(packet.getNMSPacket());
            if(velo.getEntityId() == gp.player.getEntityId()) {
                veloVector = velo.getVelocity();
                hit = true;
            }
        }
    }


    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.BLOCK_ABOVE);
        if(!exempt) {
            boolean lastPG = gp.lastPlayerGround;
            boolean jumped = !gp.playerGround && lastPG;
            if(hit) {
                if(jumped || (ticks++ > (4 + gp.transactionPing / 50))) {
                    double predictedVelocityY = veloVector.y;
                    double percentage = motionY / predictedVelocityY * 100;
                    debug("percentage=" + percentage);
                    if ((percentage < 99.999 || percentage > 153) && percentage >= 0 && !String.valueOf(percentage).equals("-0.0")) {
                        fail(null, "Modified vertical velocity", "velocity §9" + percentage + "%");
                    } else removeBuffer();
                    ticks = 0;
                    hit = false;
                }
            }

        }
        lastTicks = ticks;
    }
}
