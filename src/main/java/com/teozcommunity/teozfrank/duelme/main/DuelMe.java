package com.teozcommunity.teozfrank.duelme.main;

import com.teozcommunity.teozfrank.MetricsLite;
import com.teozcommunity.teozfrank.duelme.commands.DuelAdminExecutor;
import com.teozcommunity.teozfrank.duelme.commands.DuelExecutor;
import com.teozcommunity.teozfrank.duelme.events.*;
import com.teozcommunity.teozfrank.duelme.util.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Original Author: teozfrank
 * Date: 19/07/13
 * Time: 21:53
 * -----------------------------
 * Removing this header is in breach of the license agreement,
 * please do not remove, move or edit it in any way.
 * -----------------------------
 */
public class DuelMe extends JavaPlugin {

    /**
     * duelmanager class
     */
    private DuelManager duelManager;

    /**
     * update checker class
     */
    private UpdateChecker updateChecker;

    /**
     * filemanager class
     */
    private FileManager fileManager;

    /**
     * player events class
     */
    private PlayerEvents playerEvents;


    /**
     * string to hold the plugin version
     */
    private static String version;


    @Override
    public void onEnable() {
      SendConsoleMessage.info("Enabling.");
      this.version = this.getDescription().getVersion();
      this.fileManager = new FileManager(this);
      this.setupYMLs();
      this.checkForUpdates();
      this.submitStats();
      this.setupDependencies();
      this.duelManager = new DuelManager(this);
      this.playerEvents = new PlayerEvents(this);
      getCommand("duel").setExecutor(new DuelExecutor(this));
      getCommand("dueladmin").setExecutor(new DuelAdminExecutor(this));
      SendConsoleMessage.info("Enabled!");
    }


    @Override
    public void onDisable() {
      SendConsoleMessage.info("Disabling.");
      this.fileManager.saveDuelArenas();
    }

    public void checkForUpdates() {
        if(this.getConfig().getBoolean("duelme.checkforupdates")){
            this.updateChecker = new UpdateChecker(this,60044);
        }
    }

    public void submitStats() {
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            SendConsoleMessage.severe("Could not submit the stats! :(");
        }
    }

    public void setupYMLs() {
        if (!(new File(getDataFolder(), "config.yml")).exists()) {
            SendConsoleMessage.info("saving default config.yml.");
            saveDefaultConfig();
        }
        if (!(new File(getDataFolder(), "locations.yml")).exists()) {
            SendConsoleMessage.info("saving default locations.yml.");
            this.fileManager.saveDefaultLocations();
        }
        if (!(new File(getDataFolder(), "duelarenas.yml")).exists()) {
            SendConsoleMessage.info("saving default duelarenas.yml.");
            this.fileManager.saveDefaultDuelArenas();
        }
    }

    /**
     * sets up the plugin main dependencies such as WorldEdit
     * disables the plugin if the required dependency is not present
     */
    private void setupDependencies() {
        if (this.getServer().getPluginManager().getPlugin("WorldEdit") != null) {
            SendConsoleMessage.info("WorldEdit found!");
        } else {
            SendConsoleMessage.warning("WorldEdit dependency not found, plugin disabled!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
    }

    public DuelManager getDuelManager(){
        return this.duelManager;
    }

    public FileManager getFileManager(){
        return this.fileManager;
    }

    public static String getVersion(){
        return version;
    }

}