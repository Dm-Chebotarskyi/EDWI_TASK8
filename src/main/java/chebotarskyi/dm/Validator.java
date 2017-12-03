package chebotarskyi.dm;

public class Validator {

    private static final String urlRegExp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";


    public static boolean isLinkValid(String link)
    {
        return link.matches(urlRegExp);
    }


}
