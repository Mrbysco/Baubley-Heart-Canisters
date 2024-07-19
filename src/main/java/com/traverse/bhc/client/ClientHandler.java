package com.traverse.bhc.client;

import com.traverse.bhc.client.screens.BladeOfVitalityScreen;
import com.traverse.bhc.client.screens.HeartAmuletScreen;
import com.traverse.bhc.client.screens.SoulHeartAmuletScreen;
import com.traverse.bhc.common.init.RegistryHandler;
import com.traverse.bhc.common.util.EasterEgg;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientHandler {

    public static void registerMenu(RegisterMenuScreensEvent event) {
        event.register(RegistryHandler.HEART_AMUlET_CONTAINER.get(), HeartAmuletScreen::new);
        event.register(RegistryHandler.SOUL_HEART_AMUlET_CONTAINER.get(), SoulHeartAmuletScreen::new);
        event.register(RegistryHandler.BLADE_OF_VITALITY_CONTAINER.get(), BladeOfVitalityScreen::new);
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EasterEgg.secretCode();
        });
    }
}
