package Search;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class RoomMapCSVWriter {

    public  static void writeToCSV(String fileName, String[] record){
        File file = new File(fileName);
        FileWriter output = null;
        try {
            output = new FileWriter(file);
            CSVWriter write = new CSVWriter(output,CSVWriter.DEFAULT_SEPARATOR,CSVWriter.NO_QUOTE_CHARACTER,CSVWriter.DEFAULT_LINE_END);
            write.writeNext(record);
            write.flush();
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
