/**
 * 
 */
package cn.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.dao.ItemsMapper;
import cn.pojo.Items;
import cn.pojo.ItemsExample;
import cn.service.ItemService;

/**
 * @author xxq_1
 *
 */
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemsMapper itemsMapper;
	
	/* (non-Javadoc)
	 * @see cn.service.ItemService#getItemList()
	 */
	@Override
	public List<Items> getItemList() {
		// TODO Auto-generated method stub
		
		ItemsExample example = new ItemsExample();
		List<Items> list = itemsMapper.selectByExampleWithBLOBs(example);
		return list;
	}

}
