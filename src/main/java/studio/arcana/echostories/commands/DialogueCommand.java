package studio.arcana.echostories.commands;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import studio.arcana.echostories.EchoStories;
import studio.arcana.echostories.client.DialogueOverlayHandler;
import studio.arcana.echostories.client.DialogueScreen;
import studio.arcana.echostories.data.DialogueData;
import studio.arcana.echostories.data.QuestData;
import studio.arcana.echostories.logic.QuestManager;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class DialogueCommand {
    private static final Gson GSON = new Gson();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("echo_test")
            .executes(context -> {
                Minecraft.getInstance().tell(() -> {
                    loadAndShowDialogue("test_npc");
                });
                return 1;
            })
        );

        dispatcher.register(Commands.literal("echo_info")
            .executes(context -> {
                Minecraft.getInstance().tell(() -> {
                    loadAndShowDialogue("test_info");
                });
                return 1;
            })
        );
    }

    public static void loadAndShowDialogue(String dialogueId) {
        try {
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(EchoStories.MODID, "dialogues/" + dialogueId + ".json");
            Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(loc);

            if (resource.isPresent()) {
                try (Reader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {
                    DialogueData data = GSON.fromJson(reader, DialogueData.class);
                    
                    if (data == null || data.start_node == null) return;

                    DialogueData.Node startNode = data.getNode(data.start_node);
                    if (startNode == null) return;

                    // Если у стартового узла нет вариантов ответа — направляем в менеджер оверлеев
                    if (startNode.options == null || startNode.options.isEmpty()) {
                        DialogueOverlayHandler.showNode(data, startNode);
                    } else {
                        // Иначе открываем полноценный GUI
                        Minecraft.getInstance().setScreen(new DialogueScreen(data, startNode));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleAction(String type, String value) {
        if (Minecraft.getInstance().player == null) return;

        if ("MESSAGE".equals(type)) {
            Minecraft.getInstance().player.sendSystemMessage(Component.literal(value));
        } else if ("ACCEPT_QUEST".equals(type)) {
            try {
                ResourceLocation qLoc = ResourceLocation.fromNamespaceAndPath(EchoStories.MODID, "quests/" + value + ".json");
                Optional<Resource> qRes = Minecraft.getInstance().getResourceManager().getResource(qLoc);
                
                if (qRes.isPresent()) {
                    try (Reader reader = new InputStreamReader(qRes.get().open(), StandardCharsets.UTF_8)) {
                        QuestData quest = GSON.fromJson(reader, QuestData.class);
                        QuestManager.acceptQuest(quest);
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("§6[Echo Stories]: §fВы приняли квест: §e" + quest.title));
                    }
                }
            } catch (Exception e) {
                Minecraft.getInstance().player.sendSystemMessage(Component.literal("§cОшибка загрузки квеста: " + value));
            }
        } else if ("COMPLETE_QUEST".equals(type)) {
            QuestManager.completeQuest(value);
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("§a[Echo Stories]: §fКвест выполнен!"));
        }
    }
}