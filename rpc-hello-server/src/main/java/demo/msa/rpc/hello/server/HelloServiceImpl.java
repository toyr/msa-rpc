package demo.msa.rpc.hello.server;

import demo.msa.rpc.hello.api.Bean;
import demo.msa.rpc.hello.api.HelloService;
import demo.msa.rpc.server.RpcService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RPC 接口实现
 *
 * @author huangyong
 * @since 1.0.0
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

  @Override
  public String say(String name) {
    return "hello " + name;
  }

  @Override
  public List<String> list() {
    List<String> list = new ArrayList<>();
    list.add("a");
    list.add("b");
    return list;
  }

  @Override
  public Map<String, Object> map() {
    Map<String, Object> map = new HashMap<>();
    map.put("a", 1);
    map.put("b", 2);
    return map;
  }

  @Override
  public Bean bean() {
    Bean bean = new Bean();
    bean.setFoo("a");
    bean.setBar(1);
    return bean;
  }
}
