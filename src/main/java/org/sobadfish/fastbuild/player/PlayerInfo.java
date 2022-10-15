package org.sobadfish.fastbuild.player;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.inventory.PlayerEnderChestInventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemColorArmor;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.TextFormat;
import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;
import org.sobadfish.fastbuild.manager.TotalManager;
import org.sobadfish.fastbuild.player.message.ScoreBoardMessage;
import org.sobadfish.fastbuild.player.team.TeamInfo;
import org.sobadfish.fastbuild.room.GameRoom;
import org.sobadfish.fastbuild.room.event.IGameRoomEvent;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 玩家对象的信息
 * @author Sobadfish
 * @date 2022/9/9
 */
public class PlayerInfo {

    public EntityHuman player;

    private PlayerType playerType;

    private GameRoom gameRoom;

    private TeamInfo teamInfo;

    public boolean cancel;

    public boolean disable;

    public boolean isLeave;

    //小游戏强制等待的时间
    public int waitTime = 0;


    /**
     * 投票获得的分数
     * */
    public int score = 0;

    public PlayerInventory inventory;

    public PlayerEnderChestInventory eInventory;

    public float speed;

    public Position spawnPosition = null;


    public LinkedHashMap<Integer,Item> armor = new LinkedHashMap<>();

    /**
     * 背包初始物品
     * */
    public LinkedHashMap<Integer,Item> inventoryItem = new LinkedHashMap<>();


    /**
     * 实例化
     * */
    public PlayerInfo(EntityHuman player){
        this.player = player;
        this.speed = player.getMovementSpeed();
    }


    public EntityHuman getPlayer() {
        return player;
    }

    public void setPlayer(EntityHuman player){
        this.player = player;
    }

    public Level getLevel(){
        return player.getLevel();
    }


    public Location getLocation(){
        return player.getLocation();
    }

    public BlockFace getHorizontalFacing(){
        return player.getHorizontalFacing();
    }

    public String getName(){
        return player.getName();
    }

    public Position getPosition(){
        return player.getPosition();
    }





    public void setTeamInfo(TeamInfo teamInfo) {
        this.teamInfo = teamInfo;
        if(teamInfo != null) {
            this.armor = teamInfo.getTeamConfig().getTeamConfig().getInventoryArmor();
            this.inventoryItem = teamInfo.getTeamConfig().getTeamConfig().getInventoryItem();
        }
    }

    /**
     * 玩家数据初始化
     * */
    public void init(){
        if(TotalManager.getConfig().getBoolean("save-playerInventory",true)){
            inventory = getPlayer().getInventory();
            eInventory = getPlayer().getEnderChestInventory();
        }
        getPlayer().setHealth(getPlayer().getMaxHealth());
        if(getPlayer() instanceof Player) {
            ((Player)getPlayer()).getFoodData().reset();
        }
        getPlayer().getInventory().clearAll();
        getPlayer().getEnderChestInventory().clearAll();
        if(getPlayer() instanceof Player){
            ((Player) getPlayer()).removeAllWindows();
            ((Player) getPlayer()).setExperience(0,0);
        }
        //TODO 给玩家初始化物品
        getPlayer().getInventory().setHeldItemSlot(0);
    }




    /**
     * 发送无前缀信息
     * */
    public void sendTipMessage(String msg){
        if(getGameRoom() != null) {
            if (cancel || isLeave  || getGameRoom().getType() == GameRoom.GameType.END) {
                return;
            }
            TotalManager.sendTipMessageToObject(msg, getPlayer());
        }
    }
    /**
     * 发送信息
     * */
    public void sendMessage(String msg){
        if(getGameRoom() != null) {
            if (cancel || isLeave  || getGameRoom().getType() == GameRoom.GameType.END) {
                return;
            }
            TotalManager.sendMessageToObject(msg, getPlayer());
        }
    }
    /**
     * 发送音效
     * */
    public void addSound(Sound sound){
        if(cancel || isLeave){
            return;
        }
        getPlayer().getLevel().addSound(getPlayer(),sound);
    }

    /**
     * 增加效果
     * */
    public void addEffect(Effect effect) {
        if(cancel || isLeave){
            return;
        }
        getPlayer().addEffect(effect);
    }

    /**
     * 发送信息
     * */
    public void sendTitle(String msg,int time){
        if(cancel || isLeave){
            return;
        }
        if(getPlayer() instanceof Player) {
            TotalManager.sendTitle(msg, time * 20, (Player) getPlayer());
        }
    }
    /**
     * 发送信息
     * */
    public void sendTitle(String msg){
        sendTitle(msg,1);
    }
    /**
     * 发送信息
     * */
    public void sendSubTitle(String msg){
        if(cancel || isLeave ){
            return;
        }
        if(getPlayer() instanceof Player) {
            TotalManager.sendSubTitle(msg, ((Player) getPlayer()));
        }
    }

    public void removeScoreBoard(){
        if(gameRoom != null) {
            if (gameRoom.getScoreboards().containsKey(this)) {
                if (getPlayer() instanceof Player) {
                    ScoreboardAPI.removeScorebaord((Player) getPlayer(),
                            gameRoom.getScoreboards().get(this));
                    gameRoom.getScoreboards().remove(this);
                }

            }
        }
    }
    public boolean  isDeath(){
        return  playerType == PlayerType.DEATH;
    }

    public boolean isWatch(){
        return  playerType == PlayerType.WATCH;
    }

    public boolean isInRoom(){
        return !cancel && !isLeave;
    }

    public boolean isLive(){
        return !cancel && !isLeave && playerType != PlayerType.WATCH;
    }

    public void sendScore(ScoreBoardMessage message){
        if(getPlayer() instanceof Player) {
            if (message == null || getPlayer() == null || !((Player) getPlayer()).isOnline()) {
                removeScoreBoard();
                return;
            }
            if (((Player) getPlayer()).isOnline()) {
                try {
                    Scoreboard scoreboard = ScoreboardAPI.createScoreboard();
                    String title = message.getTitle();
                    ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay(DisplaySlot.SIDEBAR,
                            "dumy", TextFormat.colorize('&', title));

                    ArrayList<String> list = message.getLore();
                    for (int line = 0; line < list.size(); line++) {
                        String s = list.get(line);

                        scoreboardDisplay.addLine(TextFormat.colorize('&', s), line);
                    }
                    try {
                        gameRoom.getScoreboards().get(this).hideFor((Player) player);
                    } catch (Exception ignored) {
                    }
                    scoreboard.showFor((Player) player);
                    gameRoom.getScoreboards().put(this, scoreboard);
                } catch (Exception ignored) {
                }
            }
        }

    }

    public GameRoom getGameRoom() {
        return gameRoom;
    }


    public boolean isLeave() {
        return isLeave;
    }

    public void setGameRoom(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }

    public void setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    /**
     * 发送信息
     * */
    public void sendActionBar(String msg){
        if(cancel || isLeave){
            return;
        }
        if(getPlayer() instanceof Player) {
            ((Player) getPlayer()).sendActionBar(TextFormat.colorize('&',msg));
        }
    }

    /**
     * 发送信息
     * */
    public void sendTip(String msg){
        if(cancel || isLeave){
            return;
        }
        if(getPlayer() instanceof Player) {
            TotalManager.sendTip(msg, (Player) getPlayer());
        }
    }

    /**
     * 发送强制信息
     * */
    public void sendForceMessage(String msg){
        TotalManager.sendMessageToObject(msg, getPlayer());
    }

    /**
     * 发送强制信息
     * */
    public void sendForceTitle(String msg){
        if(player instanceof Player){
            ((Player) player).sendTitle(TextFormat.colorize('&',msg));
        }
    }

    /**
     * 发送强制信息
     * */
    public void sendForceTitle(String msg,int time){
        if(player instanceof Player){
            ((Player) player).sendTitle(TextFormat.colorize('&',msg),null,0,time,0);
        }
    }

    /**
     * 发送强制信息
     * */
    public void sendForceSubTitle(String msg){
        if(player instanceof Player){
            ((Player) player).setSubtitle(TextFormat.colorize('&',msg));
        }
    }

    public void spawn() {
        //TODO 玩家复活进入游戏
        //游戏刚开始也会进入这个方法
        if(isSendkey){
            isSendkey = false;
        }
        if (player instanceof Player) {
            ((Player) player).setFoodEnabled(false);
            ((Player) player).getFoodData().reset();

            if (!((Player) player).isOnline()) {
                playerType = PlayerType.LEAVE;
                return;
            }
        }

        player.getInventory().clearAll();


        boolean teleport;
        if(spawnPosition == null){
            spawnPosition = teamInfo.getSpawnLocation();
        }

        try {
            teleport = player.teleport(spawnPosition);
        }catch (Exception e){
            teleport = false;
        }
        if(!teleport){
            throw new NullPointerException("无法将玩家传送到队伍出生点");
        }
        if (getPlayer() instanceof Player) {
            //TODO 进入游戏后创造模式
            ((Player) getPlayer()).setGamemode(1);

            ((Player) getPlayer()).setExperience(0,0);


        }
        //TODO 初始装备
        for (Map.Entry<Integer, Item> entry : armor.entrySet()) {
            Item item;
            if(entry.getValue() instanceof ItemColorArmor){
                ItemColorArmor colorArmor = (ItemColorArmor) entry.getValue();
                BlockColor rgb = getTeamInfo().getTeamConfig().getRgb();
                if(rgb != null) {
                    colorArmor.setColor(rgb);
                }
                item = colorArmor;
            }else{
                item = entry.getValue();
            }

            player.getInventory().setArmorItem(entry.getKey(), item);
        }
        for (Map.Entry<Integer, Item> entry : inventoryItem.entrySet()) {
            Item item = entry.getValue();
            player.getInventory().setItem(entry.getKey(), item);
        }
        playerType = PlayerType.START;

    }


    /**
     * 取消
     * */
    public void cancel(){
        leave();

        cancel = true;
        disable = true;
        getGameRoom().getPlayerInfos().remove(this);
        setGameRoom(null);
    }

    private void leave(){
        clear();
        if(getTeamInfo() != null){
            getTeamInfo().quit(this);
        }
        sendScore(null);
        setLeave(true);
        playerType = PlayerType.LEAVE;
    }

    public void setLeave(boolean leave) {
        isLeave = leave;
        if(leave){
            playerType = PlayerType.LEAVE;

        }else{
            if(getGameRoom().getType() == GameRoom.GameType.WAIT){
                playerType = PlayerType.WAIT;
            }
            if(getGameRoom().getType() == GameRoom.GameType.START){
                playerType = PlayerType.START;
            }
        }
    }

    public void setMoveSpeed(float speed){
        player.setMovementSpeed(speed);
    }

    public void clear(){
        if(player instanceof Player){
            if(((Player) player).isOnline()){
                player.setNameTag(player.getName());
                player.getInventory().clearAll();
                player.setMovementSpeed(speed);
                player.getEnderChestInventory().clearAll();
                ((Player) player).getFoodData().reset();
                player.setHealth(player.getMaxHealth());
                ((Player) player).setExperience(0,0);
                if(inventory != null && eInventory != null){
                    player.getInventory().setContents(inventory.getContents());
                    player.getEnderChestInventory().setContents(eInventory.getContents());
                }
                if(getPlayer() instanceof Player) {
                    ((Player) getPlayer()).setGamemode(0);
                }
            }
        }
    }



    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PlayerInfo){
            return ((PlayerInfo) obj).getPlayer().getName().equalsIgnoreCase(getPlayer().getName());
        }
        return false;
    }
    @Override
    public String toString(){
        String teamName = "&r";
        String playerName = "&7"+player.getName();
        if(teamInfo != null && !isWatch()){
            teamName = "&7[&r"+teamInfo.getTeamConfig().getNameColor()+teamInfo.getTeamConfig().getName()+"&7]&r";
            playerName = teamInfo.getTeamConfig().getNameColor()+" &7"+player.getName();
        }else if(isWatch()){
            teamName = "&7[旁观] ";
        }

        return teamName+playerName;
    }

    public TeamInfo getTeamInfo() {
        return teamInfo;
    }

    private int spawnTime = 0;

    public static String formatTime(int s){
        int min = s / 60;
        int ss = s % 60;

        if(min > 0){
            return min+" 分 "+ss+" 秒";
        }else{
            return ss+" 秒";
        }

    }



    public static String formatTime1(int s){
        int min = s / 60;
        int ss = s % 60;
        String mi = min+"";
        String sss = ss+"";
        if(min < 10){
            mi = "0"+mi;
        }
        if(ss < 10){
            sss = "0"+ss;
        }
        if(min > 0){

            return mi+":"+sss;
        }else{
            return "00:"+sss+"";
        }

    }

    private ArrayList<String> getLore(boolean isWait){
        ArrayList<String> lore = new ArrayList<>();
        String levelName = TotalManager.getMenuRoomManager().getNameByRoom(gameRoom.getRoomConfig());
        if(levelName == null){
            levelName = " -- ";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        lore.add("&7"+format.format(new Date()));
        lore.add("游戏模式: &a"+levelName);

        lore.add(" ");
        if(isWait){
            lore.add("玩家数: &a"+gameRoom.getPlayerInfos().size()+" &r/&a "+gameRoom.getRoomConfig().getMaxPlayerSize());
            lore.add("等待中....");
            lore.add("   ");

        }else{
            IGameRoomEvent event = getGameRoom().getEventControl().getNextEvent();
            if(event != null){
                lore.add(event.display()+" &a"+formatTime1(event.getEventTime() - getGameRoom().getEventControl().loadTime));
                lore.add("    ");
            }else{

                lore.add("游戏结束: &a"+formatTime(getGameRoom().loadTime));
            }
//            lore.add("游戏结束: &a"+formatTime(getGameRoom().loadTime));
            if(gameRoom.roomConfig.teamConfigs.size() > 1){
                for(TeamInfo teamInfo: gameRoom.getTeamInfos()){
                    String me = "";
                    if(getTeamInfo() != null && getTeamInfo().equals(teamInfo)){
                        me = "&7(我)";
                    }
                    lore.add("◎ "+ teamInfo +": &r  &c"+teamInfo.getLivePlayer().size()+" "+me);
                }
            }else{
                TeamInfo teamInfo = gameRoom.getTeamInfos().get(0);
                lore.add("   ");
            }

            lore.add("     ");

        }
        Object obj = TotalManager.getConfig().get("game-logo");
        if(obj instanceof List){
            for(Object s : (List<?>)obj){
                lore.add(s.toString());
            }
        }else{
            lore.add(TotalManager.getConfig().getString("game-logo","&l&cT&6o&eC&ar&ba&9f&dt"));
        }
        return lore;
    }

    private int loadTime = 0;

    private boolean isSendkey = false;

    /**
     * 定时任务
     * */
    public void onUpdate(){
        //TODO 玩家进入房间后每秒就会调用这个方法

        //TODO 玩家更新线程
        if(playerType == PlayerType.START){
            //TODO 游戏开始后 可以弄一些buff
            player.setNameTag(TextFormat.colorize('&',teamInfo.getTeamConfig().getNameColor()+player.getName()+" \n&c❤&7"+String.format("%.1f",player.getHealth())));


        }else if(playerType == PlayerType.WAIT){
            if(getGameRoom().getRoomConfig().getWorldInfo().getWaitPosition().getY() - player.getY() > getGameRoom().getRoomConfig().callbackY){
                if(getGameRoom().getRoomConfig().getWorldInfo().getWaitPosition() == null){
                    if(getGameRoom() != null){
                        getGameRoom().quitPlayerInfo(this,true);
                        sendMessage("&c房间出现了错误 （未识别到等待大厅）已将你送回出生点");
                    }
                    return;
                }
                player.teleport(getGameRoom().getRoomConfig().getWorldInfo().getWaitPosition());
            }
        }

        try{
            Class.forName("de.theamychan.scoreboard.api.ScoreboardAPI");
        }catch (Exception e){
            return;
        }
        if(playerType != PlayerType.LEAVE){
            //TODO 计分板的一些内容

            ScoreBoardMessage boardMessage = new ScoreBoardMessage(TotalManager.getScoreBoardTitle());
            boardMessage.setLore(getLore(playerType == PlayerType.WAIT));
            sendScore(boardMessage);
        }else{
            sendScore(null);
        }

    }

    public int getScore() {
        return score;
    }

    public enum PlayerType{
        /**
         * WAIT: 等待 START: 开始 DEATH: 死亡(等待复活)  LEAVE: 离开 WATCH 观察(真正的死亡)
         * */
        WAIT,START,DEATH,LEAVE,WATCH
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }


}
