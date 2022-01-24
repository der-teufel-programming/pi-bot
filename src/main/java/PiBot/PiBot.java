/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PiBot;

import org.javacord.api.*;

/**
 *
 * @author der-teufel
 */
public class PiBot {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Create the bot connection
        
        DiscordApi api = new DiscordApiBuilder()
                .setToken(`ENTER TOKEN HERE`)
                .login().join();

        api.addMessageCreateListener(new CommandHandler());

//        api.addMessageCreateListener(event -> {
//            if (event.getMessageContent().equalsIgnoreCase("!ping")) {
//                event.getChannel().sendMessage("Pong!");
//            } else if (event.getMessageContent().equalsIgnoreCase("!sleep") && event.getMessageAuthor().isBotOwner()) {
//                event.getChannel().sendMessage("Going to sleep! :sleeping:");
//                api.disconnect();
//            }
//        });
    }

}
