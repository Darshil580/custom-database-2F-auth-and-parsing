package UserAuthentication;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Scanner;

public class User {


    static FileManager manager = new FileManager();
    static String question = "Set your security question for future verification. \nWhat is your favourite color?";
    static String questionLogin= "What is your favourite color?";

    int userRowIndex  = -1;
    String userId, password, answer, database = "";
    public String userDBpath = FileManager.path + this.database + "/";

    public User(){

    }
    public User(String userId,String password, String answer){
        this.userId = userId;
        this.password = password;
        this.answer = answer;
    }

//    public User(String userId,String password, String answer,String database){
//        this.userId = userId;
//        this.password = password;
//        this.answer = answer;
//        this.database = database;
//    }

    public String getUserId(){
        return this.userId;
    }

    public String getDatabase(){
        return this.database;
    }

    public String __str__(){
        String db = this.userId+";"+this.password+";"+this.answer+";"+this.database+";"+"\n";
        String nondb = this.userId+";"+this.password+";"+this.answer+";";

        if(this.database != null){
            return db;
        }
        else{
            return nondb;
        }
    }

    public static void SignUp(){

        Scanner s = new Scanner(System.in);
        String user_id,user_password,answer;

        System.out.print("Enter User id: ");
        user_id = s.nextLine();

        int user_index = findByUserID(user_id);

        if(user_index >= 0){
            System.out.println("User already exists!");
        }
        else
        {
            System.out.print("Enter User password: ");
            user_password = User.stringProtection(s.nextLine());
            System.out.print(question+": ");
            answer = s.nextLine();

            String userString = user_id+";"+user_password+";"+answer;
            manager.saveUser(userString);
        }
        System.out.println("Successfully Signed Up, Please go to Login to use your account.");

    }

    public static User Login(){
        Scanner s = new Scanner(System.in);
        String user_id,user_password,user_answer;

        System.out.print("Enter User id: ");
        user_id = s.nextLine();

        int user_index = findByUserID(user_id);

        if(user_index >= 0){

            System.out.print("Enter User password: ");
            user_password = User.stringProtection(s.nextLine());
            System.out.print(questionLogin+": ");
            user_answer = s.nextLine();


            User user = new User(user_id,user_password,user_answer);
            if(user.password.equals(manager.getUserPasswordByIndex(user_index))){
                if(user.answer.equals(manager.getUserByIndex(user_index).answer)){
                    System.out.println("\nUser Authenticated.......\n");
                    user = manager.getUserByIndex(user_index);
                    user.userRowIndex = user_index;
                    return user;
                }
                else {
                    System.out.println("Wrong Security answer.");
                    return null;
                }
            }
            System.out.println("Wrong password.");
            return null;

        }
        else
        {
            System.out.println("User Does not Exist!");
            return null;
        }

    }

    public void createDatabase(String db){
        if(this.database == null){
            if(manager.createDatabase(db,this.userRowIndex)){
                System.out.println("DB created.");
                this.database = db;
            }
            else{
                System.out.println("DB exists.");
            }

        }
        else {
            System.out.println("Only one database allowed per user.");
        }

    }

    public static int findByUserID(String user_name){

        ArrayList<User> users = manager.userList();
        int index = -1;

        for (User user: users) {
            index++;
            if(user.userId.equalsIgnoreCase(user_name)){
                return index;
            }
        }
        return -1;
    }

    public static String stringProtection(String password) {
        try {
            MessageDigest protector = MessageDigest.getInstance("MD5");

            byte[] messageDigest = protector.digest(password.getBytes());

            StringBuilder strBld = new StringBuilder();

            for (byte bt : messageDigest) {
                strBld.append(String.format("%02x", bt));
            }
            return strBld.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return password;

    }
}
