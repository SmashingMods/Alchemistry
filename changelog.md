# Alchemistry 1.21.3-2.4.6 RELEASE

Requires ChemLib and AlchemyLib for this version.

Changes:
- Updated to Minecraft 1.21.3 / NeoForge. First Alchemistry release for 1.21.3.
- The guidebook is now powered by Modonomicon, since Patchouli has no 1.21.3 build.
- JEI also has no 1.21.3 build, so the JEI integration is not included in this version; it returns with the 1.21.4 release.
- Fixed the #12 class of crashes: modpack recipes whose ingredients are NeoForge custom ingredients or resolve to no items (empty tags) no longer crash world creation or the recipe selector.
- Recipe selector choices now stick instead of snapping back to the first matching recipe a tick later.
- Locked machines now accept refills of their recipe's own ingredients (any slot, full stacks); other items are still refused.
- Reactor energy/input/output ports now connect to adjacent pipes and cables immediately after the multiblock forms, instead of needing the pipe to be broken and replaced.
- Combiner recipe layouts in the recipe selector now follow the recipe's chemical-formula order; what each recipe crafts and costs is unchanged.
- Machine tooltips now show the actual configured energy use instead of an unfilled placeholder, and all Alchemistry blocks list the mod name in their tooltip.
- The recipe selector keeps the machine screen visible behind it and gained a Back button, and ghost-item tooltips now render above neighboring ghost items.
- Machine GUIs are no longer double-darkened (via AlchemyLib 1.1.7).
- Recipe selector search now matches item display names.

Have questions about the new version or just want to chat? Join the Discord: https://discord.gg/4swu3fy
