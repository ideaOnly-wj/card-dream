package cn.tellsea.logic.provider;

import cn.tellsea.logic.entity.Card;
import cn.tellsea.logic.entity.Player;

import java.util.List;

/**
 * 发牌器
 * 
 * @author Leon
 *
 */
public interface PlayerProvider {


	// 跟换手牌
	void upHandCard(Player player, String room);

	// 跟换公共牌
	void upPubCard(Player player, String room);

	// 产生单副牌
	Player getSinglePlayer(String room);

	// 产生多副牌
	List<Player> getPlayers(int number, String room);

	// 发一张牌
	Card getCard(String room);

	// 洗牌
	void shuffle(String room);

	// 重新获取公共牌
	void resetPubCard(String room);
}
