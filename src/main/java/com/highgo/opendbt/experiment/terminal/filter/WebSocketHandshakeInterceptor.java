package com.highgo.opendbt.experiment.terminal.filter;

import com.highgo.opendbt.experiment.terminal.common.Constants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author highgo
 * @date 2021/11/26 20:27
 */
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private final Logger logger = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // websocket握手建立前调用，获取httpsession
        if(request instanceof ServletServerHttpRequest) {
          System.out.println("处理请求~");
            ServletServerHttpRequest servlet = (ServletServerHttpRequest) request;

            // 这里从request中获取session,获取不到不创建，可以根据业务处理此段
            HttpSession httpSession = servlet.getServletRequest().getSession();
            logger.info("httpSession key：" + httpSession.getId());
            httpSession.setAttribute(Constants.USER_KEY, httpSession.getId());

            // 设置连接标志
            attributes.put(Constants.CONTAINER_EXEC, false);

            String containerId = ((ServletServerHttpRequest) request).getServletRequest().getParameter("containerId");

            if (StringUtils.isNotBlank(containerId)){
                attributes.put(Constants.CONTAINER_ID, containerId);
                attributes.put(Constants.CONTAINER_EXEC, true);
            }
        }

        return super.beforeHandshake(request,response,wsHandler,attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler webSocketHandler, Exception e) {
        // websocket握手建立后调用
        logger.info("websocket连接握手成功");
    }
}

