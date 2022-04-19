package guard.api.check;

import guard.Guard;
import lombok.Getter;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

@Getter
public class CheckManager {

    public List<GuardCheck> checks = new ArrayList<>();
    public List<GuardCheck> removingchecks = new ArrayList<>();

    public void addCheck(GuardCheck check) {
        GuardCheckInfo info = check.getClass().getAnnotation(GuardCheckInfo.class);
        check.name = info.name();
        check.category = info.category();
        check.enabled = true;// config
        check.kickable = info.kickable(); // config
        check.bannable = info.banable();// config
        check.maxBuffer = info.maxBuffer();// config
        check.addBuffer = info.addBuffer(); // config
        check.removeBuffer = info.removeBuffer(); // config
        if(!checks.isEmpty()) {
            try {
                if(checks.contains(check)) {
                    for (GuardCheck c : checks) {
                        if (c.name.equals(check.name)) {
                            checks.remove(c);
                            checks.add(check);
                        } else {
                            checks.add(check);
                        }
                    }
                } else {
                    checks.add(check);
                }
            }catch (ConcurrentModificationException e)  {

            }
        } else {
            checks.add(check);
        }
    }

    public void removeCheck(GuardCheck check) {
        GuardCheckInfo info = check.getClass().getAnnotation(GuardCheckInfo.class);
        check.name = info.name();
        check.category = info.category();
        check.enabled = true;// config
        check.kickable = info.kickable(); // config
        check.bannable = info.banable();// config
        check.maxBuffer = info.maxBuffer();// config
        check.addBuffer = info.addBuffer(); // config
        check.removeBuffer = info.removeBuffer(); // config
        if(!checks.isEmpty()) {
            checks.removeIf(c -> c.name.equals(check.name));
            removingchecks.add(check);
        }
    }


}
