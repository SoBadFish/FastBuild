package org.sobadfish.fastbuild.room.event;

import org.sobadfish.fastbuild.room.GameRoom;

/**
 * @author Sobadfish
 * @date 2022/10/15
 */

public interface IEventProcess {


    /**
     * 启动事件
     * @return 是否启动成功
     * */
    boolean onEnable();
    /**
     * 判断这个事件是否被启动
     * @return 是否启动
     * */
    boolean isEnable();
    /**
     * 下个执行的任务
     * 可以返回当前类形成链表
     * @return 返回流程的事件
     * */
    IGameRoomEvent nextEvent();

    /**
     * 当事件触发时执行的任务
     * @param room 触发的游戏房间
     * */
    void doNextEvent(GameRoom room);
}
