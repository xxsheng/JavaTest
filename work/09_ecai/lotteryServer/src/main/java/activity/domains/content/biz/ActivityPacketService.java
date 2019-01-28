// package activity.domains.content.biz;
//
// import java.util.List;
//
// import activity.domains.content.entity.ActivityPacketBill;
// import activity.domains.content.entity.ActivityPacketInfo;
//
// public interface ActivityPacketService {
//
// 	/**
// 	 * 1,查询当前可以抢的红包
// 	 * 2,一个红包下面有N份
// 	 * 3,一个用户IP一个红包一天只能抢一次
// 	 * @return
// 	 */
// 	public List<ActivityPacketInfo> getAvaliablePackets(int userId, String ip);
//
// 	/**
// 	 * 抢到一个红包
// 	 * @param packet
// 	 * @return
// 	 */
// 	public ActivityPacketBill doGrabPacket(ActivityPacketInfo packetInfo, int userId, String ip);
//
// 	/**
// 	 * 发送红包
// 	 * @param count
// 	 * @param amount
// 	 * @param userId
// 	 * @return
// 	 */
// 	public boolean doSendPacket(int count, double amount, int userId);
//
// 	/**
// 	 * 获取红包信息
// 	 * @param id
// 	 * @return
// 	 */
// 	public ActivityPacketInfo get(int id);
//
// 	/**
// 	 * 一个红包每个ip一天只能抢一次
// 	 * @param ip
// 	 * @return
// 	 */
// 	public ActivityPacketInfo validateIp(int packetId, String ip, List<ActivityPacketInfo> list);
//
// 	/**
// 	 * 领取红包需要检查流水是否满足
// 	 * 成功返回 -1 失败返回 最小金额
// 	 * @return
// 	 */
// 	public double validateCost(int userId);
//
// }