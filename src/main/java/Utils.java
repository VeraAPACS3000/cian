import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javax.swing.text.html.parser.Parser;
import java.io.*;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URLClassLoader;
import java.net.URL;

public class Utils
{
    private static Logger log = Logger.getLogger(Parser.class.getName());

    public static int getIndexClosingBracket(String strWhereToSearch)
    {
        //strWhereToSearch=null;
        //strWhereToSearch="{we{}{}{hk}hk{jk}g}";
        int index = 0;
        Stack arrayListBrackets = new Stack();
        boolean flag = false;

        char[] arrayListBrackets_char = strWhereToSearch.toCharArray();

        for(int i=0; i<=strWhereToSearch.length()-1; i++)
        {
            if(flag==false && arrayListBrackets_char[i]=='{')
            {
                arrayListBrackets.push(1);
                flag=true;
            }
            else if(arrayListBrackets_char[i]=='{')
            {
                arrayListBrackets.push(1);
            }
            if(arrayListBrackets_char[i]=='}')
            {
                if(!arrayListBrackets.isEmpty() && arrayListBrackets.size()!=1)
                {
                    arrayListBrackets.pop();
                }
                else if(arrayListBrackets.size()==1)
                {
                    index=i;
                    return index;
                }
            }
        }
        //log.info("index test: " + index);
        return index;
    }

    public String getResource(String parameter) throws IOException
    {
        String result = null;
        Properties property = new Properties();
        String nameConfigFile = "congig.properties";

        InputStream inputStream = Utils.class.getResourceAsStream(nameConfigFile);
        property.load(inputStream);
        result = property.getProperty(parameter);

        log.info("result property: " + result);
        return result;
    }

    public static String getCurrentPage(StringBuffer textFromUrl)
    {
        Constants constants = null;
        try {
            constants = new Constants();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String strCurrentPage = "";
        String cutPage = "<";

        Pattern pattern = Pattern.compile(constants.tagForCountPages);
        Matcher matcher = pattern.matcher(textFromUrl);
        if(matcher.find())
        {
            strCurrentPage = textFromUrl.substring(matcher.end(), matcher.end() + 5);//5 - для кол-ва цифр в странице.
        }

        Pattern pattern1 = Pattern.compile(cutPage);
        Matcher matcher1 = pattern1.matcher(strCurrentPage);
        if(matcher1.find())
        {
            strCurrentPage=strCurrentPage.substring(0, matcher1.end()-1);//-1 - для галочки-скобочки.
        }

        return strCurrentPage;
    }

    public static String putPageInUrl(String url, String newUrl, int numberPage)
    {
        newUrl = url + "&p=" + numberPage;
        return newUrl;
    }

    //public static void printFile(String nameFolder, String text, String path, File file)
    public static void printFile(Map<String, String> mapParsedTextAndNameFolder,
                                 String strPathToSave) throws IOException {

        String nameFolder = "";
        File myPath = null;

        for(Map.Entry<String, String> item : mapParsedTextAndNameFolder.entrySet())
        {
            System.out.println("key:" + item.getKey() + "value:" + item.getValue().toString());
            nameFolder =  item.getValue().toString();
            System.out.println("папка: " + nameFolder + "//n");

            //=======================================================================================
            //create folder.
            myPath = new File(strPathToSave + "/" + nameFolder + "/");
            if(myPath.exists())
            {
                nameFolder = nameFolder + "_повтор";
                myPath.renameTo(new File(strPathToSave + "/" + nameFolder + "/"));
            }

            //=======================================================================================
            //create blank.
            XWPFDocument document = new XWPFDocument();

            //create paragraph.
            XWPFParagraph paragraph = document.createParagraph();

            //You can enter the text or any object element, using Run.
            XWPFRun run = paragraph.createRun();

            //Write into a Paragraph.
            run.setText(item.getValue());
            run.setText(item.getKey());
            System.out.println("===========ЧТО_ЗАПИСЫВАЮ_В_ФАЙЛ==================");
            System.out.println("item.getKey():" + item.getKey());
            System.out.println("item.getValue():" + item.getValue());


            //create potok.
            FileOutputStream streamDoc = new FileOutputStream(myPath + nameFolder + ".docx");

            document.write(streamDoc);
            streamDoc.close();

            /*if(!myPath.mkdirs())
            {
                System.out.println("ЧТО СЛУЧИЛОСЬ С ПАПКОЙ: " + nameFolder +" key:" + item.getKey() + "value:" + item.getValue().toString() + "|");
            }
            else
            {
                System.out.println("ПАПКА СОЗДАЛАСЬ: " + nameFolder +" key:" + item.getKey() + "value:" + item.getValue().toString() + "|");
            }*/
        }
    }

    public static String constructDoc(Map<String, String> mapText)
    {
        String outText = "";

        //приставка из экрана.

        //инфа из сайта.
        for(Map.Entry<String, String> item : mapText.entrySet())
        {
            //key, value
            /*mapForCostructDoc.put("countRooms", str_ountRooms);
            mapForCostructDoc.put("floorNumber", str_floorNumber);
            mapForCostructDoc.put("floorsCount", str_floorsCount);
            mapForCostructDoc.put("fullNameMetro", fullNameMetro);
            mapForCostructDoc.put("metroTransfer", metroTransfer);
            mapForCostructDoc.put("metroTime", str_metroTime);
            mapForCostructDoc.put("address", address);
            mapForCostructDoc.put("kitchenArea", kitchenArea);
            mapForCostructDoc.put("livingArea", livingArea);
            mapForCostructDoc.put("totalArea", totalArea);
            mapForCostructDoc.put("description", description);*/
            outText = item.getValue();
        }

        return outText;
    }

    static ResourceBundle setFileBundles() throws MalformedURLException
    {
        File file = new File( System.getProperty("user.dir"));
        URL[] urls = {file.toURI().toURL()};
        ClassLoader loader = new URLClassLoader(urls);
        return ResourceBundle.getBundle("config", Locale.getDefault(), loader);
    }
}
