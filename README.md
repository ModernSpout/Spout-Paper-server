<div align="center">

  <img src="logo.png" alt="Spout logo" width="21%" align="right">
  <h1>
    Spout server extension<br>(Paper)
  </h1>
  <h3>
    Lets you add new blocks and items
    <br>
    that are automatically sent to connecting players
  </h3>

  [![Discord](https://img.shields.io/discord/1091830813240348732?color=5865F2&label=discord&style=for-the-badge)](https://discord.gg/EduvcVmKS7)

</div>

<table>
  <tr>
    <td>
      <img src="design/fire.png">
    </td>
    <td>
      <img src="design/orange.png">
    </td>
    <td>
      <img src="design/stone.png">
    </td>
  </tr>
  <tr>
    <td>
      <img src="design/lantern.png">
    </td>
    <td>
      <img src="design/bookshelves.png">
    </td>
    <td>
      <img src="design/concrete.png">
    </td>
  </tr>
</table>

## Introduction

This is a Paper extension that lets [plugins](#Making-a-Spout-Paper-plugin-that-adds-new-blocksitems) add new blocks and items into Minecraft.

* The blocks and items are added server-side with plugins
* Custom blocks and items are automatically sent to clients with the [Spout client mod](https://github.com/FiddleMC/Fiddle-mod) and installed when they join
* Also works with vanilla clients: with an (optional!) resource pack
* All Bukkit plugins are supported, even when interacting with custom blocks and items

## Installation

Spout is a drop-in replacement for Paper.

Download the latest JAR from [GitHub Actions](https://github.com/FiddleMC/Fiddle/actions), under **Artifacts**.

## Demo

You can easily run a test server (which [includes some example blocks and items](https://github.com/FiddleMC/Fiddle/blob/master/test-plugin/src/main/java/org/fiddlemc/testplugin/TestPluginBootstrap.java)):
* `git clone https://github.com/FiddleMC/Fiddle.git`
* `./gradlew applyAllPatches`
* `./gradlew runDevServer`
<!--
You can download the latest stable JAR from [releases](https://github.com/FiddleMC/Fiddle/releases) and the latest development JAR from [actions](https://github.com/FiddleMC/Fiddle/actions).
-->

## Making a Spout Paper plugin that adds new blocks/items

See the [wiki](https://github.com/FiddleMC/Fiddle/wiki)!

<!--
New content to add to the game, like blocks and items, are loaded by Fiddle from packs, similar to resource and data packs.\
A pack is a `.zip` or `.rar` file. To install a pack, you can place it in the `fiddle_packs` folder in the server root.\
*Example location:* `fiddle_packs/WillowTrees.zip`

You can download packs made by others, or [create your own packs](https://github.com/FiddleMC/Fiddle/wiki/Making-packs) and share them.

Bukkit plugins can also add custom blocks and items.
-->

## Next

The next goals of the project are:
* Improve resource pack API
* Unused block state claiming API
* Provide more ways to serve the resource pack

Further goals of the project are:
* Read server-side translations from resource pack
* Support custom block entities and entities
* Support using display entities to display custom blocks to vanilla clients

Please feel free to join the project as a developer and contribute toward these goals!

## Known issues

* Block display entities and falling block entities are not mapped correctly
* Stonecutter recipes do not display correctly in vanilla clients

<!--
## Acknowledgements

This project has been made possible by:
* the generous donation from <a href="https://github.com/pontaoski">Janet&nbsp;Blackquill</a>
* the authors and maintainers of the Bukkit, [Spigot](https://www.spigotmc.org/) and [Paper](https://github.com/PaperMC/Paper) projects
* everyone on GitHub and the [Discord](https://discord.gg/EduvcVmKS7) server who helps test Fiddle and provide feedback and suggestions
-->
