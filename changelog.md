# Alchemistry 1.21.1-2.4.5 RELEASE

Requires ChemLib and AlchemyLib for this version.

Changes:
- Fixed a crash when opening a machine's recipe selector if a modpack recipe's ingredient resolves to no items (custom ingredients and empty tags, the #12 class of crashes).
- Hardened JEI recipe transfers (the "+" button): no more crashes or duplicated items when your inventory only partially matches a recipe, and recipes that resolve to no items no longer shadow real dissolver/liquifier entries in JEI.
- Reactor energy/input/output ports now connect to adjacent pipes and cables immediately after the multiblock forms, instead of needing the pipe to be broken and replaced.
- Recipe selector choices now stick instead of snapping back to the first matching recipe a tick later, and JEI's "+" transfer respects the exact recipe you clicked.
- Machine tooltips now show the actual configured energy use instead of an unfilled placeholder, and all Alchemistry blocks list the mod name in their tooltip.
- Machine GUIs are no longer double-darkened (via AlchemyLib 1.1.6).
- Recipe selector search now matches item display names.

Have questions about the new version or just want to chat? Join the Discord: https://discord.gg/4swu3fy
