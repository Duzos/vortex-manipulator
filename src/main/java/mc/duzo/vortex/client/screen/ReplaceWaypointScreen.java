package mc.duzo.vortex.client.screen;

import mc.duzo.vortex.VortexMod;
import mc.duzo.vortex.client.VortexClientMessages;
import mc.duzo.vortex.util.VortexUtil;
import mc.duzo.vortex.util.Waypoint;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class ReplaceWaypointScreen extends Screen {
    public static final Identifier EYE_BACKGROUND = new Identifier(VortexMod.MOD_ID, "textures/gui/manipulator.png");
    private static final int backgroundWidth = 256;
    private static final int backgroundHeight = 256;
    private int buttonCount = 0;

    public ReplaceWaypointScreen() {
        super(Text.translatable("screen." + VortexMod.MOD_ID + ".replace_waypoint"));
    }

    @Override
    protected void init() {
        super.init();

        this.buttonCount = 0;

        Optional<ItemStack> found = VortexUtil.findManipulator(MinecraftClient.getInstance().player);

        if (found.isEmpty()) {
            this.close();
            return;
        }

        List<Waypoint> waypoints = VortexUtil.findWaypoints(found.get());

        for (Waypoint point : waypoints) {
            this.createTextButton(Text.literal(point.name()).formatted(Formatting.RED), this::onChooseWaypoint);
        }
    }

    private void onChooseWaypoint(ButtonWidget button) {
        if (!(button instanceof PressableTextWidget text)) return;

        VortexClientMessages.sendReplaceWaypoint(text.getMessage().getString());

        this.close();
    }

    private void createTextButton(Text text, ButtonWidget.PressAction onPress) {
        int width = this.textRenderer.getWidth(text);

        int i = (this.width - backgroundWidth) / 2;
        int j = ((this.height) - backgroundHeight) / 2;

        this.addDrawableChild(
                new PressableTextWidget(
                        (i + backgroundWidth / 2) - (width / 2),
                        (j + backgroundHeight / 2) - (10 / 2) + (buttonCount * 20) - (backgroundHeight / 8),
                        width,
                        10,
                        text,
                        onPress,
                        this.textRenderer
                )
        );

        this.buttonCount++;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = (this.width - backgroundWidth) / 2;
        int j = ((this.height) - backgroundHeight) / 2;

        context.drawTexture(EYE_BACKGROUND, i, j, 0, 0, backgroundWidth, backgroundHeight);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
