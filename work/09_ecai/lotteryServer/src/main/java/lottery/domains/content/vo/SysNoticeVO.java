package lottery.domains.content.vo;

public class SysNoticeVO {

	private int id;
	private String title;
	private String date;
	private String content;
	private String simpleContent;

	public SysNoticeVO(int id, String title, String date, String simpleContent) {
		this.id = id;
		this.title = title;
		this.date = date;
		this.simpleContent = simpleContent;
	}

	public SysNoticeVO(int id, String title, String date, String content, String simpleContent) {
		this.id = id;
		this.title = title;
		this.date = date;
		this.content = content;
		this.simpleContent = simpleContent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSimpleContent() {
		return simpleContent;
	}

	public void setSimpleContent(String simpleContent) {
		this.simpleContent = simpleContent;
	}
}