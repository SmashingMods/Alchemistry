# Alchemistry 1.19.2-2.0.9 RELEASE

Being fairly new to mod development, there are a lot of ways to introduce bugs with minor mistakes. Trying to be agile, I let users find the bugs for me. This update fixes all of the known bugs and then some.

Changes:
- Use new maven to fetch chemlib.
- Refactor containers to remove container data and sync using update tag only. Container data wasn't syncing correctly if the value was over 16 bits (why?).
-  Refactor combiner recipe match inputs method to a much better functional method.
-  Make sure both lists are the same size when matching input in the combiner recipe.
- Fix fission and fusion controller block entities resetting progress when they didn't need to by setting the recipe to null if the input was empty.
-  Fix ProbabilitySet with unweighted probability to output air if total probability is below 100. This fixes a number of recipes like carrots which should only give beta carotene 20% of the time and air 80% of the time.
-  Add deceptively simple fix for setting the recipe for a block entity in the jei transfer handler.
- Fix (again) jei transfer handlers to make sure that max transfer in creative mode didn't try to add more than max stack size of items to slots.

Have questions about the new version or just want to chat? Join the Discord: https://discord.gg/4swu3fy