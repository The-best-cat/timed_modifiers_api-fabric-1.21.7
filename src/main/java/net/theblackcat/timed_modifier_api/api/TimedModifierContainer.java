package net.theblackcat.timed_modifier_api.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.theblackcat.timed_modifier_api.TimedModifiers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TimedModifierContainer {
    public final RegistryEntry<EntityAttribute> attribute;

    private final HashMap<Identifier, TimedModifierData> tempAttributes;

    private EntityAttributeInstance instance;

    public TimedModifierContainer(RegistryEntry<EntityAttribute> attribute) {
        this(attribute, new ArrayList<>());
    }

    public TimedModifierContainer(RegistryEntry<EntityAttribute> attribute, List<TimedModifierData> data) {
        this.attribute = attribute;
        tempAttributes = HashMap.newHashMap(data.size());
        for (TimedModifierData d : data) {
            tempAttributes.put(d.modifierId, d);
        }
    }

    public static final Codec<TimedModifierContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityAttribute.CODEC.fieldOf("attribute").forGetter(container -> container.attribute),
            TimedModifierData.CODEC.listOf().fieldOf("temporary_attribute_data").forGetter(container -> new ArrayList<>(container.tempAttributes.values()))
    ).apply(instance, TimedModifierContainer::new));

    public TimedModifierContainer ofInstance(LivingEntity entity) {
        this.instance = GetInstance(entity);
        return this;
    }

    public void tick() {
        Iterator<Identifier> it = tempAttributes.keySet().iterator();
        while (it.hasNext()) {
            var id = it.next();
            var data = tempAttributes.get(id);
            var modifier = instance.getModifier(id);

            if (modifier == null || data.tryRemove()) {
                instance.removeModifier(id);
                it.remove();
            } else if (data.shouldChange()) {
                instance.updateModifier(new EntityAttributeModifier(
                        modifier.id(),
                        data.tryChange(),
                        modifier.operation()
                ));
            }
        }
    }

    public TimedModifierData addModifier(EntityAttributeModifier modifier, int duration) {
        instance.addPersistentModifier(modifier);
        var data = new TimedModifierData(modifier, duration);
        tempAttributes.put(modifier.id(), data);
        return data;
    }

    public boolean removeModifier(EntityAttributeModifier modifier) {
        return removeModifier(modifier.id());
    }

    public boolean removeModifier(Identifier identifier) {
        tempAttributes.remove(identifier);
        return instance.removeModifier(identifier);
    }

    public void clearModifiers() {
        for (Identifier id : tempAttributes.keySet()) {
            instance.removeModifier(id);
        }
        tempAttributes.clear();
    }

    public int getDuration(Identifier identifier) {
        if (instance.hasModifier(identifier) && tempAttributes.containsKey(identifier)) {
            return tempAttributes.get(identifier).getDuration();
        }
        return -1;
    }

    public void addALl(List<TimedModifierData> data) {
        for (var d : data) {
            tempAttributes.put(d.modifierId, d);
        }
    }

    public List<TimedModifierData> getAll() {
        return tempAttributes.values().stream().toList();
    }

    private @NotNull EntityAttributeInstance GetInstance(LivingEntity entity) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        if (instance != null) {
            return instance;
        }
        throw new NullPointerException("This entity doesn't have this attribute.");
    }
}
