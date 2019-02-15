package com.warm.share.mapper;

import com.warm.share.entity.Article;
import com.warm.share.model.ArticleUser;

import java.util.List;

public interface ArticleUserMapper {

    ArticleUser getArticleUserBy(long articleId);

    int insertArticles(List<Article> articles);


    List<ArticleUser> getHotArticles(int pageNum, int pageSize);


}
