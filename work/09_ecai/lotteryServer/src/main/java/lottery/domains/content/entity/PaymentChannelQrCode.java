package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Table(name = "payment_channel_qr_code", catalog = Database.name)
public class PaymentChannelQrCode implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private int channelId;
	private double money;
	private String qrUrlCode;


	public PaymentChannelQrCode() {
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "channel_id", nullable = false)
	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	@Column(name = "money", nullable = false, precision = 12, scale = 3)
	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}
	
	@Column(name = "qr_url_code")
	public String getQrUrlCode() {
		return qrUrlCode;
	}

	public void setQrUrlCode(String qrUrlCode) {
		this.qrUrlCode = qrUrlCode;
	}
}