# Timed Modifiers API
This is an API that allows you to add temporary attribute modifiers that gets removed after a set amount of time.

If you are a **player**, simply download and add this to your mod folder.  
If you are a **developer**, please read the following to learn how to use this API.

### This is an API and does not add new gameplay features. This is just used as a dependency.

## Set up
Add this to the build.gradle to depend on this API.
```
repositories {
    maven {
        url = "https://api.modrinth.com/maven"
    }
}

dependencies {
    modImplementation "maven.modrinth:timed_modifiers_api:{the_version}"
}
```

The version number will be in the form ```x.y.z+[mc_version]```, e.g. ```1.0.0+1.21.7```.

## How to use
### Example Modifier
```
EntityAttributeModifier modifier = new EntityAttributeModifier(
  Identifier.of("example", "speed"),
  0.6,
  EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
);
```

***

### Add modifier
This adds a temporary modifier that expires after a duration (in ticks)
```
TimedModifierAPI.addModifier(living_entity, attribute_type, attribute_modifier, int duration);
```

**Example:**
```
TimedModifierAPI.addModifier(player, EntityAttributes.MOVEMENT_SPEED, modifier, 60);
//Adds a modifier that increases speed by 60% for 60 ticks (3 seconds)
```

You can make a modifier's value grow/decay over its lifetime. To do this, call ```.grow()``` or ```.decay()```.

You need to provide a limit for ```.grow()```, but this is optional for ```.decay()```.

If you want this to happen in a specific amount of time instead of over the whole lifetime, provide a duration in ticks.

**Example:**
```
TimedModifierAPI.addModifier(player, EntityAttributes.MOVEMENT_SPEED, modifier, 100).decay();
//Gradually decreases from +60% to 0% over 100 ticks (5 seconds)

TimedModifierAPI.addModifier(player, EntityAttributes.MOVEMENT_SPEED, modifier, 60).grow(1.2, 30);
//Lasts 60 ticks (3 seconds), value increases from +60% to +120% in 30 ticks (1.5 seconds)
```

You can even select an easing type and ease the over time change. There are: Sine, Quadratic, Cubic, Quartic, Quintic and Circle.

Call ```ease()``` and provide the type of easing.

**Example:**
```
TimedModifierAPI.addModifier(player, EntityAttributes.MOVEMENT_SPEED, modifier, 100).decay().ease(EaseHelper.EasingTypes.SINE_OUT);
//Gradually decreases from +60% to 0% over 100 ticks (5 seconds) with sine ease out
```

***

### Remove modifier
This manually removes a temporary modifier before it expires.
```
TimedModifierAPI.removeModifier(living_entity, attribute_type, identifier);
```

**Example:**
```
TimedModifierAPI.addModifier(player, EntityAttributes.MOVEMENT_SPEED, modifier.id());
```

***

### Clear modifier
This removes all temporary modifiers added via this API.
```
TimedModifierAPI.clearModifier(living_entity, attribute_type);
```

**Example:**
```
TimedModifierAPI.clearModifier(player, EntityAttributes.MOVEMENT_SPEED);
```

***

### Override Modifier
This will override the current modifier, preventing the error when the same modifier is added.
```
TimedModifierAPI.overrideModifier(living_entity, attribute_type, modifier, duration);
```

**Example:**
```
TimedModifierAPI.overrideModifier(player, EntityAttributes.MOVEMENT_SPEED, modifier, 40).decay();
```

***

### Get duration
This retrieves the remaining duration (in ticks) of a specific temporary modifier.
```
TimedModifierAPI.getDuration(living_entity, attribute_type, identifier);
```

**Example:**
```
TimedModifierAPI.getDuration(player, EntityAttributes.MOVEMENT_SPEED, modifier.id());
```

***

### Set/Unset value
This directly sets the value of an attribute instance, ignoring any modifiers and its base value.
```
TimedModifierAPI.setValue(living_entity, attribute_type, double);
```

**Example:**
```
TimedModifierAPI.setValue(player, EntityAttributes.MOVEMENT_SPEED, 0.088);
```

This makes the attribute instance return back to its original value.
```
TimedModifierAPI.unsetValue(living_entity, attribute_type);
```

***

### Check if value is set/obtain the value
This returns whether or not the attribute instance value is set.
```
TimedModifierAPI.isValueSet(living_entity, attribute_type);
```

This returns the value that is set, or -1 if the value isn't set.

```
TimedModifierAPI.getValueSet(living_entity, attribute_type);
```

***

### Alternative 1
Instead of calling ```TimedModifierAPI``` over and over again, you can simply cast the entity to ```ITimedModifier``` and access the methods, or just ask TimedModifierAPI to cast it.

**Example:**
```
ITimedModifier api = (ITimedModifier) player;
//or 
ITimedModifier api = TimedModifierAPI.asApi(player);

//add modifier
api.addModifier(EntityAttributes.MOVEMENT_SPEED, modifier, 60);

//remove modifier
api.removeModifier(EntityAttributes.MOVEMENT_SPEED, modifier.id());

//clear modifiers
api.clearModifier(EntityAttributes.MOVEMENT_SPEED);

//get duration
api.getDuration(EntityAttributes.MOVEMENT_SPEED, modifier.id());

//set Value
api.setValue(EntityAttributes.MOVEMENT_SPEED, 0.088);

//unset value
api.unsetValue(EntityAttributes.MOVEMENT_SPEED);
```

***

### Alternative 2
If you find writing EntityAttributes.XYZ too repetitive, you can even retrieve the ```TimedModifierContainer``` of that attribute directly.

**Example:**
```
var container = TimedModifierAPI.getContainer(player, EntityAttributes.MOVEMENT_SPEED);
//or
ITimedModifier api = TimedModifierAPI.asApi(player);
var container = api.getContainer(EntityAttributes.MOVEMENT_SPEED);

//and then do whatever you need
container.addModifier(...);
```

Please note that you cannot call ```setValue()```, ```unsetValue()```, ```isValueSet()``` and ```getValueSet()``` from a container. In order to call these methods, call ```asApi()``` first.

**Example**
```
container.asApi().setValue(25);
```
