package tacos.web;

import jakarta.validation.Valid;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.Order;
import tacos.Taco;
import tacos.data.IngredientRepository;
import tacos.data.TacoRepository;

@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {
  private final IngredientRepository ingredientRepo;
  private final TacoRepository designRepo;

  @Autowired
  public DesignTacoController(IngredientRepository ingredientRepo, TacoRepository designRepo) {
    this.ingredientRepo = ingredientRepo;
    this.designRepo = designRepo;
  }

  @ModelAttribute(name = "order")
  public Order order() {
    return new Order();
  }

  @ModelAttribute(name = "taco")
  public Taco taco() {
    return new Taco();
  }

  @GetMapping
  public String showDesignForm(Model model) {
    var ingredients = ingredientRepo.findAll();
    for (var type : Ingredient.Type.values()) {
      model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
    }
    return "design";
  }

  @PostMapping
  public String processDesign(@Valid Taco design, Errors errors, @ModelAttribute Order order) {
    if (errors.hasErrors()) {
      return "design";
    }

    var saved = designRepo.save(design);
    order.addDesign(saved);

    return "redirect:/orders/current";
  }

  private Collection<Ingredient> filterByType(Collection<Ingredient> ingredients, Type type) {
    return ingredients.stream().filter(x -> x.getType().equals(type)).toList();
  }
}
