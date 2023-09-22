package test.dto;

import java.util.List;

public record Person(
    String name, Integer age, Balance balance, List<String> jobs, List<Address> addresses) {}
