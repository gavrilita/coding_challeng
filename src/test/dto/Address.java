package test.dto;

import main.AuditKey;

import java.util.List;

public record Address(
    @AuditKey String auditKey,
    String streetName,
    String streetNumber,
    String city,
    String state,
    Balance balance,
    String country,
    List<String> attributes) {}
