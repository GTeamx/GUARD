package guard.check.checks.movement.step;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@GuardCheckInfo(name = "Step B", category = GuardCategory.Movement, state = GuardCheckState.STABLE)
public class StepB extends GuardCheck {

    private int currentTicks;
    private long lastGround;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        // TODO: Prevent false from the glitch collision in a block.
        // TODO: Prevent false from explosions.

        // To prevent false from jumping when touching vines/ladders.
        if(gp.playerGround) lastGround = System.currentTimeMillis();

        final int maxTicks = PacketEvents.get().getPlayerUtils().getClientVersion(gp.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_16) ? isExempt(ExemptType.CLIMBABLE) ? (System.currentTimeMillis() - lastGround) < 1200 ? 5 : 0 : isExempt(ExemptType.VELOCITY) ? 7 : 5 :  isExempt(ExemptType.CLIMBABLE) ? (System.currentTimeMillis() - lastGround) < 1200 ? 4 : 0 : isExempt(ExemptType.VELOCITY) ? 6 : 4;
        final boolean exempt = isExempt(ExemptType.PLACE, ExemptType.STAIRS, ExemptType.GLIDE, ExemptType.SLAB, ExemptType.SLIME, ExemptType.FLYING, ExemptType.NEAR_VEHICLE, ExemptType.TELEPORT, ExemptType.LIQUID, ExemptType.FULL_LIQUID);

        // This check also acts as a FastClimb/Ladder check since 0.11... is the max y motion on a ladder.
        if(motionY > 0.117600002289 && !gp.onLowBlock && !exempt) currentTicks++;
        if(currentTicks > maxTicks) fail(packet, "Impossible air time", "mY §9" + motionY + "\n" + " §8»§f airTicks §9" + currentTicks + "§8/§9" + maxTicks);
        if(motionY <= 0.117600002289 && !gp.isInAir() || gp.playerGround) currentTicks = 0;

    }

}
