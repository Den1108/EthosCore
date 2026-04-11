package studio.arcana.ethos;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DialogueCommand {
    private static final Gson GSON = new Gson();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ethos_test")
            .executes(context -> {
                Minecraft.getInstance().tell(() -> {
                    loadAndShowDialogue("test_npc");
                });
                return 1;
            })
        );
    }

    private static void loadAndShowDialogue(String dialogueName) {
        try {
            // Путь к файлу: assets/ethoscore/dialogues/test_npc.json
            ResourceLocation location = new ResourceLocation("ethoscore", "dialogues/" + dialogueName + ".json");
            Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(location);

            if (resource.isPresent()) {
                try (Reader reader = new InputStreamReader(resource.get().open())) {
                    DialogueData data = GSON.fromJson(reader, DialogueData.class);
                    
                    List<DialogueScreen.DialogueOption> options = new ArrayList<>();
                    for (DialogueData.Option opt : data.options) {
                        options.add(new DialogueScreen.DialogueOption(opt.text, () -> {
                            handleAction(opt.action_type, opt.action_value);
                        }));
                    }

                    Minecraft.getInstance().setScreen(new DialogueScreen(data.npc_name, data.dialogue_text, options));
                }
            } else {
                Minecraft.getInstance().player.sendSystemMessage(Component.literal("§cОшибка: Файл диалога не найден!"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleAction(String type, String value) {
        if ("MESSAGE".equals(type)) {
            Minecraft.getInstance().player.sendSystemMessage(Component.literal(value));
        } else if ("ACCEPT_QUEST".equals(type)) {
            QuestManager.acceptQuest(value);
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("§6[Ethos]: §fВы приняли квест: " + value));
        }
    }
}