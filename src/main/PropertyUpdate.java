package main;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PropertyUpdate that = (PropertyUpdate) o;

    if (!Objects.equals(previous, that.previous)) return false;
    if (!Objects.equals(property, that.property)) return false;
    return Objects.equals(current, that.current);
  }

  @Override
  public int hashCode() {
    int result = previous != null ? previous.hashCode() : 0;
    result = 31 * result + (current != null ? current.hashCode() : 0);
    return result;
  }
}
