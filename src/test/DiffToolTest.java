package test;

import main.*;
import org.junit.jupiter.api.Test;
import test.dto.Address;
import test.dto.Balance;
import test.dto.Person;
import test.dto.SampleObject;

import java.util.List;

public class DiffToolTest {
    @Test
    public void test() {
        var prior =
                new Person(
                        "John",
                        132,
                        new Balance("1", "EUR", List.of()),
                        List.of("Teacher", "Software Developer", "Engineer"),
                        List.of(
                                new Address(
                                        "id",
                                        "Tutora",
                                        "6",
                                        "Iasi",
                                        "Romania",
                                        null,
                                        "Romania",
                                        List.of("A", "B", "C"))));
        var current =
                new Person(
                        "John3",
                        null,
                        new Balance("1", "USD", List.of(new SampleObject(1))),
                        List.of("Teacher", "Software Developer", "Software Principal"),
                        List.of(
                                new Address(
                                        "id",
                                        "Tutora",
                                        "6",
                                        "Brasov",
                                        "Romania",
                                        new Balance("432", "RON", List.of(new SampleObject(5))),
                                        "Romania",
                                        List.of("A", "B")),
                                new Address(
                                        "id2",
                                        "Tutora",
                                        "3",
                                        "Brasov",
                                        "Romania",
                                        new Balance("1", "EUR", List.of()),
                                        "Romania",
                                        List.of())));
        List<ChangeType> result = DiffTool.diff(prior, current);
        System.out.println(result.size());
    }
}
