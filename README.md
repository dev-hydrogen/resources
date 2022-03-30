# resources

Spigot plugin with [Unnamed Team's "creative" library](https://github.com/unnamed/creative) included pre-shaded for developers, as well as acts as a resource pack host directly on the server.

**Developers should use the setResourcePack method in ResourcePackHandler** to set the resource pack they want to serve, as this plugin will serve that resource pack instead of the one defined in server.properties.
(By default, ResourcePackHandler downloads the pack defined in server.properties and sets that as the active resource pack.)
