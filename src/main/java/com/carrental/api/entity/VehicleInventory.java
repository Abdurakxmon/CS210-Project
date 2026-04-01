package com.carrental.api.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VehicleInventory implements SearchCatalog {

    private Map<String, List<Vehicle>> vehicleTypeIndex;
    private Map<String, List<Vehicle>> vehicleModelIndex;
    private LocalDate creationDate;

    @Override
    public List<Vehicle> searchByType(String type) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Vehicle> searchByModel(String model) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
