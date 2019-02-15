package com.warm.share.service;

import com.warm.share.entity.Article;
import com.warm.share.model.ArticleUser;

import java.util.List;

public interface ArticleService {

    Long insertArticle(Article article);

    int insertArticles(List<Article> article);

    ArticleUser selectArticle(long articleId);

    List<ArticleUser> getHotArticles(int pageNum, int pageSize);


    List<ArticleUser> getHotArticles(int pageNum);


}
