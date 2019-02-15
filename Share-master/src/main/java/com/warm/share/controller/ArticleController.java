package com.warm.share.controller;

import com.warm.share.entity.Article;
import com.warm.share.model.ArticleUser;
import com.warm.share.model.BaseModel;
import com.warm.share.model.Id;
import com.warm.share.service.ArticleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping(value = "/hotArticle")
    @ResponseBody
    public BaseModel<List<ArticleUser>> getHotArticle(@RequestParam Integer pageNum, @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        List<ArticleUser> articleUsers = articleService.getHotArticles(pageNum, pageSize);
        if (articleUsers != null) {
            return BaseModel.success(articleUsers);
        } else {
            return BaseModel.fail(300, "失败");
        }
    }

    @RequestMapping(value = "/release", method = RequestMethod.POST)
    @ResponseBody
    public Object releaseArticle(Article article) {
        return BaseModel.success(new Id(articleService.insertArticle(article)));
    }


}
