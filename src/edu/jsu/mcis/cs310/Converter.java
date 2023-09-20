package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import com.opencsv.CSVWriter;
import java.util.Arrays;
import java.io.StringReader;
import java.io.StringWriter;

public class Converter {
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
     
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
@SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        String result = "{}"; // default return value; replace later!
        try {
            // INSERT YOUR CODE HERE
            CSVReader csvReader = new CSVReader(new StringReader (csvString));
            JsonObject obj = new JsonObject();
            JsonArray prod_num = new JsonArray();
            JsonArray col_heading = new JsonArray();
            JsonArray dataA = new JsonArray();
            String[] rows = csvReader.readNext();
            
            for (String head: rows){
                col_heading.add(head);
            }
            rows = csvReader.readNext();
            while (rows != null){
                prod_num.add(rows[0]);
                JsonArray nData = new JsonArray();
                for (int i= 1; i < rows.length; i++){
                    String words = rows[i];
                    if(col_heading.toArray()[i].equals("Episode")|| col_heading.toArray()[i].equals("Season")){
                    int num = Integer.parseInt(words);
                    nData.add(num);
                }
                    else{
                        nData.add(words);
                    }
                }
                dataA.add(nData);
                obj.put("ProdNums", prod_num);
                obj.put("ColHeadings", col_heading);
                obj.put("Data", dataA);
                rows = csvReader.readNext();
            }
            result = Jsoner.serialize(obj);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        String result = ""; // default return value; replace later!
        try {
            // INSERT YOUR CODE HERE
            JsonObject obj = Jsoner.deserialize(jsonString, new JsonObject());
            JsonArray prod_num = (JsonArray)obj.get("ProdNums");
            JsonArray col_heading = (JsonArray)obj.get("ColHeadings");
            JsonArray dataA = (JsonArray)obj.get("Data");
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\\', "\n");
            String[]headings = Arrays.copyOf(col_heading.toArray(), col_heading.toArray().length, String[].class);
            
            csvWriter.writeNext(headings);
            for(int i = 0; i < dataA.size(); i++){
                String[] nAray = new String[col_heading.size()];
                nAray[0] = (String)prod_num.getString(i);
                JsonArray nData = (JsonArray)dataA.getCollection(i);
                for (int x = 1; x <(nData.size()+1); x++){
                    String words;
                    words = ((JsonArray)dataA.get(i)).get(x-1).toString();
                    
                    if(headings[x].equals("Episode")){
                        nAray[x]= String.format("%02d", Integer.valueOf(words));
                    }
                    else{
                        System.out.println(headings[x]);
                        nAray[x]= words;
                    }
                }
                csvWriter.writeNext(nAray);
            }
            result = writer.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
