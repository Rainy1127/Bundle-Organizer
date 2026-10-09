# Bundle Organizer

NeoForge QoL mod for Minecraft 26.1.2: free-space indicator, category filter, sorting, clickable item panel, unload and pack for vanilla Bundles.

See [DESCRIPTION.md](../../Users/mindo/Downloads/1/BundleOrganizer/DESCRIPTION.md) for the feature list.
## Project layout

| Package | Side | Purpose |
|---|---|---|
| `dev.mihail.bundleorganizer` | common | Mod entry point, constants |
| `...core` | pure Java | Categories, sorting, fill maths, summary, colours, animation, search, unload planning (unit tested, no Minecraft) |
| `...common` | common | Bridge between vanilla data (`BundleContents`, tags) and `core`; item moving (`BundleOps`); server config |
| `...network` | common | `SortBundlePayload` and `BundleActionPayload` (client to server, optional channel), server handlers with validation |
| `...client` | client only | Side panel (hover and pinned), slot indicator, key bindings, client config, event handlers |

The client entry point is a separate `@Mod(dist = Dist.CLIENT)` class, so nothing from `client` is loaded on a dedicated server.

## Build

```
gradlew test
gradlew build
```

JDK 25 is required (see `../../Users/mindo/Downloads/1/BundleOrganizer/build.gradle`). Output jar: `build/libs/bundleorganizer-1.1.0.jar`.

## License

MIT, see [LICENSE](../../Users/mindo/Downloads/1/BundleOrganizer/LICENSE).
