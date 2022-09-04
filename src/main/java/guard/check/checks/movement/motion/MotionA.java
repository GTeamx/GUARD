package guard.check.checks.movement.motion;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Motion A", category = GuardCategory.Movement, state = GuardCheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0.25, maxBuffer = 0)
public class MotionA extends GuardCheck {

    // um. i forgor
}
