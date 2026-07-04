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

    // Создаем кнопку "I"
    public static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        // Регистрируем кнопку в игре
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.secretcode.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                "category.secretcode.menu"
        ));

        // Каждый тик игры проверяем: если нажали "I" — открываем окно
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.player != null) {
                    client.openScreen(new CodeScreen());
                }
            }
        });
    }

    // Создаем окно ввода кода
    public static class CodeScreen extends Screen {
        private TextFieldWidget codeField;

        public CodeScreen() {
            super(new LiteralText("Ввод секретного кода"));
        }

        @Override
        protected void init() {
            // Поле для ввода текста по центру экрана
            this.codeField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, this.height / 2 - 20, 200, 20, new LiteralText(""));
            this.codeField.setMaxLength(64);
            this.children.add(this.codeField);

            // Кнопка под полем ввода
            this.addButton(new ButtonWidget(this.width / 2 - 50, this.height / 2 + 10, 100, 20, new LiteralText("Активировать"), button -> {
                String enteredCode = this.codeField.getText().trim();

                if (!enteredCode.isEmpty() && this.client.player != null) {
                    // Если ввели "kit" -> Мод сам пишет в чат команду сервера /kit
                    if (enteredCode.equalsIgnoreCase("kit")) {
                        this.client.player.sendChatMessage("/kit");
                    }
                    // Если ввели "home" -> Автоматически пишет /home
                    else if (enteredCode.equalsIgnoreCase("home")) {
                        this.client.player.sendChatMessage("/home");
                    }
                    // Если ввели любое другое слово -> Мод просто подставит косую черту и отправит в чат
                    else {
                        this.client.player.sendChatMessage("/" + enteredCode);
                    }
                }
                this.client.openScreen(null); // Закрываем окошко после нажатия
            }));
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices); // Темный фон игры
            this.codeField.render(matrices, mouseX, mouseY, delta); // Рисуем поле
            super.render(matrices, mouseX, mouseY, delta); // Рисуем кнопку
        }
    }
}
