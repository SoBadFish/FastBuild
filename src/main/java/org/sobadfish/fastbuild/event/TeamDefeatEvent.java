package org.sobadfish.fastbuild.event;

import cn.nukkit.plugin.Plugin;
import org.sobadfish.fastbuild.player.team.TeamInfo;
import org.sobadfish.fastbuild.room.GameRoom;


/**
 * 队伍失败事件
 * @author SoBadFish
 * 2022/5/24
 */
public class TeamDefeatEvent extends GameRoomEvent {

    private final TeamInfo teamInfo;

    public TeamDefeatEvent(TeamInfo teamInfo, GameRoom room, Plugin plugin) {
        super(room, plugin);
        this.teamInfo = teamInfo;
    }

    public TeamInfo getTeamInfo() {
        return teamInfo;
    }
}
