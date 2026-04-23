<div align="center">

  <!--<img src="logo.png" alt="Spout logo" width="21%" align="right">-->
  <h1>
    Spout<br>server (Paper/Bukkit)
  </h1>
  <h3>
    Lets you add new blocks and items
    <br>
    that are automatically sent to connecting players
  </h3>

[![Discord](https://img.shields.io/discord/1091830813240348732?color=5865F2&label=discord&style=for-the-badge)](https://discord.gg/EduvcVmKS7) [![1.21.11](https://img.shields.io/badge/Latest_version-1.21.11-4fa31a?style=for-the-badge)](https://github.com/ModernSpout/Spout-Paper-server/actions/workflows/build-server.yml)

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

Spout lets Spout plugins add new blocks and items server-side.
When players join, the new blocks and items will be sent to their client and also added client-side.

This is the Spout server extension of Paper.

&nbsp;&nbsp;&nbsp;&nbsp;✓&nbsp;&nbsp;Support for Bukkit / Spigot / Paper plugins
<br>
&nbsp;&nbsp;&nbsp;&nbsp;✓&nbsp;&nbsp;Add Spout plugins just like Paper plugins
<br>
&nbsp;&nbsp;&nbsp;&nbsp;✓&nbsp;&nbsp;Works with the
<a href="https://github.com/ModernSpout/Spout-Fabric-client">Spout client mod</a> and vanilla clients

## Installation

Download the latest JAR from [GitHub Actions](https://github.com/ModernSpout/Spout-Paper-server/actions/workflows/build-server.yml),
under **Artifacts**.
<!--
You can download the latest stable JAR from [releases](https://github.com/ModernSpout/Spout-Paper-server/releases)
and the latest development JAR from [actions](https://github.com/ModernSpout/Spout-Paper-server/actions/workflows/build-server.yml).
-->

It is a drop-in replacement for the Paper JAR file, so you can run it the same:

```sh
java -jar spout-paperclip-1.21.11-R0.1-SNAPSHOT-mojmap.jar
```

You can place Spout/Paper/Bukkit plugins in the `plugins` folder.

<div align="center">
  <table>
    <tr>
      <td valign="center">
        <h1>🔨</h1>
      </td>
      <td valign="center">
        Spout provides some non-trivial features.
        <br>
        It has been tested, but please report any issues you encounter.
        <br>
        Keep backups, and always proceed with care.
      </td>
    </tr>
  </table>
</div>

## Demo

You can easily run a test server (
which [includes some example blocks and items](test-plugin/src/main/java/spout/server/paper/testplugin/data/PluginBlockTypes.java)):

* `git clone https://github.com/ModernSpout/Spout-Paper-server.git`
* `./gradlew applyAllPatches`
* `./gradlew runDevServer`

## Making a Spout plugin that adds new blocks/items

Read <a href="https://github.com/ModernSpout/Spout-Paper-server/wiki">here</a>.

## Next

The next goals of the project are:

* More ways to serve the resource pack
* More types of blocks and items

Afterward, goals of the project are:

* Custom block entities and entities
* Using display entities to display custom blocks to vanilla clients

Don't hesitate to suggest ideas, send in PRs (we will take a serious look at every PR, even if it is only a draft),
or ask to join the project as a developer.

## Current known issues

* Custom block display entities and falling custom block entities are not displayed correctly
* Stonecutter recipes work, but do not display correctly

<!--
## Acknowledgements

This project has been made possible by:
* the contributions of [Alvinn8](https://github.com/Alvinn8/) and [SoSeDiK](https://github.com/SoSeDiK)
* the generous donation from <a href="https://github.com/pontaoski">Janet&nbsp;Blackquill</a>
* the authors and maintainers of the Bukkit, [Spigot](https://www.spigotmc.org/) and [Paper](https://github.com/PaperMC/Paper) projects
* everyone on GitHub and the [Discord](https://discord.gg/EduvcVmKS7) server who helps test Spout and provide feedback and suggestions
-->
