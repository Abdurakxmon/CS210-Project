package com.cs210.project.domain.account;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AccountStatusConverter implements AttributeConverter<AccountStatus, String> {

    @Override
    public String convertToDatabaseColumn(AccountStatus attribute) {
        return attribute == null ? null : attribute.getDatabaseValue();
    }

    @Override
    public AccountStatus convertToEntityAttribute(String dbData) {
        return AccountStatus.fromDatabaseValue(dbData);
    }
}
