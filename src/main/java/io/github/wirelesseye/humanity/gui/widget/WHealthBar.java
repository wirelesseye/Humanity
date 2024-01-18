package io.github.wirelesseye.humanity.gui.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;

import java.util.function.Supplier;

public class WHealthBar extends WIconBar {
    static private final int HEART_SIZE = 9;

    static private final Icon HEART_BACKGROUND = (matrices, x, y, size) -> {
        float u1 = 16 * PX, u2 = u1 + HEART_SIZE * PX;
        float v1 = 0, v2 = HEART_SIZE * PX;
        ScreenDrawing.texturedRect(matrices, x, y, size, size, ICONS_TEXTURE, u1, v1, u2, v2, COLOR);
    };

    static private final Icon HEART_FULL = (matrices, x, y, size) -> {
        float u1 = 52 * PX, u2 = u1 + HEART_SIZE * PX;
        float v1 = 0, v2 = HEART_SIZE * PX;
        ScreenDrawing.texturedRect(matrices, x, y, size, size, ICONS_TEXTURE, u1, v1, u2, v2, COLOR);
    };

    static private final Icon HEART_HALF = (matrices, x, y, size) -> {
        float u1 = 61 * PX, u2 = u1 + HEART_SIZE * PX;
        float v1 = 0, v2 = HEART_SIZE * PX;
        ScreenDrawing.texturedRect(matrices, x, y, size, size, ICONS_TEXTURE, u1, v1, u2, v2, COLOR);
    };

    public WHealthBar(Supplier<Float> valueSupplier, float maxValue, int size) {
        super(HEART_BACKGROUND, HEART_FULL, HEART_HALF, valueSupplier, maxValue, size, WBar.Direction.RIGHT);
    }
}
