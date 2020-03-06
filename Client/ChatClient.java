import java.util.List;
import java.util.Observable;

public class ChatClient extends Observable {

	User user = new User();
	// DataHandler dataHandler = new DataHandler();
	DataHandler dataHandler = null;

	public void testSendData() {
		System.out.println("[sned data]");
		DataPackage pkg = new DataPackage();
		pkg.type = 1;
		pkg.username = "Michael";
		pkg.userpw = "1234";
		pkg.message = "QAQ";
		// System.out.println("Send PKG: " + user.getDataHandler().sendDataHandle(pkg.toString()));
		System.out.println("Send PKG: " + user.sendDataString(pkg.toString()));
	}

	/**
     * Client ask for registering new account.
     * @param username
     * @param userpw
     */
    public void sendSignUp(String username, String userpw) {
		// TODO: Here shold be connected by appliection.
        System.out.printf("Singn up for name:%s and pw:%s\n", username, userpw);
        DataPackage pkg = new DataPackage();
        pkg.type = 1;
		pkg.username = username;
		pkg.userpw = userpw;
        user.sendDataString(pkg.toString());
    }

	private int recieveFromServer() {
		// if (!user.getDataHandler().isConnected()) {
		// /* TODO: try to reconnect */
		// return -1;
		// }
		List<DataPackage> packages = user.getDataHandler().recieveHandle();
		if (packages != null) {
			// System.out.printf("got %d packages\n", packages.size());
			for (DataPackage pkg : packages) {
				System.out.println("Process pkg: " + pkg.toString());
				switch (pkg.type) {
					case 1:
						System.out.println("Sign up new account: " + pkg.toString());
						break;

					default:
						System.out.println("unknown package type: " + pkg.type + ", content: " + pkg.toString());
						break;
				}
			}
		} else {
			System.out.println("got null packages, connection may have been broken");

			return -1;
		}
		return packages.size();
	}

	public boolean userConnectToServer(String host, int port){
		if(user.getDataHandler() == null) {
			user.setDataHandler(new DataHandler());
		}
		return user.getDataHandler().connectToServer(host, port);
	}

	public void runMain(String host, int port) {

		// Connect to Server.
		userConnectToServer(host, 50000);
		
		int i = 0; //TESTING
		while (true) {
			// ------ TESTING ------
			if(i%250 == 1)
			testSendData();
			i++;
			// ---------------------
			int num = recieveFromServer();
			System.out.println(num+","+i);
            if (num <= 0) {
                /* if not login yet or get no data, then just sleep */
                try {
                    Thread.sleep(20);
                } catch (Exception e) {
                    System.out.println("wake up from sleep unexpectedly, e: " + e.toString());
                }
            }
        }
    }

	public static void main(String[] args) {
		ChatClient a = new ChatClient();
		// String hostIP = "10.113.193.133";
		String hostIP = "172.22.9.176";
		int portNum = 50000;
		// try {
			// a.startClient();
			a.runMain(hostIP, portNum);
		// } catch (IOException e) {
		// 	// TODO Auto-generated catch block
		// 	e.printStackTrace();
		// } catch (InterruptedException e) {
		// 	// TODO Auto-generated catch block
		// 	e.printStackTrace();
		// }
		// Runnable client = new Runnable() {
		// 	@Override
		// 	public void run() {
				// try {
					// new ChatClient().startClient();
					// new ChatClient().runMain(hostIP, portNum);
				// } catch (IOException e) {
				// 	e.printStackTrace();
				// 	System.out.println(e.toString());
				// } catch (InterruptedException e) {
				// 	e.printStackTrace();
				// }

		// 	}
		// };
		// new Thread(client, "client-A").start();
		// new Thread(client, "client-B").start();
	}
}