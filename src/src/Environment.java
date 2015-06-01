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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import strains.DefaultStrain;
import core.ImageUtil;

public class Environment {

    BufferedImage image;

    final int width;
    final int height;
    final String name;
    int updates;

    final List<String> strainNames = new LinkedList<>();
    final HashMap<Strain, LinkedList<Organism>> strains = new HashMap<>();
    final HashMap<Strain, LinkedList<Organism>> activeStrains = new HashMap<>();
    final HashMap<Strain, LinkedList<Organism>> tombedStrains = new HashMap<>();

    int youngestIn = 0;
    int lastStrain = 0;
    String[] strainNameMods = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    private static final String defName = "V-Land";
    ArrayList<Organism> graveyard = new ArrayList<>();
    ArrayList<Organism> kids = new ArrayList<>();

    private static int randomInt(int low, int high) {
        return low + (int) (Math.random() * (high - low + 1));
    }

    static int checkBounds(int c, int max) {
        return Math.min(Math.max(c, 0), max);
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
        IntStream.range(0, w).forEach(x -> {
            IntStream.range(0, h).forEach(y -> {
                final int R = rand ? (int) (Math.random() * 256) : 255;
                final int G = rand ? (int) (Math.random() * 256) : 255;
                final int B = rand ? (int) (Math.random() * 256) : 255;
                final Color color = new Color(R, G, B);
                bimage.setRGB(x, y, color.getRGB());
            });
        });
        return bimage;
    }

    public void add(Strain str) {
        this.add(1, str);
    }

    public void add(int number, Strain str) {
        IntStream.range(0, number).forEach(i -> {
            int placeX, placeY;
            do {
                placeX = randomInt(0, this.width);
                placeY = randomInt(0, this.height);
            } while (this.orgAt(placeY, placeX));
            this.addOneAt(str, placeY, placeX);
        });
    }

    public Organism addOneAt(Strain str, int r, int c) {
        final Organism next = new Organism(this, new DefaultStrain(), checkBounds(r, height - 1),
                checkBounds(c, width - 1));
        String strName = str.getStrainName();
        final int i = 0;
        String newName = strName;
        while (strainNames.contains(newName)) {
            newName = strName += strainNameMods[i];
        }
        final Strain strainSet = str;
        next.setStrain(strainSet);
        lastStrain++;
        str.youngest(0);
        addToStrains(next);
        addToActiveStrains(next);
        return next;
    }

    public Organism addOneAt(Strain str, int xMin, int yMin, int xMax, int yMax) {
        final int checkedXMin = Math.max(xMin, 0);
        final int checkedYMin = Math.max(yMin, 0);
        final int checkedXMax = Math.max(xMax, 0);
        final int checkedYMax = Math.max(yMax, 0);
        final int orgX = randomInt(checkedXMin, checkedXMax);
        final int orgY = randomInt(checkedYMin, checkedYMax);
        final Organism next = addOneAt(str, orgY, orgX);
        return next;
    }

    private void addToStrains(Organism org) {
        final Strain s = org.strain;
        LinkedList<Organism> toAdd = strains.get(s);
        if (toAdd == null) {
            toAdd = new LinkedList<>();
        }
        toAdd.add(org);
        strains.put(s, toAdd);
        strainNames.add(s.getStrainName());
    }

    private void addToActiveStrains(Organism org) {
        final Strain s = org.strain;
        LinkedList<Organism> toAdd = activeStrains.get(s);
        if (toAdd == null) {
            toAdd = new LinkedList<>();
        }
        toAdd.add(org);
        activeStrains.put(s, toAdd);
    }

    private void removeFromActiveStrains(Organism org) {
        final Strain sChar = org.strain;
        LinkedList<Organism> toAdd = activeStrains.get(sChar);
        if (toAdd == null) {
            toAdd = new LinkedList<>();
        }
        toAdd.remove(org);
        activeStrains.put(sChar, toAdd);
    }

    private void addToTombedStrains(Organism org) {
        final Strain sChar = org.strain;
        LinkedList<Organism> toAdd = tombedStrains.get(sChar);
        if (toAdd == null) {
            toAdd = new LinkedList<>();
        }
        toAdd.add(org);
        tombedStrains.put(sChar, toAdd);
    }

    public int getStrainSize(Strain s) {
        final LinkedList<Organism> thisStrain = strains.get(s);
        if (thisStrain == null) {
            return 0;
        }
        return thisStrain.size();
    }

    public int getActiveStrainSize(Strain strain) {
        final LinkedList<Organism> thisStrain = activeStrains.get(strain);
        if (thisStrain == null) {
            return 0;
        }
        return thisStrain.size();
    }

    public int getTombStrainSize(Strain strain) {
        final LinkedList<Organism> thisStrain = tombedStrains.get(strain);
        if (thisStrain == null) {
            return 0;
        }
        return thisStrain.size();
    }

    public void update() {
        activeStrains.values().stream().flatMap(List::stream).forEach(o -> {
            o.update();
        });
        bringOutDead();
        addKids();
        updates++;
    }

    public void update(int times) {
        IntStream.range(0, times).forEach(i -> {
            update();
        });
    }

    private void addKids() {
        kids.forEach(o -> {
            this.addToStrains(o);
            this.addToActiveStrains(o);
        });
        kids.clear();
    }

    private void bringOutDead() {
        graveyard.forEach(org -> {
            this.removeFromActiveStrains(org);
            this.addToTombedStrains(org);
        });
        graveyard.clear();
    }

    public void exterminate(Strain str) {
        if (activeStrains.containsKey(str)) {
            tombedStrains.put(str, activeStrains.get(str));
            activeStrains.remove(str);
        }
    }

    public boolean orgAt(int r, int c) {
        return activeStrains.values().stream().flatMap(List::stream).anyMatch(o -> {
            return o.getRow() == r && o.getCol() == c;
        });
    }

    public String listLiving() {
        activeStrains.values().stream().forEach(Collections::sort);
        int i = 1;
        int columns = 0;
        final int spacePer = this.getLongestLivingOrgName().length() + 15;
        final StringBuilder builder = new StringBuilder();
        builder.append("\n" + livingOrgs() + " Living:\n");
        for (final LinkedList<Organism> strain : activeStrains.values()) {
            for (final Organism o : strain) {
                String str = o.orgName;
                final int spaces = this.getLongestLivingOrgName().length() - o.orgName.length() + 1;
                for (int j = 0; j < spaces; j++) {
                    str += " ";
                }
                str += o.distanceFromCenter();
                final int moreSpaces = 3 - Integer.toString(o.distanceFromCenter()).length();
                for (int j = 0; j < moreSpaces; j++) {
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
        builder.append("\n" + tombedOrgs() + " Dead:\n");
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
        for (final List<Organism> orgs : activeStrains.values()) {
            for (final Organism o : orgs) {
                if (o.orgName.length() > res.length()) {
                    res = o.orgName;
                }
            }
        }
        return res;
    }

    public String getLongestLivingOrgName_new() {
        return activeStrains.values().stream().flatMap(LinkedList::stream)
                .map(Organism::getOrganismName).max(Comparator.comparing(String::length)).get();
    }

    public String getLongestTombedOrgName() {
        String res = "";
        for (final Strain strain : tombedStrains.keySet()) {
            for (final Organism o : tombedStrains.get(strain)) {
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
        return activeStrains.values().stream().mapToInt(List::size).sum();
    }

    public int tombedOrgs() {
        return tombedStrains.values().stream().mapToInt(List::size).sum();
    }

    public int totalOrgs() {
        return strains.values().stream().mapToInt(List::size).sum();
    }

    private static String getDateTime() {
        final DateFormat dateFormat = new SimpleDateFormat("MM-dd-HHmmss");
        final Date date = new Date();
        return dateFormat.format(date);
    }

    public void saveImage() {
        final String date = getDateTime();
        final File output = new File("./outputImages/" + date + ".png");
        saveImage(output, image);
    }

    public void saveNegative() {
        final String date = getDateTime();
        final RescaleOp op = new RescaleOp(-1.0f, 255f, null);
        final BufferedImage negative = op.filter(image, null);
        final File outputNeg = new File("./outputImages/" + date + "Neg.png");
        saveImage(outputNeg, negative);
    }

    private static void saveImage(File file, BufferedImage image) {
        try {
            file.createNewFile();
            ImageIO.write(image, "png", file);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getRed(int r, int c) {
        return ImageUtil.getRed(r, c, image);
    }

    public int getGreen(int r, int c) {
        return ImageUtil.getGreen(r, c, image);
    }

    public int getBlue(int r, int c) {
        return ImageUtil.getBlue(r, c, image);
    }

    public void setRed(int r, int c, int newRed) {
        ImageUtil.setRed(r, c, newRed, image);
    }

    public void setGreen(int r, int c, int newGreen) {
        ImageUtil.setGreen(r, c, newGreen, image);
    }

    public void setBlue(int r, int c, int newBlue) {
        ImageUtil.setBlue(r, c, newBlue, image);
    }

    public void addKid(Organism org) {
        kids.add(org);
    }

}