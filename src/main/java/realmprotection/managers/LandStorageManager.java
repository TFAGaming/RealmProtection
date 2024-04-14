package realmprotection.managers;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import realmprotection.RealmProtection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class LandStorageManager {
    public static void storeItems(List<List<Object>> items, Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();

        if (items.isEmpty()) {
            data.set(new NamespacedKey(RealmProtection.getPlugin(RealmProtection.class), "playerlandstorage"),
                    PersistentDataType.STRING, "");
        } else {
            try {
                ByteArrayOutputStream io = new ByteArrayOutputStream();
                BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);

                for (List<Object> item : items) {
                    ItemStack itemStack = (ItemStack) item.get(0);
                    int slotIndex = (int) item.get(1);
                    os.writeInt(slotIndex);
                    os.writeObject(itemStack);
                }

                os.writeInt(-1);
                os.flush();

                byte[] rawData = io.toByteArray();
                String encodedData = Base64.getEncoder().encodeToString(rawData);

                data.set(new NamespacedKey(RealmProtection.getPlugin(RealmProtection.class), "playerlandstorage"),
                        PersistentDataType.STRING, encodedData);

                os.close();
            } catch (IOException exception) {
                System.out.println(exception);
            }
        }
    }

    public static List<List<Object>> getItems(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();

        List<List<Object>> items = new ArrayList<>();

        String encodedItems = data.get(
                new NamespacedKey(RealmProtection.getPlugin(RealmProtection.class), "playerlandstorage"),
                PersistentDataType.STRING);

        if (encodedItems != null && !encodedItems.isEmpty()) {
            byte[] rawData = Base64.getDecoder().decode(encodedItems);

            try {
                ByteArrayInputStream io = new ByteArrayInputStream(rawData);
                BukkitObjectInputStream in = new BukkitObjectInputStream(io);

                while (true) {
                    int slot = in.readInt(); // Read slot number
                    if (slot == -1)
                        break; // Exit loop if -1 is reached
                    ItemStack itemStack = (ItemStack) in.readObject(); // Read item
                    List<Object> itemEntry = new ArrayList<>();
                    itemEntry.add(itemStack);
                    itemEntry.add(slot);
                    items.add(itemEntry);
                }

                in.close();

            } catch (IOException | ClassNotFoundException exception) {
                System.out.println(exception);
            }
        }

        return items;
    }

}
