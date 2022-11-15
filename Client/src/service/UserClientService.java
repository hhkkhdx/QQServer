package service;

import common.Message;
import common.MessageType;
import common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.time.LocalDateTime;

/**
 * @Author: xuan
 * @CreateTime: 2022-11-15  13:31
 * @Version: 1.0
 */
public class UserClientService {

    private User user = new User();
    private Socket socket;

    public boolean checkUser(String userId, String password) {
        boolean b = false;
        user.setUserId(userId);
        user.setPassword(password);

        try {
            // 连接到服务端 发送User对象
            socket = new Socket(InetAddress.getLocalHost(), 9999);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);

            // 读取从服务端回复的message
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) ois.readObject();
            if (MessageType.MESSAGE_LOGIN_SUCCEED.equals(message.getMesType())) {

                // 登录成功 启动一个线程 持有socket
                // 创建一个和服务器端保持通信的线程  ClientConnectServerThread
                ClientConnectServerThread ccst = new ClientConnectServerThread(socket);
                ccst.start();

                // 线程放到集合中
                ManageClientConnectServerThread.addClientConnectServerThread(userId, ccst);
                b = true;


            } else {
                // 如果登录失败 关闭socket
                socket.close();

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return b;
    }

    public void getOnlineUser() {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_USER);
        message.setSender(user.getUserId());
        message.setSendTime(LocalDateTime.now().toString());
        // 得到当前线程的socket 对应的 ObjectOutputStream 对象
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(user.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void logout() {
        Message message = new Message();
        message.setSender(user.getUserId());
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            System.out.println(user.getUserId() + " 已退出系统");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
