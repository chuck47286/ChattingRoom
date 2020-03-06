import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChatServer {
    private int serverPort;
    private final UserManager userManager = new UserManager();
    private Map<SocketChannel, DataHandler> mapSocketToHandlers = new HashMap<SocketChannel, DataHandler>();

    public ChatServer() {
        this.serverPort = 50000;
    }

    public void serverRun() {
        try {
            Selector selector = Selector.open();

            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);

            InetSocketAddress hostAddress = new InetSocketAddress(this.serverPort);
            serverChannel.socket().bind(hostAddress);

            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                int count = selector.select();
                if (count > 0) {
                    // process selected key.
                    Set<SelectionKey> keys = selector.selectedKeys();
                    // System.out.println(keys);
                    for (SelectionKey key : keys) {
                        // client requires a connection.
                        if (key.isAcceptable()) {
                            // ServerSocketChannel server = (ServerSocketChannel) key.channel();
                            SocketChannel server = (SocketChannel) serverChannel.accept();
                            if (server == null) {
                                System.out.println("Got a new connection, but sc is null");
                                continue;
                            }
                            
                            // None Blocking IO
                            server.configureBlocking(false);
                            server.register(key.selector(), SelectionKey.OP_READ);

                            //
                            DataHandler handler = new DataHandler();
                            handler.setSocketChannel(server);
                            mapSocketToHandlers.put(server, handler);
                        }

                        // server is ready to read data from client.
                        else if (key.isReadable()) {

                            SocketChannel channel = (SocketChannel) key.channel();

                            DataHandler handler = mapSocketToHandlers.get(channel);
                            ArrayList<DataPackage> dataPkgs = handler.recieveHandle();

                            if (dataPkgs == null) {
                                System.out.println("no data, close connection: " + channel.toString());
                                key.cancel();

                                /* delete the handler from the map */
                                mapSocketToHandlers.remove(channel);

                                /* close the socket */
                                Socket s = channel.socket();
                                try {
                                    s.close();
                                } catch( IOException ie ) {
                                    System.err.println( "Error closing socket "+s+": "+ie );
                                }
                            } else {
                                /* process incoming data package */
                                dataPackageHandler(dataPkgs, handler);
                            }                            
                        }
                        keys.clear();
                    }
                } else {
                    /* sleep a while */
                    try { Thread.sleep(2); } catch (Exception e) {};
                }
            }

        } catch (IOException e) {
            // System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public void dataPackageHandler(ArrayList<DataPackage> dataPkgs, DataHandler handler){
        for (DataPackage pkg: dataPkgs) {
            System.out.println("[dataPackageHandler]Process pkg: " + pkg.toString());
            switch (pkg.type) {
                case 0: /* heart beat */
                    break;
                case 1: /* sign in */
                    System.out.println("signUp:" + pkg.type);
                    userManager.userSignUp(pkg, handler);
                    break;
                
                default:
                    System.err.println("Unknown package type: " + pkg.type + ", content: " + pkg.toString());
                    break;
            }
        }
    }

    
    public static void main(String[] args) {
        ChatServer a = new ChatServer();
        a.serverRun();
    }
}