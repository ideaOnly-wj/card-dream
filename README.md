# cards-dream

#### 介绍
梦幻炸金花玩法设计初稿1.0

#### demo地址
http://8.140.141.78:13001/

#### 架构
梦幻炸金花是基于炸金花玩法的衍生，需要先了解炸金花的玩法。


#### 炸金花游戏规则介绍

1.  玩炸金花首先要从牌群中去掉大小王，因此一共是52张扑克牌。每局游戏最多6人，最少2个人就可以愉快地玩耍。开始游戏之后先押底注后发牌于庄家，接着依次发给剩下的玩家，每个人可获得3张牌。然后从庄家的下家开始每个人有选择看牌、下注、跟注、加注、比牌和弃牌的机会，依此轮回直到有玩家获胜，一局就算是结束了。
2.  只需要一副纸牌，除去大王和小王，一共52张牌，一人可得三张牌，进行大小比对。其中，大小依次为豹子、同花顺、金花、顺子、对子、散牌。特殊大于豹子，特殊小于散牌。单张牌大小依次为2、3、4、5、6、7、8、9、10、J、Q、K、A。

#### 炸金花游戏术语

1.  庄家:第一局是随机选择一位玩家坐庄。 以后每局庄家由上一局庄家的逆时针的下家担任。
2.  看牌:查看自己三张牌的花色和点数。
3.  明注:看牌后投注筹码既明注。明注后投注筹码是暗注玩家投注筹码的2倍。如果加注的话是先加后翻倍。
4.  暗注:不看牌投注的筹码。
5.  底注:指游戏开始后每位玩家先放出游戏场规定数目的筹码。
6.  最小注:指单个玩家所下暗注不能小于游戏场所规定的数目。
7.  最大暗注:玩家暗注一次最 多放出的筹码。
8.  加注:指放出比其他玩家更多的筹码。所加注不能超过封顶的数目。
9.  弃牌:指玩家自动弃权,本局认输并且已下注的筹码无法收回。
10.  跟注:玩家选择跟是指和前面玩家下同样数量级的注值，既暗注(不看牌)或明注(已看牌)。达到设定的上限不能继续下注。
12.  比牌:拿自己的牌与游戏中玩家的牌比大小，两人以上玩家游戏时比牌可以选择对手。
13.  最大按钮:将预下在游戏中为封顶注目的最大单注值，再点加注按钮后将下最大注。
14.  最小按钮:将预下在游戏中为所跟注目的最小值，既暗注(或明注)，功能上与“跟注”相同。再点跟注后.


#### 梦幻炸金花游戏规则介绍

1. 每个玩家只能获得1张手牌，桌面有1张公共牌，公共牌所有玩家共享，每个玩家的牌型由公共牌、手牌以及联想的1张牌（联想牌为牌型组合最优牌）组合而成。
2. 没有下注、跟注、加注和弃牌， 调整为可以更换手牌、更换底牌、过、全部开牌。
3. 当前玩家跟换手牌或更换底牌，将进行惩罚（未定），惩罚结束后当前玩家变更为庄家，再重新开局，当前玩家继续更换或过，直到所有人都过后全部开牌，根据炸金花规则进行比大小，最小的玩家将受到惩罚，如果最小牌型出现多个玩家，那么最小牌型的玩家都会收到惩罚。


#### 梦幻炸金花游戏术语

1. 公共牌：1张，所有玩家都能看到
2. 手牌：1张，只有玩家自己可以看到
3. 联想牌：1张，不实质发放牌，是根据公共牌和手牌可以组合出最优牌型的牌。
4. 跟换手牌
5. 跟换公共牌 （惩罚是底牌的二倍）（惩罚选择未实现）
6. 过 ：当前玩家当前轮次暂时结束，如果当前玩家的所有下家没有出现更换手牌或者底牌，都为过时，游戏结束并进行惩罚。 (未实现)
7. 全部开牌：当前轮次的所有玩家都选择过，则开牌比较，牌型最小玩家收到惩罚，游戏结束。（未实现）

