# Alchemistry 1.20.1-2.3.1 RELEASE

This update fixes an issue caused by **Rubidium**. The mod does something really stupid and breaks all custom VertexConsumers, so I had to do a lot of refactoring to render semi-transparent fake items to the screen.

I also made some other minor changes to the side mode config stuff.

Changes:
- Move IOConfiguration button (SideModeButton) to AlchemyLib.
- Refactor SideModeScreen. It no longer pauses and allows for clicking parent buttons.
- Refactor RecipeSelectorButton such that it won't open the screen if the side mode screen is already open.
- Add SideConfig state boolean to ProcessingBlockEntity to allow for better user experience opening and closing side config screens.

Have questions about the new version or just want to chat? Join the Discord: https://discord.gg/4swu3fy