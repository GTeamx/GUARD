package guard.check;

import guard.Guard;
import guard.data.GuardPlayer;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.FutureTask;

public class PredictionProcessor {
    GuardPlayer gp;
    boolean isSprinting;
    boolean isSneaking;
    boolean isAttacking;
    boolean[] bools = new boolean[] {true, false};
    public PredictionProcessor(GuardPlayer gp) {
        this.gp = gp;
    }

    public void handle(PacketPlayReceiveEvent event) {
        if(event.getPacketId() == PacketType.Play.Client.ENTITY_ACTION) {
            WrappedPacketInEntityAction action = new WrappedPacketInEntityAction(event.getNMSPacket());
            if(action.getAction() == WrappedPacketInEntityAction.PlayerAction.START_SPRINTING) {
                isSprinting = true;
            }
            if(action.getAction() == WrappedPacketInEntityAction.PlayerAction.STOP_SPRINTING) {
                isSprinting = false;
            }
            if(action.getAction() == WrappedPacketInEntityAction.PlayerAction.START_SNEAKING) {
                isSneaking = true;
            }
            if(action.getAction() == WrappedPacketInEntityAction.PlayerAction.STOP_SNEAKING) {
                isSneaking = false;
            }
        }
        if(event.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity ue = new WrappedPacketInUseEntity(event.getNMSPacket());
            if(ue.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                isAttacking = true;
            }
        } else {
            isAttacking = false;
        }
    }

    public double[] predictUrAssOff() {
        double sdelta = 983475234;
        float frictionblock = getFriction(getBlock(gp.to.clone().subtract(0, 1, 0)));
        if (gp.onClimbable || System.currentTimeMillis() - gp.entityHit < 300L || gp.isInLiquid || gp.collidesHorizontally) {
            return new double[]{0, 0, 0};
        }
        double playerMotionX = 0;
        double playerMotionZ = 0;
        boolean step = mathOnGround(gp.motionY) && mathOnGround(gp.from.getY());
        boolean jumped = gp.motionY > 0 && gp.from.getY() % (1D / 64) == 0 && !gp.playerGround && !step;
        for (int f = -1; f < 2; f++) {
            for (int s = -1; s < 2; s++) {
                for (boolean using : bools) {

                    float forwardMotion = f, strafeMotion = s;
                    if (isSneaking) {
                        forwardMotion *= 0.3;
                        strafeMotion *= 0.3;
                    }
                    if (using) {
                        forwardMotion *= 0.2;
                        strafeMotion *= 0.2;
                    }
                    forwardMotion *= 0.98;
                    strafeMotion *= 0.98;

                    float walkspeed = gp.getPlayer().getWalkSpeed() / 2f;
                    float friction = 0.91f;
                    double lastmotionX = gp.lastMotionX;
                    double lastmotionZ = gp.lastMotionZ;

                    lastmotionX *= (gp.lastLastPlayerGround ? 0.6 : 1) * 0.91;
                    lastmotionZ *= (gp.lastLastPlayerGround ? 0.6 : 1) * 0.91;

                    if (PacketEvents.get().getPlayerUtils().getClientVersion(gp.player).isNewerThanOrEquals(ClientVersion.v_1_9)) {
                        if (Math.abs(lastmotionX) < 0.003)
                            lastmotionX = 0;
                        if (Math.abs(lastmotionZ) < 0.003)
                            lastmotionZ = 0;
                    } else {
                        if (Math.abs(lastmotionX) < 0.005)
                            lastmotionX = 0;
                        if (Math.abs(lastmotionZ) < 0.005)
                            lastmotionZ = 0;
                    }
                    if (isAttacking) {
                        lastmotionX *= 0.6;
                        lastmotionZ *= 0.6;
                    }
                    if (isSprinting) walkspeed += walkspeed * 0.3f;
                    if (gp.hasPotionEffect(PotionEffectType.SPEED))
                        walkspeed += (gp.getEffectByType(PotionEffectType.SPEED)
                                .get()
                                .getAmplifier() + 1) * (double) 0.2f * walkspeed;
                    if (gp.hasPotionEffect(PotionEffectType.SLOW))
                        walkspeed += (gp.getEffectByType(PotionEffectType.SLOW)
                                .get()
                                .getAmplifier() + 1) * (double) -0.15f * walkspeed;
                    float frictionwalk;
                    if (gp.onSolidGround) {
                        friction *= frictionblock;

                        frictionwalk = (float) (walkspeed * (0.16277136F / Math.pow(friction, 3)));

                        if (jumped && isSprinting) {
                            float rot = gp.to.clone().getYaw() * 0.017453292F;
                            lastmotionX -= Math.sin(rot) * 0.2F;
                            lastmotionZ += Math.cos(rot) * 0.2F;
                        }

                    } else {
                        frictionwalk = isSprinting ? 0.026f : 0.02f;
                    }

                    double keyMotion = forwardMotion * forwardMotion + strafeMotion * strafeMotion;

                    if (keyMotion >= 1.0E-4F) {
                        keyMotion = frictionwalk / Math.max(1.0, Math.sqrt(keyMotion));
                        forwardMotion *= keyMotion;
                        strafeMotion *= keyMotion;

                        final float yaws = (float) Math.sin(gp.to.clone().getYaw() * (float) Math.PI / 180.F),
                                yawc = (float) Math.cos(gp.to.clone().getYaw() * (float) Math.PI / 180.F);

                        lastmotionX += ((strafeMotion * yawc) - (forwardMotion * yaws));
                        lastmotionZ += ((forwardMotion * yawc) + (strafeMotion * yaws));
                    }

                    double delta = Math.pow(gp.motionX - lastmotionX, 2)
                            + Math.pow(gp.motionZ - lastmotionZ, 2);

                    if (delta < sdelta) {
                        sdelta = delta;
                        playerMotionX = lastmotionX;
                        playerMotionZ = lastmotionZ;
                    }
                    sdelta = Math.min(delta, sdelta);
                }
            }
        }

        double playermotion = Math.hypot(playerMotionX, playerMotionZ);
        gp.pMotionX = playerMotionX;
        gp.pMotionZ = playerMotionZ;
        return new double[]{playermotion, sdelta};
    }

    public static float getFriction(Block block) {
        Optional<Material> matched = Optional.of(block.getType());

        switch (matched.get()) {
            case SLIME_BLOCK:
                return 0.8f;
            case ICE:
            case BLUE_ICE:
            case FROSTED_ICE:
            case PACKED_ICE:
                return 0.98f;
            case AIR:
                return 0f;
            default:
                return 0.6f;
        }
    }

    public boolean mathOnGround(final double posY) {
        return posY % 0.015625 == 0;
    }

    public Block getBlock(final Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getBlock();
        } else {
            FutureTask<Block> futureTask = new FutureTask<>(() -> {
                location.getWorld().loadChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4);
                return location.getBlock();
            });
            Bukkit.getScheduler().runTask(Guard.instance, futureTask);
            try {
                return futureTask.get();
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }
    }
}
