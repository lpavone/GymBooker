package com.lpavone.gymbooker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leonardo on 15/03/17.
 */

class CsvUtils {

    private final char DEFAULT_SEPARATOR = ',';

    public void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, '"');
    }

    public FileWriter createFileWriter(String user) throws IOException {
        return new FileWriter( "/home/leonardo/" + user + Constants.FILENAME);
    }

    public void flushContent(FileWriter writer) throws IOException {
        writer.flush();
        writer.close();
    }

    //https://tools.ietf.org/html/rfc4180
    private  String followCVSformat(String value) {

        String result = value;
        if (result != null && result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    private void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }

    public List<String> readCsv(String filename){

        String line;
        List<String> content = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("/home/leonardo/" + filename))) {
            while ((line = br.readLine()) != null) {
                content.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}

