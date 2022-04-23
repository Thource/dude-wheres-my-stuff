package dev.thource.runelite.dudewheresmystuff;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.thource.runelite.dudewheresmystuff.death.Deathpile;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class DeathStorageManager extends StorageManager<DeathStorageType, DeathStorage> {
    @Inject
    CarryableStorageManager carryableStorageManager;

    @Inject
    CoinsStorageManager coinsStorageManager;

    @Inject
    ClientThread clientThread;

    @Inject
    WorldMapPointManager worldMapPointManager;

    private int startPlayedMinutes = -1;
    public long startMs = 0L;

    private static final Set<Integer> RESPAWN_REGIONS = ImmutableSet.of(
            6457, // Kourend
            12850, // Lumbridge
            11828, // Falador
            12342, // Edgeville
            11062, // Camelot
            13150, // Prifddinas (it's possible to spawn in 2 adjacent regions)
            12894 // Prifddinas
    );
    private boolean dying;
    private WorldPoint deathLocation;
    private List<ItemStack> deathItems;

    @Inject
    private DeathStorageManager(Client client, ItemManager itemManager, ConfigManager configManager, DudeWheresMyStuffConfig config, Notifier notifier) {
        super(client, itemManager, configManager, config, notifier);
    }

    @Override
    long getTotalValue() {
        return storages.stream()
                .filter(s -> s.getType() != DeathStorageType.DEATHPILE || !((Deathpile) s).hasExpired())
                .mapToLong(Storage::getTotalValue)
                .sum();
    }

    @Override
    boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged, boolean isMember) {
        if (!enabled) return false;

        DeathStorage deathbank = null;
        for (DeathStorageType deathStorageType : DeathStorageType.values()) {
            if (deathStorageType == DeathStorageType.DEATHPILE) continue;
            if (itemContainerChanged.getContainerId() != deathStorageType.getItemContainerId()) continue;

            deathbank = new DeathStorage(deathStorageType, client, itemManager);
            deathbank.lastUpdated = System.currentTimeMillis();
            for (Item item : itemContainerChanged.getItemContainer().getItems()) {
                if (item.getId() == -1) continue;

                ItemStack itemStack = deathbank.items.stream().filter(i -> i.getId() == item.getId()).findFirst().orElse(null);
                if (itemStack != null) {
                    itemStack.setQuantity(itemStack.getQuantity() + item.getQuantity());
                    continue;
                }

                ItemComposition itemComposition = itemManager.getItemComposition(item.getId());
                deathbank.items.add(new ItemStack(item.getId(), itemComposition.getName(), item.getQuantity(), itemManager.getItemPrice(item.getId()), itemComposition.getHaPrice(), itemComposition.isStackable()));
            }
        }
        if (deathbank == null) return false;

        clearDeathbank();
        storages.add(deathbank);

        return true;
    }

    @Override
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN || gameStateChanged.getGameState() == GameState.HOPPING) {
            startPlayedMinutes = -1;
            startMs = 0L;
        }
    }

    @Override
    public boolean onGameTick(boolean isMember) {
        int playedMinutes = client.getVarcIntValue(526);
        if (playedMinutes != startPlayedMinutes) {
            if (startPlayedMinutes == -1)
                refreshMapPoints();

            startPlayedMinutes = playedMinutes;
            startMs = System.currentTimeMillis();
        }
        if (startPlayedMinutes == -1) return false;

        updateWorldMapPoints();
        return processDeath();
    }

    private void updateWorldMapPoints() {
        for (DeathStorage storage : storages) {
            if (!(storage instanceof Deathpile)) continue;

            Deathpile deathpile = (Deathpile) storage;
            if (deathpile.worldMapPoint == null) {
                if (!deathpile.hasExpired()) {
                    refreshMapPoints();
                    break;
                }

                continue;
            }

            if (deathpile.hasExpired()) {
                refreshMapPoints();
                break;
            }
            if (deathpile.worldMapPoint.getTooltip() == null) continue;

            deathpile.worldMapPoint.setTooltip("Deathpile (" + deathpile.getExpireText() + ")");
        }
    }

    private boolean processDeath() {
        if (client.getLocalPlayer() == null) return false;

        boolean updated = false;

        if (dying && client.getBoostedSkillLevel(Skill.HITPOINTS) >= 10) {
            if (!RESPAWN_REGIONS.contains(client.getLocalPlayer().getWorldLocation().getRegionID())) {
                System.out.println("Died, but did not respawn in a known respawn location: " +
                        client.getLocalPlayer().getWorldLocation().getRegionID());
            } else {
                storages.add(new Deathpile(client, itemManager, getPlayedMinutes(), deathLocation, this, deathItems));
                coinsStorageManager.storages.stream()
                        .filter(s -> s.getType() == CoinsStorageType.LOOTING_BAG)
                        .forEach(s -> s.getCoinStack().setQuantity(0));
                carryableStorageManager.storages.stream()
                        .filter(s -> s.getType() == CarryableStorageType.LOOTING_BAG || s.getType() == CarryableStorageType.SEED_BOX)
                        .forEach(s -> s.getItems().clear());

                updated = true;
                refreshMapPoints();
            }

            dying = false;
            deathLocation = null;
            deathItems = null;
        }

        return updated;
    }

    @Override
    public void onActorDeath(ActorDeath actorDeath) {
        if (client.getLocalPlayer() == null || actorDeath.getActor() != client.getLocalPlayer()) return;
        if (client.isInInstancedRegion()) return; // died in an instance, this can be used for death banks

        List<ItemStack> items = getDeathItems();
        if (items.isEmpty()) return;

        dying = true;
        deathLocation = client.getLocalPlayer().getWorldLocation();
        deathItems = items;
    }

    @Override
    public boolean onItemDespawned(ItemDespawned itemDespawned) {
        WorldPoint worldPoint = itemDespawned.getTile().getWorldLocation();
        TileItem item = itemDespawned.getItem();

        for (DeathStorage storage : storages) {
            if (!(storage instanceof Deathpile)) continue;

            Deathpile deathpile = (Deathpile) storage;
            if (deathpile.hasExpired()) continue;
            if (!deathpile.getWorldPoint().equals(worldPoint)) continue;

            for (ItemStack itemStack : deathpile.items) {
                if (itemStack.getId() != item.getId()) continue;
                if (itemStack.getQuantity() >= 65535 && item.getQuantity() != 65535) continue;
                if (itemStack.getQuantity() != item.getQuantity()) continue;

                deathpile.items.remove(itemStack);
                if (deathpile.items.isEmpty()) {
                    storages.remove(deathpile);
                    refreshMapPoints();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public String getConfigKey() {
        return "death";
    }

    @Override
    public Tab getTab() {
        return Tab.DEATH;
    }

    public int getPlayedMinutes() {
        return (int) (startPlayedMinutes + ((System.currentTimeMillis() - startMs) / 60000));
    }

    List<ItemStack> getDeathItems() {
        return carryableStorageManager.storages.stream()
                .flatMap(s -> s.getItems().stream())
                .filter(i -> i.getId() != ItemID.LOOTING_BAG && i.getId() != ItemID.LOOTING_BAG_22586)
                .collect(Collectors.toList());
    }

    @Override
    public void save() {
        if (!enabled) return;

        saveDeathpiles();
        saveDeathbank();
    }

    private void saveDeathbank() {
        String data = "";

        DeathStorage deathbank = storages.stream().filter(s -> !(s instanceof Deathpile)).findFirst().orElse(null);
        if (deathbank != null)
            data += deathbank.getType().getConfigKey() + ";"
                    + deathbank.getLastUpdated() + ";"
                    + deathbank.getItems().stream().map(i -> i.getId() + "," + i.getQuantity()).collect(Collectors.joining("="));

        configManager.setRSProfileConfiguration(
                DudeWheresMyStuffConfig.CONFIG_GROUP,
                getConfigKey() + ".deathbank",
                data
        );
    }

    private void saveDeathpiles() {
        configManager.setRSProfileConfiguration(
                DudeWheresMyStuffConfig.CONFIG_GROUP,
                getConfigKey() + "." + DeathStorageType.DEATHPILE.getConfigKey(),
                storages.stream().filter(Deathpile.class::isInstance)
                        .map(s -> {
                            Deathpile d = (Deathpile) s;
                            return d.getPlayedMinutesAtCreation() + ";"
                                    + d.getWorldPoint().getX() + "," + d.getWorldPoint().getY() + "," + d.getWorldPoint().getPlane() + ";"
                                    + d.getItems().stream().map(i -> i.getId() + "," + i.getQuantity()).collect(Collectors.joining("="));
                        }).collect(Collectors.joining("$"))
        );
    }

    @Override
    public void load() {
        if (!enabled) return;

        loadDeathpiles();
        loadDeathbank();
    }

    private void loadDeathbank() {
        clearDeathbank();

        String data = configManager.getRSProfileConfiguration(
                DudeWheresMyStuffConfig.CONFIG_GROUP,
                getConfigKey() + ".deathbank",
                String.class
        );
        if (data == null) return;

        String[] dataSplit = data.split(";");
        if (dataSplit.length != 3) return;

        String deathbankKey = dataSplit[0];
        DeathStorageType deathStorageType = Arrays.stream(DeathStorageType.values())
                .filter(s -> Objects.equals(s.getConfigKey(), deathbankKey))
                .findFirst().orElse(null);
        if (deathStorageType == null) return;

        long lastUpdate = NumberUtils.toLong(dataSplit[1], 0);
        if (lastUpdate == 0) return;

        List<ItemStack> items = new ArrayList<>();
        for (String itemData : dataSplit[2].split("=")) {
            String[] itemDataSplit = itemData.split(",");
            if (itemDataSplit.length != 2) continue;

            int itemId = NumberUtils.toInt(itemDataSplit[0], 0);
            int quantity = NumberUtils.toInt(itemDataSplit[1], 0);
            if (itemId == 0 || quantity == 0) continue;

            ItemStack item = new ItemStack(itemId, client, clientThread, itemManager);
            item.setQuantity(quantity);
            items.add(item);
        }
        if (items.isEmpty()) return;

        DeathStorage deathbank = new DeathStorage(deathStorageType, client, itemManager);
        deathbank.lastUpdated = lastUpdate;
        deathbank.items = items;
        storages.add(deathbank);
    }

    private void clearDeathbank() {
        storages.removeIf(s -> !(s instanceof Deathpile));
    }

    @Override
    void reset() {
        storages.clear();
        enable();
        refreshMapPoints();
    }

    void refreshMapPoints() {
        if (worldMapPointManager == null) return;

        worldMapPointManager.removeIf(DeathWorldMapPoint.class::isInstance);

        int index = 1;
        for (DeathStorage storage : storages) {
            if (!(storage instanceof Deathpile)) continue;

            Deathpile deathpile = (Deathpile) storage;
            if (deathpile.hasExpired()) continue;

            deathpile.worldMapPoint = new DeathWorldMapPoint(deathpile.getWorldPoint(), itemManager, index++);
            worldMapPointManager.add(deathpile.getWorldMapPoint());
        }
    }

    private void loadDeathpiles() {
        String data = configManager.getRSProfileConfiguration(
                DudeWheresMyStuffConfig.CONFIG_GROUP,
                getConfigKey() + "." + DeathStorageType.DEATHPILE.getConfigKey(),
                String.class
        );
        if (data == null) return;

        String[] pilesData = data.split("\\$");
        for (String pilesDatum : pilesData) {
            String[] dataSplit = pilesDatum.split(";");
            if (dataSplit.length != 3) continue;

            String[] worldPointSplit = dataSplit[1].split(",");
            if (worldPointSplit.length != 3) continue;

            String[] itemSplit = dataSplit[2].split("=");

            List<ItemStack> items = new ArrayList<>();
            for (String itemData : itemSplit) {
                String[] itemDataSplit = itemData.split(",");
                if (itemDataSplit.length != 2) continue;

                int itemId = NumberUtils.toInt(itemDataSplit[0], 0);
                int quantity = NumberUtils.toInt(itemDataSplit[1], 0);
                if (itemId == 0 || quantity == 0) continue;

                ItemStack item = new ItemStack(itemId, client, clientThread, itemManager);
                item.setQuantity(quantity);
                items.add(item);
            }
            if (items.isEmpty()) continue;

            storages.add(
                    new Deathpile(
                            client,
                            itemManager,
                            NumberUtils.toInt(dataSplit[0], 0),
                            new WorldPoint(
                                    NumberUtils.toInt(worldPointSplit[0], 0),
                                    NumberUtils.toInt(worldPointSplit[1], 0),
                                    NumberUtils.toInt(worldPointSplit[2], 0)
                            ),
                            this,
                            items
                    )
            );
        }

        refreshMapPoints();
    }
}
