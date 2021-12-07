package us.jcedeno.cookie.objects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Enum designed to hold the possible cookie types for the CookieGame.
 * CookieEnums also hold the location of the image they will load.
 * 
 * @author jcedeno
 */
public enum CookieEnum {
    CREEPER(0), EYE(1), RODOLFO(2), SQUID(3);

    final String assetLocation;
    final File assetFile;
    final int modelData;
    BufferedImage bufferedImage;

    /**
     * Constructor for CookieEnum. It simply assumes the image location to be under
     * the assets folder with the format of "cookie_{enumType}.png".
     */
    CookieEnum(int modelData) {
        this.modelData = modelData;
        this.assetLocation = System.getProperty("user.dir") + File.separatorChar + "assets" + File.separatorChar
                + "cookie_" + toString().toLowerCase() + ".png";
        this.assetFile = new File(assetLocation);
        reloadAsset();
    }

    /**
     * @return Returns the location of the image for the CookieEnum.
     */
    public String getAssetLocation() {
        return assetLocation;
    }

    /**
     * Util function designed to ease command autocompletion.
     * 
     * @return Returns a list of all the CookieEnums.
     */
    public static List<CookieEnum> getAll() {
        return Arrays.asList(values());
    }

    /**
     * @return Returns the modelData for the CookieEnum.
     */
    public int getModelData() {
        return modelData;
    }

    /**
     * @return Returns the CookieEnum that matches the given modelData.
     */
    public static CookieEnum getByModelData(int modelData) {
        for (CookieEnum cookieEnum : values()) {
            if (cookieEnum.getModelData() == modelData) {
                return cookieEnum;
            }
        }
        return null;
    }

    /**
     * @return Returns the file for the CookieEnum.
     */
    public File getAssetFile() {
        return assetFile;
    }

    /**
     * @return Returns the bufferedImage for the CookieEnum.
     */
    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void reloadAsset() {
        try {
            this.bufferedImage = ImageIO.read(assetFile);
            System.out.println("Registered asset " + assetLocation);
        } catch (IOException e) {
            System.out.println("Failed to register asset " + assetLocation);
            e.printStackTrace();
        }
    }

    public static void reloadAllAssets() {
        for (CookieEnum cookieEnum : values()) {
            cookieEnum.reloadAsset();
        }
    }

}
