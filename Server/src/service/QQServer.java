package service;

import common.Message;
import common.MessageType;
import common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: xuan
 * @CreateTime: 2022-11-15  14:25
 * @Version: 1.0
 */
public class QQServer {
    private ServerSocket ss;
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, List<Message>> offLineMessage = new ConcurrentHashMap<>();

    static {
        // 读数据库
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("101", new User("101", "123456"));
        validUsers.put("102", new User("102", "123456"));
        validUsers.put("103", new User("103", "123456"));
    }

    private boolean checkUser(User u1, User u2) {
        return (u1 != null && u1.getUserId().equals(u2.getUserId()) && u1.getPassword().equals(u2.getPassword()));
    }

    public static void saveOffLineMessage(Message message) {
        if (message == null || message.getReceiver() == null) return;
        if (offLineMessage.get(message.getReceiver()) == null) {
            List<Message> messages = new Vector<>();
            messages.add(message);
            offLineMessage.put(message.getReceiver(), messages);
        } else {
            List<Message> messages = offLineMessage.get(message.getReceiver());
            messages.add(message);
            offLineMessage.put(message.getReceiver(), messages);
        }
    }

    public static List<Message> getOffLineMessage(String userId) {
        return offLineMessage.get(userId);
    }

    public static void removeOffLineMessage(String userId) {
        offLineMessage.remove(userId);
    }

    public QQServer() {
        System.out.println("服务端在9999端口监听..." + new Date().toString());
        try {
            ss = new ServerSocket(9999);
            while (true) {
                Socket socket = ss.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                Object o = ois.readObject();
                Message message = new Message();
                if (o instanceof User) {
                    User u = (User) o;
                    User validUser = validUsers.get(u.getUserId());
                    if (checkUser(u, validUser)) {
                        message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                        oos.writeObject(message);
                        // 创建一个线程 和客户端保持通信 持有socket对象
                        ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, u.getUserId());
                        serverConnectClientThread.start();
                        // 放入集合中进行管理
                        ManageClientThread.addClientThread(u.getUserId(), serverConnectClientThread);

                    } else {
                        System.out.println("用户 id = " + u.getUserId() + ", pwd = " + u.getPassword() + " 验证失败 ");
                        message.setMesType(MessageType.MESSAGE_LOGIN_ERROR);
                        oos.writeObject(message);
                        socket.close();
                     }

                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                ss.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public QQServer(ServerSocket ss) {
        System.out.println("服务端在9999端口监听");
        this.ss = ss;
    }
}
