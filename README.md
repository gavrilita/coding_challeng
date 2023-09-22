# Functional Description: Object Audit Tool

The Object Audit Tool is a Java-based utility designed to perform object structure audits, primarily focusing on tracking property changes within simple and nested objects and identifying changes within lists. The tool utilizes Java reflection as its underlying mechanism for reading object structures. The core functionality of the tool can be described as follows:

### Simple and Nested Object Property Changes: The tool provides the ability to check for changes in properties of simple and nested objects. A simple object, in this context, refers to an object that does not contain nested objects or lists. The tool analyzes two instances of a simple object to identify and report any property changes between them. This functionality is valuable for monitoring changes in individual objects within your application.

### List Changes: The tool includes a feature for tracking changes within lists. Lists can contain elements with primitive types or complex objects. For list auditing, the following considerations are made:

### Element Addition: An element within the list is considered newly added if it has an "id" field with a value that wasn't previously present in the list. The "id" field is used as a unique identifier to determine whether an element is new.

### Element Removal: If an "id" found in the current list is not present in the updated list, it is considered as a removed element. The tool identifies and reports removed elements.

### Primitive Types in Lists: The tool supports auditing lists where elements can have primitive types (e.g., int, String) as their values.

### Complex Objects in Lists: The tool can handle lists that contain complex objects. When auditing such lists, it identifies changes within these complex objects and is doing a recursive analysis for the contained objects.

## Implementation and Testing:

The tool was implemented over a period of approximately 2 hours, focusing on the core functionality of auditing simple objects and lists. Following the initial implementation, an additional 2 or 3 hours were dedicated to refining and testing the tool under various scenarios to ensure its robustness and accuracy.

## Possible improvements:

I thought about 2 possible immediate improvements:
### allowance of support for primitive values: for example diff("A", "B");
### allowance of support for list of values: for example diff(List.of("A"), List.of("B"));
### allowance of support for list with complex ids: for example diff(List.of(new Nested(new NestedKey("A", "1"), new NestedProperty("B"))), List.of());
