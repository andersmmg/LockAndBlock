package com.andersmmg.lockandblock.client.screen;

import com.andersmmg.lockandblock.LockAndBlock;
import com.andersmmg.lockandblock.block.entity.KeycardReaderBlockEntity;
import com.andersmmg.lockandblock.record.KeycardReaderPacket;
import com.andersmmg.lockandblock.record.KeycardReaderPacketType;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class KeycardReaderScreen extends BaseOwoScreen<FlowLayout> {
    private final KeycardReaderBlockEntity blockEntity;
    private final String uuid;
    private final boolean toggle;

    public KeycardReaderScreen(KeycardReaderBlockEntity entity, String uuid, boolean toggle) {
        this.blockEntity = entity;
        this.uuid = uuid;
        this.toggle = toggle;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        rootComponent.child(
                Containers.verticalFlow(Sizing.content(), Sizing.content())
                        .child(Components.label(LockAndBlock.langText("keycard_reader.title", "gui")))
                        .gap(5)
                        .child(Components.button(LockAndBlock.langText("keycard_reader.clear", "gui"), button -> {
                            LockAndBlock.KEYCARD_READER_CHANNEL.clientHandle().send(new KeycardReaderPacket(blockEntity.getPos(), KeycardReaderPacketType.CLEAR, uuid));
                            close();
                        }).horizontalSizing(Sizing.fixed(70)))
                        .child(Components.button(LockAndBlock.langText("keycard_reader.remove", "gui"), button -> {
                            LockAndBlock.KEYCARD_READER_CHANNEL.clientHandle().send(new KeycardReaderPacket(blockEntity.getPos(), KeycardReaderPacketType.REMOVE, uuid));
                            close();
                        }).horizontalSizing(Sizing.fixed(70)))
                        .child(Components.checkbox(LockAndBlock.langText("keycard_reader.toggle", "gui"))
                                .checked(toggle)
                                .onChanged(value -> {
                                    LockAndBlock.KEYCARD_READER_CHANNEL.clientHandle().send(
                                            new KeycardReaderPacket(blockEntity.getPos(), value ? KeycardReaderPacketType.TOGGLE_ON : KeycardReaderPacketType.TOGGLE_OFF, uuid)
                                    );
                                })
                                .horizontalSizing(Sizing.fixed(70)))
                        .padding(Insets.of(10))
                        .surface(Surface.DARK_PANEL)
                        .verticalAlignment(VerticalAlignment.CENTER)
                        .horizontalAlignment(HorizontalAlignment.CENTER)
        );
    }
}