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
package com.rs.game.engine.cutscene.actions;

import java.util.Map;

import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;

public final class InterfaceAction extends CutsceneAction {

	private final int interfaceId;
	private final int delay;

	public InterfaceAction(int interfaceId, int actionDelay) {
		super(null, actionDelay);
		this.interfaceId = interfaceId;
		delay = actionDelay;
	}

	@Override
	public void process(final Player player, Map<String, Object> objects) {
		player.getInterfaceManager().sendInterface(interfaceId);
		WorldTasks.schedule(delay, () -> player.getInterfaceManager().removeCentralInterface());
	}

}