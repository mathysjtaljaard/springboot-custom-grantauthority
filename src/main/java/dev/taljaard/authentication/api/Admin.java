package dev.taljaard.authentication.api;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class Admin {

  @GetMapping
  public String adminHome() {
    return "hello admin";
  }

  @GetMapping("/user")
  public Principal principal(Principal principal) {
    return principal;
  }

  @GetMapping("/joker")
  public String onlyJokers() {
    return "welcome Joker";
  }

  @GetMapping("/sudo")
  public String superAdmin() {
    return "Welcome Sudo";
  }

  @GetMapping("/wtf")
  public String wtf() {
    return "This Should not work WTF";
  }
}
