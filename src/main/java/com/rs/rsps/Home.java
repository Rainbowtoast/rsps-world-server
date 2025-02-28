package com.rs.rsps;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.combat.CombatDefinitions.Spellbook;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.engine.dialogue.Dialogue;
import com.rs.game.engine.dialogue.Options;
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
public class Home {
	
	@ServerStartupEvent
	public static void spawnNPCs() {
		/* Task master */
		//spawnNPC(14858, WorldTile.of(3090, 3494, 0), "Cadet Cassandra", Direction.WEST, false);
		
		/* Slayer Masters */
		spawnNPC(8480, WorldTile.of(3091, 3487, 0), Direction.SOUTH, false);
		spawnNPC(8481, WorldTile.of(3092, 3487, 0), Direction.SOUTH, false);
		spawnNPC(1597, WorldTile.of(3093, 3487, 0), Direction.SOUTH, false);
		spawnNPC(1598, WorldTile.of(3094, 3487, 0), Direction.SOUTH, false);
		spawnNPC(7779, WorldTile.of(3095, 3487, 0), Direction.SOUTH, false);
		spawnNPC(8466, WorldTile.of(3096, 3487, 0), Direction.SOUTH, false);
		spawnNPC(9085, WorldTile.of(3097, 3487, 0), Direction.SOUTH, false);
		spawnNPC(15661, WorldTile.of(3082, 3490, 0), Direction.EAST, false);

		/* GE Clarks */
		spawnNPC(2240, WorldTile.of(3096, 3492, 0), Direction.WEST, false);
		spawnNPC(2241, WorldTile.of(3096, 3490, 0), Direction.WEST, false);
		spawnNPC(2593, WorldTile.of(3096, 3494, 0), Direction.NORTHWEST, false);
		spawnNPC(2240, WorldTile.of(3098, 3494, 0), Direction.NORTH, false);
		spawnNPC(2241, WorldTile.of(3096, 3488, 0), Direction.WEST, false);

		/* Loyalty point shop */
		spawnNPC(13727, WorldTile.of(3091, 3505, 0), Direction.SOUTH, false);
	}

	public static ObjectClickHandler handleShops = new ObjectClickHandler(new Object[] { 18789 }, WorldTile.of(3095, 3499, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().startConversation(new Dialogue().addOptions("Which shops would you like to see?", new Options() {
				@Override
				public void create() {
					option("Melee shops", new Dialogue().addOptions("Which shop would you like to see?", ops -> {
						ops.add("Melee weapons", () -> ShopsHandler.openShop(e.getPlayer(), "hs_melee_weapons"));
						ops.add("Melee armor", () -> ShopsHandler.openShop(e.getPlayer(), "hs_melee_armor"));
					}));
					option("Ranged shops", new Dialogue().addOptions("Which shop would you like to see?", ops -> {
						ops.add("Ranged weapons", () -> ShopsHandler.openShop(e.getPlayer(), "hs_range_weapons"));
						ops.add("Ranged armor", () -> ShopsHandler.openShop(e.getPlayer(), "hs_range_armor"));
					}));
					option("Magic shops", new Dialogue().addOptions("Which shop would you like to see?", ops -> {
						ops.add("Magic weapons", () -> ShopsHandler.openShop(e.getPlayer(), "hs_mage_weapons"));
						ops.add("Magic armor", () -> ShopsHandler.openShop(e.getPlayer(), "hs_mage_armor"));
					}));
					option("Supply shop", () -> ShopsHandler.openShop(e.getPlayer(), "hs_supplies"));
				}
			}));
		}
	};

	private static Teleport[] PVP_TELEPORTS = {
			new Teleport("Mage bank (SAFE)", WorldTile.of(2539, 4715, 0)),
			new Teleport("Clan Wars (SAFE)", WorldTile.of(2997, 9680, 0)),
			new Teleport("Wests (level 10)", WorldTile.of(2976, 3597, 0), p -> p.getControllerManager().startController(new WildernessController())),
			new Teleport("Chaos altar (level 12)", WorldTile.of(3229, 3609, 0), p -> p.getControllerManager().startController(new WildernessController())),
			new Teleport("Dark warrior's fortress (level 15)", WorldTile.of(3007, 3632, 0), p -> p.getControllerManager().startController(new WildernessController())),

			new Teleport("Revenant caves (level 18)", WorldTile.of(3082, 10058, 0), p -> p.getControllerManager().startController(new WildernessController())),
			new Teleport("Easts (level 19)", WorldTile.of(3350, 3667, 0), p -> p.getControllerManager().startController(new WildernessController())),
			new Teleport("West Lava Maze/KBD (level 40)", WorldTile.of(3028, 3839, 0), p -> p.getControllerManager().startController(new WildernessController())),
			new Teleport("Red Dragon Isle (level 43)", WorldTile.of(3202, 3860, 0), p -> p.getControllerManager().startController(new WildernessController())),

			new Teleport("Agility course (level 49)", WorldTile.of(2997, 3909, 0), p -> p.getControllerManager().startController(new WildernessController())),
			null,
			null,
			null
	};
	
	public static ObjectClickHandler healingWell = new ObjectClickHandler(new Object[] { 28715 }, WorldTile.of(3080, 3487, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().sendMessage("You feel refreshed.");
			e.getPlayer().getPrayer().restorePrayer(e.getPlayer().getSkills().getLevelForXp(Constants.PRAYER) * 10);
			e.getPlayer().getPoison().reset();
			e.getPlayer().setHitpoints(e.getPlayer().getMaxHitpoints());
			e.getPlayer().refreshHitPoints();
			e.getPlayer().getSkills().set(Constants.SUMMONING, e.getPlayer().getSkills().getLevelForXp(Constants.SUMMONING));
			e.getPlayer().sendOptionDialogue("Would you like to switch up your prayers or magic?", o1 -> {
				o1.add("Select new magic book", new Dialogue().addOptions(o2 -> {
					o2.add("Modern", () -> e.getPlayer().getCombatDefinitions().setSpellbook(Spellbook.MODERN));
					o2.add("Ancient", () -> e.getPlayer().getCombatDefinitions().setSpellbook(Spellbook.ANCIENT));
					o2.add("Lunar", () -> e.getPlayer().getCombatDefinitions().setSpellbook(Spellbook.LUNAR));
				}));
				o1.add("Select new prayer book", new Dialogue().addOptions(o2 -> {
					o2.add("Modern", () -> e.getPlayer().getPrayer().setPrayerBook(false));
					o2.add("Ancient curses", () -> e.getPlayer().getPrayer().setPrayerBook(true));
				}));
			});
		}
	};

	public static ObjectClickHandler pvpPortal = new ObjectClickHandler(new Object[] { 46935 }, WorldTile.of(3093, 3506, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().startConversation(new Dialogue()
					.addSimple("<col=FF0000><shad=000000>THIS PORTAL LEADS TO WILDERNESS LOCATIONS. USE AT YOUR OWN RISK. THE LEVEL OF WILDERNESS YOU WILL BE TELEPORTED TO ARE DISPLAYED ON EACH TELEPORT.")
					.addOptions("Where would you like to go?", new Options() {
						@Override
						public void create() {
							for (Teleport t : PVP_TELEPORTS)
								if (t != null)
									option(t.getName(), new Dialogue().addNext(() -> t.teleport(e.getPlayer())));
								else
									option("Nowhere.", () -> {});
						}
					}));
		}
	};

	public static ObjectClickHandler bossPortal = new ObjectClickHandler(new Object[] { 46933 }, WorldTile.of(3083, 3492, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getBossTask() == null || e.getPlayer().getBossTask().getTask() == null) {
				e.getPlayer().sendMessage("You don't have a boss task to teleport to currently.");
				return;
			}
			Teleport[] teleports = BossTeleport.forBoss(e.getPlayer().getBossTask().getTask()).getTeleports();
			if (teleports == null || teleports.length <= 0) {
				e.getPlayer().sendMessage("Your task doesn't have any teleports configured for it. Suggest it in discord.");
				return;
			}
			e.getPlayer().startConversation(new Dialogue().addOptions("Where would you like to go?", new Options() {
				@Override
				public void create() {
					for (Teleport t : teleports)
						option(t.getName(), new Dialogue().addNext(() -> t.teleport(e.getPlayer())));
					option("Nowhere.");
				}
			}));
		}
	};

	public static ObjectClickHandler slayerPortal = new ObjectClickHandler(new Object[] { 46934 }, WorldTile.of(3095, 3483, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getSlayer().getTask() == null) {
				e.getPlayer().sendMessage("You don't have a slayer task to teleport to currently.");
				return;
			}
			Teleport[] teleports = SlayerTeleport.forMonster(e.getPlayer().getSlayer().getTask().getMonster()).getTeleports();
			if (teleports == null || teleports.length <= 0) {
				e.getPlayer().sendMessage("Your task doesn't have any teleports configured for it. Suggest it in discord.");
				return;
			}
			e.getPlayer().startConversation(new Dialogue().addOptions("Where would you like to go?", new Options() {
				@Override
				public void create() {
					for (Teleport t : teleports)
						option(t.getName(), new Dialogue().addNext(() -> t.teleport(e.getPlayer())));
					option("Nowhere.");
				}
			}));
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
