package com.tena.sbcommunity2021.demo;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(exclude = "password")
@Getter
class DemoAccount {
	private static int count = 0;

	private int id;
	private String username;
	private String password;

	@Builder
	public DemoAccount(String username, String password) {
		this.username = username;
		this.password = password;
		id = ++count;
	}

	public static DemoAccount of(String username, String password) {
		return DemoAccount.builder()
				.username(username)
				.password(password)
				.build();
	}

	public static List<DemoAccount> of(int size) {
		return IntStream.rangeClosed(1, size) // 1 <= range <= size
				.mapToObj(i -> DemoAccount.of(
						String.format("user%d", i),
						String.format("pass%d", i)))
				.collect(Collectors.toList());
	}

	public static void main(String[] args) {
		DemoAccount account = DemoAccount.of("user1", "pass1");
		DemoAccount account2 = DemoAccount.of("user2", "pass2");

		System.out.println("account = " + account);
		System.out.println("account2 = " + account2);

		List<DemoAccount> accounts = DemoAccount.of(10);

		System.out.println("accounts = " + accounts);
	}

}