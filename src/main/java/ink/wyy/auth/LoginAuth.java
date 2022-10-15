package ink.wyy.auth;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginAuth {
    boolean value() default true;
}
