package ng.precious.LimitedEffects;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
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


    private boolean fixEnchantments(ItemStack item) {
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
            if ((enchantment.getValue() < minimum) && minimum > 0) {
                item.addEnchantment(enchantment.getKey(), minimum);
                fixed = true;
            }

            if (enchantment.getValue() > limit) {
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

    private void fixEnchantments(ItemStack[] items) {
        for (ItemStack i : items) {
            if (i != null) {
                fixEnchantments(i);
            }
        }
    }

    private void fixEnchantments(Player player) {
        Inventory inventory = player.getInventory();
        ItemStack[] items = inventory.getContents();

        fixEnchantments(items);
    }


    @EventHandler
    public void onItemChange(PlayerItemHeldEvent e) {
        // PlayerSwapHandItemsEvent
        int itemIndex = e.getNewSlot();
        Player p = e.getPlayer();

        ItemStack item = p.getInventory().getItem(itemIndex);
        fixEnchantments(item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        fixEnchantments(e.getCursor());
        fixEnchantments(e.getCurrentItem());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        fixEnchantments(e.getCursor());
        fixEnchantments(e.getOldCursor());
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
            public void run() {
                fixEnchantments(e.getItem());
            }
        }, 5);
    } // Can be replaced with Lambda - IDEA

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        ItemStack item = e.getItem().getItemStack();

        if (e.getEntity() instanceof Player) { // item.hasMeta
            fixEnchantments(item);
        }

    }

    // i.getItemStack().getType().toString()

    @EventHandler
    public void onItemUse(BlockBreakEvent e) {
        if (fixEnchantments(e.getPlayer().getInventory().getItemInMainHand())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFishingRodUse(PlayerFishEvent e) {
        Player p = e.getPlayer();
        ItemStack rod = p.getInventory().getItemInMainHand();
        if (fixEnchantments(rod)) {
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
        if (fixEnchantments(attacker.getInventory().getItemInMainHand())) {
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
