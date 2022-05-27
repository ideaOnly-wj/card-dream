package cn.tellsea.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class WebSocketController {

    @PostMapping("/login")
    public String webSocket(@RequestParam("username")String username,
                            @RequestParam("room")String room, Model model) {
        try {
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(room)) {
                return "index";
            }
            log.info("跳转到websocket的页面上");
            model.addAttribute("username", username);
            model.addAttribute("room", room);
            return "websocket";
        } catch (Exception e) {
            e.printStackTrace();
            log.info("跳转到websocket的页面上发生异常，异常信息是：" + e.getMessage());
            return "error";
        }
    }

}
