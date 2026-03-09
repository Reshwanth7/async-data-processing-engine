package com.reshwanth.engine.util;

import com.reshwanth.engine.model.Employee;
import java.time.LocalDate;
import java.util.List;

public class DataLoader {

    public static List<Employee> loadEmployees() {
        return List.of(
                new Employee(1, "Alice", "IT", 90000, LocalDate.of(2020, 1, 10)),
                new Employee(2, "Bob", "HR", 75000, LocalDate.of(2019, 3, 15)),
                new Employee(3, "Charlie", "Finance", 120000, LocalDate.of(2018, 7, 20))
        );
    }
}
