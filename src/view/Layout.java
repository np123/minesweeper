package view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

public class Layout implements LayoutManager2 {

  private final int windowHeight;
  private final int windowWidth;

  public Layout(int width, int height) {
    windowWidth = width;
    windowHeight = height;
  }

  @Override
  public void addLayoutComponent(String name, Component comp) { }

  @Override
  public void addLayoutComponent(Component arg0, Object arg1) {
    if (arg1 == null) {
      throw new IllegalArgumentException();
    } else if (arg1 instanceof String) {
      addLayoutComponent((String) arg1, arg0);
    } else {
      throw new IllegalArgumentException("Invalid constraints specified: " + arg1);
    }
  }

  @Override
  public void layoutContainer(Container parent) {

  }

  @Override
  public Dimension minimumLayoutSize(Container parent) {
    return preferredLayoutSize(parent);
  }

  @Override
  public Dimension preferredLayoutSize(Container parent) {
    return new Dimension(windowWidth,windowHeight);
  }

  @Override
  public void removeLayoutComponent(Component comp) { }

  @Override
  public float getLayoutAlignmentX(Container arg0) {
    return 0.5f;
  }

  @Override
  public float getLayoutAlignmentY(Container arg0) {
    return 0.5f;
  }

  @Override
  public void invalidateLayout(Container arg0) { }

  @Override
  public Dimension maximumLayoutSize(Container parent) {
    return preferredLayoutSize(parent);
  }

}
