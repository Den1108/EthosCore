package studio.arcana.ethos.logic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import studio.arcana.ethos.data.QuestData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class QuestManager {
    private static final Gson GSON = new Gson();
    private static List<QuestData> activeQuests = new ArrayList<>();

    private static File getSaveFile() {
        // Проверяем, зашел ли игрок в мир
        if (Minecraft.getInstance().level == null) return null;
        
        // Путь: .minecraft/saves/Название_Мира/ethos/player_quests.json
        File saveDir = new File(Minecraft.getInstance().gameDirectory, "saves/" + Minecraft.getInstance().getSingleplayerServer().getWorldData().getLevelName() + "/ethos");
        if (!saveDir.exists()) saveDir.mkdirs();
        return new File(saveDir, "player_quests.json");
    }

    public static void acceptQuest(QuestData quest) {
        if (activeQuests.stream().noneMatch(q -> q.id.equals(quest.id))) {
            activeQuests.add(quest);
            saveProgress();
        }
    }

    public static void saveProgress() {
        File file = getSaveFile();
        if (file == null) return;

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(activeQuests, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadProgress() {
        File file = getSaveFile();
        if (file != null && file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                List<QuestData> loaded = GSON.fromJson(reader, new TypeToken<List<QuestData>>(){}.getType());
                if (loaded != null) activeQuests = loaded;
            } catch (Exception e) {
                // Вместо ошибки в лог просто создаем пустой список
                activeQuests = new ArrayList<>();
            }
        } else {
            activeQuests = new ArrayList<>();
        }
    }

    public static void checkItems(net.minecraft.world.entity.player.Player player) {
        boolean changed = false;
        for (QuestData quest : activeQuests) {
            for (QuestData.Objective obj : quest.objectives) {
                if ("ITEM".equals(obj.type)) {
                    // Считаем сколько предметов нужного типа у игрока
                    int count = player.getInventory().items.stream()
                        .filter(stack -> !stack.isEmpty() && stack.getItem().getDescriptionId().contains(obj.target_id.replace("minecraft:", "")))
                        .mapToInt(net.minecraft.world.item.ItemStack::getCount)
                        .sum();
                
                    if (obj.current_progress != count) {
                        obj.current_progress = count;
                        changed = true;
                    }
                }
            }
        }
        if (changed) saveProgress(); // Сохраняем, если цифры изменились
    }

    public static List<QuestData> getActiveQuests() {
        return activeQuests;
    }
}