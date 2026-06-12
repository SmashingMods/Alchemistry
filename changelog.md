# Alchemistry 1.21.5-2.4.8 RELEASE

Requires ChemLib and AlchemyLib for this version.

Changes:
- Updated to Minecraft 1.21.5 / NeoForge. First Alchemistry release for 1.21.5.
- JEI integration returns: every machine's recipe category is back in JEI, along with the "+" recipe-transfer buttons.
- Hardened JEI recipe transfers (the "+" button): no more crashes or duplicated items when your inventory only partially matches a recipe, max-transfer now claims your largest matching stack instead of the first one found, and transfers into a locked machine explain the refusal in the button's tooltip instead of presenting a dead button.
- The Modonomicon guidebook now renders its crafting-recipe pages.
- Fixed the #12 class of crashes: modpack recipes whose ingredients are NeoForge custom ingredients or resolve to no items (empty tags) no longer crash world creation or the recipe selector.
- Recipe selector choices now stick instead of snapping back to the first matching recipe a tick later, and JEI's "+" transfer respects the exact recipe you clicked.
- Locked machines now accept refills of their recipe's own ingredients (any slot, full stacks); other items are still refused.
- Reactor energy/input/output ports now connect to adjacent pipes and cables immediately after the multiblock forms, instead of needing the pipe to be broken and replaced.
- Machine tooltips now show the actual configured energy use instead of an unfilled placeholder, and all Alchemistry blocks list the mod name in their tooltip.
- The recipe selector keeps the machine screen visible behind it and gained a Back button, and ghost-item tooltips now render above neighboring ghost items.
- Combiner recipe layouts in the recipe selector now follow the recipe's chemical-formula order; what each recipe crafts and costs is unchanged.
- Recipe selector search now matches item display names.

Have questions about the new version or just want to chat? Join the Discord: https://discord.gg/4swu3fy
