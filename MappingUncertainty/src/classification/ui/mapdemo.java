///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package classification.ui;
//import java.io.File;
//
//import org.geotools.data.CachingFeatureSource;
//import org.geotools.data.FeatureSource;
//import org.geotools.data.FileDataStore;
//import org.geotools.data.FileDataStoreFinder;
//import org.geotools.map.DefaultMapContext;
//import org.geotools.map.MapContext;
//import org.geotools.swing.JMapFrame;
//import org.geotools.swing.data.JFileDataStoreChooser;
//
///**
// * GeoTools Quickstart demo application. Prompts the user for a shapefile
// * and displays its contents on the screen in a map frame
// *
// * @source $URL: http://svn.osgeo.org/geotools/branches/2.7.x/demo/example/src/main/java/org/geotools/demo/Quickstart.java $
// */
//public class mapdemo {
//
//    /**
//     * GeoTools Quickstart demo application. Prompts the user for a shapefile
//     * and displays its contents on the screen in a map frame
//     */
//    public static void main(String[] args) throws Exception {
//        // display a data store file chooser dialog for shapefiles
//        File file = JFileDataStoreChooser.showOpenFile("shp", null);
//        if (file == null) {
//            return;
//        }
//
//        FileDataStore store = FileDataStoreFinder.getDataStore(file);
//        FeatureSource featureSource = store.getFeatureSource();
//
//        // Create a map context and add our shapefile to it
//        MapContext map = new DefaultMapContext();
//        map.setTitle("Quickstart");
//        map.addLayer(featureSource, null);
//
//        // Now display the map
//        JMapFrame.showMap(map);
//
//    }
//
//}