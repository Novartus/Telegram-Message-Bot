package com.novartus;

import com.novartus.Robot.RohBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class StartBot {

    public static void main(String[] args) {

        ApiContextInitializer.init();   // Initialize Api Context

        TelegramBotsApi botsApi = new TelegramBotsApi(); //Instantiate Telegram Bots API

        try {                                   // Register Bot
            botsApi.registerBot(new RohBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


        System.out.println("Bot has successfully started!");
    }

}