package guard.check;

import guard.Guard;
import guard.data.PlayerData;
import guard.exempt.Exempt;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class Check {

    public String name;
    public boolean enabled;
    public Category category;
    private boolean sent = false;
    private long lastFlying;
    private long lastotherPacket;
    public double buffer = 0;
    public double maxBuffer;
    private double bufferpost;
    public boolean bannable;
    public boolean kickable;
    public boolean silent;
    public double addBuffer;
    public double removeBuffer;
    public int punishVL;
    public PlayerData data;
    public boolean isdebugging;
    public List<Player> debugtoplayers = new ArrayList<>();

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(data == null) {
            return;
        }
    }

    public void onPacketSend(PacketPlaySendEvent packet) {
        if(data == null) {
            return;
        }
    }

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        if(data == null) {
            return;
        }
    }

    public void fail(Object a, Object b) {
        if(data != null) {
            if(buffer < maxBuffer + 10 && addBuffer != 0) {
                buffer += addBuffer;
            }
            if(buffer > maxBuffer || addBuffer == 0) {
                //sendMessage("fail a=" + addBuffer + " b=" + buffer + " r=" + removeBuffer + " silent=" + silent + " kick=" + kickable + " ban=" + bannable);
                data.flag(this, Guard.instance.configUtils.getIntFromConfig("checks", name + ".Punishments.punishVL") - 1, a, b, buffer, maxBuffer);

            }
        }
    }

    public boolean mathOnGround(final double posY) {
        return posY % 0.015625 == 0;
    }

    public void debug(String message) {
        if(isdebugging) {
            if(debugtoplayers.size() == 0) {
                sendMessage(message);
            } else {
                for(Player p : debugtoplayers) {
                    //if(p.getName().equals(p.getName())) {
                        p.sendMessage(message);
                    //}
                }
            }
        }
    }

    public boolean isExempt(final ExemptType exemptType) {
        return data.getExempt().isExempt(exemptType);
    }

    public boolean isExempt(ExemptType... type) {
        return data.getExempt().isExempt(type);
    }

    public void removeBuffer() {
        if(removeBuffer <= 0) {
            buffer = 0;
        } else {
            if (buffer >= removeBuffer) {
                buffer -= removeBuffer;
            } else {
                buffer = 0;
            }
        }
        //sendMessage("remove a=" + addBuffer + " b=" + buffer + " r=" + removeBuffer + " silent=" + silent + " kick=" + kickable + " ban=" + bannable);
    }

    public void sendMessage(String Message) {
        if(data != null) {
            data.sendMessage(Message);
        }
    }


    public boolean isPost(byte type1, byte type2) {
        if (type1 == PacketType.Play.Client.POSITION || type1 == PacketType.Play.Client.POSITION_LOOK ||type1 == PacketType.Play.Client.LOOK) {
            final long now = System.currentTimeMillis();
            final long delay = now - lastotherPacket;

            if (sent) {
                if (delay > 40L && delay < 100L) {
                    bufferpost += 0.25;

                    if (bufferpost > 0.5) {
                        return true;
                    }
                } else {
                    bufferpost = Math.max(bufferpost - 0.025, 0);
                }

                sent = false;
            }

            this.lastFlying = now;
        } else if (type1 == type2) {
            final long now = System.currentTimeMillis();
            final long delay = now - lastFlying;

            if (delay < 10L) {
                lastotherPacket = now;
                sent = true;
            } else {
                bufferpost = Math.max(bufferpost - 0.025, 0.0);
            }
        }

        return false;
    }
}
