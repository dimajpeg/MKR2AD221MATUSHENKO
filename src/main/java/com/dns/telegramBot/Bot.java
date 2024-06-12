package com.dns.telegramBot;

import com.dns.telegramBot.services.SendMessageService;
import com.dns.telegramBot.services.SendMessageServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Bot extends TelegramLongPollingBot {

    @Value("WeatherAmazingBot")
    private String botUsername;

    @Value("7184218466:AAHZyHEcyKgPE2J3kfJ8dq0tX6WlG7bKdBk")
    private String botToken;

    private final SendMessageService sendMessageService;

    public Bot(SendMessageServiceImpl sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var sm = sendMessageService.send(update.getMessage());

        try {
            this.execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}