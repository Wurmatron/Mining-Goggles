package io.wurmatron.mining_goggles.config;

import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.api.MiningGogglesApi;
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

  public static HashMap<String, OreWavelength> load() {
    HashMap<String, OreWavelength> oreConfig = new HashMap<>();
    try {
      OreWavelength[] ores = MiningGoggles.GSON.fromJson(Strings.join(Files.readAllLines(
              Paths.get(CONFIG_DIR + File.separator + "wavelengths.json")), "\n"),
          OreWavelength[].class);
      for (OreWavelength ore : ores) {
        oreConfig.put(ore.ore,ore);
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

  public static int get(String name) {
    return MiningGogglesApi.oreWavelengths.getOrDefault(name, -1);
  }

  public static void generateWavelengthsForOres() {
    save(new OreWavelength("forge:ores/iron", 250, 100));
    save(new OreWavelength("forge:ores/coal", 350, 100));
    save(new OreWavelength("forge:ores/gold", 500, 50));
    save(new OreWavelength("forge:ores/debris", 510, 25));
    save(new OreWavelength("forge:ores/redstone", 550, 100));
    save(new OreWavelength("forge:ores/diamond", 750, 25));
    save(new OreWavelength("forge:ores/lapis", 800, 50));
    save(new OreWavelength("forge:ores/emerald", 1000, 30));
    save(new OreWavelength("forge:ores/copper", 1050, 100));
  }
}
