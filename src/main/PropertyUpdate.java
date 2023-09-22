package main;

public final class PropertyUpdate extends ChangeType {
  private final String previous;
  private final String current;

  public PropertyUpdate(String property, String previous, String current) {
    super(property);
    this.previous = previous;
    this.current = current;
  }

  public String getPrevious() {
    return previous;
  }

  public String getCurrent() {
    return current;
  }
}
