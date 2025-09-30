package com.cxk.gameinfo.keybind;

import com.cxk.gameinfo.Gameinfo;
import com.cxk.gameinfo.GameinfoClient;
import com.cxk.gameinfo.gui.GameInfoConfigScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {


    public static void register() {
        registerOpenGui();
        registerOpenGameInfo();
    }

    public static void registerOpenGui() {
        KeyBinding openGuiKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gameinfo.open_gui", // 翻译键
                GLFW.GLFW_KEY_U, // P键
                KeyBinding.Category.create(Identifier.of(GameinfoClient.modName))
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKeyBinding.wasPressed()) {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.currentScreen == null) {
                    mc.setScreen(new GameInfoConfigScreen(null));
                }
            }
        });
    }

    public static void registerOpenGameInfo() {
//        KeyBinding toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
//                "key.gameinfo.toggle_hud",
//                GLFW.GLFW_KEY_F1,
//                KeyBinding.Category.create(Identifier.of(GameinfoClient.modName))
//        ));
//
//        ClientTickEvents.END_CLIENT_TICK.register(client -> {
//            while (toggleHudKey.wasPressed()) {
//                GameinfoClient.hudOverlay.toggleHudVisibility();
//            }
//        });
    }
} 