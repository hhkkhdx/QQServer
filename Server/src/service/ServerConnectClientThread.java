package service;

import common.Message;
import common.MessageType;
import common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;

/**
 * @Author: xuan
 * @CreateTime: 2022-11-15  14:35
 * @Version: 1.0
 */
public class ServerConnectClientThread extends Thread{
    private Socket socket;
    private String userId;
    private boolean onceStart = true;

    public ServerConnectClientThread() {
    }

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        boolean loop = true;
        while (loop) {
            System.out.println("服务端和客户端 " + userId + " 保持通信 读取数据\t " + new Date().toString());
            if (onceStart) {
                List<Message> messages = QQServer.getOffLineMessage(userId);
                if (messages != null) {
                    ObjectOutputStream oos = null;
                    try {
                        oos = new ObjectOutputStream(ManageClientThread.getClientThread(userId).getSocket().getOutputStream());
                        oos.writeObject(messages);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
                onceStart = false;
                QQServer.removeOffLineMessage(userId);
            }
            try {
                ObjectInputStream ois = new ObjectInputStream(ManageClientThread.getClientThread(userId).getSocket().getInputStream());
                Message message = (Message) ois.readObject();
                switch (message.getMesType()) {
                    case MessageType.MESSAGE_COMMON_MESSAGE:
                    case MessageType.MESSAGE_FILE_MESSAGE:
                        ServerConnectClientThread scct = ManageClientThread.getClientThread(message.getReceiver());
                        if (scct != null) {
                            ObjectOutputStream receiverOos = new ObjectOutputStream(scct.getSocket().getOutputStream());
                            receiverOos.writeObject(message);
                        } else {
                            QQServer.saveOffLineMessage(message);
                        }
                        break;
                    case MessageType.MESSAGE_GET_ONLINE_USER:
                        System.out.println("用户id=" + userId + ", 获取在线用户列表..." + "\t " + message.getSendTime());
                        List<String> users = ManageClientThread.getOnlineUsers();
                        Message m = new Message();
                        m.setMesType(MessageType.MESSAGE_RETURN_ONLINE_USER);
                        m.setContent(users.toString());
                        m.setReceiver(message.getSender());
                        System.out.println("用户列表: " + users + "\t " + new Date().toString());
                        ObjectOutputStream oos = new ObjectOutputStream(ManageClientThread.getClientThread(userId).getSocket().getOutputStream());
                        oos.writeObject(m);
                        break;
                    case MessageType.MESSAGE_CLIENT_EXIT:
                        System.out.println(message.getSender() + " 退出系统\t " + new Date().toString());
                        ManageClientThread.removeClientThread(message.getSender());
                        socket.close();
                        loop = false;
                        break;
                    case MessageType.MESSAGE_MESSAGE_TO_ALL:
                        List<String> onlineUsers = ManageClientThread.getOnlineUsers();
                        for (String s: onlineUsers) {
                            ServerConnectClientThread receiverThread = ManageClientThread.getClientThread(s);
                            ObjectOutputStream receiver2Oos = new ObjectOutputStream(receiverThread.getSocket().getOutputStream());
                            receiver2Oos.writeObject(message);
                        }
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }
}
