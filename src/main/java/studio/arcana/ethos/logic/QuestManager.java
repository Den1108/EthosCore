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
    private static List<String> completedQuestIds = new ArrayList<>();

    // Путь к файлу сохранения в папке мира
    private static File getSaveFile() {
        File dir = new File(Minecraft.getInstance().gameDirectory, "saves/" + Minecraft.getInstance().getSingleplayerServer().getWorldData().getLevelName() + "/ethos");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "player_quests.json");
    }

    public static void acceptQuest(QuestData quest) {
        if (activeQuests.stream().noneMatch(q -> q.id.equals(quest.id))) {
            activeQuests.add(quest);
            saveProgress();
        }
    }

    public static void saveProgress() {
        try (FileWriter writer = new FileWriter(getSaveFile())) {
            GSON.toJson(activeQuests, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadProgress() {
        File file = getSaveFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                activeQuests = GSON.fromJson(reader, new TypeToken<List<QuestData>>(){}.getType());
            } catch (Exception e) {
                activeQuests = new ArrayList<>();
            }
        }
    }

    public static List<QuestData> getActiveQuests() { return activeQuests; }
}