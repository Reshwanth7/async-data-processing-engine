package com.reshwanth.engine.service;

import com.reshwanth.engine.model.Employee;

import java.util.*;
import java.util.stream.Collectors;

public class AnalyticsService {

    public Optional<Employee> findHighestSalary(List<Employee> employees) {
        return employees.stream()
                .max(Comparator.comparingDouble(Employee::salary));
    }

    public Map<String, Long> countByDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::department,
                        Collectors.counting()
                ));
    }

    public Map<String, Double> averageSalaryByDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::department,
                        Collectors.averagingDouble(Employee::salary)
                ));
    }
}
