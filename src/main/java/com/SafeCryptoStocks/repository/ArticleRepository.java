package com.SafeCryptoStocks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SafeCryptoStocks.model.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
