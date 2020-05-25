import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.*;

//main class
public class Parser
{

    Logger log = Logger.getLogger(Parser.class.getName());

    ResourceBundle resourceBundle = null;

    private String mainPath = "";//где соранять

    //количество квартир на странице.
    private int countAppartments = -1;
    //количество квартир уже спарсили на всех предыдущих страницах.
    private int countedApartments = 0;


    public String mainParser(String pathToSave, String strUrl) throws NoSuchMethodException, MalformedURLException
    {
        mainPath = pathToSave;//с формы пришло где сохранять
        //String url = strUrl;//с формы пришла ссылка откуда скачивать
        resourceBundle = Utils.setFileBundles();//указали файл с bundles. там ключи для html


        //int curPutPage = 0;
        String numberCurrentPage = "";
        Boolean flagStop = false;
        Boolean isFerstParse = true;//парсили ли уже 1ую страницу
        //TODO удалить потом. сюда из формы.
        String url = "https://www.cian.ru/cat.php?currency=2&deal_type=rent&engine_version=2&is_by_homeowner=1&maxprice=25000&minprice=25000&offer_type=flat&region=1&room1=1&room2=1&type=4";
        int countPage = 0;

        try {

            while (!flagStop) {
                //какая по счету страница парсится
                countPage++;

                //этап получения ссылки. Либо первая, что с формы пришла. Либо уже последующая.
                url = SettingConnectUrl.selectUrl(numberCurrentPage, url, countPage);

                //этап подключения к сайту по ссылке
                HttpURLConnection connection = SettingConnectUrl.connection(url);

                //эатп берем весь текст со страницы
                StringBuffer result = getTextFromUrl(connection);

                //этап отключения от сайта
                SettingConnectUrl.disconnect(connection);

                //узнаем номер текущей страницы
                numberCurrentPage = Utils.getCurrentPage(result);
                if (numberCurrentPage.equalsIgnoreCase("1")
                        && !isFerstParse)//т.е.парсили уже 1 страницу
                {
                    flagStop = true;
                    break;//выходим из while, все странцы спарсили.
                }

                //этап формирования JSON из текста
                String resultGetJson = getJson(result);
                if (resultGetJson.equalsIgnoreCase("0")) {
                    log.info("Error in getJson()");
                    return "";
                }

                //Парсим JSON
                doParser(resultGetJson, countPage);

                //Сохраняем в файлики

                isFerstParse = false;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return "";
    }

    //считываем весь текст-код страниц
    public StringBuffer getTextFromUrl(HttpURLConnection urlConnection)
    {
        InputStream inputStream = null;
        StringBuffer buffer = null;
        try
        {
            inputStream = urlConnection.getInputStream();
            buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }

        } catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if(inputStream != null)
                    inputStream.close();
            }catch(Exception e1)
            {
                e1.printStackTrace();
            }
        }
        return buffer;
    }


    //из всего текста-кода страниц вытаскиваем JSON,который в поседствии будем парсить для получения конкретной инфы
    public String getJson(StringBuffer textFromUrl) throws JSONException, IOException {
        int indexFinish = 0;
        int indexStart = 0;
        String strForBrackes = null;
        String strResultJson = null;
        Utils utils = new Utils();

        String serp_data = resourceBundle.getString("serp");
        log.info("resourceBundle value of key 'serp_data': " + serp_data);
        if(serp_data==null)
        {
            log.info("ERROR not found parameter 'serp_data' at config file!");
            return "0";
        }

        //регулярное выражение, которое нам найдет "window.__serp_data__=" и индекс его нахождения.
        Pattern pattern = Pattern.compile(serp_data);
        Matcher matcher = pattern.matcher(textFromUrl);
        if(!matcher.find())
        {
            log.info("not found 'window.__serp_data__=' at TEXT html");
        }
        else
        {
            indexStart = matcher.end();
        }

        //вырезаем от начала 'window.__serp_data__='
        strForBrackes = textFromUrl.substring(indexStart);
        //log.info("String for getIndexClosingBracket(): " + strForBrackes);

        indexFinish = Utils.getIndexClosingBracket(strForBrackes);
        if(indexFinish!=0 && indexStart!=0)
        {
            strResultJson = strForBrackes.substring(0,indexFinish+1);
            //log.info("JSON: " + strResultJson);
        }
        else
        {
            log.info("indexFinish = 0 or indexStart = 0 OR CAPTCHA!!!!");
            strResultJson="0";
        }

        return strResultJson;
    }

    /*
      [] - массив.
      {} - объект.
    */
    public boolean doParser(String json, int countPage) throws MalformedURLException
    {
        Map<String, String> mapParsedTextAndNameFolder = new HashMap<String, String>();

        Map<String, String> mapForCostructDoc = new HashMap<String, String>();

        Constants constants = new Constants();

        int countRooms = 0;
        int floorNumber = 0;
        int floorsCount = 0;

        String fullNameMetro = "";
        String metroTransfer = "";
        int metroTime = 0;

        String address = "";
        //String fullNameAddress = null;

        String kitchenArea = "";
        String livingArea = "";
        String totalArea = "";

        String description = "";

        String nameForFolder = "";
        String fullInfoMetro1 = "";
        String fullInfoMetro2 = "";

        String textOf1Page = "";
        int counted = 0;

        log.info( "=============" + countPage + " страница============");

        try {
            JSONObject jsonMainObject = new JSONObject(json);
            JSONObject.getNames(jsonMainObject);

            JSONObject jsObResults = jsonMainObject.getJSONObject(constants.results);

            //найдем на странице число всего квартир. Потом по этому числу будем смотреть - стоит ли коннктиться к следующей странице.
            //или и на первой/предыдущей уже все досчитали.
            if(!jsObResults.isNull(constants.countAppartments))
            {

                countAppartments = jsObResults.getInt(constants.countAppartments);
                log.info("кол-во квартир:" + countAppartments);
              }
            else
            {
                countAppartments = 0;
                log.info("Not found 'aggregatedOffers' in html");
            }

            JSONArray jsOffers = jsObResults.optJSONArray(constants.offers);

            for(int i=0; i<jsOffers.length();i++)
            {
                counted = 0;
                //из массива оферсов.Каждый эл-т массива офферсов - {объект, массив} - для каждой квартиры одинаковые имена объектов.
                JSONObject jsObjectsOffers = jsOffers.getJSONObject(i);

                //из каждого элемента массива берем(по именам поиск):
                //эл-т офферс - int-значение.
                if(!jsObjectsOffers.isNull(constants.roomsCount))
                {
                    countRooms = jsObjectsOffers.getInt(constants.roomsCount);
                }
                if(!jsObjectsOffers.isNull(constants.floorNumber))
                {
                    floorNumber = jsObjectsOffers.getInt(constants.floorNumber);
                }

                //эл-т офферс - объект.
                if(!jsObjectsOffers.isNull(constants.building))
                {
                    JSONObject jsObjectBuilding = jsObjectsOffers.getJSONObject(constants.building);
                    if(!jsObjectBuilding.isNull(constants.floorsCount))
                    {
                        floorsCount = jsObjectBuilding.getInt(constants.floorsCount);
                    }
                }

                //эл-т офферс - массив.
                if(!jsObjectsOffers.isNull(constants.geo))
                {
                    JSONObject jsObjectGeo = jsObjectsOffers.getJSONObject(constants.geo);

                    if(!jsObjectGeo.isNull(constants.undergrounds))
                    {
                        fullInfoMetro2 = "";
                        JSONArray jsArrayUndergrounds = jsObjectGeo.getJSONArray(constants.undergrounds);
                        for(int j=0;j<jsArrayUndergrounds.length();j++)
                        {
                            JSONObject jsObjectsUnderground= jsArrayUndergrounds.getJSONObject(j);
                            if(!jsObjectsUnderground.isNull(constants.fullNameMetro))
                            {
                                fullNameMetro = jsObjectsUnderground.getString(constants.fullNameMetro);
                            }
                            else if(!jsObjectsUnderground.isNull(constants.shortName))
                            {
                                fullNameMetro = jsObjectsUnderground.getString(constants.shortName);
                            }

                            if(!jsObjectsUnderground.isNull(constants.metroTransfer))
                            {
                                if(jsObjectsUnderground.getString(constants.metroTransfer).equalsIgnoreCase(constants.metroWalk))
                                {
                                    metroTransfer = constants.walk;
                                }
                                else if(jsObjectsUnderground.getString(constants.metroTransfer).equalsIgnoreCase(constants.metroTransport))
                                {
                                    metroTransfer = constants.transport;
                                }
                                else
                                {
                                    metroTransfer = jsObjectsUnderground.getString(constants.metroTransfer);
                                }
                            }

                            if(!jsObjectsUnderground.isNull(constants.metroTime))
                            {
                                metroTime = jsObjectsUnderground.getInt(constants.metroTime);
                            }
                            fullInfoMetro1 = fullNameMetro + ", " + metroTransfer + ", " + metroTime + ".";
                            fullInfoMetro2 +=fullInfoMetro1;
                        }
                    }

                    //geoType - street
                    /*if(!jsObjectGeo.isNull(constants.street))
                    {

                    }

                    //geoType - house
                    if(!jsObjectGeo.isNull(constants.house))
                    {

                    }*/

                    if(!jsObjectGeo.isNull(constants.address))
                    {
                        String comma = ",";
                        address = "";
                        nameForFolder = "";
                        JSONArray jsArrayAddress = jsObjectGeo.getJSONArray(constants.address);
                        for(int j=0; j<jsArrayAddress.length();j++)
                        {
                            JSONObject jsObjectAddress = jsArrayAddress.getJSONObject(j);
                            if(!jsObjectAddress.isNull(constants.fullNameInAddress))
                            {
                                address+=jsObjectAddress.getString(constants.fullNameInAddress) + ",";
                                //для названия папки
                                if(j!=0 && j!=1)
                                {
                                    if(j==jsArrayAddress.length()-1)
                                    {
                                        comma="";
                                    }
                                    nameForFolder += jsObjectAddress.getString(constants.fullNameInAddress) + comma;
                                }
                            }
                        }
                    }
                }

                //Площадь и описание.
                if(!jsObjectsOffers.isNull(constants.kitchenArea))
                {
                    kitchenArea = jsObjectsOffers.getString(constants.kitchenArea);
                }
                if(!jsObjectsOffers.isNull(constants.livingArea))
                {
                    livingArea = jsObjectsOffers.getString(constants.livingArea);
                }
                if(!jsObjectsOffers.isNull(constants.totalArea))
                {
                    totalArea = jsObjectsOffers.getString(constants.totalArea);
                }

                if(!jsObjectsOffers.isNull(constants.description))
                {
                    description = jsObjectsOffers.getString(constants.description);
                }

                System.out.println(i+1 + ":" + " countRooms:" + countRooms  + " floorNumber:" + floorNumber +
                        " floorsCount:" + floorsCount + " fullNameMetro:" + fullNameMetro + " metroTransfer:" +
                        metroTransfer + " metroTime:" + metroTime + " address:" + address +
                " kitchenArea:" + kitchenArea + " livingArea:" + livingArea + " totalArea:" + totalArea +
                " description:" + description + " ulica and house:" + nameForFolder + ", " + "fullInfoMetro2: "
                + fullInfoMetro2);

                /*textOf1Page = " countRooms:" + countRooms  + " floorNumber:" + floorNumber +
                        " floorsCount:" + floorsCount + " fullNameMetro:" + fullNameMetro + " metroTransfer:" +
                        metroTransfer + " metroTime:" + metroTime + " address:" + address +
                        " kitchenArea:" + kitchenArea + " livingArea:" + livingArea + " totalArea:" + totalArea +
                        " description:" + description;*/

                String str_ountRooms = String.valueOf(countRooms);
                String str_floorNumber = String.valueOf(floorNumber);
                String str_floorsCount = String.valueOf(floorsCount);
                String str_metroTime = String.valueOf(metroTime);

                mapForCostructDoc.put("countRooms", str_ountRooms);
                mapForCostructDoc.put("floorNumber", str_floorNumber);
                mapForCostructDoc.put("floorsCount", str_floorsCount);
                mapForCostructDoc.put("fullNameMetro", fullNameMetro);
                mapForCostructDoc.put("metroTransfer", metroTransfer);
                mapForCostructDoc.put("metroTime", str_metroTime);
                mapForCostructDoc.put("address", address);
                mapForCostructDoc.put("kitchenArea", kitchenArea);
                mapForCostructDoc.put("livingArea", livingArea);
                mapForCostructDoc.put("totalArea", totalArea);
                mapForCostructDoc.put("description", description);


                textOf1Page = Utils.constructDoc(mapForCostructDoc);

                if(nameForFolder.equalsIgnoreCase(""))
                {
                    if(!address.equalsIgnoreCase(""))
                    {
                        nameForFolder=address;
                    }
                    else
                        nameForFolder=constants.noNameFile;
                }
                mapParsedTextAndNameFolder.put(textOf1Page, nameForFolder);
                //address - на случай, если
                //не будет названия улицы с домом. Такое бывает.
                counted = i + 1;
            }

            System.out.println(countedApartments);
            countedApartments +=counted;

            System.out.println("С предыдущих страниц считано: " + countedApartments + " квартир");

            if(!mapParsedTextAndNameFolder.isEmpty())
            {
                Utils.printFile(mapParsedTextAndNameFolder,
                        mainPath);//где создавать папаку.
            }
            else
            {
                log.info("Map for address and info - is empty. In doParser.");
                return false;
            }
        }
        catch (JSONException ex)
        {
             log.info("msg: " + ex.getMessage() +"cause: " + ex.getCause() + "StackTrace: " + ex.getStackTrace() );
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}

