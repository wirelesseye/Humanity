package io.github.wirelesseye.humanity.gui.widget;

import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class WIconBar extends WWidget {
    static protected final Identifier ICONS_TEXTURE = new Identifier("minecraft", "textures/gui/icons.png");
    static protected final float PX = 1f / 256f;
    static protected final int COLOR = 0xFF_FFFFFF;

    protected final Icon backgroundIcon;
    protected final Icon fullIcon;
    protected final Icon halfIcon;
    protected final int size;
    protected final Supplier<Float> valueSupplier;
    protected final float maxValue;
    protected final WBar.Direction direction;

    public WIconBar(Icon backgroundIcon, Icon fullIcon, Icon halfIcon, Supplier<Float> valueSupplier, float maxValue,
                    int size, WBar.Direction direction) {
        this.backgroundIcon = backgroundIcon;
        this.fullIcon = fullIcon;
        this.halfIcon = halfIcon;
        this.size = size;
        this.valueSupplier = valueSupplier;
        this.maxValue = maxValue;
        this.direction = direction;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        float value = this.valueSupplier.get();
        int iconCount = (int) Math.ceil(maxValue / 2);

        int ceilValue = (int) Math.ceil(value);
        int fullIconCount = ceilValue / 2;
        int halfIconCount = ceilValue % 2;

        int i = direction == WBar.Direction.LEFT ? iconCount - 1 : 0;
        while (direction == WBar.Direction.LEFT ? i >= 0 : i < iconCount) {
            this.backgroundIcon.paint(matrices, x + i * size, y, size);
            if (fullIconCount > 0) {
                this.fullIcon.paint(matrices, x + i * size, y, size);
                fullIconCount--;
            } else if (halfIconCount > 0) {
                this.halfIcon.paint(matrices, x + i * size, y, size);
                halfIconCount--;
            }

            if (direction == WBar.Direction.LEFT) {
                i--;
            } else {
                i++;
            }
        }
    }
}
