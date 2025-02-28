package com.rs.rsps.teleports;

import com.rs.game.content.bosses.godwars.GodwarsController;
import com.rs.game.content.skills.slayer.BossTask;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.lib.game.WorldTile;

import java.util.HashMap;
import java.util.Map;

public enum BossTeleport {

    CHAOS_ELEMENTAL(BossTask.BossTasks.CHAOS_ELEMENTAL,
            new Teleport("Chaos Elemental", WorldTile.of(3279, 3925, 0), player -> player.getControllerManager().startController(new WildernessController()))),
    GENERAL_GRAARDOR(BossTask.BossTasks.GENERAL_GRAARDOR,
            new Teleport("General Graardor", WorldTile.of(2858, 5354, 2), player -> player.getControllerManager().startController(new GodwarsController()))),
    COMMANDER_ZILYANA(BossTask.BossTasks.COMMANDER_ZILYANA,
            new Teleport("Commander Zilyana", WorldTile.of(2912, 5267, 0), player -> player.getControllerManager().startController(new GodwarsController()))),
    KRIL_TSUTAROTH(BossTask.BossTasks.KRIL_TSUTAROTH,
            new Teleport("K'ril Tsutaroth", WorldTile.of(2926, 5340, 2), player -> player.getControllerManager().startController(new GodwarsController()))),
    KREE_ARRA(BossTask.BossTasks.KREE_ARRA,
            new Teleport("Kree'arra", WorldTile.of(2836, 5291, 2), player -> player.getControllerManager().startController(new GodwarsController()))),
    NEX(BossTask.BossTasks.NEX,
            new Teleport("Nex", WorldTile.of(2885, 5284, 2), player -> player.getControllerManager().startController(new GodwarsController()))),
    KING_BLACK_DRAGON(BossTask.BossTasks.KING_BLACK_DRAGON,
            new Teleport("King Black Dragon", WorldTile.of(2273, 4683, 0))),
    KALPHITE_QUEEN(BossTask.BossTasks.KALPHITE_QUEEN,
            new Teleport("Kalphite Queen", WorldTile.of(3512, 9494, 2))),
    CORPOREAL_BEAST(BossTask.BossTasks.CORPOREAL_BEAST,
            new Teleport("Corporeal Beast", WorldTile.of(2965, 4383, 2))),
    DAGANNOTH_REX(BossTask.BossTasks.DAGANNOTH_REX,
            new Teleport("Dagannoth Rex", WorldTile.of(1923, 4366, 0))),
    DAGANNOTH_PRIME(BossTask.BossTasks.DAGANNOTH_PRIME,
            new Teleport("Dagannoth Prime", WorldTile.of(1923, 4366, 0))),
    DAGANNOTH_SUPREME(BossTask.BossTasks.DAGANNOTH_SUPREME,
            new Teleport("Dagannoth Supreme", WorldTile.of(1923, 4366, 0))),
    TORMENTED_DEMON(BossTask.BossTasks.TORMENTED_DEMON,
            new Teleport("Tormented Demon", WorldTile.of(2569, 5734, 0))),
    ;

    private static Map<BossTask.BossTasks, BossTeleport> MAP = new HashMap<>();

    static {
        for (BossTeleport t : BossTeleport.values())
            MAP.put(t.boss, t);
    }

    public static BossTeleport forBoss(BossTask.BossTasks boss) {
        return MAP.get(boss);
    }

    private BossTask.BossTasks boss;
    private Teleport[] teleports;

    BossTeleport(BossTask.BossTasks boss, Teleport... teleports) {
        this.boss = boss;
        this.teleports = teleports;
    }

    public Teleport[] getTeleports() {
        return teleports;
    }

}
