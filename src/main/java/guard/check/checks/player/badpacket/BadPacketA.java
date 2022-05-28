package guard.check.checks.player.badpacket;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "BadPacket A", category = Category.PLAYER)
public class BadPacketA extends Check {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (data.eatDelay < 1300) {
            fail("Ate too fast", "delay=" + data.eatDelay);
            data.eatDelay = 1300;
        }
        if(data.lastShootDelay < 99) {
            fail("Shot too fast", "delay=" + data.lastShootDelay);
            data.lastShootDelay = 99;
        }
        if(data.shootDelay < 299) {
            fail("Shot too fast", "delay=" + data.shootDelay);
            data.shootDelay = 299;
        }
    }
}
