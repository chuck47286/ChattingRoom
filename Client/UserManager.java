
public class UserManager {

    /**
     * Server handle user sign up new account.
     * @param pkg
     * @param dataHandler
     * @return
     */
    public User userSignUp(DataPackage pkg, DataHandler dataHandler){
        boolean checkdatabase = true;
        // TODO: Here will connect Database and check other details.
        if(checkdatabase){
            System.out.println("UserManager - signUp");
            User user = new User(pkg.username, pkg.userpw);

            // Get correspond data handler.
            user.setDataHandler(dataHandler);

            pkg.flag = 1;
            
            // Send back to Client and return scuess.
            user.getDataHandler().sendDataHandle(pkg.toString());
            return user;
        }
        else {
            System.out.println("User sign up Error!!!");
            pkg.flag = 0;
            
            dataHandler.sendDataHandle(pkg.toString());
            return null;
        }
    }
}