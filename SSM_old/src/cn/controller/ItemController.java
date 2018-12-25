/**
 * 
 */
package cn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.pojo.Items;
import cn.service.ItemService;

/**
 * @author xxq_1
 *
 */
@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/itemList")
    public ModelAndView getItemsList() {
        // 查询商品列表
        List<Items> itemList = itemService.getItemList();

        // 把查询结果传递给页面
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("itemList", itemList); // addObject方法相当于放到request域上
        // 设置逻辑视图
        modelAndView.setViewName("itemList"); 
        // 返回结果
        return modelAndView;
    }
}
