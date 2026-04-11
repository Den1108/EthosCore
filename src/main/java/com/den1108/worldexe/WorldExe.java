package com.den1108.worldexe;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(WorldExe.MODID)
public class WorldExe {
    public static final String MODID = "worldexe";

    public WorldExe() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        // Регистрация нашей тестовой команды диалога
        DialogueCommand.register(event.getDispatcher());
    }
}