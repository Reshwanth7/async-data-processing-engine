package com.reshwanth.engine.service;

import com.reshwanth.engine.model.Employee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalyticsServiceTest {

    @Test
    void testFindHighestSalary() {
        AnalyticsService service = new AnalyticsService();

        List<Employee> employees = List.of(
                new Employee(1, "A", "IT", 50000, LocalDate.now()),
                new Employee(2, "B", "IT", 70000, LocalDate.now())
        );

        var result = service.findHighestSalary(employees);

        assertTrue(result.isPresent());
        assertEquals(70000, result.get().salary());
    }
}
