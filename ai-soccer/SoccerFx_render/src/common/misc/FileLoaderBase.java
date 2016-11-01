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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileLoaderBase {

    //the file the parameters are stored in
    private BufferedReader file;
    private String CurrentLine = "";
     //this is set to true if the file specified by the user is valid
    private boolean goodFile;
    
    public FileLoaderBase(URL filename) {
        CurrentLine = "";
        goodFile = true;
        try {
            file = new BufferedReader(new FileReader(new File(filename.toURI())));
        } catch (FileNotFoundException ex) {
            goodFile = false;
        } catch(URISyntaxException ex) {
            goodFile = false;
        }
    }
	
	public FileLoaderBase(InputStream stream) {
		CurrentLine = "";
        goodFile = true;
		file = new BufferedReader(new InputStreamReader(stream));
	}

    //-------------------------- GetParameterValueAsString ------------------------
    //
    // given a line of text this function removes the parameter description
    // and returns just the parameter as a String
    //-----------------------------------------------------------------------------
    private String getParameterValueAsString(String line) {
        //find beginning of parameter description
        int begIdx;
        int endIdx;

        //define some delimiters
        final String delims = "[ ;=,]";
        final Pattern pattern = Pattern.compile(delims);
        String[] s = pattern.split(line);
        if (s.length > 0) {
            return s[s.length - 1];
        }
        return "";
    }

    //----------------------- GetNextParameter ------------------------------------
    //
    //  searches the text file for the next valid parameter. Discards any comments
    //  and returns the value as a string
    //-----------------------------------------------------------------------------
    String getNextParameter() throws IOException {

        //this will be the string that holds the bext parameter
        String line;
        line = file.readLine();
        line = removeCommentingFromLine(line);

        //if the line is of zero length, get the next line from the file
        if (line.length() == 0) {
            return getNextParameter();
        }
        line = getParameterValueAsString(line);
        return line;
    }

    //--------------------------- GetNextToken ------------------------------------
    //
    //  ignores any commenting and gets the next delimited string
    //-----------------------------------------------------------------------------
    String getNextToken() throws IOException {
        //strip the line of any commenting
        while (CurrentLine.equals("")) {
            CurrentLine = file.readLine();
            CurrentLine = removeCommentingFromLine(CurrentLine);
        }

        //find beginning of parameter description
        int begIdx = CurrentLine.length();
        int endIdx = CurrentLine.length();

        //define some delimiters
        final String delims = "[ ;=,]+";
        final Pattern pattern = Pattern.compile(delims);
        Matcher matcher = pattern.matcher(CurrentLine);

        //begIdx = CurrentLine.find_first_not_of(delims);

        //find the end of the parameter description
        if (matcher.find()) {
            begIdx = matcher.end();
            if (matcher.find()) {
                endIdx = matcher.start();
            } else {
                endIdx = CurrentLine.length();
            }
        }

        String s = CurrentLine.substring(begIdx, endIdx);
        if (endIdx != CurrentLine.length()) {
            //strip the token from the line
            CurrentLine = CurrentLine.substring(endIdx + 1, CurrentLine.length());
        } else {
            CurrentLine = "";
        }
        return s;
    }

    //helper methods. They convert the next parameter value found into the relevant type
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

    //removes any commenting from a line of text
    public static String removeCommentingFromLine(String line) {
        //search for any comment and remove
        int idx = line.indexOf("//");
        if (idx != -1) {
            //cut out the comment
            return line.substring(0, idx);
        }
        return line;
    }
}