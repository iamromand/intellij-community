// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.ui.tabs;

import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBValue;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

import static javax.swing.SwingConstants.*;

/**
 * @author pegov
 */
public final class TabsUtil {
  public static final JBValue TAB_VERTICAL_PADDING = new JBValue.Float(2);
  public static final int NEW_TAB_VERTICAL_PADDING = JBUIScale.scale(2);
  private static final @NonNls String FAKE_LABEL_TEXT = "XXX";

  private TabsUtil() {
  }

  public static int getTabsHeight() {
    return getTabsHeight(NEW_TAB_VERTICAL_PADDING);
  }

  public static int getTabsHeight(int verticalPadding) {
    JLabel xxx = new JLabel(FAKE_LABEL_TEXT);
    xxx.setFont(getLabelFont());
    return xxx.getPreferredSize().height + (verticalPadding * 2);
  }

  public static Font getLabelFont() {
    UISettings uiSettings = UISettings.getInstance();
    Font font = JBUI.CurrentTheme.ToolWindow.headerFont();
    if (uiSettings.getOverrideLafFonts()) {
      return font.deriveFont((float)uiSettings.getFontSize() + JBUI.CurrentTheme.ToolWindow.overrideHeaderFontSizeOffset());
    }

    return font;
  }

  @MagicConstant(intValues = {CENTER, TOP, LEFT, BOTTOM, RIGHT, -1})
  public static int getDropSideFor(Point point, JComponent component) {
    double r = Math.max(.05, Math.min(.45, Registry.doubleValue("ide.tabbedPane.dragToSplitRatio")));

    int placement = UISettings.getInstance().getState().getEditorTabPlacement();
    Dimension size = component.getSize();
    double width = size.getWidth();
    double height = size.getHeight();
    GeneralPath topShape = new GeneralPath();
    topShape.moveTo(0, 0);
    topShape.lineTo(width, 0);
    topShape.lineTo(width * (1 - r), height * r);
    topShape.lineTo(width  * r, height  * r);
    topShape.closePath();

    GeneralPath leftShape = new GeneralPath();
    leftShape.moveTo(0, 0);
    leftShape.lineTo(width  * r, height  * r);
    leftShape.lineTo(width  * r, height * (1 - r));
    leftShape.lineTo(0, height);
    leftShape.closePath();

    GeneralPath bottomShape = new GeneralPath();
    bottomShape.moveTo(0, height);
    bottomShape.lineTo(width  * r, height * (1 - r));
    bottomShape.lineTo(width * (1 - r), height * (1 - r));
    bottomShape.lineTo(width, height);
    bottomShape.closePath();

    GeneralPath rightShape = new GeneralPath();
    rightShape.moveTo(width, 0);
    rightShape.lineTo(width * (1 - r), height  * r);
    rightShape.lineTo(width * (1 - r), height * (1 - r));
    rightShape.lineTo(width, height);
    rightShape.closePath();

    if (placement != RIGHT && rightShape.contains(point)) return RIGHT;
    if (placement != LEFT && leftShape.contains(point)) return LEFT;
    if (placement != BOTTOM && bottomShape.contains(point)) return BOTTOM;
    if (placement != TOP && topShape.contains(point)) return TOP;
    return new Rectangle(size).contains(point) ? CENTER : -1;
  }
}
