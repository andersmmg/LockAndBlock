package com.andersmmg.lockandblock.client.screen;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.entity.KeypadBlockEntity;
import com.andersmmg.lockandblock.record.KeypadCodePacket;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KeypadScreen extends BaseOwoScreen<FlowLayout> {
    private final KeypadBlockEntity blockEntity;
    private final boolean hasCode;
    private final int BUTTON_WIDTH = 20;
    private final int BUTTON_GAP = 1;
    private String current_code = "";
    private final TextBoxComponent textBox = Components.textBox(Sizing.fixed(68), current_code);

    public KeypadScreen(KeypadBlockEntity entity) {
        this.blockEntity = entity;
        this.hasCode = entity.isSet();
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        textBox.active = false;

        textBox.keyPress().subscribe((keyCode, scanCode, modifiers) -> {
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                current_code = textBox.getText();
                checkCode();
            }
            return false;
        });

        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        rootComponent.child(
                Containers.verticalFlow(Sizing.content(), Sizing.content())
                        .child(Components.label(LockAndBlock.langText("keypad.title", "gui")))
                        .child(textBox)
                        .gap(5)
                        .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                                .gap(BUTTON_GAP)
                                .child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                        .gap(BUTTON_GAP)
                                        .child(Components.button(Text.literal("1"), button -> {
                                            addKey(button.getMessage().getString());
                                        }).horizontalSizing(Sizing.fixed(BUTTON_WIDTH)))
                                        .child(Components.button(Text.literal("2"), button -> {
                                            addKey(button.getMessage().getString());
                                        }).horizontalSizing(Sizing.fixed(BUTTON_WIDTH)))
                                        .child(Components.button(Text.literal("3"), button -> {
                                            addKey(button.getMessage().getString());
                                        }).horizontalSizing(Sizing.fixed(BUTTON_WIDTH)))
                                )
                                .child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                        .gap(BUTTON_GAP)
                                        .child(Components.button(Text.literal("4"), button -> {
                                            addKey(button.getMessage().getString());
                                        }).horizontalSizing(Sizing.fixed(BUTTON_WIDTH)))
                                        .child(Components.button(Text.literal("5"), button -> {
                                            addKey(button.getMessage().getString());
                                        }).horizontalSizing(Sizing.fixed(BUTTON_WIDTH)))
                                        .child(Components.button(Text.literal("6"), button -> {
                                            addKey(button.getMessage().getString());
                                        }).horizontalSizing(Sizing.fixed(BUTTON_WIDTH)))
                                )
                                .child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                        .gap(BUTTON_GAP)
                                        .child(Components.button(Text.literal("7"), button -> {
                                            addKey(button.getMessage().getString());
                                        }).horizontalSizing(Sizing.fixed(BUTTON_WIDTH)))
                                        .child(Components.button(Text.literal("8"), button -> {
                                            addKey(button.getMessage().getString());
                                        }).horizontalSizing(Sizing.fixed(BUTTON_WIDTH)))
                                        .child(Components.button(Text.literal("9"), button -> {
                                            addKey(button.getMessage().getString());
                                        }).horizontalSizing(Sizing.fixed(BUTTON_WIDTH)))
                                )
                                .child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                        .gap(BUTTON_GAP)
                                        .child(Components.button(Text.literal("C"), button -> {
                                            clearCode();
                                        }).horizontalSizing(Sizing.fixed(BUTTON_WIDTH)))
                                        .child(Components.button(Text.literal("0"), button -> {
                                            addKey(button.getMessage().getString());
                                        }).horizontalSizing(Sizing.fixed(BUTTON_WIDTH)))
                                        .child(Components.button(Text.literal("E"), button -> {
                                            checkCode();
                                        }).horizontalSizing(Sizing.fixed(BUTTON_WIDTH)))
                                )
                        )
                        .padding(Insets.of(10))
                        .surface(Surface.DARK_PANEL)
                        .verticalAlignment(VerticalAlignment.CENTER)
                        .horizontalAlignment(HorizontalAlignment.CENTER)
        );

        if (!hasCode) {
            rootComponent.child(
                    Components.label(LockAndBlock.langText("keypad.set_new_code", "gui").formatted(Formatting.GOLD))
            ).gap(5);
        }
    }

    void addKey(String key) {
        current_code = current_code + key;
        textBox.setText(current_code);
    }

    void checkCode() {
        LockAndBlock.KEYPAD_CODE_CHANNEL.clientHandle().send(new KeypadCodePacket(blockEntity.getPos(), current_code));
        close();
    }

    void clearCode() {
        current_code = "";
        textBox.setText(current_code);
    }

    void backspace() {
        if (!current_code.isEmpty()) {
            current_code = current_code.substring(0, current_code.length() - 1);
            textBox.setText(current_code);
        }
    }
}