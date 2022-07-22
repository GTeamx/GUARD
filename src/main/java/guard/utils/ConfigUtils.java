package guard.utils;

import guard.Guard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class ConfigUtils {

    Guard guard;
    FileConfiguration config;
    File checksFile = null;
    FileConfiguration checks = null;

    public ConfigUtils(Guard guard) {
        this.guard = guard;
        this.config = guard.getConfig();
        saveDefaults("config");
        saveDefaults("checks");
    }


    public void reloadConfigs() {
        guard.reloadConfig();
        if(checksFile == null) {
            checksFile = new File(guard.getDataFolder(), "checks.yml");
        }
        checks = YamlConfiguration.loadConfiguration(checksFile);

        InputStream checksfilestream = guard.getResource("checks.yml");
        if(checksfilestream != null) {
            YamlConfiguration dconfig = YamlConfiguration.loadConfiguration(new InputStreamReader(checksfilestream));
            checks.setDefaults(dconfig);
        }
    }


    public boolean getBooleanFromConfig(String configName, String value, boolean defaultValue) {
        try {
            if(configName.equalsIgnoreCase("config")) {
                config.get(value);
            }
            if(configName.equalsIgnoreCase("checks")) {
                checks.get(value);
            }
        }catch (NullPointerException e) {
            return defaultValue;
        }
        if(configName.equalsIgnoreCase("config")) {
            if (config == null) {
                reloadConfigs();
            }
            return config.getBoolean(value);

        }
        if(configName.equalsIgnoreCase("checks")) {
            if(checks == null) {
                reloadConfigs();
            }
           return checks.getBoolean(value);
        }
        return defaultValue;
    }
    public String getStringFromConfig(String configName, String value, String defaultValue) {
        try {
            if(configName.equalsIgnoreCase("config")) {
                config.get(value);
            }
            if(configName.equalsIgnoreCase("checks")) {
                checks.get(value);
            }
        }catch (NullPointerException e) {
            return defaultValue;
        }
        if(configName.equalsIgnoreCase("config")) {
            if (config == null) {
                guard.reloadConfig();
            }
            return config.getString(value);

        }
        if(configName.equalsIgnoreCase("checks")) {
            if(checks == null) {
                reloadConfigs();
            }
            return checks.getString(value);
        }
        return defaultValue;
    }

    public String getConvertedStringFromConfig(String configName, Player player, String value, String defaultValue) {
        try {
            if(configName.equalsIgnoreCase("config")) {
                config.get(value);
            }
            if(configName.equalsIgnoreCase("checks")) {
                checks.get(value);
            }
        }catch (NullPointerException e) {
            String s = defaultValue;
            String newStringConverted = "";
            if(s.contains("%player%")) {
                newStringConverted = s.replace("%player%", player.getName());
            }
            return newStringConverted;
        }
        if(configName.equalsIgnoreCase("config")) {
            if (config == null) {
                guard.reloadConfig();
            }

            String s = config.getString(value);
            String s2 = config.getString("prefix");
            String newStringConverted = "";
            //s2.replace("\"", "");
            if(s.contains("%GUARD PREFIX%")) {
                newStringConverted = s.replace("%GUARD PREFIX%", s2);
            }
            if(s.contains("%player%")) {
                newStringConverted = s.replace("%player%", player.getName());
            }
            // s.replace("\"", "");
            return newStringConverted;
            //}

        } else
        if(configName.equalsIgnoreCase("checks")) {
            if(checks == null) {
                reloadConfigs();
            }
            if (config == null) {
                guard.reloadConfig();
            }

            String s = checks.getString(value);
            //if(s != null) {
                String s2 = config.getString("prefix");
                String newStringConverted = "";
                //s2.replace("\"", "");
            if(s.contains("%GUARD PREFIX%")) {
                newStringConverted = s.replace("%GUARD PREFIX%", s2);
            }
            if(s.contains("%player%")) {
                newStringConverted = s.replace("%player%", player.getName());
            }
               // s.replace("\"", "");
                return newStringConverted;
            //}
        } else {
            String s = defaultValue;
            String newStringConverted = "";
            if(s.contains("%player%")) {
                newStringConverted = s.replace("%player%", player.getName());
            }
            return newStringConverted;
        }
    }
    public int getIntFromConfig(String configName, String value, int defaultValue) {
        try {
            if(configName.equalsIgnoreCase("config")) {
                config.get(value);
            }
            if(configName.equalsIgnoreCase("checks")) {
                checks.get(value);
            }
        }catch (NullPointerException e) {
            return defaultValue;
        }
        if(configName.equalsIgnoreCase("config")) {
            if (config == null) {
                guard.reloadConfig();
            }
            return config.getInt(value);

        }
        if(configName.equalsIgnoreCase("checks")) {
            if(checks == null) {
                reloadConfigs();
            }
            return checks.getInt(value);
        }
        return defaultValue;
    }
    public double getDoubleFromConfig(String configName, String value, double defaultValue) {
        try {
            if(configName.equalsIgnoreCase("config")) {
                config.get(value);
            }
            if(configName.equalsIgnoreCase("checks")) {
                checks.get(value);
            }
        }catch (NullPointerException e) {
            return defaultValue;
        }
        if(configName.equalsIgnoreCase("config")) {
            if (config == null) {
                guard.reloadConfig();
            }
            return config.getDouble(value);

        }
        if(configName.equalsIgnoreCase("checks")) {
            if(checks == null) {
                reloadConfigs();
            }
            return checks.getDouble(value);
        }
        return defaultValue;
    }


    public FileConfiguration getConfig(String configName) {
        if(configName.equalsIgnoreCase("config")) {
            if (config == null) {
                guard.reloadConfig();
            }
            return config;
        }
        if(configName.equalsIgnoreCase("checks")) {
            if(checks == null) {
                reloadConfigs();
            }
            return checks;
        }
        return null;

    }

    public void saveConfig(String configName) {
        if(configName.equalsIgnoreCase("config")) {
            if(config == null) return;
            guard.saveDefaultConfig();
        }
        if(configName.equalsIgnoreCase("checks")) {
            if(checks == null || checksFile == null) return;
            try {
                checks.save(checksFile);
            } catch (IOException e) {
                guard.getLogger().log(Level.SEVERE, "Could not save checks.yml!");
            }
        }
    }

    public void saveDefaults(String configName) {
        if(configName.equalsIgnoreCase("config")) {
            if(config == null) return;
            guard.saveDefaultConfig();
        }
        if(configName.equalsIgnoreCase("checks")) {
            if(checksFile == null) checksFile = new File(guard.getDataFolder(), "checks.yml");

            if(!checksFile.exists()) {
                guard.saveResource("checks.yml", false);
            }
        }
    }

}
