package com.ll.spring_additional.boundedContext.question.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ll.spring_additional.boundedContext.question.entity.Question;
import com.ll.spring_additional.boundedContext.user.entity.SiteUser;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
	Page<Question> findAll(Pageable pageable);


	@Query("select "
		+ "distinct q "
		+ "from Question q "
		+ "left outer join SiteUser u1 on q.author=u1 "
		+ "left outer join Answer a on a.question=q "
		+ "left outer join SiteUser u2 on a.author=u2 "
		+ "where "
		+ "   q.subject like %:kw% "
		+ "   or q.content like %:kw% "
		+ "   or u1.username like %:kw% "
		+ "   or a.content like %:kw% "
		+ "   or u2.username like %:kw% ")
	Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable);

	Long countByAuthor(SiteUser author);

	List<Question> findTop5ByAuthorOrderByCreateDateDesc(SiteUser author);

	@Query("select "
		+ "distinct q "
		+ "from Question q "
		+ "left outer join SiteUser u1 on q.author=u1 "
		+ "left outer join Answer a on a.question=q "
		+ "left outer join SiteUser u2 on a.author=u2 "
		+ "where "
		+ "   (q.author.id = :authorId) "
		+ "   and ( "
		+ "       q.subject like %:kw% "
		+ "       or q.content like %:kw% "
		+ "       or a.content like %:kw% "
		+ "       or u2.username like %:kw% "
		+ "   )")
	Page<Question> findAllByKeywordAndAuthorId(@Param("kw") String kw, @Param("authorId") Long authorId, Pageable pageable);

	@Query("select "
		+ "distinct q "
		+ "from Question q "
		+ "left outer join SiteUser u1 on q.author = u1 "
		+ "left outer join q.answerList a "
		+ "left outer join SiteUser u2 on a.author = u2 "
		+ "where "
		+ "   (u2.id = :authorId) "
		+ "   and ( "
		+ "       q.subject like %:kw% "
		+ "       or q.content like %:kw% "
		+ "       or u1.username like %:kw% "
		+ "       or a.content like %:kw% "
		+ "       or u2.username like %:kw% "
		+ "   )")
	Page<Question> findAllByKeywordAndAnswer_AuthorId(@Param("kw") String kw, @Param("authorId") Long authorId, Pageable pageable);
}