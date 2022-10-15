package org.sobadfish.fastbuild;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.fastbuild.command.GameAdminCommand;
import org.sobadfish.fastbuild.command.GameCommand;
import org.sobadfish.fastbuild.command.GameSpeakCommand;
import org.sobadfish.fastbuild.manager.TotalManager;

/**

 * @author Sobadfish
 * 13:07
 */
public class FastBuildMain extends PluginBase {



    @Override
    public void onEnable() {

        this.getLogger().info(TextFormat.colorize('&',"&e ___        _   ___      _ _    _ "));
        this.getLogger().info(TextFormat.colorize('&',"&e| __|_ _ __| |_| _ )_  _(_) |__| |"));
        this.getLogger().info(TextFormat.colorize('&',"&e| _/ _` (_-<  _| _ \\ || | | / _` |"));
        this.getLogger().info(TextFormat.colorize('&',"&e|_|\\__,_/__/\\__|___/\\_,_|_|_\\__,_|"));
        this.getLogger().info(TextFormat.colorize('&',"&e"));
        this.getLogger().info(TextFormat.colorize('&',"&e正在加载"+TotalManager.GAME_NAME+" 插件 本版本为&av"+this.getDescription().getVersion()));
        this.getLogger().info(TextFormat.colorize('&',"&a插件加载完成，祝您使用愉快"));

        TotalManager.init(this);
        this.getServer().getCommandMap().register(TotalManager.GAME_NAME,new GameAdminCommand(TotalManager.COMMAND_ADMIN_NAME));
        this.getServer().getCommandMap().register(TotalManager.GAME_NAME,new GameCommand(TotalManager.COMMAND_NAME));
        this.getServer().getCommandMap().register(TotalManager.GAME_NAME,new GameSpeakCommand(TotalManager.COMMAND_MESSAGE_NAME));

        this.getLogger().info(TextFormat.colorize('&',"&a插件加载完成，祝您使用愉快"));

    }

    @Override
    public void onDisable() {
       TotalManager.onDisable();
    }


//    public enum UiType{
//        /**
//         * auto: 自动
//         *
//         * packet: GUI界面
//         *
//         * ui: 箱子界面
//         * */
//        AUTO,PACKET,UI
//    }
}
