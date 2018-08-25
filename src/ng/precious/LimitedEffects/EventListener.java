package ng.precious.LimitedEffects;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class EventListener implements org.bukkit.event.Listener {
    private Main plugin;

    EventListener(Main plugin) {
        this.plugin = plugin;
    }


    private boolean fixEnchantments(ItemStack item, Player player) {
        if (player.hasPermission("limitedeffects.bypass.enchants.*")) {
            return false;
        }
        FileConfiguration config = plugin.getConfig();
        if (item == null) {
            return false;
        }
        Map<Enchantment, Integer> enchantments = item.getEnchantments();
        if (enchantments.isEmpty()) {
            return false;
        }

        boolean fixed = false;

        int limit = config.getInt("limit");
        int minimum = config.getInt("minimum");

        // TODO test
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            if (((enchantment.getValue() < minimum) && minimum > 0) &&
            !player.hasPermission("limitedeffects.bypass.enchants.minimum")) {
                item.addEnchantment(enchantment.getKey(), minimum);
                fixed = true;
            }

            if ((enchantment.getValue() > limit) &&
                    !player.hasPermission("limitedeffects.bypass.enchants.limit")) {
                if (limit <= 0) {
                    item.removeEnchantment(enchantment.getKey());
                    fixed = true;
                } else {
                    item.addEnchantment(enchantment.getKey(), limit);
                    fixed = true;
                }

            }
        }
        return fixed;
    }

    private void fixEnchantments(ItemStack[] items, Player player) {
        for (ItemStack i : items) {
            if (i != null) {
                fixEnchantments(i, player);
            }
        }
    }

    private void fixEnchantments(Player player) {
        Inventory inventory = player.getInventory();
        ItemStack[] items = inventory.getContents();

        fixEnchantments(items, player);
    }


    @EventHandler
    public void onItemChange(PlayerItemHeldEvent e) {
        // PlayerSwapHandItemsEvent
        int itemIndex = e.getNewSlot();
        Player p = e.getPlayer();

        ItemStack item = p.getInventory().getItem(itemIndex);
        fixEnchantments(item, p);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        fixEnchantments(e.getCursor(), p);
        fixEnchantments(e.getCurrentItem(), p);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        Player p = (Player) e.getWhoClicked();
        fixEnchantments(e.getCursor(), p);
        fixEnchantments(e.getOldCursor(), p);
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent e) {

        Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
            public void run() {
                Player p = e.getEnchanter();
                fixEnchantments(e.getItem(), p);
            }
        }, 5);
    } // Can be replaced with Lambda - IDEA

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        ItemStack item = e.getItem().getItemStack();
        LivingEntity ent = e.getEntity();
        if (ent instanceof Player) { // item.hasMeta
            fixEnchantments(item, (Player) ent);
        }

    }

    // i.getItemStack().getType().toString()

    @EventHandler
    public void onItemUse(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (fixEnchantments(p.getInventory().getItemInMainHand(), p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFishingRodUse(PlayerFishEvent e) {
        Player p = e.getPlayer();
        ItemStack rod = p.getInventory().getItemInMainHand();
        if (fixEnchantments(rod, p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemUse(EntityDamageByEntityEvent e) {
        Player attacker;
        if (e.getDamager() instanceof Player) {
            attacker = (Player) e.getDamager();
        } else {
            return;
        }
        if (fixEnchantments(attacker.getInventory().getItemInMainHand(), attacker)) {
            e.setCancelled(true);
        }
    }

    @EventHandler // Will cause lag?
    public void onInventoryEvent(InventoryCloseEvent e) {
        fixEnchantments((Player) e.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        fixEnchantments(e.getPlayer());
    }

}
