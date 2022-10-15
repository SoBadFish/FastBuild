package org.sobadfish.fastbuild.room.event.defaults;

import org.sobadfish.fastbuild.room.GameRoom;
import org.sobadfish.fastbuild.room.config.GameRoomEventConfig;
import org.sobadfish.fastbuild.room.event.IGameRoomEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * @author Sobadfish
 * @date 2022/10/15
 */
public class VoteEvent extends IGameRoomEvent  {


    public VoteEvent(GameRoomEventConfig.GameRoomEventItem item) {
        super(item);
    }

    @Override
    public void onStart(GameRoom room) {
        //TODO 获取玩家的投票结果 并选出最高的
        int page = 0;
        String maxVote = null;
        for(Map.Entry<String, Integer> vEntry: room.voteList.entrySet()){
            if(vEntry.getValue() > page){
                page = vEntry.getValue();
                maxVote = vEntry.getKey();
            }
        }
        if(maxVote == null){
            //没人投票就随机抽一个
            maxVote = new ArrayList<>(room.voteList.keySet())
                    .get(new Random().nextInt(room.voteList.size()));
        }
        room.voteMsg = maxVote;
        room.sendTitle(maxVote);
        room.sendMessage("&e当前主题为: &f"+maxVote);

    }


}
