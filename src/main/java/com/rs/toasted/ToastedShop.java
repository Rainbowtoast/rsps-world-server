package com.rs.toasted;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.combat.CombatDefinitions;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.WorldObject;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.rsps.teleports.BossTeleport;
import com.rs.rsps.teleports.SlayerTeleport;
import com.rs.rsps.teleports.Teleport;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class ToastedShop {

    @ServerStartupEvent
    public static void spawnNPCs() {

    }

    //Bank
    public static ObjectClickHandler handleBankShops = new ObjectClickHandler(new Object[] { 8981 }, new WorldTile(3092, 3504, 0)) {
        @Override
        public void handle(ObjectClickEvent e) {
            e.getPlayer().getBank().open();
        }
    };

    //Ranged
    public static ObjectClickHandler handleRangedShops = new ObjectClickHandler(new Object[] { 38503 }, new WorldTile(3093, 3504, 0)) {
        @Override
        public void handle(ObjectClickEvent e) {
            ShopsHandler.openShop(e.getPlayer(), "toasted_ranged_shop");
        }
    };

    //Magic
    public static ObjectClickHandler handleMagicShops = new ObjectClickHandler(new Object[] { 38631 }, new WorldTile(3094, 3504, 0)) {
        @Override
        public void handle(ObjectClickEvent e) {
            ShopsHandler.openShop(e.getPlayer(), "toasted_magic_shop");
        }
    };

    //Melee Armor
    public static ObjectClickHandler handleMeleeGearShops = new ObjectClickHandler(new Object[] { 170 }, new WorldTile(3095, 3504, 0)) {
        @Override
        public void handle(ObjectClickEvent e) {
            ShopsHandler.openShop(e.getPlayer(), "toasted_armor_shop");
        }
    };

    //Melee Weapons
    public static ObjectClickHandler handleMeleeWeaponShop = new ObjectClickHandler(new Object[] { 170 }, new WorldTile(3096, 3504, 0)) {
        @Override
        public void handle(ObjectClickEvent e) {
            ShopsHandler.openShop(e.getPlayer(), "toasted_weapon_shop");
        }
    };

    //Runecrafting
    public static ObjectClickHandler handleRunesShop = new ObjectClickHandler(new Object[] { 170 }, new WorldTile(3097, 3504, 0)) {
        @Override
        public void handle(ObjectClickEvent e) {
            ShopsHandler.openShop(e.getPlayer(), "toasted_runecrafting_shop");
        }
    };

    //Herblore
    public static ObjectClickHandler handleHerbShop = new ObjectClickHandler(new Object[] { 170 }, new WorldTile(3098, 3504, 0)) {
        @Override
        public void handle(ObjectClickEvent e) {
            ShopsHandler.openShop(e.getPlayer(), "toasted_herblore_shop");
        }
    };

    //Skilling
    public static ObjectClickHandler handleSkillingShop = new ObjectClickHandler(new Object[] { 170 }, new WorldTile(3099, 3504, 0)) {
        @Override
        public void handle(ObjectClickEvent e) {
            ShopsHandler.openShop(e.getPlayer(), "toasted_skilling_shop");
        }
    };


    //Misc.
    public static ObjectClickHandler handleMiscShop = new ObjectClickHandler(new Object[] { 170 }, new WorldTile(3100, 3504, 0)) {
        @Override
        public void handle(ObjectClickEvent e) {
            ShopsHandler.openShop(e.getPlayer(), "toasted_misc_shop");
        }
    };
    public static NPC spawnNPC(int id, WorldTile tile, String name, Direction direction, boolean randomWalk) {
        NPC npc = new NPC(id, tile);
        if (name != null)
            npc.setPermName(name);
        if (direction != null)
            npc.setFaceAngle(direction.getAngle());
        npc.setRandomWalk(randomWalk);
        return npc;
    }

    public static NPC spawnNPC(int id, WorldTile tile, Direction direction, boolean randomWalk) {
        return spawnNPC(id, tile, null, direction, randomWalk);
    }

    public static NPC spawnNPC(int id, WorldTile tile, String name) {
        return spawnNPC(id, tile, name, null, false);
    }

    public static WorldObject spawnObject(int id, WorldTile tile) {
        GameObject object = new GameObject(id, ObjectType.SCENERY_INTERACT, 0, tile);
        World.spawnObject(object);
        return object;
    }
}
