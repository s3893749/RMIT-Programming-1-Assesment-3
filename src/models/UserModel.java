//**** SET PACKAGE ****\\
package models;

//**** IMPORT PACKAGES ****\\
//Here we import all the relevant packages that we will be referencing, calling and accessing in this class.
import core.CustomArray;
import exceptions.InvalidFullnameException;
import exceptions.InvalidPasswordException;
import exceptions.InvalidUsernameException;
import intefaces.Savable;

import java.io.*;
import java.util.Objects;

//**** START CLASS ****\\
public class UserModel extends Model implements Savable {

    //Private Class Variables
    //-------------------------------------------------------------------------------------------
    //Here we declare our class wide instance variables, in this case we have two arrays, users and errors, both are final
    //users array, is final and created once by the constructor
    private CustomArray users;

    //**** CONSTRUCTOR ****\\
    //main constructor method for the UserModel class
    public UserModel() {
        //initialize the Users array to the value of the private loadUserAccounts method
        this.load("users.csv");
    }

    //**** CHECK USERNAME METHOD ****\\
    //Check username method, returns true if it finds a matching user with the username provided
    public void checkUsername(String username) throws InvalidUsernameException {

        //check if the array key exists
        if(!this.users.arrayKeyExists(username)){
            //if so set the outcome to true
            throw new InvalidUsernameException();
        }

    }

    //**** CHECK PASSWORD METHOD ****\\
    //accepts a username and password string, checks to see if the password provided matches the password for that user
    public boolean checkPassword(String username, String password){
        //create the outcome variable
        boolean outcome = false;
        //create the user based on the username
        User user = this.getUser(username);
        //now check if this is a valid user and not null and if the password matches
        if(user != null && user.getPassword().matches(password)){
            //if so set the outcome to true
            outcome = true;
        }
        //finally the return the outcome
        return outcome;
    }

    //**** GET USER METHOD ****\\
    //Returns the user matching the username that's parsed
    public User getUser(String username){
        //return the value for the key of that username
        return (User) this.users.getValue(username);
    }

    //**** VALIDATE NEW USERNAME ****\\
    //this method validates a new username request, it takes proposed username as a string and checks to make sure no
    //names match it, if there it a match it returns true
    public void validateNewUsername(String username) throws InvalidUsernameException{

        //declare the unique match to false by default
        boolean uniqueMatch  = false;

        //check to ensure the username is not blank, if not then proceed, else leave as false
        if(username != null && !username.isBlank()){
            //create a i variable to be our counter
            int i = 0;
            //create while loop to loop over of the users
            while(i < this.users.length()){
                //check if the two strings match, if so set unique match to true
                if(((User)this.users.getValues()[i]).getUsername().matches(username)){
                    //unique match true
                    uniqueMatch = true;
                }
                //increase the loop counter to the next user until we have checked all users
                i++;
            }
        }

        //finally we check to see if we have a unique match or if it is null, if so then we throw a InvalidUsernameException
        if(uniqueMatch || Objects.requireNonNull(username).isBlank()){
            throw new InvalidUsernameException();
        }
    }

    //**** VALIDATE NEW PASSWORD ****\\
    //this method is our password validator and ensuring that a password meets the criteria as stated in this method
    public void validatePassword(String password) throws InvalidPasswordException {

        //check if the password is not null or blank
        if(password == null || password.isBlank()){
            //if the password is null or blank we throw a InvalidPasswordException
            throw new InvalidPasswordException();
        }

    }

    //**** VALIDATE NEW FULLNAME ****\\
    //this model method validates the fullname input string and returns true or false
    public void validateFullname(String fullname) throws InvalidFullnameException {

        //check if the full name is not null and is not blank
        if(fullname == null || fullname.isBlank()){
            //If the fullname is null or blank we throw a InvalidFullnameException
            throw new InvalidFullnameException();
        }
    }

    //**** CREATE NEW USER ACCOUNT ****\\
    //this method accepts an array of account data that has been validated by the controller and uses it to create the object
    public void createUserAccount(CustomArray account){
        //create a new user object in the this.users array with the key as the users username
        this.users.add(new User((String)account.getValue("username"),(String)account.getValue("password"),(String)account.getValue("fullname"),Integer.parseInt((String) account.getValue("registrationDate")),Boolean.parseBoolean((String) account.getValue("isAdmin"))),(String) account.getValue("username"));
        //update the accounts CSV file on new account creation with the private this.saveUserAccounts method
        this.save("users.csv");
    }

    //**** GET ALL USERS ****\\
    //returns a string array of all the usernames in the users array
    public String getAllUsers(){
        //create the outcome string
        String outcome = "";

        //create our counter i
        int i  = 0;
        //create our while loop to count over each of our users
        while(i< this.users.length()){
            //add the user name to the outcome separated by a comers
            outcome += this.users.getKeys()[i]+",";
            //increase the counter and repeat
            i++;
        }
        return outcome;
    }

    //**** IMPLEMENTS SAVE METHOD ****\\
    //This method is the implementation of the save method implemented from the savable interface.
    @Override
    public void save(String path) {

        //set the tag for the file
        String tag = "Main Users CSV file";

        //open a try catch block to catch any errors that have been thrown
        try {
            // Creates a FileWriter
            FileWriter file = new FileWriter(path);

            // Creates a BufferedWriter
            BufferedWriter output = new BufferedWriter(file);

            //write the tags to the file
            output.write("//Tags: "+tag);
            //proceed to a new line
            output.newLine();

            //create our i counter variable used to count over our users array
            int i = 0;
            //while we have users to process run this code
            while(i<this.users.length()){
                //write the users values to the file separated by commas
                output.write(((User)this.users.getValues()[i]).getUsername()+","+((User)this.users.getValues()[i]).getPassword()+","+((User)this.users.getValues()[i]).getFullname()+","+((User)this.users.getValues()[i]).getRegistrationDate()+","+((User)this.users.getValues()[i]).getAdminStatus());
                //jump to the next line
                output.newLine();
                //increase our counter and run again
                i++;
            }

            // Closes the writer
            output.close();

        } catch (IOException e) {
            //if we catch any errors add them to the errors array under the key error
            this.errors.add("Cannot save users.csv file, please check you have a csv file in this folder called '" +
                    "users.csv' and restart the program\nJava Error: "+e.toString(),"error");
        }
    }

    //**** IMPLEMENTS LOAD METHOD ****\\
    //This method is the implementation of the load method implemented from the savable interface.
    @Override
    public void load(String path) {

        //create an array of users with the accepting object the User.class
        CustomArray users = new CustomArray(User.class);

        //create a try catch loop to catch any errors
        try {
            //create a instance of the buffered reader
            BufferedReader br = new BufferedReader(new FileReader(path));

            //create a line String variable
            String line;
            //start a while loop to get the lines in if they are not null
            while((line = br.readLine()) != null){
                //ignore any lines that have the \\ comment marker
                if(!line.contains("//")){
                    //split the lines values apart by the ,
                    String[] values = line.split(",");
                    //create a user using the new values array and convert the and check if the user has the admin true variable or not
                    if(Boolean.parseBoolean(values[4])){
                        //if so create a new admin user
                        users.add(new AdminUser(values[0], values[1], values[2], Integer.parseInt(values[3]), Boolean.parseBoolean(values[4])),values[0]);
                    }else {
                        //else create a standard user
                        users.add(new User(values[0], values[1], values[2], Integer.parseInt(values[3]), Boolean.parseBoolean(values[4])), values[0]);
                    }
                }
            }
            //finally close our buffered reader
            br.close();

        } catch (IOException e) {
            //if we catch any errors add them to the this.errors file
            this.errors.add("Cannot load users.csv file, please check you have a csv file in this folder called '" +
                    "users.csv' and restart the program\nJava Error: "+e.toString(),"error");
        }

        //return the new users array
        this.users =  users;
    }
}
