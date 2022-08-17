package guard.check.checks.combat.autoclicker;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.SampleList;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;

@GuardCheckInfo(name = "AutoClicker A", category = GuardCategory.Combat, state = GuardCheckState.Coding, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class AutoClickerA extends GuardCheck {

    boolean isBlockDig;
    SampleList<Long> delay = new SampleList<>(20);
    long lastDelay;
    double lastStd;
    double lastAvg;
    long lastBlockBreak;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.ARM_ANIMATION) {
            if(!isBlockDig) {
                long now = System.currentTimeMillis();
                delay.add((now - lastDelay));
                if(System.currentTimeMillis() - lastBlockBreak < 200) delay.clear();
                if(delay.isCollected()) {
                    if(Math.abs(delay.getStandardDeviation(delay) - lastStd) < 10 && Math.abs(delay.getAverageLong(delay) - lastAvg) < 5 && System.currentTimeMillis() - lastBlockBreak > 200) {
                        fail(null, "Suspicous Clicking", "std=" + delay.getStandardDeviation(delay) + " avg=" + delay.getAverageLong(delay));
                        debug("std=" + delay.getStandardDeviation(delay) + " avg=" + delay.getAverageLong(delay));
                    }
                    lastStd = delay.getStandardDeviation(delay);
                    lastAvg = delay.getAverageLong(delay);
                }
                lastDelay = now;
            }
        }
        if(packet.getPacketId() == PacketType.Play.Client.BLOCK_DIG) {
            WrappedPacketInBlockDig dig = new WrappedPacketInBlockDig(packet.getNMSPacket());
            if(dig.getDigType() == WrappedPacketInBlockDig.PlayerDigType.START_DESTROY_BLOCK) {
                isBlockDig = true;

            }
            if(dig.getDigType() == WrappedPacketInBlockDig.PlayerDigType.STOP_DESTROY_BLOCK ||dig.getDigType() == WrappedPacketInBlockDig.PlayerDigType.ABORT_DESTROY_BLOCK) {
                if(isBlockDig) {
                    lastBlockBreak = System.currentTimeMillis();
                }
                isBlockDig = false;
            }
        }
    }
}
