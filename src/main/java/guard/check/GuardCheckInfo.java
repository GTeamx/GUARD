package guard.check;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GuardCheckInfo {
    String name() default "Test";
    GuardCategory category() default GuardCategory.Movement;
    GuardCheckState state() default GuardCheckState.Coding;
    boolean enabled() default true;
    boolean kickable() default true;
    boolean bannable() default true;
    boolean silent() default true;
    double maxBuffer() default 0;
    double addBuffer() default 0;
    double removeBuffer() default 0;
    int punishVL() default 3;
}
