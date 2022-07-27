package guard.check.checks.combat.killaura;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.SampleList;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@GuardCheckInfo(name = "KillAura A", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class KillAuraA extends GuardCheck {

    SampleList<Double> sensList = new SampleList<>(4);
    SampleList<Double> sensList2 = new SampleList<>(2);
    double lastSens;
    double lastSensList;
    double actualSensMaybe;



    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity ue = new WrappedPacketInUseEntity(packet.getNMSPacket());
            if(ue.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
            }
        }
    }

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        //getSens(deltaPitch, lastDeltaPitch);
        if(gp.target != null) {
            final Location origin = gp.player.getLocation().clone();
            final Vector end = gp.target.getLocation().clone().toVector();

            final float optimalYaw = origin.setDirection(end.subtract(origin.toVector())).getYaw() % 360F;
            final float rotationYaw = gp.to.clone().getYaw();
            final float fixedRotYaw = (rotationYaw % 360F + 360F) % 360F;
            final double difference = Math.abs(fixedRotYaw - optimalYaw);
            //sendMessage("" + difference);
        }
    }

    public void getSens(float deltaPitch, float lastDeltaPitch) {
        if(deltaPitch != lastDeltaPitch) {
            float gcd = (float) gp.getGCD(Math.abs(deltaPitch), Math.abs(lastDeltaPitch));
            double modifier = Math.cbrt(0.8333 * gcd);
            double sens2 = (1.666 * modifier) - 0.3333;
            double sens = sens2 * 200;
            if(sens > 0) {
                if(sens == lastSens)
                    sensList.add(sens);

                if(sensList.isCollected()) {
                    for(int i = 0; i < sensList.size(); i++) {
                        double sens22 = sensList.get(i);
                        if(sens22 == lastSensList) {
                            sensList2.add(sens22);
                        }
                        lastSensList = sens22;
                    }
                    if(sensList2.isCollected()) {
                        double sensbetter = Math.abs((Math.round(sensList2.getAverageDouble(sensList2) * 100.0) / 100.0) - (Math.round(sensList2.getAverageDouble(sensList2))));
                        double sensaccurate = 0;
                        int senseint = 0;
                        if (sensbetter < 0.1) {
                            sensaccurate = (Math.round(sensList2.getAverageDouble(sensList2)));
                            int value = (int) sensaccurate;
                            senseint = value;
                        } else {
                            sensaccurate = Math.round((Math.round(sensList2.getAverageDouble(sensList2) * 100.0) / 100.0) - sensbetter);
                            int value = (int) sensaccurate;
                            senseint = value;
                        }
                        actualSensMaybe = senseint;
                        //Bukkit.broadcastMessage(gp.getPlayer().getName() + " SensInt=" + senseint);
                        sensList2.clear();
                    }
                    sensList.clear();
                }
                lastSens = sens;
            }
        }
    }
}
