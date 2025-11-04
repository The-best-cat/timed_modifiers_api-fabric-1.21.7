package net.theblackcat.timed_modifier_api;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.theblackcat.timed_modifier_api.api.TimedModifierAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimedModifiers implements ModInitializer {
	public static final String MOD_ID = "timed_modifier_api";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
//        AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
//            if (!world.isClient()) {
//                TimedModifierAPI.addModifier(playerEntity, EntityAttributes.MOVEMENT_SPEED, new EntityAttributeModifier(
//                        Id("test"),
//                        2,
//                        EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
//                ), 60).decrease();
//            }
//            return ActionResult.PASS;
//        });
	}

    public static Identifier Id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}