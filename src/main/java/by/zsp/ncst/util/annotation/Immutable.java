package by.zsp.ncst.util.annotation;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.LOCAL_VARIABLE, ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE_USE, ElementType.TYPE})
public @interface Immutable {
}
