package studio.arcana.ethos.commands;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import studio.arcana.ethos.EthosCore;
import studio.arcana.ethos.client.DialogueOverlayHandler; // Импорт твоего оверлея
import studio.arcana.ethos.client.DialogueOption;
import studio.arcana.ethos.client.DialogueScreen;
import studio.arcana.ethos.data.DialogueData;
import studio.arcana.ethos.data.QuestData;
import studio.arcana.ethos.logic.QuestManager;

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
                // Выполняем в потоке клиента
                Minecraft.getInstance().execute(() -> {
                    try {
                        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "dialogues/test_npc.json");
                        Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(loc);
                        
                        if (res.isPresent()) {
                            try (Reader reader = new InputStreamReader(res.get().open(), StandardCharsets.UTF_8)) {
                                DialogueData data = GSON.fromJson(reader, DialogueData.class);
                                
                                List<DialogueOption> options = new ArrayList<>();
                                for (DialogueData.Option opt : data.options) {
                                    // Теперь передаем имя NPC и время отображения в обработчик
                                    options.add(new DialogueOption(opt.text, () -> 
                                        handleAction(data.npc_name, opt.action_type, opt.action_value, data.display_ticks)
                                    ));
                                }

                                Minecraft.getInstance().setScreen(new DialogueScreen(data.npc_name, data.dialogue_text, options));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return 1;
            })
        );
    }

    private static void handleAction(String npcName, String type, String value, int ticks) {
        if (Minecraft.getInstance().player == null) return;

        // Старый вариант — сообщение в чат
        if ("MESSAGE".equals(type)) {
            Minecraft.getInstance().player.sendSystemMessage(Component.literal(value));
        } 
        // НОВЫЙ ВАРИАНТ — Твой оверлей
        else if ("OVERLAY".equals(type)) {
            // Если в JSON не указано display_ticks, ставим 200 тиков (10 секунд) по умолчанию
            int displayTime = ticks > 0 ? ticks : 200;
            DialogueOverlayHandler.show(npcName, value, displayTime);
        }
        else if ("ACCEPT_QUEST".equals(type)) {
            try {
                ResourceLocation qLoc = ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "quests/" + value + ".json");
                Optional<Resource> qRes = Minecraft.getInstance().getResourceManager().getResource(qLoc);
                
                if (qRes.isPresent()) {
                    try (Reader reader = new InputStreamReader(qRes.get().open(), StandardCharsets.UTF_8)) {
                        QuestData quest = GSON.fromJson(reader, QuestData.class);
                        QuestManager.acceptQuest(quest);
                        
                        // Квест тоже можно подтверждать через оверлей
                        DialogueOverlayHandler.show("Система", "§6Задание принято: §e" + quest.title, 100);
                    }
                }
            } catch (Exception e) {
                Minecraft.getInstance().player.sendSystemMessage(Component.literal("§cОшибка загрузки квеста: " + value));
            }
        }
    }

    // Добавь этот метод в DialogueCommand.java
    public static void loadAndShowDialogue(String relativePath) {
        Minecraft.getInstance().execute(() -> {
            try {
                ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "dialogues/" + relativePath);
                Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(loc);
            
                if (res.isPresent()) {
                    try (Reader reader = new InputStreamReader(res.get().open(), StandardCharsets.UTF_8)) {
                        DialogueData data = GSON.fromJson(reader, DialogueData.class);
                    
                        List<DialogueOption> options = new ArrayList<>();
                        for (DialogueData.Option opt : data.options) {
                            options.add(new DialogueOption(opt.text, () -> 
                                handleAction(data.npc_name, opt.action_type, opt.action_value, data.display_ticks)
                            ));
                        }
                        Minecraft.getInstance().setScreen(new DialogueScreen(data.npc_name, data.dialogue_text, options));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}