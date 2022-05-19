package io.wurmatron.mining_goggles.config;

import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.config.wrapper.OreWavelength;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import joptsimple.internal.Strings;

public class OreConfigLoader {

  public static final File CONFIG_DIR = new File("./config/Mining-Goggles");

  public static void save(OreWavelength ore) {
    HashSet<OreWavelength> oreConfig = new HashSet<>();
    // Add existing
    try {
      OreWavelength[] ores = MiningGoggles.GSON.fromJson(Strings.join(Files.readAllLines(
              Paths.get(CONFIG_DIR + File.separator + "wavelengths.json")), "\n"),
          OreWavelength[].class);
      for (OreWavelength o : ores) {
        oreConfig.add(o);
      }
    } catch (Exception e) {
    }
    if (!CONFIG_DIR.exists()) {
      CONFIG_DIR.mkdirs();
    }
    oreConfig.add(ore);
    try {
      Files.write(new File(CONFIG_DIR + File.separator + "wavelengths.json").toPath(),
          MiningGoggles.GSON.toJson(oreConfig.toArray(new OreWavelength[0])).getBytes(),
          StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    } catch (IOException e) {
      MiningGoggles.LOGGER.warn(
          "Failed to save wavelength config '" + e.getMessage() + "'");
    }
  }

  public static HashMap<String, Integer> load() {
    HashMap<String, Integer> oreConfig = new HashMap<>();
    try {
      OreWavelength[] ores = MiningGoggles.GSON.fromJson(Strings.join(Files.readAllLines(
              Paths.get(CONFIG_DIR + File.separator + "wavelengths.json")), "\n"),
          OreWavelength[].class);
      for (OreWavelength ore : ores) {
        oreConfig.put(ore.ore, ore.optimalWavelength);
      }
    } catch (Exception e) {
      MiningGoggles.LOGGER.info("Failed to load wavelengths.json, attempting to create");
    }
    if (oreConfig.size() == 0) {
      generateWavelengthsForOres();
      load();
    }
    return oreConfig;
  }

  public static void generateWavelengthsForOres() {
    save(new OreWavelength("oreIron", 250));
    save(new OreWavelength("oreCoal", 350));
    save(new OreWavelength("oreGold", 500));
    save(new OreWavelength("oreDebris", 510));
    save(new OreWavelength("oreRedstone", 550));
    save(new OreWavelength("oreDiamond", 750));
    save(new OreWavelength("oreLapis", 800));
    save(new OreWavelength("oreEmerald", 1000));
    save(new OreWavelength("oreCopper", 1050));
  }
}
