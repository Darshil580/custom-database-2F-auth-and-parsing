package SQLParser;

import UserAuthentication.User;

import java.util.ArrayList;
import java.util.Scanner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Query {

    public static User currentUser;

    public static boolean CREATE(String query){

        System.out.println(query);
        query = query+";";


            Table newTable;

            Pattern pattern = Pattern.compile("create\\s*table\\s*([a-zA-Z0-9_]*)\s?\\((.*)\\);$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(query);


            if(matcher.find()){
                //Table name fetched from string using regex
                String name = matcher.group(1);
                newTable = new Table(name);

                //Table field fetched from string using regex
                String fields = matcher.group(2);
                System.out.println(fields.trim());
                if(newTable.parseFields(fields)){
                    if(newTable.createTable(currentUser)){
                        newTable.writeMeta(currentUser);
                        System.out.println("Table created Successfully");
                        return true;
                    }
                    else{
                        System.out.println("Table already Exist.");
                    }
                }
                else {
                    System.out.println("Query failed unknown syntax");
                }
            }

        return false;
    }

    public static void SELECT(String query){

//        System.out.println(query);

        query = query + ";";

        Pattern pattern = Pattern.compile("select\\s+(.*)\\s+from\\s+(.*)\\s+(where .*)?;$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);

        System.out.println(query);

        if(matcher.find()){

            Table table = new Table(matcher.group(2));
            String field = matcher.group(1);
            String where = matcher.group(3);
            if(table.loadTableData(currentUser)){
                table.fetchData(field.replaceAll(" ",""),where);
            }
            else{
                System.out.println("Select query failed.");
            }

        }
        else if(!matcher.find()){
            pattern = Pattern.compile("select\\s+(.*)\\s+from\\s+([a-zA-Z0-9_]*);$", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(query);

            if(matcher.find()){
                String field = matcher.group(1);
                System.out.println();
                field = matcher.group(1);

                Table table = new Table(matcher.group(2));


                if(table.loadTableData(currentUser)){
                    table.fetchData(field.replaceAll(" ",""),null);
                }
                else{
                    System.out.println("Select query failed.");
                }
            }

        }
    }

    public static boolean INSERT(String query){

        System.out.println(query);

        query = query + ";";

        Pattern pattern = Pattern.compile("insert into ([a-zA-Z0-9_]*) ?\\((.*)\\) ?values ?\\((.*)\\) ?;$",Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(query);

        if(m.matches()){

            String name = m.group(1);
            String[] fields = m.group(2).replaceAll("\\)|\\(","").split(",");
            String values = m.group(3);

            Table table = new Table(name);


            if(table.loadTableData(currentUser)){
                if(table.insertRows(fields,values,currentUser)){
                    System.out.println("Data Inserted");
                    return true;
                }
                else{
                    System.out.println("Insert query failed");
                    return false;
                }
            }
            else{
                System.out.println("Could not load the data.");
            }


            return true;
        }
        else{
            System.out.println("Invalid Query.");
        }

        return false;
    }

    public static boolean UPDATE(String query){

        query += ";";

        Pattern p = Pattern.compile("update ([a-zA-Z0-9_]*) set (.*) where (.*);$",Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(query);

        if(m.matches()){
            System.out.println("Success.");
            String name = m.group(1);
            String newValField = m.group(2);
            String condition = m.group(3);

            Table table = new Table(name);
            if(table.loadTableData(currentUser)){
                if(table.updateRecords(newValField,condition,currentUser)){
                    System.out.println("Records updated successfully.");
                    return true;
                }
                else
                {
                    System.out.println("Update Query Failed.");
                    return  false;
                }
            }
            else{
                System.out.println("Could not load the data");
                return false;
            }

        }
        else{
            p = Pattern.compile("update ([a-zA-Z0-9_]*) set (.*);$",Pattern.CASE_INSENSITIVE);
            m = p.matcher(query);

            if(m.matches()){
                String name = m.group(1);
                String newValField = m.group(2);

                Table table = new Table(name);
                if(table.loadTableData(currentUser)){
                    if(table.updateRecords(newValField,null,currentUser)){
                        System.out.println("Records updated successfully.");
                        return true;
                    }
                    else{
                        System.out.println("Update Query Failed.");
                        return  false;
                    }
                }
                else{
                    System.out.println("Could not load the data");
                    return false;
                }

            }
            else{

            }
        }

        return false;
    }

    public static boolean DELETE(String query){

        query+= ";";

        System.out.println(query);


        Pattern p = Pattern.compile("^delete\\s+from\\s+([a-zA-Z0-9_ ]+)(\\s+where\\s+([a-zA-Z0-9_ ]+)\\s?(=|<>|!=|<|>|<=|>=)\\s?\"([a-zA-Z0-9_ ]+)\"(\\s?(and|or)?\\s?([a-zA-Z0-9_ ]+)\\s?(=|<>|!=|<|>|<=|>=)\\s?\"([a-zA-Z0-9_ ]+)\")*\\s?)?\\s*;$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(query);
//        Pattern p = Pattern.compile("delete from ([a-zA-Z0-9_]*) ((.*)|(where (.*)));$",Pattern.CASE_INSENSITIVE);
//        Matcher m = p.matcher(query);

        if(m.matches()){
            String name = m.group(1);
            String condition = m.group(2);

            Table table = new Table(name);

            if(table.loadTableData(currentUser)){
                if(table.deleteRecord(currentUser,m)){
                    System.out.println("Record Deleted.");
                    return true;
                }
                else{
                    System.out.println("Delete query failed");
                    return false;
                }
            }


        }
        else{
            p = Pattern.compile("delete from ([a-zA-Z0-9_]*);$");
            m = p.matcher(query);

            if(m.matches()){
                Table table = new Table(m.group(1));
                if(table.loadTableData(currentUser)){
                    if(table.deleteRecord(currentUser,null)){
                        System.out.println("Record Deleted.");
                        return true;
                    }
                    else{
                        System.out.println("Delete query failed");
                        return false;
                    }
                }


            }
        }

        return false;
    }

    public static void QueryHandler(String query,boolean transaction){


        if(transaction){
//            String[] Queries = querySet.split(";");
//
//            for (String ss: Queries
//            ) {
        }
        else{

            Pattern p = Pattern.compile("create\\s*database\\s*(.*);",Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(query.trim());

            if(m.matches()){
                String s = query.replaceAll(";","");
                s = s.replaceAll("\\s+"," ");
                s = s.split(" ")[2];
                currentUser.createDatabase(s);
            }
            else{
                if(currentUser.getDatabase() == null){
                    System.out.println("Database does not exist.");
                }
                else{

                    String[] keys = query.split(" ");

                    if(keys[0] == null){
                        System.out.println("Wrong syntax");
                    }
                    query = query.replace(";","");
                    switch (keys[0].toLowerCase().trim()){
                        case "create": CREATE(query);
                            break;
                        case "select": SELECT(query);
                            break;
                        case "insert":INSERT(query);
                            break;
                        case "update":UPDATE(query);
                            break;
                        case "delete":DELETE(query);
                            break;
                    }

                }

            }

        }

    }

    public static void initSQL(){
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("                         Welcome to SQL Editor                            ");
        System.out.println("    This SQL Editor allows One line execution unless transaction is init. ");
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("               Type Exit to  go back to operation Menu.                   ");
        System.out.println("NOTE:TYPE SUPPORT FOR FIELDS - INTEGER/INT,VARCHAR(SIZE),BOOLEAN/BOOL,FLOAT");
        System.out.println("--------------------------------------------------------------------------");
    }

    public static void SQLEditor(User user) {

        initSQL();
        currentUser = user;
        int cnt = 1;
        Scanner s = new Scanner(System.in);

        String input = s.nextLine();

        int flag1 = 0;


        while(!input.equalsIgnoreCase("exit;")) {

            QueryHandler(input.trim(),false);

            input = s.nextLine();

            if (input.equals("")) {
                System.out.println("Empty command not allowed. ");

            }
            System.out.println("-------------------------------------------------------------");

        }
        System.out.println("You are successfully logged out.");
        System.out.println("Re-run the program/console.");

    }


    /**
    * Not implemented  -  lack of time
    *
    * Not implemented  - lack of time
     */
    public static boolean ROLLBACK(String query){

        return false;
    }

    public static boolean COMMIT(String query){

        return false;
    }

}
