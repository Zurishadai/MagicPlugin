package com.elmakers.mine.bukkit.action.builtin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.elmakers.mine.bukkit.action.BaseSpellAction;
import com.elmakers.mine.bukkit.api.action.CastContext;
import com.elmakers.mine.bukkit.api.magic.Mage;
import com.elmakers.mine.bukkit.api.magic.Messages;
import com.elmakers.mine.bukkit.api.spell.SpellResult;
import com.elmakers.mine.bukkit.utility.ConfigurationUtils;

public class BookAction extends BaseSpellAction {

    @Nonnull
    private String title = "";
    @Nonnull
    private String author = "";
    @Nullable
    private ConfigurationSection pages;

    private ItemStack createBook(CastContext context, Mage targetMage) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        List<String> pages = replaceContents(context, targetMage);

        meta.setTitle(title);
        meta.setAuthor(author);
        meta.setPages(pages);

        book.setItemMeta(meta);

        return book;
    }

    private List<String> replaceContents(CastContext context, Mage targetMage) {
        Map<String, String> replacements = new HashMap<>();

        replacements.put("@tn", targetMage.getName());
        replacements.put("@td", targetMage.getDisplayName());

        Set<String> attributes = context.getController().getAttributes();
        Set<String> currencies = context.getController().getCurrencyKeys();

        for (String attr : attributes) {
            replacements.put("$attribute_" + attr, String.valueOf(targetMage.getAttribute(attr)));
        }

        for (String currency : currencies) {
            replacements.put("$balance_" + currency, String.valueOf(targetMage.getCurrency(currency)));
        }

        List<String> newContents = new ArrayList<>();
        Set<String> pageKeys = pages.getKeys(false);
        for (String pageKey : pageKeys) {
            int pageNumber = 0;
            try {
                pageNumber = Integer.parseInt(pageKey) - 1;
            } catch (NumberFormatException ex) {
                context.getController().getLogger().warning("Invalid page number: " + pageKey);
                continue;
            }
            String pageText = "";
            List<String> lines = ConfigurationUtils.getStringList(pages, pageKey);
            if (lines == null) {
                pageText = pages.getString(pageKey);
            } else {
                pageText = StringUtils.join(lines, "\n");
            }

            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                pageText = pageText.replace(entry.getKey(), entry.getValue());
            }

            while (newContents.size() <= pageNumber) newContents.add("");
            newContents.set(pageNumber, ChatColor.translateAlternateColorCodes('&', pageText));
        }

        return newContents;
    }

    @Override
    public void prepare(CastContext context, ConfigurationSection parameters) {
        super.prepare(context, parameters);

        Messages messages = context.getController().getMessages();
        String titleParam = parameters.getString("title", "");
        String authorParam = parameters.getString("author", context.getMage().getName());

        title = messages.get(titleParam, ChatColor.translateAlternateColorCodes('&', titleParam));
        author = messages.get(authorParam, ChatColor.translateAlternateColorCodes('&', authorParam));
        pages = parameters.getConfigurationSection("pages");
    }

    @Override
    public SpellResult perform(CastContext context) {
        if (pages == null) {
            return SpellResult.FAIL;
        }
        Entity target = context.getTargetEntity();
        if (target == null) {
            return SpellResult.NO_TARGET;
        }
        if (!(target instanceof InventoryHolder)) {
            return SpellResult.NO_TARGET;
        }

        Mage targetMage = context.getController().getMage(target);
        ItemStack book = createBook(context, targetMage);

        targetMage.giveItem(book);

        return SpellResult.CAST;
    }

    @Override
    public boolean requiresTarget() {
        return true;
    }

    @Override
    public boolean requiresTargetEntity() {
        return true;
    }
}