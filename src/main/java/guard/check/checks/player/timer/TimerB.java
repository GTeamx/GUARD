package guard.check.checks.player.timer;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.SampleList;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "Timer B", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0.13, maxBuffer = 1)
public class TimerB extends GuardCheck {

    double bal;
    double lastBal;
    long lastMS;
    long joinTime;
    boolean wasFirst;
    SampleList<Long> balls = new SampleList<>(3);

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        //if(packet.getPacketId() == PacketType.Play.Client.POSITION || packet.getPacketId() == PacketType.Play.Client.POSITION_LOOK || packet.getPacketId() == PacketType.Play.Client.LOOK || packet.getPacketId() == PacketType.Play.Client.FLYING) {
        long now = System.currentTimeMillis();
        long rate = (System.currentTimeMillis() - lastMS);
        bal += 50 - rate;
        if(!wasFirst) {
            wasFirst = true;
            joinTime = now;
        }
        if(now - joinTime < 3000) {
            bal = 10;
        }
        if(String.valueOf(bal).contains("E")) {
            bal = 0;
        }
        if(bal > 10) {
            bal = 2;
        }
        if(bal < -3) {
            balls.add(50 - rate);
            if(balls.isCollected()) {
                if(balls.getAverageLong(balls) < 0) {
                    fail(packet, "Sent less Move Packets than Normal", bal);
                    bal = 0;
                } else if(balls.getAverageLong(balls) > 0){
                    removeBuffer();
                }
            }
        }
        if(bal < -12) {
            //fail(packet, "Sent less Move Packets than Normal", bal);
            //bal = 0;
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
            if(!wasFirst) {
                wasFirst = true;
                joinTime = now;
            }
            if(now - joinTime < 6000) {
                bal = 0;
            }
            lastMS = now;
        }
    }


    public void onPacketSend(PacketPlaySendEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Server.POSITION) {
            bal -= 50;
        }
    }
}
