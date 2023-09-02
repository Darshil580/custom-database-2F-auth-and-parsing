import SQLParser.Query;
import UserAuthentication.User;



import java.util.Scanner;

public class Main {
    public static void main(String[] args) {


        init();
        options();

        Scanner s = new Scanner(System.in);

        String choice;

        boolean flag = true;

        do {
            System.out.print("Enter your choice: ");
            choice = s.nextLine();

            switch (choice){
                case "1":

                    User user = User.Login();
                    if(user == null){
                        options();
                        continue;
                    }
                    else {
                        flag = false;
                    }
                    UserOperations(user);
                    break;
                case "2":
                    User.SignUp();
                    break;
                case "3":
                    flag = false;
                    System.out.println("Application Terminated.");
                    break;

                default:System.out.println("Wrong choice, enter either 1 or 2.");
                    break;
            }
            if(flag == false){
                break;
            }
            options();

        }while(flag);

        System.out.println();
    }

    public static void init()
    {
        System.out.println("=========================================================================");
        System.out.println("                    Welcome to Darshil's DBMS System                     ");
        System.out.println("=========================================================================");

    }

    public static void options(){
        System.out.println("--------------------------------------------------------------------------");
        System.out.println(" To access the DBMS System, Either use credential or Setup new credential");
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("1. Sign In\n2. Register/Create Account\n3. Exit");
        System.out.println("--------------------------------------------------------------------------");
    }

    public static void UserOperations(User user){

        System.out.println("=============================================================================================================");
        System.out.println("                                                 User Operations                              ");
        System.out.println("---------------------------------------------------------------------------------------------------------------");
        System.out.println("User: "+user.getUserId());
        System.out.println("Database: "+user.getDatabase());
        System.out.println("Note: One Database is allowed per User. Delete the old DB to create New one ");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.println("           Functions          |           Syntax                 ");
        System.out.println("1. Create Database - "+ (user.getDatabase()==null?"Enabled ":"Disabled")+" | CREATE DATABASE DATABASE_NAME;");
        System.out.println("2. Delete Database - "+ (user.getDatabase()!=null?"Enabled ":"Disabled")+" | DROP DATABASE DATABASE_NAME;");
        System.out.println("3. CREATE TABLES   - "+ (user.getDatabase()!=null?"Enabled ":"Disabled")+" | CREATE TABLE TABLE_NAME( FIELD TYPE(SIZE),..);");
        System.out.println("4. SELECT QUERY    - "+ (user.getDatabase()!=null?"Enabled ":"Disabled")+" | SELECT FIELD_NAMES,.. FROM TABLE_NAME WHERE CONDITION(OPTIONAL);");
        System.out.println("5. INSERT QUERY    - "+ (user.getDatabase()!=null?"Enabled ":"Disabled")+" | INSERT INTO TABLE_NAME(FIELDS_OPTIONAL) VALUES(FIELD_VALUES,..);");
        System.out.println("6. UPDATE QUERY    - "+ (user.getDatabase()!=null?"Enabled ":"Disabled")+" | UPDATE TABLE_NAME SET FIELD_NAME=FIELD_VALUE WHERE CONDITION(OPTIONAL);");
        System.out.println("7. DELETE QUERY    - "+ (user.getDatabase()!=null?"Enabled ":"Disabled")+" | DELETE FROM TABLE_NAME WHERE CONDITION(OPTIONAL);");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        while (true){
            Query.SQLEditor(user);
        }

    }
}