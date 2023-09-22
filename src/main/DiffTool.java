package main;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class DiffTool {

  public static <T> List<ChangeType> diff(T obj1, T obj2) {
    return diff(obj1, obj2, "").stream()
        .sorted(Comparator.comparing(a -> a.property))
        .collect(Collectors.toList());
  }

  private static <T> List<ChangeType> diff(T obj1, T obj2, String prefix) throws SecurityException {
    if (obj1 == null && obj2 == null) {
      return List.of();
    }
    List<ChangeType> changes = new ArrayList<>();
    Class<?> clazz = obj1 != null ? obj1.getClass() : obj2.getClass();
    Field[] fields = clazz.getDeclaredFields();

    for (Field field : fields) {
      try {
        if (field.trySetAccessible()) {
          field.setAccessible(true);

          Object priorValue = obj1 != null ? field.get(obj1) : null;
          Object currentValue = obj2 != null ? field.get(obj2) : null;

          if (!Objects.equals(currentValue, priorValue) && isPrimitive(field)) {
            List<ChangeType> propertyChanges = handlePropertyChange(prefix, field, priorValue, currentValue);
            changes.addAll(propertyChanges);
          }
          List<ChangeType> listChanges = handleListChanges(prefix, field, priorValue, currentValue);
          changes.addAll(listChanges);

          // Recursively check nested objects
          if (currentValue != null && priorValue != null && !(isPrimitive(field))) {
            List<ChangeType> nestedDifferences = handleNestedChanges(prefix, field, priorValue, currentValue);
            changes.addAll(nestedDifferences);
          }
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return changes;
  }

  private static List<ChangeType> handleNestedChanges(String prefix, Field field, Object priorValue, Object currentValue) {
    String newPrefix =
        prefix.isBlank()
            ? field.getName()
            : prefix + "." + field.getName();
    return diff(currentValue, priorValue, newPrefix);
  }

  private static <T> List<ChangeType> handlePropertyChange(String prefix, Field field, T priorValue, T currentValue) {
    List<ChangeType> changes = new ArrayList<>();
    String propertyName = prefix + (prefix.isBlank() ? "" : ".") + field.getName();
    String prior = priorValue != null ? priorValue.toString() : "null";
    String current = currentValue != null ? currentValue.toString() : "null";
    changes.add(new PropertyUpdate(propertyName, prior, current));
    return changes;
  }

  private static <T> List<ChangeType> handleListChanges(String prefix, Field field, T priorValue, T currentValue) {
    List<ChangeType> changes = new ArrayList<>();
    if (field.getType().equals(List.class)) {
      List<String> added = differences(priorValue, currentValue);
      List<String> removed = differences(currentValue, priorValue);
      List<String> common = common(currentValue, priorValue);
      if (!(added.isEmpty() && removed.isEmpty())) {
        changes.add(
            new ListUpdate(
                prefix.isBlank() ? field.getName() : prefix + "." + field.getName(),
                added,
                removed));
      }
      common.forEach(
          elem -> {
            List<ChangeType> nestedDifferences =
                getChangeTypes(prefix, field, (List) priorValue, (List) currentValue, elem);

            changes.addAll(nestedDifferences);
          });
      added.forEach(
          elem -> {
            List<ChangeType> nestedDifferences =
                getChangeTypes(prefix, field, null, (List) currentValue, elem);
            changes.addAll(nestedDifferences);
          });
      removed.forEach(
          elem -> {
            List<ChangeType> nestedDifferences =
                getChangeTypes(prefix, field, (List) priorValue, null, elem);
            changes.addAll(nestedDifferences);
          });
    }
    return changes;
  }

  private static <T> List<ChangeType> getChangeTypes(
      String prefix, Field field, List<T> prevValue, List<T> currentValue, String elem) {
    String newPrefix;
    try {
      newPrefix =
          prefix.isBlank()
              ? field.getName() + computeKeyFieldPrefix(elem)
              : prefix + "." + field.getName() + computeKeyFieldPrefix(elem);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    return diff(
        prevValue != null ? findElementByKeyValue(prevValue, elem) : null,
        currentValue != null ? findElementByKeyValue(currentValue, elem) : null,
        newPrefix);
  }

  private static boolean isPrimitive(Field field) {
    return field.getType().isPrimitive()
        || field.getType().equals(Integer.class)
        || field.getType().equals(Character.class)
        || field.getType().equals(Short.class)
        || field.getType().equals(Long.class)
        || field.getType().equals(Void.class)
        || field.getType().equals(Float.class)
        || field.getType().equals(Double.class)
        || field.getType().equals(Boolean.class)
        || field.getType().equals(Byte.class)
        || field.getType().equals(String.class);
  }

  private static <T> List<String> differences(T priorListValues, T currentListValues) {
    List<String> priorIds =
        extractNonPrimitiveTypeIds(priorListValues != null ? (List) priorListValues : List.of());
    List<String> currentIds =
        extractNonPrimitiveTypeIds(
            currentListValues != null ? (List) currentListValues : List.of());
    return currentIds.stream()
        .filter(element -> !priorIds.contains(element))
        .collect(Collectors.toList());
  }

  private static <T> List<String> common(T priorListValues, T currentListValues) {
    List<String> priorIds =
        extractNonPrimitiveTypeIds(priorListValues != null ? (List) priorListValues : List.of());
    List<String> currentIds =
        extractNonPrimitiveTypeIds(
            currentListValues != null ? (List) currentListValues : List.of());
    return currentIds.stream().filter(priorIds::contains).collect(Collectors.toList());
  }

  private static <T> List<String> extractNonPrimitiveTypeIds(List<T> elements) {
    return elements.stream()
        .map(
            elem -> {
              if (elem.getClass().isPrimitive() || elem.getClass().equals(String.class)) {
                return elem.toString();
              } else {
                try {
                  Optional<Field> keyField = getKeyField(elem);
                  Field field = keyField.get();
                  field.setAccessible(true);
                  return field.get(elem).toString();
                } catch (NoSuchFieldException | IllegalAccessException e) {
                  throw new RuntimeException(e);
                }
              }
            })
        .toList();
  }

  private static <T> T findElementByKeyValue(List<T> elements, String value) {
    return elements.stream()
        .filter(
            elem -> {
              if (elem.getClass().isPrimitive() || elem.getClass().equals(String.class)) {
                return true;
              } else {
                try {
                  Optional<Field> keyField = getKeyField(elem);
                  Field field = keyField.get();
                  field.setAccessible(true);
                  return value.equals(field.get(elem).toString());
                } catch (NoSuchFieldException | IllegalAccessException e) {
                  throw new RuntimeException(e);
                }
              }
            })
        .findAny()
        .get();
  }

  private static <T> String computeKeyFieldPrefix(T elem) throws NoSuchFieldException {
    return "[" + elem + "]";
  }

  private static <T> Optional<Field> getKeyField(T object) throws NoSuchFieldException {
    List<Field> auditKeys =
        Arrays.stream(object.getClass().getDeclaredFields())
            .filter(fld -> fld.isAnnotationPresent(AuditKey.class))
            .toList();
    if (auditKeys.isEmpty()
        && Arrays.stream(object.getClass().getDeclaredFields())
            .noneMatch(elem -> "id".equals(elem.getName()))) {
      throw new RuntimeException(
          "the audit system lacks the information it needs to determine what has changed.");
    }
    Field field =
        auditKeys.size() != 1 ? object.getClass().getDeclaredField("id") : auditKeys.get(0);
    return Optional.of(field);
  }
}
