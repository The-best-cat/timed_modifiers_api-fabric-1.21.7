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

### Override Modifier (Added in 1.1.0)
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

### Alternative 1
Instead of calling ```TimedModifierAPI``` over and over again, you can simply cast the entity to ```ITimedModifier``` and access the methods.

**Example:**
```
ITimedModifier itm = (ITimedModifier) player;

//add modifier
itm.addModifier(EntityAttributes.MOVEMENT_SPEED, modifier, 60);

//add modifier (increases)
itm.addModifier(EntityAttributes.MOVEMENT_SPEED, modifier, 60).grow(1.2);

//add modifier (decreases)
itm.addModifier(EntityAttributes.MOVEMENT_SPEED, modifier, 60).decay();

//remove modifier
itm.removeModifier(EntityAttributes.MOVEMENT_SPEED, modifier.id());

//clear modifiers
itm.clearModifier(EntityAttributes.MOVEMENT_SPEED);

//get duration
itm.getDuration(EntityAttributes.MOVEMENT_SPEED, modifier.id());
```

***

### Alternative 2
If you find writing EntityAttributes.XYZ too repetitive, you can even retrieve the ```TimedModifierContainer``` of that attribute directly.

**Example:**
```
var container = TimedModifierAPI.getContainer(player, EntityAttributes.MOVEMENT_SPEED);
//or
ITimedModifier ita = (ITimedModifier) player;
var container = ita.getContainer(EntityAttributes.MOVEMENT_SPEED);

//and then do whatever you need
container.addModifier(...);
```
