package org.sobadfish.fastbuild.panel.items;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.fastbuild.item.ItemIDSunName;
import org.sobadfish.fastbuild.panel.ChestInventoryPanel;
import org.sobadfish.fastbuild.panel.from.GameFrom;
import org.sobadfish.fastbuild.player.PlayerInfo;
import org.sobadfish.fastbuild.player.team.TeamInfo;

public class PlayerItem extends BasePlayPanelItemInstance{

    private final PlayerInfo info;

    public PlayerItem(PlayerInfo info){
        this.info = info;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Item getItem() {
        return new Item(397,3);
    }

    @Override
    public void onClick(ChestInventoryPanel inventory, Player player) {
        player.teleport(info.getPlayer());
    }

    @Override
    public void onClickButton(Player player, GameFrom shopFrom) {

    }

    @Override
    public Item getPanelItem(PlayerInfo i, int index) {
        return getItem().clone();
    }

    @Override
    public ElementButton getGUIButton(PlayerInfo info) {
        TeamInfo t = this.info.getTeamInfo();
        String img = ItemIDSunName.getIDByPath(14);
        if(t != null){
            Item i = t.getTeamConfig().getTeamConfig().getBlockWoolColor();
            img = ItemIDSunName.getIDByPath(i.getId(),i.getDamage());
        }

        return new ElementButton(TextFormat.colorize('&', this.info.toString()),new ElementButtonImageData("path",img));
    }
}
