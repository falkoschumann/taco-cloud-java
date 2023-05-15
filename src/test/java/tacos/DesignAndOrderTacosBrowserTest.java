package tacos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By.*;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DesignAndOrderTacosBrowserTest {
  private static HtmlUnitDriver browser;

  @LocalServerPort private int port;

  @Autowired TestRestTemplate rest;

  @BeforeAll
  static void setup() {
    browser = new HtmlUnitDriver();
    browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
  }

  @AfterAll
  static void closeBrowser() {
    browser.quit();
  }

  @Test
  void testDesignATacoPage_HappyPath() throws Exception {
    browser.get(homePageUrl());
    clickDesignATaco();
    assertDesignPageElements();
    buildAndSubmitATaco("Basic Taco", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");
    clickBuildAnotherTaco();
    buildAndSubmitATaco("Another Taco", "COTO", "CARN", "JACK", "LETC", "SRCR");
    fillInAndSubmitOrderForm();
    assertEquals(homePageUrl(), browser.getCurrentUrl());
  }

  @Test
  void testDesignATacoPage_EmptyOrderInfo() throws Exception {
    browser.get(homePageUrl());
    clickDesignATaco();
    assertDesignPageElements();
    buildAndSubmitATaco("Basic Taco", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");
    submitEmptyOrderForm();
    fillInAndSubmitOrderForm();
    assertEquals(homePageUrl(), browser.getCurrentUrl());
  }

  @Test
  void testDesignATacoPage_InvalidOrderInfo() throws Exception {
    browser.get(homePageUrl());
    clickDesignATaco();
    assertDesignPageElements();
    buildAndSubmitATaco("Basic Taco", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");
    submitInvalidOrderForm();
    fillInAndSubmitOrderForm();
    assertEquals(homePageUrl(), browser.getCurrentUrl());
  }

  //
  // Browser test action methods
  //
  private void buildAndSubmitATaco(String name, String... ingredients) {
    assertDesignPageElements();

    for (var ingredient : ingredients) {
      browser.findElement(new ByCssSelector("input[value='" + ingredient + "']")).click();
    }
    browser.findElement(new ByCssSelector("input#name")).sendKeys(name);
    browser.findElement(new ByCssSelector("form")).submit();
  }

  private void assertDesignPageElements() {
    assertEquals(designPageUrl(), browser.getCurrentUrl());
    var ingredientGroups = browser.findElements(new ByClassName("ingredient-group"));
    assertEquals(5, ingredientGroups.size());

    var wrapGroup = browser.findElement(new ByCssSelector("div.ingredient-group#wraps"));
    var wraps = wrapGroup.findElements(new ByTagName("div"));
    assertEquals(2, wraps.size());
    assertIngredient(wrapGroup, 0, "FLTO", "Flour Tortilla");
    assertIngredient(wrapGroup, 1, "COTO", "Corn Tortilla");

    var proteinGroup = browser.findElement(new ByCssSelector("div.ingredient-group#proteins"));
    var proteins = proteinGroup.findElements(new ByTagName("div"));
    assertEquals(2, proteins.size());
    assertIngredient(proteinGroup, 0, "GRBF", "Ground Beef");
    assertIngredient(proteinGroup, 1, "CARN", "Carnitas");

    var cheeseGroup = browser.findElement(new ByCssSelector("div.ingredient-group#cheeses"));
    var cheeses = proteinGroup.findElements(new ByTagName("div"));
    assertEquals(2, cheeses.size());
    assertIngredient(cheeseGroup, 0, "CHED", "Cheddar");
    assertIngredient(cheeseGroup, 1, "JACK", "Monterrey Jack");

    var veggieGroup = browser.findElement(new ByCssSelector("div.ingredient-group#veggies"));
    var veggies = proteinGroup.findElements(new ByTagName("div"));
    assertEquals(2, veggies.size());
    assertIngredient(veggieGroup, 0, "TMTO", "Diced Tomatoes");
    assertIngredient(veggieGroup, 1, "LETC", "Lettuce");

    var sauceGroup = browser.findElement(new ByCssSelector("div.ingredient-group#sauces"));
    var sauces = proteinGroup.findElements(new ByTagName("div"));
    assertEquals(2, sauces.size());
    assertIngredient(sauceGroup, 0, "SLSA", "Salsa");
    assertIngredient(sauceGroup, 1, "SRCR", "Sour Cream");
  }

  private void fillInAndSubmitOrderForm() {
    assertTrue(browser.getCurrentUrl().startsWith(orderDetailsPageUrl()));
    fillField("input#deliveryName", "Ima Hungry");
    fillField("input#deliveryStreet", "1234 Culinary Blvd.");
    fillField("input#deliveryCity", "Foodsville");
    fillField("input#deliveryState", "CO");
    fillField("input#deliveryZip", "81019");
    fillField("input#ccNumber", "4111111111111111");
    fillField("input#ccExpiration", "10/19");
    fillField("input#ccCvv", "123");
    browser.findElement(new ByCssSelector("form")).submit();
  }

  private void submitEmptyOrderForm() {
    assertEquals(currentOrderDetailsPageUrl(), browser.getCurrentUrl());
    browser.findElement(new ByCssSelector("form")).submit();

    assertEquals(orderDetailsPageUrl(), browser.getCurrentUrl());

    var validationErrors = getValidationErrorTexts();
    assertEquals(9, validationErrors.size());
    assertTrue(validationErrors.contains("Please correct the problems below and resubmit."));
    assertTrue(validationErrors.contains("Delivery name is required"));
    assertTrue(validationErrors.contains("Street is required"));
    assertTrue(validationErrors.contains("City is required"));
    assertTrue(validationErrors.contains("State is required"));
    assertTrue(validationErrors.contains("Zip code is required"));
    assertTrue(validationErrors.contains("Not a valid credit card number"));
    assertTrue(validationErrors.contains("Must be formatted MM/YY"));
    assertTrue(validationErrors.contains("Invalid CVV"));
  }

  private List<String> getValidationErrorTexts() {
    var validationErrorElements = browser.findElements(new ByClassName("validationError"));
    return validationErrorElements.stream().map(WebElement::getText).collect(Collectors.toList());
  }

  private void submitInvalidOrderForm() {
    assertTrue(browser.getCurrentUrl().startsWith(orderDetailsPageUrl()));
    fillField("input#deliveryName", "I");
    fillField("input#deliveryStreet", "1");
    fillField("input#deliveryCity", "F");
    fillField("input#deliveryState", "C");
    fillField("input#deliveryZip", "8");
    fillField("input#ccNumber", "1234432112344322");
    fillField("input#ccExpiration", "14/91");
    fillField("input#ccCvv", "1234");
    browser.findElement(new ByCssSelector("form")).submit();

    assertEquals(orderDetailsPageUrl(), browser.getCurrentUrl());

    var validationErrors = getValidationErrorTexts();
    assertEquals(4, validationErrors.size());
    assertTrue(validationErrors.contains("Please correct the problems below and resubmit."));
    assertTrue(validationErrors.contains("Not a valid credit card number"));
    assertTrue(validationErrors.contains("Must be formatted MM/YY"));
    assertTrue(validationErrors.contains("Invalid CVV"));
  }

  private void fillField(String fieldName, String value) {
    var field = browser.findElement(new ByCssSelector(fieldName));
    field.clear();
    field.sendKeys(value);
  }

  private void assertIngredient(
      WebElement ingredientGroup, int ingredientIdx, String id, String name) {
    var proteins = ingredientGroup.findElements(new ByTagName("div"));
    var ingredient = proteins.get(ingredientIdx);
    assertEquals(id, ingredient.findElement(new ByTagName("input")).getAttribute("value"));
    assertEquals(name, ingredient.findElement(new ByTagName("span")).getText());
  }

  private void clickDesignATaco() {
    assertEquals(homePageUrl(), browser.getCurrentUrl());
    browser.findElement(new ByCssSelector("a[id='design']")).click();
  }

  private void clickBuildAnotherTaco() {
    assertTrue(browser.getCurrentUrl().startsWith(orderDetailsPageUrl()));
    browser.findElement(new ByCssSelector("a[id='another']")).click();
  }

  //
  // URL helper methods
  //
  private String designPageUrl() {
    return homePageUrl() + "design";
  }

  private String homePageUrl() {
    return "http://localhost:" + port + "/";
  }

  private String orderDetailsPageUrl() {
    return homePageUrl() + "orders";
  }

  private String currentOrderDetailsPageUrl() {
    return homePageUrl() + "orders/current";
  }
}
