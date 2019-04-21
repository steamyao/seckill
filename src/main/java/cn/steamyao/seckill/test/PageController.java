package cn.steamyao.seckill.test;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.web
 * @date 2019/4/12 19:37
 * @description   测试模板引擎    @RestController 会直接返回test  不返回对应的页面
 */
@Controller
public class PageController {

    @RequestMapping("/test/page")
    public String index(ModelMap map) {
        // 加入一个属性，用来在模板中读取
        map.addAttribute("host", "http://blog.didispace.com");
        // return模板文件的名称，对应src/main/resources/templates/index.html
        return "test";
    }
}
