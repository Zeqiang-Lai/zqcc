package error;

public class TextColor {
    public static String red_color = (char)27 + "[31m";
    public static String green_color = (char)27 + "[32m";
    public static String reset_color = (char)27  + "[0m";
    public static String bold_color = "\033[1m";

    public static boolean is_windows = System.getProperty("os.name").startsWith("Windows");
}
