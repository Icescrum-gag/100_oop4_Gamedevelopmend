package input.settings;

import input.Fileinput;

import java.util.ArrayList;

public class Maplist extends Fileinput {

    private static String path = "pics/map";
    private String[] list;

    public Maplist() {
        super(path);
        process();
    }

    private void process() {
        int i = 0;

        for (String s : super.getList()) {
            if (s.contains("Maps:")) {
                list = new String[Integer.parseInt(s.substring(5))];
            } else {
                list[i] = s;
                i++;
            }
        }
    }

    // Getter

    public String[] getMapList() {
        return list;
    }
}
