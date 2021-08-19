package com.scritorrelo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private DatabaseManager dbManager;

    @GetMapping("/audios")
    public ModelAndView getAllAudios() {

        ModelAndView mav = new ModelAndView("list-audios");
        mav.addObject("audios", dbManager.getAllAudios());
        return mav;

    }
}
