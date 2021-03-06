/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PiBot;

import java.awt.Color;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;

/**
 *
 * @author der-teufel
 */
public class CommandHandler implements MessageCreateListener {

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
	if (event.getMessageAuthor().isBotUser()) return;
        switch (event.getMessageContent().charAt(0)) {
            case '!' ->
                handleAdminCommand(event);
            case '&' ->
                handleTeXCommand(event);
            case '=' ->
                handleMaximaCommand(event);
        }
    }

    // Admin commands prefixed with `!`
    void handleAdminCommand(MessageCreateEvent event) {
    }

    // TeX commands prefixed with `&`
    void handleTeXCommand(MessageCreateEvent event) {
        String[] args = event.getMessageContent().split(" ", 2);
        String user = event.getMessageAuthor().getIdAsString();

    }

    // Math/Maxima commands prefixed with `=`
    void handleMaximaCommand(MessageCreateEvent event) {
        String maxima = event.getMessageContent().substring(1);
        String command = String.format("maxima --very-quiet --batch-string=tex(%s);", maxima);
        String result;
        try {
            result = new String(Runtime.getRuntime().exec(command).getInputStream().readAllBytes());
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            sendErrorMessage(e, event.getChannel());
            return;
        }
        String tex_code;
        try {
            tex_code = result.split("[$]")[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println(e.getLocalizedMessage());
            sendErrorMessage(e, event.getChannel());
            return;
        }
        String tex = "\\documentclass[margin=.5cm]{standalone}\n"
                + "\\usepackage{amsmath,amsfonts,MnSymbol,cancel,listings,xcolor}\n"
                + "\\usepackage{graphicx,tikz}\n"
		+ "\\usepackage[g]{esvect}\n"
                + "\\usetikzlibrary{decorations.pathreplacing, calc, arrows}\n"
                + "\\begin{document}\n"
                + "$\\displaystyle\n"
                + tex_code
                + "$\n"
                + "\\end{document});";
        
        File img = convertTeXtoImg(tex);
        
        if (img != null) {
            new MessageBuilder()
                    .setEmbed(new EmbedBuilder()
                                        .addField("Input:", maxima)
					.addField("Result:", tex_code)
                                        .setImage(img)
                                        .setColor(Color.BLUE))
                    .send(event.getChannel());
        } else {
            new MessageBuilder()
                    .setEmbed(new EmbedBuilder()
                                        .addField("Error!", "")
                                        .setColor(Color.RED))
                    .send(event.getChannel());
        }
    }

    // Help prefixed with `?`
    
    
    
    //Utils
    File convertTeXtoImg(String tex) {
        try {
            try (FileWriter writer = new FileWriter("./unknown.tex")) {
                writer.write(tex);
            }
	    try {
            	Runtime.getRuntime().exec("lualatex unknown.tex").waitFor();
            	Runtime.getRuntime().exec("pdftoppm -png -r 300 -singlefile unknown.pdf unknown").waitFor();
	    } catch (InterruptedException e) {
		    System.err.println(e);
		    return null;
	    }
        } catch (IOException e) {
            System.err.println(e);
            return null;
        }
        return new File("./unknown.png");
    }

    File convertTeXtoImg(String tex, String name) {
        try {
            try (FileWriter writer = new FileWriter("./" + name + ".tex")) {
                writer.write(tex);
            }
	    try {
            	Runtime.getRuntime().exec("lualatex " + name + ".tex").waitFor();
            	Runtime.getRuntime().exec("pdftoppm -png -r 300 -singlefile " + name + ".pdf " + name).waitFor();
	    } catch (InterruptedException e) {
		    System.err.println(e);
		    return null;
	    }
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            return null;
        }
        return new File("./" + name + ".png");
    }

    void sendErrorMessage(Exception e, TextChannel c) {
        new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                                    .addField("Error:", e.toString())
                                    .setColor(Color.RED))
                .send(c);
    }
}
