package main;

import java.util.List;

public final class ListUpdate extends ChangeType {
  private final List<String> added;
  private final List<String> removed;

  public ListUpdate(String property, List<String> added, List<String> removed) {
    super(property);
    this.added = added;
    this.removed = removed;
  }

  public List<String> getAdded() {
    return added;
  }

  public List<String> getRemoved() {
    return removed;
  }
}
