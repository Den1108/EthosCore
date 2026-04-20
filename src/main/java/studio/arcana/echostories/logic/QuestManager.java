package studio.arcana.echostories.logic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import studio.arcana.echostories.data.QuestData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class QuestManager {
    private static final Gson GSON = new Gson();
    private static List<QuestData> activeQuests = new ArrayList<>();

    private static File getSaveFile() {
        if (Minecraft.getInstance().level == null) return null;
        File saveDir = new File(Minecraft.getInstance().gameDirectory, "saves/" + Minecraft.getInstance().getSingleplayerServer().getWorldData().getLevelName() + "/echostories");
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
        if (changed) saveProgress(); 
    }

    public static List<QuestData> getActiveQuests() {
        return activeQuests;
    }

    // НОВЫЕ МЕТОДЫ ДЛЯ СИСТЕМЫ СТЕЙТОВ:

    // 1. Проверяет, взят ли этот квест
    public static boolean hasQuest(String questId) {
        return activeQuests.stream().anyMatch(q -> q.id.equals(questId));
    }

    // 2. Проверяет, собраны ли все предметы для квеста
    public static boolean isQuestComplete(String questId) {
        for (QuestData q : activeQuests) {
            if (q.id.equals(questId)) {
                for (QuestData.Objective obj : q.objectives) {
                    if (obj.current_progress < obj.amount_required) {
                        return false; // Нашли невыполненную цель
                    }
                }
                return true; // Все цели выполнены
            }
        }
        return false;
    }

    // 3. Завершает квест (удаляет из списка активных)
    public static void completeQuest(String questId) {
        activeQuests.removeIf(q -> q.id.equals(questId));
        saveProgress();
    }
}