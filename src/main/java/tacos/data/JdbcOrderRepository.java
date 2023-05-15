package tacos.data;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import tacos.Order;
import tacos.Taco;

@Repository
public class JdbcOrderRepository implements OrderRepository {
  private final SimpleJdbcInsert orderInserter;
  private final SimpleJdbcInsert orderTacoInserter;

  @Autowired
  public JdbcOrderRepository(JdbcTemplate jdbc) {
    orderInserter =
        new SimpleJdbcInsert(jdbc).withTableName("taco_orders").usingGeneratedKeyColumns("id");
    orderTacoInserter = new SimpleJdbcInsert(jdbc).withTableName("taco_order_tacos");
  }

  @Override
  public Order save(Order order) {
    order.setPlacedAt(LocalDateTime.now());
    var orderId = saveOrderDetails(order);
    order.setId(orderId);
    order.getTacos().forEach(taco -> saveTacoToOrder(taco, orderId));
    return order;
  }

  private long saveOrderDetails(Order order) {
    var args = new LinkedHashMap<String, Object>();
    args.put("id", order.getId());
    args.put("delivery_name", order.getDeliveryName());
    args.put("delivery_street", order.getDeliveryStreet());
    args.put("delivery_city", order.getDeliveryCity());
    args.put("delivery_state", order.getDeliveryState());
    args.put("delivery_zip", order.getDeliveryZip());
    args.put("cc_number", order.getCcNumber());
    args.put("cc_expiration", order.getCcExpiration());
    args.put("cc_cvv", order.getCcCvv());
    args.put("placed_at", order.getPlacedAt());
    return orderInserter.executeAndReturnKey(args).longValue();
  }

  private void saveTacoToOrder(Taco taco, long orderId) {
    orderTacoInserter.execute(Map.of("taco_order", orderId, "taco", taco.getId()));
  }
}
