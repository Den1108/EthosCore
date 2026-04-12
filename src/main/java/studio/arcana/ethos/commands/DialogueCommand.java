package studio.arcana.ethos.commands;

import studio.arcana.ethos.EthosCore;
import studio.arcana.ethos.data.DialogueData;
import studio.arcana.ethos.data.QuestData;
import studio.arcana.ethos.logic.QuestManager;
import studio.arcana.ethos.client.DialogueScreen;
import studio.arcana.ethos.client.DialogueOption;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DialogueCommand {
    private static final Gson GSON = new Gson();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ethos_test")
            .executes(context -> {
                // Вызываем через tell(), так как команды сервера не могут напрямую открывать Screen на клиенте
                Minecraft.getInstance().tell(() -> {
                    loadAndShowDialogue("test_npc");
                });
                return 1;
            })
        );
    }

    public static void loadAndShowDialogue(String dialogueId) {
        try {
            ResourceLocation loc = new ResourceLocation(EthosCore.MODID, "dialogues/" + dialogueId + ".json");
            Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(loc);

            if (resource.isPresent()) {
                try (Reader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {
                    DialogueData data = GSON.fromJson(reader, DialogueData.class);

                    // ПРОВЕРКА: Есть ли варианты выбора?
                    if (data.options == null || data.options.isEmpty()) {
                        // ВАРИАНТ 1: Просто фраза над инвентарем (Action Bar)
                        if (Minecraft.getInstance().player != null) {
                            String message = "§6" + data.npc_name + ": §f" + data.dialogue_text;
                            Minecraft.getInstance().player.displayClientMessage(Component.literal(message), true);
                        }
                    } else {
                        // ВАРИАНТ 2: Открываем полноценное меню с выбором
                        List<DialogueOption> options = new ArrayList<>();
                        for (DialogueData.Option opt : data.options) {
                            options.add(new DialogueOption(opt.text, () -> handleAction(opt.action_type, opt.action_value)));
                        }
                        Minecraft.getInstance().setScreen(new DialogueScreen(data.npc_name, data.dialogue_text, options));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleAction(String type, String value) {
        if ("MESSAGE".equals(type)) {
            Minecraft.getInstance().player.sendSystemMessage(Component.literal(value));
        } else if ("ACCEPT_QUEST".equals(type)) {
            try {
                ResourceLocation qLoc = new ResourceLocation(EthosCore.MODID, "quests/" + value + ".json");
                Optional<Resource> qRes = Minecraft.getInstance().getResourceManager().getResource(qLoc);
                
                if (qRes.isPresent()) {
                    try (Reader reader = new InputStreamReader(qRes.get().open(), StandardCharsets.UTF_8)) {
                        QuestData quest = GSON.fromJson(reader, QuestData.class);
                        QuestManager.acceptQuest(quest);
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("§6[Ethos]: §fВы приняли квест: §e" + quest.title));
                    }
                }
            } catch (Exception e) {
                Minecraft.getInstance().player.sendSystemMessage(Component.literal("§cОшибка загрузки квеста: " + value));
            }
        } else if ("CLOSE".equals(type)) {
            // Screen закроется автоматически при нажатии кнопки (логика в DialogueScreen)
        }
    }
}