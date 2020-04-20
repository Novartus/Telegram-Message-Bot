package com.novartus.Robot;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.toIntExact;


public class RohBot extends TelegramLongPollingBot {

    SendMessage message;
    long  found;
    final String apiToken="123456789:ReplaceTHisWithYourToken";
    String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
    final String bot_name ="YourBotName";
    final long  owner_chat_id = 0000000; //Replace This WIth Owner Chat ID {Channel Admin}
    String photo_id;

    @Override
    public void onUpdateReceived(Update update) {

        String user_first_name ="-" ;
        String user_last_name = "-" ;
        String user_username = "-" ;

        int uid = toIntExact(update.getMessage().getChat().getId());  //Long -> Integer
        long chat_id = update.getMessage().getChatId();

        String msg = update.getMessage().getText();

        if (update.hasMessage() && update.getMessage().hasText()) {
             user_first_name = update.getMessage().getChat().getFirstName();
             user_last_name = update.getMessage().getChat().getLastName();
             user_username = update.getMessage().getChat().getUserName();

            switch (msg.toLowerCase()) {    //STOPS CASE SENSITIVITY

                case "/help":
                case "help" : {
                    message = new SendMessage()
                            .setChatId(chat_id)
                            .setText("1. /start : Let's get started" +
                                    "\n2. /interested : Register Your Self in Giveaway" +
                                    "\n3. UnderDevelopment More Functions Coming Soon"+
                                    "\n About Me ? : www.github.com/Novartus");
                    try {   // Sending  message object to user
                        this.execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "/start" : {
                    message = new SendMessage()
                            .setChatId(chat_id)
                            .setText("Welcome To the Premium Account Giveaway please submit: \n /interested \nTo Enter in Giveaway ");
                    try {   // Sending  message object to user
                        this.execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case "/interested" : {
                    registration_db_user(user_first_name, user_last_name, uid, user_username, chat_id);
                    if (found == 0) {
                        message = new SendMessage()
                                .setChatId(chat_id)
                                .setText("Response Added");

                        urlString = String.format(urlString, apiToken, owner_chat_id, "Response From: @"+user_username);

                        try {   // Sending  message object to user
                            this.execute(message);
                            URL url = new URL(urlString);
                            URLConnection conn = url.openConnection();
                            InputStream is = new BufferedInputStream(conn.getInputStream());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        message = new SendMessage()
                                .setChatId(chat_id)
                                .setText("Response is already recorded ");

                        try {   // Sending  message object to user
                            this.execute(message);
                            //  log(user_first_name, user_last_name, uid,user_username,message_text,bot_reply);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }

                case "/reset_photo_db":{
                    if (chat_id == owner_chat_id) {
                        message = new SendMessage()
                                .setChatId(chat_id)
                                .setText("As You Wish Master\n Photo Database is reset.");
                        registration_db_deleteall_photos();

                        try {   // Sending  message object to user
                            this.execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else {
                        urlString = String.format(urlString, apiToken, owner_chat_id, "Intruder was: @"+user_username);

                        message = new SendMessage()
                                .setChatId(chat_id)
                                .setText("YOU ARE NOT AUTHORIZED.\n This Incident will be reported");
                        try {   // Sending  message object to user
                            this.execute(message);
                            URL url = new URL(urlString);
                            URLConnection conn = url.openConnection();
                            InputStream is = new BufferedInputStream(conn.getInputStream());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }

                case "/reset_userdb":{
                    if (chat_id == owner_chat_id) {
                        message = new SendMessage()
                                .setChatId(chat_id)
                                .setText("As You Wish Master\n User Database is reset.");
                         registration_db_deleteall_users();


                        try {   // Sending  message object to user
                            this.execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else {
                        urlString = String.format(urlString, apiToken, owner_chat_id, "Intruder was: @"+user_username);

                        message = new SendMessage()
                                .setChatId(chat_id)
                                .setText("YOU HAVE NO AUTHORIZED ACCESS");
                        try {   // Sending  message object to user
                            this.execute(message);
                            URL url = new URL(urlString);
                            URLConnection conn = url.openConnection();
                            InputStream is = new BufferedInputStream(conn.getInputStream());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }

            }
        }

    else

     if (update.hasMessage() && update.getMessage().hasPhoto()) {
          user_first_name = update.getMessage().getChat().getFirstName();
          user_last_name = update.getMessage().getChat().getLastName();
          user_username = update.getMessage().getChat().getUserName();

            List<PhotoSize> photos = update.getMessage().getPhoto();

            String f_id = photos.stream()
                    .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                    .findFirst()
                    .orElse(null).getFileId();

            String caption =("Photo is recorded :) \n From :@"+user_username+ "\n\nFile_id: " + f_id);
            SendPhoto message_photo = new SendPhoto()
                    .setChatId(owner_chat_id)
                    .setPhoto(f_id)
                    .setCaption(caption);
         photo_id = f_id;
         registration_db_photos(user_first_name ,  user_last_name ,  uid,  user_username, chat_id,  photo_id);

         try {
                this.execute(message_photo); // Call method to send the photo with caption

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

    
    @Override
    public String getBotUsername() {
        return bot_name;
    }

    @Override
    public String getBotToken() {
        return apiToken;
    }


        //  DATABASE FUNCTIONS

    final String atlas_url ="mongodb+srv://replace this url with your atlas MongoDB URL "; //To use ocal host replace this string with local host

    private String registration_db_user(String user_first_name , String user_last_name , int uid, String user_username, long chatid) {
        MongoClientURI connectionString = new MongoClientURI(atlas_url);
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("database_bot");
        MongoCollection<Document> collection = database.getCollection("registered_users");
        found = collection.countDocuments(Document.parse("{ uid : " + uid + "}"));
        if (found == 0) {
            Document doc = new Document("first_name", user_first_name)
                    .append("last_name", user_last_name)
                    .append("username", user_username)
                    .append("chatID" , chatid)
                    .append("uid", uid);

            collection.insertOne(doc);
            mongoClient.close();
            System.out.println("New User Added in Database.");
            return "new_user";
        } else {
            System.out.println("User is already exists in database.");
            mongoClient.close();
            return "existing_user";
        }


    }

    private String registration_db_deleteall_users() {
        MongoClientURI connectionString = new MongoClientURI(atlas_url);
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("database_bot");
        MongoCollection<Document> collection = database.getCollection("registered_users");
        collection.drop();
        mongoClient.close();
        System.out.println("Collection Users DROP");
        return "deleted_all_users";
    }

    private String registration_db_deleteall_photos(){
        MongoClientURI connectionString = new MongoClientURI(atlas_url);
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("database_bot");
        MongoCollection<Document> collection = database.getCollection("users_photos");
        collection.drop();
        mongoClient.close();
        System.out.println("Collection Photos DROP");
        return "deleted_all_photos";
    }

    private String registration_db_photos(String user_first_name , String user_last_name , int uid, String user_username, long chat_id, String photo_id) {
        MongoClientURI connectionString = new MongoClientURI(atlas_url);
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("database_bot");
        MongoCollection<Document> collection = database.getCollection("users_photos");
        found = collection.countDocuments(Document.parse("{ uid : " + uid + "}"));
        if (found == 0) {
            Document doc = new Document("first_name", user_first_name)
                    .append("last_name", user_last_name)
                    .append("username", user_username)
                    .append("chatID" , chat_id)
                    .append("uid", uid)
                    .append("File_ID", photo_id);

            collection.insertOne(doc);
            mongoClient.close();
            System.out.println("Photo Added in DataBase.");
            return "photo_added";
        } else {
            System.out.println("photo already exists in DataBase.");
            mongoClient.close();
            return "photo_exists";
        }
    }
}