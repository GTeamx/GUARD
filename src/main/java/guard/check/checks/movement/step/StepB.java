package guard.check.checks.movement.step;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.check.CheckState;
import guard.exempt.ExemptType;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Step B", category = Category.Movement, state = CheckState.STABLE)
public class StepB extends Check {

    private int currentTicks;
    private long lastGround;
    private long lastJumpBoost = 0;

    public void onMove(PacketReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        // TODO: Prevent false from the glitch collision in a block.
        // TODO: Prevent false from explosions.
        // TODO: Prevent false from Velocity

        // To prevent false from jumping when touching vines/ladders.
        if(gp.playerGround) lastGround = System.currentTimeMillis();

        // Proper handling for JumpBoost.
        if(gp.getPotionEffectAmplifier(PotionEffectType.JUMP) > 0) lastJumpBoost = System.currentTimeMillis();
        final int extraTicks = lastJumpBoost - System.currentTimeMillis() >= 1500 ? 0 : gp.getPotionEffectAmplifier(PotionEffectType.JUMP);

        final int maxTicks = (PacketEvents.getAPI().getPlayerManager().getClientVersion(gp.getPlayer()).isNewerThanOrEquals(ClientVersion.V_1_16) ? isExempt(ExemptType.CLIMBABLE) ? (System.currentTimeMillis() - lastGround) < 1200 ? 5 : 0 : isExempt(ExemptType.VELOCITY) ? 7 : 5 :  isExempt(ExemptType.CLIMBABLE) ? (System.currentTimeMillis() - lastGround) < 1200 ? 4 : 0 : isExempt(ExemptType.VELOCITY) ? 6 : 4) + extraTicks;
        final boolean exempt = isExempt(ExemptType.PLACE, ExemptType.STAIRS, ExemptType.GLIDE, ExemptType.SLAB, ExemptType.SLIME, ExemptType.FLYING, ExemptType.NEAR_VEHICLE, ExemptType.TELEPORT, ExemptType.LIQUID, ExemptType.FULL_LIQUID);

        // This check also acts as a FastClimb/Ladder check since 0.11... is the max y motion on a ladder.
        if(motionY > 0.117600002289 && !gp.onLowBlock && !exempt) currentTicks++;
        if(currentTicks > maxTicks) fail(packet, "Impossible air time", "mY §9" + motionY + "\n" + " §8»§f airTicks §9" + currentTicks + "§8/§9" + maxTicks);
        if(motionY <= 0.117600002289 && !gp.isInAir() || gp.playerGround) currentTicks = 0;

    }

}
