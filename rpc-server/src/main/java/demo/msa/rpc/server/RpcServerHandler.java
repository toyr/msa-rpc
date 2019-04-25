package demo.msa.rpc.server;

import demo.msa.rpc.common.bean.RpcRequest;
import demo.msa.rpc.common.bean.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * RPC 服务端处理器（用于处理 RPC 请求）
 *
 * @author huangyong
 * @since 1.0.0
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

  private static final Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);

  /**
   * 存放 服务名称 与 服务实例 之间的映射关系
   */
  private final Map<String, Object> handlerMap;

  public RpcServerHandler(Map<String, Object> handlerMap) {
    this.handlerMap = handlerMap;
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
    // 创建 RPC 响应对象
    RpcResponse response = new RpcResponse();
    response.setRequestId(request.getRequestId());
    try {
      // 处理 RPC 请求成功
      Object result = handle(request);
      response.setResult(result);
    } catch (Exception e) {
      // 处理 RPC 请求失败
      response.setException(e);
      logger.error("handle result failure", e);
    }
    // 写入 RPC 响应对象（写入完毕后立即关闭与客户端的连接）
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error("server caught exception", cause);
    ctx.close();
  }

  private Object handle(RpcRequest request) throws Exception {
    // 获取服务实例
    String serviceName = request.getInterfaceName();
    Object serviceBean = handlerMap.get(serviceName);
    if (serviceBean == null) {
      throw new RuntimeException(String.format("can not find service bean by key: %s", serviceName));
    }
    // 获取反射调用所需的变量
    Class<?> serviceClass = serviceBean.getClass();
    String methodName = request.getMethodName();
    Class<?>[] parameterTypes = request.getParameterTypes();
    Object[] parameters = request.getParameters();
    // 执行反射调用
    Method method = serviceClass.getMethod(methodName, parameterTypes);
    method.setAccessible(true);
    return method.invoke(serviceBean, parameters);
  }
}
