package my.wirelesseye.humanity.gui.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;

import java.util.function.Supplier;

public class WHungerBar extends WIconBar {
    static private final int HUNGER_SIZE = 9;

    static private final Icon HUNGER_BACKGROUND = (matrices, x, y, size) -> {
        float u1 = 16 * PX, u2 = u1 + HUNGER_SIZE * PX;
        float v1 = 27 * PX, v2 = v1 + HUNGER_SIZE * PX;
        ScreenDrawing.texturedRect(matrices, x, y, size, size, ICONS_TEXTURE, u1, v1, u2, v2, COLOR);
    };

    static private final Icon HUNGER_FULL = (matrices, x, y, size) -> {
        float u1 = 52 * PX, u2 = u1 + HUNGER_SIZE * PX;
        float v1 = 27 * PX, v2 = v1 + HUNGER_SIZE * PX;
        ScreenDrawing.texturedRect(matrices, x, y, size, size, ICONS_TEXTURE, u1, v1, u2, v2, COLOR);
    };

    static private final Icon HUNGER_HALF = (matrices, x, y, size) -> {
        float u1 = 61 * PX, u2 = u1 + HUNGER_SIZE * PX;
        float v1 = 27 * PX, v2 = v1 + HUNGER_SIZE * PX;
        ScreenDrawing.texturedRect(matrices, x, y, size, size, ICONS_TEXTURE, u1, v1, u2, v2, COLOR);
    };

    public WHungerBar(Supplier<Float> valueSupplier, float maxValue, int size) {
        super(HUNGER_BACKGROUND, HUNGER_FULL, HUNGER_HALF, valueSupplier, maxValue, size, WBar.Direction.LEFT);
    }
}
