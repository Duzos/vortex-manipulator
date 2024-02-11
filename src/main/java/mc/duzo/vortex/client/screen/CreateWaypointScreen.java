package mc.duzo.vortex.client.screen;

import mc.duzo.vortex.VortexMod;
import mc.duzo.vortex.client.VortexClientMessages;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import static mc.duzo.vortex.client.screen.ReplaceWaypointScreen.EYE_BACKGROUND;

public class CreateWaypointScreen extends Screen {
    private static final int backgroundWidth = 256;
    private static final int backgroundHeight = 256;

    private static final int nameBoxWidth = 128;
    private static final int nameBoxHeight = 20;
    private EditBoxWidget nameBox;
    private static final int confirmWidth = 48;
    private static final int confirmHeight = 16;
    private ButtonWidget confirmButton;

    public CreateWaypointScreen() {
        super(Text.translatable("screen." + VortexMod.MOD_ID + ".create_waypoint"));
    }

    @Override
    public void init() {
        super.init();

        int i = (this.width - backgroundWidth) / 2;
        int j = ((this.height) - backgroundHeight) / 2;

        this.nameBox = new EditBoxWidget(
                this.textRenderer,
                i + (backgroundWidth - nameBoxWidth) / 2,
                j + (backgroundHeight - nameBoxHeight) / 2,
                nameBoxWidth,
                nameBoxHeight,
                Text.literal("Name"),
                Text.translatable("screen." + VortexMod.MOD_ID + ".name_box")
        );
        this.nameBox.setMaxLength(16);
        this.addDrawableChild(this.nameBox);

        this.confirmButton = ButtonWidget.builder(
                Text.translatable("screen." + VortexMod.MOD_ID + ".confirm"),
                this::pressConfirm
        ).dimensions(
                i + (backgroundWidth - confirmWidth) / 2,
                (int) ((j + (backgroundHeight - confirmHeight) / 2) + (nameBoxHeight) + 4),
                confirmWidth,
                confirmHeight
        ).build();
        this.addDrawableChild(this.confirmButton);
    }

    private void pressConfirm(ButtonWidget button) {
        VortexClientMessages.sendWaypointName(this.nameBox.getText());
        this.close();
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
}
