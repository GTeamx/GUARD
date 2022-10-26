package guard.runnable;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import guard.check.Check;
import guard.data.GuardPlayer;
import guard.data.GuardPlayerManager;
import guard.utils.packet.TransactionPacketServer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class RunnableTransaction extends BukkitRunnable {
    Player p;

    public RunnableTransaction(Player p) {
        this.p = p;
    }

    @Override
    public void run() {
        Random random = new Random();
        if (GuardPlayerManager.doesGuardPlayerExist(p)) {
            GuardPlayer gp = GuardPlayerManager.getGuardPlayer(p);
            gp.transactionTick++;
        short id = (short) (gp.transactionTick > Short.MAX_VALUE ? gp.transactionTick % Short.MAX_VALUE : gp.transactionTick);

        if(PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_17)) {
            WrapperPlayServerPing trans = new WrapperPlayServerPing(id);
            gp.sentTransaction++;
            long now = System.currentTimeMillis();
            gp.transactions.add(new TransactionPacketServer(trans, now));
            PacketEvents.getAPI().getPlayerManager().sendPacket(p, trans);
            gp.sentTransactionTime = now;
            for (Check c : gp.getCheckManager().checks) {
                c.gp = gp;
                c.onTransactionSend(new TransactionPacketServer(trans, now));
            }
        } else {
            WrapperPlayServerWindowConfirmation trans = new WrapperPlayServerWindowConfirmation(0, id, false);
            gp.sentTransaction++;
            long now = System.currentTimeMillis();
            gp.transactions.add(new TransactionPacketServer(trans, now));
            PacketEvents.getAPI().getPlayerManager().sendPacket(p, trans);
            gp.sentTransactionTime = now;
            for (Check c : gp.getCheckManager().checks) {
                c.gp = gp;
                c.onTransactionSend(new TransactionPacketServer(trans, now));
            }
        }
        if(gp.packetTracker != null) {
            gp.packetTracker.setIntervalPackets(0);
        }
    }
    }
}
