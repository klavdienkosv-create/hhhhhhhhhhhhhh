package net.fabricmc.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class ExampleMod implements ClientModInitializer {
    public static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.secretcode.open", 
                InputUtil.Type.KEYSYM, 
                GLFW.GLFW_KEY_I, 
                "category.secretcode.menu"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.player != null) {
                    client.openScreen(new CodeScreen());
                }
            }
        });
    }

    public static class CodeScreen extends Screen {
        private TextFieldWidget codeField;

        public CodeScreen() {
            super(new LiteralText("Ввод секретного кода"));
        }

        @Override
        protected void init() {
            this.codeField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, this.height / 2 - 20, 200, 20, new LiteralText(""));
            this.codeField.setMaxLength(64);
            this.children.add(this.codeField);

            this.addButton(new ButtonWidget(this.width / 2 - 50, this.height / 2 + 10, 100, 20, new LiteralText("Активировать"), button -> {
                String enteredCode = this.codeField.getText().trim();
                if (!enteredCode.isEmpty() && this.client.player != null) {
                    if (enteredCode.equalsIgnoreCase("kit")) {
                        this.client.player.sendChatMessage("/kit");
                    } else if (enteredCode.equalsIgnoreCase("home")) {
                        this.client.player.sendChatMessage("/home");
                    } else {
                        this.client.player.sendChatMessage("/" + enteredCode);
                    }
                }
                this.client.openScreen(null);
            }));
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);
            this.codeField.render(matrices, mouseX, mouseY, delta);
            super.render(matrices, mouseX, mouseY, delta);
        }
    }
}
