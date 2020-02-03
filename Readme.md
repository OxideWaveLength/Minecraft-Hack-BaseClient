# Minecraft Hacked Client Base Client 1.8.8 (with optifine)

### What's needed

- Internet connection
- At least 4gb of ram
- Java JDK (1.8)
- Java JRE (1.8)
- An IDE (The setup is for Eclipse, but MCP can also be setup on [IntelliJ Idea](https://lmgtfy.com/?q=How+to+setup+Minecraft+Coders+Pack+in+IntelliJ+Idea))

------------

### Setup

_Note: the instructions in italics apply ONLY to Eclipse, if you have a different IDE, [search how to set the project up on Google](https://lmgtfy.com/?q=How+to+setup+Minecraft+Coders+Pack+in+%5BMY+IDE%5D)_

- Decompile the [Minecraft Coders Pack 918 (1.8.8)](http://www.modcoderpack.com/files/mcp918.zip) _(or MCP)_ in a folder (it might take a few minutes)
- Download the project as ZIP
- Enter the folder where you decompiled the MCP in
- Enter the "src" folder
- Delete the "minecraft" folder
- Copy the ZIP's "minecraft" folder into the "src" folder
- _Launch Eclipse into the "eclipse" folder (that is inside the folder you decompiled the MCP in) as workspace_
- Optionally delete the Server project (it is safe to do so)
- _Optionally enable the @formatting tags (Window -> Preferences -> Java -> Code Style -> Formatter -> Edit -> Expand "Off/On tags" -> Tick the "Enable Off/On tags" -> Change the Profile Name (or you will not be able to save) -> OK -> Apply and Close)_
- Change the JRE version from JRE1\_6 to JRE1\_8 _(Right click on "Client" -> Build Path -> Libraries -> Scroll to the bottom -> Double click "JRE System" -> Choose a JRE1\_8 -> Finish -> Apply and Close)_

------------

### Features

- CommandManager (with four commands, "irc", "set", "help" and bind (added in the commit #41, 0be34e4))
- ModuleManager (with module settings, one example module (the fly/flight module) and the "AdvancedTabGui" module)
- [EventManager](https://github.com/OxideWaveLength/Minecraft-Hack-BaseClient/wiki/EventManager) (not all of the event handlers are working or cancellable, some marked as cancellable are not _yet_)
- FileManager
- ConfigManager
- [TabGUI](https://github.com/OxideWaveLength/Minecraft-Hack-BaseClient/wiki/Tab-GUI)
- [Module ArrayList](https://github.com/OxideWaveLength/Minecraft-Hack-BaseClient/wiki/Modules-ArrayList-(or-%22ToggledModules%22))
- Hotbar overlay
- Alt Manager (By Russian412)
- NahrFont (Font Manager) ported to 1.8.8 (By Russian412)
- IRC Client

------------

### Coming Soon / TODO

- ~~Better hotbar overlay~~ (Added in commit #44 / 7634127)
- ~~Better module arraylist~~ (Added in commit #29 / 070586b!)
- ~~Better font manager~~ (Not needed as of now)
- ~~Help command~~ (Done)
- ~~Better FileManager / ConfigManager~~ (Not needed as of now)
- Enhance some code
- Multi Versioning! Exactly, joining 1.12 servers in 1.8.8...

------------

### Most Important TODOs

- Create a wiki for every part of the client
- ~~Finish all of the event handlers~~ (Done! Added in the commit #38 / a2c72e1)
- Finish the TabGui, so that the module settings can be changed from there (they can be seen already, but not changed)

------------

##### Any help is highly appreciated, I cannot put all of my time into this project, but expect updates to be coming pretty soon
