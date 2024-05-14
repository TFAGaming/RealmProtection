package realmprotection.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandBansManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.Language;
import realmprotection.utils.ParticleSpawner;
import realmprotection.utils.StringUtils;

public class ChunksProtection implements Listener {
    // Block place
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();

        if (player != null && ChunksManager.isChunkClaimed(chunk)) {
            if (isPlayerAnOperator(player)) {
                return;
            }

            if (event.getBlock().getType() == Material.FIRE) {
                return;
            }

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (!player.getUniqueId().toString().equals(land_owner_uuid) && !LandMembersManager
                    .hasFlagPermission(new Integer(land_id), player.getUniqueId().toString(), "placeblocks")) {
                event.setCancelled(true);

                StringUtils.sendMessageToPlayerWithTimeout(player, "placeblocks", chunk);

                ParticleSpawner.spawnTemporarySmokeParticle(chunk.getWorld(), event.getBlock().getX() + 0.5,
                        event.getBlock().getY() + 1, event.getBlock().getZ() + 0.5, 0, 0, 0, 0, 0);
            }
        }
    }

    // Block break
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();

        if (player != null && ChunksManager.isChunkClaimed(chunk)) {
            if (isPlayerAnOperator(player)) {
                return;
            }

            if (_isCropBlock(event.getBlock())) {
                return;
            }

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (!player.getUniqueId().toString().equals(land_owner_uuid) && !LandMembersManager
                    .hasFlagPermission(new Integer(land_id), player.getUniqueId().toString(), "breakblocks")) {
                event.setCancelled(true);
                StringUtils.sendMessageToPlayerWithTimeout(player, "breakblocks", chunk);

                ParticleSpawner.spawnTemporarySmokeParticle(chunk.getWorld(), event.getBlock().getX() + 0.5,
                        event.getBlock().getY() + 1, event.getBlock().getZ() + 0.5, 0, 0, 0, 0, 0);
            }
        }
    }

    // Containers
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        if (holder instanceof Villager) {
            Player player = (Player) event.getPlayer();
            Villager villager = (Villager) holder;
            Chunk chunk = villager.getLocation().getChunk();

            if (ChunksManager.isChunkClaimed(chunk)) {
                if (isPlayerAnOperator(player)) {
                    return;
                }

                String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id),
                        "owner_uuid");

                if (!player.getUniqueId().toString().equals(land_owner_uuid)
                        && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                player.getUniqueId().toString(),
                                "tradewithvillagers")) {
                    event.setCancelled(true);
                    StringUtils.sendMessageToPlayerWithTimeout(player, "tradewithvillagers",
                            chunk);
                }
            }
        } else if (event.getInventory().getHolder() instanceof org.bukkit.entity.ChestBoat
                || event.getInventory().getHolder() instanceof org.bukkit.entity.ChestedHorse
                || event.getInventory().getHolder() instanceof org.bukkit.entity.minecart.StorageMinecart
                || event.getInventory().getHolder() instanceof org.bukkit.entity.minecart.HopperMinecart) {
            Player player = (Player) event.getPlayer();
            Chunk chunk = player.getLocation().getChunk();

            if (ChunksManager.isChunkClaimed(chunk)) {
                if (isPlayerAnOperator(player)) {
                    return;
                }

                String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id),
                        "owner_uuid");

                if (!player.getUniqueId().toString().equals(land_owner_uuid)
                        && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                player.getUniqueId().toString(),
                                "containers")) {
                    event.setCancelled(true);
                    StringUtils.sendMessageToPlayerWithTimeout(player, "containers",
                            chunk);
                }
            }
        }
    }

    // Liquid flow
    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        Chunk fromChunk = event.getBlock().getChunk();
        Chunk toChunk = event.getToBlock().getChunk();

        if (!fromChunk.equals(toChunk)) {
            if (ChunksManager.isChunkClaimed(toChunk) && ChunksManager.isChunkClaimed(fromChunk)) {
                event.setCancelled(false);
            } else if (ChunksManager.isChunkClaimed(toChunk)) {
                String land_id = ChunksManager.getChunkDetail(toChunk, "land_id");

                if (!LandsManager.getFlagValue(new Integer(land_id), "liquidflow")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    // Empty buckets
    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Chunk chunk = event.getBlockClicked().getRelative(event.getBlockFace()).getChunk();
        Player player = event.getPlayer();

        if (ChunksManager.isChunkClaimed(chunk)) {
            if (isPlayerAnOperator(player)) {
                return;
            }

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (!player.getUniqueId().toString().equals(land_owner_uuid)
                    && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                            player.getUniqueId().toString(),
                            "emptybuckets")) {
                event.setCancelled(true);
                StringUtils.sendMessageToPlayerWithTimeout(player, "emptybuckets", chunk);

                ParticleSpawner.spawnTemporarySmokeParticle(chunk.getWorld(), event.getBlock().getX() + 0.5,
                        event.getBlock().getY() + 1, event.getBlock().getZ() + 0.5, 0, 0, 0, 0, 0);
            }
        }
    }

    // Fill bucket
    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Chunk chunk = event.getBlockClicked().getRelative(event.getBlockFace()).getChunk();
        Player player = event.getPlayer();

        if (ChunksManager.isChunkClaimed(chunk)) {
            if (isPlayerAnOperator(player)) {
                return;
            }

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (!player.getUniqueId().toString().equals(land_owner_uuid) && !LandMembersManager
                    .hasFlagPermission(new Integer(land_id), player.getUniqueId().toString(), "fillbuckets")) {
                event.setCancelled(true);
                StringUtils.sendMessageToPlayerWithTimeout(player, "fillbuckets", chunk);

                ParticleSpawner.spawnTemporarySmokeParticle(chunk.getWorld(), event.getBlock().getX() + 0.5,
                        event.getBlock().getY() + 1, event.getBlock().getZ() + 0.5, 0, 0, 0, 0, 0);
            }
        }
    }

    // Crops breaking
    @EventHandler
    public void onBreakCrop(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (event.getAction() == Action.PHYSICAL) {
            if (clickedBlock != null && clickedBlock.getType() == Material.FARMLAND) {
                Chunk chunk = event.getClickedBlock().getLocation().getChunk();

                if (ChunksManager.isChunkClaimed(chunk)) {
                    if (isPlayerAnOperator(player)) {
                        return;
                    }

                    String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                    String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "harvestcrops")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "harvestcrops", chunk);

                        ParticleSpawner.spawnTemporarySmokeParticle(chunk.getWorld(),
                                event.getClickedBlock().getX() + 0.5,
                                event.getClickedBlock().getY() + 1, event.getClickedBlock().getZ() + 0.5, 0, 0, 0, 0,
                                0);
                    }
                }
            }
        } else if (clickedBlock != null && _isCropBlock(clickedBlock)) {
            Chunk chunk = event.getClickedBlock().getLocation().getChunk();

            if (ChunksManager.isChunkClaimed(chunk)) {
                if (isPlayerAnOperator(player)) {
                    return;
                }

                String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                if (!player.getUniqueId().toString().equals(land_owner_uuid)
                        && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                player.getUniqueId().toString(),
                                "harvestcrops")) {
                    event.setCancelled(true);
                    StringUtils.sendMessageToPlayerWithTimeout(player, "harvestcrops", chunk);

                    ParticleSpawner.spawnTemporarySmokeParticle(chunk.getWorld(), event.getClickedBlock().getX() + 0.5,
                            event.getClickedBlock().getY() + 1, event.getClickedBlock().getZ() + 0.5, 0, 0, 0, 0, 0);
                }
            }
        }
    }

    private boolean _isCropBlock(Block block) {
        Material type = block.getType();
        return type == Material.WHEAT || type == Material.CARROTS || type == Material.POTATOES
                || type == Material.BEETROOTS || type == Material.PITCHER_PLANT || type == Material.NETHER_WART
                || type == Material.KELP || type == Material.CACTUS || type == Material.SEA_PICKLE
                || type == Material.RED_MUSHROOM || type == Material.BROWN_MUSHROOM || type == Material.SWEET_BERRIES
                || type == Material.SWEET_BERRY_BUSH;
    }

    // Anti Frostwalker
    @EventHandler
    public void onEntityBlockForm(EntityBlockFormEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Chunk chunk = event.getBlock().getChunk();

            ItemStack boots = player.getEquipment().getBoots();

            if (boots != null && boots.getEnchantments().containsKey(Enchantment.FROST_WALKER)) {
                if (ChunksManager.isChunkClaimed(chunk)) {
                    if (isPlayerAnOperator(player)) {
                        return;
                    }

                    String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                    String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "frostwalker")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "frostwalker", chunk);

                        ParticleSpawner.spawnTemporarySmokeParticle(chunk.getWorld(), event.getBlock().getX() + 0.5,
                                event.getBlock().getY() + 1, event.getBlock().getZ() + 0.5, 0, 0, 0, 0, 0);
                    }
                }
            }
        }
    }

    // Explosions: TNT, Creeper, Fireball
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed || event.getEntity() instanceof Creeper
                || event.getEntity() instanceof Fireball || event.getEntity() instanceof EnderCrystal
                || event.getEntity().getType() == EntityType.TNT_MINECART) {
            Chunk explosionChunk = event.getLocation().getChunk();

            if (ChunksManager.isChunkClaimed(explosionChunk)) {
                String land_id = ChunksManager.getChunkDetail(explosionChunk, "land_id");

                if (!LandsManager.getFlagValue(new Integer(land_id), "tntblockdamage")) {
                    event.setCancelled(true);
                }
            } else {
                List<Block> allowedBlocks = new ArrayList<>();

                for (Block block : event.blockList()) {
                    Location blockLocation = block.getLocation();
                    Chunk blockChunk = blockLocation.getChunk();

                    if (!ChunksManager.isChunkClaimed(blockChunk)) {
                        allowedBlocks.add(block);
                    }
                }

                event.blockList().clear();
                event.blockList().addAll(allowedBlocks);
            }
        }
    }

    // Respawn anchors and Beds
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (event.getBlock().getType() == Material.AIR) {
            Chunk explosionChunk = event.getBlock().getLocation().getChunk();

            if (ChunksManager.isChunkClaimed(explosionChunk)) {
                String land_id = ChunksManager.getChunkDetail(explosionChunk, "land_id");

                if (!LandsManager.getFlagValue(new Integer(land_id), "respawnanchorblockdamage")) {
                    event.setCancelled(true);
                }
            } else {
                List<Block> allowedBlocks = new ArrayList<>();

                for (Block block : event.blockList()) {
                    Location blockLocation = block.getLocation();
                    Chunk blockChunk = blockLocation.getChunk();

                    if (!ChunksManager.isChunkClaimed(blockChunk)) {
                        allowedBlocks.add(block);
                    }
                }

                event.blockList().clear();
                event.blockList().addAll(allowedBlocks);
            }
        }
    }

    // Entities shear
    @EventHandler
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getEntity().getLocation().getChunk();

        if (ChunksManager.isChunkClaimed(chunk)) {
            if (isPlayerAnOperator(player)) {
                return;
            }

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (!player.getUniqueId().toString().equals(land_owner_uuid) && !LandMembersManager
                    .hasFlagPermission(new Integer(land_id), player.getUniqueId().toString(),
                            "shearentities")) {
                StringUtils.sendMessageToPlayerWithTimeout(player, "shearentities", chunk);
                event.setCancelled(true);
            }
        }
    }

    // Fire protection
    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (event.getNewState().getType() == Material.FIRE) {
            Chunk chunk = event.getBlock().getLocation().getChunk();

            if (ChunksManager.isChunkClaimed(chunk)) {
                String land_id = ChunksManager.getChunkDetail(chunk, "land_id");

                if (!LandsManager.getFlagValue(new Integer(land_id), "firespread")) {
                    event.setCancelled(true);
                }
            }
        } else {
            Chunk chunk = event.getBlock().getLocation().getChunk();

            if (ChunksManager.isChunkClaimed(chunk)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getLocation().getChunk();

        if (ChunksManager.isChunkClaimed(chunk)) {
            if (isPlayerAnOperator(player)) {
                return;
            }

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (player == null) {
                event.setCancelled(true);
                return;
            }

            if (!player.getUniqueId().toString().equals(land_owner_uuid) && !LandMembersManager
                    .hasFlagPermission(new Integer(land_id), player.getUniqueId().toString(), "createfire")) {
                event.setCancelled(true);
                StringUtils.sendMessageToPlayerWithTimeout(player, "createfire", chunk);

                ParticleSpawner.spawnTemporarySmokeParticle(chunk.getWorld(), event.getBlock().getX() + 0.5,
                        event.getBlock().getY() + 1, event.getBlock().getZ() + 0.5, 0, 0, 0, 0, 0);
            }
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        Chunk chunk = event.getBlock().getLocation().getChunk();

        if (ChunksManager.isChunkClaimed(chunk)) {
            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");

            if (!LandsManager.getFlagValue(new Integer(land_id), "firespread")) {
                event.setCancelled(true);
            }
        }
    }

    // Leaves decay
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        Chunk chunk = event.getBlock().getLocation().getChunk();

        if (ChunksManager.isChunkClaimed(chunk)) {
            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");

            if (!LandsManager.getFlagValue(new Integer(land_id), "leavesdecay")) {
                event.setCancelled(true);
            }
        }
    }

    // Plant growth
    @EventHandler
    public void onBlowGrow(BlockGrowEvent event) {
        Chunk chunk = event.getBlock().getLocation().getChunk();

        if (ChunksManager.isChunkClaimed(chunk)) {
            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");

            if (!LandsManager.getFlagValue(new Integer(land_id), "plantgrowth")) {
                event.setCancelled(true);
            }
        }
    }

    // Damage by entity to another entity
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        Chunk chunk = entity.getLocation().getChunk();

        if (ChunksManager.isChunkClaimed(chunk)) {
            if (damager instanceof Player && isPlayerAnOperator((Player) damager)) {
                return;
            }

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (entity.getUniqueId().toString().contains("Armor Stand")) {
                if (damager instanceof Player && !damager.getUniqueId().toString().equals(land_owner_uuid)
                        && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                damager.getUniqueId().toString(),
                                "breakblocks")) {
                    event.setCancelled(true);
                    StringUtils.sendMessageToPlayerWithTimeout((Player) damager, "breakblocks", chunk);

                    return;
                }
            } else if (entity instanceof Player && damager instanceof Player) {
                if (!LandMembersManager.hasFlagPermission(new Integer(land_id),
                        damager.getUniqueId().toString(),
                        "pvp")) {
                    event.setCancelled(true);
                    StringUtils.sendMessageToPlayerWithTimeout((Player) damager, "pvp", chunk);

                    return;
                }
            } else if (damager instanceof Player && (entity instanceof Monster || entity instanceof IronGolem)) {
                if (!damager.getUniqueId().toString().equals(land_owner_uuid)
                        && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                damager.getUniqueId().toString(),
                                "damagehostilemobs")) {
                    event.setCancelled(true);
                    StringUtils.sendMessageToPlayerWithTimeout((Player) damager, "damagehostilemobs",
                            chunk);

                    return;
                }
            } else if (damager instanceof Player && (entity instanceof Animals || entity instanceof Mob)) {
                if (!damager.getUniqueId().toString().equals(land_owner_uuid)
                        && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                damager.getUniqueId().toString(),
                                "damagepassivemobs")) {
                    event.setCancelled(true);
                    StringUtils.sendMessageToPlayerWithTimeout((Player) damager, "damagepassivemobs",
                            chunk);

                    return;
                }
            } else if (damager instanceof Creeper || damager instanceof TNTPrimed || damager instanceof Fireball
                    || damager instanceof EnderCrystal || damager.getType() == EntityType.TNT_MINECART) {
                if (!LandsManager.getFlagValue(new Integer(land_id), "tntblockdamage")) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

    }

    // Pistons
    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        Block piston = event.getBlock();
        @SuppressWarnings({ "rawtypes", "unchecked" })
        List<Block> affectedBlocks = new ArrayList(event.getBlocks());
        BlockFace direction = event.getDirection();
        if (!affectedBlocks.isEmpty()) {
            affectedBlocks.add(piston.getRelative(direction));
        }

        if (!this.canPistonMoveBlock(affectedBlocks, direction, piston.getLocation().getChunk(), false)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        Block piston = event.getBlock();
        @SuppressWarnings({ "rawtypes", "unchecked" })
        List<Block> affectedBlocks = new ArrayList(event.getBlocks());
        BlockFace direction = event.getDirection();
        if (event.isSticky() && !affectedBlocks.isEmpty()) {
            affectedBlocks.add(piston.getRelative(direction));
        }

        if (!this.canPistonMoveBlock(affectedBlocks, direction, piston.getLocation().getChunk(), true)) {
            event.setCancelled(true);
        }

    }

    private boolean canPistonMoveBlock(List<Block> blocks, BlockFace direction, Chunk pistonChunk,
            boolean retractOrNot) {
        @SuppressWarnings("rawtypes")
        Iterator var5;
        Block block;
        Chunk chunk;
        if (retractOrNot) {
            var5 = blocks.iterator();

            while (var5.hasNext()) {
                block = (Block) var5.next();
                chunk = block.getLocation().getChunk();

                if (!chunk.equals(pistonChunk) && ChunksManager.isChunkClaimed(chunk)) {
                    String pistonChunkOwnerUUID = ChunksManager.getOwnerUUIDByChunk(pistonChunk);
                    String chunkOwnerUUID = ChunksManager.getOwnerUUIDByChunk(chunk);

                    if (pistonChunkOwnerUUID != null && chunkOwnerUUID != null
                            && pistonChunkOwnerUUID.equals(chunkOwnerUUID)) {
                        return true;
                    }

                    String land_id = ChunksManager.getChunkDetail(chunk, "land_id");

                    if (land_id != null && !LandsManager.getFlagValue(new Integer(land_id), "pistonsfromwilderness")) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            var5 = blocks.iterator();

            while (var5.hasNext()) {
                block = (Block) var5.next();
                chunk = block.getRelative(direction).getLocation().getChunk();

                if (!chunk.equals(pistonChunk) && ChunksManager.isChunkClaimed(chunk)) {
                    String pistonChunkOwnerUUID = ChunksManager.getOwnerUUIDByChunk(pistonChunk);
                    String chunkOwnerUUID = ChunksManager.getOwnerUUIDByChunk(chunk);

                    if (pistonChunkOwnerUUID != null && chunkOwnerUUID != null
                            && pistonChunkOwnerUUID.equals(chunkOwnerUUID)) {
                        return true;
                    }

                    String land_id = ChunksManager.getChunkDetail(chunk, "land_id");

                    if (land_id != null && !LandsManager.getFlagValue(new Integer(land_id), "pistonsfromwilderness")) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    // Dispensers
    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        Block block = event.getBlock();
        BlockData blockdata = event.getBlock().getBlockData();
        Chunk targetChunk = block.getRelative(((Directional) blockdata).getFacing()).getLocation().getChunk();

        if (!block.getLocation().getChunk().equals(targetChunk)) {
            if (ChunksManager.isChunkClaimed(targetChunk)) {
                String blockChunkOwnerUUID = ChunksManager.getOwnerUUIDByChunk(block.getLocation().getChunk());
                String chunkOwnerUUID = ChunksManager.getOwnerUUIDByChunk(targetChunk);

                if (blockChunkOwnerUUID != null && chunkOwnerUUID != null
                        && blockChunkOwnerUUID.equals(chunkOwnerUUID)) {
                    return;
                }

                String land_id = ChunksManager.getChunkDetail(targetChunk, "land_id");

                if (land_id != null && !LandsManager.getFlagValue(new Integer(land_id), "dispensersfromwilderness")) {
                    event.setCancelled(true);
                }
            }

        }
    }

    // Player interaction
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Chunk chunk;

        if (block == null) {
            chunk = player.getLocation().getChunk();
        } else {
            chunk = block.getLocation().getChunk();
        }

        if (ChunksManager.isChunkClaimed(chunk)) {
            if (isPlayerAnOperator(player)) {
                return;
            }

            if (event.getItem() != null) {
                if (event.getItem().getType().name().contains("BOAT")
                        || event.getItem().getType().name().contains("ARMOR_STAND")
                        || event.getItem().getType().name().contains("MINECART")
                        || event.getItem().getType().name().contains("PAINTING")
                        || event.getItem().getType().name().contains("BONE_MEAL")) {
                    String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                    String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "placeblocks")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "placeblocks", chunk);

                        return;
                    }
                }
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Material material = event.getClickedBlock().getType();

                String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                boolean spawn_particle = false;

                if (material.name().contains("CHEST") || material.equals(Material.FURNACE)
                        || material.equals(Material.SMOKER) || material.equals(Material.BLAST_FURNACE)
                        || material.equals(Material.BREWING_STAND) || material.equals(Material.BARREL)
                        || material.equals(Material.SHULKER_BOX) || material.equals(Material.BEACON)
                        || material.equals(Material.DROPPER) || material.equals(Material.DISPENSER)
                        || material.equals(Material.CHISELED_BOOKSHELF)) {
                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "containers")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "containers", chunk);

                        spawn_particle = true;
                    }
                } else if (material.name().contains("ANVIL")) {
                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "useanvil")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "useanvil", chunk);

                        spawn_particle = true;
                    }
                } else if (material.name().contains("TRAPDOOR")) {
                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "trapdoors")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "trapdoors", chunk);

                        spawn_particle = true;
                    }
                } else if (material.name().contains("DOOR")) {
                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "doors")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "doors", chunk);

                        spawn_particle = true;
                    }
                } else if (material.name().contains("SIGN")) {
                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "editsigns")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "editsigns", chunk);

                        spawn_particle = true;
                    }
                } else if (material.name().contains("BUTTON")) {
                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "buttons")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "buttons", chunk);

                        spawn_particle = true;
                    }
                } else if (material.name().contains("FENCE_GATE")) {
                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "fencegates")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "fencegates", chunk);

                        spawn_particle = true;
                    }
                } else if (material.name().contains("POT")
                        || material.name().contains("CANDLE")
                        || material.equals(Material.CAKE)
                        || material.equals(Material.DAYLIGHT_DETECTOR)) {
                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "generalinteractions")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "generalinteractions", chunk);

                        spawn_particle = true;
                    }
                } else if (material.equals(Material.LEVER)) {
                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "levers")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "levers", chunk);

                        spawn_particle = true;
                    }
                } else if (material.equals(Material.REPEATER) || material.equals(Material.COMPARATOR)
                        || material.equals(Material.COMMAND_BLOCK) || material.equals(Material.COMMAND_BLOCK_MINECART)
                        || material.equals(Material.REDSTONE) || material.equals(Material.REDSTONE_WIRE)
                        || material.equals(Material.NOTE_BLOCK) || material.equals(Material.JUKEBOX)
                        || material.equals(Material.COMPOSTER)) {
                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "redstone")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "redstone", chunk);

                        spawn_particle = true;
                    }
                } else if (material.equals(Material.BELL)) {
                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "bells")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "bells", chunk);

                        spawn_particle = true;
                    }
                } else if (material.equals(Material.CAULDRON) || material.equals(Material.LAVA_CAULDRON)
                        || material.equals(Material.WATER_CAULDRON)) {
                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "usecauldron")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "usecauldron", chunk);

                        spawn_particle = true;
                    }
                }

                if (spawn_particle) {
                    ParticleSpawner.spawnTemporarySmokeParticle(chunk.getWorld(), event.getClickedBlock().getX() + 0.5,
                            event.getClickedBlock().getY() + 1, event.getClickedBlock().getZ() + 0.5, 0, 0, 0, 0, 0);
                }
            } else {
                String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                if (event.getAction() == Action.PHYSICAL) {
                    if (block != null && block.getType().name().contains("PRESSURE_PLATE")) {
                        if (!player.getUniqueId().toString().equals(land_owner_uuid)
                                && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                        player.getUniqueId().toString(),
                                        "pressureplates")) {
                            event.setCancelled(true);
                            StringUtils.sendMessageToPlayerWithTimeout(player, "pressureplates", chunk);

                            ParticleSpawner.spawnTemporarySmokeParticle(chunk.getWorld(),
                                    event.getClickedBlock().getX() + 0.5,
                                    event.getClickedBlock().getY() + 1, event.getClickedBlock().getZ() + 0.5, 0, 0, 0,
                                    0, 0);

                            return;
                        }
                    }

                    if (block != null) {
                        if (block.getType() == Material.TRIPWIRE) {
                            if (!player.getUniqueId().toString().equals(land_owner_uuid)
                                    && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                            player.getUniqueId().toString(),
                                            "tripwires")) {
                                event.setCancelled(true);
                                StringUtils.sendMessageToPlayerWithTimeout(player, "tripwires", chunk);

                                ParticleSpawner.spawnTemporarySmokeParticle(chunk.getWorld(),
                                        event.getClickedBlock().getX() + 0.5,
                                        event.getClickedBlock().getY() + 1, event.getClickedBlock().getZ() + 0.5, 0, 0,
                                        0,
                                        0, 0);

                                return;
                            }
                        }
                    }
                }
            }
        }

    };

    // Ender pearl thrown (and somehow, splash potions)
    @EventHandler
    public void onPearl(PlayerInteractEvent event) {
        Player player = (Player) event.getPlayer();
        Chunk chunk = player.getLocation().getChunk();

        if (event.getItem() != null) {
            if (event.getItem().getType().equals(Material.ENDER_PEARL)) {
                if (ChunksManager.isChunkClaimed(chunk)) {
                    if (isPlayerAnOperator(player)) {
                        return;
                    }

                    String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                    String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "throwenderpearls")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "throwenderpearls", chunk);

                        return;
                    }
                }
            } else if (event.getItem().getType().equals(Material.SPLASH_POTION)
                    || event.getItem().getType().equals(Material.LINGERING_POTION)) {
                if (ChunksManager.isChunkClaimed(chunk)) {
                    if (isPlayerAnOperator(player)) {
                        return;
                    }

                    String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                    String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "throwpotions")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "throwpotions", chunk);

                        return;
                    }
                }
            }
        }
    }

    // Armor stands
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ArmorStand) {
            Player player = event.getPlayer();
            Chunk chunk = event.getRightClicked().getLocation().getChunk();

            if (ChunksManager.isChunkClaimed(chunk)) {
                if (isPlayerAnOperator(player)) {
                    return;
                }

                String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                if (!player.getUniqueId().toString().equals(land_owner_uuid)
                        && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                player.getUniqueId().toString(),
                                "armorstands")) {
                    event.setCancelled(true);
                    StringUtils.sendMessageToPlayerWithTimeout(player, "armorstands", chunk);

                    return;
                }
            }

        }
    }

    // Projectile hit
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        ProjectileSource source = event.getEntity().getShooter();
        Chunk chunk = event.getEntity().getLocation().getChunk();
        Entity entityhit = event.getHitEntity();

        if (ChunksManager.isChunkClaimed(chunk)) {
            if (source instanceof Player && isPlayerAnOperator((Player) source)) {
                return;
            }

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (source instanceof Player) {
                if (entityhit instanceof Player) {
                    if (!LandMembersManager.hasFlagPermission(new Integer(land_id),
                            ((Player) source).getUniqueId().toString(),
                            "pvp")) {
                        event.setCancelled(true);
                        event.getEntity().remove();

                        StringUtils.sendMessageToPlayerWithTimeout((Player) source, "pvp", chunk);

                        return;
                    }
                } else if (entityhit instanceof Monster || entityhit instanceof IronGolem) {
                    if (!((Player) source).getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    ((Player) source).getUniqueId().toString(),
                                    "damagehostilemobs")) {
                        event.setCancelled(true);
                        event.getEntity().remove();

                        StringUtils.sendMessageToPlayerWithTimeout((Player) source, "damagehostilemobs",
                                chunk);

                        return;
                    }
                } else if (entityhit instanceof Animals || entityhit instanceof Mob) {
                    if (!((Player) source).getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    ((Player) source).getUniqueId().toString(),
                                    "damagepassivemobs")) {
                        event.setCancelled(true);
                        event.getEntity().remove();

                        StringUtils.sendMessageToPlayerWithTimeout((Player) source, "damagepassivemobs",
                                chunk);

                        return;
                    }
                }
            }
        }

    }

    // Item frames
    @EventHandler
    public void onPlayerInteractEntity2(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.ITEM_FRAME
                || event.getRightClicked().getType() == EntityType.GLOW_ITEM_FRAME) {
            Player player = event.getPlayer();
            Chunk chunk = event.getRightClicked().getLocation().getChunk();

            if (ChunksManager.isChunkClaimed(chunk)) {
                if (isPlayerAnOperator(player)) {
                    return;
                }

                String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                if (!player.getUniqueId().toString().equals(land_owner_uuid)
                        && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                player.getUniqueId().toString(),
                                "generalinteractions")) {
                    event.setCancelled(true);
                    StringUtils.sendMessageToPlayerWithTimeout(player, "generalinteractions", chunk);

                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPunchFrame(EntityDamageByEntityEvent event) {
        if (event.getEntityType() == EntityType.ITEM_FRAME || event.getEntityType() == EntityType.GLOW_ITEM_FRAME) {
            if (event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                Chunk chunk = event.getEntity().getLocation().getChunk();

                if (ChunksManager.isChunkClaimed(chunk)) {
                    if (isPlayerAnOperator(player)) {
                        return;
                    }

                    String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                    String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "itemframes")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "itemframes", chunk);

                        return;
                    }
                }
            }
        }
    }

    // Item frame and painting breaking
    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        Chunk chunk;
        Player player;

        if (event.getEntity().getType().name().contains("PAINTING") && event.getRemover() instanceof Player) {
            chunk = event.getEntity().getLocation().getChunk();

            if (ChunksManager.isChunkClaimed(chunk)) {
                player = (Player) event.getRemover();

                if (isPlayerAnOperator(player)) {
                    return;
                }

                String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                if (!player.getUniqueId().toString().equals(land_owner_uuid)
                        && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                player.getUniqueId().toString(),
                                "breakblocks")) {
                    event.setCancelled(true);
                    StringUtils.sendMessageToPlayerWithTimeout(player, "breakblocks", chunk);

                    return;
                }
            }
        }

        if (event.getEntity().getType().name().contains("ITEM_FRAME") && event.getRemover() instanceof Player) {
            chunk = event.getEntity().getLocation().getChunk();

            if (ChunksManager.isChunkClaimed(chunk)) {
                player = (Player) event.getRemover();

                if (isPlayerAnOperator(player)) {
                    return;
                }

                String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                if (!player.getUniqueId().toString().equals(land_owner_uuid)
                        && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                player.getUniqueId().toString(),
                                "breakblocks")) {
                    event.setCancelled(true);
                    StringUtils.sendMessageToPlayerWithTimeout(player, "breakblocks", chunk);

                    return;
                }
            }
        }
    }

    // Player drop item
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = (Player) event.getPlayer();
        Chunk chunk = player.getLocation().getChunk();

        if (ChunksManager.isChunkClaimed(chunk)) {
            if (isPlayerAnOperator(player)) {
                return;
            }

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (!player.getUniqueId().toString().equals(land_owner_uuid)
                    && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                            player.getUniqueId().toString(),
                            "pickupitems")) {
                event.setCancelled(true);
                StringUtils.sendMessageToPlayerWithTimeout(player, "pickupitems", chunk);

                return;
            }
        }
    }

    // Player pickup item
    @EventHandler
    public void onPlayerPickupItem(PlayerDropItemEvent event) {
        Player player = (Player) event.getPlayer();
        Chunk chunk = player.getLocation().getChunk();

        if (ChunksManager.isChunkClaimed(chunk)) {
            if (isPlayerAnOperator(player)) {
                return;
            }

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (!player.getUniqueId().toString().equals(land_owner_uuid)
                    && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                            player.getUniqueId().toString(),
                            "pickupitems")) {
                event.setCancelled(true);
                StringUtils.sendMessageToPlayerWithTimeout(player, "pickupitems", chunk);

                return;
            }
        }
    }

    // Creature spawn
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Chunk chunk = event.getLocation().getChunk();
        Entity entity = event.getEntity();

        if (ChunksManager.isChunkClaimed(chunk)) {
            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");

            if (entity instanceof Monster || entity instanceof IronGolem) {
                if (!LandsManager.getFlagValue(new Integer(land_id), "hostilemobsspawn")) {
                    event.setCancelled(true);
                    return;
                }
            } else if (entity instanceof Animals || entity instanceof Mob) {
                if (!LandsManager.getFlagValue(new Integer(land_id), "passivemobsspawn")) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    // Vehicles
    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        Vehicle vehicle = event.getVehicle();
        Chunk chunk = vehicle.getLocation().getChunk();
        Entity entity = event.getEntered();

        if (vehicle != null) {
            if (entity instanceof Player && ChunksManager.isChunkClaimed(chunk)) {
                if (isPlayerAnOperator((Player) entity)) {
                    return;
                }

                String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                if (!entity.getUniqueId().toString().equals(land_owner_uuid)
                        && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                entity.getUniqueId().toString(),
                                "usevehicles")) {
                    event.setCancelled(true);
                    StringUtils.sendMessageToPlayerWithTimeout((Player) entity, "usevehicles", chunk);

                    ParticleSpawner.spawnTemporarySmokeParticle(chunk.getWorld(), vehicle.getLocation().getX() + 0.5,
                        vehicle.getLocation().getY() + 1, vehicle.getLocation().getZ() + 0.5, 0, 0, 0, 0, 0);

                    return;
                }
            }
        }
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        Vehicle vehicle = event.getVehicle();
        Chunk chunk = vehicle.getLocation().getChunk();
        Entity entity = event.getAttacker();

        if (vehicle != null) {
            if (entity instanceof Player && ChunksManager.isChunkClaimed(chunk)) {
                if (isPlayerAnOperator((Player) entity)) {
                    return;
                }

                String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                if (!entity.getUniqueId().toString().equals(land_owner_uuid)
                        && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                entity.getUniqueId().toString(),
                                "breakblocks")) {
                    event.setCancelled(true);
                    StringUtils.sendMessageToPlayerWithTimeout((Player) entity, "breakblocks", chunk);

                    ParticleSpawner.spawnTemporarySmokeParticle(chunk.getWorld(), vehicle.getLocation().getX() + 0.5,
                        vehicle.getLocation().getY() + 1, vehicle.getLocation().getZ() + 0.5, 0, 0, 0, 0, 0);

                    return;
                }
            }
        }
    }

    // Leash event
    @EventHandler
    public void onPlayerLeashEntity(PlayerLeashEntityEvent event) {
        Player player = (Player) event.getPlayer();
        Entity entity = event.getEntity();
        Chunk chunk = entity.getLocation().getChunk();

        if (ChunksManager.isChunkClaimed(chunk)) {
            if (isPlayerAnOperator(player)) {
                return;
            }

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (!player.getUniqueId().toString().equals(land_owner_uuid)
                    && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                            player.getUniqueId().toString(),
                            "leashmobs")) {
                event.setCancelled(true);
                StringUtils.sendMessageToPlayerWithTimeout(player, "leashmobs", chunk);

                return;
            }
        }
    }

    @EventHandler
    public void onLeashEvent(PlayerLeashEntityEvent event) {
        Player player = event.getPlayer();
        Block block = event.getEntity().getLocation().getBlock();
        Chunk chunk = block.getLocation().getChunk();

        if (block.getType().name().contains("FENCE")) {
            if (ChunksManager.isChunkClaimed(chunk)) {
                if (isPlayerAnOperator(player)) {
                    return;
                }

                String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                if (!player.getUniqueId().toString().equals(land_owner_uuid)
                        && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                player.getUniqueId().toString(),
                                "leashmobs")) {
                    event.setCancelled(true);
                    StringUtils.sendMessageToPlayerWithTimeout(player, "leashmobs", chunk);

                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerUnleashEntity(PlayerUnleashEntityEvent event) {
        Player player = (Player) event.getPlayer();
        Entity entity = event.getEntity();
        Chunk chunk = entity.getLocation().getChunk();

        if (ChunksManager.isChunkClaimed(chunk)) {
            if (isPlayerAnOperator(player)) {
                return;
            }

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (!player.getUniqueId().toString().equals(land_owner_uuid)
                    && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                            player.getUniqueId().toString(),
                            "leashmobs")) {
                event.setCancelled(true);
                StringUtils.sendMessageToPlayerWithTimeout(player, "leashmobs", chunk);

                return;
            }
        }
    }

    // Lectern book taken
    @EventHandler
    public void onPlayerTakeLecternBook(PlayerTakeLecternBookEvent event) {
        Player player = event.getPlayer();
        Block block = event.getLectern().getLocation().getBlock();
        Chunk chunk = block.getLocation().getChunk();

        if (ChunksManager.isChunkClaimed(chunk)) {
            if (isPlayerAnOperator(player)) {
                return;
            }

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (!player.getUniqueId().toString().equals(land_owner_uuid)
                    && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                            player.getUniqueId().toString(),
                            "containers")) {
                event.setCancelled(true);
                StringUtils.sendMessageToPlayerWithTimeout(player, "containers", chunk);

                ParticleSpawner.spawnTemporarySmokeParticle(chunk.getWorld(), block.getX() + 0.5,
                        block.getY() + 1, block.getZ() + 0.5, 0, 0, 0, 0, 0);

                return;
            }
        }
    }

    // Dye mob event
    @EventHandler
    public void onClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        Chunk chunk = entity.getLocation().getChunk();

        ItemStack item = player.getInventory().getItemInMainHand();

        if (entity instanceof Wolf || entity instanceof Sheep || entity instanceof Cat) {
            if (item.getType().name().contains("DYE")) {
                if (ChunksManager.isChunkClaimed(chunk)) {
                    if (isPlayerAnOperator(player)) {
                        return;
                    }

                    String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                    String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "dyemobs")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "dyemobs", chunk);

                        return;
                    }
                }
            }
        }
    }

    // Rename mob event
    @EventHandler
    public void onClick2(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        Chunk chunk = entity.getLocation().getChunk();

        ItemStack item = player.getInventory().getItemInMainHand();

        if (entity instanceof Entity) {
            if (item.getType().equals(Material.NAME_TAG)) {
                if (ChunksManager.isChunkClaimed(chunk)) {
                    if (isPlayerAnOperator(player)) {
                        return;
                    }

                    String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
                    String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

                    if (!player.getUniqueId().toString().equals(land_owner_uuid)
                            && !LandMembersManager.hasFlagPermission(new Integer(land_id),
                                    player.getUniqueId().toString(),
                                    "renamemobs")) {
                        event.setCancelled(true);
                        StringUtils.sendMessageToPlayerWithTimeout(player, "renamemobs", chunk);

                        return;
                    }
                }
            }
        }
    }

    // Player greeting to land
    private static final Map<String, Boolean> player_is_in_claimed_chunk_cache = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = (Player) event.getPlayer();
        Chunk chunk = player.getLocation().getChunk();

        if (!player_is_in_claimed_chunk_cache.containsKey(player.getUniqueId().toString())) {
            player_is_in_claimed_chunk_cache.put(player.getUniqueId().toString(), false);
        }

        if (ChunksManager.isChunkClaimed(chunk)) {
            if (player_is_in_claimed_chunk_cache.get(player.getUniqueId().toString()))
                return;

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_name = LandsManager.getLandDetailById(new Integer(land_id), "land_name");

            if (LandBansManager.isPlayerBannedFromLand(new Integer(land_id), player.getUniqueId().toString())) {
                ChunksManager.findUnclaimedChunkPositionAndTeleportPlayer(player, new Integer(land_id));

                boolean isclaimed = true;

                while (isclaimed) {
                    Chunk newchunk = player.getLocation().getChunk();

                    if (ChunksManager.isChunkClaimed(newchunk)) {
                        ChunksManager.findUnclaimedChunkPositionAndTeleportPlayer(player, new Integer(land_id));
                    } else {
                        isclaimed = false;
                    }
                }

                String ban_reason = LandBansManager.getBanReason(new Integer(land_id), player.getUniqueId().toString());

                player.sendMessage(
                        ChatColorTranslator.translate(
                                ((String) Language.get("general.player_chunk_entry.claimed.__PLAYER_BANNED__"))
                                        .replace("%land%",
                                                land_name)
                                        .replace("%reason%", ban_reason)));

                return;
            }

            String owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            String action_bar = ((String) Language.get("general.player_chunk_entry.claimed.action_bar"))
                    .replace("%land%",
                            land_name)
                    .replace("%land_owner%", Bukkit.getOfflinePlayer(UUID.fromString(owner_uuid)).getName());

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(ChatColorTranslator.translate(action_bar)));

            player_is_in_claimed_chunk_cache.put(player.getUniqueId().toString(), true);
        } else {
            if (!player_is_in_claimed_chunk_cache.get(player.getUniqueId().toString()))
                return;

            String action_bar = ((String) Language.get("general.player_chunk_entry.unclaimed.action_bar"));

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(ChatColorTranslator.translate(action_bar)));

            player_is_in_claimed_chunk_cache.put(player.getUniqueId().toString(), false);

            if (player.getAllowFlight() && !isPlayerAnOperator(player)) {
                player.setAllowFlight(false);

                player.sendMessage(ChatColorTranslator.translate(
                        ((String) Language.get("general.player_chunk_entry.unclaimed.__FLY_MODE_DISABLED__"))));
            }
        }
    }

    // This will delete the player uuid from cache
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (player_is_in_claimed_chunk_cache.containsKey(player.getUniqueId().toString())) {
            player_is_in_claimed_chunk_cache.remove(player.getUniqueId().toString());
        }
    }

    // Just a function to check if player is operator or not
    public static boolean isPlayerAnOperator(Player player) {
        return player.hasPermission("realmprotection.lands.__operator__");
    }
}