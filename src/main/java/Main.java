import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Main {
    private static final TemperatureConverter converter =
            new TemperatureConverter();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Locale locale = Locale.getDefault();
        System.out.println("Select language 1. English 2. suomeksi 3. svenska 4. 日本語");
        int lang = sc.nextInt();
        switch (lang) {
            case 1:
                locale = new Locale("en", "US");
                break;
            case 2:
                locale = new Locale("fi", "FI");
                break;
            case 3:
                locale = new Locale("sv", "SE");
                break;
            case 4:
                locale = new Locale("ja", "JP");
                break;
            default:
                System.out.println("Invalid choice.");
                break;
        }

        ResourceBundle rs = ResourceBundle.getBundle("MessagesBundle", locale);
        System.out.println(rs.getString("lang_chosen"));
        System.out.println(rs.getString("question"));
        int convert = sc.nextInt();
        if (convert == 1) {
            System.out.println(rs.getString("celsius"));
            double celsius = sc.nextDouble();
            double fahrenheit = converter.celsiusToFahrenheit(celsius);
            System.out.println(celsius + " " + (rs.getString("ctof") + " " + fahrenheit));
        }  else if (convert == 2) {
            System.out.println(rs.getString("fahrenheit"));
            double fahrenheit = sc.nextDouble();
            double celsius = converter.fahrenheitToCelsius(fahrenheit);
            System.out.println(fahrenheit + " " + (rs.getString("ftoc") + " " + celsius));
        } else {
            System.out.println(rs.getString("error"));
        }
}}
