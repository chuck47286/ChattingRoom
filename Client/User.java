public class User {


    public String username;
    public String password;
    private DataHandler dataHandler = null;

    public User() {
        
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public DataHandler getDataHandler(){
        return dataHandler;
    }

    public void setDataHandler(DataHandler dataHandler){
        this.dataHandler = dataHandler;
    }

    public boolean sendDataPackage(DataPackage pkg){
        return sendDataString(pkg.toString());
    }

    public boolean sendDataString(String msg) {
        boolean ret = dataHandler.sendDataHandle(msg);
        return ret;
    }
}