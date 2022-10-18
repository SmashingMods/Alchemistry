# Alchemistry 1.18.2-2.1.0 RELEASE

This is the much awaited update. With any luck, this adds a ton of polish to the usability of the mod. But importantly, it should also fix some well known bugs!

The biggest change involves updates to the way GUIs look and feel. There is now a new Recipe Selector button that pops open a new gui so you can really easily select the recipe you want to use for the Combiner and Compactor. I also standardized the size, shape, and location of elements in the gui. This should make it easier for us to make changes to any machine in the future.

Bug Fixes:
- Fix recipes that had duplication glitches such as graphite to diamond.
- Make sure saved recipes are loading when the processing machine is loaded in the world.
- Make block entities copy their recipes rather than using values of the current recipe so that any changes made don't break the recipe for the rest of the game session.

Changes:
- Add recipe selector button and screen for the Combiner and Compactor. Use this screen to more easily select the recipe you want to make. The search functionality is improved over previous iterations as well. You can now use '@' to search for namespaced items.
- Remove locking feature from single input machines.
- Redo machine gui textures for consistency and refactor screens/menus to account for changes.
- Make list of recipes linked lists. That combined with their sortability should make them always display the same way and only need to be sorted once.
- Create LinkedList of widgets and init them per screen. Then render them all in order with a defined x/y placement. This allows each machine to display only the buttons it needs.

Have questions about the new version or just want to chat? Join the Discord: https://discord.gg/4swu3fy