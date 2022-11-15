package view;

import service.FileClientService;
import service.MessageClientService;
import service.UserClientService;
import utils.ScannerUtils;

import java.util.List;

/**
 * @Author: xuan
 * @CreateTime: 2022-11-15  11:36
 * @Version: 1.0
 */
public class QQView {

    private boolean loop = true;    // 控制是否显示菜单
    private String key;    // 接收用户键盘的输入
    private UserClientService userClientService = new UserClientService();
    private MessageClientService messageClientService = new MessageClientService();
    private FileClientService fileClientService = new FileClientService();

    // 显示主菜单
    public void mainMenu() {
        while (loop) {
            System.out.println("========= 欢迎登录网络通信系统 ===========");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入你的选择： ");
            key = ScannerUtils.readString(1);

            switch (key) {
                case "1":
                    System.out.print("请输入用户号： ");
                    String userId = ScannerUtils.readString(50);
                    System.out.print("请输入密码： ");
                    String password = ScannerUtils.readString(50);
                    // 构建User对象 到服务端验证
                    if (userClientService.checkUser(userId, password)) {
                        System.out.println("========= 欢迎 " + userId + " 登录成功 ===========");
                        // 进入到二级菜单
                        while (loop) {
                            System.out.println("\n========= 网络通信系统二级菜单(用户 " + userId +" ) ===========");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");
                            System.out.print("请输入你的选择： ");
                            key = ScannerUtils.readString(1);
                            switch (key) {
                                case "1":
                                    userClientService.getOnlineUser();
                                    break;
                                case "2":
                                    System.out.print("请输入想说的话： ");
                                    String content = ScannerUtils.readString(100);
                                    messageClientService.sendMessageToAll(userId, content);
                                    break;
                                case "3":
                                    System.out.print("请输入想聊天的用户号： ");
                                    String receiverId = ScannerUtils.readString(50);
                                    System.out.print("请输入想说的话： ");
                                    content = ScannerUtils.readString(100);
                                    messageClientService.sendMessageToOne(userId, receiverId, content);
                                    break;
                                case "4":
                                    System.out.print("请输入你想把文件发送给的用户(在线): ");
                                    receiverId = ScannerUtils.readString(50);
                                    System.out.print("请输入发送文件的完整路径(形式如: d:\\xx.jpg): ");
                                    String src = ScannerUtils.readString(50);
                                    System.out.print("请输入把文件发送给对方的完整路径(形式如: d:\\xx.jpg):");
                                    String dest = ScannerUtils.readString(50);
                                    fileClientService.sentToOne(src, dest, userId, receiverId);
                                    break;
                                case "9":
                                    userClientService.logout();
                                    System.out.println("退出系统");
                                    loop = false;
                                    break;
                            }
                        }
                    } else {
                        System.out.println("登录失败");
                    }
                    break;
                case "9":
                    loop = false;
                    break;
            }
        }
    }

    public static void main(String[] args) {
        new QQView().mainMenu();
        System.out.println();
    }

}
