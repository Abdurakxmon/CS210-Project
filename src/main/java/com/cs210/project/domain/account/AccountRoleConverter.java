package com.cs210.project.domain.account;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AccountRoleConverter implements AttributeConverter<AccountRole, String> {

    @Override
    public String convertToDatabaseColumn(AccountRole attribute) {
        return attribute == null ? null : attribute.getDatabaseValue();
    }

    @Override
    public AccountRole convertToEntityAttribute(String dbData) {
        return AccountRole.fromDatabaseValue(dbData);
    }
}
