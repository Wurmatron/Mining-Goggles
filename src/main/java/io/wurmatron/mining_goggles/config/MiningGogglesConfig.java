package io.wurmatron.mining_goggles.config;

import io.wurmatron.mining_goggles.MiningGoggles;
import joptsimple.internal.Strings;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static io.wurmatron.mining_goggles.config.OreConfigLoader.CONFIG_DIR;

public class MiningGogglesConfig {

    public int maxBlocksPerUpdate;
    public int renderUpdateTimerTicks;
    public int renderCleanup;
    public double fuzzyRangeBest;
    public double fuzzyRangeLow;
    public int rescanInterval;
    public int damageInterval;

    public Primitive_Goggles primitiveGoggles;
    public Goggles goggles;
    public Digital_Goggles digitalGoggles;

    public MiningGogglesConfig() {
        maxBlocksPerUpdate = 5;
        renderUpdateTimerTicks = 20;
        renderCleanup = 5;
        fuzzyRangeBest = .2;
        fuzzyRangeLow = .3;
        rescanInterval = 5;
        damageInterval = 5;
        primitiveGoggles = new Primitive_Goggles(8, new float[]{1f, 1f, 1f, 1f}, 2);
        goggles = new Goggles(12, new float[]{1f, 0f, 1f, 1f}, 4);
        digitalGoggles = new Digital_Goggles(24, new String[]{"forge:chests"});
    }

    public MiningGogglesConfig(int maxBlocksPerUpdate, int renderUpdateTimerTicks, int renderCleanup, double fuzzyRangeBest, double fuzzyRangeLow, int rescanInterval, int damageInterval, Primitive_Goggles primitiveGoggles, Goggles goggles, Digital_Goggles digitalGoggles) {
        this.maxBlocksPerUpdate = maxBlocksPerUpdate;
        this.renderUpdateTimerTicks = renderUpdateTimerTicks;
        this.renderCleanup = renderCleanup;
        this.fuzzyRangeBest = fuzzyRangeBest;
        this.fuzzyRangeLow = fuzzyRangeLow;
        this.rescanInterval = rescanInterval;
        this.damageInterval = damageInterval;
        this.primitiveGoggles = primitiveGoggles;
        this.goggles = goggles;
        this.digitalGoggles = digitalGoggles;
    }

    public static void createDefaults() {
        MiningGogglesConfig defaults = new MiningGogglesConfig();
        File configLocation = new File(CONFIG_DIR + File.separator + "config.json");
        try {
            Files.write(configLocation.toPath(), MiningGoggles.GSON.toJson(defaults).getBytes(), StandardOpenOption.CREATE);
        } catch (Exception e) {
            MiningGoggles.LOGGER.error("Failed to create config.json!");
            MiningGoggles.config = defaults;
        }
    }

    public static MiningGogglesConfig load() {
        File configLocation = new File(CONFIG_DIR + File.separator + "config.json");
        if (configLocation.exists()) {
            try {
                return MiningGoggles.GSON.fromJson(Strings.join(Files.readAllLines(configLocation.toPath()), "\n"), MiningGogglesConfig.class);
            } catch (Exception e) {
                MiningGoggles.LOGGER.warn("Failed to load config.json, renaming to config-invalid.json and creating defaults");
                File errorLocation = new File(CONFIG_DIR + File.separator + "config-invalid.json");
                try {
                    Files.write(errorLocation.toPath(), Files.readAllBytes(configLocation.toPath()), StandardOpenOption.CREATE_NEW);
                } catch (Exception f) {
                    f.printStackTrace();
                    MiningGoggles.LOGGER.error("Failed to move error'd config!");
                }
                if(configLocation.delete())
                    MiningGoggles.LOGGER.error("Failed to delete config.json");
                createDefaults();
                return load();
            }
        } else {
            createDefaults();
            return load();
        }
    }

    public static class Primitive_Goggles {
        public int maxRadius;
        public float[] defaultColor;
        public int crystalDamageChance;

        public Primitive_Goggles(int maxRadius, float[] defaultColor, int crystalDamageChance) {
            this.maxRadius = maxRadius;
            this.defaultColor = defaultColor;
            this.crystalDamageChance = crystalDamageChance;
        }
    }

    public static class Goggles {
        public int maxRadius;
        public float[] defaultColor;
        public int crystalDamageChance;

        public Goggles(int maxRadius, float[] defaultColor, int crystalDamageChance) {
            this.maxRadius = maxRadius;
            this.defaultColor = defaultColor;
            this.crystalDamageChance = crystalDamageChance;
        }
    }

    public static class Digital_Goggles {
        public int maxRadius;
        public String[] filterBlacklist;

        public Digital_Goggles(int maxRadius, String[] filterBlacklist) {
            this.maxRadius = maxRadius;
            this.filterBlacklist = filterBlacklist;
        }
    }


}
