package demo.msa.rpc.hello.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "demo.msa.rpc")
public class HelloServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(HelloServerApplication.class, args);
  }
}
