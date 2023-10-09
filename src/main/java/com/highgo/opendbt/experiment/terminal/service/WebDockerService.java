package com.highgo.opendbt.experiment.terminal.service;

import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @author highgo
 * @date 2021/12/2 16:25
 */
public interface WebDockerService {

    /**
     * 创建与 docker 容器建立 exec 的连接
     * @return Socket
     */
    void createExec(WebSocketSession session) throws Exception;

    /**
     * 接收消息处理
     * @param buffer 消息对象
     * @param session
     */
    void recvHandle(String buffer, WebSocketSession session) throws IOException;


    void recvHandle(byte[] fileData, WebSocketSession session) throws IOException;
}
