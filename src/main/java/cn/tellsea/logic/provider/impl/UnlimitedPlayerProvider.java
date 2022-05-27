package cn.tellsea.logic.provider.impl;

import cn.tellsea.logic.entity.Card;
import cn.tellsea.logic.entity.Player;
import cn.tellsea.logic.provider.PlayerProvider;
import cn.tellsea.logic.util.PlayerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 无限制的发牌器，将产生指定个玩家数量的牌数，这里不限制玩家的数量，不考虑不同的玩家出现完全同样的牌
 * 
 * @author Leon
 *
 */
public class UnlimitedPlayerProvider implements PlayerProvider {

	private Random random = new Random();

	@Override
	public void upHandCard(Player player,String room) {

	}

	@Override
	public void upPubCard(Player player,String room) {

	}

	@Override
	public Player getSinglePlayer(String room) {
		Player player = new Player();
		for (int j = 0; j < 3; j++) {
			Card card = new Card();
			// 以下防止同一副牌中出现花色和大小都相同的牌
			int cardFlower = getRandomFlower(random);
			int cardNumber = getRandomNumber(random);
			if (j == 0) {
				card.setFlower(cardFlower);
				card.setNumber(cardNumber);
			} else if (j == 1) {
				while (cardFlower == player.cards[0].getFlower() && cardNumber == player.cards[0].getNumber()) {
					cardFlower = getRandomFlower(random);
					cardNumber = getRandomNumber(random);
				}
				card.setFlower(cardFlower);
				card.setNumber(cardNumber);
			} else {
				while ((cardFlower == player.cards[0].getFlower() && cardNumber == player.cards[0].getNumber())
						|| (cardFlower == player.cards[1].getFlower() && cardNumber == player.cards[1].getNumber())) {
					cardFlower = getRandomFlower(random);
					cardNumber = getRandomNumber(random);
				}
				card.setFlower(cardFlower);
				card.setNumber(cardNumber);
			}
			player.cards[j] = card;
		}
		PlayerUtil.sortPlayerByNumber(player);
		return player;
	}

	@Override
	public List<Player> getPlayers(int number, String room) {
		List<Player> players = new ArrayList<>();
		for (int i = 0; i < number; i++) {
			players.add(getSinglePlayer(room));
		}
		return players;
	}

	private int getRandomFlower(Random random) {
		return random.nextInt(4);
	}

	private int getRandomNumber(Random random) {
		return 2 + random.nextInt(13);
	}

	//无限制模式下，发单张牌是如此的肆无忌惮
	@Override
	public Card getCard(String room) {
		return new Card(getRandomFlower(random), getRandomNumber(random));
	}
	
	@Override
	public void shuffle(String room) {
		// 非限制玩家数的情况，不需要洗牌
	}

	@Override
	public void resetPubCard(String room) {

	}
}