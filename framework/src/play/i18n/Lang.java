package play.i18n;

import play.Logger;
import play.Play;
import play.mvc.Http.Request;
import play.mvc.Http.Response;

public class Lang {

    public static ThreadLocal<String> current = new ThreadLocal<String>();

    public static String get() {
        return current.get();
    }

    public static boolean set(String locale) {
        if (locale.equals("") || Play.langs.contains(locale)) {
            current.set(locale);
            return true;
        } else {
            Logger.warn("Locale %s is not defined in your application.conf", locale);
            return false;
        }
    }

    public static void change(String locale) {
        if (set(locale)) {
            Response.current().setCookie("PLAY_LANG", locale);
        }
    }

    /**
     * Guess the language for current request in the following order:
     * <ol>
     * <li>if a <b>PLAY_LANG</b> cookie is set, use this value</li>
     * <li>if <b>Accept-Language</b> header is set, use it only if the Play! application allows it.<br/>supported language may be defined in application configuration, eg : <em>play.langs=fr,en,de)</em></li>
     * <li>otherwise, server's locale language is assumed
     * </ol>
     * @param request
     */
    public static void resolvefrom(Request request) {
        // Check a cookie
        if (request.cookies.containsKey("PLAY_LANG")) {
            if (!set(request.cookies.get("PLAY_LANG").value)) {
                Response.current().setCookie("PLAY_LANG", "");
            }
            return;
        }
        // Try from accept-language
        if (request.headers.containsKey("accept-language")) {
            String al = request.headers.get("accept-language").value();
            for (String a : al.split(",")) {
                if (a.indexOf(";") > 0) {
                    a = a.substring(0, a.indexOf(";"));
                }
                if (a.indexOf("-") > 0) {
                    a = a.substring(0, a.indexOf("-"));
                }
                for (String locale : Play.langs) {
                    if (locale.equals(a)) {
                        set(locale);
                        return;
                    }

                }
            }
        }
        // Use default
        if (Play.langs.isEmpty()) {
            set("");
        } else {
            set(Play.langs.get(0));
        }
    }
}