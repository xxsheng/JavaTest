package com.warm.share.service.impl;

import com.warm.share.entity.Article;
import com.warm.share.mapper.ArticleUserMapper;
import com.warm.share.mapper.gmapper.ArticleMapper;
import com.warm.share.model.ArticleUser;
import com.warm.share.service.ArticleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("articleService")
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleUserMapper articleUserMapper;

    @Override
    public Long insertArticle(Article article) {
        article.setReleaseTime(System.currentTimeMillis());
        articleMapper.insertSelective(article);
        return article.getId();
    }

    @Override
    public int insertArticles(List<Article> article) {
        return articleUserMapper.insertArticles(article);
    }


    @Override
    public ArticleUser selectArticle(long articleId) {
        return articleUserMapper.getArticleUserBy(articleId);
    }

    @Override
    public List<ArticleUser> getHotArticles(int pageNum, int pageSize) {
        return articleUserMapper.getHotArticles(pageNum, pageSize);
    }

    @Override
    public List<ArticleUser> getHotArticles(int pageNum) {
        return getHotArticles(pageNum, 20);
    }
}
