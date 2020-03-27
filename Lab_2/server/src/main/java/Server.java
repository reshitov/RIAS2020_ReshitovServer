import com.sun.javafx.scene.shape.PathUtils;
import org.apache.log4j.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

class ServerSomthing extends Thread {

    private Socket socket; // сокет, через который сервер общается с клиентом,
    private BufferedReader in; // поток чтения из сокета
    private DataOutputStream out; // поток завписи в сокет
    private Logger log = Logger.getRootLogger();
    public ServerSomthing(Socket socket) throws IOException {
        this.socket = socket;
        log.debug("socket create");
        // если потоку ввода/вывода приведут к генерированию искдючения, оно проброситься дальше
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        log.debug("reader create");
        out = new DataOutputStream(socket.getOutputStream());
        log.debug("writer create");
        System.out.println("new client");
        log.debug("thread start");
        start(); // вызываем run()
    }
    @Override
    public void run() {
        boolean isRun = true;
        //PropertyConfigurator.configure("./log4j.properties");
        String word;
        try {
            try {
                while (isRun) {
                    word = in.readLine();
                    String[] comand =  word.split(" ");
                    switch (comand[0]){
                        case "disconnect":
                            log.debug("client diconnect" + socket.toString());
                            isRun = false;
                            downService();
                            break;
                        case "send":
                            System.out.println("client send "+word.substring(5));
                            log.debug("client send "+word.substring(5));
                            out.writeUTF(word.substring(5));
                            out.flush();

                            break;
                        case "logLevel":
                            log.info("logLevel set" + comand[1]);
                            switch (comand[1]){
                                case "off":
                                    log.setLevel(Level.OFF);
                                    break;
                                case "fatal":
                                    log.setLevel(Level.FATAL);
                                    break;
                                case "error":
                                    log.setLevel(Level.ERROR);
                                    break;
                                case "warn":
                                    log.setLevel(Level.WARN);
                                    break;
                                case "info":
                                    log.setLevel(Level.INFO);
                                    break;
                                case "debug":
                                    log.setLevel(Level.DEBUG);
                                    break;
                                case "trace":
                                    log.setLevel(Level.TRACE);
                                    break;
                                case "all":
                                    log.setLevel(Level.ALL);
                                    break;
                                default:
                            }
                            break;


                    }
                    }

                } catch (NullPointerException ignored) {}
            } catch (IOException e) {
            this.downService();
        }
    }
    /* закрытие сервера, прерывание себя как потока и удаление из списка потоков */
    private void downService() {
        try {
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }
}

public class Server {

    public static  int PORT = 8080;
    public static LinkedList<ServerSomthing> serverList = new LinkedList<>(); // список всех нитей - экземпляров
    // сервера, слушающих каждый своего клиента
    private static  Logger log = Logger.getRootLogger();
    public static void main(String[] args) throws IOException {
      if (args.length>0){
            PORT = Integer.parseInt(args[0]);
        }
        new LogSetup("log/server.log", Level.ALL);
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Server Started");
        log.info("start server");
        String current = new java.io.File( "." ).getCanonicalPath();
        System.out.println("Current dir:"+current);
        String currentDir = System.getProperty("user.dir");
        System.out.println("Current dir using System:" +currentDir);

        try {
            while (true) {
                // Блокируется до возникновения нового соединения:
                Socket socket = server.accept();
                log.debug("server accept client");
                try {
                    serverList.add(new ServerSomthing(socket)); // добавить новое соединенние в список
                } catch (IOException e) {
                    // Если завершится неудачей, закрывается сокет в противном случае, поток закроет его:
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }
}