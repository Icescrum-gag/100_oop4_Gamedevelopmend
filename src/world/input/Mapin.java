package world.input;

import input.settings.Settings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import world.HitBox;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class Mapin {

    private URL xmlpath;
    private int width;
    private int height;
    private int layers;
    private int[][][] contents;
    private Tile[][][] imgs;
    private Tiles spritesheed;
    private Settings settings;
    private NodeList objList;
    private ArrayList<HitBox> objects;
    private Document doc;

    public Mapin(String path) {
        xmlpath = getClass().getClassLoader().getResource(path);
        settings = new Settings();
        String[] s = settings.getSpritesheedparameters();
        objects = new ArrayList<HitBox>();
//        System.out.println(settings.getSpritesheedpath());
        spritesheed = new Tiles(settings.getSpritesheedpath(), Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]));
        xmlinput();
        tielmap();
        collisionlayer();
    }

    private void xmlinput() {

        try {
            String split1 = "\n";
            String split2 = ",";
            String[] s1;
            String[] s2;

            DocumentBuilderFactory docBF = DocumentBuilderFactory.newInstance();
            DocumentBuilder docB = docBF.newDocumentBuilder();
            doc = docB.parse(String.valueOf(xmlpath));

            NodeList layerList = doc.getElementsByTagName("layer");
            NodeList dataList = doc.getElementsByTagName("data");
            objList = doc.getElementsByTagName("objectgroup");


            Node whNode = layerList.item(0);
            this.layers = layerList.getLength();
            this.width = Integer.parseInt(((Element) whNode).getAttribute("width"));
            this.height = Integer.parseInt(((Element) whNode).getAttribute("height"));


            contents = new int[layers][height][width];
            imgs = new Tile[layers][height][width];

            for (int i = 0; i < layers; i++) {
                Node data = dataList.item(i);
                Element eData = (Element) data;

                if (data.getNodeType() == Node.ELEMENT_NODE) {
                    s1 = eData.getTextContent().trim().split(split1);

                    for (int j = 0; j < height; j++) {
                        s2 = s1[j].split(split2);

                        for (int k = 0; k < width; k++) {
                            contents[i][j][k] = Integer.parseInt(s2[k]);
                        }
                    }
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    // map in tieles umwandeln
    private void tielmap() {

        // System.out.println(spritesheed.getX());

        for (int i = 0; i < layers; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < width; k++) {
                    if (contents[i][j][k] != 0) {
                        imgs[i][j][k] = new Tile((k * 64), (j * 64), spritesheed.getTieles((contents[i][j][k] - 1) / spritesheed.getX(), (contents[i][j][k] - 1) % spritesheed.getX()));
                    } else {
                        imgs[i][j][k] = new Tile((k * 64), (j * 64));
                    }
                }
            }
        }
    }

    public void drawMap(Graphics2D graphics) {

        for (int i = 0; i < layers; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < width; k++) {
                    if (contents[i][j][k] != 0) {
                        if (imgs[i][j][k] != null) {
                            Graphics2D graphics2 = (Graphics2D) graphics.create();
                            graphics2.drawImage(imgs[i][j][k].getImg(), (int) imgs[i][j][k].getX(), (int) imgs[i][j][k].getY(), null);
                        }
                    }
                }
            }
        }
    }

    private void collisionlayer() {

        //Element objectgroup = (Element) doc.getElementsByTagName("objectgroup");
        // System.out.println("zeile 133 "+objList.getLength());
        for (int i = 0; i < objList.getLength(); i++) {
            NodeList objects = ((Element) objList.item(i)).getElementsByTagName("object");
            // System.out.println("zeile 136 "+objects.getLength());

            for (int j = 0; j < objects.getLength(); j++) {
                Element object = (Element) objects.item(j);
                HitBox test = new HitBox(Double.parseDouble(object.getAttribute("x")), Double.parseDouble(object.getAttribute("y")), Double.parseDouble(object.getAttribute("height")), Double.parseDouble(object.getAttribute("width")));
                this.objects.add(new HitBox(Double.parseDouble(object.getAttribute("x")), Double.parseDouble(object.getAttribute("y")), Double.parseDouble(object.getAttribute("height")), Double.parseDouble(object.getAttribute("width"))));
                //       System.out.println("zeile 142 "+ test.toString());
            }
        }
        // System.out.println("zeile 145 "+objects.size());
    }


    // Getter
    public ArrayList<HitBox> getObjects() {
        return objects;
    }

}
