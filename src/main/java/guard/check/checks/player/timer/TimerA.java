package guard.check.checks.player.timer;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import guard.utils.SampleList;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "Timer A", category = GuardCategory.Player, state = GuardCheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0.02, maxBuffer = 2)
public class TimerA extends GuardCheck {
    double bal;
    double lastBal;
    long lastMS;
    long joinTime;
    boolean wasFirst;
    SampleList<Long> balls = new SampleList<>(3);
    SampleList<Long> times = new SampleList<>(5);
    double balAvg;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        //if(packet.getPacketId() == PacketType.Play.Client.POSITION || packet.getPacketId() == PacketType.Play.Client.POSITION_LOOK || packet.getPacketId() == PacketType.Play.Client.LOOK || packet.getPacketId() == PacketType.Play.Client.FLYING) {
        long now = System.currentTimeMillis();
        long rate = (System.currentTimeMillis() - lastMS);
        bal += 50 - rate;
        if(now - gp.weirdTeleport < 1000 || isExempt(ExemptType.TELEPORT)) {
            bal = -50;
        }
        if(!wasFirst) {
            wasFirst = true;
            joinTime = now;
        }
        if(now - joinTime < 3000) {
            bal = -40;
        }
        if(String.valueOf(bal).contains("E")) {
            bal = -5;
        }
        if(String.valueOf(balAvg).contains("E")) {
            balAvg = -5;
        }
        if(Math.abs(rate) >= 48 && Math.abs(rate) <= 52 && bal < -10) {
            balls.add(rate);
            if(balls.isCollected()) {
                if (Math.abs(balls.getAverageLong(balls) - Math.abs(rate)) < 20) {
                    bal = -10;
                }
            }
        }
        times.add(rate);
        if(times.isCollected()) {

            balAvg += 50 - times.getAverageLong(times);
            //sendMessage("balAvg=" + balAvg);
            if(balAvg > 10) {
                if(times.getStandardDeviation(times) < 10 && bal > 9) {
                    //sendMessage("avg=" + times.getAverageLong(times) + " std=" + times.getStandardDeviation(times));
                }
                balAvg = 0;
            }
        }
        if(bal > 15) {
            fail(packet, "Sent packets too fast", "bal §9" + bal);
            bal = -2;
        } else removeBuffer();
        lastMS = now;
        lastBal = bal;
        //}
    }

    @Override
    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.FLYING || packet.getPacketId() == PacketType.Play.Client.LOOK) {
            long now = System.currentTimeMillis();
            double rate = (System.currentTimeMillis() - lastMS);
            bal += 50 - rate;
            if(now - gp.weirdTeleport < 1000 || isExempt(ExemptType.TELEPORT)) {
                bal = -50;
            }
            if(!wasFirst) {
                wasFirst = true;
                joinTime = now;
            }
            if(now - joinTime < 6000) {
                bal = -40;
            }
            if(bal > 15) {
                fail(packet, "Sent too many Move Packets", bal);
                bal = -5;
            }else removeBuffer();
            lastMS = now;
        }
    }


    public void onPacketSend(PacketPlaySendEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Server.POSITION) {
            bal -= 50;
        }
    }
}
