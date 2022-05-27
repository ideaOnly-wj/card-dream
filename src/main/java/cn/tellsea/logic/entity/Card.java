package cn.tellsea.logic.entity;

/**
 * 单张牌
 * 
 * @author Leon
 *
 */
public class Card {

	public static final int FLOWER_SPADE = 3;// 黑桃
	public static final int FLOWER_HEART = 2;// 红桃
	public static final int FLOWER_CLUB = 1;// 梅花
	public static final int FLOWER_DIAMOND = 0;// 方片

	public static final int NUM_A = 14;
	public static final int NUM_K = 13;
	public static final int NUM_Q = 12;
	public static final int NUM_J = 11;
	public static final int NUM_10 = 10;
	public static final int NUM_9 = 9;
	public static final int NUM_8 = 8;
	public static final int NUM_7 = 7;
	public static final int NUM_6 = 6;
	public static final int NUM_5 = 5;
	public static final int NUM_4 = 4;
	public static final int NUM_3 = 3;
	public static final int NUM_2 = 2;

	// 单张牌大小
	private int number;
	// 花色
	private int flower;
	// 是否手牌
	private boolean handCard;
	private boolean pubCard;

	public Card() { }

	public Card(int flower, int number) {
		this.flower = flower;
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getFlower() {
		return flower;
	}

	public void setFlower(int flower) {
		this.flower = flower;
	}

	public boolean isHandCard() {
		return handCard;
	}

	public void setHandCard(boolean handCard) {
		this.handCard = handCard;
	}

	public boolean isPubCard() {
		return pubCard;
	}

	public void setPubCard(boolean pubCard) {
		this.pubCard = pubCard;
	}
}