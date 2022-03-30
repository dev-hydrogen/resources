# resources

Spigot plugin with [Unnamed Team's "creative" library](https://github.com/unnamed/creative) included pre-shaded for developers, as well as acts as a resource pack host directly on the server.

**Developers should use the ResourcePack in ResourcePackHandler** as the resource pack they want to modify programmatically, as this plugin will serve that resource pack instead of the one defined in server.properties.  (By default, ResourcePackHandler downloads the pack defined in server.properties and sets that as the active resource pack.)
