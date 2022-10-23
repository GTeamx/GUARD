package guard.check;

import guard.Guard;

import guard.check.checks.movement.step.StepA;
import guard.check.checks.movement.step.StepB;
import guard.check.checks.movement.vclip.vClipA;

import java.util.ArrayList;
import java.util.List;

public class GuardCheckManager {

    public List<GuardCheck> checks = new ArrayList<>();

    public GuardCheckManager() {

        registerCheck(new vClipA());

        registerCheck(new StepA());
        registerCheck(new StepB());

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
        check.addBuffer = Guard.instance.configUtils.getDoubleFromConfig("checks", info.name() + ".Buffer.addBuffer", info.addBuffer());; // config
        check.removeBuffer = Guard.instance.configUtils.getDoubleFromConfig("checks", info.name() + ".Buffer.removeBuffer", info.removeBuffer());; // config
        if(!checks.contains(check))
            checks.add(check);
    }
}
