var webSocket;
var commWebSocket;
var canvas1 = document.getElementById('myowncanvas1').getContext('2d');
// canvas1.drawPokerCard(60, 40, 200, 'h', 'A');
var canvas2 = document.getElementById('myowncanvas2').getContext('2d');
// canvas2.drawPokerCard(60, 40, 200, 'd', '2');
var canvas3 = document.getElementById('myowncanvas3').getContext('2d');
// canvas3.drawPokerCard(60, 40, 200, 's', '3');
var div4_text = "隐藏";

var player;

var messageArea = document.getElementById('message');
document.getElementById("room1").innerHTML="房间号：" + document.getElementById("room").value;
if ("WebSocket" in window) {
    param = {
        "username": document.getElementById('username').value,
        "room": document.getElementById('room').value
    }
    webSocket = new WebSocket("ws://127.0.0.1:13001/websocket/" + document.getElementById('username').value + "&&" + document.getElementById('room').value);


    //连通之后的回调事件
    webSocket.onopen = function () {
        //webSocket.send( document.getElementById('username').value+"已经上线了");
        console.log("已经连通了websocket");
        setMessageInnerHTML("网络连接成功！");
    };

    //接收后台服务端的消息
    webSocket.onmessage = function (evt) {
        var received_msg = evt.data;
        console.log("数据已接收:" + received_msg);
        var obj = JSON.parse(received_msg);
        // var card1 = "";
        // var card2 = "";
        // var card3 = "";
        if (obj.messageType == 5 || obj.messageType == 6 || obj.messageType == 7) {
            if (obj.player != null) {
                player = obj.player
                if(div4_text == "隐藏") {
                    showCard()
                } else {
                    hiddenCard()
                }
                // div4_text = "隐藏";
                // document.getElementById("div_4").innerHTML = div4_text;

            }
        }
        console.log("可以解析成json:" + obj.messageType);
        //1代表上线 2代表下线 3代表在线名单 4代表普通消息
        if (obj.messageType == 1) {
            //把名称放入到selection当中供选择
            var onlineName = obj.username;
            var option = "<option>" + onlineName + "</option>";
            // $("#onLineUser").append(option);
            setMessageInnerHTML(onlineName + "上线了");
        } else if (obj.messageType == 2) {
            if (obj.username == "admin") {
                window.history.go(-1);
            }
            // $("#onLineUser").empty();
            var onlineName = obj.onlineUsers;
            var offlineName = obj.username;
            var option = "<option>" + "--所有--" + "</option>";
            for (var i = 0; i < onlineName.length; i++) {
                if (!(onlineName[i] == document.getElementById('username').value)) {
                    option += "<option>" + onlineName[i] + "</option>"
                }
            }
            // $("#onLineUser").append(option);

            setMessageInnerHTML(offlineName + "下线了");
        } else if (obj.messageType == 3) {
            var onlineName = obj.onlineUsers;
            var option = null;
            for (var i = 0; i < onlineName.length; i++) {
                if (!(onlineName[i] == document.getElementById('username').value)) {
                    option += "<option>" + onlineName[i] + "</option>"
                }
            }
            // $("#onLineUser").append(option);
            console.log("获取了在线的名单" + onlineName.toString());
        } else if (obj.messageType == 4) {
            setMessageInnerHTML(obj.fromusername + "对" + obj.tousername + "说：" + obj.textMessage);
        } else if (obj.messageType == 5) {
            document.getElementById("div_2").style.display = "block";
            document.getElementById("div_3").style.display = "block";
            document.getElementById("div_5").style.display = "block";
            let div_4 = document.getElementById("div_4");
            div_4.innerHTML = div4_text;
            div_4.style.display = "block";
            document.getElementById('message').innerHTML = "";
            setMessageInnerHTML("游戏开始：" + obj.fromusername + "进行了发牌");
            // setCard(card1 + "<br/>" + card2 + "<br/>" + card3);
        } else if (obj.messageType == 6) {
            // setCard(card1 + "<br/>" + card2 + "<br/>" + card3);
            setMessageInnerHTML(obj.textMessage);
        } else if (obj.messageType == 7) {
            // setCard(card1 + "<br/>" + card2 + "<br/>" + card3);
            setMessageInnerHTML(obj.textMessage);
        } else if (obj.messageType == 8) {
            setMessageInnerHTML(obj.fromusername + "对" + obj.tousername + "说：" + obj.textMessage);
        }
        messageArea.scrollTop = messageArea.scrollHeight;
    };

    //连接关闭的回调事件
    webSocket.onclose = function () {
        console.log("连接已关闭...");
        setMessageInnerHTML("连接已经关闭....");
    };
} else {
    // 浏览器不支持 WebSocket
    alert("您的浏览器不支持 WebSocket!");
}
if ("admin" == document.getElementById('username').value) {
    document.getElementById("div_1").style.display = "block";
}

//将消息显示在网页上
function setMessageInnerHTML(innerHTML) {
    document.getElementById('message').innerHTML += innerHTML + '<br/>';
}

//将牌显示到最上面
function setCard(value) {
    document.getElementById('card').innerHTML = value;
}

function closeWebSocket() {
    //直接关闭websocket的连接
    webSocket.close();
}

function getCards(card) {
    var flower = card.flower;
    var number = card.number;
    var pubCard = card.pubCard;
    var handCard = card.handCard;
    var str = "";
    switch (flower) {
        case 3:
            flower = "spades";
            break;
        case 2:
            flower = "hearts";
            break;
        case 1:
            flower = "clubs";
            break;
        default:
            flower = "diamonds";
            break;
    }
    number = getCardStringNumber(number);
    if (pubCard) {
        str = "公共牌：";
        console.log("flower:" + flower.toString() + ", number:" + number.toString())
        canvas1.drawPokerCard(0, 40, 100, flower.toString(), number.toString());
    } else if (handCard) {
        str = "手牌：";
        console.log("flower:" + flower.toString() + ", number:" + number.toString())
        canvas2.drawPokerCard(0, 40, 100, flower.toString(), number.toString());
    } else {
        str = "梦幻牌：";
        console.log("flower:" + flower.toString() + ", number:" + number.toString())
        canvas3.drawPokerCard(0, 40, 100, flower.toString(), number.toString());
    }

    return str
}

function getCardStringNumber(number) {
    if (number <= 10) {
        return "" + number;
    } else if (number === 11) {
        return "J";
    } else if (number === 12) {
        return "Q";
    } else if (number === 13) {
        return "K";
    } else {
        return "A";
    }

}

function flip() {
    if (div4_text === "查看") {
        if (player != null) {
            showCard()
            div4_text = "隐藏";
        }
    } else if (div4_text === "隐藏") {
        hiddenCard();
        div4_text = "查看";
    }
    document.getElementById("div_4").innerHTML = div4_text;
}
function showCard() {
    for (let i = 0; i < 3; i++) {
        getCards(player.cards[i]);
    }
}

function hiddenCard() {
    canvas1.drawPokerBack(0, 40, 100, '#b55', '#a22');
    canvas2.drawPokerBack(0, 40, 100, '#b55', '#a22');
    canvas3.drawPokerBack(0, 40, 100, '#b55', '#a22');
}


function send(type) {
    var selectText = $("#onLineUser").find("option:selected").text();
    if (type === 5) {
        selectText = "All";
    } else if (type === 6) {
        selectText = "";
    } else if (type === 7) {
        selectText = "";
    } else {
        type = 4
        if (selectText === "--所有--") {
            selectText = "All";

        } else {
            type = ''
            setMessageInnerHTML(document.getElementById('username').value + "对" + selectText + "说：" + $("#text").val());
        }
    }

    var message = {
        // "message": document.getElementById('text').value,
        "message": "",
        "room": document.getElementById('room').value,
        "username": document.getElementById('username').value,
        "to": selectText,
        "type": type
    };
    webSocket.send(JSON.stringify(message));
    $("#text").val("");

}