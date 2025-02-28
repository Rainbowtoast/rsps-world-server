package com.rs.game.content.quests.holygrail.dialogue.knightsroundtable;

import static com.rs.game.content.quests.holygrail.HolyGrail.GIVE_AURTHUR_HOLY_GRAIL;
import static com.rs.game.content.quests.holygrail.HolyGrail.GO_TO_ENTRANA;
import static com.rs.game.content.quests.holygrail.HolyGrail.GO_TO_MCGRUBOR;
import static com.rs.game.content.quests.holygrail.HolyGrail.QUEST_COMPLETE;
import static com.rs.game.content.quests.holygrail.HolyGrail.SPEAK_TO_FISHER_KING;
import static com.rs.game.content.quests.holygrail.HolyGrail.SPEAK_TO_PERCIVAL;

import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class SirPalomedesHolyGrailD extends Conversation {
	private static final int NPC = 3787;
	public SirPalomedesHolyGrailD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.HOLY_GRAIL)) {
			case GO_TO_ENTRANA, GO_TO_MCGRUBOR, SPEAK_TO_FISHER_KING, SPEAK_TO_PERCIVAL, GIVE_AURTHUR_HOLY_GRAIL -> {
				addNPC(NPC, HeadE.CALM_TALK, "Hello there adventurer, what do you want of me?");
				addPlayer(HeadE.HAPPY_TALKING, "I'd like some advice on finding the Grail.");
				addNPC(NPC, HeadE.CALM_TALK, "Sorry, I cannot help you with that.");
			}
			case QUEST_COMPLETE -> {
				addNPC(NPC, HeadE.CALM_TALK, "Amazing job getting the Holy Grail!");
				addPlayer(HeadE.HAPPY_TALKING, "Thank you.");
			}
		}
	}
}
