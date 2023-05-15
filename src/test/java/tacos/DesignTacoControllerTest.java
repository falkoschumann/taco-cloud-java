package tacos;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tacos.Ingredient.*;
import tacos.data.IngredientRepository;
import tacos.data.OrderRepository;
import tacos.data.TacoRepository;
import tacos.web.DesignTacoController;

@WebMvcTest(DesignTacoController.class)
class DesignTacoControllerTest {
  @Autowired private MockMvc mockMvc;

  private List<Ingredient> ingredients;

  private Taco design;

  @MockBean private IngredientRepository ingredientRepository;

  @MockBean private TacoRepository designRepository;

  @MockBean private OrderRepository orderRepository;

  @BeforeEach
  public void setup() {
    ingredients =
        List.of(
            new Ingredient("FLTO", "Flour Tortilla", Type.WRAP),
            new Ingredient("COTO", "Corn Tortilla", Type.WRAP),
            new Ingredient("GRBF", "Ground Beef", Type.PROTEIN),
            new Ingredient("CARN", "Carnitas", Type.PROTEIN),
            new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES),
            new Ingredient("LETC", "Lettuce", Type.VEGGIES),
            new Ingredient("CHED", "Cheddar", Type.CHEESE),
            new Ingredient("JACK", "Monterrey Jack", Type.CHEESE),
            new Ingredient("SLSA", "Salsa", Type.SAUCE),
            new Ingredient("SRCR", "Sour Cream", Type.SAUCE));

    when(ingredientRepository.findAll()).thenReturn(ingredients);

    when(ingredientRepository.findById("FLTO"))
        .thenReturn(new Ingredient("FLTO", "Flour Tortilla", Type.WRAP));
    when(ingredientRepository.findById("GRBF"))
        .thenReturn(new Ingredient("GRBF", "Ground Beef", Type.PROTEIN));
    when(ingredientRepository.findById("CHED"))
        .thenReturn(new Ingredient("CHED", "Cheddar", Type.CHEESE));

    design = new Taco();
    design.setName("Test Taco");

    design.setIngredients(
        List.of(
            new Ingredient("FLTO", "Flour Tortilla", Type.WRAP),
            new Ingredient("GRBF", "Ground Beef", Type.PROTEIN),
            new Ingredient("CHED", "Cheddar", Type.CHEESE)));
  }

  @Test
  public void testShowDesignForm() throws Exception {
    mockMvc
        .perform(get("/design"))
        .andExpect(status().isOk())
        .andExpect(view().name("design"))
        .andExpect(model().attribute("wrap", ingredients.subList(0, 2)))
        .andExpect(model().attribute("protein", ingredients.subList(2, 4)))
        .andExpect(model().attribute("veggies", ingredients.subList(4, 6)))
        .andExpect(model().attribute("cheese", ingredients.subList(6, 8)))
        .andExpect(model().attribute("sauce", ingredients.subList(8, 10)));
  }

  @Test
  public void processDesign() throws Exception {
    when(designRepository.save(design)).thenReturn(design);

    mockMvc
        .perform(
            post("/design")
                .content("name=Test+Taco&ingredients=FLTO,GRBF,CHED")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().stringValues("Location", "/orders/current"));
  }
}
