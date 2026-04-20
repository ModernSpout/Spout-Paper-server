<div align="center">

  <!--<img src="logo.png" alt="Spout logo" width="21%" align="right">-->
  <h1>
    Spout<br>server (Bukkit/Paper)
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
      <a href="design/fire.png"><img src="design/fire_small.png"></a>
    </td>
    <td>
      <a href="design/orange.png"><img src="design/orange_small.png"></a>
    </td>
    <td>
      <a href="design/stone.png"><img src="design/stone_small.png"></a>
    </td>
  </tr>
  <tr>
    <td>
      <a href="design/lantern.png"><img src="design/lantern_small.png"></a>
    </td>
    <td>
      <a href="design/bookshelves.png"><img src="design/bookshelves_small.png"></a>
    </td>
    <td>
      <a href="design/concrete.png"><img src="design/concrete_small.png"></a>
    </td>
  </tr>
</table>

## Introduction

Spout lets Spout plugins add new blocks and items server-side. When players join, the new blocks and items will be sent to their client and also added client-side.

This is the Spout server extension of Paper.

&nbsp;&nbsp;&nbsp;&nbsp;✓&nbsp;&nbsp;Support for Bukkit / Spigot / Paper plugins
<br>
&nbsp;&nbsp;&nbsp;&nbsp;✓&nbsp;&nbsp;Add Spout plugins just like Paper plugins
<br>
&nbsp;&nbsp;&nbsp;&nbsp;✓&nbsp;&nbsp;Works with the <a href="https://github.com/FiddleMC/Fiddle-mod">Spout client mod</a> and vanilla clients

## Installation

<div align="center">
  <table>
    <tr>
      <td valign="center">
        <h1>🔨</h1>
      </td>
      <td valign="center">
        Spout is currently in beta testing.
        <br>
        It works and is running on some servers already,
        <br>
        but there may be some bugs still, so you should proceed with care.
      </td>
    </tr>
  </table>
</div>

Download the latest JAR from [GitHub Actions](https://github.com/FiddleMC/Fiddle/actions), under **Artifacts**.

## Demo

You can easily run a test server (which [includes some example blocks and items](https://github.com/FiddleMC/Fiddle/blob/master/test-plugin/src/main/java/org/fiddlemc/testplugin/TestPluginBootstrap.java)):
* `git clone https://github.com/FiddleMC/Fiddle.git`
* `./gradlew applyAllPatches`
* `./gradlew runDevServer`
<!--
You can download the latest stable JAR from [releases](https://github.com/FiddleMC/Fiddle/releases) and the latest development JAR from [actions](https://github.com/FiddleMC/Fiddle/actions).
-->

## Making a Spout plugin that adds new blocks/items

Read <a href="https://github.com/FiddleMC/Fiddle/wiki">here</a>.

<!--
New content to add to the game, like blocks and items, are loaded by Fiddle from packs, similar to resource and data packs.\
A pack is a `.zip` or `.rar` file. To install a pack, you can place it in the `fiddle_packs` folder in the server root.\
*Example location:* `fiddle_packs/WillowTrees.zip`

You can download packs made by others, or [create your own packs](https://github.com/FiddleMC/Fiddle/wiki/Making-packs) and share them.

Bukkit plugins can also add custom blocks and items.
-->

## Next

The next goals of the project are:
* More ways to serve the resource pack
* More types of blocks and items

Afterward, goals of the project are:
* Custom block entities and entities
* Using display entities to display custom blocks to vanilla clients

Don't hesitate to suggest ideas, send in PRs (we will take a serious look at every PR, even if it is only a draft), or ask to join the project as a developer.

## Current known issues

* Custom block display entities and falling custom block entities are not displayed correctly
* Stonecutter recipes work, but do not display correctly

<!--
## Acknowledgements

This project has been made possible by:
* the generous donation from <a href="https://github.com/pontaoski">Janet&nbsp;Blackquill</a>
* the authors and maintainers of the Bukkit, [Spigot](https://www.spigotmc.org/) and [Paper](https://github.com/PaperMC/Paper) projects
* everyone on GitHub and the [Discord](https://discord.gg/EduvcVmKS7) server who helps test Fiddle and provide feedback and suggestions
-->
