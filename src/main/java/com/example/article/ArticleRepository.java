package com.example.article;

import com.example.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
  // ID 순서대로 큰 최상위 20개
  // Top 옆에 숫자만큼 불러올 수 있다.
  List<Article> findTop20ByOrderByIdDesc();

  // ID를 내림차순으로, 인자로 받은 ID를 전달해주면
  // 그 이전 ID의 상위 20개를 반환해준다.
  // Ex. 1 ~ 60개의 ID 중 41번 ID 전달 받음
  //     => 21 ~ 40번까지의 ID를 반환해준다.
  // 페이지네이션보다 인피니티 스크롤을 구현할 때, 더 효율적인 경우가 있다.
  List<Article> findTop20ByIdLessThanOrderByIdDesc(Long id);
}
