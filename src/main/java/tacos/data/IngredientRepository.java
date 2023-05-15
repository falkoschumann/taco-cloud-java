package tacos.data;

import java.util.Collection;
import tacos.Ingredient;

public interface IngredientRepository {
  Collection<Ingredient> findAll();

  Ingredient findById(String id);

  Ingredient save(Ingredient ingredient);
}
