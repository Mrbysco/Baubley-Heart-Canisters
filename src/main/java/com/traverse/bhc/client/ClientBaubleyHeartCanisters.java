package com.traverse.bhc.client;

import com.traverse.bhc.common.BaubleyHeartCanisters;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

@Mod.EventBusSubscriber(modid = BaubleyHeartCanisters.MODID, bus = MOD)
public class ClientBaubleyHeartCanisters {

    public static final ResourceLocation SLOT_TEXTURE = new ResourceLocation(BaubleyHeartCanisters.MODID, "slot/empty_heartamulet");

//    @OnlyIn(Dist.CLIENT)
//    @SubscribeEvent
//    public static void textureStitch(final TextureStitchEvent.Post evt) {
//       evt.getAtlas().getTextureLocations().add(SLOT_TEXTURE);
//    }
}