# Alchemistry 1.18.2-2.1.3 RELEASE

This is just a quick fix for a few little bugs.

Bug Fixes:
- Fix diamond recipe; you couldn't make diamond for at least one version of the mod. :|
- Fix how the dissolver handles recipes over the network. It was allowing item stacks larger than 64 which was causing the network packets to overflow.
- Make sure the dissolver is outputting all of the outputs when a recipe outputs more than one stack of the same item.
- Fix ChemLibRecipes provider such that the tags for elements only works for SOLID matter state elements. No hydrogen nuggets for dinner.

Changes:
- Tweak some Thermal dissolver recipes.

Have questions about the new version or just want to chat? Join the Discord: https://discord.gg/4swu3fy