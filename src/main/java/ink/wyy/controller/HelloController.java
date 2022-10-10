package ink.wyy.controller;

import ink.wyy.bean.APIResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public APIResult hello(String name) {
        return APIResult.createOk(name);
    }
}
