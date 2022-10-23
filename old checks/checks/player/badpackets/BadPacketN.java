package guard.check.checks.player.badpackets;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.steervehicle.WrappedPacketInSteerVehicle;

@GuardCheckInfo(name = "BadPacket N", category = GuardCategory.Player, state = GuardCheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class BadPacketN extends GuardCheck {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.STEER_VEHICLE) {
            WrappedPacketInSteerVehicle wrapper = new WrappedPacketInSteerVehicle(packet.getNMSPacket());
            float forwards = Math.abs(wrapper.getForwardValue());
            float sideways = Math.abs(wrapper.getSideValue());
            if (forwards > 0.98f || sideways > 0.98f) fail(packet, "Impossible packet", "fw §9" + forwards + "\n" + " §8»§f sw §9" + sideways); else removeBuffer();
        }
    }

}
