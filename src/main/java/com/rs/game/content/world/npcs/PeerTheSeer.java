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
package com.rs.game.content.world.npcs;

import com.rs.game.engine.quest.Quest;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class PeerTheSeer {

	public static NPCClickHandler handler = new NPCClickHandler(new Object[] { "Peer the Seer" }) {
		@Override
		public void handle(NPCClickEvent e) {
			switch(e.getOption()) {
			case "Deposit":
				if (e.getPlayer().isQuestComplete(Quest.FREMENNIK_TRIALS, "to deposit with Peer."))
					e.getPlayer().getBank().openDepositBox();
				break;
			}
		}
	};

}
