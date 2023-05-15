package tacos.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tacos.Ingredient;

@Repository
public class JdbcIngredientRepository implements IngredientRepository {
  private final JdbcTemplate jdbc;

  @Autowired
  public JdbcIngredientRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public Collection<Ingredient> findAll() {
    return jdbc.query("SELECT id, name, type FROM ingredients", this::mapRowToIngredient);
  }

  @Override
  public Ingredient findById(String id) {
    return jdbc.queryForObject(
        "SELECT id, name, type FROM ingredients WHERE id=?", this::mapRowToIngredient, id);
  }

  @Override
  public Ingredient save(Ingredient ingredient) {
    jdbc.update(
        "INSERT INTO ingredients (id, name, type) VALUES (?, ?, ?)",
        ingredient.getId(),
        ingredient.getName(),
        ingredient.getType().toString());
    return ingredient;
  }

  private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {
    return new Ingredient(
        rs.getString("id"), rs.getString("name"), Ingredient.Type.valueOf(rs.getString("type")));
  }
}
