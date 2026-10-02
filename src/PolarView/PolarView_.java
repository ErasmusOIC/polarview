package PolarView;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import net.imglib2.*;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.interpolation.neighborsearch.NearestNeighborSearchInterpolatorFactory;
import net.imglib2.neighborsearch.NearestNeighborSearch;
import net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

import java.awt.*;
import java.awt.Point;
import java.lang.reflect.Array;

public class PolarView_ implements PlugIn {
    @Override
    public void run(String s) {
        ImagePlus imp = WindowManager.getCurrentImage();
        ImageStack ims = imp.getImageStack();
        Roi roi = imp.getRoi();

        int xc = roi.getBounds().x + (roi.getBounds().width/2);
        int yc = roi.getBounds().y + (roi.getBounds().height/2);
        int ResultsWidth = (int) Math.ceil(Math.PI*roi.getBounds().width);
        int ResultsHeight = (int) Math.ceil(roi.getBounds().width/2.0);

        ImageStack imsResults = new ImageStack(ResultsWidth,ResultsHeight,ims.size());



        for(int slice=0;slice<ims.size();slice++) {
            PointSampleList<UnsignedByteType> elements = new PointSampleList<>(2);
            ImageProcessor ip = ims.getProcessor(slice + 1);
            ImageProcessor ipResults = new ByteProcessor(ResultsWidth, ResultsHeight);
            Object pixels = ipResults.getPixels();
            for (int i = 0; i < ((byte[])pixels).length; i++) {

                    double xResults = i%ResultsWidth;
                    double yResults = ResultsHeight - Math.floor(i/ResultsWidth); // == r in polar coordinates
                    double phiResults = ((xResults/ResultsWidth)*(2*Math.PI))-Math.PI;

                    //determine pixel in original for given result pixel
                    int xOrig = (int) (xc + (Math.cos(phiResults) * yResults));
                    int yOrig = (int) (yc + (Math.sin(phiResults) * yResults));

                    int val = ip.get(xOrig, yOrig);
                    ((byte[])pixels)[i] = (byte) val;


            }
            ipResults.setPixels(pixels);
            imsResults.setProcessor(ipResults, slice + 1);
            IJ.showProgress(slice,ims.size()-1);
        }
        ImagePlus impResults = new ImagePlus("Results",imsResults);
        impResults.updateAndDraw();
        impResults.show();

            /*for (int i = 0; i < points.length; i++) {
                double dx = points[i].x - xc;
                double dy = points[i].y - yc;
                rpoints[i] = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                phipoints[i] = Math.atan2(dy, dx);
                intpoints[i] = ip.getPixel(points[i].x, points[i].y);
                ry[i] = (int) rpoints[i];
                phix[i] = (int) (((phipoints[i] - (0 - Math.PI)) / (2 * Math.PI)) * ResultsWidth);
                net.imglib2.Point rpoint = new net.imglib2.Point(2);
                rpoint.setPosition(phix[i], 0);
                rpoint.setPosition(ry[i], 1);
                elements.add(rpoint, new UnsignedByteType(intpoints[i]));
            }



            FinalInterval interval = new FinalInterval(new long[]{ResultsWidth, ResultsHeight});
            IJ.log("dims: " + interval.numDimensions());
            IterableInterval<UnsignedByteType> Interval = elements;
            KDTree<UnsignedByteType> kd = new KDTree<>(Interval);
            NearestNeighborSearch<UnsignedByteType> search = new NearestNeighborSearchOnKDTree<>(kd);
            RealRandomAccessible<UnsignedByteType> randaccess = Views.interpolate(search, new NearestNeighborSearchInterpolatorFactory<UnsignedByteType>());
            RandomAccessible<UnsignedByteType> randomAccessible = Views.raster(randaccess);
            view[slice] = Views.interval(randomAccessible, interval);

            ImageJFunctions.show((RandomAccessibleInterval<UnsignedByteType>) view[slice]);

             */





    }

}
