package annotations;

import com.sun.org.apache.regexp.internal.RE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author csl
 * @date 2019/12/14 10:46
 * 注解，用于描述一个类：作者+类的作用
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Info {
    public String author() default "CSL";
    public String description() default "no description";
}
