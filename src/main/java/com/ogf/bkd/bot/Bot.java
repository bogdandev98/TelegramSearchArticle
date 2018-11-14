package com.ogf.bkd.bot;

import com.ogf.bkd.Article;
import com.ogf.bkd.Response;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    @Override
    public String getBotToken() {
        return "483190807:AAEL1xQx3Xvx9b00jWQjq_ZDGJ62ocsL_vY";
    }
    private Response response;
    private TypeMessage typeMessage= TypeMessage.SIMPLE;
    public void answerOnMessage(Message msg) throws TelegramApiException {
        String txt = msg.getText();
        if (txt.equals("/start")) {
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow row =new KeyboardRow();
            row.add("/help");
            keyboard.add( row);
            row =new KeyboardRow();
            row.add("/new");
            keyboard.add( row);
            keyboardMarkup.setKeyboard(keyboard).setOneTimeKeyboard(true);
            execute(new SendMessage().setChatId(msg.getChatId()).setReplyMarkup(keyboardMarkup).setText("Welcome! Type /help to get help."));
            System.out.println(msg.getFrom().getUserName());
        }else if (txt.equals("/new")) {
            sendMsg(msg.getChatId(), "*Write your query*", null);
            typeMessage = TypeMessage.NEW_QUERY;
        }else if (txt.equals("/help")){
            sendMsg(msg.getChatId(), "Type /new ", null);
        }else if (typeMessage != TypeMessage.SIMPLE) {
            if (typeMessage == TypeMessage.NEW_QUERY) {
                this.response = new Response(txt);
            } else if (typeMessage == TypeMessage.COUNTRY) {
                this.response.setCountry(txt);
            } else if (typeMessage == TypeMessage.NUMBER_ARTICLE) {
                try {
                    this.response.setNumberArticle(Integer.parseInt(txt));
                } catch (NumberFormatException e) {
                } catch (IllegalArgumentException e){
                    sendMsg(msg.getChatId(), e.getMessage(), null);
                }
            }
            execute(getInformMessage().setChatId(msg.getChatId()));
            typeMessage = TypeMessage.SIMPLE;
        }
    }
    public void answerOnCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException{
        String data=callbackQuery.getData();
        if (data.equals("run")) {
            response.runQuery();
            for (Article article : response.getRecords()) {
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
                rowInline.add(new InlineKeyboardButton().setText("Download all article").setUrl(article.getUrl()));
                rowsInline.add(rowInline);
                keyboardMarkup.setKeyboard(rowsInline);
                sendMsg(callbackQuery.getFrom().getId(), article.toString(), keyboardMarkup);
            }
        }else if (data.equals("openaccess")){
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
            rowInline.add(new InlineKeyboardButton().setText("All").setCallbackData("openaccess_all"));
            rowInline.add(new InlineKeyboardButton().setText("Only open").setCallbackData("openaccess_open"));
            rowsInline.add(rowInline);
            keyboardMarkup.setKeyboard(rowsInline);
            sendMsg(callbackQuery.getFrom().getId(), "Select the type access.", keyboardMarkup);
        }else if (data.equals("country")){
            sendMsg(callbackQuery.getFrom().getId(), "Write authored in a particular country.", null);
            typeMessage= TypeMessage.COUNTRY;
        }else if (data.equals("count_article")){
            sendMsg(callbackQuery.getFrom().getId(), "Write max number article\n"+"The number of articles can not be more than 100 and less than 1", null);
            typeMessage= TypeMessage.NUMBER_ARTICLE;
        }else if (data.equals("openaccess_all")){
            response.setOpenaccess(false);
            sendMessage(getInformMessage().setChatId((long)(callbackQuery.getFrom().getId())));
        }else if (data.equals("openaccess_open")){
            response.setOpenaccess(true);
            sendMessage(getInformMessage().setChatId((long)(callbackQuery.getFrom().getId())));
        }
    }
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText())
                answerOnMessage(update.getMessage());
            if (update.hasCallbackQuery())
                answerOnCallbackQuery(update.getCallbackQuery());
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
    public SendMessage getInformMessage(){
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        rowInline.add(new InlineKeyboardButton().setText("Change type access").setCallbackData("openaccess"));
        rowInline.add(new InlineKeyboardButton().setText("Change country").setCallbackData("country"));
        rowInline1.add(new InlineKeyboardButton().setText("Change max count article").setCallbackData("count_article"));
        rowInline1.add(new InlineKeyboardButton().setText("Run query").setCallbackData("run"));
        rowsInline.add(rowInline);
        rowsInline.add(rowInline1);
        keyboardMarkup.setKeyboard(rowsInline);
        StringBuilder text = new StringBuilder("*Your query:\n*");
        text.append("Key word: _"+response.getKeyWord()+"_;\n");
        text.append("Type access: ");
        if (response.getOpenaccess()){
            text.append("_only open;_\n");
        }else {
            text.append("_all;_\n");
        }
        text.append("Country: _"+response.getCountry()+"_;\n");
        text.append("Max number article: _"+response.getNumberArticle()+"_;\n");
        SendMessage message = new SendMessage();
        message.setText(text.toString()).setReplyMarkup(keyboardMarkup).setParseMode(ParseMode.MARKDOWN);
        return message;
    }

    @Override
    public String getBotUsername() {
        return "MedicalArticleBot";
    }

    private void sendMsg(long chatId, String text, InlineKeyboardMarkup markup)throws TelegramApiException {
        SendMessage s = new SendMessage();
        s.setChatId(chatId); // Боту может писать не один человек, и поэтому чтобы отправить сообщение, грубо говоря нужно узнать куда его отправлять
        s.setText(text);
        if (markup!=null)
            s.setReplyMarkup(markup);
        s.setParseMode(ParseMode.MARKDOWN);
        execute(s);
    }
}