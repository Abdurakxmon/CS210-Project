package com.cs210.project.domain.account;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class AccountStatusConverter implements AttributeConverter<AccountStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AccountStatus attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public AccountStatus convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : AccountStatus.fromCode(dbData);
    }
}