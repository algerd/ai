/**
 *  Desc: Inherit from this to create a class capable of reading
 *        parameters from an ascii file
 *
 *        instantiate this class with the name of the parameter file. Then
 *        call the helper functions to retrieve the data. 
 */
package common.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class iniFileLoaderBase {

    //the file the parameters are stored in
    private BufferedReader file;
    private String currentLine = "";
    //this is set to true if the file specified by the user is valid
    private boolean goodFile;
    
    public iniFileLoaderBase(URL filename) {
        currentLine = "";
        goodFile = true;
        try {
            file = new BufferedReader(new FileReader(new File(filename.toURI())));
        } catch (FileNotFoundException ex) {
            goodFile = false;
        } catch(URISyntaxException ex) {
            goodFile = false;
        }
    }
    
    /**
     * removes any commenting from a line of text.
     */
    public static String RemoveCommentingFromLine(String line) {
        //search for any comment and remove
        int idx = line.indexOf("//");
        if (idx != -1) {
            //cut out the comment
            return line.substring(0, idx);
        }
        return line;
    }

    /**
     * given a line of text this function removes the parameter description
     * and returns just the parameter as a String
     */
    private String getParameterValueAsString(String line) {
        //define some delimiters
        final String delims = "[ ;=,]";
        final Pattern pattern = Pattern.compile(delims);
        String[] s = pattern.split(line);
        return (s.length > 0) ? s[s.length - 1] : "";
    }

    /**
     * searches the text file for the next valid parameter. 
     * Discards any comments and returns the value as a string.
     */
    String getNextParameter() throws IOException {

        //this will be the string that holds the next parameter
        String line;
        line = file.readLine();
        line = RemoveCommentingFromLine(line);

        //if the line is of zero length, get the next line from the file
        if (line.length() == 0) {
            return getNextParameter();
        }
        return getParameterValueAsString(line);
    }
    
    /**
     * ignores any commenting and gets the next delimited string.
     */
    String getNextToken() throws IOException {
        //strip the line of any commenting
        while (currentLine.equals("")) {
            currentLine = file.readLine();
            currentLine = RemoveCommentingFromLine(currentLine);
        }
        //find beginning of parameter description
        int begIdx = currentLine.length();
        int endIdx = currentLine.length();

        //define some delimiters
        final String delims = "[ ;=,]+";
        final Pattern pattern = Pattern.compile(delims);
        Matcher matcher = pattern.matcher(currentLine);

        //find the end of the parameter description
        if (matcher.find()) {
            begIdx = matcher.end();
            endIdx = (matcher.find()) ? matcher.start() : currentLine.length();
        }
        currentLine = (endIdx != currentLine.length()) ? currentLine.substring(endIdx + 1, currentLine.length()) : "";       
        return currentLine.substring(begIdx, endIdx);
    }
    
    /**
     * helper methods. They convert the next parameter value found into the relevant type
     */
    public double getNextParameterDouble() throws IOException {
        if (goodFile) {
            return Double.valueOf(getNextParameter());
        }
        throw new RuntimeException("bad file");
    }

    public float getNextParameterFloat() throws IOException {
        if (goodFile) {
            return Float.valueOf(getNextParameter());
        }
        throw new RuntimeException("bad file");
    }

    public int getNextParameterInt() throws IOException {
        if (goodFile) {
            return Integer.valueOf(getNextParameter());
        }
        throw new RuntimeException("bad file");
    }

    public boolean getNextParameterBool() throws IOException {
        if (goodFile) {
            return 0 != Integer.valueOf(getNextParameter());
        }
        throw new RuntimeException("bad file");
    }

    public double getNextTokenAsDouble() throws IOException {
        if (goodFile) {
            return Double.valueOf(getNextToken());
        }
        throw new RuntimeException("bad file");
    }

    public float getNextTokenAsFloat() throws IOException {
        if (goodFile) {
            return Float.valueOf(getNextToken());
        }
        throw new RuntimeException("bad file");
    }

    public int getNextTokenAsInt() throws IOException {
        if (goodFile) {
            return Integer.valueOf(getNextToken());
        }
        throw new RuntimeException("bad file");
    }

    public String getNextTokenAsString() throws IOException {
        if (goodFile) {
            return getNextToken();
        }
        throw new RuntimeException("bad file");
    }

    public boolean eof() throws IOException {
        if (goodFile) {
            return !file.ready();
        }
        throw new RuntimeException("bad file");
    }

    public boolean fileIsGood() {
        return goodFile;
    }

}
