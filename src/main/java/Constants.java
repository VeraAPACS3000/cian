import java.net.MalformedURLException;
import java.util.ResourceBundle;

public class Constants
{
    ResourceBundle resourceBundle = Utils.setFileBundles();

    public String results = resourceBundle.getString("results");
    public String offers = resourceBundle.getString("offers");
    public String roomsCount = resourceBundle.getString("roomsCount");
    public String floorNumber = resourceBundle.getString("floorNumber");
    public String building = resourceBundle.getString("building");
    public String floorsCount = resourceBundle.getString("floorsCount");
    public String geo = resourceBundle.getString("geo");
    public String undergrounds = resourceBundle.getString("undergrounds");
    public String underground = resourceBundle.getString("underground");
    public String fullNameMetro = resourceBundle.getString("fullName");
    public String shortName = resourceBundle.getString("shortName");
    public String metroTransfer = resourceBundle.getString("transportType");
    public String metroWalk = resourceBundle.getString("walk");
    public String metroTransport = resourceBundle.getString("transport");
    public String metroTime = resourceBundle.getString("time");

    //Вместо address появился UserInput.
    public String address = resourceBundle.getString("address");
    public String geoType = resourceBundle.getString("geoType");
    public String fullNameInAddress = resourceBundle.getString("fullName");
    //public String userInput = resourceBundle.getString("userInput");

    public String kitchenArea = resourceBundle.getString("kitchenArea");
    public String livingArea = resourceBundle.getString("livingArea");
    public String totalArea = resourceBundle.getString("totalArea");
    public String description = resourceBundle.getString("description");
    public String street = resourceBundle.getString("street");
    public String house = resourceBundle.getString("house");

    public String walk = "пешком";
    public String transport = "на машине";

    public String tagForCountPages = "list-item--active--2-sVo\"><span>";
    public String countAppartments = resourceBundle.getString("aggregatedOffers");

    public String location = "location";
    public String noNameFile = "нет_названия_улицы";
    //==========================Для документа===========================
    /*public static String  count_rooms = "Количество комнат:";

    public static String number_floor = "Этаж:";

    public static String from_number_floor = "из";

    public String metro = "Метро:";

    public String metro_transfer = "мин.";

    public String metro_foot = "пешком";

    public String metro_auto = "на автомобиле";

    public String address_doc = "Адрес:";

    public String area = "Площадь:";

    public String totalArea_doc = "Общая - ";

    public String kitchenArea_doc = "Кухни - ";

    public String livingArea_doc = "Комнат - ";

    public String kv_m = "кв.м";*/


    public Constants() throws MalformedURLException {
    }
}
