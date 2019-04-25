package demo.msa.rpc.hello.client;

import demo.msa.rpc.client.RpcClient;
import demo.msa.rpc.hello.api.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication(scanBasePackages = "demo.msa.rpc")
public class HelloClientApplication {

  @Autowired
  private RpcClient rpcClient;

  @PostConstruct
  public void run() {
    HelloService helloService = rpcClient.create(HelloService.class);
    System.out.println(helloService.say("world"));
    System.out.println(helloService.list());
    System.out.println(helloService.map());
    System.out.println(helloService.bean());
//    for (int i = 0; i < 1000; i++) {
//      System.out.println(helloService.say("world"));
//    }
  }

  public static void main(String[] args) {
    SpringApplication.run(HelloClientApplication.class, args).close();
  }
}
