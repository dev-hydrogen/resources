# resources
### Allows plugins to compile a resource pack completely on the server, and serves any resource pack to players!

Spigot plugin with [Unnamed Team's "creative" library](https://github.com/unnamed/creative) included pre-shaded for developers, as well as acts as a resource pack host directly on the server.

Developers may add MetadataPart's and FileResource's to the ResourcePackHandler (``Resources.getResourcePackHandler()``) where a resource pack can be compiled entirely on the server from these parts, and then served out to players.
