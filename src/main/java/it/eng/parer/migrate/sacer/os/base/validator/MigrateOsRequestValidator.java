/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

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
