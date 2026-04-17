package com.cs210.project.domain.account;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class AccountRoleConverter implements AttributeConverter<AccountRole, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AccountRole attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public AccountRole convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : AccountRole.fromCode(dbData);
    }
}