package com.example.article;

import com.example.article.dto.ArticleDto;
import com.example.article.entity.Article;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// Pageable
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service  // 비즈니스 로직을 담당하는 클래스
@RequiredArgsConstructor
public class ArticleService {
  private final ArticleRepository repository;

  // CREATE
  public ArticleDto create(ArticleDto dto) {
    Article newArticle = new Article(
            dto.getTitle(),
            dto.getContent(),
            dto.getWriter()
    );
//        newArticle = repository.save(newArticle);
//        return ArticleDto.fromEntity(newArticle);

    return ArticleDto.fromEntity(repository.save(newArticle));
  }

  // READ ALL
  public List<ArticleDto> readAll() {
    List<ArticleDto> articleList = new ArrayList<>();
    // 여기에 모든 게시글을 리스트로 정리해서 전달
    List<Article> articles = repository.findAll();
    for (Article entity: articles) {
        articleList.add(ArticleDto.fromEntity(entity));
    }
    return articleList;
  }

  // READ ONE
  public ArticleDto readOne(Long id) {
    Optional<Article> optionalArticle = repository.findById(id);
    // 해당하는 Article이 있었다.
    if (optionalArticle.isPresent()) {
        Article article = optionalArticle.get();
        return ArticleDto.fromEntity(article);
    }
    // 없으면 예외를 발생시킨다.
    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  }

  // UPDATE
  public ArticleDto update(Long id, ArticleDto dto) {
    Optional<Article> optionalArticle = repository.findById(id);
    if (optionalArticle.isPresent()) {
        Article targetEntity = optionalArticle.get();
        targetEntity.setTitle(dto.getTitle());
        targetEntity.setContent(dto.getContent());
        targetEntity.setWriter(dto.getWriter());
        return ArticleDto.fromEntity(repository.save(targetEntity));
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  }

  // DELETE
  public void delete(Long id) {
    if (repository.existsById(id))
        repository.deleteById(id);
    else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  }

  // JPA Query Method
  // 위에 있는 20개를 반환하는 것이므로 페이지네이션을 구현했다고 보긴 어렵다.
  // JPA에선 offset을 지원하지 않으므로 2페이지를 보기엔 어렵다.
  // 페이지 단위를 구분하기 힘들다.
  // 마지막으로 확인한 게시글의 ID를 바탕으로 조회해야 한다는 단점.
  public List<ArticleDto> readTop20() {
     List<ArticleDto> articleDtoList = new ArrayList<>();
     List<Article> articleList = repository.findTop20ByOrderByIdDesc();

     for (Article entity: articleList) {
       articleDtoList.add(ArticleDto.fromEntity(entity));
     }

     return articleDtoList;
  }

  // Pageable을 사용해서, List로 반환
  public List<ArticleDto> readArticlePagedList(
    Integer pageNubmer,
    Integer pageSize
  ) {
    // PagindAndSoringRepository의 findAll에 인자를 전달함으로써
    // 조회하고 싶은 페이지와, 각 페이지 별 개수를 조정해서 조회하는 것을 도와주는 객체
    // 0번이 제일 앞페이지다.
    Pageable pageable = PageRequest.of(0, 20);
    // Page<Article>: pageable을 전달해서 받은 결과를 정리해둔 객체
    // findAll은 인자를 안넣어줘도 되고 넣어줘도 된다. <- 메서드 오버로딩
    Page<Article> articlePage = repository.findAll(pageable);
    // 결과 반환 준비
    List<ArticleDto> articleDtoList = new ArrayList<>();
    for (Article entity: articlePage.getContent()) { // getContent가 없어도 된다. 명시적으로 표현하기 위해 표기했다.
      articleDtoList.add(ArticleDto.fromEntity(entity));
    }

    return articleDtoList;
  }

  // Pageable을 사용해서 Page<Entity>를 Page<Dto>로 변환 후
  // 모든 정보 활용 (Page 객체에만 있는 정보를 활용하고 싶을 때!)
  public Page<ArticleDto> readArticlePaged(
    Integer pageNum,
    Integer pageSize
  ) {
    Pageable pageable = PageRequest.of(
      pageNum, pageSize, Sort.by("id").descending()
    );
    Page<Article> articlePages = repository.findAll(pageable);
    // map method: Page의 각 데이터(Entity)를 인자로,
    // 특정 메서드를 실행한 후
    // 해당 메서드 실행 결과를 모아서
    // 새로운 Page 객체를, 만약 반환형이 바뀐다면 타입을 바꿔서 반환한다.
    Page<ArticleDto> articleDtoPages
      // = articlePages.map(entity -> ArticleDto.fromEntity(entity));
    = articlePages.map(ArticleDto::fromEntity);

    return articleDtoPages;
  }
}







