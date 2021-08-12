package com.tena.sbcommunity2021.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/test")
public class DemoController {

	private int count;

	protected DemoController() {
		count = 0;
	}

	@RequestMapping("/getCount")
	@ResponseBody
	public int getCount() {
		return this.count;
	}

	@RequestMapping("/setCount")
	@ResponseBody
	public String setCount(int count) {
		this.count = count;
		return "count의 값이 " + this.count + "으로 초기화 되었습니다.";
	}

	@RequestMapping("/getString")
	@ResponseBody
	public String getString() {
		return "HI";
	}

	@RequestMapping("/getInt")
	@ResponseBody
	public int getInt() {
		return 10;
	}

	@RequestMapping("/getFloat")
	@ResponseBody
	public float getFloat() {
		return 10.5f;
	}

	@RequestMapping("/getDouble")
	@ResponseBody
	public double getDouble() {
		return 10.5;
	}

	@RequestMapping("/getBoolean")
	@ResponseBody
	public boolean getBoolean() {
		return true;
	}

	@RequestMapping("/getCharacter")
	@ResponseBody
	public char getCharacter() {
		return 'a';
	}

	@RequestMapping("/getMap")
	@ResponseBody
	public Map<String, Object> getMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("철수나이", 22);
		map.put("영희나이", 21);

		return map;
	}

	@RequestMapping("/getList")
	@ResponseBody
	public List<String> getList() {
		List<String> list = new ArrayList<>();
		list.add("철수");
		list.add("영희");

		return list;
	}

	@RequestMapping(value = "/getAccount")
	@ResponseBody
	public DemoAccount getAccount() {
		DemoAccount account = DemoAccount.of("user1", "pass1");

		return account;
	}

	@RequestMapping(value = "/getAccounts")
	@ResponseBody
	public List<DemoAccount> getAccounts() {
		List<DemoAccount> accounts = DemoAccount.of(10);

		return accounts;
	}

}



