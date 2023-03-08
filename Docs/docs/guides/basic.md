# Basics

Mining Goggles use a "wavelength" system where each "ore" has a specific wavelength that it can be found under. See table below for defaults.
Each set of goggles has a max range that it can see, however this is the optimal distance this can be seen. Such as if you are looking for something with a wavelength of 500 however you crystal's range is 300- 1000, it may be reduced due to the range's covered by the crystal. The optimal / max distance for average wavelength is shown in the gui.

![](../../img/goggles_gui.png)

## Crystals

There are multiple types of crystals.

- "Natural" crystals simple known as crystals are found in dungeon chests underground. These have random values, generally a small variation in wavelength, once activated. To activate simple right click the crystal.

- Constructed Crystals are the most basic crystals, These have random values, with quite a large range of variations in wavelength once activate. These can be crafted.
![](../../img/crystal_recipe.png)

- Attuned Crystals are the most advance type of crystal, these are "attuned" to specific ores. These are the most difficult crystals to create, requiring you to craft the crystal along with attuning it to a specific ore.

![](../../img/attunment_crystal_unattuned.png)


To "attune" a Attunement Crystal you need to have an a tuning fork with the crystal inside, and  mine a specific amount of the ore you want it to be attuned to. The first block broken while holding the crystal will bind it to that specific block. See the table below for the default amount of blocks required for any specific ore.

![](../../img/tuning_fork.png)


## Wavelength System

The wavelength system is a simple yet complex system that allows for users to find specific ores. The system allows for multiple crystals to be combined together to look for different wavelength's. This is primarily used to fine-tune for a specific ore when you don't have crystals that are attuned to a specific ore. Do note that each set of goggles has 2 sides, red and blue each side has a sperate wavelength it is looking for.
An example of this would be an `900-1100` crystal merged with an `400-550` crystal will result in `418-916` wavelength. This system is designed to be experimented with to find the optimal configuration depending on what ore you are looking for.

![](../../img/merged_crystals.png)

## Defaults

Block            | Wavelength                     | Blocks to Attune |
|:-----------------|:---------------:|:----------------:|
Coal Ore         | 350           | 100 Blocks     |
Debris           | 510           | 25  Blocks     |
Iron Ore         | 250           | 100 Blocks     |
Gold Ore         | 500           | 50 Blocks      |
Diamond Ore      | 750           | 25 Blocks      |
Redstone Ore     | 550           | 100 Blocks     |
Copper Ore       | 1050          | 100 Blocks     |
Lapis Ore        | 800           | 50 Blocks      |
Emerald Ore      | 1000          | 30 Blocks      |

Do note these are the defaults, Modpacks may change these values or add more ores. Its highly configurable.
