/**
 *
 */
package it.eng.parer.migrate.sacer.os.base.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;

import io.quarkus.runtime.LaunchMode;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import jakarta.inject.Inject;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

public class MigrateOsRequestValidator implements
	ConstraintValidator<it.eng.parer.migrate.sacer.os.base.validator.MigrateOsRequestValidator.ValidOsMigrationReq, MigrateRequest> {

    @Inject
    LaunchMode mode;

    @Override
    public boolean isValid(MigrateRequest value, ConstraintValidatorContext context) {
	return ((!Objects.isNull(value.dtapertura) || !Objects.isNull(value.dtaperturayy))
		&& !Objects.isNull(value.idstrut) || !Objects.isNull(value.idstrut))
		|| mode.isDevOrTest() && (!Objects.isNull(value.iddoc)
			|| !Objects.isNull(value.idsessionvers) || !Objects.isNull(value.idunitadoc)
			|| !Objects.isNull(value.idcomp) || !Objects.isNull(value.idverindiceaip)
			|| !Objects.isNull(value.idverserie))
		|| !Objects.isNull(value.idelencovers);
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = {
	    MigrateOsRequestValidator.class })
    public @interface ValidOsMigrationReq {
	String message() default "Invalid filter";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
    }
}
