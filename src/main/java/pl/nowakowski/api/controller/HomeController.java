package pl.nowakowski.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@AllArgsConstructor
public class HomeController {

    static final String HOME = "/";
    static final String LOGIN = "/login";

    @RequestMapping(value = HOME, method = RequestMethod.GET)
    public String homePage() {
        return "home";
    }
    
    @GetMapping(value = LOGIN)
    public String loginPage() {
        return "login";
    }
}
