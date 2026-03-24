<div align="center">

  <img src="logo.png" alt="Fiddle logo" width="21%" align="right">
  <h1>
    Fiddle
  </h1>
  <h3>
    A Paper fork that lets you
    <br>
    add real custom blocks and items
  </h3>

  [![Discord](https://img.shields.io/discord/1091830813240348732?color=5865F2&label=discord&style=for-the-badge)](https://discord.gg/EduvcVmKS7)

</div>

<table>
  <tr>
    <td>
      <img src="design/fire.png">
    </td>
    <td>
      <img src="design/trees.png">
    </td>
    <td>
      <img src="design/stone.png">
    </td>
  </tr>
  <tr>
    <td>
      <img src="design/newblocks.png">
    </td>
    <td>
      <img src="design/bookshelves.png">
    </td>
    <td>
      <img src="design/concrete.png">
    </td>
  </tr>
</table>

<div align="center">
  <i>
    Pictures from the version currently running live on a public server (see <b>Demo</b> below)
  </i>
</div>

## Introduction

Fiddle is a Paper server fork that lets plugins add new blocks and items into Minecraft.

Fiddle:
* adds new blocks and items natively
* works with existing Bukkit plugins
* works with vanilla clients<!--, both with/without a resource pack, and also offers a client-side mod with extra performance--><!--* modded blocks and items keep working after Minecraft updates-->

You are very welcome to help with implementation, testing, sharing knowledge or giving suggestions.

## Installation

Fiddle is a drop-in replacement for Paper.

There are no builds available at the moment, but you can build Fiddle yourself by cloning the project and doing:
* `./gradlew applyAllPatches`
* Create a runnable server jar with `./gradlew createMojmapPaperclipJar` (the jar file will be placed in `fiddle-server/build/libs`)

You can easily run a test server (which [includes some example blocks and items](https://github.com/FiddleMC/Fiddle/blob/master/test-plugin/src/main/java/org/fiddlemc/testplugin/TestPluginBootstrap.java)):
* `./gradlew applyAllPatches`
* `./gradlew runDevServer`
<!--
You can download the latest stable JAR from [releases](https://github.com/FiddleMC/Fiddle/releases) and the latest development JAR from [actions](https://github.com/FiddleMC/Fiddle/actions).

After running Fiddle once, you must open `fiddle.txt`, read the warning carefully, and set `modded=true`.

<div align="center">
  <table>
    <tr>
      <td valign="center">
        <h1>🔨</h1>
      </td>
      <td valign="center">
        Note: Fiddle is in active development.
        <br>
        Some of the information below may be outdated or incorrect.
        <br>
        Do <b>NOT</b> run Fiddle on a production server!
      </td>
    </tr>
  </table>
</div>
-->

## Adding custom blocks/items

See the [wiki](https://github.com/FiddleMC/Fiddle/wiki)!

<!--
New content to add to the game, like blocks and items, are loaded by Fiddle from packs, similar to resource and data packs.\
A pack is a `.zip` or `.rar` file. To install a pack, you can place it in the `fiddle_packs` folder in the server root.\
*Example location:* `fiddle_packs/WillowTrees.zip`

You can download packs made by others, or [create your own packs](https://github.com/FiddleMC/Fiddle/wiki/Making-packs) and share them.

Bukkit plugins can also add custom blocks and items.
-->

## Live demo

An experimental version of Fiddle is already running on a small community server.\
You can join *sucraft.org* (Minecraft 1.19.2) and do `/warp Demo` to see it in action yourself.

## Soon

The next goals of the project are:
* Improve resource pack API
* Unused block state claiming API
* Support block display entities
* Support falling block entities
* Provide more ways to serve the resource pack
* Send blocks and items to client mod

Please feel free to join the project as a developer and contribute toward these goals!

<!--
## Goals of the project

### Supports any player

* A client mod that supports the custom blocks and items directly
* A resource pack that allows vanilla clients to see the custom blocks and items
* Players do not have to accept the resource pack, and will be shown custom blocks and items as closely as possible

### Seamless updates

Mods adding custom blocks frequently have the problem that they break with a new Minecraft version, and then sometimes stop being updated.

The blocks and items added to a Fiddle server do not need to be updated, and always remain saved in the world and loadable, placeable and breakable as before.

### Works with plugins

Custom blocks and items work fully with Bukkit plugins out of the box.

## Architecture

<img src="design/architecture.svg" width="100%">

## Acknowledgements

This project has been made possible by:
* the generous donation from <a href="https://github.com/pontaoski">Janet&nbsp;Blackquill</a>
* the authors and maintainers of the Bukkit, [Spigot](https://www.spigotmc.org/) and [Paper](https://github.com/PaperMC/Paper) projects
* everyone on GitHub and the [Discord](https://discord.gg/EduvcVmKS7) server who helps test Fiddle and provide feedback and suggestions
-->
