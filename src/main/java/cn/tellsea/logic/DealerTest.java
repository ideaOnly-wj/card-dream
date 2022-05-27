package cn.tellsea.logic;

import cn.tellsea.logic.calculator.PlayerType;
import cn.tellsea.logic.calculator.impl.FlowerValueCalculator;
import cn.tellsea.logic.calculator.impl.Low2HeighCalculator;
import cn.tellsea.logic.compare.PlayerComparator;
import cn.tellsea.logic.entity.Card;
import cn.tellsea.logic.entity.Player;
import cn.tellsea.logic.provider.PlayerProvider;
import cn.tellsea.logic.provider.impl.LimitedPlayerProvider;
import cn.tellsea.logic.provider.impl.UnlimitedPlayerProvider;

import java.util.List;

/**
 * 测试庄家，负责洗牌，发牌，比较牌
 * 
 * @author Leon
 *
 */
public class DealerTest {

	// 测试结果：
	// 发出1000副牌耗时1-15毫秒，计算牌大小并排序耗时1-15毫秒
	private static final int PlayerNumber = 1024;

	public static void main(String args[]) {
//		testManualInputPlayer();
//		System.out.println("\n===============================================");
//		testSinglePlayer();
//		System.out.println("\n===============================================");
		testLimitedPlayer();
//		System.out.println("\n===============================================");
//		testManyPlayers();
	}

	// 测试手动产生一副牌
	public static void testManualInputPlayer() {
		// 使用花色参与大小比较的计算器，并且牌值越大，牌越小
		PlayerComparator comparator = new PlayerComparator(new Low2HeighCalculator());
		Player player = new Player(new Card(Card.FLOWER_SPADE, 6), new Card(Card.FLOWER_HEART, 6),
				new Card(Card.FLOWER_DIAMOND, 6));
		// 对于一副未按大小排好序的牌，调用setupUnRegularPlayer()方法
		comparator.setupUnRegularPlayer(player);
		printPlayerCards(player);
		printTypeValue(player);
	}

	// 测试从一副牌中产生一个玩家
	private static void testSinglePlayer() {
		// 使用有人数限制的发牌器
		PlayerProvider playerProvider = new LimitedPlayerProvider();
		// 使用花色参与大小比较的计算器
		PlayerComparator juger = new PlayerComparator(new FlowerValueCalculator());
		Player player = playerProvider.getSinglePlayer("");
		// 使用发牌器发出的牌，每副牌已经自动按大到小排好序，调用setupRegularPlayer()方法
		juger.setupRegularPlayer(player);
		printPlayerCards(player);
		printTypeValue(player);
	}

	// 测试从一副牌中产生多个玩家，一般玩家数量2-6个
	private static void testLimitedPlayer() {
		// 使用有人数限制的发牌器
		PlayerProvider playerProvider = new LimitedPlayerProvider();
		// 使用花色参与大小比较的计算器
		PlayerComparator juger = new PlayerComparator(new FlowerValueCalculator());
		List<Player> players = playerProvider.getPlayers(6,"1");
		// 使用发牌器发出的牌，每副牌已经自动按大到小排好序，调用sortRegularPlayers()方法
		juger.sortRegularPlayers(players);
		printPlayers(players);
	}

	// 测试随机产生不限制数量的玩家，不考虑多个玩家出现完全相同牌型的情况，但一个玩家不会出现完全相同的两张牌
	private static void testManyPlayers() {
		// 使用没有人数限制的发牌器
		PlayerProvider generator = new UnlimitedPlayerProvider();
		// 使用花色参与大小比较的计算器，并且按照牌的值越大，牌越小
		PlayerComparator juger = new PlayerComparator(new FlowerValueCalculator());
		System.out.println("\n开始发牌..." + System.currentTimeMillis());
		List<Player> players = generator.getPlayers(PlayerNumber, "1");
		System.out.println("发牌完成，开始排序..." + System.currentTimeMillis());
		juger.sortRegularPlayers(players);
		System.out.println("排序完成..." + System.currentTimeMillis());
        
		printPlayers(players);
	}

	// ========================以下代码全是打印输出====================================

	private static void printPlayers(List<Player> players) {
		for (int i = 0; i < players.size(); i++) {
			System.out.print("玩家_" + i + "_的牌：");
			printPlayerCards(players.get(i));
			printTypeValue(players.get(i));
			System.out.println();
		}
	}

	private static void printPlayerCards(Player player) {
		for (int j = 0; j < 3; j++) {
			printCard(player.cards[j]);
		}
	}

	private static void printCard(Card card) {
		int flower = card.getFlower();
		int number = card.getNumber();
		boolean pubCard = card.isPubCard();
		boolean handCard = card.isHandCard();
		String str = "";
		if (pubCard) {
			str = "公共牌";
		}else if (handCard) {
			str = "手牌";
		} else {
			str = "梦幻牌";
		}
		switch (flower) {
		case Card.FLOWER_SPADE:
			System.out.print(str + "-黑桃" + getCardStringNumber(number));
			break;
		case Card.FLOWER_HEART:
			System.out.print(str + "-红桃" + getCardStringNumber(number));
			break;
		case Card.FLOWER_CLUB:
			System.out.print(str + "-梅花" + getCardStringNumber(number));
			break;
		default:
			System.out.print(str + "-方片" + getCardStringNumber(number));
			break;
		}
		System.out.print(", ");
	}

	private static String getCardStringNumber(int number) {
		if (number <= 10) {
			return "" + number;
		} else if (number == 11) {
			return "J";
		} else if (number == 12) {
			return "Q";
		} else if (number == 13) {
			return "K";
		} else {
			return "A";
		}

	}

	private static void printTypeValue(Player player) {
		int type = player.getType();
		int value = player.getValue();
		switch (type) {
		case PlayerType.BOMB:
			System.out.print("最后牌型为炸弹,  牌值:" + value);
			break;
		case PlayerType.STRAIGHT_FLUSH:
			System.out.print("最后牌型为同花顺,  牌值:" + value);
			break;
		case PlayerType.FLUSH:
			System.out.print("最后牌型为同花，  牌值:" + value);
			break;
		case PlayerType.STRAIGHT:
			System.out.print("最后牌型为顺子,  牌值:" + value);
			break;
		case PlayerType.DOUBLE:
			System.out.print("最后牌型为对子,  牌值:" + value);
			break;
		default:
			if (player.isSpecial()) {
				System.out.print("特殊牌,  牌值:" + value);
			} else {
				System.out.print("普通牌,  牌值:" + value);
			}
			break;
		}
	}

}