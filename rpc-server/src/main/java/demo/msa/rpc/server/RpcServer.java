package demo.msa.rpc.server;

import demo.msa.rpc.common.bean.RpcRequest;
import demo.msa.rpc.common.bean.RpcResponse;
import demo.msa.rpc.common.codec.RpcDecoder;
import demo.msa.rpc.common.codec.RpcEncoder;
import demo.msa.rpc.common.util.CollectionUtil;
import demo.msa.rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * RPC 服务器（用于发布 RPC 服务）
 *
 * @author huangyong
 * @since 1.0.0
 */
@Component
public class RpcServer implements ApplicationContextAware, InitializingBean {

  private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

  @Value("${rpc.port}")
  private int port;

  @Autowired
  private ServiceRegistry serviceRegistry;

  /**
   * 存放 服务名称 与 服务实例 之间的映射关系
   */
  private Map<String, Object> handlerMap = new HashMap<>();

  @Override
  public void setApplicationContext(ApplicationContext ctx) throws BeansException {
    // 扫描带有 @RpcService 注解的服务类
    Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
    if (CollectionUtil.isNotEmpty(serviceBeanMap)) {
      for (Object serviceBean : serviceBeanMap.values()) {
        RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
        String serviceName = rpcService.value().getName();
        handlerMap.put(serviceName, serviceBean);
      }
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup childGroup = new NioEventLoopGroup();
    try {
      // 启动 RPC 服务
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(bossGroup, childGroup);
      bootstrap.channel(NioServerSocketChannel.class);
      bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
        @Override
        public void initChannel(SocketChannel channel) throws Exception {
          ChannelPipeline pipeline = channel.pipeline();
          pipeline.addLast(new RpcDecoder(RpcRequest.class));  // 解码 RPC 请求
          pipeline.addLast(new RpcEncoder(RpcResponse.class)); // 编码 RPC 响应
          pipeline.addLast(new RpcServerHandler(handlerMap));  // 处理 RPC 请求
        }
      });
      ChannelFuture future = bootstrap.bind(port).sync(); // 同步方式，等待RPC服务器启动完毕后，才执行后续代码
      logger.debug("server started, listening on {}", port);
      // 注册 RPC 服务地址
      String serviceAddress = InetAddress.getLocalHost().getHostAddress() + ":" + port;
      for (String interfaceName : handlerMap.keySet()) {
        serviceRegistry.register(interfaceName, serviceAddress);
        logger.debug("register service: {} => {}", interfaceName, serviceAddress);
      }
      // 释放资源
      future.channel().closeFuture().sync();
    } catch (Exception e) {
      logger.error("server exception", e);
    } finally {
      // 关闭 RPC 服务
      childGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }
}
