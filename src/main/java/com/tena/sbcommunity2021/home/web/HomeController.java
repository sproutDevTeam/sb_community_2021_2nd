package com.tena.sbcommunity2021.home.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class HomeController {

	@GetMapping
	@ResponseBody
	public String home() {
		return "SPRING_BOOT_COMMUNITY_2021_2ND";
	}

}
