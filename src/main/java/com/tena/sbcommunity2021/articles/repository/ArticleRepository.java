package com.tena.sbcommunity2021.articles.repository;

import com.tena.sbcommunity2021.articles.domain.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ArticleRepository {

	String FIND_ALL_SQL = "SELECT * FROM article";
	String FIND_BY_ID_SQL = "SELECT * FROM article WHERE id = #{id}";
	String SAVE_SQL = "INSERT INTO article SET regDate = NOW(), updateDate = NOW(), title = #{title}, content = #{content}";
	String UPDATE_SQL = "UPDATE article SET title = #{title}, content = #{content}, updateDate = #{updateDate} WHERE id = #{id}";
	String DELETE_BY_ID_SQL = "DELETE FROM article WHERE id = #{id}";
	String DELETE_ALL_SQL = "DELETE FROM article";

	@Select(FIND_ALL_SQL)
	List<Article> findAll();

	@Select(FIND_BY_ID_SQL)
	Optional<Article> findById(Long id);

	// Insert 시 자동 생성된 PK 맵핑 : @Optional 또는 @SelectKey 방식 중 택 1
	// @Options(useGeneratedKeys = true, keyProperty = "id")
	@SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = long.class)
	@Insert(SAVE_SQL)
	void save(Article article);

	@Update(UPDATE_SQL)
	void update(Article article);

	@Delete(DELETE_BY_ID_SQL)
	void deleteById(Long id);

	@Delete(DELETE_ALL_SQL)
	void deleteAll();

}