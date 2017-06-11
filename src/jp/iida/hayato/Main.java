package jp.iida.hayato;

import jp.iida.hayato.aiwolf1.RoleAssigner;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Team;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.common.util.CalendarTools;
import org.aiwolf.sample.player.SampleRoleAssignPlayer;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.net.DirectConnectServer;
import org.aiwolf.server.net.GameServer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

  /**
   * 参加エージェントの数
   */
  static protected int PLAYER_NUM = 15;

  /**
   * 1回の実行で行うゲーム数
   */
  static protected int GAME_NUM = 100;

  public static void main(String[] args)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
    //村人側勝利数
    int villagerWinNum = 0;
    //人狼側勝利数
    int werewolfWinNum = 0;
    Integer wolfWinCount = 0;
    Integer humanWinCount = 0;

    for (int i = 0; i < GAME_NUM; i++) {
      List<Player> playerList = new ArrayList<Player>();

      for (int j = 0; j < PLAYER_NUM; j++) {
        if (j == 0) {
          playerList.add(new RoleAssigner()); //ここで作成したエージェントを指定
        } else {
          //playerList.add(new RoleAssigner());
          //playerList.add(new RoleAssigner());
          playerList.add(new SampleRoleAssignPlayer());
        }
      }

      GameServer gameServer = new DirectConnectServer(playerList);
      GameSetting gameSetting = GameSetting.getDefaultGame(PLAYER_NUM);

      AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
      game.setRand(new Random(gameSetting.getRandomSeed()));

      String logDir = "./log";
      String timeString = CalendarTools
          .toDateTime(System.currentTimeMillis()).replace("//","");
      File logFile = new File(String.format("%s/aiwolfGame%s.log", logDir, timeString));
      game.setLogFile(logFile);


      game.start();
      Role role = game.getGameData()
          .getRole(game.getGameData().getAgentList().get(0));
      if (game.getWinner() == Team.VILLAGER) {
        villagerWinNum++;
        if (role.getTeam() == game.getWinner()) {
          humanWinCount++;
        }
      } else {
        werewolfWinNum++;
        if (role.getTeam() == game.getWinner()) {
          wolfWinCount++;
        }
      }

      System.out.println("Wolf1村人側勝利:" + humanWinCount + "Wolf1人狼側勝利" + wolfWinCount);
      System.out.println("村人側勝利:" + villagerWinNum + " 人狼側勝利" + werewolfWinNum);

    }
  }
}