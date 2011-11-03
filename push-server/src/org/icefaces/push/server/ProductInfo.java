package org.icefaces.push.server;

public class ProductInfo {
    /**
     * The company that owns this product.
     */
    public static String COMPANY = "ICEsoft Technologies, Inc.";
    /**
     * The name of the product.
     */
    public static String PRODUCT = "ICEfaces Push Server";
    /**
     * The 3 levels of version identification, e.g. 1.0.0.
     */
    public static String PRIMARY = "x";
    public static String SECONDARY = "x";
    public static String TERTIARY = "x";
    /**
     * The release type of the product (alpha, beta, production).
     */
    public static String RELEASE_TYPE = "x";
    /**
     * The build number.  Typically this would be tracked and maintained
     * by the build system (i.e. Ant).
     */
    public static String BUILD_NO = "x";
    /**
     * The revision number retrieved from the repository for this build.
     * This is substitued automatically by subversion.
     */
    public static String REVISION = "x";

    /**
     * Convenience method to get all the relevant product information.
     * @return
     */
    public String toString(){
        return
            new StringBuffer().
                append("\r\n").
                append(COMPANY).append("\r\n").
                append(PRODUCT).append(" ").
                    append(PRIMARY).append(".").
                        append(SECONDARY).append(".").
                        append(TERTIARY).append(" ").
                    append(RELEASE_TYPE).append("\n").
                append("Build number: ").append(BUILD_NO).append("\n").
                append("Revision: ").append(REVISION).append("\n").
                    toString();
    }

    public static void main(final String[] arguments) {
        System.out.println(new ProductInfo());
    }
}
