package studio.arcana.ethos;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.Commands;
import net.minecraft.client.Minecraft;
import java.util.Arrays;

public class DialogueCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ethos_test")
            .executes(context -> {
                Minecraft.getInstance().tell(() -> {
                    var options = Arrays.asList(
                        new DialogueScreen.DialogueOption("§e[Принять] §fЯ сделаю это.", () -> {
                            Minecraft.getInstance().player.sendSystemMessage(Component.literal("§6[Ethos]: §fЗадание добавлено в журнал."));
                        }),
                        new DialogueScreen.DialogueOption("§7[Уйти] §fМне пора.", () -> {})
                    );

                    Minecraft.getInstance().setScreen(new DialogueScreen(
                        "Странник", 
                        "Времена меняются, путник. Ты чувствуешь, как код этого мира начинает дрожать?", 
                        options
                    ));
                });
                return 1;
            })
        );
    }
}