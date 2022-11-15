package service;

import common.Message;
import common.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * @Author: xuan
 * @CreateTime: 2022-11-15  13:41
 * @Version: 1.0
 */
public class ClientConnectServerThread extends Thread {

    private Socket socket;

    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // 需要在后台和服务器通信 , while循环控制
        while (true) {
            System.out.println("客户端线程 等待从服务器端发送的消息");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Object o = ois.readObject();
                List<Message> messages;
                Message message = null;
                if (o instanceof List){
                    messages = (List<Message>) o;
                    for (Message m : messages) {
                        System.out.println("\n" + m.getSender() + " 给你留言 说 " + m.getContent() + "\t " + m.getSendTime());
                    }
                } else if (o instanceof Message) {
                    message = (Message) o;
                    if (MessageType.MESSAGE_RETURN_ONLINE_USER.equals(message.getMesType())) {
                        System.out.println("\n =======  当前在线用户列表:" +  "  ==========");
                        String[] ss = message.getContent().substring(1, message.getContent().length() - 1).split(",");
                        for (String s: ss) {
                            System.out.println("用户: " + s);
                        }
                    } else if (MessageType.MESSAGE_COMMON_MESSAGE.equals(message.getMesType())) {
                        System.out.println("\n" + message.getSender() + " 对 " + message.getReceiver() + " 说 " + message.getContent() + "\t " + message.getSendTime());
                    } else if (MessageType.MESSAGE_FILE_MESSAGE.equals(message.getMesType())){
                        System.out.println("\n" + message.getSender() + " 给 " + message.getReceiver() + " 发送了一个文件 " +
                                message.getSrc() + " 到 我的电脑 " + message.getDest());
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(message.getDest());
                            fos.write(message.getFileBytes());

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (fos != null)
                                    fos.close();
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        System.out.println("\n 保存文件成功");
                    } else if (MessageType.MESSAGE_MESSAGE_TO_ALL.equals(message.getMesType())) {
                        System.out.println("\n" + message.getSender() + " 对 所有人 说 " + message.getContent());
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
