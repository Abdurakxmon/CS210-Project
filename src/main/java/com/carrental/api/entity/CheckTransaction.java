package com.carrental.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "check_transactions")
public class CheckTransaction extends Payment {

    private String bankName;
    private String checkNumber;
}
