# Minecraft-Hack-BaseClient - AcaiBerii's Fork

## Welcome!
The [original](https://github.com/OxideWaveLength/Minecraft-Hack-BaseClient) repository has way less updates and way less support/activity so I made my own fork.  
This was going to be a pull request into the main repository, but there was no activity at all there for ~2 weeks so I closed it.

Now it's time for my fork! ♥

------------

### Setup

#### What's needed
- Internet connection
- At least 4gb of ram
- Java JDK (1.8)
- Java JRE (1.8)
- An IDE (The setup is for Eclipse, but MCP can also be setup on [IntelliJ Idea](https://www.youtube.com/watch?v=CtMhtp6QQoY))

#### Video by OxideWaveLength
Now a [video](https://www.youtube.com/watch?v=LSKu_zhPKc8) is available, showing how to set up the client. - The video is not up to date and a step is missing. Until that is updated you can watch that video and then come back and do the last step (adding javaws to your build path)

#### Let's get started!
_Note: the instructions in italic apply ONLY to Eclipse, if you have a different IDE, [Search how to set the project up on Google](https://lmgtfy.com/?q=How+to+setup+Minecraft+Coders+Pack+in+%5BMY+IDE%5D)_

- Download [Minecraft Coders Pack 918 (1.8.8)](http://www.modcoderpack.com/files/mcp918.zip) _(or MCP)_
- Unzip the MCP into a folder and enter it
- Double click the "decompile" bat file inside of the folder and wait for the assets to be copied
- When the message "== Decompiling client using fernflower ==" pops up inside of the terminal you can close the window (there is no need to actually decompile the MCP)
- Download the project as ZIP
- Enter the "src" folder
- Delete the "minecraft" folder, if any is present
- Copy the folder "minecraft" from the project's ZIP into the "src" folder
- _Launch Eclipse into the "eclipse" folder (that is inside the folder you decompiled the MCP in) as workspace_
- Optionally delete the Server project (it is safe to do so)
- _Optionally enable the @formatting tags (Window -> Preferences -> Java -> Code Style -> Formatter -> Edit -> Expand "Off/On tags" -> Tick the "Enable Off/On tags" -> Change the Profile Name (or you will not be able to save) -> OK -> Apply and Close)_
- Change the JRE version from JRE1\_6 to JRE1\_8 _(Right click on "Client" -> Build Path -> Libraries -> Scroll to the bottom -> Double click "JRE System" -> Choose a JRE1\_8 -> Finish -> Apply and Close)_
- Add the javaws.jar to your project buildpath (the file is located here: "JAVA_PATH\jre1.8.0_VERSION\lib\javaws.jar", if you can't find it, the program should still run without it).

#### Let's build it!
Here are videos showing how to export your client in Eclipse:
- [Minecraft Launcher (full tutorial)](https://www.youtube.com/watch?v=nlZKdifpoUA)
- [MultiMC (requires JSON file)](https://www.youtube.com/watch?v=TjJz9Iisg-Y)
