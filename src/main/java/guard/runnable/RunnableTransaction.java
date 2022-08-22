package guard.runnable;

import guard.check.GuardCheck;
import guard.data.GuardPlayer;
import guard.data.GuardPlayerManager;
import guard.utils.packet.TransactionPacketServer;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.out.ping.WrappedPacketOutPing;
import io.github.retrooper.packetevents.packetwrappers.play.out.transaction.WrappedPacketOutTransaction;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
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

        if(PacketEvents.get().getServerUtils().getVersion().isNewerThanOrEquals(ServerVersion.v_1_17)) {
            WrappedPacketOutPing trans = new WrappedPacketOutPing(id);
            gp.sentTransaction++;
            long now = System.currentTimeMillis();
            gp.transactions.add(new TransactionPacketServer(trans, now));
            PacketEvents.get().getPlayerUtils().sendPacket(p, trans);
            gp.sentTransactionTime = now;
            for (GuardCheck c : gp.getCheckManager().checks) {
                c.gp = gp;
                c.onTransactionSend(new TransactionPacketServer(trans, now));
            }
        } else {
            WrappedPacketOutTransaction trans = new WrappedPacketOutTransaction(0, id, false);
            gp.sentTransaction++;
            long now = System.currentTimeMillis();
            gp.transactions.add(new TransactionPacketServer(trans, now));
            PacketEvents.get().getPlayerUtils().sendPacket(p, trans);
            gp.sentTransactionTime = now;
            for (GuardCheck c : gp.getCheckManager().checks) {
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
