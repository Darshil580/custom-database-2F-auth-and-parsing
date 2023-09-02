package SQLParser;

import UserAuthentication.User;

import java.io.File;
import java.io.FileWriter;

import java.util.ArrayList;

import java.util.Hashtable;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 *
 * **/


public class Table {

//    public static TableFileManager manager = new TableFileManager();
    public String name;
    ArrayList<Field> fields = new ArrayList<>();
    ArrayList<Hashtable> rows = new ArrayList<>();

    public Table(String name){
        this.name = name;
    }

    /**
     *
     * @return it true if the parse is succesfully of the field string
     * **/
    public  boolean  parseFields(String rawFields){
        String [] fields = rawFields.trim().split(",");
        for(String field : fields) {
            Field f = new Field();

//            field = "-" +field;
//            Pattern p = Pattern.compile("^-(.*)[\s*](.*)[\s?]\\((.*)\\)$");
//            Matcher matcher = p.matcher(field);
//
//            if(matcher.find()){
//                System.out.println( matcher.group(1));
//                System.out.println( matcher.group(2));
//                System.out.println( matcher.group(3));
//            }

            if(field.toLowerCase().contains("not null") || field.toLowerCase().contains("primary key")){
                if(field.toLowerCase().contains("not null")){
                    f.notNull = true;
                }
                if(field.toLowerCase().contains("primary key")){
                    f.primaryKey = true;
                }
                continue;
            }


                String [] data = field.trim().split(" ");
                int flag = 1;
                f.name = data[0];

                if(data[1].contains("(")){
                    data[1] = data[1].replace("("," ");
                    data[1] = data[1].replace("("," ");

                    String [] t = data[1].split(" ");
                    f.type = t[0];
                    f.size = t[1];
                    flag = 0;
                }
                else
                {
                   f.type = data[1];
                }

                if (flag == 0){
                }
                else {
                    if (data.length == 3) {
                        f.setSize(data[2].replaceAll("\\(\\)",""));
                        f.size = data[2];
                    }
                }

            this.fields.add(f);
        }
        return true;
    }

    /**
     * @param user to specify for which user you want to add table or more like which database.
     *
     * @return creates a table file where data will be stored for paticular table file.
     * **/

    public boolean createTable(User user){
        try{
            File file = new File(user.userDBpath+"/"+this.name);
            if(file.exists()){
                return false;
            }
            else{
                file.createNewFile();
                return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param user to specify for which user u want to load the table.
     *
     * @return load the existing user table if it is available.
     * **/

    public boolean loadTableData(User user){

        try{
            File file = new File(user.userDBpath+"/"+this.name);

            try{
                Scanner fileReader = new Scanner(file);
                String line;

                String linefield = fileReader.nextLine();
                String linetype = fileReader.nextLine();
                String linesize = fileReader.nextLine();

                String[] fields = linefield.split(";");
                String[] types = linetype.split(";");
                String[] sizes = linesize.split(";");

                for(int i = 0; i <fields.length; i++){

                    Field field = new Field();

                    field.name = fields[i];
                    field.type = types[i];
                    field.size = sizes[i];

                    this.fields.add(field);
                }

                while(fileReader.hasNextLine()){

                    Hashtable<String,String> map = new Hashtable<>();
                    line = fileReader.nextLine();

                    String [] rowValues = line.split(";");

                    for (int i = 0; i < rowValues.length;i++){
                        map.put(this.fields.get(i).name,rowValues[i]);
                    }
                    rows.add(map);
                }


                fileReader.close();
            }
            catch (Exception e){
                System.out.println("Invalid Table Name");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public boolean fetchData(String fields,String conditions){

        Pattern p = Pattern.compile("[\\*]?");
        Matcher m = p.matcher(fields);

        String[] givenFields = fields.split(",");

        if(m.matches()){



            int[] max = new int[this.fields.size()];

            for(Hashtable t : this.rows){
                for (int i = 0; i< this.fields.size() ; i++){
                    try{
                        if(max[i] < t.get(this.fields.get(i).name).toString().length()){
                            max[i] = t.get(this.fields.get(i).name).toString().length();
                        }
                    }
                    catch(Exception e){
                        System.out.println("0 Rows Found.");
                        return false;
                    }
                }

                for (int i = 0; i< this.fields.size() ; i++){
                    try{
                        if(max[i] < this.fields.get(i).name.length()){
                            max[i] = this.fields.get(i).name.length();
                        }
                    }
                    catch(Exception e){
                        System.out.println("0 Rows Found.");
                        return false;
                    }
                }
            }

            printTableAll(null,max);
            return true;
        }

        int flag = 0;

        for (String gf : givenFields){

            for(Field f : this.fields){

                if (f.name.equals(gf)){
                    flag = 0;
                    break;
                }
                flag = 1;
            }
            if(flag == 1){
                break;
            }
        }

        if (flag == 1){
            System.out.println("Invalid Column Name.");
            return false;
        }

        if(conditions != null){

            if(conditions.contains("and")){
                String [] cnd = conditions.split("and|AND");
                String [] pair1 = cnd[0].split("=");
                String [] pair2 = cnd[0].split("=");

                for(Field f : this.fields) {
                    if(f.name.equals(pair1[0].replaceAll("\"|'",""))){
                        break;
                    }
                    flag = 1;
                }

                if(flag == 1){
                    System.out.println("Invalid column name in where");
                    return false;
                }

                for(Field f : this.fields) {
                    if(f.name.equals(pair2[0].replaceAll("\"|\'",""))){
                        break;
                    }
                    flag = 1;
                }

                if(flag == 1){
                    System.out.println("Invalid column name in where");
                    return false;
                }

                ArrayList<Hashtable> data1 = new ArrayList<>();

                for(Hashtable t : this.rows){
                    if(t.get(pair1[0]) == pair1[1] && t.get(pair2[0]) == pair2[2]){
                        data1.add(t);
                    }
                }


            }
            else if(conditions.contains("or")){
                String [] cnd = conditions.split("or|OR");
                String [] pair1 = cnd[0].split("=");
                String [] pair2 = cnd[0].split("=");

                for(Field f : this.fields) {
                    if(f.name.equals(pair1[0].replaceAll("\"|\'",""))){
                        break;
                    }
                    flag = 1;
                }

                if(flag == 1){
                    System.out.println("Invalid column name in where");
                    return false;
                }

                for(Field f : this.fields) {
                    if(f.name.equals(pair2[0].replaceAll("\"|'",""))){
                        break;
                    }
                    flag = 1;
                }

                if(flag == 1){
                    System.out.println("Invalid column name in where");
                    return false;
                }

                ArrayList<Hashtable> data1 = new ArrayList<>();

                for(Hashtable t : this.rows){
                    if(t.get(pair1[0]) == pair1[1] || t.get(pair2[0]) == pair2[2]){
                        data1.add(t);
                    }
                }

            }

        }

        //checking maximum value

        int[] max = new int[givenFields.length];

        for(Hashtable t : this.rows){
            for (int i = 0; i< givenFields.length ; i++){
                try{
                    if(max[i] < t.get(givenFields[i]).toString().length()){
                        max[i] = t.get(givenFields[i]).toString().length();
                    }

                }
                catch (Exception e){
                    System.out.println("0 Rows found.");
                }
            }

        }

        for (int i = 0; i< givenFields.length ; i++){
            try{
                if(max[i] < givenFields[i].length()){
                    max[i] = givenFields[i].length();
                }
            }
            catch (Exception e){
                System.out.println("0 Rows found.");
            }
        }

        for (int i : max){
            System.out.println(i);
        }

        printTableAll(givenFields,max);


        return false;
    }

    public void printTableAll(String [] givenFields,int[] max){

        if(givenFields != null){

            System.out.print("+");
            //for column
            for (int i = 0; i < givenFields.length; i++) {

                int diff = max[i] - givenFields[i].length();

                for (int k = 0; k <  max[i]; k++) {
                    System.out.print("-");
                }

                System.out.print("+");

            }
            System.out.println();

            for (int i = 0; i < givenFields.length; i++) {

                System.out.print("|");

                System.out.print(givenFields[i]);

                for(int j = 0 ; j < max[i] - givenFields[i].length();j++){
                    System.out.print(" ");
                }

            }

            System.out.println("|");


            for(Hashtable row : rows) {

                System.out.print("+");

                for (int i = 0; i < givenFields.length; i++) {

                    int diff = max[i] - givenFields[i].length();

                    for (int k = 0; k <  max[i]; k++) {
                        System.out.print("-");
                    }

                    System.out.print("+");

                }
                System.out.println("");

                for (int i = 0; i < givenFields.length; i++) {

                    System.out.print("|");

                    System.out.print(row.get(givenFields[i]));

                    for(int j = 0 ; j < max[i] - row.get(givenFields[i]).toString().length();j++){
                        System.out.print(" ");
                    }

                }

                System.out.println("|");
            }

            System.out.print("+");

            for (int i = 0; i < givenFields.length; i++) {

                int diff = max[i] - givenFields[i].length();

                for (int k = 0; k < max[i]; k++) {
                    System.out.print("-");
                }

                System.out.print("+");
            }

        }
        else
        {
            System.out.print("+");

            for (int i = 0; i < this.fields.size(); i++) {


                for (int k = 0; k <  max[i]; k++) {
                    System.out.print("-");
                }

                System.out.print("+");

            }

            System.out.println();

            for (int i = 0; i < this.fields.size(); i++) {

                System.out.print("|");

                System.out.print(this.fields.get(i).name);

                for(int j = 0 ; j < max[i] - (this.fields.get(i).name).length(); j++){
                    System.out.print(" ");
                }

            }

            System.out.println("|");


            for(Hashtable row : rows) {


                System.out.print("+");

                for (int i = 0; i < this.fields.size(); i++) {


                    for (int k = 0; k <  max[i]; k++) {
                        System.out.print("-");
                    }

                    System.out.print("+");

                }

                System.out.println();

                for (int i = 0; i < this.fields.size(); i++) {

                    System.out.print("|");

                    System.out.print(row.get(this.fields.get(i).name));

                    for(int j = 0 ; j < max[i] - (row.get(this.fields.get(i).name)).toString().length(); j++){
                        System.out.print(" ");
                    }

                }

                System.out.println("|");

            }

            System.out.print("+");

            for (int i = 0; i < this.fields.size(); i++) {


                for (int k = 0; k <  max[i]; k++) {
                    System.out.print("-");
                }

                System.out.print("+");

            }

        }
        System.out.println("");

    }

    public boolean insertRows(String[] fields, String value, User user){

        int flag = 0;

        String[] values = value.split(",");

        if(fields.length != values.length){
            System.out.println("Number of Column and Number of value Mismatch.");
            return false;
        }

        //removing spaces
        for (int i =0 ; i<fields.length;i++){
            fields[i] = fields[i].trim();
            values[i] = values[i].trim();
        }

        if(fieldNotExist(fields)){
            return false;
        }

        Hashtable <String,String> newRow = new Hashtable<>();

        for (int i = 0 ; i < fields.length; i++){

            for (Field f : this.fields){

                if (f.name.equals(fields[i])){
                    if(!f.primaryKey){
                        if(f.validate(values[i])){
                            if(f.type.equalsIgnoreCase("varchar")){
                                values[i]= values[i].replaceAll("\"","");
                            }
                            newRow.put(f.name,values[i]);
//                            System.out.println(values[i]);
                            continue;
                        }
                        else{
                            System.out.println("Invalid type of value for field -> "+ f.name);
                            return false;
                        }
                    }
                    else{
                        // validate value before inserting data
                    }

                }
                else{

                }

            }

        }
        this.rows.add(newRow);
        this.writeTable(user);

        return true;
    }

    public boolean updateRecords(String newValField,String condition,User user){

        String[] conditions;
        int flag =0;

        String[] fieldsval = newValField.split(",");

        String[] fields = new String[fieldsval.length];
        String [] val = new String[fieldsval.length];

        for(int i=0; i< fieldsval.length;i++)
        {
            fields[i] = fieldsval[i].split("=")[0];
            val[i] = fieldsval[i].split("=")[1];
        }

        if(fieldNotExist(fields)){
            return false;
        }

        if(condition != null){
            if(condition.equalsIgnoreCase("AND")){
                conditions = condition.split("AND");

            }
            else{
                conditions = condition.split("||");
            }
        }

        for(Hashtable row: this.rows){
            for(int i = 0; i < fields.length; i++){
                for(Field f : this.fields){
                    if(f.name.equalsIgnoreCase(fields[i].trim())){
                        row.put(f.name,val[i]);
                        break;
                    }
                    else{
                        continue;
                    }
                }
            }
        }

        writeTable(user);

        return true;
    }

    public boolean deleteRecord(User user, Matcher m){

        if(m.groupCount() > 1){
            String[] conditions = m.group(2).replaceFirst("where","").split("(and|or)");

            String[] fields = new String[conditions.length];
            String[] vals = new String[conditions.length];
            String[] ex = new String[conditions.length];

            String[] rel = new String[conditions.length-1];

            for (int i = 0; i < conditions.length; i++)
            {

                fields[i] = m.group(3+i);
                ex[i] = m.group(4+i);
                vals[i] = m.group(5+i);

                try{
                    rel[i] = m.group(6);
                }
                catch(Exception e)
                {

                }
            }

            if(fieldNotExist(fields)){
                return  false;
            }

            int k=0;
            for (Hashtable row: this.rows){

                if (rel[k] == "and") {

                    for(int i = 0; i < fields.length; i++) {
                        if(row.get(fields[i]).toString().equals(vals[i])){

                        }

                    }
                }
                else if(rel[k] == "or"){

                }

            }

        }


        this.rows.clear();
        writeTable(user);
        return  true;
    }

    public boolean fieldNotExist(String [] fields){

        int flag = 0;

        for (int i = 0; i < fields.length;i++){

            for(Field f : this.fields){

                if(f.name.equals(fields[i].trim())){
                    flag = 0;
                    break;
                }
                else{
                    flag = 1;
                    continue;
                }

            }
            if(flag == 1){
                System.out.println("Invalid column name found.");
                return true;
            }

        }
        return false;
    }

    public void writeTable(User user){
        try{
            FileWriter fw = new FileWriter(user.userDBpath+"/"+this.name);
            fw.write(__strMeta__());
            for (Hashtable row:this.rows){

                fw.write(__strRow__(row)+"\n");
            }
            fw.close();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void writeMeta(User user){
        try{
            FileWriter fw = new FileWriter(user.userDBpath+"/"+this.name);
            fw.write(this.__strMeta__());
            fw.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public String __strRow__(Hashtable row){
        String temp = "";
        for(Field f: this.fields){
            if(row.get(f.name) == null){
                temp += null +";" ;
            }
            else{
                temp += row.get(f.name).toString()+";" ;
            }

        }
        return temp;
    }



    public  String __strMeta__(){
        String temp = "";
        String temp2 = "";
        String temp3 = "";

        for (Field field : fields){
            temp += field.__strName__()+";";
            temp2 += field.__strType__()+";";
            temp3 += field.__strSize__()+";";
        }
        temp += "\n";
        temp += temp2 + "\n";
        temp += temp3 + "\n";

        return  temp;
    }
}
class Field {
    static String[] Types = {"STRING","INT","INTEGER","BOOLEAN"};
    String name;
    String type;
    String size;
    boolean notNull = false;
    boolean primaryKey = false;

    public String __strName__() {
        return this.name;
    }

    public String __strType__() {
        return this.type;
    }

    public String __strSize__() {
        return this.size;
    }

    public void setSize(String size) {
        size = size.replace("(","");
        size =size.replace(")","");
        this.size = size;
    }

    public boolean validate(String value){

        String type = this.type;

        if(type.equalsIgnoreCase("VARCHAR")){
            if(value.length() <= 30)
            {
                return true;
            }
        }

        if(type.equalsIgnoreCase("BOOLEAN") || type.equalsIgnoreCase("BOOL")){
            if(value.equalsIgnoreCase("TRUE") || value.equalsIgnoreCase("FALSE")){
                return true;
            }
            else{
                return false;
            }
        }

        if(type.equalsIgnoreCase("int") || type.equalsIgnoreCase("integer")){
            try{
                Integer.parseInt(value);
                return  true;
            }
            catch (Exception e){
                return false;
            }
        }

        if(this.type.equalsIgnoreCase("FLOAT")){
            try{
                Float.parseFloat(value);
                return true;
            }
            catch (Exception e){
                return false;
            }
        }

        return false;

    }
}

