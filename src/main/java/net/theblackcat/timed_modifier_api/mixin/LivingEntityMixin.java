package net.theblackcat.timed_modifier_api.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.theblackcat.timed_modifier_api.TimedModifiers;
import net.theblackcat.timed_modifier_api.api.TimedModifierContainer;
import net.theblackcat.timed_modifier_api.api.TimedModifierData;
import net.theblackcat.timed_modifier_api.interfaces.ITimedModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements ITimedModifier {
    @Unique
    private HashMap<RegistryEntry<EntityAttribute>, TimedModifierContainer> tempModifierContainers;

    @Unique
    private static final Codec<Map<RegistryEntry<EntityAttribute>, TimedModifierContainer>> MOD_MAP_CODEC = Codec.unboundedMap(
            EntityAttribute.CODEC,
            TimedModifierContainer.CODEC
    );

    @Inject(method = "<init>", at = @At("TAIL"))
    private void Initialise(EntityType<? extends LivingEntity> entityType, World world, CallbackInfo info) {
        tempModifierContainers = HashMap.newHashMap(10);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void Tick(CallbackInfo info) {
        for (var container : tempModifierContainers.values()) {
            container.tick();
        }
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void SaveData(WriteView view, CallbackInfo info) {
        view.put("the_black_cat:temporary_attribute_modifiers", MOD_MAP_CODEC, tempModifierContainers);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void ReadData(ReadView view, CallbackInfo info) {
        var map = view.read("the_black_cat:temporary_attribute_modifiers", MOD_MAP_CODEC).orElse(HashMap.newHashMap(10));
        if (!map.isEmpty()) {
            for (var entry : map.entrySet()) {
                if (getContainer(entry.getKey()) != null) {
                    getContainer(entry.getKey()).addALl(entry.getValue().getAll());
                } else {
                    tempModifierContainers.put(entry.getKey(), entry.getValue().ofInstance(GetSelf()));
                }
            }
        }
    }

    @Override
    public TimedModifierData addModifier(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, int tick) {
        return this.getContainer(attribute).addModifier(modifier, tick);
    }

    @Override
    public void removeModifier(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier) {
        this.removeModifier(attribute, modifier.id());
    }

    @Override
    public void removeModifier(RegistryEntry<EntityAttribute> attribute, Identifier identifier) {
        if (ContainerExists(attribute)) {
            tempModifierContainers.get(attribute).removeModifier(identifier);
        }
    }

    @Override
    public void clearModifier(RegistryEntry<EntityAttribute> attribute) {
        if (ContainerExists(attribute)) {
            tempModifierContainers.get(attribute).clearModifiers();
        }
    }

    @Override
    public int getDuration(RegistryEntry<EntityAttribute> attribute, Identifier identifier) {
        if (ContainerExists(attribute)) {
            return tempModifierContainers.get(attribute).getDuration(identifier);
        }
        return -1;
    }

    @Override
    public TimedModifierContainer getContainer(RegistryEntry<EntityAttribute> attribute) {
        if (!ContainerExists(attribute)) {
            tempModifierContainers.put(attribute, new TimedModifierContainer(attribute).ofInstance(GetSelf()));
        }
        return tempModifierContainers.get(attribute);
    }

    @Unique
    private boolean ContainerExists(RegistryEntry<EntityAttribute> attribute) {
        return tempModifierContainers.containsKey(attribute) && tempModifierContainers.get(attribute) != null;
    }

    @Unique
    private LivingEntity GetSelf() {
        return (LivingEntity) (Object)this;
    }
}