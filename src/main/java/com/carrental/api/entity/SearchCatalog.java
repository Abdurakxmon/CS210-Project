package com.carrental.api.entity;

import java.util.List;

public interface SearchCatalog {

    List<Vehicle> searchByType(String type);

    List<Vehicle> searchByModel(String model);
}
