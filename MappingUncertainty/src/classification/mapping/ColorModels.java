/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.mapping;
//import statements
import Jama.Matrix;
import java.text.*;
import java.util.*;
import java.io.*;
import java.awt.*;
/**
 *
 * @author cisc
 */
public class ColorModels {

                 // unless otherwise stated, all color values are
        // given as doubles in the range 0.0 - 1.0

        // all methods throw an IllegalArgumentException if an unexpected argument is encountered

        /**
        * converts 'additive' RGB to 'subtractive' CMY
        * @param rgb - color in RGB space
        * @return color in CMY space
        */
        public static double[] RGBtoCMY (double[] rgb) {
        for(int index = 0; index < 3; index++)
        rgb[index] = 1.0 - rgb[index];
        return rgb;
        }

        /**
        * converts 'subtractive' CMY to 'additive' RGB
        * @param cmy - color in CMY space
        * @return color in RGB space
        */
        public static double[] CMYtoRGB (double[] cmy) {
        for(int index = 0; index < 3; index++)
        cmy[index] = 1.0 - cmy[index];
        return cmy;
        }

        /**
        * converts color from RGB to HSV color space
        * hue will be a number between 0..360
        * @param rgb - color in RGB space
        * @return color in HSV space
        */
        public static double[] RGBtoHSV (double[] rgb)
        {
        double min = rgb[0], max = rgb[0], h = 0, s = 0, v = 0;

        //find the max and the min
        for(int index = 1; index < 3; index++)
        {
        if(max < rgb[index])
        max = rgb[index];
        if(min > rgb[index])
        min = rgb[index];
        }

        //finding h (according to wikipedia)
        if(max == min)
        h = 0;

        if(max == rgb[0]) //if max = r
        h = (60 * ((rgb[1] - rgb[2])/(max - min)) + 360) % 360;

        if(max == rgb[1]) //if max = g
        h = (60 * (rgb[2] - rgb[0])/(max - min)) + 120;

        if(max == rgb[2]) //if max = b
        h = (60 * (rgb[0] - rgb[1])/(max - min)) + 240;

        //determine s
        if(max == 0)
        s = 0;
        else
        s = (max - min)/max;
        v = max;
        //put all variables into array

        rgb[0] = h; rgb[1] = s; rgb[2] = v;
        return rgb;
        }

        /**
        * converts color from HSV to RGB color space
        * hue will be a number between 0..360
        * @param hsv - color in HSV space
        * @return color in RGB space
        */
        public static double[] HSVtoRGB (double[] hsv)
        {
        //using same variable names as wikipedia's article
        double i = (hsv[0] / 60) % 6;
        double f = (hsv[0] / 60) - (hsv[0] / 60);
        double p = hsv[2] * (1 - hsv[1]);
        double q = hsv[2] * (1- f * hsv[1]);
        double t = hsv[2] * (1 - (1 - f) * hsv[1]);
        double v = hsv[2];

        //compute RGB based on i
        switch((int)i)
        {
        case 0:
        hsv[0] = v; hsv[1] = t; hsv[2] = p;
        break;
        case 1:
        hsv[0] = q; hsv[1] = v; hsv[2] = p;
        break;
        case 2:
        hsv[0] = p; hsv[1] = v; hsv[2] = t;
        break;
        case 3:
        hsv[0] = p; hsv[1] = q; hsv[2] = v;
        break;
        case 4:
        hsv[0] = t; hsv[1] = p; hsv[2] = v;
        break;
        case 5:
        hsv[0] = v; hsv[1] = p; hsv[2] = q;
        break;
        }
        return hsv;
        }

        /**
        * converts color from RGB to YIQ color space
        * @param rgb - color in RGB space
        * @return color in YIQ space
        */
        public static double[] RGBtoYIQ (double[] rgb)
        {
        //create Matrices
        double [][] toYIQNums = {{.299, .587, .114}, {.596, -.275, -.321}, {.212, -.523, .311}};
        Matrix linearTransform = new Matrix(toYIQNums);
        Matrix rGBMatrix = new Matrix(rgb, 3);

        //run the linear transformation (matrix multiplication)
        linearTransform = linearTransform.times(rGBMatrix);
        rgb = linearTransform.getRowPackedCopy();

        return rgb;
        }


        /**
        * converts color from YIQ to RGB color space
        * @param yiq - color in YIQ space
        * @return color in RGB space
        */
        public static double[] YIQtoRGB (double[] yiq)
        {
        //create Matrices
        double [][] toRGBNums = {{1.0, .956, .621}, {1.0, -.272, -.647}, {1.0, -1.105, 1.702}};
        Matrix linearTransform = new Matrix(toRGBNums);
        Matrix yIQMatrix = new Matrix(yiq, 3);

        //run the linear transformation (matrix multiplication)
        linearTransform = linearTransform.times(yIQMatrix);
        yiq = linearTransform.getRowPackedCopy();
        return yiq;
        }

        /**
        * converts color from RGB to CIE XYZ color space
        * @param rgb - color in RGB space
        * @return color in CIE XYZ space
        */
        public static double[] RGBtoXYZ (double[] rgb)
        {
        //create Matrices
        double [][] toXYZNums = {{.412453, .357580, .180423}, {.212671, .715160, .072169}, {.019334, .119193, .950227}};
        Matrix linearTransform = new Matrix(toXYZNums);
        Matrix rGBMatrix = new Matrix(rgb, 3);

        //run the linear transformation (matrix multiplication)
        linearTransform = linearTransform.times(rGBMatrix);
        rgb = linearTransform.getRowPackedCopy();
        return rgb;
        }


        /**
        * converts color from CIE XYZ to RGB color space
        * @param xyz - color in XYZ space
        * @return color in RGB space
        */
        public static double[] XYZtoRGB (double[] xyz)
        {
        //Create Matrices
        double [][] toRGBNums = {{3.240479, -1.537150, -0.498535}, {-.969256, 1.875992, .041556}, {.055648, -.204043, 1.057311}};
        Matrix linearTransform = new Matrix(toRGBNums);
        Matrix xYZMatrix = new Matrix(xyz, 3);

        //run the linear transformation (matrix multiplication)
        linearTransform = linearTransform.times(xYZMatrix);
        xyz = linearTransform.getRowPackedCopy();
        return xyz;
        }

        public static double[] xyz2rgb(double[] xyz)
        {
        //X from 0 to  95.047      (Observer = 2°, Illuminant = D65)
        //Y from 0 to 100.000
        //Z from 0 to 108.883
        double x = xyz[0] / 100;
        double y = xyz[1] / 100;
        double z = xyz[2] / 100;

        double r = x * 3.240479 + y * -1.537150 + z * -0.498535;
        double g = x * -0.969256 + y * 1.875992 + z * 0.041556;
        double b = x * 0.055648 + y * -0.204043 + z * 1.057311;

       /* if ( r > 0.0031308 ) { r = 1.055 * Math.pow( r , ( 1 / 2.4 ) ) - 0.055; }
        else { r = 12.92 * r; }
        if ( g > 0.0031308 ) { g = 1.055 * Math.pow( g , ( 1 / 2.4 ) ) - 0.055; }
        else { g = 12.92 * g; }
        if ( b > 0.0031308 ) { b = 1.055 * Math.pow( b , ( 1 / 2.4 ) ) - 0.055; }
        else { b = 12.92 * b; }*/
        
         if ( r > 0.0031308 ) { r = 1.055 * Math.exp(Math.log(r)/2.4) - 0.055; }
        else { r = 12.92 * r; }
        if ( g > 0.0031308 ) { g = 1.055 * Math.exp(Math.log(g)/2.4) - 0.055; }
        else { g = 12.92 * g; }
        if ( b > 0.0031308 ) { b = 1.055 * Math.exp(Math.log(b)/2.4) - 0.055; }
        else { b = 12.92 * b; }

        double[] rgb = new double[3];
//        rgb[0] =r;
//        rgb[1]=g;
//        rgb[2]=b;
//
//        rgb[0] = Math.round( r * 255 );
//        rgb[1] = Math.round( g * 255 );
//        rgb[2] = Math.round( b * 255 );
          rgb[0] =r*255;
          rgb[1]=g*255;
          rgb[2]=b*255;

//          double min =1000;
//          double max = 0;
//          for(int k =0; k<3;k++)
//          {
//              if(rgb[k]<min)min=rgb[k];
//              if(rgb[k]>max)max=rgb[k];
//          }
//          for(int k =0; k<3;k++)
//          {
//              rgb[k]=(rgb[k]-min)/(max-min)*255;
//          }
//
        rgb[0]=Math.min(255.0, Math.max(0.0,rgb[0]));
        rgb[1]=Math.min(255.0, Math.max(0.0,rgb[1]));
        rgb[2]=Math.min(255.0, Math.max(0.0,rgb[2]));
        rgb[0] = Math.round(rgb[0]);
        rgb[1] = Math.round(rgb[1]);
        rgb[2] = Math.round(rgb[2]);

        
        return rgb;
        }


                /**
        * converts color from RGB to CIE Lab color space
        * @param rgb - color in RGB space
        * @return color in CIE Lab space
        */
        public static double[] RGBtoCIELab (double[] rgb)
        {
        //start by converting to XYZ colorspace
        rgb = RGBtoXYZ(rgb);

        //Create variables needed for functions
        double ySubN = 100.000;
        double xSubN = 95.047;
        double zSubN = 108.883;
        double uSubN = .2009;
        double vSubN = .4610;

        /* At this point we use the function f(t)
        such that:
        f(t) = t^(1/3) t > (6/29)^3
        f(t) = (1/3(29/6)^2)t + 4/29 t <= (6/29)^3
        for x, y, and z and then plug the resulting number
        into a function for L*, a*, and b* as pulled from wikipedia
        */

        double functX = rgb[0] / xSubN;
        double functY = rgb[1] / ySubN;
        double functZ = rgb[2] / zSubN;

        if(functX > Math.pow(6.0/29.0, 3.0))
        functX = Math.pow(functX, 1.0/3.0);
        else
        functX = (1.0/3.0)*(Math.pow(29.0 / 6.0, 2.0)*(functX) + 4.0 / 29.0);

        if(functY > Math.pow(6.0/29.0, 3.0))
        functY = Math.pow(functY, 1.0/3.0);
        else
        functY = (1.0/3.0)*(Math.pow(29.0 / 6.0, 2.0)*(functY) + 4.0 / 29.0);

        if(functZ > Math.pow(6.0/29.0, 3.0))
        functZ = Math.pow(functZ, 1.0/3.0);
        else
        functZ = (1.0/3.0)*(Math.pow(29.0 / 6.0, 2.0)*(functZ) + 4.0 / 29.0);

        //using functions from Wikipedia to get L, A, and B
        double l = 116.0 * functY - 16;
        double a = 500.0 * (functX - functY);
        double b = 200.0 * (functY - functZ);
        rgb[0] = l; rgb[1] = a; rgb[2] = b;

        return rgb;
        }

        

        /**
        * converts color from CIE Lab to RGB color space
        * @param lab - color in CIE Lab space
        * @return color in RGB space
        */
        public static double[] CIELabtoRGB (double[] lab)
        {
        //Create variables
        double x = 0, y = 0, z = 0;
        double ySubN = 100.000;
        double xSubN = 95.047;
        double zSubN = 108.883;
        double uSubN = .2009;
        double vSubN = .4610;
        double delta = 6.0 / 29.0;

        //Create functions pulled from wikipedia (steps 1-6)
        double functY = (lab[0] + 16) / 116.0;
        double functX = functY + lab[1] / 500.0;
        double functZ = functY - lab[2] / 200.0;

        if(functY > delta)
        y = ySubN * Math.pow(functY, 3.0);
        else
        y = (functY - 16.0/116.0) * 3.0 * Math.pow(delta, 2.0) * ySubN;

        if(functX > delta)
        x = xSubN * Math.pow(functX, 3.0);
        else
        x = (functX - 16.0/116.0) * 3 * Math.pow(delta, 2.0) * xSubN;

        if(functZ > delta)
        z = zSubN * Math.pow(functZ, 3.0);
        else
        z = (functZ - 16.0 / 116.0) * 3 * Math.pow(delta, 2.0) * zSubN;

        lab[0] = x; lab[1] = y; lab[2] = z;

        //end by converting from xyz to rgb
       // lab = XYZtoRGB(lab);
       lab = xyz2rgb(lab);
        return lab;
        }

        /**
        * converts color from RGB to CIE Luv color space
        * @param rgb - color in RGB space
        * @return color in CIE Luv space
        */
        public static double[] RGBtoCIELuv (double[] rgb)
        {
        //start by converting to XYZ
        rgb = RGBtoXYZ(rgb);

        //Create Variables
        double ySubN = 100.000;
        double xSubN = 95.047;
        double zSubN = 108.883;
        double uSubN = .2009;
        double vSubN = .4610;
        double l = 0, u = 0, v = 0;
        double delta = 6.0 / 29.0;

        //functions according to wikipedia
        double functY = rgb[1] / ySubN;
        double uPrime = 4.0 * rgb[0] / (rgb[0] + 15.0 * rgb[1] + 3.0 * rgb[2]); //IE u' = 4x / (x + 15y + 3z)
        double vPrime = 9.0 * rgb[1] / (rgb[0] + 15.0 * rgb[1] + 3.0 * rgb[2]);

        if(functY > Math.pow(delta, 3.0))
        l = 116.0 * Math.pow(functY, 1.0/3.0) - 16.0;
        else
        l = Math.pow(29.0 / 3.0, 3.0) * functY;

        u = 13.0 * l * (uPrime - uSubN);
        v = 13.0 * l * (vPrime - vSubN);

        //Toss results into the array
        rgb[0] = l; rgb[1] = u; rgb[2] = v;

        return rgb;
        }


        /**
        * converts color from CIE Luv to RGB color space
        * @param luv - color in CIE Luv space
        * @return color in RGB space
        */
        public static double[] CIELuvtoRGB (double[] luv)
        {
        //Create Variables
        double ySubN = 100.000;
        double xSubN = 95.047;
        double zSubN = 108.883;
        double uSubN = .2009;
        double vSubN = .4610;
        double x = 0, y = 0, z = 0;

        //Create functions as per wikipedia's instructions
        double uPrime = luv[1] / 13 * luv[0] + uSubN;
        double vPrime = luv[2] / 13 * luv[0] + vSubN;

        if(luv[0] > 8.0)
        y = ySubN * (9.0 * uPrime / 4.0 * vPrime);
        else
        y = ySubN * luv[0] * Math.pow(3.0 / 29.0, 3.0);

        x = y * (9.0 * uPrime / 4.0 * vPrime);
        z = y * ((12.0 - 3.0 * uPrime - 20.0 * vPrime) / 4.0 * vPrime);

        //toss the xyz results into the array
        luv[0] = x; luv[1] = y; luv[2] = z;

        //end by converting from XYZ to RGB
        luv = XYZtoRGB(luv);
        return luv;
        }

}
