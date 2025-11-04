package net.theblackcat.timed_modifier_api.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.theblackcat.timed_modifier_api.TimedModifiers;

public class TimedModifierData {
    public final Identifier modifierId;

    private final double baseValue;
    private final int maxDuration;

    private int duration;
    private boolean change;
    private double limit;
    private int changeDuration;

    public TimedModifierData(EntityAttributeModifier modifier, int duration) {
        this(modifier.id(), modifier.value(), duration, duration, false, 0, 0);
    }

    public TimedModifierData(Identifier id, double baseValue, int baseDuration, int duration, boolean change, double limit, int changeDuration) {
        this.modifierId = id;
        this.baseValue = baseValue;
        this.maxDuration = baseDuration;
        this.duration = duration;
        this.change = change;
        this.limit = limit;
        this.changeDuration = changeDuration;
    }

    public int getDuration() {
        return duration;
    }

    public boolean shouldChange() {
        return change;
    }

    public double getChangeLimit() {
        return limit;
    }

    public void decay() {
        decay(0);
    }

    public void decay(double limit) {
        decay(limit, maxDuration);
    }

    public void decay(double limit, int duration) {
        Change(Math.min(baseValue, limit), duration);
    }

    public void grow(double limit) {
        grow(limit, maxDuration);
    }

    public void grow(double limit, int duration) {
        Change(Math.max(baseValue, limit), duration);
    }

    private void Change(double limit, int duration) {
        change = true;
        this.limit = limit;
        changeDuration = duration;
    }

    public boolean tryRemove() {
        duration--;
        return duration <= 0;
    }

    public double tryChange() {
        if (change) {
            return MathHelper.lerp(Math.clamp((float) (maxDuration - duration) / (float) changeDuration, 0f, 1f), baseValue, limit);
        }
        return baseValue;
    }

    public TimedModifierData copy(TimedModifierData data) {
        return new TimedModifierData(data.modifierId, data.baseValue, data.maxDuration, data.getDuration(), data.shouldChange(), data.getChangeLimit(), data.changeDuration);
    }

    public static Codec<TimedModifierData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("modifier_id").forGetter(data -> data.modifierId),
            Codec.DOUBLE.fieldOf("base_value").forGetter(data -> data.baseValue),
            Codec.INT.fieldOf("base_duration").forGetter(data -> data.maxDuration),
            Codec.INT.fieldOf("duration").forGetter(TimedModifierData::getDuration),
            Codec.BOOL.fieldOf("change").forGetter(TimedModifierData::shouldChange),
            Codec.DOUBLE.fieldOf("limit").forGetter(TimedModifierData::getChangeLimit),
            Codec.INT.fieldOf("change_duration").forGetter(data -> data.changeDuration)
    ).apply(instance, TimedModifierData::new));
}
