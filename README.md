# Temporary Attribute API
This is an API that allows you to add temporary attribute modifiers that gets removed after a set amount of time.

### This is an API and does not add new gameplay features. This is just used as a dependency.

# For players
Simply download this and add this to your mod folder.

# For developers
# Set up


## How to use
### Example Modifier
```
EntityAttributeModifier modifier = new EntityAttributeModifier(
  Identifier.of("example", "speed"),
  0.6,
  EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
);
```

### Add modifier
This adds a temporary modifier that expires after a duration (in ticks)
```
TempAttributeAPI.addModifier(living_entity, attribute_type, attribute_modifier, int duration);
```

**Example:**
```
TempAttributeAPI.addModifier(player, EntityAttributes.MOVEMENT_SPEED, modifier, 60);
//Adds a modifier that increases speed by 60% for 60 ticks (3 seconds)
```

You can make a modifier's value grow/decay over its lifetime. To do this, call ```.increase()``` or ```.decrease()```.

You need to provide a limit for ```.increase()```, but this is optional for ```.decrease()```.

If you want this to happen in a specific amount of time instead of over the whole lifetime, provide a duration in ticks.

**Example:**
```
TempAttributeAPI.addModifier(player, EntityAttributes.MOVEMENT_SPEED, modifier, 100).decrease();
//Gradually decreases from +60% to 0% over 100 ticks (5 seconds)

TempAttributeAPI.addModifier(player, EntityAttributes.MOVEMENT_SPEED, modifier, 60).increase(1.2, 30);
//Lasts 60 ticks (3 seconds), value increases from +60% to +120% in 30 ticks (1.5 seconds)
```

### Remove modifier
This manually removes a temporary modifier before it expires.
```
TempAttributeAPI.removeModifier(living_entity, attribute_type, identifier);
```

**Example:**
```
TempAttributeAPI.addModifier(player, EntityAttributes.MOVEMENT_SPEED, modifier.id());
```

### Clear modifier
This removes all temporary modifiers added via this API.
```
TempAttributeAPI.clearModifier(living_entity, attribute_type);
```

**Example:**
```
TempAttributeAPI.clearModifier(player, EntityAttributes.MOVEMENT_SPEED);
```

### Get duration
This retrieves the remaining duration (in ticks) of a specific temporary modifier.
```
TempAttributeAPI.getDuration(living_entity, attribute_type, identifier);
```

**Example:**
```
TempAttributeAPI.getDuration(player, EntityAttributes.MOVEMENT_SPEED, modifier.id());
```

### Alternative 1
Instead of calling ```TempAttributeAPI``` over and over again, you can simply cast the entity to ```ITempAttribute``` and access the methods.

Example:
```
ITempAttribute ita = (ITempAttribute) player;

//add modifier
ita.addModifier(EntityAttributes.MOVEMENT_SPEED, modifier, 60);

//add modifier (increases)
ita.addModifier(EntityAttributes.MOVEMENT_SPEED, modifier, 60).increase(1.2);

//add modifier (decreases)
ita.addModifier(EntityAttributes.MOVEMENT_SPEED, modifier, 60).decrease();

//remove modifier
ita.removeModifier(EntityAttributes.MOVEMENT_SPEED, modifier.id());

//clear modifiers
ita.clearModifier(EntityAttributes.MOVEMENT_SPEED);

//get duration
ita.getDuration(EntityAttributes.MOVEMENT_SPEED, modifier.id());
```
### Alternative 2
If you find writing EntityAttributes.XYZ too repetitive, you can even retrieve the ```TempAttributeContainer``` of that attribute.

```
var container = TempAttributeAPI.getContainer(player, EntityAttributes.MOVEMENT_SPEED);
//or
ITempAttribute ita = (ITempAttribute) player;
var container = ita.getContainer(EntityAttributes.MOVEMENT_SPEED);

//and then do whatever you need
container.addModifier(...);
```
