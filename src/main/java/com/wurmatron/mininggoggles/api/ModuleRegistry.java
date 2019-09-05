package com.wurmatron.mininggoggles.api;

import static java.lang.String.format;

import com.wurmatron.mininggoggles.MiningGoggles;
import com.wurmatron.mininggoggles.common.config.ConfigHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ModuleRegistry {

  // Registry
  private static Set<IModule> modules = new HashSet<>();
  // Caching
  private static HashMap<String, IModule> modulesCache = new HashMap<>();

  /**
   * Create / Register a Goggle's Module Modules names are case insensitive
   *
   * @param module Module to be registered
   * @return Returns false if another module with the same name exists, else will return true
   */
  public static boolean register(IModule module) {
    if (isModuleDisabled(module)) {
      MiningGoggles.LOGGER.info("Module '" + module.getName() + "' has been disabled by the config");
      return false;
    }
    if (!modulesCache.containsKey(module.getName().toLowerCase()) && !module.getName().isEmpty()) {
      modules.add(module);
      modulesCache.put(module.getName().toLowerCase(), module);
      return true;
    }
    MiningGoggles.LOGGER.error(format("Module '%s' has registered twice!", module.getName()));
    return false;
  }

  /**
   * Get a Module from its name (cached)
   *
   * @param name Name of the module to look for
   * @return Instance of the module, if the module does not exist it will return null
   */
  public static IModule getModuleFromName(String name) {
    if (modulesCache.containsKey(name.toLowerCase())) {
      return getModule(name);
    } else {
      MiningGoggles.LOGGER.error("Module '" + name + "' was incorrectly registered, rebuilding cache");
      rebuildCache();
      return getModule(name);
    }
  }

  /**
   * Look for a certain module based on its name (ignoringCase)
   *
   * @param name Name of the module you are looking for
   * @return the instance of the module with this name
   */
  private static IModule getModule(String name) {
    if (modulesCache.containsKey(name)) {
      return modulesCache.get(name);
    }
    for (IModule module : modules) {
      if (module.getName().equalsIgnoreCase(name)) {
        // Add entry to keep module names ignoringCase
        // Slight Performance Increase, helps with fast update modules
        modulesCache.put(name, module);
        return module;
      }
    }
    return null;
  }

  /**
   * Clears and rebuilds the module cache without touching the actually registered modules
   *
   * @see #register(IModule)
   */
  private static void rebuildCache() {
    modulesCache.clear();
    modules.forEach(module -> modulesCache.put(module.getName().toLowerCase(), module));
  }

  /**
   * Checks the config to make sure the module has not been disabled
   *
   * @param module Module to check if its enabled
   * @return The Module is enabled or not
   */
  private static boolean isModuleDisabled(IModule module) {
    for (String disabledModule : ConfigHandler.disabledModules) {
      if (disabledModule.equalsIgnoreCase(module.getName())) {
        return true;
      }
    }
    return false;
  }
}
