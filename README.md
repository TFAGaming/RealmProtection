<p align="center">
    <a href="https://github.com/TFAGaming/RealmProtection/releases/latest">
        <img src="https://img.shields.io/github/downloads/TFAGaming/RealmProtection/total?label=Downloads">
        <img src="https://img.shields.io/github/v/tag/TFAGaming/RealmProtection?label=Version">
        <img src="https://img.shields.io/github/license/TFAGaming/RealmProtection?label=License">
        <img src="https://img.shields.io/github/repo-size/TFAGaming/RealmProtection?label=Size">
    </a>
</p>

<img width="150" height="150" align="left" style="float: left; margin: 0 10px 10px 0;" alt="RealmProtection" src="images/realmprotection-icon.png">

<h3><u>RealmProtection</u></h3>

RealmProtection is a Minecraft plugin that allows you to claim a 16x16 block segment from a world (a chunk) and protects it from griefers and thieves. It is customizable, roles-based for land members, and has 30+ flags for each role. This plugin uses SQLite as the main database and has a powerful cache system to avoid the database being locked every time.

<br>

**Supported Minecraft version:** 1.20 <br>
**Tested Minecraft versions:** 1.20

## Features
- Free, easy to use, and open-source.
- **30+** role flags and **10** land nature flags.
- Protects any claimed chunk from:
    - Lavacasts (lava and water).
    - Wilderness pistons.
    - Explosions:
        - TNT
        - Creeper
        - Respawn Anchor
        - End crystal
        - Bed (Nether and End)
    - Interactions (in general):
        - Item frames
        - Armor stands
        - Flower pots
        - Doors, trapdoors, and fence gates
        - And many more!
    - Building and breaking blocks.
    - Opening containers (chests, furnaces, droppers...).
    - Using/spawning vehicles.
    - Damaging hostile/passive mobs (by the player or by an explosion).
    - Interacting with redstone (repeaters, comparators...).
    - And many more!
- External plugins APIs used:
    - [VaultAPI](https://github.com/MilkBowl/VaultAPI) (used for Economy)
    - [LuckPerms](https://github.com/LuckPerms/LuckPerms) (used for permissions)

## How to install

Go to the [releases section](https://github.com/TFAGaming/RealmProtector/releases), scroll down to find the version you want to use, click on **Assets** and then click on the **.jar** file to download.

Once the download is finished, copy the **.jar** file, open the plugins folder from your Minecraft server directory, and paste it there. If your Minecraft server is running, you can use the command `/reload` to get the plugin ready for the server, but we recommend you stop the server and then start it again for a fresh startup.

## How does it work?
Once you try to claim a chunk, the plugin will ask you to enter a new land name with the command arguments. This will make the plugin create a new land with its specific name and ID, and protect the chunk that the player (you) is standing in. When new land is created, there will be two default roles to maintain the land; The first one is the **Visitor** role, which has nearly every flag disabled by default. The role is given by default to any non-trusted player in the land. The second role is the **Member**, which has nearly every flag enabled by default. These two roles are uneditable, which means they cannot be deleted or renamed because they're plugin-based roles. You can create other roles, but these two specific roles cannot be changed.

> [!NOTE]
> If you try to invite a player to the land, you have to give the player a role to join. Note that if the **Visitor** role has a flag enabled and the role that the trusted player got has the same flag but is disabled, the player won't have permission to use that flag in the land.

## Commands
The main command for the plugin is `/land`, aliases: `/lands`. It's not `/realmprotection` because the command name is long, while `/land` is short and easy to remember.

### Sub-commands:
- `/land claim {name}`: Creates a new land (if the player doesn't have one) and protects the chunk that the player is on it.
- `/land unclaim [confirm]`: Unclaims the chunk that the player is on it, deletes the land if there are no chunks to maintain it.
- `/land roles [create/delete/rename/flags]`: The main roles manager command:
    - `/land roles create [role name]`: Creates a new role with a unique ID for the land, has nearly every flag enabled.
    - `/land roles delete [role name]`: Deletes an existing role from the land.
    - `/land roles rename [old name] [new name]`: Renames a role's name.
    - `/land roles flags [role role] (flag) (true/false)`:
        - If the argument **flag** is undefined, it will show a GUI of enabled and disabled flags for the role.
        - If the argument **flag** is not undefined, it will update the role flag's value from the second optional argument **true/false**.
- `/land nature flags (flag) (true/false)`:
    - If the argument **flag** is undefined, it will show a GUI of enabled and disabled flags for the land's nature.
    - If the argument **flag** is not undefined, it will update the flag's value from the second optional argument **true/false**.
- `/land spawn [land name]`: Teleports a player to a land, only if the player has the flag **teleporttospawn** enabled or the player is the owner of the land.
- `/land setspawn`: Updates the spawn point of the land with the coordinates of the player's current position.
- `/land info (land name)`: Opens a GUI of land information; Number of chunks, ID, Owner, Members and their roles... etc.
- `/land view`: Spawns colored particles around the claimed chunks of the land, without the need of **F3 + G** Minecraft hotkey.
    - If the particles' colors are **Green**, it means you are the owner of the land and you have every permission to modify and update land's data.
    - If the particles' colors are **Yellow**, it means you are trusted in the land but you do not have the enough power to be like the land's owner.
    - If the particles' colors are **Red**, it means you are not trusted in the land and you are marked as a Visitor.
- `/land deposit [amount]`: Deposit an amount of money to the land's bank system.
- `/land withdraw [amount]`: Withdraw an amount of money from the land's bank system.
- `/land balance`: View the land's balance (bank).
- `/land trust [player name] [role name]`: Trust a player to your land, and let that player to be a member in your land with a specific role.
- `/land untrust [player name]`: Untrust a player from your land.
- `/land leave [land name]`: Leave a land that you are trusted in.
- `/land ban [player name] (reason)`: Ban a player to enter to your land.
- `/land unban [player name]`: Unban a player from your land.
- `/land banlist`: Returns a list of banned players from the land.
- `/land fly`: Toggle fly mode for a player, only allowed to fly in their own claimed chunks.
- `/land delete [confirm]`: Deletes the land; Deletes every role, removes every trusted member and unclaims every claimed chunk (for the land).

### Arguments:
- `{arg}`: An argument that could be required or optional.
- `(arg)`: An optional argument.
- `[arg]`: A required argument.

## Frequently Asked Questions (FAQs)

### Can I use this plugin with a Minecraft server version below the supported versions?

No, we do not recommend you to do this. It **may** work, but there is a higher chance of getting a lot of errors in the console.

### How do I configure the plugin commands permissions for player groups (using LuckPerms)?

Run the command on the server `/lp editor` and open the link generated. Once the editor is ready, open the dropdown **GROUPS** and select the group you want to edit. Follow the instructions from the image below:

<img src="./images/img1.png">

> [!WARNING]  
> The permission `realmprotection.lands.__operator__` is a dangerous permission that allows any player to bypass disabled flags in any land. Please use this permission for player groups with more trust on the server like Administrator or Owner.

Once you finish configuring permissions, click on the **Save** button. If there is a popup with a command to use, copy the command and use it.

### What's the difference between role flag and nature flag?

A role flag is a permission that is allowed/disallowed for a specific land role. A nature flag is an allowed/disallowed feature or protection for a land, that is undetectable when a player performs it (examples: Wilderness pistons) or by nature (examples: Mob spawners).

### Why must every player claim one chunk away from any land?

To avoid any neighbor claiming right next to you and some other reasons to tell. Note that if a player has the permission `realmprotection.lands.__operator__`, they can claim chunks next to other claimed chunks.

## Contributing
Feel free to fork the repository and submit a new pull request if you wish to contribute to this project.

Before you submit a pull request, ensure you tested it and have no issues. Also, keep the same coding style, which means don't use many unnecessary spaces or tabs.

## License
The [**Apache-2.0**](./LICENSE) license.