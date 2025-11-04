package net.theblackcat.timed_modifier_api.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.theblackcat.timed_modifier_api.interfaces.ITimedModifier;

public class TimedModifierAPI {
    public static TimedModifierContainer getContainer(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
        return AsITA(entity).getContainer(attribute);
    }

    public static TimedModifierData addModifier(LivingEntity entity, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, int duration) {
        ValidateEntity(entity);
        ValidateAttribute(attribute);
        return AsITA(entity).addModifier(attribute, modifier, duration);
    }

    public static TimedModifierData addModifier(LivingEntity entity, EntityAttributeInstance instance, EntityAttributeModifier modifier, int duration) {
        ValidateInstance(instance);
        return addModifier(entity, instance.getAttribute(), modifier, duration);
    }

    public static void removeModifier(LivingEntity entity, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier) {
        removeModifier(entity, attribute, modifier.id());
    }

    public static void removeModifier(LivingEntity entity, RegistryEntry<EntityAttribute> attribute, Identifier identifier) {
        ValidateEntity(entity);
        ValidateAttribute(attribute);
        AsITA(entity).removeModifier(attribute, identifier);
    }

    public static void removeModifier(LivingEntity entity, EntityAttributeInstance instance, EntityAttributeModifier modifier) {
        ValidateEntity(entity);
        ValidateInstance(instance);
        removeModifier(entity, instance, modifier.id());
    }

    public static void removeModifier(LivingEntity entity, EntityAttributeInstance instance, Identifier identifier) {
        removeModifier(entity, instance.getAttribute(), identifier);
    }

    public static int getDuration(LivingEntity entity, RegistryEntry<EntityAttribute> attribute, Identifier identifier) {
        return entity instanceof ITimedModifier ita ? ita.getDuration(attribute, identifier) : 0;
    }

    public static void clearModifier(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
        AsITA(entity).clearModifier(attribute);
    }

    private static void ValidateEntity(LivingEntity entity) {
        if (entity == null) {
            throw new NullPointerException("Entity is null.");
        }
    }

    private static void ValidateInstance(EntityAttributeInstance instance) {
        if (instance == null) {
            throw new NullPointerException("Entity attribute instance is null.");
        }
    }

    private static void ValidateAttribute(RegistryEntry<EntityAttribute> attribute) {
        if (attribute == null) {
            throw new NullPointerException("Entity attribute is null.");
        }
    }

    private static ITimedModifier AsITA(LivingEntity entity) {
        if (entity instanceof ITimedModifier ita) {
            return ita;
        }
        throw new ClassCastException("This type of entity is not capable of adding temporary attributes.");
    }
}
