package org.galaxystudios.hyperitems;




import org.galaxystudios.hyperitems.Listeners.BlockTrimmingLeatherListener;
import org.galaxystudios.hyperitems.Listeners.DamageDoneListener;
import org.galaxystudios.hyperitems.LoadPlugin.*;



import org.galaxystudios.hyperitems.MythicIntegration.MythicMobKilledListener;
import org.galaxystudios.hyperitems.Listeners.PlayerStatUpdateListener;

import org.mineacademy.fo.plugin.SimplePlugin;




public final class hyperitems extends SimplePlugin {

    @Override
    public void onPluginStart() {
        //Files
        // Only for development: ensures default config files are present after development switch to false
        saveResource("armors.yml",true);
        saveResource("weapons.yml",true);
        saveResource("items.yml", true);
        saveResource("drops.yml", true);


        //Loading classes
        UnifiedItemLoader.registerAll();
        LoadElementBeatsMap.load();


        //Listeners
        getServer().getPluginManager().registerEvents(new DamageDoneListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerStatUpdateListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockTrimmingLeatherListener(this), this);
        getServer().getPluginManager().registerEvents(new MythicMobKilledListener(this), this);

        //commands






    }

    @Override
    public void onPluginStop() {
        // Plugin shutdown logic
    }

    public void onPluginReload() {
        UnifiedItemLoader.registerAll();
        LoadElementBeatsMap.load();


    }
}
