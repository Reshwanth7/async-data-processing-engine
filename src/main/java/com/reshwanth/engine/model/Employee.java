package com.reshwanth.engine.model;

import java.time.LocalDate;

public record Employee(
        int id,
        String name,
        String department,
        double salary,
        LocalDate joinDate
) {}