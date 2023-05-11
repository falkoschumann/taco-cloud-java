package tacos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HomePageBrowserTest {

  private static HtmlUnitDriver browser;

  @LocalServerPort private int port;

  @BeforeAll
  static void setup() {
    browser = new HtmlUnitDriver();
    browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
  }

  @AfterAll
  static void teardown() {
    browser.quit();
  }

  @Test
  void testHomePage() {
    var homePage = "http://localhost:" + port;
    browser.get(homePage);

    var titleText = browser.getTitle();
    assertEquals("Taco Cloud", titleText);

    var h1Text = browser.findElement(new ByTagName("h1")).getText();
    assertEquals("Welcome to...", h1Text);

    var imgSrc = browser.findElement(new ByTagName("img")).getAttribute("src");
    assertEquals(homePage + "/images/TacoCloud.png", imgSrc);
  }
}
