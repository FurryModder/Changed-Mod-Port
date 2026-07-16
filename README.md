# Changed: Minecraft Mod — NeoForge 1.21.1 Port

This repository contains an **unofficial community port** of
[Changed: Minecraft Mod](https://github.com/LtxProgrammer/Changed-Minecraft-Mod)
to Minecraft 1.21.1 NeoForge.

It is not an official release by LtxProgrammer or DragonSnow. Please do not
report port-specific bugs to the upstream Changed project.

## Project status

The port is experimental and may still contain gameplay, rendering, or mod
compatibility differences from the upstream version.

- Minecraft: `1.21.1`
- NeoForge: `21.1.233` or newer
- Java: `21`
- Port version: `0.15.7-neoforge-1.21.1-port`

## Building

Clone the repository and run:

```shell
./gradlew build
```

On Windows:

```powershell
.\gradlew.bat build
```

The resulting mod JAR is written to `build-port/libs/`.

## Credits

- **LtxProgrammer and upstream contributors** — original Changed: Minecraft
  Mod code and assets.
- **FurryModder** — NeoForge 1.21.1 porting and compatibility work.
- Individual texture and model contributors are preserved in
  [`src/main/resources/assets/changed/textures/_CREDITS.txt`](src/main/resources/assets/changed/textures/_CREDITS.txt).
- The project uses the NeoForge MDK; its template notice is preserved in
  [`TEMPLATE_LICENSE.txt`](TEMPLATE_LICENSE.txt).

Changed is a game by DragonSnow. This fan project is not affiliated with or
endorsed by DragonSnow.

## License

The source is distributed under the MIT License. The original copyright and
the notice for the port modifications are preserved in [`LICENSE.txt`](LICENSE.txt).
