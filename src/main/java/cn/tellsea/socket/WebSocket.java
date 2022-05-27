package cn.tellsea.socket;

import cn.tellsea.logic.calculator.impl.FlowerValueCalculator;
import cn.tellsea.logic.compare.PlayerComparator;
import cn.tellsea.logic.entity.Player;
import cn.tellsea.logic.provider.PlayerProvider;
import cn.tellsea.logic.provider.impl.LimitedPlayerProvider;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint(value = "/websocket/{param}")
public class WebSocket {

    /**
     * 在线人数
     */
    public static int onlineNumber = 0;
    /**
     * 以用户的姓名为key，WebSocket为对象保存起来
     */
    private static Map<String, WebSocket> clients = new ConcurrentHashMap<String, WebSocket>();
    /**
     * 以用户的姓名为key，Player为对象保存起来
     */
    private static Map<String, Player> client_Player = new ConcurrentHashMap<String, Player>();
    /**
     * 使用有人数限制的发牌器
     */
    private static PlayerProvider playerProvider = new LimitedPlayerProvider();
    /**
     * 使用花色参与大小比较的计算器
     */
    private static PlayerComparator juger = new PlayerComparator(new FlowerValueCalculator());

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    /**
     * 会话
     */
    private Session session;
    /**
     * 用户名称
     */
    private String username;

    /**
     * 房间号
     */
    private String room;

    /**
     * 房间在线人数
     */
    private static Map<String, Integer> roomNumber = new ConcurrentHashMap<>();

    /**
     * OnOpen 表示有浏览器链接过来的时候被调用
     * OnClose 表示浏览器发出关闭请求的时候被调用
     * OnMessage 表示浏览器发消息的时候被调用
     * OnError 表示有错误发生，比如网络断开了等等
     */

    /**
     * 建立连接
     *
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("param") String param, Session session) {
        String[] split = param.split("&&");
        String username = split[0];
        String room = split[1];
        onlineNumber++;
        log.info("现在来连接的客户id：" + session.getId() + " 用户名：" + username + " 房间号：" + room);
        this.username = username;
        this.session = session;
        this.room = room;
        int roomNum = roomNumber.containsKey(room) ? roomNumber.get(room) + 1 : 1;
        roomNumber.put(room, roomNum);
        log.info("有新连接加入！ 当前总在线人数：" + onlineNumber + ",总房间数：" + roomNumber.size() + ",当前房间号" + room + ",在线人数：" + roomNum);
        try {
            //messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息
            //先给所有人发送通知，说我上线了
            Map<String, Object> map1 = new HashMap<>();
//            Map<WebSocket, Player> mapUser = new ConcurrentHashMap<WebSocket, Player>();
            map1.put("messageType", 1);
            map1.put("username", this.username);
            sendMessageAllByRoom(map1, this.username);
            //把自己的信息加入到map当中去
//            mapUser.put(this, null);
            clients.put(this.username + "-" + this.room, this);
            Player player = new Player();
            player.setRoom(room);
            client_Player.put(this.username + "-" + this.room, player);
            //给自己发一条消息：告诉自己现在都有谁在线
            Map<String, Object> map2 = new HashMap<>();
            map2.put("messageType", 3);
            //移除掉自己
            Set<String> set = clients.keySet();
            map2.put("onlineUsers", set);
            sendMessageToByRoom(JSON.toJSONString(map2), this.username);
        } catch (IOException e) {
            e.printStackTrace();
            log.info(this.username + "上线的时候通知所有人发生了错误");
        }


    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.info("服务端发生了错误" + error.getMessage());
        //error.printStackTrace();
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(Session userSession) {
        onlineNumber--;
        client_Player.remove(username + "-" + room);
        clients.remove(username + "-" + room);
        int roomNum = roomNumber.get(room) - 1;
        if (roomNum == 0) {
            roomNumber.remove(room);
        } else {
            roomNumber.put(room, roomNum);
        }

        //webSockets.remove(this);

        try {
            //messageType 1代表上线 2代表下线 3代表在线名单  4代表普通消息
            Map<String, Object> map1 = new HashMap<>();
            map1.put("messageType", 2);
            map1.put("onlineUsers", clients.keySet());
            map1.put("username", username);
            sendMessageAllByRoom(map1, username);
        } catch (IOException e) {
            e.printStackTrace();
            log.info(username + "下线的时候通知所有人发生了错误");
        }
        log.info("有连接关闭！ 当前总在线人数：" + onlineNumber + " 总房间数：" + roomNumber.size());

    }

    /**
     * 收到客户端的消息
     *
     * @param message 消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            log.info("来自客户端消息：" + message + "客户端的id是：" + session.getId());
            JSONObject jsonObject = JSON.parseObject(message);
            String textMessage = jsonObject.getString("message");
            String fromusername = jsonObject.getString("username");
            String tousername = jsonObject.getString("to");
            // 1代表上线 2代表下线 3代表在线名单  4代表普通消息 5发牌 6 跟换底牌 7跟换手牌 8过
            String type = jsonObject.getString("type");
            List<Player> players = new ArrayList<>();
            //如果不是发给所有，那么就发给某一个人
            //messageType 1代表上线 2代表下线 3代表在线名单  4代表普通消息 5发牌 6 跟换底牌 7跟换手牌 8过
            Map<String, Object> map1 = new HashMap<>();
            map1.put("messageType", type);
            map1.put("textMessage", textMessage);
            map1.put("fromusername", fromusername);

            if ("5".equals(type) && "admin".equals(fromusername)) {

                if (roomNumber.get(room) > 1) {
                    playerProvider.shuffle(room);
                    players = playerProvider.getPlayers(Math.toIntExact(roomNumber.get(room)), room);
                    // 使用发牌器发出的牌，每副牌已经自动按大到小排好序，调用sortRegularPlayers()方法
//                    juger.sortRegularPlayers(players);
//                    for (String user : client_Player.keySet()) {
//                        Player player = client_Player.get(user);
//                        if (player != null && room.equals(player.getRoom())) {
//                            client_Player.remove(user + "-" + room);
//                        }
//                    }
//                    client_Player.clear();
                    int index = 0;
                    for (String username : clients.keySet()) {
                        Player player = client_Player.get(username);
                        if (player != null && room.equals(player.getRoom())) {
                            Player player1 = players.get(index++);
                            player1.setRoom(room);
                            client_Player.put(username, player1);
                        }
                    }
                    if ("All".equals(tousername)) {
                        map1.put("tousername", "所有人");
                        sendMessageAllByRoom(map1, fromusername);
                    } else {
                        map1.put("tousername", tousername);
                        sendMessageToByRoom(JSON.toJSONString(map1), tousername);
                    }
                } else {
                    map1.put("textMessage", "人数不足，无法发牌");
                    map1.put("fromusername", "系统");
                    map1.put("tousername", fromusername);
                    map1.put("messageType", 4);
                    sendMessageToByRoom(JSON.toJSONString(map1), fromusername);
                }

            } else if ("6".equals(type)) {
//                Player player = client_Player.get(fromusername);
                // 重新获取一张公共牌
//                Card card = playerProvider.getCard();
                playerProvider.resetPubCard(room);
                for (Map.Entry<String, Player> entry : client_Player.entrySet()) {
                    String username = entry.getKey();
                    Player player1 = entry.getValue();
                    if (player1 != null && room.equals(player1.getRoom())) {
                        playerProvider.upPubCard(player1, room);
                        client_Player.put(username, player1);
                    }
                }
                sendMessageForPubCardByRoom(map1, fromusername);
            } else if ("7".equals(type)) {
                Player player = client_Player.get(fromusername + "-" + room);
                playerProvider.upHandCard(player, room);
                client_Player.put(fromusername + "-" + room, player);
                sendMessageToByRoom(map1, fromusername, tousername);
            } else {
                if ("All".equals(tousername)) {
                    map1.put("tousername", "所有人");
                    sendMessageAllByRoom(map1, fromusername);
                } else {
                    map1.put("tousername", tousername);
                    sendMessageToByRoom(JSON.toJSONString(map1), tousername);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("发生了错误了");
        }
    }

    private void sendMessageToByRoom(Map<String, Object> message, String fromusername, String tousername) {
        for (String user : clients.keySet()) {
            WebSocket item = clients.get(user);
//            String user = item.username + "-" + room;
            Player player = client_Player.get(user);
            if (player != null && room.equals(player.getRoom())) {
                message.put("player", player);
                if (user.equals(fromusername + "-" + room)) {
                    message.put("textMessage", "您更换了手牌  " + sdf.format(new Date()));
                } else {
                    message.put("textMessage", fromusername + "更换了手牌  " + sdf.format(new Date()));
                }
                item.session.getAsyncRemote().sendText(JSON.toJSONString(message));
                log.info(JSON.toJSONString(message));
            }
        }
    }


    public void sendMessageToByRoom(String message, String toUserName) throws IOException {
        toUserName = toUserName + "-" + room;
        for (WebSocket item : clients.values()) {
            Player player = client_Player.get(toUserName);
            if (player != null && room.equals(player.getRoom())) {
                if ((item.username + "-" + room).equals(toUserName)) {
                    item.session.getAsyncRemote().sendText(message);
                    log.info(JSON.toJSONString(message));
                    break;
                }
            }
        }
    }

    public void sendMessageAllByRoom(Map<String, Object> message, String fromUserName) throws IOException {
        fromUserName = fromUserName + "-" + room;
        for (Map.Entry<String, WebSocket> entry : clients.entrySet()) {
            String username = entry.getKey();
            WebSocket webSocket = entry.getValue();
            Player player = client_Player.get(username);
            if (player != null && room.equals(player.getRoom())) {
                message.put("player", player);
                webSocket.session.getAsyncRemote().sendText(JSON.toJSONString(message));
                log.info(JSON.toJSONString(message));
            }

        }
    }

    public void sendMessageForPubCardByRoom(Map<String, Object> message, String fromUserName) throws IOException {
        for (Map.Entry<String, WebSocket> entry : clients.entrySet()) {
            String username = entry.getKey();
            WebSocket webSocket = entry.getValue();
            Player player = client_Player.get(username);
            if (player != null && room.equals(player.getRoom())) {
                message.put("player", player);
                if (username.equals(fromUserName + "-" + room)) {
                    message.put("textMessage", "您更换了公共牌  " + sdf.format(new Date()));
                } else {
                    message.put("textMessage", fromUserName + "更换了公共牌  " + sdf.format(new Date()));
                }
                webSocket.session.getAsyncRemote().sendText(JSON.toJSONString(message));
                log.info(JSON.toJSONString(message));
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineNumber;
    }

}
