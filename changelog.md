# Alchemistry 1.21.1-2.3.5 RELEASE

NeoForge port of Alchemistry for Minecraft 1.21.1. Requires NeoForge 21.1.228+,
ChemLib 2.0.20+, and AlchemyLib 1.0.31+.

Datapack authors: this release contains breaking schema changes for any datapack
that overrides Alchemistry recipes.

- Recipe `result`: `"item": "..."` → `"id": "..."` (vanilla 1.21 schema change).
- Fluid `"amount"`: string → integer.
- Tag namespace: `forge:` → `c:` (NeoForge Common tag namespace, which ChemLib now
  emits). Affected paths include `dusts/<chemical>`, `storage_blocks/<chemical>`,
  `ingots/<metal>`, `nuggets/<metal>`, `plates/<metal>`, `ores/<chemical>`,
  plus the cross-mod tags `chests/wooden`, `dyes/*`, `slag`, `bitumen`,
  `coal_coke`, `gems/apatite`, `gems/cinnabar`, `gems/niter`, `gems/sulfur`.
- License: `gradle.properties` now declares `LGPL-2.1-only` (matching ChemLib /
  AlchemyLib). The `LICENSE` file text is unchanged.

Patchouli guidebook is restored against Patchouli 1.21.1-93-NEOFORGE
(`vazkii.patchouli:Patchouli` from `maven.blamejared.com`). The book is
optional at runtime — Alchemistry runs without Patchouli installed.

# Alchemistry 1.20.1-2.3.4 RELEASE

Changes:
- Merge PR #327 to fix issues with pushing items through Mekanism pipes into the combiner inventory. Better logic, thank you!
- Allow NBT when checking item stacks for the dissolver and combiner recipe serializers. For closed PR #321.

Have questions about the new version or just want to chat? Join the Discord: https://discord.gg/4swu3fy