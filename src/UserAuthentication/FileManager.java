package UserAuthentication;


import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class FileManager {

    static String path = "data/";
    private ArrayList<User> userData = new ArrayList<User>();

    // Constuctor to initialize the structure.
    public FileManager() {

        File file = new File("data/");

        if(!file.exists())
        {
            if(file.mkdir()){
                createUserInfofile();
            }
        }
        else
        {
            createUserInfofile();
        }

    }

    private void createUserInfofile(){

        File file = new File("data/userinfo.txt");

        try {
            if (file.createNewFile()) {
                System.out.println("File Created.");
            }
            else
            {
//                System.out.println("Loading the file....");
                loadfiles(file);
            }
        }
        catch (Exception e) {
            System.out.println("userinfo.txt file could not be loaded.");
        }
    }

    private void loadfiles(File file){

        try{
            Scanner fileReader = new Scanner(file);
            String line;


            while (fileReader.hasNextLine()){



                line = fileReader.nextLine();
                if(line == ""){
                    continue;
                }
                addUserToClass(line);
            }

            fileReader.close();
            System.out.println("User info is Loaded.");
        }
        catch (Exception e){
            System.out.println("Failed");
            e.printStackTrace();
        }

    }

    public boolean createDatabase(String db,int userIndex){
        File fileDir = new File(path+db);
        if(!fileDir.exists()){
            fileDir.mkdir();
            userData.get(userIndex).database = db;
            userData.get(userIndex).userDBpath = path + db + "/";
            userDataWriter();
            return true;
        }
        else{
            return false;
        }
    }

    private void addUserToClass(String line){

        String [] s = line.split(";");

        User user = new User();

        user.userId = s[0];
        user.password = s[1];
        user.answer = s[2];
        try {
            user.database = s[3];
            user.userDBpath = FileManager.path + user.database;
        }
        catch (Exception e){
            user.database = null;
        }

        userData.add(user);
    }

    public void saveUser(String userString){
        addUserToClass(userString);
        userDataWriter();
    }

    private void userDataWriter(){
        try {
            FileWriter fw = new FileWriter("data/userinfo.txt");
            for (User userDatum : userData) {
                fw.write(userDatum.__str__());
            }
            fw.write('\n');
            fw.close();
//            try{
//                File f = new File("data/userinfo.txt");
//                loadfiles(f);
//            }catch (Exception e){
//                System.out.println("File is locked");
//            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<User> userList(){
        return userData;
    }

    public String getUserPasswordByIndex(int index){
        return userData.get(index).password;
    }

    public User getUserByIndex(int index){
        return userData.get(index);
    }
}


