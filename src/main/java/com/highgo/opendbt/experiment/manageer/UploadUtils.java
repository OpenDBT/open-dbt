package com.highgo.opendbt.experiment.manageer;

import com.github.dockerjava.api.exception.DockerClientException;
import com.jcraft.jsch.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * @Description:
 * @Title: UploadUtils
 * @Package com.highgo.opendbt.experiment.manageer
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/9/4 15:12
 */

public class UploadUtils {
  //打印log日志
  private static final Log logger = LogFactory.getLog(UploadUtils.class);
  private static final int TIME_OUT = 60000;
  private Session sshSession;
  private ChannelSftp channel;
  private static ThreadLocal<UploadUtils> sftpLocal = new ThreadLocal<>();

  private UploadUtils(String host, int port, String username, String password){
    sshSession=sftpConnection("s",username,host,port,password);
    logger.info("连接ftp成功!" + sshSession);
  }

  public Session sftpConnection( String value,  String sftpUser,  String sftpHost,  int sftpPort,
                                 String sftpPass) {
     Session session = null;
    logger.info("preparing the host information for sftp.");
    try {
      JSch jsch = new JSch();
      session = jsch.getSession(sftpUser, sftpHost, sftpPort);
      if (value.equals("puttyServer")) {
        session.setProxy(new ProxySOCKS5("proxy need to add", Integer.parseInt("portnumber need to add")));
      }
      session.setPassword(sftpPass);
      java.util.Properties config = new java.util.Properties();
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);
      session.connect();
      return session;
    } catch (Exception ex) {
      ex.printStackTrace();
      //sftpConnection(value, sftpUser, sftpHost, sftpPort, sftpPass);
    }
    return null;
  }

  /**
   * @description: 执行命令
   * @author:
   * @date: 2023/9/5 13:31
   * @param: [command 命令]
   * @return: java.lang.String
   **/
  public  String executeCommand(String command) throws Exception {

    ChannelExec channelExec = null;
    try {
      channelExec = (ChannelExec) sshSession.openChannel("exec");
      channelExec.setCommand(command);

      // 获取命令执行的输出流
      InputStream in = channelExec.getInputStream();
// 获取命令执行的错误流
      InputStream errorStream = channelExec.getErrStream();
      // 连接并执行命令
      channelExec.connect();

      // 读取命令执行的输出
      StringBuilder output = new StringBuilder();
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(buffer)) > 0) {
        output.append(new String(buffer, 0, bytesRead));
      }
      // 读取命令执行的错误输出
      StringBuilder errorOutput = new StringBuilder();
      while ((bytesRead = errorStream.read(buffer)) > 0) {
        errorOutput.append(new String(buffer, 0, bytesRead));
      }
      // 等待命令执行完成
      int exitCode = channelExec.getExitStatus();
      logger.error("Docker Compose 构建返回信息, 信息输出: " + errorOutput.toString());
      if (exitCode != 0) {
        throw new DockerClientException("Docker Compose 构建失败");
      }else{
        logger.info("Docker Compose 构建成功");
      }

      return output.toString();
    } finally {
      if (channelExec != null) {
        channelExec.disconnect();
      }
    }
  }

  /**
   * 是否已连接
   *
   * @return
   */
  private boolean isConnected() {
    return null != channel && channel.isConnected();
  }
  /**
   * 获取本地线程存储的sftp客户端
   *
   * @return
   * @throws Exception
   */
  public static UploadUtils getSftpUtil(String host, int port, String username, String password) throws Exception {
    //获取本地线程
    UploadUtils sftpUtil = sftpLocal.get();
    if (null == sftpUtil || !sftpUtil.isConnected()) {
      //将新连接防止本地线程，实现并发处理
      sftpLocal.set(new UploadUtils(host, port, username, password));
    }
    return sftpLocal.get();
  }
  /**
   * 释放本地线程存储的sftp客户端
   */
  public static void release() {
    if (null != sftpLocal.get()) {
      sftpLocal.get().closeChannel();
      logger.info("关闭连接" + sftpLocal.get());
      sftpLocal.set(null);
    }
  }
  /**
   * 关闭通道
   *
   * @throws Exception
   */
  public void closeChannel() {
    if (null != channel) {
      try {
        channel.disconnect();
      } catch (Exception e) {
        logger.error("关闭SFTP通道发生异常:", e);
      }
    }
    if (null != sshSession) {
      try {
        sshSession.disconnect();
      } catch (Exception e) {
        logger.error("SFTP关闭 session异常:", e);
      }
    }
  }
  /**
   * @param directory 上传ftp的目录
   * @param uploadFile 本地文件目录
   *
   * @return
   */
  public String upload(String directory, String uploadFile) throws Exception {
    String fileName=null;
    try {
      //获取sftp通道
      channel = (ChannelSftp)sshSession.openChannel("sftp");
      channel.connect();
      //执行列表展示ls 命令
      channel.ls(directory);    //执行盘符切换cd 命令
      channel.cd(directory);
    //  List<File> files = getFiles(uploadFile, new ArrayList<File>());
     // for (int i = 0; i < files.size(); i++) {
        //File file = files.get(i);
         File file =new File(uploadFile);
        InputStream input = new BufferedInputStream(new FileInputStream(file));
        channel.put(input, file.getName());
        try {
          if (input != null) input.close();
        } catch (Exception e) {
          e.printStackTrace();
          logger.error(file.getName() + "关闭文件时.....异常!" + e.getMessage());
        }
        if (file.exists()) {
          boolean b = file.delete();
          logger.info(file.getName() + "文件上传完毕!删除标识:" + b);
          fileName=file.getName();
        }
     // }
    }catch (Exception e) {
      logger.error("【子目录创建中】：",e);
      //创建子目录
      channel.mkdir(directory);
    }finally {
      UploadUtils.release();
    }
    return fileName;
  }
/*  //获取文件
  public List<File> getFiles(String realpath, List<File> files) {
    File realFile = new File(realpath);
    if (realFile.isDirectory()) {
      File[] subfiles = realFile.listFiles(new FileFilter() {
        @Override
        public boolean accept(File file) {
          if (null == last_push_date ) {
            return true;
          } else {
            long modifyDate = file.lastModified();
            return modifyDate > last_push_date.getTime();
          }
        }
      });
      for (File file : subfiles) {
        if (file.isDirectory()) {
          getFiles(file.getAbsolutePath(), files);
        } else {
          files.add(file);
        }
        if (null == last_push_date) {
          last_push_date = new Date(file.lastModified());
        } else {
          long modifyDate = file.lastModified();
          if (modifyDate > last_push_date.getTime()) {
            last_push_date = new Date(modifyDate);
          }
        }
      }
    }
    return files;
  }*/
}


