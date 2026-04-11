package com.den1108.worldexe;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.client.Minecraft;
import java.util.Arrays;

public class DialogueCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("testdialogue")
            .executes(context -> {
                Minecraft.getInstance().tell(() -> {
                    // Пример структуры диалога
                    var options = Arrays.asList(
                        new DialogueScreen.DialogueOption("Да, я готов помочь!", () -> {
                            // Тут будет выдача квеста
                            Minecraft.getInstance().player.sendSystemMessage(Component.literal("§a[Квест]: §fВы приняли задание 'Мастерская 47'!"));
                        }),
                        new DialogueScreen.DialogueOption("Нет, сейчас некогда", () -> {
                            Minecraft.getInstance().player.sendSystemMessage(Component.literal("§c[Система]: §fВы отказались от предложения."));
                        })
                    );

                    Minecraft.getInstance().setScreen(new DialogueScreen(
                        "Техник Вика", 
                        "Приветствую в секторе 47. Мне нужны детали для world.exe. Поможешь достать?", 
                        options
                    ));
                });
                return 1;
            })
        );
    }
}