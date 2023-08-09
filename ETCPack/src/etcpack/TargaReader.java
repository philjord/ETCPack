package etcpack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;

//http://paulbourke.net/dataformats/tga/
//little endian multi-byte integers: "low-order byte,high-order byte"
//       00,04 -> 04,00 -> 1024
class TargaReader {
	
	static class ImgData{
		public enum TYPE {ARGB,RGB};
		int width;
		int height;
		TYPE type;
		byte[] img;
		byte[] imgalpha;
	}
	
	
     public static BufferedImage getImage(String fileName) throws IOException {
             File f = new File(fileName);
             byte[] buf = new byte[(int)f.length()];
             BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
             bis.read(buf);
             bis.close();
             return decode(buf);
     }
     
     public static ImgData getImageBytes(String fileName) throws IOException {
         File f = new File(fileName);
         byte[] buf = new byte[(int)f.length()];
         BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
         bis.read(buf);
         bis.close();
         return decodeToBytes(buf);
 }

     private static int offset;

     private static int btoi(byte b) {
             int a = b;
             return (a<0?256+a:a);
     }

     private static int read(byte[] buf) {
             return btoi(buf[offset++]);
     }

     public static BufferedImage decode(byte[] buf) throws IOException {
             offset = 0;

             // Reading header bytes
             // buf[2]=image type code 0x02=uncompressed BGR or BGRA
             // buf[12]+[13]=width
             // buf[14]+[15]=height
             // buf[16]=image pixel size 0x20=32bit, 0x18=24bit 
             // buf{17]=Image Descriptor Byte=0x28 (00101000)=32bit/origin upperleft/non-interleaved
             for (int i=0;i<12;i++)
                     read(buf);
             int width = read(buf)+(read(buf)<<8);   // 00,04=1024
             int height = read(buf)+(read(buf)<<8);  // 40,02=576
             read(buf);
             read(buf);

             int n = width*height;
             int[] pixels = new int[n];
             int idx=0;

             if (buf[2]==0x02 && buf[16]==0x20) { // uncompressed BGRA
                 while(n>0) {
                     int b = read(buf);
                     int g = read(buf);
                     int r = read(buf);
                     int a = read(buf);
                     int v = (a<<24) | (r<<16) | (g<<8) | b;
                     pixels[idx++] = v;
                     n-=1;
                 }
             } else if (buf[2]==0x02 && buf[16]==0x18) {  // uncompressed BGR
                 while(n>0) {
                     int b = read(buf);
                     int g = read(buf);
                     int r = read(buf);
                     int a = 255; // opaque pixel
                     int v = (a<<24) | (r<<16) | (g<<8) | b;
                     pixels[idx++] = v;
                     n-=1;
                 }
             } else {
                 // RLE compressed
                 while (n>0) {
                     int nb = read(buf); // num of pixels
                     if ((nb&0x80)==0) { // 0x80=dec 128, bits 10000000
                         for (int i=0;i<=nb;i++) {
                             int b = read(buf);
                             int g = read(buf);
                             int r = read(buf);
                             pixels[idx++] = 0xff000000 | (r<<16) | (g<<8) | b;
                         }
                     } else {
                         nb &= 0x7f;
                         int b = read(buf);
                         int g = read(buf);
                         int r = read(buf);
                         int v = 0xff000000 | (r<<16) | (g<<8) | b;
                         for (int i=0;i<=nb;i++)
                             pixels[idx++] = v;
                     }
                     n-=nb+1;
                 }
             }

             BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
             bimg.setRGB(0, 0, width,height, pixels, 0,width);
             return bimg;
     }
     
     public static ImgData decodeToBytes(byte[] buf) throws IOException {
    	 ImgData imgData = new ImgData();
         

         // Reading header bytes
         // buf[2]=image type code 0x02=uncompressed BGR or BGRA
         // buf[12]+[13]=width
         // buf[14]+[15]=height
         // buf[16]=image pixel size 0x20=32bit, 0x18=24bit 
         // buf{17]=Image Descriptor Byte=0x28 (00101000)=32bit/origin upperleft/non-interleaved

         int offset = 12;
         int width = (buf[offset++]&0xff)+(buf[offset++]<<8);   // 00,04=1024
         int height = (buf[offset++]&0xff)+(buf[offset++]<<8);  // 40,02=576
         offset++;
         offset++;

         int n = width*height;
         byte[] img = new byte[n*3];
         byte[] imgalpha = new byte[n*1];
         int idx=0;
         int idxA=0;

         if (buf[2]==0x02 && buf[16]==0x20) { // uncompressed BGRA
        	 imgData.type = ImgData.TYPE.ARGB;
             while(n>0) {
                 img[idx+2] = buf[offset++];
                 img[idx+1] = buf[offset++];
                 img[idx+0] = buf[offset++];
                 imgalpha[idxA++] = buf[offset++];
                 idx+=3;    
                 n-=1;
             }
         } else if (buf[2]==0x02 && buf[16]==0x18) {  // uncompressed BGR
        	 imgData.type = ImgData.TYPE.RGB;
             while(n>0) {
            	 img[idx+2] = buf[offset++];
                 img[idx+1] = buf[offset++];
                 img[idx+0] = buf[offset++];
                 idx+=3;                 
                 n-=1;
             }
         } else {
        	 imgData.type = ImgData.TYPE.RGB;
             // RLE compressed
             while (n>0) {
                 int nb = buf[offset++]&0xff; // num of pixels
                 if ((nb&0x80)==0) { // 0x80=dec 128, bits 10000000
                     for (int i=0;i<=nb;i++) {
                    	 img[idx+2] = buf[offset++];
                         img[idx+1] = buf[offset++];
                         img[idx+0] = buf[offset++];
                         idx+=3;  
                     }
                 } else {
                     nb &= 0x7f;
                     byte b = buf[offset++];
                     byte g = buf[offset++];
                     byte r = buf[offset++];                     
                     for (int i=0;i<=nb;i++) {
                    	 img[idx+2] = r;
                         img[idx+1] = g;
                         img[idx+0] = b;
                         idx+=3;  
                     }
                 }
                 n-=nb+1;
             }
         }

         imgData.width = width;
         imgData.height = height;
                
         imgData.img = img;
         imgData.imgalpha = imgalpha;
         return imgData;
 }
}
