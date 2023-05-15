package tacos.data;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import tacos.Ingredient;
import tacos.Taco;

@Repository
public class JdbcTacoRepository implements TacoRepository {
  private final JdbcTemplate jdbc;

  public JdbcTacoRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public Taco save(Taco taco) {
    var tacoId = saveTacoInfo(taco);
    taco.setId(tacoId);
    taco.getIngredients().forEach(i -> saveIngredientToTaco(i, tacoId));
    return taco;
  }

  private long saveTacoInfo(Taco taco) {
    taco.setCreatedAt(LocalDateTime.now());
    PreparedStatementCreatorFactory pscFactory =
        new PreparedStatementCreatorFactory(
            "INSERT INTO tacos (name, created_at) VALUES (?, ?)", Types.VARCHAR, Types.TIMESTAMP);
    pscFactory.setGeneratedKeysColumnNames("id");
    var psc = pscFactory.newPreparedStatementCreator(List.of(taco.getName(), taco.getCreatedAt()));
    var keyHolder = new GeneratedKeyHolder();
    jdbc.update(psc, keyHolder);
    return keyHolder.getKey().longValue();
  }

  private void saveIngredientToTaco(Ingredient ingredient, long tacoId) {
    jdbc.update(
        "INSERT INTO taco_ingredients (taco, ingredient) VALUES (?, ?)",
        tacoId,
        ingredient.getId());
  }
}
