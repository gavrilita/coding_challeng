package main;

public abstract sealed class ChangeType permits ListUpdate, PropertyUpdate {
  protected final String property;

  protected ChangeType(String property) {
    this.property = property;
  }

  public String getProperty() {
    return property;
  }
}
