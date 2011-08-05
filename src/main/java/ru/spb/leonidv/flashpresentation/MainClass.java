package ru.spb.leonidv.flashpresentation;

import java.io.File;
import java.io.Serializable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import ru.spb.leonidv.flashpresentation.gui.MainFrame;
import ru.spb.leonidv.flashpresentation.presentation.ImageInfo;
import ru.spb.leonidv.flashpresentation.presentation.Presentation;
import ru.spb.leonidv.flashpresentation.presentation.PresentationEvent;

public class MainClass implements Serializable {

    private static final long serialVersionUID = -438204421659891568L;

    /**
     * @param args
     */
    public static void main(String[] args) {
        Options options = new Options();

        options.addOption("h", "help", false, "Display this message");
        options.addOption("s", "silent", false, "Run in silent mode, without GUI. "
                + "Gallery.XML will build automatically with all "
                + "images in current folder. The server info and "
                + " title message must be added by hands.");
        CommandLineParser parser = new PosixParser();
        try {
            CommandLine commands = parser.parse(options, args);
            if (commands.hasOption("h")) {
                new HelpFormatter().printHelp("test", options);
                return;
            }

            if (commands.hasOption("s")) {
                silentWork();
            } else {
                MainFrame.showWindow();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void silentWork() {

        PresentationEvent eventsHandler = new PresentationEvent() {
            int count = -1;

            public void imageLoaded(int index, ImageInfo imageInfo) {
                System.out.println("[" + index + "/" + count + "]" + imageInfo + "\n");
            }

            public void startLoading(int count) {
                this.count = count;
                if (count > 0) {
                    System.out.println("Founded " + count + " images." + "Start load process.\n");
                } else {
                    System.out.println("Nothing is found.");
                    System.exit(0);
                }
            }

            public void move(int source, int dest) {
            }

            public void remove(int index) {
            }

        };

        Presentation presentation = new Presentation();
        presentation.setEventsHandler(eventsHandler);
        presentation.loadFromFolder(new File("."));

        if (presentation.saveAsXML()) {
            System.out.println("Gallery.XML file successfull created.");
        }
    }
}
