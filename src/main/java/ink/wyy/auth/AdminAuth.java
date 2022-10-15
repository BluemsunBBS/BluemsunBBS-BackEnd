package ink.wyy.auth;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@LoginAuth
public @interface AdminAuth {
    boolean value() default true;
}
