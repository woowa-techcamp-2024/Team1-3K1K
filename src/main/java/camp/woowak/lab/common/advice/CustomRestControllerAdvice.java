package camp.woowak.lab.common.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public @interface CustomRestControllerAdvice {
	@AliasFor(annotation = RestControllerAdvice.class, attribute = "basePackages")
	String[] basePackages() default {};

	@AliasFor(annotation = RestControllerAdvice.class, attribute = "basePackageClasses")
	Class<?>[] basePackageClasses() default {};

	@AliasFor(annotation = RestControllerAdvice.class, attribute = "assignableTypes")
	Class<?>[] assignableTypes() default {};

	@AliasFor(annotation = RestControllerAdvice.class, attribute = "annotations")
	Class<? extends Annotation>[] annotations() default {};
}
