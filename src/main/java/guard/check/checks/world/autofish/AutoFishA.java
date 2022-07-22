package guard.check.checks.world.autofish;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@CheckInfo(name = "AutoFish A", category = Category.WORLD)
public class AutoFishA extends Check {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ITEM) {
            data.sendMessage("Used!");
            if(data.lastFishDiff - (System.currentTimeMillis() - data.lastFish) < 60) fail("AutoFishing", "delay=" + (data.lastFishDiff - (System.currentTimeMillis() - data.lastFish)));
            if(data.lastFishDiff - (System.currentTimeMillis() - data.lastFish) >= 60) buffer = 0;
        }
    }

}