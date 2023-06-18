# Please use [creative central](https://modrinth.com/plugin/central) instead!

# resources
### Allows plugins to compile a resource pack completely on the server, and serves any resource pack to players!

Spigot and Minestom cross-compatible plugin with [Unnamed Team's "creative" library](https://github.com/unnamed/creative) included pre-shaded for developers, as well as acts as a resource pack host directly on the server.

Developers may add MetadataPart's and FileResource's to the ResourcePackHandler (``Resources.getResourcePackHandler()``) where a resource pack can be compiled entirely on the server from these parts, and then served out to players.

Check out [Nightclub](https://github.com/dev-hydrogen/Nightclub) for an example on how im using this api!
![code](https://user-images.githubusercontent.com/96733109/167196857-b601bec0-a109-4fbd-b021-8311fcf78a5b.png)
![explorer_Xyk3iS1ajz](https://user-images.githubusercontent.com/96733109/167197232-dc67a17a-98a5-455c-9441-7a7ed6b6ba6b.gif)

Available on JitPack: https://jitpack.io/#dev-hydrogen/resources/1.1
```
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
<dependency>
	<groupId>com.github.dev-hydrogen</groupId>
	<artifactId>resources</artifactId>
	<version>1.1</version>
</dependency>
```
