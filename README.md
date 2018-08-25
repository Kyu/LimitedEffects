# LimitedEffects
###### v 0.2.0

## About
- This plugin does what the name suggests, it limits effects such as Enchantments, Potions etc
- This plugin is in very early development, the features are very primitive

## Features
- Limit all Enchants to a number

## How to use
- Change the limit number in the `config.yml` file to any number you want (Default is 6)
- Set minimum enchant value(Default is 0)
- Set limit at 0 to disable enchanting
- Negative config values are treated as 0

#### Commands
- `/le reload` : Reflects config changes in server

#### Permissions
- `limitedeffects.*` : Grants users all plugin permissions
- `limitedeffects.reload`: Allows user to use `/le reload`
- `limitedeffects.bypass` : Allows player to bypass all limits in all categories
- `limitedeffects.bypass.enchants.*`: Allows player to bypass all enchant limits
- `limitedeffects.bypass.enchants.limit` : Allows player to bypass all enchant upper limits
- `limitedeffects.bypass.enchants.minimum`: Allows player to bypass all minimum enchant limits

#### Download
- [Latest Version](https://github.com/Kyu/LimitedEffects/releases/tag/v0.2.0)
- [All Versions](https://github.com/Kyu/LimitedEffects/releases)

#### TODO
- ~~Bypass permission~~
- ~~Minimum enchant level~~
- Limit potion effects
- More exhaustive config(limit specific effects, items, item types etc
effects gained from other sources (like food), item stacking)
- Taking more suggestions
