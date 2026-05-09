# Alchemistry: NeoForged 1.21.1-3.0.0 RELEASE

NeoForged fork. Ported to NeoForge 21.1.228 / Minecraft 1.21.1. 1.20.1 worlds are not save-compatible.

Changes:
- Updated to NeoForge 21.1.228, Java 21, ModDevGradle.
- Bundles AlchemyLib and ChemLib via composite build (`includeBuild`).
- Recipe and tag JSONs migrated to the 1.21 pack format (`pack_format=34`, `forge:` → `c:`, ingredient/result schema updates).
- JEI updated to 19.21.1.312.

---

# Alchemistry 1.20.1-2.3.4 RELEASE

Changes:
- Merge PR #327 to fix issues with pushing items through Mekanism pipes into the combiner inventory. Better logic, thank you!
- Allow NBT when checking item stacks for the dissolver and combiner recipe serializers. For closed PR #321.

Have questions about the new version or just want to chat? Join the Discord: https://discord.gg/4swu3fy