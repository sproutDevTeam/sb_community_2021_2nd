package com.tena.sbcommunity2021.articles.domain;

import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"})
@ToString
@Getter
public class Article {

	private Long id;

	private String title;

	private String content;

	private LocalDateTime regDate;

	private LocalDateTime updateDate;

	@Builder
	public Article(String title, String content) {
		this.title = title;
		this.content = content;
	}

	// TODO ModelMapper 로 대체할 것
	public void updateArticle(ArticleDto.Save saveDto) {
		this.title = saveDto.getTitle();
		this.content = saveDto.getContent();
		this.updateDate = LocalDateTime.now();
	}

}


/**
 * 일단은 updateArticle 메서드를 제거하고, ModelMapper 를 사용하려고 한다.
 *  1. 애초에 메서드를 둔 목적은 다음과 같다.
 *   - 도메인 객체 변경 시, 다른 레이어에서의 무분별한 Setter 남용을 피하고 메서드를 통해 의도를 명확히 드러내기 위해서
 *   - 객체의 변경은 객체 자기 자신에게 책임을 두어 일관성을 보장하기 위해서
 *  2. 객체지향에서 중요한 것들이 많지만 그 중 하나가 객체 본인의 책임을 다하는 것이다.
 * 	 - 도메인 객체는 특히 모든 레이어에서 사용하는 객체이므로 올바른 책임을 제공해 주어야 한다.
 * 	 - 객체가 자기 자신의 책임을 다하지 않으면 그 책임은 다른 객체에게 넘어가게 된다.
 * 	 - 가령 도메인 객체 변경 시 Service 레이어의 로직 내에서 Setter 를 사용해 객체의 값을 변경하는 경우를 예로 들 수 있다.
 * 	 - 도메인 객체에 기본적인 Getter, Setter 외에 메서드를 작성하지 않는다면, 객체 자신의 책임을 다할 수 없으므로 그 책임들이 다른 객체(레이어)에서 이뤄진다.
 * 	 - 이때 다른 객체(레이어)에서는 도메인 객체의 속사정을 모두 알고 있다는 가정 하에 로직을 작성하게 된다.
 * 	 - 또한 도메인 객체는 자기 자신의 일 임에도 다른 객체가 시키는대로 따르는 종속적인 입장이 된다.
 * 	 - 즉 캡슐화의 의미가 퇴색된다.
 * 	 - 이렇듯 무분별한 Setter 의 사용은 객체를 누구나 언제 어디서든 변경할 수 있는 상태가 되므로 객체의 안전성을 보장받기 힘들다.
 * 	 - 또한 Setter 를 사용한 객체의 변경은 의도가 분명히 드러나지 않기 때문에 지양해야 한다.
 * 	 3. 하지만 사실 ORM 방식의 JPA 처럼 Entity 로 관리되는 도메인 객체가 아닌 단순 쿼리 맵핑을 위한 VO 의 용도가 짙기 때문에
 * 	 Rich Object 로 두기보단 간편히 ModelMapper 를 사용하는 것이 나을 것 같다는 판단이 든다.
 * 	 테스트 작성 시에도 난잡한 메서드 목킹 등 제반사항이 더해져 훨씬 귀찮은 상황이다.
 */