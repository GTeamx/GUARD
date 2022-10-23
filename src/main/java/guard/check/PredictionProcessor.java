package guard.check;

import guard.Guard;
import guard.data.GuardPlayer;
import guard.exempt.ExemptType;
import guard.utils.BoundingBox;
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

import java.util.Optional;
import java.util.concurrent.FutureTask;

public class PredictionProcessor {
    GuardPlayer gp;
    boolean isSprinting;
    boolean isSneaking;
    boolean isAttacking;
    double predictedMotionX;
    double predictedMotionY;
    double predictedMotionZ;
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
            default:
                return 0.6f;
        }
    }

    public double[] newPrediction() {
        double playerMotionX = gp.lastMotionX;
        double playerMotionY = gp.lastMotionY;
        double playerMotionZ = gp.lastMotionZ;
        double playerMotionX2 = gp.lastMotionX;
        double playerMotionY2 = gp.lastMotionY;
        double playerMotionZ2 = gp.lastMotionZ;
        double fallDistance = gp.lastFallDistance;
        double smallestDelta = Double.MAX_VALUE;
        double smallestYDelta = Double.MAX_VALUE;
        for (int forward = -1; forward < 2; forward++) {
            for (int strafe = -1; strafe < 2; strafe++) {
                float forwardMotion = forward, strafeMotion = strafe;
                if (isSneaking) {
                    forwardMotion *= 0.3;
                    strafeMotion *= 0.3;
                }
                forwardMotion *= 0.98;
                strafeMotion *= 0.98;
                float walkspeed = gp.getPlayer().getWalkSpeed() / 2f;
                if (gp.hasPotionEffect(PotionEffectType.SPEED))
                    walkspeed += (gp.getEffectByType(PotionEffectType.SPEED)
                            .get()
                            .getAmplifier() + 1) * (double) 0.2f * walkspeed;
                if (gp.hasPotionEffect(PotionEffectType.SLOW))
                    walkspeed += (gp.getEffectByType(PotionEffectType.SLOW)
                            .get()
                            .getAmplifier() + 1) * (double) -0.15f * walkspeed;
                boolean step = mathOnGround(gp.motionY) && mathOnGround(gp.from.getY());
                boolean jumped = gp.motionY > 0 && gp.from.getY() % (1D / 64) == 0 && !gp.playerGround && !step;
                if (PacketEvents.get().getPlayerUtils().getClientVersion(gp.player).isNewerThanOrEquals(ClientVersion.v_1_9)) {
                    if (Math.abs(playerMotionX) < 0.003)
                        playerMotionX = 0;
                    if (Math.abs(playerMotionZ) < 0.003)
                        playerMotionZ = 0;
                } else {
                    if (Math.abs(playerMotionX) < 0.005)
                        playerMotionX = 0;
                    if (Math.abs(playerMotionZ) < 0.005)
                        playerMotionZ = 0;
                }
                float f55 = 0;
                if (!gp.inWater && gp.player.isFlying()) {
                    if (!gp.inLava && gp.player.isFlying()) {
                        float f4 = 0.91F;

                        if (gp.playerGround) {
                            f4 = getFriction(getBlock(gp.getTo().clone().subtract(0, 1, 0)));
                        }

                        float f = 0.16277136F / (f4 * f4 * f4);
                        float f5;

                        if (gp.playerGround) {
                            f5 = walkspeed * f;
                            double expectedJumpMotion = 0.42F + (double)(gp.hasPotionEffect(PotionEffectType.JUMP) ? (gp.getEffectByType(PotionEffectType.JUMP).get().getAmplifier() + 1) * 0.1F : 0);

                            if(jumped) playerMotionY = expectedJumpMotion;
                            if (jumped && isSprinting) {
                                float rot = gp.to.clone().getYaw() * 0.017453292F;
                                playerMotionX -= Math.sin(rot) * 0.2F;
                                playerMotionZ += Math.cos(rot) * 0.2F;
                            }
                        } else {
                            f5 = isSprinting ? 0.026f : 0.02f;
                        }
                        f55 = f5;
                        f4 = 0.91F;

                        if (gp.playerGround) {
                            f4 = getFriction(getBlock(gp.getTo().clone().subtract(0, 1, 0)));
                        }

                        if (gp.inClimbableBlock) {
                            float f6 = 0.15F;
                            playerMotionX = clamp_double(gp.motionX, (double) (-f6), (double) f6);
                            playerMotionZ = clamp_double(gp.motionZ, (double) (-f6), (double) f6);
                            fallDistance = 0.0F;

                            if (playerMotionY < -0.15D) {
                                playerMotionY = -0.15D;
                            }

                            boolean flag = isSneaking;

                            if (flag && playerMotionY < 0.0D) {
                                playerMotionY = 0.0D;
                            }
                        }

                        //this.moveEntity(this.motionX, this.motionY, this.motionZ);

                        if (gp.onClimbable) {
                            playerMotionY = 0.2D;
                        }

                        if (!gp.getPlayer().getWorld().isChunkLoaded(gp.to.getChunk())) {
                            if (gp.to.clone().getY() > 0.0D) {
                                playerMotionY = -0.1D;
                            } else {
                                playerMotionY = 0.0D;
                            }
                        } else {
                            playerMotionY -= 0.08D;
                        }
                        if(gp.getExempt().isExempt(ExemptType.VELOCITY)) {
                            if(gp.validVelocityHit) {
                                float p_70653_3_ = (float) gp.velocity.x;
                                float p_70653_5_ = (float) gp.velocity.z;
                                float f2 = sqrt_double(p_70653_3_ * p_70653_3_ + p_70653_5_ * p_70653_5_);
                                float f1 = 0.4F;
                                playerMotionX /= 2.0D;
                                playerMotionY /= 2.0D;
                                playerMotionZ /= 2.0D;
                                playerMotionX -= p_70653_3_ / (double) f2 * (double) f1;
                                playerMotionY += (double) f1;
                                playerMotionZ -= p_70653_5_ / (double) f2 * (double) f1;

                                if (playerMotionY > 0.4000000059604645D) {
                                    playerMotionY = 0.4000000059604645D;
                                }
                            }
                        }
                        playerMotionY *= 0.98F;
                        playerMotionX *= (double) f4;
                        playerMotionZ *= (double) f4;
                    } else {
                        double d1 = gp.to.clone().getY();
                        this.moveFlying(strafe, forward, 0.02F);
                        playerMotionX += predictedMotionX;
                        playerMotionZ += predictedMotionZ;
                        //this.moveEntity(this.motionX, this.motionY, this.motionZ);
                        playerMotionX *= 0.5D;
                        playerMotionY *= 0.5D;
                        playerMotionZ *= 0.5D;
                        playerMotionY -= 0.02D;

                        if (gp.collidesHorizontally && new BoundingBox(gp.getPlayer()).offset(playerMotionX, playerMotionY + 0.6000000238418579D - playerMotionY + d1, playerMotionZ).getBlocks(gp.getPlayer().getWorld()).stream().anyMatch(Block::isLiquid)) {
                            playerMotionY = 0.30000001192092896D;
                        }
                    }
                } else {
                    double d0 = gp.to.clone().getY();
                    float f1 = 0.8F;
                    float f2 = 0.02F;
                    float f3 = (float) gp.getDepthStriderLevel();

                    if (f3 > 3.0F) {
                        f3 = 3.0F;
                    }

                    if (!gp.playerGround) {
                        f3 *= 0.5F;
                    }

                    if (f3 > 0.0F) {
                        f1 += (0.54600006F - f1) * f3 / 3.0F;
                        f2 += (walkspeed * 1.0F - f2) * f3 / 3.0F;
                    }

                    this.moveFlying(strafe, forward, f2);
                    playerMotionX += predictedMotionX;
                    playerMotionZ += predictedMotionZ;
                    //this.moveEntity(this.motionX, this.motionY, this.motionZ);
                    playerMotionX *= (double) f1;
                    playerMotionY *= 0.800000011920929D;
                    playerMotionZ *= (double) f1;
                    playerMotionY -= 0.02D;

                    if (gp.collidesHorizontally && new BoundingBox(gp.getPlayer()).offset(playerMotionX, playerMotionY + 0.6000000238418579D - playerMotionY + d0, playerMotionZ).getBlocks(gp.getPlayer().getWorld()).stream().anyMatch(Block::isLiquid)) {
                        playerMotionY = 0.30000001192092896D;
                    }
                }
                if(gp.inWeb) {
                    playerMotionX *= 0.25D;
                    playerMotionY *= 0.05000000074505806D;
                    playerMotionZ *= 0.25D;
                }

                double keyMotion = forwardMotion * forwardMotion + strafeMotion * strafeMotion;

               /* if (keyMotion >= 1.0E-4F) {
                    keyMotion = f55 / Math.max(1.0, Math.sqrt(keyMotion));
                    forwardMotion *= keyMotion;
                    strafeMotion *= keyMotion;

                    final float yaws = (float) Math.sin(gp.to.clone().getYaw() * (float) Math.PI / 180.F),
                            yawc = (float) Math.cos(gp.to.clone().getYaw() * (float) Math.PI / 180.F);

                    playerMotionX += ((strafeMotion * yawc) - (forwardMotion * yaws));
                    playerMotionZ += ((forwardMotion * yawc) + (strafeMotion * yaws));
                } */
                double delta = Math.pow(gp.motionX - playerMotionX, 2)
                        + Math.pow(gp.motionZ - playerMotionZ, 2);
                double yDelta = Math.pow(gp.motionY - playerMotionY, 2);

                if (delta < smallestDelta) {
                    smallestDelta = delta;
                }
                if (yDelta < smallestYDelta) {
                    smallestYDelta = yDelta;
                }
                smallestDelta = Math.min(delta, smallestDelta);
                smallestYDelta = Math.min(yDelta, smallestYDelta);
            }
        }
        double playermotion = Math.hypot(playerMotionX, playerMotionZ);
        //gp.pMotionX = playerMotionX;
        //gp.pMotionZ = playerMotionZ;

        return new double[] {playermotion, smallestDelta, smallestYDelta};
    }

    public static double clamp_double(double num, double min, double max)
    {
        return num < min ? min : (num > max ? max : num);
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

    public void moveFlying(float strafe, float forward, float friction)
    {
        float f = strafe * strafe + forward * forward;

        if (f >= 1.0E-4F)
        {
            f = (float)Math.sqrt(f);;

            if (f < 1.0F)
            {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;
            float f1 = (float) Math.sin(gp.to.getYaw() * (float)Math.PI / 180.0F);
            float f2 = (float) Math.cos(gp.to.getYaw() * (float)Math.PI / 180.0F);
            predictedMotionX += (double)(strafe * f2 - forward * f1);
            predictedMotionZ += (double)(forward * f2 + strafe * f1);
        }
    }
    public static float sqrt_double(double value)
    {
        return (float)Math.sqrt(value);
    }
}
