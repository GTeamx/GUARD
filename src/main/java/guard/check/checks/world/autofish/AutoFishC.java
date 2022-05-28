package guard.check.checks.world.autofish;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@CheckInfo(name = "AutoFish C", category = Category.WORLD)
public class AutoFishC extends Check {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ITEM) {
            if (data.lastFishDiff < 10) fail("AutoFishing Pattern", "delay=" + data.lastFishDiff);
        }
    }
}
