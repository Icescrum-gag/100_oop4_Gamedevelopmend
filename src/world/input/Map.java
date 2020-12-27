package world.input;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;


public class Map {

    private static final String source = "src\\assets\\map-xml\\";
    private int width;
    private int height;
    private int layers;
    private int[][][] contents;

    private Game game;
    private Spritesheet spritesheet;
    private Tile[][][] tiles;
    private GameScaler gameScaler;
    private ArrayList<Object> mapCollisions;

    public Map(Game game) {
        this.game = game;
        mapCollisions = new ArrayList<Hitbox>();
    }

    // ------------------------------------------------------------
    // Create Map
    // ------------------------------------------------------------

    public void loadMap(Maps maps) {

        String mapImg = maps.getSpriteFile();
        String mapXml = maps.getXml();
        String split1 = "\n";
        String split2 = ",";
        String[] s1;
        String[] s2;

        try {
            File xmlDoc = new File(source + mapXml + ".tmx");
            DocumentBuilderFactory docBF = DocumentBuilderFactory.newInstance();
            DocumentBuilder docB = docBF.newDocumentBuilder();
            Document doc = docB.parse(xmlDoc);

            NodeList layerList = doc.getElementsByTagName("layer");
            NodeList dataList = doc.getElementsByTagName("data");
            NodeList objList = doc.getElementsByTagName("objectgroup");

            System.out.println(objList.getLength());

            collision(objList);

            Node whNode = layerList.item(0);
            this.layers = layerList.getLength();
            this.width = Integer.parseInt(((Element) whNode).getAttribute("width"));
            this.height = Integer.parseInt(((Element) whNode).getAttribute("height"));

            contents = new int[layers][height][width];
            tiles = new Tile[layers][height][width];

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

            this.gameScaler = new GameScaler(new Size(width, height));
            createTileMap(mapImg);
            Element e = (Element) objList.item(0);
            System.out.println(e.getAttribute("id"));

        } catch (Exception e) {}
    }

    //--------------------------------------------------------------
    //neu machne
    //--------------------------------------------------------------
    public void createTileMap(String mapImg) {
        spritesheet = new Spritesheet(mapImg);
        BufferedImage[][] sheet = spritesheet.getParts();

        for (int i = 0; i < layers; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < width; k++) {
                    tiles[i][j][k] = new Tile(game, new Hitbox(CustomHitbox.OBJ_TILE));
                    tiles[i][j][k].setPos(new Position(k * Config.TILESIZE, j * Config.TILESIZE));
                    if (contents[i][j][k] != 0) {
                        tiles[i][j][k].setImg(sheet[(contents[i][j][k] - 1) % spritesheet.getImgHorizontalCount()][(contents[i][j][k] - 1) / spritesheet.getImgHorizontalCount()]);
                    } else {
                        tiles[i][j][k].setImg("NA");
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------
    // Collision Neu machen
    // ------------------------------------------------------------

    private void collision(NodeList colList) {
        for (int i = 0; i < colList.getLength(); i++) {

            Element parent = (Element) colList.item(i).getParentNode();
//			if(parent.getAttribute("name").equals("collision")) {
            Element col = (Element) colList.item(i);
            mapCollisions.add(
                    new Hitbox(
                            Double.parseDouble(col.getAttribute("width")),
                            Double.parseDouble(col.getAttribute("height")),
                            new Position(
                                    Double.parseDouble(col.getAttribute("x")),
                                    Double.parseDouble(col.getAttribute("y"))
                            )
                    )
            );
//			}
        }
        DisplayHitboxes();
    }

    // ------------------------------------------------------------
    // Draw Map
    // ------------------------------------------------------------

    public void drawMap(Graphics2D graphics) {

        for (int i = 0; i < layers; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < width; k++) {
                    if (contents[i][j][k] != 0) {
                        if (tiles[i][j][k] != null) {

                            Graphics2D graphics2 = (Graphics2D) graphics.create();
                            AffineTransform latch = graphics2.getTransform();
                            AffineTransform transform = new AffineTransform();

                            if (gameScaler.compareXY()) {
                                transform.translate(
                                        tiles[i][j][k].getPos().getX() / 2,
                                        tiles[i][j][k].getPos().getY() / 2 + (Config.CANVAS_HEIGHT / (2 * Config.SCALE) - (Config.TILESIZE * height) / 2) / 2
                                );
                            } else {
                                transform.translate(
                                        tiles[i][j][k].getPos().getX() / 2 + (Config.CANVAS_WIDTH / (2 * Config.SCALE) - (Config.TILESIZE * width) / 2) / 2,
                                        tiles[i][j][k].getPos().getY() / 2
                                );
                            }

                            graphics2.transform(transform);
                            graphics2.drawImage(
                                    tiles[i][j][k].getImg(),
                                    transform,
                                    null
                            );

                            graphics2.setTransform(latch);
                            graphics2.dispose();
                        }
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------
    // Getters - Setters
    // ------------------------------------------------------------

    public int[][][] getContents() {
        return contents;
    }

    public void setContent(int content, int layer, int posX, int posY) {
        this.contents[layer][posY][posX] = content;
    }

    public Tile[][][] getTiles() {
        return tiles;
    }

    public void setTile(Tile tile, int layer, int posX, int posY) {
        this.tiles[layers][posY][posX] = tile;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getLayers() {
        return layers;
    }

    public Spritesheet getSpritesheet() {
        return spritesheet;
    }

    public GameScaler getGameScaler() {
        return gameScaler;
    }

    public Position getCenter() {
        return new Position(((Config.CANVAS_WIDTH/2)/Config.SCALE) - Config.TILESIZE/2, ((Config.CANVAS_HEIGHT/2)/Config.SCALE) - Config.TILESIZE/2);
    }

    // ------------------------------------------------------------
    // Debug
    // ------------------------------------------------------------

    public void DisplayContents() {
        String ret = "";

        for (int[][] iv : contents) {
            for (int[] jv : iv) {
                for (int kv : jv) {
                    ret += kv;
                }
                ret += "\n";
            }
            ret += "\n";
        }
        System.out.println(ret);
    }

    public void DisplayHitboxes() {
        String ret = "";

        for (Hitbox iv : mapCollisions) {
            ret += iv.getPos().getX() + ", "
                    + iv.getPos().getX() + ", "
                    + iv.getWidth() + ", "
                    + iv.getHeight() + "\n";
        }
        System.out.println(ret);
    }


}