package demo.msa.rpc.server;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC 服务注解（标注在服务实现类上）
 *
 * @author huangyong
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface RpcService {

  /**
   * 服务接口类
   */
  Class<?> value();
}
