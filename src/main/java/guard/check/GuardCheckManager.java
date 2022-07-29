package guard.check;

import guard.Guard;
import guard.check.checks.combat.aim.*;
import guard.check.checks.combat.aim.aimassist.AimAssistA;
import guard.check.checks.combat.aim.aimassist.AimAssistB;
import guard.check.checks.combat.aim.aimassist.AimAssistC;
import guard.check.checks.combat.killaura.KillAuraA;
import guard.check.checks.combat.killaura.KillAuraB;
import guard.check.checks.combat.killaura.KillAuraC;
import guard.check.checks.movement.fastclimb.FastClimbA;
import guard.check.checks.movement.fly.*;
import guard.check.checks.movement.ground.GroundA;
import guard.check.checks.movement.jump.JumpA;
import guard.check.checks.movement.speed.*;
import guard.check.checks.movement.step.StepA;
import guard.check.checks.movement.step.StepB;
import guard.check.checks.movement.step.StepC;
import guard.check.checks.player.badpackets.*;
import guard.check.checks.player.badpackets.post.*;
import guard.check.checks.player.inventory.InventoryA;
import guard.check.checks.player.timer.TimerA;
import guard.check.checks.world.baritone.BaritoneA;

import java.util.ArrayList;
import java.util.List;

public class GuardCheckManager {

    public List<GuardCheck> checks = new ArrayList<>();

    public GuardCheckManager() {
        registerCheck(new FlyA());
        registerCheck(new FlyB());
        registerCheck(new FlyC());
        registerCheck(new FlyD());
        registerCheck(new FlyE());
        registerCheck(new FlyF());

        registerCheck(new JumpA());

        registerCheck(new GroundA());

        registerCheck(new FastClimbA());

        registerCheck(new SpeedA());
        registerCheck(new SpeedB());
        registerCheck(new SpeedC());
        registerCheck(new SpeedD());
        registerCheck(new SpeedE());

        registerCheck(new StepA());
        registerCheck(new StepB());
        registerCheck(new StepC());

        registerCheck(new TimerA());
        //registerCheck(new TimerB());

        registerCheck(new BadPacketA());
        registerCheck(new BadPacketB());
        registerCheck(new BadPacketC());
        registerCheck(new BadPacketD());
        registerCheck(new BadPacketE());
        registerCheck(new BadPacketF());
        registerCheck(new BadPacketG());
        registerCheck(new BadPacketH());
        registerCheck(new BadPacketI());
        registerCheck(new BadPacketJ());
        registerCheck(new BadPacketK());
        registerCheck(new BadPacketL());
        registerCheck(new BadPacketM());
        registerCheck(new BadPacketN());
        registerCheck(new BadPacketO());
        registerCheck(new BadPacketP());

        registerCheck(new KillAuraA());
        registerCheck(new KillAuraB());
        registerCheck(new KillAuraC());
        registerCheck(new AimA());
        registerCheck(new AimB());
        registerCheck(new AimC());
        registerCheck(new AimD());
        registerCheck(new AimE());
        registerCheck(new AimF());
        registerCheck(new AimG());
        registerCheck(new AimAssistA());
        registerCheck(new AimAssistB());
        registerCheck(new AimAssistC());

        registerCheck(new BaritoneA());

        registerCheck(new InventoryA());
    }

    public void registerCheck(GuardCheck check) {
        GuardCheckInfo info = check.getClass().getAnnotation(GuardCheckInfo.class);
        check.name = info.name();
        check.category = info.category();
        check.state = info.state();
        check.enabled = Guard.instance.configUtils.getBooleanFromConfig("checks", info.name() + ".enabled", info.enabled());// config
        check.kickable = Guard.instance.configUtils.getBooleanFromConfig("checks", info.name() + ".Punishments.kick",info.kickable()); // config
        check.bannable = Guard.instance.configUtils.getBooleanFromConfig("checks", info.name() + ".Punishments.ban", info.bannable());// config
        if(Guard.instance.configUtils.getBooleanFromConfig("config", "silentChecks", info.silent())) {// config decides
            check.silent = Guard.instance.configUtils.getBooleanFromConfig("checks", info.name() + ".silent", info.silent());
        } else {
            check.silent = false;
        }
        check.maxBuffer = Guard.instance.configUtils.getDoubleFromConfig("checks", info.name() + ".Buffer.maxBuffer", info.maxBuffer());// config
        check.addBuffer = Guard.instance.configUtils.getDoubleFromConfig("checks", info.name() + ".Buffer.addBuffer", info.addBuffer()); // config
        check.removeBuffer = Guard.instance.configUtils.getDoubleFromConfig("checks", info.name() + ".Buffer.removeBuffer", info.removeBuffer()); // config
        if(!checks.contains(check))
            checks.add(check);
    }
}
