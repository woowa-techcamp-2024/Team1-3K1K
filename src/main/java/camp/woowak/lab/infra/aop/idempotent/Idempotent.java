package camp.woowak.lab.infra.aop.idempotent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
	boolean throwError() default false;

	Class<? extends RuntimeException> throwable() default RuntimeException.class;

	String exceptionMessage() default "";
}
