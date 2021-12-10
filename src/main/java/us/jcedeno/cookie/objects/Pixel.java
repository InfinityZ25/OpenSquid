package us.jcedeno.cookie.objects;

/**
 * A record object to represent a pixel in an image. This pixel is meant to be
 * used as a read-only object.
 * 
 * @author jcedeno
 */
public record Pixel(byte x, byte z, byte color) {

}
