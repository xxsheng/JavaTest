package com.warm.share.service.impl;

import com.warm.share.entity.Article;
import com.warm.share.model.ArticleUser;
import com.warm.share.service.ArticleService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class ArticleServiceImplTest {

    ApplicationContext context;
    ArticleService articleService;

    @Before
    public void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
        articleService = (ArticleService) context.getBean("articleService");
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void insertArticle() {
        Article article = new Article();
        article.setTitle("这是第一篇文章");
        article.setContent("这是第一篇文章这是第一篇文章这是第一篇文章这是第一篇文章这是第一篇文章");
        article.setUserId(9l);
        Long id = articleService.insertArticle(article);
        System.out.println(id);
    }


    @Test
    public void insertArticles() {
        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Article article = new Article();
            article.setTitle("这是第一篇文章" + i);
            article.setContent(i + "这是第一篇文章这是第一篇文章这是第一篇文章这是第一篇文章这是第一篇文章");
            article.setUserId(9l);
            article.setReleaseTime(System.currentTimeMillis());
            articles.add(article);
        }
        int rowCount = articleService.insertArticles(articles);
        System.out.println(rowCount);
    }

    @Test
    public void selectArticle() {
        ArticleUser articleUser = articleService.selectArticle(1);
        System.out.println(articleUser);
    }


    @Test
    public void getHotArticles() {
        int pageC = 0;
        int pageS = 20;
        List<ArticleUser> articleUsers = articleService.getHotArticles(pageC * pageS, pageS);
        System.out.println(articleUsers);

    }

}