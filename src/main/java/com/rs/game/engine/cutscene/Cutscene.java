// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.engine.cutscene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.World;
import com.rs.game.engine.cutscene.actions.ConstructMapAction;
import com.rs.game.engine.cutscene.actions.CreateNPCAction;
import com.rs.game.engine.cutscene.actions.CutsceneAction;
import com.rs.game.engine.cutscene.actions.CutsceneCodeAction;
import com.rs.game.engine.cutscene.actions.DelayAction;
import com.rs.game.engine.cutscene.actions.DestroyCachedObjectAction;
import com.rs.game.engine.cutscene.actions.DialogueAction;
import com.rs.game.engine.cutscene.actions.LookCameraAction;
import com.rs.game.engine.cutscene.actions.MoveNPCAction;
import com.rs.game.engine.cutscene.actions.MovePlayerAction;
import com.rs.game.engine.cutscene.actions.NPCAnimationAction;
import com.rs.game.engine.cutscene.actions.NPCFaceTileAction;
import com.rs.game.engine.cutscene.actions.NPCForceTalkAction;
import com.rs.game.engine.cutscene.actions.NPCGraphicAction;
import com.rs.game.engine.cutscene.actions.PlayerAnimationAction;
import com.rs.game.engine.cutscene.actions.PlayerFaceEntityAction;
import com.rs.game.engine.cutscene.actions.PlayerFaceTileAction;
import com.rs.game.engine.cutscene.actions.PlayerForceTalkAction;
import com.rs.game.engine.cutscene.actions.PlayerGraphicAction;
import com.rs.game.engine.cutscene.actions.PlayerMusicEffectAction;
import com.rs.game.engine.cutscene.actions.PosCameraAction;
import com.rs.game.engine.dialogue.Dialogue;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.Entity.MoveType;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.region.RegionBuilder.DynamicRegionReference;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;

public abstract class Cutscene {
	private Player player;
	private int currIndex;
	private Map<String, Object> objects = new HashMap<>();
	private List<CutsceneAction> actions = new ArrayList<>();
	private int delay;
	private boolean hideMap;
	private boolean dialoguePaused;
	private boolean constructingRegion;
	private DynamicRegionReference region;
	private WorldTile endTile;
	
	public abstract void construct(Player player);

	public final void stopCutscene() {
		if (player.getX() != endTile.getX() || player.getY() != endTile.getY() || player.getPlane() != endTile.getPlane())
			player.setNextWorldTile(endTile);
		if (hideMap)
			player.getPackets().setBlockMinimapState(0);
		player.getVars().setVar(1241, 3);
		player.getPackets().sendResetCamera();
		player.setLargeSceneView(false);
		player.unlock();
		deleteObjects();
		if (region != null)
			region.destroy();
		player.getTempAttribs().removeB("CUTSCENE_INTERFACE_CLOSE_DISABLED");
	}

	public final void startCutscene() {
		if (hideMap)
			player.getPackets().setBlockMinimapState(2);
		player.getVars().setVar(1241, 1);
		player.setLargeSceneView(true);
		player.lock();
		player.stopAll(true, false);
		player.getTempAttribs().setB("CUTSCENE_INTERFACE_CLOSE_DISABLED", true);
	}

	public void constructArea(final int baseChunkX, final int baseChunkY, final int widthChunks, final int heightChunks) {
		constructingRegion = true;
		DynamicRegionReference old = region;
		region = new DynamicRegionReference(widthChunks, heightChunks);
		region.copyMapAllPlanes(baseChunkX, baseChunkY, () -> {
			player.setNextWorldTile(WorldTile.of(region.getBaseX() + widthChunks * 4, region.getBaseY() + heightChunks * 4, 0));
			constructingRegion = false;
			if (old != null)
				old.destroy();
		});
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}

	public final void logout() {
		stopCutscene();
	}

	public final boolean process() {
		if (dialoguePaused)
			return true;
		if (delay > 0) {
			delay--;
			return true;
		}
		while(true) {
			if (constructingRegion)
				return true;
			if (currIndex == actions.size()) {
				stopCutscene();
				return false;
			} else if (currIndex == 0)
				startCutscene();
			CutsceneAction action = actions.get(currIndex++);
			action.process(player, objects);
			int delay = action.getDelay();
			if (delay == -1)
				continue;
			this.delay = delay;
			return true;
		}
	}

	public void deleteObjects() {
		for (Object object : objects.values())
			deleteObject(object);
	}

	public void deleteObject(Object object) {
		if (object instanceof NPC n)
			n.finish();
	}

	public final void createObjectMap() {
		endTile = WorldTile.of(player.getTile());
		objects.put("cutscene", this);
	}

	public int getX(int x) {
		return region != null && region.isCreated() ? region.getLocalX(x) : x;
	}
	
	public int getY(int y) {
		return region != null && region.isCreated() ? region.getLocalY(y) : y;
	}
	
	public int getLocalX(int x) {
		return x > 0xFF ? (x - MapUtils.decode(Structure.CHUNK, player.getSceneBaseChunkId())[0] * 8) : x;
	}
	
	public int getLocalY(int y) {
		return y > 0xFF ? (y - MapUtils.decode(Structure.CHUNK, player.getSceneBaseChunkId())[1] * 8) : y;
	}

	public boolean isHideMap() {
		return hideMap;
	}

	public void hideMinimap() {
		this.hideMap = true;
	}
	
	public void setEndTile(WorldTile tile) {
		this.endTile = tile;
	}
	
	public WorldTile getEndTile() {
		return endTile;
	}
	
	public void camPos(int x, int y, int height, int delay) {
		actions.add(new PosCameraAction(x, y, height, delay));
	}
	
	public void camLook(int x, int y, int height, int delay) {
		actions.add(new LookCameraAction(x, y, height, delay));
	}
	
	public void camPos(int x, int y, int height, int speed1, int speed2, int delay) {
		actions.add(new PosCameraAction(x, y, height, speed1, speed2, delay));
	}
	
	public void camLook(int x, int y, int height, int speed1, int speed2, int delay) {
		actions.add(new LookCameraAction(x, y, height, speed1, speed2, delay));
	}
	
	public void camPos(int x, int y, int height) {
		camPos(x, y, height, -1);
	}
	
	public void camLook(int x, int y, int height) {
		camLook(x, y, height, -1);
	}
	
	public void camPos(int x, int y, int height, int speed1, int speed2) {
		camPos(x, y, height, speed1, speed2, -1);
	}
	
	public void camLook(int x, int y, int height, int speed1, int speed2) {
		camLook(x, y, height, speed1, speed2, -1);
	}
	
	public void camShake(int slotId, int v1, int v2, int v3, int v4, int delay) {
		action(delay, () -> player.getPackets().sendCameraShake(slotId, v1, v2, v3, v4));
	}
	
	public void camShake(int slotId, int v1, int v2, int v3, int v4) {
		camShake(slotId, v1, v2, v3, v4, -1);
	}
	
	public void camShakeReset(int delay) {
		action(delay, () -> player.getPackets().sendStopCameraShake());
	}
	
	public void camShakeReset() {
		camShakeReset(-1);
	}
	
	public void camPosReset(int delay) {
		action(delay, () -> player.getPackets().sendResetCamera());
	}
	
	public void camPosReset() {
		camPosReset(-1);
	}
	
	public void dialogue(Dialogue dialogue, int delay) {
		actions.add(new DialogueAction(dialogue, delay, false));
	}
	
	public void dialogue(Dialogue dialogue) {
		dialogue(dialogue, -1);
	}
	
	public void dialogue(Dialogue dialogue, boolean pause) {
		if (pause)
			dialogue.addNext(() -> { dialoguePaused = false; });
		actions.add(new DialogueAction(dialogue, pause ? 1 : -1, pause));
	}
	
	public void dynamicRegion(int baseX, int baseY, int widthChunks, int heightChunks) {
		actions.add(new ConstructMapAction(baseX, baseY, widthChunks, heightChunks));
	}
	
	public void fadeIn(int delay) {
		action(delay, () -> player.getInterfaceManager().fadeIn());
	}
	
	public void fadeOut(int delay) {
		action(delay, () -> player.getInterfaceManager().fadeOut());
	}
	
	public void fadeInBG(int delay) {
		action(delay, () -> player.getInterfaceManager().fadeInBG());
	}
	
	public void fadeOutBG(int delay) {
		action(delay, () -> player.getInterfaceManager().fadeOutBG());
	}
	
	public void music(int id, int delay) {
		action(delay, () -> player.musicTrack(id, 5));
	}
	
	public void music(int id) {
		music(id, -1);
	}
	
	public void musicEffect(int id, int delay) {
		actions.add(new PlayerMusicEffectAction(id, delay));
	}
	
	public void musicEffect(int id) {
		musicEffect(id, -1);
	}
	
	public NPC getNPC(String key) {
		if (objects.get(key) != null)
			return (NPC) objects.get(key);
		return null;
	}
	
	public void npcCreate(String key, int npcId, int x, int y, int z, int delay) {
		actions.add(new CreateNPCAction(key, npcId, x, y, z, delay));
	}
	
	public void npcCreate(String key, int npcId, int x, int y, int z) {
		npcCreate(key, npcId, x, y, z, -1);
	}
	
	public void npcDestroy(String key, int delay) {
		actions.add(new DestroyCachedObjectAction(key, delay));
	}
	
	public void npcDestroy(String key) {
		npcDestroy(key, -1);
	}
	
	public void npcFaceTile(String key, int x, int y, int delay) {
		actions.add(new NPCFaceTileAction(key, x, y, delay));
	}
	
	public void npcFaceTile(String key, int x, int y) {
		npcFaceTile(key, x, y, -1);
	}
	
	public void npcSpotAnim(String key, SpotAnim anim, int delay) {
		actions.add(new NPCGraphicAction(key, anim, delay));
	}
	
	public void npcSpotAnim(String key, SpotAnim anim) {
		npcSpotAnim(key, anim, -1);
	}
	
	public void npcAnim(String key, Animation anim, int delay) {
		actions.add(new NPCAnimationAction(key, anim, delay));
	}
	
	public void npcAnim(String key, Animation anim) {
		npcAnim(key, anim, -1);
	}
	
	public void npcTalk(String key, String message, int delay) {
		actions.add(new NPCForceTalkAction(key, message, delay));
	}
	
	public void npcTalk(String key, String message) {
		npcTalk(key, message, -1);
	}
	
	public void npcMove(String key, int x, int y, int z, MoveType type, int delay) {
		actions.add(new MoveNPCAction(key, x, y, z, type, delay));
	}
	
	public void npcMove(String key, int x, int y, int z, MoveType type) {
		npcMove(key, x, y, z, type, -1);
	}
	
	public void npcMove(String key, int x, int y, MoveType type, int delay) {
		npcMove(key, x, y, player.getPlane(), type, delay);
	}
	
	public void npcMove(String key, int x, int y, MoveType type) {
		npcMove(key, x, y, player.getPlane(), type, -1);
	}
	
	public void playerMove(int x, int y, int z, MoveType type, int delay) {
		actions.add(new MovePlayerAction(x, y, z, type, delay));
	}
	
	public void playerMove(int x, int y, int z, MoveType type) {
		playerMove(x, y, z, type, -1);
	}
	
	public void playerMove(int x, int y, MoveType type, int delay) {
		playerMove(x, y, player.getPlane(), type, delay);
	}
	
	public void playerMove(int x, int y, MoveType type) {
		playerMove(x, y, player.getPlane(), type, -1);
	}
	
	public void playerFaceTile(int x, int y, int delay) {
		actions.add(new PlayerFaceTileAction(x, y, delay));
	}
	
	public void playerFaceTile(int x, int y) {
		playerFaceTile(x, y, -1);
	}
	
	public void playerAnim(Animation anim, int delay) {
		actions.add(new PlayerAnimationAction(anim, delay));
	}
	
	public void playerAnim(Animation anim) {
		playerAnim(anim, -1);
	}
	
	public void playerSpotAnim(SpotAnim anim, int delay) {
		actions.add(new PlayerGraphicAction(anim, delay));
	}
	
	public void playerSpotAnim(SpotAnim anim) {
		playerSpotAnim(anim, -1);
	}
	
	public void playerTalk(String message, int delay) {
		actions.add(new PlayerForceTalkAction(message, delay));
	}
	
	public void playerTalk(String message) {
		playerTalk(message, -1);
	}
	
	public void playerFaceEntity(String key, int delay) {
		actions.add(new PlayerFaceEntityAction(key, delay));
	}
	
	public void playerFaceEntity(String key) {
		playerFaceEntity(key, -1);
	}
	
	public void action(int delay, Runnable runnable) {
		actions.add(new CutsceneCodeAction(runnable, delay));
	}
	
	public void action(Runnable runnable) {
		action(-1, runnable);
	}
	
	public void delay(int delay) {
		actions.add(new DelayAction(delay));
	}

	public void setDialoguePause(boolean paused) {
		this.dialoguePaused = paused;
	}
	
	public void projectile(int delay, WorldTile from, WorldTile to, int graphicId, int startHeight, int endHeight, int startTime, double speed, int angle, int slope, Consumer<WorldProjectile> task) {
		action(delay, () -> World.sendProjectile(WorldTile.of(getX(from.getX()), getY(from.getY()), from.getPlane()), WorldTile.of(getX(to.getX()), getY(to.getY()), to.getPlane()), graphicId, startHeight, endHeight, startTime, speed, angle, slope, task));
	}
	
	public void projectile(int delay, WorldTile from, WorldTile to, int graphicId, int startHeight, int endHeight, int startTime, double speed, int angle, int slope) {
		action(delay, () -> World.sendProjectile(WorldTile.of(getX(from.getX()), getY(from.getY()), from.getPlane()), WorldTile.of(getX(to.getX()), getY(to.getY()), to.getPlane()), graphicId, startHeight, endHeight, startTime, speed, angle, slope));
	}
	
	public void projectile(WorldTile from, WorldTile to, int graphicId, int startHeight, int endHeight, int startTime, double speed, int angle, int slope, Consumer<WorldProjectile> task) {
		projectile(-1, from, to, graphicId, startHeight, endHeight, startTime, speed, angle, slope, task);
	}
	
	public void projectile(WorldTile from, WorldTile to, int graphicId, int startHeight, int endHeight, int startTime, double speed, int angle, int slope) {
		projectile(-1, from, to, graphicId, startHeight, endHeight, startTime, speed, angle, slope);
	}

	public void npcFaceNPC(String key, String targetKey, int delay) {
		action(delay, () -> getNPC(key).setNextFaceEntity(getNPC(targetKey)));
	}
	
	public void npcFaceNPC(String key, String targetKey) {
		npcFaceNPC(key, targetKey, -1);
	}

	public void playerFaceDir(Direction dir, int delay) {
		action(delay, () -> player.setNextFaceWorldTile(player.transform(dir.getDx(), dir.getDy())));
	}
	
	public void playerFaceDir(Direction dir) {
		playerFaceDir(dir, -1);
	}

	public void npcFaceDir(String key, Direction dir, int delay) {
		action(delay, () -> getNPC(key).setNextFaceWorldTile(getNPC(key).transform(dir.getDx(), dir.getDy())));
	}
	
	public void npcFaceDir(String key, Direction dir) {
		npcFaceDir(key, dir, -1);
	}
	
	public void spawnObj(int id, int rotation, int x, int y, int z) {
		action(() -> World.spawnObject(new GameObject(id, ObjectDefinitions.getDefs(id).types[0], rotation, WorldTile.of(getX(x), getY(y), z))));
	}
}
