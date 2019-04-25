package demo.msa.rpc.hello.api;

import java.util.List;
import java.util.Map;

/**
 * RPC 接口
 *
 * @author huangyong
 * @since 1.0.0
 */
public interface HelloService {

  String say(String name);

  List<String> list();

  Map<String, Object> map();

  Bean bean();
}
