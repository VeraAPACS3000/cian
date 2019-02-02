import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

public class SettingConnectUrl
{
    static Logger log = Logger.getLogger(SettingConnectUrl.class.getName());

    /*
     *   Взять текущую url или сгенерить след.стр.
     */
    public static String selectUrl(String currentPage, String url, int curPutPage)
    {
        String newUrl = "";
        if(!currentPage.equalsIgnoreCase(""))//т.е не первая страница стаскивается.
        {
            //подставляем "&p="
            newUrl=Utils.putPageInUrl(url, newUrl, curPutPage);
        }
        else
        {
            newUrl=url;
        }

        return newUrl;
    }

    /*
     *  Подключение к странице по выбранной url
     */

    public static HttpURLConnection connection(String myUrl)
    {
        HttpURLConnection urlConnection = null;
        StringBuffer result = new StringBuffer();
        try
        {
            log.info("url: " + myUrl);
            URL url = new URL(myUrl);
            //1.создаю объект соединения.
            urlConnection = (HttpURLConnection) url.openConnection();
            log.info("Response Message:" + urlConnection.getResponseMessage());

            //2.Параметры запроса. У меня один.
            //urlConnection.setRequestMethod("GET");
            //3.фактическое соединение с удаленным объектом.
            urlConnection.connect();
            log.info("connect is success");

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();

        } /*finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
                log.info("disconnect is success");
            }
        }*/

        return urlConnection;
    }

    /*
     *  Отключение от страницы
     */

    public static void disconnect(HttpURLConnection connection)
    {
        if (connection != null)
        {
            connection.disconnect();
            log.info("disconnect is success");
        }
    }
}
