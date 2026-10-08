# Bundle Organizer

NeoForge QoL mod for Minecraft 26.1.2: free-space indicator, category filter and one-key sorting for vanilla Bundles.

See [DESCRIPTION.md](../../Users/mindo/Downloads/BundleOrganizer-1.0.0/BundleOrganizer/DESCRIPTION.md) for the feature list, [RELEASE.md](../../Users/mindo/Downloads/BundleOrganizer-1.0.0/BundleOrganizer/RELEASE.md) for release notes and [TEST.md](../../Users/mindo/Downloads/BundleOrganizer-1.0.0/BundleOrganizer/TEST.md) for the manual test plan.

## Project layout

| Package | Side | Purpose |
|---|---|---|
| `dev.mihail.bundleorganizer` | common | Mod entry point, constants |
| `...core` | pure Java | Categories, sorting, fill maths, summary (unit tested, no Minecraft) |
| `...common` | common | Bridge between vanilla data (`BundleContents`, tags) and `core`; server config |
| `...network` | common | `SortBundlePayload` (client to server, optional channel) and the server handler |
| `...client` | client only | Side panel, slot indicator, key bindings, client config, event handlers |

The client entry point is a separate `@Mod(dist = Dist.CLIENT)` class, so nothing from `client` is loaded on a dedicated server.

## Build

```
gradlew test
gradlew build
```

JDK 25 is required (see `../../Users/mindo/Downloads/BundleOrganizer-1.0.0/BundleOrganizer/build.gradle`). Output jar: `build/libs/bundleorganizer-1.0.0.jar`.

## License

MIT, see [LICENSE](LICENSE).
