package dev.thource.runelite.dudewheresmystuff;

import java.awt.Color;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;

class ItemIdentificationConfig {
  static final String CONFIG_GROUP = "itemidentification";

  @Inject ConfigManager configManager;

  private boolean _showHerbSeeds;
  private boolean _showBerrySeeds;
  private boolean _showAllotmentSeeds;
  private boolean _showSpecialSeeds;
  private boolean _showTreeSeeds;
  private boolean _showFruitTreeSeeds;
  private boolean _showFlowerSeeds;
  private boolean _showHopsSeeds;
  private boolean _showSacks;
  private boolean _showHerbs;
  private boolean _showLogs;
  private boolean _showPyreLogs;
  private boolean _showPlanks;
  private boolean _showSaplings;
  private boolean _showComposts;
  private boolean _showOres;
  private boolean _showBars;
  private boolean _showGems;
  private boolean _showPotions;
  private boolean _showImplingJars;
  private boolean _showTablets;
  private boolean _showTeleportScrolls;
  private boolean _showJewellery;
  private boolean _showEnchantedJewellery;
  private ItemIdentificationMode _identificationType = ItemIdentificationMode.MEDIUM;
  private Color _textColor = Color.WHITE;

  public void reloadConfig() {
    _showHerbSeeds = configManager.getConfiguration(
        CONFIG_GROUP, "showHerbSeeds", Boolean.class);
    _showBerrySeeds = configManager.getConfiguration(
        CONFIG_GROUP, "showBerrySeeds", Boolean.class);
    _showAllotmentSeeds = configManager.getConfiguration(
        CONFIG_GROUP, "showAllotmentSeeds", Boolean.class);
    _showSpecialSeeds = configManager.getConfiguration(
        CONFIG_GROUP, "showSpecialSeeds", Boolean.class);
    _showTreeSeeds = configManager.getConfiguration(
        CONFIG_GROUP, "showTreeSeeds", Boolean.class);
    _showFruitTreeSeeds = configManager.getConfiguration(
        CONFIG_GROUP, "showFruitTreeSeeds", Boolean.class);
    _showFlowerSeeds = configManager.getConfiguration(
        CONFIG_GROUP, "showFlowerSeeds", Boolean.class);
    _showHopsSeeds = configManager.getConfiguration(
        CONFIG_GROUP, "showHopSeeds", Boolean.class);
    _showSacks = configManager.getConfiguration(
        CONFIG_GROUP, "showSacks", Boolean.class);
    _showHerbs = configManager.getConfiguration(
        CONFIG_GROUP, "showHerbs", Boolean.class);
    _showLogs = configManager.getConfiguration(
        CONFIG_GROUP, "showLogs", Boolean.class);
    _showPyreLogs = configManager.getConfiguration(
        CONFIG_GROUP, "showPyreLogs", Boolean.class);
    _showPlanks = configManager.getConfiguration(
        CONFIG_GROUP, "showPlanks", Boolean.class);
    _showSaplings = configManager.getConfiguration(
        CONFIG_GROUP, "showSaplings", Boolean.class);
    _showComposts = configManager.getConfiguration(
        CONFIG_GROUP, "showComposts", Boolean.class);
    _showOres = configManager.getConfiguration(
        CONFIG_GROUP, "showOres", Boolean.class);
    _showBars = configManager.getConfiguration(
        CONFIG_GROUP, "showBars", Boolean.class);
    _showGems = configManager.getConfiguration(
        CONFIG_GROUP, "showGems", Boolean.class);
    _showPotions = configManager.getConfiguration(
        CONFIG_GROUP, "showPotions", Boolean.class);
    _showImplingJars = configManager.getConfiguration(
        CONFIG_GROUP, "showImplingJars", Boolean.class);
    _showTablets = configManager.getConfiguration(
        CONFIG_GROUP, "showTablets", Boolean.class);
    _showTeleportScrolls = configManager.getConfiguration(
        CONFIG_GROUP, "showTeleportScrolls", Boolean.class);
    _showJewellery = configManager.getConfiguration(
        CONFIG_GROUP, "showJewellery", Boolean.class);
    _showEnchantedJewellery = configManager.getConfiguration(
        CONFIG_GROUP, "showEnchantedJewellery", Boolean.class);
    _identificationType = configManager.getConfiguration(
        CONFIG_GROUP, "identificationType", ItemIdentificationMode.class);
    _textColor = configManager.getConfiguration(
        CONFIG_GROUP, "textColor", Color.class);
  }

  public boolean showHerbSeeds() {
    return _showHerbSeeds;
  }

  public boolean showBerrySeeds() {
    return _showBerrySeeds;
  }

  public boolean showAllotmentSeeds() {
    return _showAllotmentSeeds;
  }

  public boolean showSpecialSeeds() {
    return _showSpecialSeeds;
  }

  public boolean showTreeSeeds() {
    return _showTreeSeeds;
  }

  public boolean showFruitTreeSeeds() {
    return _showFruitTreeSeeds;
  }

  public boolean showFlowerSeeds() {
    return _showFlowerSeeds;
  }

  public boolean showHopsSeeds() {
    return _showHopsSeeds;
  }

  public boolean showSacks() {
    return _showSacks;
  }

  public boolean showHerbs() {
    return _showHerbs;
  }

  public boolean showLogs() {
    return _showLogs;
  }

  public boolean showPyreLogs() {
    return _showPyreLogs;
  }

  public boolean showPlanks() {
    return _showPlanks;
  }

  public boolean showSaplings() {
    return _showSaplings;
  }

  public boolean showComposts() {
    return _showComposts;
  }

  public boolean showOres() {
    return _showOres;
  }

  public boolean showBars() {
    return _showBars;
  }

  public boolean showGems() {
    return _showGems;
  }

  public boolean showPotions() {
    return _showPotions;
  }

  public boolean showImplingJars() {
    return _showImplingJars;
  }

  public boolean showTablets() {
    return _showTablets;
  }

  public boolean showTeleportScrolls() {
    return _showTeleportScrolls;
  }

  public boolean showJewellery() {
    return _showJewellery;
  }

  public boolean showEnchantedJewellery() {
    return _showEnchantedJewellery;
  }

  public ItemIdentificationMode identificationType() {
    return _identificationType;
  }

  public Color textColor() {
    return _textColor;
  }
}
