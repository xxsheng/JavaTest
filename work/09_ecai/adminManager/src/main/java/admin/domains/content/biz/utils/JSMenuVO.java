package admin.domains.content.biz.utils;

import java.util.LinkedList;
import java.util.List;

public class JSMenuVO {

	private String name;
	private String icon;
	private String link;
	private List<JSMenuVO> items = new LinkedList<>();
	
	public JSMenuVO() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public List<JSMenuVO> getItems() {
		return items;
	}

	public void setItems(List<JSMenuVO> items) {
		this.items = items;
	}

}