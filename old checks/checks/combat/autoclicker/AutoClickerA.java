package guard.check.checks.combat.autoclicker;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.SampleList;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.windowclick.WrappedPacketInWindowClick;

@GuardCheckInfo(name = "AutoClicker A", category = GuardCategory.Combat, state = GuardCheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class AutoClickerA extends GuardCheck {

    boolean isBlockDig;
    SampleList<Long> delay = new SampleList<>(20);
    long lastDelay;
    double lastStd;
    double lastAvg;
    long lastBlockBreak;
    boolean isExempt = false;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.ARM_ANIMATION) {
            if(!isBlockDig) {
                long now = System.currentTimeMillis();
                delay.add((now - lastDelay));
                if(System.currentTimeMillis() - lastBlockBreak < 200) delay.clear();
                if(delay.isCollected() && !isExempt) {
                    if(Math.abs(delay.getStandardDeviation(delay) - lastStd) < 10 && Math.abs(delay.getAverageLong(delay) - lastAvg) < 5 && System.currentTimeMillis() - lastBlockBreak > 200) {
                        fail(null, "Suspicious clicking pattern", "std §9" + delay.getStandardDeviation(delay) + "\n" + " §8»§f avg §9" + delay.getAverageLong(delay));
                        debug("std=" + delay.getStandardDeviation(delay) + " avg=" + delay.getAverageLong(delay));
                    }
                    lastStd = delay.getStandardDeviation(delay);
                    lastAvg = delay.getAverageLong(delay);
                }
                lastDelay = now;
            } else {
                if(!delay.isEmpty())
                    delay.removeFirst();
            }
        }

        if(packet.getPacketId() == PacketType.Play.Client.WINDOW_CLICK) {
            isExempt = true;
        } else isExempt = false;

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
