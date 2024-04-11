package realmprotection.utils;

import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import realmprotection.RealmProtection;

public class VaultAPIEconomy {
    public static Economy vaultapieconomy = null;

    public static boolean setupVault() {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> registerer = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (registerer == null) {
            return false;
        }

        vaultapieconomy = registerer.getProvider();

        return vaultapieconomy != null;
    }

    public static Economy getEconomy() {
        return vaultapieconomy;
    }

    public static boolean isReady() {
        return vaultapieconomy != null ? true : false;
    }
}
