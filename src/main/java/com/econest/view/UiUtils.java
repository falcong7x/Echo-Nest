package com.econest.view;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;

/** to make every thing look same incase a mess up**/
public final class UiUtils {

    public static final Color PRIMARY = new Color(0x2E7D32);
    public static final Color DANGER = new Color(0xC62828);
    public static final Color NEUTRAL = new Color(0x546E7A);

    private UiUtils() {
    }

    public static JButton button(String text, Color background) {
        JButton b = new JButton(text);
        b.setBackground(background);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(110, 32));
        return b;
    }
}
