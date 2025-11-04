package net.theblackcat.timed_modifier_api.interfaces;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.theblackcat.timed_modifier_api.api.TimedModifierContainer;
import net.theblackcat.timed_modifier_api.api.TimedModifierData;

public interface ITimedModifier {
    TimedModifierData addModifier(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, int tick);

    void removeModifier(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier);
    void removeModifier(RegistryEntry<EntityAttribute> attribute, Identifier identifier);

    void clearModifier(RegistryEntry<EntityAttribute> attribute);
    int getDuration(RegistryEntry<EntityAttribute> attribute, Identifier identifier);

    TimedModifierContainer getContainer(RegistryEntry<EntityAttribute> attribute);
}
