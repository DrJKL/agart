package src;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import strains.DefaultStrain;

public class Environment {

    BufferedImage image;

    int width;
    int height;
    String name;
    int updates;

    List<String> strainNames = new LinkedList<String>();
    HashMap<Strain, LinkedList<Organism>> strains = new HashMap<Strain, LinkedList<Organism>>();
    HashMap<Strain, LinkedList<Organism>> activeStrains = new HashMap<Strain, LinkedList<Organism>>();
    HashMap<Strain, LinkedList<Organism>> tombedStrains = new HashMap<Strain, LinkedList<Organism>>();

    int youngestIn = 0;
    int lastStrain = 0;
    String[] strainNameMods = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    private static final String defName = "V-Land";
    // ArrayList<Organism> orgs = new ArrayList<Organism>();
    // ArrayList<Organism> tomb = new ArrayList<Organism>();
    ArrayList<Organism> graveyard = new ArrayList<Organism>();
    ArrayList<Organism> kids = new ArrayList<Organism>();

    private int randomInt(int low, int high) {
        return low + (int) (Math.random() * (high - low + 1));
    }

    private int checkCol(int c) {
        if (c < 0) {
            c = 0;
        }
        if (c >= this.width) {
            c = this.width - 1;
        }

        return c;
    }

    private int checkRow(int r) {
        if (r < 0) {
            r = 0;
        }
        if (r >= this.height) {
            r = this.height - 1;
        }

        return r;
    }

    public Environment(BufferedImage bimage) {
        image = bimage;
        width = image.getWidth();
        height = image.getHeight();
        name = defName;
        updates = 0;
    }

    public Environment(int w, int h, boolean rand) {
        image = SetEnvironment(w, h, rand);
        width = w;
        height = h;
        name = defName;
        updates = 0;
    }

    public BufferedImage SetEnvironment(int w, int h, boolean rand) {
        // Create buffered image that does not support transparency
        final BufferedImage bimage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        if (rand) {
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    final int R = (int) (Math.random() * 256);
                    final int G = (int) (Math.random() * 256);
                    final int B = (int) (Math.random() * 256);
                    final Color randomColor = new Color(R, G, B);
                    bimage.setRGB(i, j, randomColor.getRGB());
                }
            }
        } else {
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    final int R = 255;
                    final int G = 255;
                    final int B = 255;
                    final Color color = new Color(R, G, B);
                    bimage.setRGB(i, j, color.getRGB());
                }
            }
        }
        return bimage;
    }

    public void add(Strain str) {
        this.add(1, str);
    }

    public void add(int number, Strain str) {
        // if (activeStrains.keySet().size() + number > 26)
        // throw new RuntimeException("adding too many critters");
        int placeX, placeY;
        for (int i = 0; i < number; i++) {
            do {
                placeX = randomInt(0, this.width);
                placeY = randomInt(0, this.height);
            } while (this.orgAt(placeY, placeX));
            this.addOneAt(str, placeY, placeX);
        }
    }

    public Organism addOneAt(Strain str, int r, int c) {
        // if (strains.keySet().size() + 1 > 26)
        // throw new RuntimeException("too many critters");
        r = checkRow(r);
        c = checkCol(c);
        final Organism next = new Organism(this, new DefaultStrain(), r, c);
        String strName = str.getStrainName();
        final int i = 0;
        String newName = strName;
        while (strainNames.contains(newName)) {
            if (i == 26) {
                return null;
            }
            newName = strName += strainNameMods[i];
        }
        final Strain strainSet = str;
        next.setStrain(strainSet);
        lastStrain++;
        // orgs.add(next);
        str.youngest(0);
        addToStrains(next);
        addToActiveStrains(next);
        return next;
    }

    public Organism addOneAt(Strain str, int xMin, int yMin, int xMax, int yMax) {
        // if (orgs.size() + 1 > this.size())
        // throw new RuntimeException("too many critters");
        if (xMin < 0) {
            xMin = 0;
        }
        if (yMin < 0) {
            yMin = 0;
        }
        if (xMax >= width) {
            xMax = width - 1;
        }
        if (yMax >= height) {
            yMax = height - 1;
        }
        final int orgX = randomInt(xMin, xMax);
        final int orgY = randomInt(yMin, yMax);
        final Organism next = addOneAt(str, orgY, orgX);
        return next;
    }

    private void addToStrains(Organism org) {
        final Strain s = org.strain;
        LinkedList<Organism> toAdd = strains.get(s);
        if (toAdd == null) {
            toAdd = new LinkedList<Organism>();
        }
        toAdd.add(org);
        strains.put(s, toAdd);
        strainNames.add(s.getStrainName());
    }

    private void addToActiveStrains(Organism org) {
        final Strain s = org.strain;
        LinkedList<Organism> toAdd = activeStrains.get(s);
        if (toAdd == null) {
            toAdd = new LinkedList<Organism>();
        }
        toAdd.add(org);
        activeStrains.put(s, toAdd);
    }

    private void removeFromActiveStrains(Organism org) {
        final Strain sChar = org.strain;
        LinkedList<Organism> toAdd = activeStrains.get(sChar);
        if (toAdd == null) {
            toAdd = new LinkedList<Organism>();
        }
        toAdd.remove(org);
        activeStrains.put(sChar, toAdd);
    }

    private void addToTombedStrains(Organism org) {
        final Strain sChar = org.strain;
        LinkedList<Organism> toAdd = tombedStrains.get(sChar);
        if (toAdd == null) {
            toAdd = new LinkedList<Organism>();
        }
        toAdd.add(org);
        tombedStrains.put(sChar, toAdd);
    }

    public int getStrainSize(Strain s) {
        final LinkedList<Organism> thisStrain = strains.get(s);
        if (thisStrain == null) {
            return 0;
        } else {
            return thisStrain.size();
        }
    }

    public int getActiveStrainSize(Strain strain) {
        final LinkedList<Organism> thisStrain = activeStrains.get(strain);
        if (thisStrain == null) {
            return 0;
        } else {
            return thisStrain.size();
        }
    }

    public int getTombStrainSize(Strain strain) {
        final LinkedList<Organism> thisStrain = tombedStrains.get(strain);
        if (thisStrain == null) {
            return 0;
        } else {
            return thisStrain.size();
        }
    }

    public void update() {
        for (final Strain s : activeStrains.keySet()) {
            for (final Organism o : activeStrains.get(s)) {
                o.update();
            }
        }
        bringOutDead();
        addKids();
        updates++;
    }

    public void update(int times) {
        for (int i = 0; i < times; i++) {
            update();
        }
    }

    private void addKids() {
        // orgs.addAll(kids);
        if (kids.size() > 0) {
            for (final Organism o : kids) {
                this.addToStrains(o);
                this.addToActiveStrains(o);
            }
        }
        kids.clear();
    }

    private void bringOutDead() {
        // orgs.removeAll(graveyard);
        // tomb.addAll(graveyard);
        if (graveyard.size() > 0) {
            for (final Organism org : graveyard) {
                this.removeFromActiveStrains(org);
                this.addToTombedStrains(org);
            }
        }
        graveyard.clear();
    }

    public void exterminate(Strain str) {
        if (activeStrains.containsKey(str)) {
            // orgs.removeAll(activeStrains.get(str));
            // tomb.addAll(activeStrains.get(str));
            tombedStrains.put(str, activeStrains.get(str));
            activeStrains.remove(str);
        }
    }

    public boolean orgAt(int r, int c) {
        for (final List<Organism> orgs : activeStrains.values()) {
            for (final Organism o : orgs) {
                if (o.getRow() == r && o.getCol() == c) {
                    return true;
                }
            }
        }
        return false;
    }

    // public void youngestGen(int o) {
    // if (o > youngestIn) {
    // youngestIn = o;
    // }
    // }
    //
    // public int youngestIn(Strain strain) {
    // int youngest = 0;
    // for (Organism o : strains.get(strain)) {
    // int check = o.getGeneration();
    // if (check > youngest)
    // youngest = check;
    // }
    // return youngest;
    // }

    public String listLiving() {
        for (final List<Organism> orgs : activeStrains.values()) {
            Collections.sort(orgs);
        }
        int i = 1;
        int columns = 0;
        final int spacePer = this.getLongestLivingOrgName().length() + 15;
        final StringBuilder builder = new StringBuilder();
        builder.append("\n" + livingOrgs() + " Living:" + "\n");
        for (final Strain strain : activeStrains.keySet()) {
            final List<Organism> orgs = activeStrains.get(strain);
            for (final Organism o : orgs) {
                String str = o.orgName;
                for (int j = 0; j < this.getLongestLivingOrgName().length() - o.orgName.length()
                        + 1; j++) {
                    str += " ";
                }
                str += o.distanceFromCenter();
                for (int j = 0; j < 3 - Integer.toString(o.distanceFromCenter()).length(); j++) {
                    str += " ";
                }
                str += "from OP";
                builder.append(str);
                columns += str.length();
                while (columns % spacePer != 0) {
                    builder.append(" ");
                    columns++;
                }
                if (i % 1 == 0) {
                    builder.append("\n");
                    columns = 0;
                }
                i++;
            }
        }
        return builder.toString();
    }

    public String listTombed() {
        for (final List<Organism> tomb : tombedStrains.values()) {

            Collections.sort(tomb);
        }
        int i = 1;
        int columns = 0;
        final int spacePer = this.getLongestTombedOrgName().length() + 16;
        final StringBuilder builder = new StringBuilder();
        builder.append("\n" + tombedOrgs() + " Dead:" + "\n");
        for (final Strain strain : tombedStrains.keySet()) {
            final List<Organism> tomb = tombedStrains.get(strain);
            for (final Organism o : tomb) {
                final String str = o.orgName + " of " + o.causeOfDeath;
                builder.append(str);
                columns += str.length();
                while (columns % spacePer != 0) {
                    builder.append(" ");
                    columns++;
                }
                if (i % 3 == 0) {
                    builder.append("\n");
                    columns = 0;
                }
                i++;
            }
        }
        return builder.toString();
    }

    public String getLongestLivingOrgName() {
        String res = "";
        for (final Strain strain : activeStrains.keySet()) {
            final List<Organism> orgs = activeStrains.get(strain);
            for (final Organism o : orgs) {
                if (o.orgName.length() > res.length()) {
                    res = o.orgName;
                }
            }
        }
        return res;
    }

    public String getLongestTombedOrgName() {
        String res = "";
        for (final Strain strain : tombedStrains.keySet()) {
            final List<Organism> tomb = tombedStrains.get(strain);
            for (final Organism o : tomb) {
                if (o.orgName.length() > res.length()) {
                    res = o.orgName;
                }
            }
        }
        return res;
    }

    public String listStrains() {
        final StringBuilder res = new StringBuilder();
        for (final Strain c : strains.keySet()) {
            final LinkedList<Organism> orgsIn = strains.get(c);
            Collections.sort(orgsIn);
            res.append(orgsIn.size() + " in Strain " + c.getStrainName() + ":" + "\n");
            for (final Organism o : orgsIn) {
                res.append(o.orgName + "\n");
            }
            res.append("-------------------" + "\n");
        }
        return res.toString();
    }

    public String listStrainDataText() {
        final StringBuilder res = new StringBuilder();
        for (final Strain s : this.strains.keySet()) {
            res.append("Strain " + s.getStrainName() + ": \t" + this.getActiveStrainSize(s) + "\n");
        }
        return res.toString();
    }

    public String listActiveStrainDataText() {
        final StringBuilder res = new StringBuilder();
        for (final Strain s : this.activeStrains.keySet()) {
            res.append("Strain " + s.getStrainName() + ": \t" + this.getActiveStrainSize(s) + "\n");
        }
        return res.toString();
    }

    public String listTombStrainDataText() {
        final StringBuilder res = new StringBuilder();
        for (final Strain s : this.tombedStrains.keySet()) {
            res.append("Strain " + s.getStrainName() + ": \t" + this.getTombStrainSize(s) + "\n");
        }
        return res.toString();
    }

    public String listStrainData() {
        final StringBuilder res = new StringBuilder();
        res.append("<html>");
        for (final Strain s : this.strains.keySet()) {
            res.append("Strain " + s.getStrainName() + ": \t" + this.getStrainSize(s) + "<BR>");
        }
        res.append("</html>");
        return res.toString();
    }

    public String listActiveStrainData() {
        final StringBuilder res = new StringBuilder();
        res.append("<html>");
        for (final Strain s : this.activeStrains.keySet()) {
            res.append("Strain " + s.getStrainName() + ": \t" + this.getActiveStrainSize(s)
                    + "<BR>");
        }
        res.append("</html>");
        return res.toString();
    }

    public String listTombStrainData() {
        final StringBuilder res = new StringBuilder();
        res.append("<html>");
        for (final Strain s : this.tombedStrains.keySet()) {
            res.append("Strain " + s.getStrainName() + ": \t" + this.getTombStrainSize(s) + "<BR>");
        }
        res.append("</html>");
        return res.toString();
    }

    public int livingOrgs() {
        int i = 0;
        for (final List<Organism> orgs : activeStrains.values()) {
            i += orgs.size();
        }
        return i;
    }

    public int tombedOrgs() {
        int i = 0;
        for (final List<Organism> orgs : tombedStrains.values()) {
            i += orgs.size();
        }
        return i;
    }

    public int totalOrgs() {
        int i = 0;
        for (final List<Organism> orgs : strains.values()) {
            i += orgs.size();
        }
        return i;
    }

    private static String getDateTime() {
        final DateFormat dateFormat = new SimpleDateFormat("MM-dd-HHmmss");
        final Date date = new Date();
        return dateFormat.format(date);
    }

    public void saveImage() {
        final String date = getDateTime();
        final File output = new File("./outputImages/" + date + ".png");
        if (!output.exists()) {
            try {
                output.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        try {
            ImageIO.write(image, "jpeg", output);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void saveNegative() {
        final String date = getDateTime();
        final RescaleOp op = new RescaleOp(-1.0f, 255f, null);
        final BufferedImage negative = op.filter(image, null);
        final File outputNeg = new File("./outputImages/" + date + "Neg.png");
        try {
            outputNeg.createNewFile();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        try {
            ImageIO.write(negative, "jpeg", outputNeg);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public int getRed(int r, int c) {
        final Color color = new Color(image.getRGB(r, c));
        return color.getRed();
    }

    public int getGreen(int r, int c) {
        final Color color = new Color(image.getRGB(r, c));
        return color.getGreen();
    }

    public int getBlue(int r, int c) {
        final Color color = new Color(image.getRGB(r, c));
        return color.getBlue();
    }

    public void setRed(int r, int c, int newRed) {
        final Color color = new Color(image.getRGB(r, c));
        final Color newColor = new Color(newRed, color.getGreen(), color.getBlue());
        image.setRGB(r, c, newColor.getRGB());
    }

    public void setGreen(int r, int c, int newGreen) {
        final Color color = new Color(image.getRGB(r, c));
        final Color newColor = new Color(color.getRed(), newGreen, color.getBlue());
        image.setRGB(r, c, newColor.getRGB());
    }

    public void setBlue(int r, int c, int newBlue) {
        final Color color = new Color(image.getRGB(r, c));
        final Color newColor = new Color(color.getRed(), color.getGreen(), newBlue);
        image.setRGB(r, c, newColor.getRGB());
    }

    public int size() {
        return width * height;
    }

    /**
     * @param child1
     */
    public void addKid(Organism org) {
        kids.add(org);
    }

}