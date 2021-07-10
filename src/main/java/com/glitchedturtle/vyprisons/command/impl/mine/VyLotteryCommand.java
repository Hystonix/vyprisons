package com.glitchedturtle.vyprisons.command.impl.mine;

import com.glitchedturtle.common.menu.MenuManager;
import com.glitchedturtle.vyprisons.command.abs.VySubPlayerCommand;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.MineManageMenu;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.page.MineLotteryPage;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.player.mine.lottery.MineLotteryHandler;
import com.glitchedturtle.vyprisons.util.TFormatter;
import org.bukkit.entity.Player;

public class VyLotteryCommand extends VySubPlayerCommand {

    private MenuManager _menuManager;

    public VyLotteryCommand(MenuManager menuManager) {
        super("lottery", "vyprison.command.lottery", "[join/manage/roll]", "Join the current mine's lottery");

        _menuManager = menuManager;

    }

    @Override
    public void executeCommand(VyPlayer vyPlayer, String[] args) {

        Player ply = vyPlayer.getPlayer();

        PlayerMineInstance instance = vyPlayer.getVisiting();
        if(instance == null) {

            ply.sendMessage(Conf.CMD_LOTTERY_NOT_VISITING);
            return;

        }

        MineLotteryHandler lotteryHandler = instance.getLotteryHandler();
        if(!lotteryHandler.isLotteryEnabled() || lotteryHandler.isRolling()) {

            ply.sendMessage(Conf.CMD_LOTTERY_DISABLED);
            return;

        }

        if(args.length > 0 && args[0].equalsIgnoreCase("join")) {

            if (lotteryHandler.hasEntry(ply.getUniqueId())) {

                ply.sendMessage(Conf.CMD_LOTTERY_ALREADY_ENTERED);
                return;

            }

            if(vyPlayer.cooldown("Join lottery", Conf.MINE_LOTTERY_JOIN_COOLDOWN))
                return;

            lotteryHandler.addEntry(ply.getUniqueId());
            ply.sendMessage(Conf.CMD_LOTTERY_JOINED);

            instance.broadcast(Conf.MINE_LOTTERY_ENTRY.replaceAll("%name%", ply.getName()));

        } else if(args.length > 0 && args[0].equalsIgnoreCase("roll")) {

            if(!ply.getUniqueId().equals(instance.getOwnerUniqueId())) {

                ply.sendMessage(Conf.CMD_LOTTERY_NOT_OWNER);
                return;

            }

            if(lotteryHandler.getEntries().size() < Conf.LOTTERY_MIN_ENTRIES) {

                ply.sendMessage(Conf.CMD_LOTTERY_MIN_ENTRIES);
                return;

            }

            if(lotteryHandler.getValue() < Conf.LOTTERY_MIN_VALUE) {

                ply.sendMessage(Conf.CMD_LOTTERY_MIN_VALUE);
                return;

            }

            if(vyPlayer.cooldown("Roll lottery", Conf.MINE_LOTTERY_ROLL_COOLDOWN))
                return;

            lotteryHandler.rollLottery(1);

        } else if(args.length > 0 && args[0].equalsIgnoreCase("manage")) {

            if(!ply.getUniqueId().equals(instance.getOwnerUniqueId())) {

                ply.sendMessage(Conf.CMD_LOTTERY_NOT_OWNER);
                return;

            }

            MineManageMenu menu = new MineManageMenu(vyPlayer, instance);
            menu.openPage(new MineLotteryPage(menu));

            _menuManager.openMenu(ply, menu);

        } else {

            ply.sendMessage(Conf.CMD_LOTTERY_INFO
                    .replaceAll("%value%", TFormatter.formatLargeNumber(Math.round(lotteryHandler.getValue())))
                    .replaceAll("%entry_count%", "" + lotteryHandler.getEntries().size())
            );

        }

    }

}
