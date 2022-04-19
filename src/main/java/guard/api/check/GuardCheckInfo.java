package guard.api.check;

import guard.check.Category;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GuardCheckInfo {
    String name() default "Test A";
    Category category() default Category.MOVEMENT;
    int punishvl() default 3;
    boolean kickable() default false;
    boolean banable() default false;
    int maxBuffer() default 1;
    int addBuffer() default 1;
    int removeBuffer() default 1;
}