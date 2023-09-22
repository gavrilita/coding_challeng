package test.dto;

import java.util.List;

public record Balance(String amount, String unit, List<SampleObject> objectList) {}
