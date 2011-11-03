package com.icesoft.faces.application;

public class ProductInfo {

    /**
     * The company that owns this product.
     */
    public static String COMPANY = "ICEsoft Technologies, Inc.";

    /**
     * The name of the product.
     */
    public static String PRODUCT = "ICEfaces";

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
        StringBuffer info = new StringBuffer();
        info.append( "\n" );
        info.append( COMPANY );
        info.append( "\n" );
        info.append( PRODUCT );
        info.append( " " );
        info.append( PRIMARY );
        info.append( "." );
        info.append( SECONDARY );
        info.append( "." );
        info.append( TERTIARY );
        info.append( " " );
        info.append( RELEASE_TYPE );
        info.append( "\n" );
        info.append( "Build number: " );
        info.append( BUILD_NO );
        info.append( "\n" );
        info.append( "Revision: " );
        info.append( REVISION );
        info.append( "\n" );
        return info.toString();
    }

	public static void main(String[] args) {
        ProductInfo app = new ProductInfo();
        System.out.println( app.toString() );
    }

}
