package lottery.domains.content.dao.read;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserTransfers;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserTransfersReadDao {
	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);
}