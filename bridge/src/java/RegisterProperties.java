import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class RegisterProperties {

    private static String[] BROWSER_GLOBALS = {

        "RegisteringGlobalProperties","console" ,   "window", "navigator", "document", "userAgent" ,"OpenAjax"
    };

    public static void main(String args[]) {
        String libName = args[0];
        String libUri = args[1];
        String libVersion = args[2];

        String src = args[3];

        String dependenciesFile = null;
        if(args.length == 5)
            dependenciesFile = args[4];
        if(dependenciesFile != null && dependenciesFile.trim().length()==0)
                dependenciesFile = null;

        // Creates and enters a Context. The Context stores information
        // about the execution environment of a script.
        Context cx = Context.enter();
        try {
            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed. Returns
            // a scope object that we use in later calls.
            Scriptable scope = cx.initStandardObjects();

            // Collect the arguments into a single string.
            String s = "";
            for (int i = 0; i < BROWSER_GLOBALS.length; i++) {
                s += "var " + BROWSER_GLOBALS[i] + " = {};";
            }
            s += "console.log = function(s){};";
            s += "navigator.appVersion='rhino';";
            s += "document.getElementsByTagName = function(){return [];};";
            s += "document.documentElement = {};";
            s += "document.cookie = '';";
            //s += "document.cookie.split = {};";
            s += "Prototype = []; Prototype.Version='';";
            cx.evaluateString(scope, s, "<cmd>", 1, null);
            List dependencies = new ArrayList();
            if(dependenciesFile != null){
                dependencies = getProperties(dependenciesFile, cx, scope);
            }
            List properties = getProperties(src, cx, scope);
            Iterator iter = dependencies.iterator();
            while(iter.hasNext()){
                Object o = iter.next();
                if(properties.contains(o))
                    properties.remove(o);
            }

            StringBuffer sb = new StringBuffer();
            sb.append("if (typeof OpenAjax!='undefined' && typeof OpenAjax.registerLibrary!='undefined' && typeof OpenAjax.registerGlobals!='undefined'){");
            sb.append("OpenAjax.registerLibrary('").append(libName).append("','")
                    .append(libUri).append("','").append(libVersion).append("');\n");
            sb.append("OpenAjax.registerGlobals('").append(libName).append("', [");
            boolean appended = false;
            iter = properties.iterator();
            while(iter.hasNext()){
                String name = iter.next().toString();
                if (!isBrowserGlobal(name)) {
                    if (appended) sb.append(",");
                    sb.append("'").append(name).append("'");
                    appended = true;
                }
            }
            sb.append("]);}\n");
            System.err.println(sb.toString());

        } finally {
            // Exit from the context.
            Context.exit();
        }
    }

    private static List getProperties(String fileName, Context context, Scriptable scope) {

        String s = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            while (in.ready()) {
                s += in.readLine();
                s += "\n";
            }
            in.close();
        } catch (Exception e) {
           // e.printStackTrace();
        }

        // Now evaluate the string we've colected.
       context.evaluateString(scope, s, "<cmd>", 1, null);

        Object[] o = scope.getIds();
        List result = new ArrayList();
        for(int i = 0;i<o.length;i++){
            result.add(o[i].toString());
        }
        return result;
    }

    private static boolean isBrowserGlobal(String s) {
        for (int i = 0; i < BROWSER_GLOBALS.length; i++) {
            if (BROWSER_GLOBALS[i].equals(s)) return true;
        }
        return false;
    }
}
