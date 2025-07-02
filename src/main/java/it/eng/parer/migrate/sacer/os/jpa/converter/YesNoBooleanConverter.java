/**
 *
 */
package it.eng.parer.migrate.sacer.os.jpa.converter;

import java.util.Objects;

import jakarta.persistence.AttributeConverter;

public class YesNoBooleanConverter implements AttributeConverter<Boolean, String> {
    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
	return Objects.nonNull(attribute) && attribute.booleanValue() ? "Y" : "N";
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
	return dbData.equalsIgnoreCase("Y");
    }
}