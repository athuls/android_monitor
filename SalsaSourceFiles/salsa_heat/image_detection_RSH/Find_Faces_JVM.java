package image_detection_RSH;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.io.IOException;
import java.util.LinkedList;

import jjil.algorithm.Gray8Rgb;
import jjil.algorithm.RgbAvgGray;
import jjil.core.Image;
import jjil.core.Rect;
import jjil.core.RgbImage;
import jjil.j2se.RgbImageJ2se;

public class Find_Faces_JVM {
    private BufferedImage bi = null;

    public Find_Faces_JVM(){

    }
   
    public boolean readImage(InputStream img_st){
       try {
          bi = ImageIO.read(img_st);
      } catch (IOException e) {
          return false;        
       }

       if (bi!=null)
          return true;
       else
          return false;
    }


    public int findFaces(int minScale, int maxScale, InputStream hcsb_file) {
       return findFaces(minScale, maxScale, hcsb_file, false, null);
    }

    public int findFaces(int minScale, int maxScale, InputStream hcsb_file, boolean toReport, File output) {
        if ( bi==null || hcsb_file==null )
          return 0;

        try {
            // step #2 - convert BufferedImage to JJIL Image
            RgbImage im = RgbImageJ2se.toRgbImage(bi);

            // step #3 - convert image to greyscale 8-bits            
            RgbAvgGray toGray = new RgbAvgGray();
            toGray.push(im);

            // step #4 - initialise face detector with correct Haar profile
            InputStream is  = hcsb_file;            
            Gray8DetectHaarMultiScale detectHaar = new Gray8DetectHaarMultiScale(is, minScale, maxScale);

            // step #5 - apply face detector to grayscale image
            //returns a list of rectangles of detected faces
            List<Rect> results = detectHaar.pushAndReturn(toGray.getFront());
            if (results!=null && results.size()>0)
                results = filter(results);

            if (toReport) {
               // step #6 - retrieve resulting face detection mask
               Image i = detectHaar.getFront();
               // finally convert back to RGB image to write out to .jpg file
               Gray8Rgb g2rgb = new Gray8Rgb();
               g2rgb.push(i);
               RgbImageJ2se conv = new RgbImageJ2se();
               conv.toFile((RgbImage)g2rgb.getFront(), output.getCanonicalPath());
            }
            
            return results.size();
        } catch (Throwable e) {
            return -1;
        }
    }

    private List<Rect> filter(List<Rect> rects){
      List<Rect> result = new LinkedList<Rect>();
      for(Rect new_rect:rects){
        if (result.isEmpty()){
          result.add(new_rect);
          continue;
        }

        int result_size = result.size();
        boolean to_be_added = true;
        for(int i=0; i<result_size; i++){
          if (aContainsB(new_rect, result.get(i))){
            result.remove(i);
            break;
          } else if (aContainsB(result.get(i), new_rect)){
            to_be_added = false;
            break;
          }
        }
        if (to_be_added)
          result.add(new_rect);           
      }
      return result;
    }
    
    private boolean aContainsB(Rect a, Rect b){
    if (a.getLeft()  <= b.getLeft() && 
        a.getRight() >= b.getRight() &&
        a.getTop()   <= b.getTop() &&
        a.getBottom()>= b.getBottom())
        return true;
        
     return false;
    }
    
}
