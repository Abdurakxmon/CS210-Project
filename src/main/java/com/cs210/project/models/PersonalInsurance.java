package com.cs210.project.models;

import com.cs210.project.constants.Enums.NotificationType;
import java.time.LocalDateTime;

public class PersonalInsurance extends RentalInsurance {
    public PersonalInsurance(String id) {
        super(id);
    }

    @Override
    public boolean addInsurance() {
        System.out.println("Personal insurance added");
        return true;
    }
}
