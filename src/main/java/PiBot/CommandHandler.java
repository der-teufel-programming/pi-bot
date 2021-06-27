/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PiBot;

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
            new MessageBuilder().append(maxima, MessageDecoration.CODE_LONG).addAttachment(img).send(event.getChannel());
        } else {
            new MessageBuilder().append("Error!", MessageDecoration.BOLD).send(event.getChannel());
        }
    }

    // Help prefixed with `?`
    //Utils
    File convertTeXtoImg(String tex) {
        try {
            try (FileWriter writer = new FileWriter("./unknown.tex")) {
                writer.write(tex);
            }
            System.out.println(new String(Runtime.getRuntime().exec("lualatex unknown.tex").getInputStream().readAllBytes()));
            System.out.println(new String(Runtime.getRuntime().exec("pdftoppm -png -r 300 -singlefile unknown.pdf unknown").getInputStream().readAllBytes()));
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            return null;
        }
        return new File("./unknown.png");
    }

    File convertTeXtoImg(String tex, String name) {
        try {
            try (FileWriter writer = new FileWriter("./" + name + ".tex")) {
                writer.write(tex);
            }
            System.out.println(new String(Runtime.getRuntime().exec("lualatex " + name + ".tex").getInputStream().readAllBytes()));
            System.out.println(new String(Runtime.getRuntime().exec("pdftoppm -png -r 300 -singlefile " + name + ".pdf " + name).getInputStream().readAllBytes()));
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            return null;
        }
        return new File("./" + name + ".png");
    }

    void sendErrorMessage(Exception e, TextChannel c) {
        new MessageBuilder()
                .append("Error!", MessageDecoration.BOLD).appendNewLine()
                .setEmbed(new EmbedBuilder().addField("Message:", e.getLocalizedMessage())
                                            .addField("Stacktrace:", e.getStackTrace().toString())).send(c);
    }
}
