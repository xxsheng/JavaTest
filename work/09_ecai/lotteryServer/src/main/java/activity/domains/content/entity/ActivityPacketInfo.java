// package activity.domains.content.entity;
//
// import java.util.List;
// import java.util.concurrent.TimeUnit;
//
// import javax.persistence.Column;
// import javax.persistence.Entity;
// import javax.persistence.GeneratedValue;
//
// import static javax.persistence.GenerationType.IDENTITY;
//
// import javax.persistence.Id;
// import javax.persistence.Table;
//
// import lottery.domains.content.dao.ActivityPacketBillWriteDao;
// import lottery.domains.content.global.Database;
//
// /**
//  * ActivitySignBill entity. @author MyEclipse Persistence Tools
//  */
// @Entity
// @Table(name = "activity_packet_info", catalog = Database.name)
// public class ActivityPacketInfo implements java.io.Serializable {
//
// 	// Fields
//
// 	/**
// 	 *
// 	 */
// 	private static final long serialVersionUID = 1L;
// 	private int id;
// 	private int userId;
// 	private String time;
// 	private double amount;
// 	private int count;
// 	private int type;//红包类型0：系统红包 1：用户红包
// 	private int status;//红包状态 0可抢状态 1已抢完
//
// 	private static final String CACHE_PREFIX_PB = "_packet_bills_";//可用红包cache前缀
//
// 	private static final int CACHE_EXPIRE_SECONDS = 60 * 10;//cache过期时间
//
// 	public enum PacketType{
// 		SYSTEM_PACKET(0),//"系统红包"
// 		USER_PACKET(1);//"用户红包"
//
// 		private Integer type;
//
// 		private PacketType(int type){
// 			this.type = type;
// 		}
//
// 		public int get(){
// 			return this.type;
// 		}
// 	}
//
// 	public enum PacketStatus{
// 		AVALIABLE(0),//"可抢状态"
// 		FINISH(1);//"已抢完
//
// 		private Integer status;
//
// 		private PacketStatus(int status){
// 			this.status = status;
// 		}
//
// 		public int get(){
// 			return this.status;
// 		}
// 	}
//
// 	// Constructors
//
// 	/** default constructor */
// 	public ActivityPacketInfo() {
// 	}
//
// 	/** full constructor */
//
//
// 	// Property accessors
// 	@Id
// 	@GeneratedValue(strategy = IDENTITY)
// 	@Column(name = "id", unique = true, nullable = false)
// 	public int getId() {
// 		return this.id;
// 	}
//
// 	public void setId(int id) {
// 		this.id = id;
// 	}
//
// 	@Column(name = "user_id", nullable = false)
// 	public int getUserId() {
// 		return this.userId;
// 	}
//
// 	public void setUserId(int userId) {
// 		this.userId = userId;
// 	}
//
// 	@Column(name = "amount", nullable = false)
// 	public double getAmount() {
// 		return amount;
// 	}
//
// 	public void setAmount(double amount) {
// 		this.amount = amount;
// 	}
//
// 	@Column(name = "count", nullable = false)
// 	public int getCount() {
// 		return count;
// 	}
//
// 	public void setCount(int count) {
// 		this.count = count;
// 	}
//
// 	@Column(name = "type", nullable = false)
// 	public int getType() {
// 		return type;
// 	}
//
// 	public void setType(int type) {
// 		this.type = type;
// 	}
//
// 	@Column(name = "status", nullable = false)
// 	public int getStatus() {
// 		return status;
// 	}
//
// 	public void setStatus(int status) {
// 		this.status = status;
// 	}
//
// 	@Column(name = "time", nullable = false, length = 19)
// 	public String getTime() {
// 		return this.time;
// 	}
//
// 	public void setTime(String time) {
// 		this.time = time;
// 	}
//
// 	/**
// 	 * 获取一个红包小面的所有份数对象
// 	 * @param dao
// 	 * @param redis
// 	 * @param useCache
// 	 * 		 是否使用缓存
// 	 * @return
// 	 */
// 	@SuppressWarnings("unchecked")
// 	public List<ActivityPacketBill> getPackets(ActivityPacketBillWriteDao dao, RedisTemplate<String, Object> redis, boolean useCache){
// 		if(useCache){
// 			List<ActivityPacketBill> cPackets = (List<ActivityPacketBill>) redis.opsForValue().get(CACHE_PREFIX_PB + this.getId());
// 			if(cPackets == null){
// 				return getPackets(dao, redis, false);
// 			}else{
// 				return cPackets;
// 			}
// 		}else{
// 			List<ActivityPacketBill> packets = dao.findByPacketId(this.getId());
// 			if(packets != null && !packets.isEmpty()){
// 				redis.opsForValue().set(CACHE_PREFIX_PB + this.getId(), packets);
// 				redis.expire(CACHE_PREFIX_PB + this.getId(), CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
// 			}
// 			return packets;
// 		}
// 	}
//
// 	/**
// 	 * 更新缓存
// 	 * @param redis
// 	 * @param cList
// 	 */
// 	public void refreshCache(RedisTemplate<String, Object> redis, List<ActivityPacketBill> cList){
// 		redis.opsForValue().set(CACHE_PREFIX_PB + this.getId(), cList);
// 		redis.expire(CACHE_PREFIX_PB + this.getId(), CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
// 	}
//
// 	public void deleteCache(RedisTemplate<String, Object> redis){
// 		redis.deleteAllByUpUserId(CACHE_PREFIX_PB + this.getId());
// 	}
// }