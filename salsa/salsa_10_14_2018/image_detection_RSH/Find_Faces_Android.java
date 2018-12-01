package image_detection_RSH;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.LinkedList;

import jjil.algorithm.Gray8Rgb;
import jjil.algorithm.RgbAvgGray;
import jjil.android.RgbImageAndroid;
import jjil.core.Image;
import jjil.core.Rect;
import jjil.core.RgbImage;

public class Find_Faces_Android {
    private Bitmap bi = null;

    public Find_Faces_Android(){

    }
   
    public boolean readImage(InputStream img_st){
       bi = BitmapFactory.decodeStream(img_st);

       if (bi!=null)
          return true;
       else
          return false;
    }

    public int findFaces(int minScale, int maxScale, InputStream hcsb_file) {
       return findFaces(minScale, maxScale, hcsb_file, false, null, null);
    }

    public int findFaces(int minScale, int maxScale, InputStream hcsb_file, boolean toReport, File output, Context c) {
        if ( bi==null || hcsb_file==null )
          return 0;

        try {
            InputStream is  = hcsb_file;
            Gray8DetectHaarMultiScale detectHaar = new Gray8DetectHaarMultiScale(is, minScale, maxScale);
            RgbImage im = RgbImageAndroid.toRgbImage(bi);
            RgbAvgGray toGray = new RgbAvgGray();
            toGray.push(im);
            List<Rect> results = detectHaar.pushAndReturn(toGray.getFront());
            if (results!=null && results.size()>0)
                results = filter(results);
              
            if (toReport) {
                Image i = detectHaar.getFront();
                Gray8Rgb g2rgb = new Gray8Rgb();
                g2rgb.push(i);
                RgbImageAndroid conv = new RgbImageAndroid();
                //The following line has not been tested and might not work correctly ?????
                conv.toFile( c, (RgbImage)g2rgb.getFront(), 1, output.getCanonicalPath());
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
