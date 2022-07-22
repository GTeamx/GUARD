package guard.check.checks.world.autofish;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "AutoFish B", category = Category.WORLD)
public class AutoFishB extends Check {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(!data.armAnimation) fail("No Swing", "NaN");
    }
}
