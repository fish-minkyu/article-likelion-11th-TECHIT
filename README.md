# Query Parameter & Pagination

- 2024.01.22`10주차`

지난 9주차의 Query Parameter와 Pagination을 복습하기 위한 프로젝트다.  
해당 프로젝트의 핵심 코드들은 `9주차, article` 프로젝트의 일부분이다.(`article-likelion-9th-TECHIT` 참고)  
Query Parameter와 Pagination 로직이 구현되어 있다.

## 스팩

- Spring Boot 3.2.1
- Spring Web
- Spring Boot Data JPA
- SQLite

## Key Point

[Query Parameter](/src/main/java/com/example/article/QueryController.java)
```java
  // Query Parameter
  // 어떠한 기준을 받을 것인지 RequestParam으로 넣어줄 수 있다.
  // GET /query-example?query=keyword&limit=20 HTTP/1.1
  @GetMapping("/query-example")
  public String queryParams(
    @RequestParam("query")
    String query,
    // 받을 자료형 선택 가능
    // 만약 변환 불가일 경우, Bad Request(400)
    // Ex) Integer limit이지만 limit=3.5로 줄 경우, Bad Request
    @RequestParam("limit")
    Integer limit,
    // required
    // : 반드시 포함해야 하는지 아닌지를 required로 정의 가능
    @RequestParam(value = "notReq", required = false)
    String notRequired, // value값과 param이 일치하여야 한다. notReq="test"
    // defaultValue
    // : 기본값 설정을 원한다면 defaultValue
    // 값은 반드시 문자열로 들어간다.
    @RequestParam(value = "default", defaultValue = "hello")
    String defaultVal
  ) {
    log.info("query: " + query);
    log.info("limit: " + limit);
    log.info("notRequired: " + notRequired);
    log.info("default: " + defaultVal);

    return "done";
  }
```
\
\
[Search Query](/src/main/java/com/example/article/QueryController.java)
```java
  // 간단한 Search Query
  // GET /query-search?q=keyword&cat=writer
  @GetMapping("/query-search")
  public String querySearch(
    @RequestParam("q")
    String keyword,
    @RequestParam(value = "cat", defaultValue = "title")
    String category
  ) {
    log.info("keyword: " + keyword);
    log.info("category: " + category);
    return "done";
  }
```

\
\
`Query Method로 Pagination 흉내내기`  
[Query Method - ArticleRepository](/src/main/java/com/example/article/ArticleRepository.java)
```java
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
```
[Query Method - ArticleService](/src/main/java/com/example/article/ArticleService.java)
```java
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
```
\
\
`pagination`  
[Pagination - ArticleService](/src/main/java/com/example/article/ArticleService.java)
```java
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
```

## 복습
~~2024.01.22 복습 완료~~
