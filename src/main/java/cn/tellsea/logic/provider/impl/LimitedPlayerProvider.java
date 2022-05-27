package cn.tellsea.logic.provider.impl;

import cn.tellsea.logic.entity.Card;
import cn.tellsea.logic.entity.Player;
import cn.tellsea.logic.provider.PlayerProvider;
import cn.tellsea.logic.util.PlayerUtil;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 有限制的发牌器，只有一副牌，玩家数量有限
 * 
 * @author Leon
 *
 */
public class LimitedPlayerProvider implements PlayerProvider {

	private static Map<String, List<Card>> cards = new ConcurrentHashMap<>();
	private Map<String,Random> random = new ConcurrentHashMap<>();
	private static Map<String, Card> pubCard = new ConcurrentHashMap<>();

//	public LimitedPlayerProvider() {
//		this.initCards();
//		this.initPubCard();
//	}

	// 产生一副新的牌
	private void initCards(String room) {
		List<Card> cards = LimitedPlayerProvider.cards.containsKey(room) ? LimitedPlayerProvider.cards.get(room) : new ArrayList<>();
		cards.clear();
		for (int i = 14; i > 1; i--) {
			for (int j = 3; j >= 0; j--) {
				Card card = new Card(j, i);
				cards.add(card);
			}
		}
		LimitedPlayerProvider.cards.put(room, cards);
	}
	// 抽取一张公共牌
	private void initPubCard(String room) {
		List<Card> cards = LimitedPlayerProvider.cards.get(room);
		Random random = this.random.get(room);
		Card card = cards.remove(random.nextInt(cards.size()));
		LimitedPlayerProvider.cards.put(room, cards);
		this.random.put(room, random);
		card.setPubCard(true);
		LimitedPlayerProvider.pubCard.put(room, card);

	}

	@Override
	public void upHandCard(Player player, String room) {
		Card[] cards = player.cards;

		for (int i = 0; i < cards.length; i++) {
			if (cards[i].isHandCard()) {
				Card card = cards[i];
				card.setPubCard(false);
				card.setHandCard(false);
				List<Card> cards1 = LimitedPlayerProvider.cards.get(room);
				Random random = this.random.get(room);
				player.cards[i] = cards1.remove(random.nextInt(LimitedPlayerProvider.cards.get(room).size()));
				player.cards[i].setHandCard(true);
				cards1.add(card);
				this.random.put(room, random);
				LimitedPlayerProvider.cards.put(room, cards1);
			}
		}
		for (int i = 0; i < player.cards.length; i++) {
			if (!player.cards[i].isPubCard() && !player.cards[i].isHandCard()) {
				player.cards[i] = generateThirdCard(player);
			}
		}
	}

	@Override
	public void upPubCard(Player player, String room) {
		Card[] cards = player.cards;
		for (int i = 0; i < cards.length; i++) {
			if (cards[i].isPubCard()) {
//				Card cardOld = cards[i];
//				cardOld.setPubCard(false);
//				cardOld.setHandCard(false);
				player.cards[i] = pubCard.get(room);
//				player.cards[i].setPubCard(true);
//				long count = this.cards.stream().filter(x -> x.getFlower() == cardOld.getFlower() && x.getNumber() == cardOld.getNumber()).count();
//				if (count == 0) {
//					this.cards.add(cardOld);
//				}
			}
		}
		for (int i = 0; i < player.cards.length; i++) {
			if (!player.cards[i].isPubCard() && !player.cards[i].isHandCard()) {
				player.cards[i] = generateThirdCard(player);
			}
		}
	}

	@Override
	public Player getSinglePlayer(String room) {
		if (cards.get(room).size() < 3) {// 牌不够发了，请洗牌！
			return null;
		}
		Player player = new Player();
		for (int i = 0; i < 3; i++) {
			// 随机从一副有序的牌中抽取一张牌
			if (i == 0) {
				Card card = new Card();
				BeanUtils.copyProperties(pubCard.get(room), card);
				player.cards[0] = card;
			} else if (i == 1) {
				Card card = getCard(room);
				card.setHandCard(true);
				player.cards[1] = card;
			} else {
				// 计算第三张牌
				player.cards[2] = generateThirdCard(player);
			}
		}
		PlayerUtil.sortPlayerByNumber(player);
		return player;
	}

	@Override
	public List<Player> getPlayers(int number, String room) {
		if (cards.get(room).size() == 52 && number > 17) {
			throw new IllegalArgumentException("这么多人玩？牌都不够发!");
		} else if (number * 3 > cards.get(room).size()) {
			return null;
		}
		List<Player> players = new ArrayList<>();
		if (pubCard !=  null && pubCard.get(room) != null) {
			resetPubCard(room);
		}
		for (int i = 0; i < number; i++) {
			Player player = getSinglePlayer(room);
			players.add(player);
		}
		return players;
	}

	@Override
	public void shuffle(String room) {
		random.remove(room);
		random.put(room, new Random());
		this.initCards(room);
		this.initPubCard(room);
	}

	@Override
	public void resetPubCard(String room) {
		Card card = pubCard.get(room);

		Card pubOldCard = new Card();
		BeanUtils.copyProperties(card, pubOldCard);
		this.initPubCard(room);
		pubOldCard.setPubCard(false);
		List<Card> cards = LimitedPlayerProvider.cards.get(room);
		cards.add(pubOldCard);
		LimitedPlayerProvider.cards.put(room, cards);
	}

	@Override
	public Card getCard(String room) {
		Random random = this.random.get(room);
		Card card = cards.get(room).size() > 0 ? cards.get(room).remove(random.nextInt(cards.get(room).size())) : null;
		this.random.put(room, random);
		return card;
	}

	private Card generateThirdCard (Player player) {
		Card[] cards = player.getCards();
		Card ggp = null;
		Card sp = null;
		for (int i = 0; i < cards.length; i++) {
			if (cards[i] != null) {
				if (cards[i].isHandCard()) {
					sp = cards[i];
				}
				if (cards[i].isPubCard()) {
					ggp = cards[i];
				}
			}
		}

		Card card = new Card();

		// 获取最大的牌
		int max = 0;
		assert ggp != null;
		int ggpNumber = ggp.getNumber();
		assert sp != null;
		int spNumber = sp.getNumber();
		if (ggpNumber > spNumber) {
			max = ggpNumber;
		}
		if (spNumber > ggpNumber) {
			max = spNumber;
		}
		int flower1 = ggp.getFlower();
		int flower2 = sp.getFlower();

		if (ggpNumber == spNumber) {
			/**
			 * 最优牌 判断是否可以组合为炸弹
			 * 判断手牌和公共牌是否数字相同
			 */
			if (Card.FLOWER_DIAMOND != flower1 && Card.FLOWER_DIAMOND != flower2) {
				card.setFlower(Card.FLOWER_DIAMOND);
			} else if (Card.FLOWER_CLUB != flower1 && Card.FLOWER_CLUB != flower2) {
				card.setFlower(Card.FLOWER_CLUB);
			} else {
				card.setFlower(Card.FLOWER_HEART);
			}
			card.setNumber(ggpNumber);
			return card;
		} else if ((ggpNumber + 1 == spNumber || ggpNumber - 1 == spNumber || ggpNumber + 2 == spNumber || ggpNumber -2 == spNumber)
				&& flower1 == flower2) {
			/**
			 * 最优牌 判断是否可以组合成同花顺
			 * 判断手牌和公共牌是否是相邻数字并且花色相同
			 * 如果最大数字为14 则取12 否则取最大数字加1
			 */
			if (ggpNumber + 2 == spNumber || ggpNumber - 2 == spNumber) {
				if (max == 14) {
					card.setNumber(13);
				} else {
					card.setNumber(max - 1);
				}
			} else {
				if (max == 14) {
					card.setNumber(12);
				} else {
					card.setNumber(max + 1);
				}
			}
			card.setFlower(flower1);
		} else if (flower1 == flower2) {
			/**
			 * 最优牌 判断是否可以组合成同花
			 * 判断手牌和公共牌是否花色一致
			 * 数字取手牌和公共牌最大的
			 */
//			int num = 0;
//			if (ggpNumber > spNumber) {
//				card.setNumber(ggpNumber);
//			} else {
//				card.setNumber(spNumber);
//			}
			if (ggpNumber == Card.NUM_A || spNumber == Card.NUM_A) {
				card.setNumber(Card.NUM_K);
			} else {
				card.setNumber(Card.NUM_A);
			}
			card.setFlower(flower1);
		} else if (ggpNumber + 1 == spNumber || ggpNumber - 1 == spNumber || ggpNumber + 2 == spNumber || ggpNumber -2 == spNumber) {
			/**
			 * 最优牌 判断是否可以组合成顺子
			 * 判断手牌和公共牌是否是相邻数字
			 * 如果最大数字为14 则取12 否则取最大数字加1 花色随机
			 */
			if (ggpNumber + 2 == spNumber || ggpNumber - 2 == spNumber) {
				if (max == 14) {
					card.setNumber(13);
				} else {
					card.setNumber(max - 1);
				}
			} else {
				if (max == 14) {
					card.setNumber(12);
				} else {
					card.setNumber(max + 1);
				}
			}

			card.setFlower(Card.FLOWER_HEART);
		} else {
			/**
			 * 最优牌 组合成对子
			 */
			card.setNumber(max);
			card.setFlower(Card.FLOWER_HEART);
		}
		return card;
	}

}
