package it.eng.parer.migrate.sacer.os.jpa.sacer.sequence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.annotations.IdGeneratorType;

@IdGeneratorType(NonMonotonicSequenceGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.FIELD, ElementType.METHOD })
public @interface NonMonotonicSequence {

    String sequenceName();

    int startWith() default 1;

    int incrementBy() default 1;

}