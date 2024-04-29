package realmprotection.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.gui.LandInfoGUI;
import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.Language;

public class InfoCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (sender instanceof Player) {
                        Player player = (Player) sender;
                        Chunk chunk = player.getLocation().getChunk();

                        String land_id;

                        boolean isclaimed = ChunksManager.isChunkClaimed(chunk);

                        if (args.length == 1) {
                                if (!isclaimed) {
                                        player.sendMessage(Language.getCommand("info.chunk_wilderness"));
                                        return true;
                                }

                                land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                        } else {
                                if (!LandsManager.landNameExist(args[1])) {
                                        player.sendMessage(Language.getCommand("info.land_name_not_found"));
                                        return true;
                                }

                                land_id = LandsManager.getLandDetailByLandName(args[1], "id");
                        }

                        LandInfoGUI.create(player, land_id);

                        return true;
                } else {
                        return false;
                }
        }
}
