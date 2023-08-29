package etcpack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Random;

import etcpack.TargaReader.ImgData;

// from here
//https://github.com/Ericsson/ETCPACK/blob/master/source/etcpack.cxx


////etcpack v2.74
////
////NO WARRANTY 
////
////BECAUSE THE PROGRAM IS LICENSED FREE OF CHARGE THE PROGRAM IS PROVIDED
////"AS IS". ERICSSON MAKES NO REPRESENTATIONS OF ANY KIND, EXTENDS NO
////WARRANTIES OR CONDITIONS OF ANY KIND; EITHER EXPRESS, IMPLIED OR
////STATUTORY; INCLUDING, BUT NOT LIMITED TO, EXPRESS, IMPLIED OR
////STATUTORY WARRANTIES OR CONDITIONS OF TITLE, MERCHANTABILITY,
////SATISFACTORY QUALITY, SUITABILITY AND FITNESS FOR A PARTICULAR
////PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE
////PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE, YOU ASSUME
////THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION. ERICSSON
////MAKES NO WARRANTY THAT THE MANUFACTURE, SALE, OFFERING FOR SALE,
////DISTRIBUTION, LEASE, USE OR IMPORTATION UNDER THE LICENSE WILL BE FREE
////FROM INFRINGEMENT OF PATENTS, COPYRIGHTS OR OTHER INTELLECTUAL
////PROPERTY RIGHTS OF OTHERS, AND THE VALIDITY OF THE LICENSE IS SUBJECT
////TO YOUR SOLE RESPONSIBILITY TO MAKE SUCH DETERMINATION AND ACQUIRE
////SUCH LICENSES AS MAY BE NECESSARY WITH RESPECT TO PATENTS, COPYRIGHT
////AND OTHER INTELLECTUAL PROPERTY OF THIRD PARTIES.
////
////FOR THE AVOIDANCE OF DOUBT THE PROGRAM (I) IS NOT LICENSED FOR; (II)
////IS NOT DESIGNED FOR OR INTENDED FOR; AND (III) MAY NOT BE USED FOR;
////ANY MISSION CRITICAL APPLICATIONS SUCH AS, BUT NOT LIMITED TO
////OPERATION OF NUCLEAR OR HEALTHCARE COMPUTER SYSTEMS AND/OR NETWORKS,
////AIRCRAFT OR TRAIN CONTROL AND/OR COMMUNICATION SYSTEMS OR ANY OTHER
////COMPUTER SYSTEMS AND/OR NETWORKS OR CONTROL AND/OR COMMUNICATION
////SYSTEMS ALL IN WHICH CASE THE FAILURE OF THE PROGRAM COULD LEAD TO
////DEATH, PERSONAL INJURY, OR SEVERE PHYSICAL, MATERIAL OR ENVIRONMENTAL
////DAMAGE. YOUR RIGHTS UNDER THIS LICENSE WILL TERMINATE AUTOMATICALLY
////AND IMMEDIATELY WITHOUT NOTICE IF YOU FAIL TO COMPLY WITH THIS
////PARAGRAPH.
////
////IN NO EVENT WILL ERICSSON, BE LIABLE FOR ANY DAMAGES WHATSOEVER,
////INCLUDING BUT NOT LIMITED TO PERSONAL INJURY, ANY GENERAL, SPECIAL,
////INDIRECT, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN
////CONNECTION WITH THE USE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT
////NOT LIMITED TO LOSS OF PROFITS, BUSINESS INTERUPTIONS, OR ANY OTHER
////COMMERCIAL DAMAGES OR LOSSES, LOSS OF DATA OR DATA BEING RENDERED
////INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A FAILURE OF
////THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS) REGARDLESS OF THE
////THEORY OF LIABILITY (CONTRACT, TORT OR OTHERWISE), EVEN IF SUCH HOLDER
////OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
////
////(C) Ericsson AB 2005-2013. All Rights Reserved.
////


//#include "image.h"
//this has readPGM and readPPM but actually need ImageMagic so I've cut it out, and simply load tga files,see readSrcFile
//fReadPPM("tmp.ppm",w1,h1,img,bitrate)
//fReadPGM("alphaout.pgm", rw, rh, pixelsA, 8);
 
//Typedefs
//typedef unsigned char uint8;
//typedef unsigned short uint16;
//typedef short int16;

public class ETCPack {	
	//Image.cxx cut out, see readSrcFile
	//Reads a ppm file which is 3x width x height x [1 or 2] bytes (if bit is 8 or 16) of RGB pixel data
//	protected boolean fReadPPM(String string, int[] w, int[] h, byte[][] img, int bitrate) 
//	{return  Image.fReadPPM(string, w, h, img,  bitrate); }
	//Reads a pgm file which is width x height x [1 or 2] bytes (if bit is 8 or 16) of alpha data!
//	protected void fReadPGM(String string, int[] w, int[] h, byte[][] tempdata, int wantedBitDepth) 
//	{  Image.fReadPGM(string, w, h, tempdata, wantedBitDepth); }
	private static void fwrite(int intval, int i, FileChannel f) throws IOException {
		// i tends to be 1	
		ByteBuffer bb = ByteBuffer.allocateDirect(4);
		bb.putInt(intval);
		f.write((ByteBuffer)bb.flip());		
	}
	private static void fwrite(char c, int i, FileChannel f) throws IOException {
		ByteBuffer bb = ByteBuffer.allocateDirect(1);
		bb.put((byte)c);
		f.write((ByteBuffer)bb.flip());
	}
	private static void fwrite(byte b, int i, FileChannel f) throws IOException {
		ByteBuffer bb = ByteBuffer.allocateDirect(1);
		bb.put(b);
		f.write((ByteBuffer)bb.flip());
	}
	private static void fwrite(byte[] data, int i, int j, FileChannel f) throws IOException {
		ByteBuffer bb = ByteBuffer.wrap(data);
		f.write(bb);// no flip as straight wrap
	}
	private static void fwrite(ETCPack.KTX_header header, int i, FileChannel f) throws IOException {
		ByteBuffer bb = ByteBuffer.allocateDirect(12 + 13*4);
		bb.put(KTX_header.KTX_IDENTIFIER_REF);//12
		// now 13*4 bytes => 12+(13*4) = 64bytes
		bb.putInt(header.endianness);
		bb.putInt(header.glType);
		bb.putInt(header.glTypeSize);
		bb.putInt(header.glFormat);
		bb.putInt(header.glInternalFormat);
		bb.putInt(header.glBaseInternalFormat);
		bb.putInt(header.pixelWidth);
		bb.putInt(header.pixelHeight);
		bb.putInt(header.pixelDepth);
		bb.putInt(header.numberOfArrayElements);
		bb.putInt(header.numberOfFaces);
		bb.putInt(header.numberOfMipmapLevels);
		bb.putInt(header.bytesOfKeyValueData);
		f.write((ByteBuffer)bb.flip());		
	}
	private static void fwrite(int intval, int i, ByteBuffer bb) throws IOException {
		// i tends to be 1	
		bb.putInt(intval);
	}
	private static void fwrite(char c, int i, ByteBuffer bb) throws IOException {
		bb.put((byte)c);
	}
	private static void fwrite(byte b, int i, ByteBuffer bb) throws IOException {
		bb.put(b);
	}
	private static void fwrite(byte[] data, int i, int j, ByteBuffer bb) throws IOException {
		bb.put(data); 
	}
	private static void fwrite(ETCPack.KTX_header header, int i, ByteBuffer bb) throws IOException {
		bb.put(KTX_header.KTX_IDENTIFIER_REF);//12
		// now 13*4 bytes => 12+(13*4) = 64bytes
		bb.putInt(header.endianness);
		bb.putInt(header.glType);
		bb.putInt(header.glTypeSize);
		bb.putInt(header.glFormat);
		bb.putInt(header.glInternalFormat);
		bb.putInt(header.glBaseInternalFormat);
		bb.putInt(header.pixelWidth);
		bb.putInt(header.pixelHeight);
		bb.putInt(header.pixelDepth);
		bb.putInt(header.numberOfArrayElements);
		bb.putInt(header.numberOfFaces);
		bb.putInt(header.numberOfMipmapLevels);
		bb.putInt(header.bytesOfKeyValueData);
	}
	
	//helpers
	private static void strcpy(String[] string, String string2) {
		string[0]=string2;	
	}
	private static boolean strncmp(String string, int from, String string2, int count) {
		return string.substring(from).contains(string2.substring(0, count));
	}
	private static boolean strcmp(String string, String string2) {	
		return string.equals(string2);
	}
	

	
	//precalc to ensure the hotspot gets it
	float sqrtW13 = (float)(1.0/Math.sqrt(1.0*3));
	float sqrtW12 = (float)(1.0/Math.sqrt(1.0*2));
	float sqrtW16 = (float)(1.0/Math.sqrt(1.0*6));
	float sqrtW26 = (float)(2.0/Math.sqrt(1.0*6));
	
//Functions needed for decompression ---- in etcdec.cxx
/*
void read_big_endian_2byte_word(unsigned short *blockadr, FILE *f);
void read_big_endian_4byte_word(unsigned int *blockadr, FILE *f);
void unstuff57bits(unsigned int planar_word1, unsigned int planar_word2, unsigned int &planar57_word1, unsigned int &planar57_word2);
void unstuff59bits(unsigned int thumbT_word1, unsigned int thumbT_word2, unsigned int &thumbT59_word1, unsigned int &thumbT59_word2);
void unstuff58bits(unsigned int thumbH_word1, unsigned int thumbH_word2, unsigned int &thumbH58_word1, unsigned int &thumbH58_word2);
*/
//uint8 (colors_RGB444)[2][3], uint8 (colors)[2][3]);
static void decompressColor(int R_B, int G_B, int B_B, byte[][] colors_RGB444, byte[][] colors)
{ETCDec.decompressColor(R_B, G_B, B_B, colors_RGB444, colors);}
//uint8 (colors)[2][3], uint8 (possible_colors)[4][3]
static void calculatePaintColors59T(byte d, PATTERN p, byte[][] colors, byte[][] possible_colors)
{ETCDec.calculatePaintColors59T(d, p, colors, possible_colors);}
//uint8 (colors)[2][3], uint8 (possible_colors)[4][3]
static void calculatePaintColors58H(byte d, PATTERN p, byte[][] colors, byte[][] possible_colors)
{ETCDec.calculatePaintColors58H(d, p, colors, possible_colors);}
static void decompressBlockTHUMB59T(int block_part1, int block_part2, byte[] img,int width,int height,int startx,int starty)
{ETCDec.decompressBlockTHUMB59T(block_part1, block_part2, img, width, height, startx, starty);}
static void decompressBlockTHUMB58H(int block_part1, int block_part2, byte[] img,int width,int height,int startx,int starty)
{ETCDec.decompressBlockTHUMB58H(block_part1, block_part2, img, width, height, startx, starty);}
static void decompressBlockPlanar57(int compressed57_1, int compressed57_2, byte[] img,int width,int height,int startx,int starty)
{ETCDec.decompressBlockPlanar57(compressed57_1, compressed57_2, img, width, height, startx, starty);}
static void decompressBlockDiffFlip(int block_part1, int block_part2, byte[] img,int width,int height,int startx,int starty)
{ETCDec.decompressBlockDiffFlip(block_part1, block_part2, img, width, height, startx, starty);}
//void decompressBlockETC2( int block_part1, int block_part2, byte[] img,int width,int height,int startx,int starty)
//{ETCDec.decompressBlockETC2(block_part1, block_part2, img, width, height, startx, starty);}
static void decompressBlockDifferentialWithAlpha( int block_part1, int block_part2, byte[] img, byte[] alpha, int width, int height, int startx, int starty)
{ETCDec.decompressBlockDifferentialWithAlpha(block_part1, block_part2, img, alpha, width, height, startx, starty);}
//void decompressBlockETC21BitAlpha( int block_part1, int block_part2, byte[] img, byte[] alphaimg, int width,int height,int startx,int starty)
//{ETCDec.decompressBlockETC21BitAlpha(block_part1, block_part2, img, alphaimg, width, height, startx, starty);}
static void decompressBlockTHUMB58HAlpha( int block_part1, int block_part2, byte[] img, byte[] alpha,int width,int height,int startx,int starty)
{ETCDec.decompressBlockTHUMB58HAlpha(block_part1, block_part2, img, alpha, width, height, startx, starty);}
static void decompressBlockTHUMB59TAlpha(int block_part1, int block_part2, byte[] img, byte[] alpha,int width,int height,int startx,int starty)
{ETCDec.decompressBlockTHUMB59TAlpha(block_part1, block_part2, img, alpha, width, height, startx, starty);}
//byte getbit(byte input, int frompos, int topos)
//{return ETCDec.getbit(input, frompos, topos);}
static int getbit(int input, int frompos, int topos)
{return ETCDec.getbit(input, frompos, topos);}
static int clamp(int val)
{return ETCDec.clamp(val);}
//void decompressBlockAlpha(byte[] data,byte[] img,int width,int height,int ix,int iy)
//{ETCDec.decompressBlockAlpha(data, img, width, height, ix, iy);}
static short get16bits11bits(int base, int table, int mul, int index)
{return ETCDec.get16bits11bits(base,  table,  mul, index);}
//void decompressBlockAlpha16bit(byte[] data,byte[] img,int width,int height,int ix,int iy)
//{ETCDec.decompressBlockAlpha16bit(data, img, width, height, ix, iy);} 
static short get16bits11signed(int base, int table, int mul, int index)
{return ETCDec.get16bits11signed(base, table, mul, index);}
static void setupAlphaTable()
{ETCDec.setupAlphaTable();}


//This source code is quite long. You can make it shorter by not including the
//code doing the exhaustive code. Then the -slow modes will not work, but the
//code will be approximately half the number of lines of code.
//Then the lines between "exhaustive code starts here" and "exhaustive code ends here"
//can then be removed.
 

static final int CLAMP(int ll, int x, int ul) { return (((x)<(ll)) ? (ll) : (((x)>(ul)) ? (ul) : (x))); }
static final byte CLAMP(int ll, byte x, int ul) { return (byte)(((x&0xff)<(ll)) ? (ll) : (((x&0xff)>(ul)) ? (ul) : (x&0xff))); }
static final double CLAMP(double ll, double x, double ul) { return (((x)<(ll)) ? (ll) : (((x)>(ul)) ? (ul) : (x))); }

//The below code works as CLAMP(0, x, 255) if x < 255
static final int CLAMP_LEFT_ZERO(int x){return ((~(((int)(x))>>31))&(x));}
//The below code works as CLAMP(0, x, 255) if x is in [0,511]
static final int CLAMP_RIGHT_255(int x){return (((( ((((int)(x))<<23)>>31)  ))|(x))&0x000000ff); }  

static final int SQUARE(int x){return ((x)*(x));}
static final float SQUARE(float x){return ((x)*(x));}
static final double SQUARE(double x){return ((x)*(x));}
// careful with byte inputs and cast outputs
static final int JAS_ROUND(int x){return (((x) < 0.0 ) ? ((int)((x)-0.5)) : ((int)((x)+0.5)));}
static final double JAS_ROUND(double x){return (((x) < 0.0 ) ? ((int)((x)-0.5)) : ((int)((x)+0.5)));}
static final int JAS_MIN(int a,int b){return ((a) < (b) ? (a) : (b));}
static final int JAS_MAX(int a,int b){return ((a) > (b) ? (a) : (b));}

//The error metric Wr Wg Wb should be defined so that Wr^2 + Wg^2 + Wb^2 = 1.
//Hence it is easier to first define the squared values and derive the weights
//as their square-roots.

static final double PERCEPTUAL_WEIGHT_R_SQUARED =0.299;
static final double PERCEPTUAL_WEIGHT_G_SQUARED =0.587;
static final double PERCEPTUAL_WEIGHT_B_SQUARED =0.114;

static final int PERCEPTUAL_WEIGHT_R_SQUARED_TIMES1000 =299;
static final int PERCEPTUAL_WEIGHT_G_SQUARED_TIMES1000 =587;
static final int PERCEPTUAL_WEIGHT_B_SQUARED_TIMES1000 =114;

static final byte RED(byte[] img,int width,int x,int y){return  img[3*(y*width+x)+0];}
static final byte GREEN(byte[] img,int width,int x,int y){return  img[3*(y*width+x)+1];}
static final byte BLUE(byte[] img,int width,int x,int y){return   img[3*(y*width+x)+2];}

static final int SHIFT(int size,int startpos){return  ((startpos)-(size)+1);}
static final int MASK(int size, int startpos){return  (((2<<(size-1))-1) << SHIFT(size,startpos));}
static final void PUTBITS( int dest[], int data, int size, int startpos){dest[0] = ((dest[0] & ~MASK(size, startpos)) | ((data << SHIFT(size, startpos)) & MASK(size,startpos)));}
static final int SHIFTHIGH(int size, int startpos){return  (((startpos)-32)-(size)+1);}
static final int MASKHIGH(int size, int startpos){return  (((1<<(size))-1) << SHIFTHIGH(size,startpos));}
static final void PUTBITSHIGH(int[] dest, int data, int size, int startpos){dest[0] = ((dest[0] & ~MASKHIGH(size, startpos)) | ((data << SHIFTHIGH(size, startpos)) & MASKHIGH(size,startpos)));}
static final void PUTBITSHIGH(int[] dest, byte data, int size, int startpos){dest[0] = ((dest[0] & ~MASKHIGH(size, startpos)) | ((data << SHIFTHIGH(size, startpos)) & MASKHIGH(size,startpos)));}
//Return is an int as more than 8 bits can be asked for
static final int GETBITS(int source, int size, int startpos){return   (( (source) >> ((startpos)-(size)+1) ) & ((1<<(size)) -1));}
//Return is an int as more than 8 bits can be asked for
static final int GETBITSHIGH(int source, int size, int startpos){return   (( (source) >> (((startpos)-32)-(size)+1) ) & ((1<<(size)) -1));}



//Thumb macros and definitions
static final int	R_BITS59T =4;
static final int G_BITS59T =4;
static final int	B_BITS59T =4;
static final int	R_BITS58H =4;
static final int G_BITS58H =4;
static final int	B_BITS58H =4;
static final int	MAXIMUM_ERROR =(255*255*16*1000);
static final int R =0;
static final int G =1;
static final int B =2;
static final int BLOCKHEIGHT =4;
static final int BLOCKWIDTH =4;
static final int BINPOW(int power) {return(1<<(power));}
//#define RADIUS 2
static final int	TABLE_BITS_59T =3;
static final int	TABLE_BITS_58H =3;


//Global tables, used and found in ETCDec
//byte[] table59T= new byte[]{3,6,11,16,23,32,41,64};  // 3-bit table for the 59 bit T-mode
//byte[] table58H = new byte[]{3,6,11,16,23,32,41,64};  // 3-bit table for the 58 bit H-mode
static final int[] weight = new int[]{1,1,1};			// Color weight

//Enums
public static enum PATTERN{PATTERN_H, 	PATTERN_T};

static enum MODE1{MODE_ETC1, MODE_THUMB_T, MODE_THUMB_H, MODE_PLANAR};
//The ETC2 package of codecs includes the following codecs:
//
//codec                                             enum
//--------------------------------------------------------
//GL_COMPRESSED_R11_EAC                            0x9270
//GL_COMPRESSED_SIGNED_R11_EAC                     0x9271
//GL_COMPRESSED_RG11_EAC                           0x9272
//GL_COMPRESSED_SIGNED_RG11_EAC                    0x9273
//GL_COMPRESSED_RGB8_ETC2                          0x9274
//GL_COMPRESSED_SRGB8_ETC2                         0x9275
//GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2      0x9276
//GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2     0x9277
//GL_COMPRESSED_RGBA8_ETC2_EAC                     0x9278
//GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC              0x9279
//
//The older codec ETC1 is not included in the package 
//GL_ETC1_RGB8_OES                                 0x8d64
//but since ETC2 is backwards compatible an ETC1 texture can
//be decoded using the RGB8_ETC2 enum (0x9274)
//
//In a PKM-file, the codecs are stored using the following identifiers
//
//identifier                         value               codec
//--------------------------------------------------------------------
//ETC1_RGB_NO_MIPMAPS                  0                 GL_ETC1_RGB8_OES
//ETC2PACKAGE_RGB_NO_MIPMAPS           1                 GL_COMPRESSED_RGB8_ETC2
//ETC2PACKAGE_RGBA_NO_MIPMAPS_OLD      2, not used       -
//ETC2PACKAGE_RGBA_NO_MIPMAPS          3                 GL_COMPRESSED_RGBA8_ETC2_EAC
//ETC2PACKAGE_RGBA1_NO_MIPMAPS         4                 GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2
//ETC2PACKAGE_R_NO_MIPMAPS             5                 GL_COMPRESSED_R11_EAC
//ETC2PACKAGE_RG_NO_MIPMAPS            6                 GL_COMPRESSED_RG11_EAC
//ETC2PACKAGE_R_SIGNED_NO_MIPMAPS      7                 GL_COMPRESSED_SIGNED_R11_EAC
//ETC2PACKAGE_RG_SIGNED_NO_MIPMAPS     8                 GL_COMPRESSED_SIGNED_RG11_EAC
//
//In the code, the identifiers are not always used strictly. For instance, the
//identifier ETC2PACKAGE_R_NO_MIPMAPS is sometimes used for both the unsigned
//(GL_COMPRESSED_R11_EAC) and signed (GL_COMPRESSED_SIGNED_R11_EAC) version of 
//the codec.
//
public static enum FORMAT{ETC1_RGB,ETC2PACKAGE_RGB,ETC2PACKAGE_RGBA_OLD,ETC2PACKAGE_RGBA,ETC2PACKAGE_RGBA1,ETC2PACKAGE_R,ETC2PACKAGE_RG,ETC2PACKAGE_R_SIGNED,ETC2PACKAGE_RG_SIGNED,ETC2PACKAGE_sRGB,ETC2PACKAGE_sRGBA,ETC2PACKAGE_sRGBA1};
public static enum MODE2{MODE_COMPRESS, MODE_UNCOMPRESS, MODE_PSNR};
public static enum SPEED{SPEED_SLOW, SPEED_FAST, SPEED_MEDIUM};
public static enum METRIC{METRIC_PERCEPTUAL, METRIC_NONPERCEPTUAL};
public static enum CODEC{CODEC_ETC, CODEC_ETC2};


//NOT static, used per instance
private MODE2 mode = MODE2.MODE_COMPRESS;
private SPEED speed = SPEED.SPEED_FAST;
private METRIC metric = METRIC.METRIC_PERCEPTUAL;
private CODEC codec = CODEC.CODEC_ETC2;
private FORMAT format = FORMAT.ETC2PACKAGE_RGB;
private boolean verbose = true;
private boolean generateMipMaps = false;
//extern 
private int formatSigned = 0;
private boolean ktxFile=false;
private boolean first_time_message = true;

private Random rand = new Random(10000);

static final int[] scramble= new int[]{3, 2, 0, 1};
static final int[] unscramble= new int[]{2, 3, 1, 0};
// KTX Header has this stuff
static class KTX_header
{
	static final byte[] KTX_IDENTIFIER_REF = new byte[] { (byte)0xAB, (byte)0x4B, (byte)0x54, (byte)0x58, (byte)0x20, (byte)0x31, (byte)0x31, (byte)0xBB, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A };

	 int endianness;
	 int glType;
	 int glTypeSize;
	 int glFormat;
	 int glInternalFormat;
	 int glBaseInternalFormat;
	 int pixelWidth;
	 int pixelHeight;
	 int pixelDepth;
	 int numberOfArrayElements;
	 int numberOfFaces;
	 int numberOfMipmapLevels;
	 int bytesOfKeyValueData;
}; 


static final int KTX_ENDIAN_REF   =   (0x04030201);
static final int KTX_ENDIAN_REF_REV = (0x01020304) ;

public static final int  GL_R=0x1903;
public static final int  GL_RG=0x8227;
public static final int  GL_RGB=0x1907;
public static final int  GL_RGBA=0x1908;

public static final int  GL_SRGB                                          =0x8C40;
public static final int  GL_SRGB8                                         =0x8C41;
public static final int  GL_SRGB8_ALPHA8                                  =0x8C43;
public static final int  GL_ETC1_RGB8_OES                                 =0x8d64;
public static final int  GL_COMPRESSED_R11_EAC                            =0x9270;
public static final int  GL_COMPRESSED_SIGNED_R11_EAC                     =0x9271;
public static final int  GL_COMPRESSED_RG11_EAC                           =0x9272;
public static final int  GL_COMPRESSED_SIGNED_RG11_EAC                    =0x9273;
public static final int  GL_COMPRESSED_RGB8_ETC2                          =0x9274;
public static final int  GL_COMPRESSED_SRGB8_ETC2                         =0x9275;
public static final int  GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2      =0x9276;
public static final int  GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2     =0x9277;
public static final int  GL_COMPRESSED_RGBA8_ETC2_EAC                     =0x9278;
public static final int  GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC              =0x9279;


//static int RAND_MAX = 2147483647;//https://en.cppreference.com/w/cpp/numeric/random/RAND_MAX

//int[] ktx_identifier; 


//converts indices from        |a0|a1|e0|e1|i0|i1|m0|m1|b0|b1|f0|f1|j0|j1|n0|n1|c0|c1|g0|g1|k0|k1|o0|o1|d0|d1|h0|h1|l0|l1|p0|p1| previously used by T- and H-modes 
//				         into  |p0|o0|n0|m0|l0|k0|j0|i0|h0|g0|f0|e0|d0|c0|b0|a0|p1|o1|n1|m1|l1|k1|j1|i1|h1|g1|f1|e1|d1|c1|b1|a1| which should be used for all modes.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int indexConversion(int pixelIndices) 
{
	int correctIndices = 0;
	int[][] LSB = new int[4][4];
	int[][] MSB = new int[4][4];
	int shift=0;
	for(int y=3; y>=0; y--) 
	{
		for(int x=3; x>=0; x--) 
		{
			LSB[x][y] = (pixelIndices>>shift)&1;
			shift++;
			MSB[x][y] = (pixelIndices>>shift)&1;
			shift++;
		}
	}
	shift=0;
	for(int x=0; x<4; x++) 
	{
		for(int y=0; y<4; y++) 
		{
			correctIndices|=(LSB[x][y]<<shift);
			correctIndices|=(MSB[x][y]<<(16+shift));
			shift++;
		}
	}
	return correctIndices;
}


//Tests if a file exists.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static boolean fileExist(String filename)
{	 
	if(new File(filename).exists())
	{
		return true;
	}
	return false;
}

//Expand source image so that it is divisible by a factor of four in the x-dimension.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
// address pointers byte[1][] img, int[1] expandedwidth, int[1] expandedheight
static boolean expandToWidthDivByFour(byte[][] img, int width, int height, int[] expandedwidth, int[] expandedheight, int bitrate)
{
	int wdiv4;
	int xx, yy;
	byte[] newimg;

	wdiv4 = width /4;
	if( !(wdiv4 *4 == width) )
	{
    	expandedwidth[0] = (wdiv4 + 1)*4;
		expandedheight[0] = height;
	    newimg= new byte[3*expandedwidth[0]*expandedheight[0]*bitrate/8];


		// First copy image
		for(yy = 0; yy<height; yy++)
		{
			for(xx = 0; xx < width; xx++)
			{
				//we have 3*bitrate/8 bytes for each pixel..
				for(int i=0; i<3*bitrate/8; i++) 
				{
					newimg[(yy * expandedwidth[0]+ xx)*3*bitrate/8 + i] = img[0][(yy * width+xx)*3*bitrate/8 + i];

				}
			}
		}

		// Then make the last column of pixels the same as the previous column.

		for(yy = 0; yy< height; yy++)
		{
			for(xx = width; xx < expandedwidth[0]; xx++)
			{
				for(int i=0; i<3*bitrate/8; i++) 
				{
					newimg[(yy * expandedwidth[0]+xx)*3*bitrate/8 + i] = img[0][(yy * width+(width-1))*3*bitrate/8 + i];
				}
			}
		}

		// Now free the old image
		//free(img);

		// Use the new image
		img[0] = newimg;

		return true;
	}
	else
	{
		System.out.println("Image already of even width");
		expandedwidth[0] = width;
		expandedheight[0] = height;
		return false;
	}
}

//Expand source image so that it is divisible by a factor of four in the y-dimension.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
// address pointers byte[1][] img, int[1] expandedwidth, int[1] expandedheight
static boolean expandToHeightDivByFour(byte[][] img, int width, int height, int[] expandedwidth, int[] expandedheight, int bitrate)
{
	int hdiv4;
	int xx, yy;
	int numlinesmissing;
	byte[] newimg;

	hdiv4 = height/4;

	if( !(hdiv4 * 4 == height) )
	{
		expandedwidth[0] = width;
		expandedheight[0] = (hdiv4 + 1) * 4;
		numlinesmissing = expandedheight[0] - height;
		newimg= new byte[3*expandedwidth[0]*expandedheight[0]*bitrate/8];
		
		// First copy image. No need to reformat data.

		for(xx = 0; xx<3*width*height*bitrate/8; xx++)
			newimg[xx] = img[0][xx];

		// Then copy up to three lines.

		for(yy = height; yy < height + numlinesmissing; yy++)
		{
			for(xx = 0; xx<width; xx++)
			{
				for(int i=0; i<3*bitrate/8; i++) 
				{
					newimg[(yy*width+xx)*3*bitrate/8 + i] = img[0][((height-1)*width+xx)*3*bitrate/8 + i];
				}
			}
		}

		// Now free the old image;
		//free(img);

		// Use the new image:
		img[0] = newimg;

		return true;

	}
	else
	{
		System.out.println("Image height already divisible by four.");
		expandedwidth[0] = width;
		expandedheight[0] = height;
		return true;
	}
}


//Find the position of a file extension such as .ppm or .pkm
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int find_pos_of_extension(String src)
{
	return src.lastIndexOf(".");
/*	int q=strlen(src);
	while(q>=0)		// find file name extension
	{
		if(src[q]=='.') break;
		q--;
	}
	if(q<0) 
		return -1;
	else
		return q;*/
}


//Read source file. Does conversion if file format is not .ppm.
//Will expand file to be divisible by four in the x- and y- dimension.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &width,int &height, int &expandedwidth, int &expandedheight
//img is an address of a byte[]
boolean readSrcFile(String filename,byte[][] img,byte[][] imgalpha, int[] width,int[] height, int[] expandedwidth, int[] expandedheight)
{
	//int[] w1=new int[1],h1= new int[1];
	int wdiv4, hdiv4;
	//String str;

	
	//this assumes magick is callable, so I've cut that out
/*
	// Delete temp file if it exists.
	if(fileExist("tmp.ppm"))
	{
		str= "del tmp.ppm";
		System.err.println(str);//system();
	}

	int q = find_pos_of_extension(filename);
	if(filename.endsWith(".ppm")) 
	{
		// Already a .ppm file. Just copy. 
		str="copy "+filename+" tmp.ppm";
		System.out.println("Copying source file"+filename+" to tmp.ppm");
	}
	else
	{
		// Converting from other format to .ppm 
		// 
		// Use your favorite command line image converter program,
		// for instance Image Magick. Just make sure the syntax can
		// be written as below:
		// 
		// C:\magick convert source.jpg dest.ppm
		//
		str="magick convert "+filename+" tmp.ppm";
		System.out.println("Converting source file from "+filename+" to .ppm");
	}
	// Execute system call
	System.err.println(str);//system(str);*/
	
	
	// Load data from tga files only for now
	ImgData imgData = null;
	try {
		imgData = TargaReader.getImageBytes(filename);
	} catch (IOException e) {
		e.printStackTrace();
	}

	int bitrate=8;
	if(format==FORMAT.ETC2PACKAGE_RG)
		bitrate=16;
	if(imgData!= null)//fReadPPM("tmp.ppm",w1,h1,img,bitrate))
	{		
		img[0] = imgData.img;
		imgalpha[0] = imgData.imgalpha;
		
		width[0]=imgData.width;
		height[0]=imgData.height;
		//System.err.println("del tmp.ppm");//system("del tmp.ppm");

		// Width must be divisible by 4 and height must be
		// divisible by 4. Otherwise, we will expand the image

		wdiv4 = width[0] / 4;
		hdiv4 = height[0] / 4;

		expandedwidth[0] = width[0];
		expandedheight[0] = height[0];

		if( !(wdiv4 * 4 == width[0]) )
		{
			System.out.print(" Width = "+width[0]+" is not divisible by four expanding image in x-dir... ");
			if(expandToWidthDivByFour(img, width[0], height[0], expandedwidth, expandedheight, bitrate))
			{
				System.out.println("OK.");
			}
			else
			{
				System.out.println("Error: could not expand image");
				return false;
			}
		}
		if( !(hdiv4 * 4 == height[0]))
		{
			System.out.print(" Height = "+height[0]+" is not divisible by four expanding image in y-dir... ");
			if(expandToHeightDivByFour(img, expandedwidth[0], height[0], expandedwidth, expandedheight, bitrate))
			{
				System.out.println("OK.");
			}
			else
			{
				System.out.println("Error: could not expand image");
				return false;
			}
		}
		if(!(expandedwidth[0] == width[0] && expandedheight[0] == height[0]))
		   System.out.println("Active pixels: "+width[0]+"x"+height[0]+". Expanded image: "+expandedwidth[0]+"x"+expandedheight[0]);
		return true;
	}
	else
	{
		System.out.println("Could not read tmp.ppm file");
		System.exit(1);	
	}
	return false;

}


//Reads a file without expanding it to be divisible by 4.
//Is used when doing PSNR calculation between two files.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static boolean readSrcFileNoExpand(String filename,byte[][] img, int[] width,int[] height)
{
	//int w1,h1;
	//String str;


	// Delete temp file if it exists.
/*	if(fileExist("tmp.ppm"))
	{
		sprintf(str, "del tmp.ppm");
		system(str);
	}*/


/*	int q = find_pos_of_extension(filename);
	if(!strcmp(&filename[q],".ppm")) 
	{
		// Already a .ppm file. Just copy. 
		sprintf(str,"copy %s tmp.ppm ", filename);
		System.out.println("Copying source file to tmp.ppm", filename);
	}
	else
	{
		// Converting from other format to .ppm 
		// 
		// Use your favorite command line image converter program,
		// for instance Image Magick. Just make sure the syntax can
		// be written as below:
		// 
		// C:\magick convert source.jpg dest.ppm
		//
		sprintf(str,"magick convert %s tmp.ppm", filename);
//		System.out.println("Converting source file from %s to .ppm", filename);
	}
	// Execute system call
	system(str);*/
	
	// Load data from tga files only for now
		ImgData imgData = null;
		try {
			imgData = TargaReader.getImageBytes(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(imgData!= null)
		{		
			img[0] = imgData.img;
			//imgalpha[0] = imgData.imgalpha;
			
			width[0]=imgData.width;
			height[0]=imgData.height;
			return true;
		}
	/*if(fReadPPM("tmp.ppm",w1,h1,img,8))
	{
		width=w1;
		height=h1;
		system("del tmp.ppm");

		return true;
	}*/
	return false;
}





//Parses the arguments from the command line.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address src dest
boolean readArguments(String[] args,String[] src,String[] dst)
{
	int q;

	//new code!! do this in a more nicer way!
	boolean srcfound=false,dstfound=false;
	for(int i=0; i<args.length; i++) 
	{
		//loop through the arguments!
		//first check for flags..
		if(args[i].charAt(0)=='-') 
		{
			//handle no arg flags first	
			
			// handle mip maps flag
			if(strcmp(args[i],"-mipmaps")||strcmp(args[i],"-mipmap"))  
			{
				generateMipMaps = true;
			}
			// handle PSNR test flag
			else if(strcmp(args[i],"-p")) 
			{
				mode=MODE2.MODE_PSNR;
			} 
			else
			{						
				if(i==args.length-1) 
				{
					System.out.println("flag missing argument: %s!");
					return false;
				}
				//handle speed flag
				if(strcmp(args[i],"-s"))  
				{
					// We have argument -s. Now check for slow, medium or fast.
					if(strcmp(args[i+1],"slow")) 
						speed = SPEED.SPEED_SLOW;
					else if(strcmp(args[i+1],"medium")) 
						speed = SPEED.SPEED_MEDIUM;
					else if(strcmp(args[i+1],"fast")) 
						speed = SPEED.SPEED_FAST;
					else 
					{
						System.out.println("Error: "+args[i+1]+" not part of flag "+args[i]+"");
						return false;
					}
				}
				//handle verbose flag
				else if(strcmp(args[i],"-v"))  
				{
					// We have argument -s. Now check for slow, medium or fast.
					if(strcmp(args[i+1],"off")) 
						verbose = false;
					else if(strcmp(args[i+1],"on")) 
						verbose = true;
					else 
					{
						System.out.println("Error: "+args[i+1]+" not part of flag "+args[i]+"");
						return false;
					}
				}			
				//error metric flag
				else if(strcmp(args[i],"-e")) 	
				{
					// We have argument -e. Now check for perceptual or nonperceptual
					if(strcmp(args[i+1],"perceptual")) 
						metric = METRIC.METRIC_PERCEPTUAL;
					else if(strcmp(args[i+1],"nonperceptual")) 
						metric = METRIC.METRIC_NONPERCEPTUAL;
					else 
					{
						System.out.println("Error: "+args[i+1]+" not part of flag "+args[i]+"");
						return false;
					}
				}
				//codec flag
				else if(strcmp(args[i],"-c")) 
				{
					// We have argument -c. Now check for perceptual or nonperceptual
					if(strcmp(args[i+1],"etc") || strcmp(args[i+1],"etc1"))
						codec = CODEC.CODEC_ETC;
					else if(strcmp(args[i+1],"etc2")) 
						codec = CODEC.CODEC_ETC2;
					else 
					{
						System.out.println("Error: "+args[i+1]+" not part of flag "+args[i]+"");
						return false;
					}
				}
				//format flag
				else if(strcmp(args[i],"-f")) 
				{
					if(strcmp(args[i+1],"R"))
						format=FORMAT.ETC2PACKAGE_R;
					else if(strcmp(args[i+1],"RG"))
						format=FORMAT.ETC2PACKAGE_RG;
					else if(strcmp(args[i+1],"R_signed")) 
					{
						format=FORMAT.ETC2PACKAGE_R;
						formatSigned=1;
					}
					else if(strcmp(args[i+1],"RG_signed")) 
					{
						format=FORMAT.ETC2PACKAGE_RG;
						formatSigned=1;
					}
					else if(strcmp(args[i+1],"RGB"))
						format=FORMAT.ETC2PACKAGE_RGB;
					else if(strcmp(args[i+1],"sRGB"))
						format=FORMAT.ETC2PACKAGE_sRGB;
					else if(strcmp(args[i+1],"RGBA")||strcmp(args[i+1],"RGBA8"))
						format=FORMAT.ETC2PACKAGE_RGBA;
					else if(strcmp(args[i+1],"sRGBA")||strcmp(args[i+1],"sRGBA8"))
						format=FORMAT.ETC2PACKAGE_sRGBA;
					else if(strcmp(args[i+1],"RGBA1"))
						format=FORMAT.ETC2PACKAGE_RGBA1;
					else if(strcmp(args[i+1],"sRGBA1"))
						format=FORMAT.ETC2PACKAGE_sRGBA1;
					else 
					{
						System.out.println("Error: "+args[i+1]+" not part of flag "+args[i]+"");
						return false;
					}
				}			
				else 
				{
					System.out.println("Error: cannot interpret flag "+args[i]+" "+args[i+1]+"");
					return false;
				}
				//don't read the flag argument next iteration..
				i++;
			}
		}
		//this isn't a flag, so must be src or dst
		else 
		{
			if(srcfound&&dstfound) 
			{
				System.out.println("too many arguments! expecting src, dst; found "+src[0]+", "+dst[0]+", "+args[i]+"");
				return false;
			}
			else if(srcfound) 
			{
				strcpy(dst,args[i]);
				dstfound=true;
			}
			else 
			{
				strcpy(src,args[i]);
				srcfound=true;
			}
		}
	}
	if(!srcfound&&dstfound) 
	{
		System.out.println("too few arguments! expecting src, dst");
		return false;
	}
	if(mode==MODE2.MODE_PSNR)
		return true;
	//check source/destination.. is this compression or decompression?
	q = find_pos_of_extension(src[0]);
	if(q<0) 
	{
		System.out.println("invalid source file: "+src+"");
		return false;
	}

	// If we have etcpack img.pkm img.any
	if(strncmp(src[0],q,".pkm",4)) 
	{
		// First argument is .pkm. Decompress. 
		mode = MODE2.MODE_UNCOMPRESS;			// uncompress from binary file format .pkm
		System.out.println("decompressing pkm");
	}
	else if(strncmp(src[0],q,".ktx",4)) 
	{
		// First argument is .ktx. Decompress. 
		mode = MODE2.MODE_UNCOMPRESS;			// uncompress from binary file format .pkm
		ktxFile=true;
		System.out.println("decompressing ktx");
	}
	else
	{
		// The first argument was not .pkm. The second argument must then be .pkm.
		q = find_pos_of_extension(dst[0]);
		if(q<0) 
		{
			System.out.println("invalid destination file: "+src+"");
			return false;
		}
		if(strncmp(dst[0],q,".pkm",4)) 
		{
			// Second argument is .pkm. Compress. 
			mode = MODE2.MODE_COMPRESS;			// compress to binary file format .pkm
			System.out.println("compressing to pkm");
		}
		else if(strncmp(dst[0],q,".ktx",4)) 
		{
			// Second argument is .ktx. Compress. 
			ktxFile=true;
			mode = MODE2.MODE_COMPRESS;			// compress to binary file format .pkm
			System.out.println("compressing to ktx");
		}
		else 
		{
			System.out.println("source or destination must be a .pkm or .ktx file");
			return false;
		}
	}
	//do some sanity check stuff..
	if(codec==CODEC.CODEC_ETC&&format!=FORMAT.ETC2PACKAGE_RGB) 
	{
		System.out.println("ETC1 codec only supports RGB format");
		return false;
	}
	else if(codec==CODEC.CODEC_ETC)
		format=FORMAT.ETC1_RGB;
	
	return true;
}

static final int[][] compressParams = new int[][]//16][4];
{
	new int[]{-8,-2,2,8},
	new int[]{-8,-2,2,8},
	new int[]{-17,-5,5,17},
	new int[]{-17,-5,5,17},
	new int[]{-29,-9,9,29},
	new int[]{-29,-9,9,29},
	new int[]{-42,-13,13,42},
	new int[]{-42,-13,13,42},
	new int[]{-60,-18,18,60},
	new int[]{-60,-18,18,60},
	new int[]{-80,-24,24,80},
	new int[]{-80,-24,24,80},
	new int[]{-106,-33,33,106},
	new int[]{-106,-33,33,106},
	new int[]{-183,-47,47,183},
	new int[]{-183,-47,47,183},	
};
static final int[] compressParamsFast = new int[]{  -8,  -2,  2,   8,
									 -17,  -5,  5,  17,
									 -29,  -9,  9,  29,
									 -42, -13, 13,  42,
									 -60, -18, 18,  60,
									 -80, -24, 24,  80,
									-106, -33, 33, 106,
									-183, -47, 47, 183};
/*
 *just a static now
boolean readCompressParams()
{
	compressParams[0][0]  =  -8; compressParams[0][1]  =  -2; compressParams[0][2]  =  2; compressParams[0][3]  =   8;
	compressParams[1][0]  =  -8; compressParams[1][1]  =  -2; compressParams[1][2]  =  2; compressParams[1][3]  =   8;
	compressParams[2][0]  = -17; compressParams[2][1]  =  -5; compressParams[2][2]  =  5; compressParams[2][3]  =  17;
	compressParams[3][0]  = -17; compressParams[3][1]  =  -5; compressParams[3][2]  =  5; compressParams[3][3]  =  17;
	compressParams[4][0]  = -29; compressParams[4][1]  =  -9; compressParams[4][2]  =  9; compressParams[4][3]  =  29;
	compressParams[5][0]  = -29; compressParams[5][1]  =  -9; compressParams[5][2]  =  9; compressParams[5][3]  =  29;
	compressParams[6][0]  = -42; compressParams[6][1]  = -13; compressParams[6][2]  = 13; compressParams[6][3]  =  42;
	compressParams[7][0]  = -42; compressParams[7][1]  = -13; compressParams[7][2]  = 13; compressParams[7][3]  =  42;
	compressParams[8][0]  = -60; compressParams[8][1]  = -18; compressParams[8][2]  = 18; compressParams[8][3]  =  60;
	compressParams[9][0]  = -60; compressParams[9][1]  = -18; compressParams[9][2]  = 18; compressParams[9][3]  =  60;
	compressParams[10][0] = -80; compressParams[10][1] = -24; compressParams[10][2] = 24; compressParams[10][3] =  80;
	compressParams[11][0] = -80; compressParams[11][1] = -24; compressParams[11][2] = 24; compressParams[11][3] =  80;
	compressParams[12][0] =-106; compressParams[12][1] = -33; compressParams[12][2] = 33; compressParams[12][3] = 106;
	compressParams[13][0] =-106; compressParams[13][1] = -33; compressParams[13][2] = 33; compressParams[13][3] = 106;
	compressParams[14][0] =-183; compressParams[14][1] = -47; compressParams[14][2] = 47; compressParams[14][3] = 183;
	compressParams[15][0] =-183; compressParams[15][1] = -47; compressParams[15][2] = 47; compressParams[15][3] = 183;
	
	return true;
}*/

//Computes the average color in a 2x4 area and returns the average color as a float.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void computeAverageColor2x4noQuantFloat(byte[] img,int width,int height,int startx,int starty,float[] avg_color)
{
	int r=0,g=0,b=0;
	for(int y=starty; y<starty+4; y++)
	{
		for(int x=startx; x<startx+2; x++)
		{
			r+=RED(img,width,x,y)&0xff;
			g+=GREEN(img,width,x,y)&0xff;
			b+=BLUE(img,width,x,y)&0xff;
		}
	}

	avg_color[0]=(float)(r/8.0);
	avg_color[1]=(float)(g/8.0);
	avg_color[2]=(float)(b/8.0);

}

//Computes the average color in a 4x2 area and returns the average color as a float.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void computeAverageColor4x2noQuantFloat(byte[] img,int width,int height,int startx,int starty,float[] avg_color)
{
	int r=0,g=0,b=0;
	for(int y=starty; y<starty+2; y++)
	{
		for(int x=startx; x<startx+4; x++)
		{
			r+=RED(img,width,x,y)&0xff;
			g+=GREEN(img,width,x,y)&0xff;
			b+=BLUE(img,width,x,y)&0xff;
		}
	}

	avg_color[0]=(float)(r/8.0);
	avg_color[1]=(float)(g/8.0);
	avg_color[2]=(float)(b/8.0);
}

//Finds all pixel indices for a 2x4 block.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int compressBlockWithTable2x4(byte[] img,int width,int height,int startx,int starty,byte[] avg_color,int table,int[] pixel_indices_MSBp, int[] pixel_indices_LSBp)
{
	int[] orig= new int[3],approx= new int[3];
	int[] pixel_indices_MSB=new int[] {0}, pixel_indices_LSB=new int[] {0};
	int pixel_indices=0;
	int sum_error=0;
	int q, i;


	i = 0;
	for(int x=startx; x<startx+2; x++)
	{
		for(int y=starty; y<starty+4; y++)
		{
			int err;
			int best=0;
			int min_error=255*255*3*16;
			orig[0]=RED(img,width,x,y);
			orig[1]=GREEN(img,width,x,y);
			orig[2]=BLUE(img,width,x,y);

			for(q=0;q<4;q++)
			{
				approx[0]=CLAMP(0, (avg_color[0]&0xff)+compressParams[table][q],255);
				approx[1]=CLAMP(0, (avg_color[1]&0xff)+compressParams[table][q],255);
				approx[2]=CLAMP(0, (avg_color[2]&0xff)+compressParams[table][q],255);

				// Here we just use equal weights to R, G and B. Although this will
				// give visually worse results, it will give a better PSNR score. 
				err=SQUARE(approx[0]-orig[0]) + SQUARE(approx[1]-orig[1]) + SQUARE(approx[2]-orig[2]);
				if(err<min_error)
				{
					min_error=err;
					best=q;
				}

			}
			pixel_indices = scramble[best];

			PUTBITS( pixel_indices_MSB, (pixel_indices >> 1), 1, i);
			PUTBITS( pixel_indices_LSB, (pixel_indices & 1) , 1, i);

			i++;

			// In order to simplify hardware, the table {-12, -4, 4, 12} is indexed {11, 10, 00, 01}
			// so that first bit is sign bit and the other bit is size bit (4 or 12). 
			// This means that we have to scramble the bits before storing them. 
			sum_error+=min_error;
		}
	}

	pixel_indices_MSBp = pixel_indices_MSB;//note pointer assignment
	pixel_indices_LSBp = pixel_indices_LSB;
	return sum_error;
}

static final int MAXERR1000 = 1000*255*255*16;

//Finds all pixel indices for a 2x4 block using perceptual weighting of error.
//Done using fixed poinit arithmetics where weights are multiplied by 1000.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//unsigned 
static int compressBlockWithTable2x4percep1000(byte[] img,int width,int height,int startx,int starty,byte[] avg_color,int table, int[] pixel_indices_MSBp,  int[] pixel_indices_LSBp)
{
	int[] orig = new int[3], approx=new int[3];
	//unsigned 
	int pixel_indices_MSB=0, pixel_indices_LSB=0, pixel_indices = 0;
	//unsigned 
	int sum_error=0;
	int q, i;

	i = 0;
	for(int x=startx; x<startx+2; x++)
	{
		for(int y=starty; y<starty+4; y++)
		{
			int err;
			int best=0;
			int min_error=MAXERR1000;
			orig[0]=RED(img,width,x,y);
			orig[1]=GREEN(img,width,x,y);
			orig[2]=BLUE(img,width,x,y);

			for(q=0;q<4;q++)
			{
				approx[0]=CLAMP(0, (avg_color[0]&0xff)+compressParams[table][q],255);
				approx[1]=CLAMP(0, (avg_color[1]&0xff)+compressParams[table][q],255);
				approx[2]=CLAMP(0, (avg_color[2]&0xff)+compressParams[table][q],255);

				// Here we just use equal weights to R, G and B. Although this will
				// give visually worse results, it will give a better PSNR score. 
 				err = (PERCEPTUAL_WEIGHT_R_SQUARED_TIMES1000*SQUARE((approx[0]-orig[0])) 
					 + PERCEPTUAL_WEIGHT_G_SQUARED_TIMES1000*SQUARE((approx[1]-orig[1])) 
					 + PERCEPTUAL_WEIGHT_B_SQUARED_TIMES1000*SQUARE((approx[2]-orig[2])));
				if(err<min_error)
				{
					min_error=err;
					best=q;
				}

			}

			pixel_indices = scramble[best];

			PUTBITS( pixel_indices_MSBp, (pixel_indices >> 1), 1, i);
			PUTBITS( pixel_indices_LSBp, (pixel_indices & 1) , 1, i);

			i++;

			// In order to simplify hardware, the table {-12, -4, 4, 12} is indexed {11, 10, 00, 01}
			// so that first bit is sign bit and the other bit is size bit (4 or 12). 
			// This means that we have to scramble the bits before storing them. 

			
			sum_error+=min_error;
		}

	}

	pixel_indices_MSBp[0] = pixel_indices_MSB;
	pixel_indices_LSBp[0] = pixel_indices_LSB;

	return sum_error;
}

//Finds all pixel indices for a 2x4 block using perceptual weighting of error.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int[] pixel_indices_MSBp, int[] pixel_indices_LSBp
static float compressBlockWithTable2x4percep(byte[] img,int width,int height,int startx,int starty,byte[] avg_color,int table,int[] pixel_indices_MSBp, int[] pixel_indices_LSBp)
{
	byte[] orig= new byte[3],approx= new byte[3];
	int[] pixel_indices_MSB=new int[] {0}, pixel_indices_LSB=new int[] {0};
	int pixel_indices=0;
	float sum_error=0;
	int q, i;

	double wR2 = PERCEPTUAL_WEIGHT_R_SQUARED;
	double wG2 = PERCEPTUAL_WEIGHT_G_SQUARED;
	double wB2 = PERCEPTUAL_WEIGHT_B_SQUARED;

	i = 0;
	for(int x=startx; x<startx+2; x++)
	{
		for(int y=starty; y<starty+4; y++)
		{
			float err;
			int best=0;
			float min_error=255*255*3*16;
			orig[0]=RED(img,width,x,y);
			orig[1]=GREEN(img,width,x,y);
			orig[2]=BLUE(img,width,x,y);

			for(q=0;q<4;q++)
			{

				approx[0]=(byte)CLAMP(0, (avg_color[0]&0xff)+compressParams[table][q],255);
				approx[1]=(byte)CLAMP(0, (avg_color[1]&0xff)+compressParams[table][q],255);
				approx[2]=(byte)CLAMP(0, (avg_color[2]&0xff)+compressParams[table][q],255);

				// Here we just use equal weights to R, G and B. Although this will
				// give visually worse results, it will give a better PSNR score. 
 				err=(float)(wR2*SQUARE(((approx[0]&0xff)-(orig[0]&0xff))) + (float)wG2*SQUARE(((approx[1]&0xff)-(orig[1]&0xff))) + (float)wB2*SQUARE(((approx[2]&0xff)-(orig[2]&0xff))));
				if(err<min_error)
				{
					min_error=err;
					best=q;
				}
			}

			pixel_indices = scramble[best];

			PUTBITS( pixel_indices_MSB, (pixel_indices >> 1), 1, i);
			PUTBITS( pixel_indices_LSB, (pixel_indices & 1) , 1, i);

			i++;

			// In order to simplify hardware, the table {-12, -4, 4, 12} is indexed {11, 10, 00, 01}
			// so that first bit is sign bit and the other bit is size bit (4 or 12). 
			// This means that we have to scramble the bits before storing them. 
		
			sum_error+=min_error;
		}
	}

	pixel_indices_MSBp = pixel_indices_MSB;//note pointer assignment
	pixel_indices_LSBp = pixel_indices_LSB;

	return sum_error;
}

//Finds all pixel indices for a 4x2 block.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int[] pixel_indices_MSBp, int[] pixel_indices_LSBp
static int compressBlockWithTable4x2(byte[] img,int width,int height,int startx,int starty,byte[] avg_color,int table,int[] pixel_indices_MSBp, int[] pixel_indices_LSBp)
{
	byte[] orig= new byte[3],approx= new byte[3];
	int[] pixel_indices_MSB=new int[] {0}, pixel_indices_LSB=new int[] {0};
	int pixel_indices=0;
	int sum_error=0;
	int q;
	int i;

	i = 0;
	for(int x=startx; x<startx+4; x++)
	{
		for(int y=starty; y<starty+2; y++)
		{
			int err;
			int best=0;
			int min_error=255*255*3*16;
			orig[0]=RED(img,width,x,y);
			orig[1]=GREEN(img,width,x,y);
			orig[2]=BLUE(img,width,x,y);

			for(q=0;q<4;q++)
			{
				approx[0]=(byte)CLAMP(0, (avg_color[0]&0xff)+compressParams[table][q],255);
				approx[1]=(byte)CLAMP(0, (avg_color[1]&0xff)+compressParams[table][q],255);
				approx[2]=(byte)CLAMP(0, (avg_color[2]&0xff)+compressParams[table][q],255);

				// Here we just use equal weights to R, G and B. Although this will
				// give visually worse results, it will give a better PSNR score. 
				err=SQUARE((approx[0]&0xff)-(orig[0]&0xff)) + SQUARE((approx[1]&0xff)-(orig[1]&0xff)) + SQUARE((approx[2]&0xff)-(orig[2]&0xff));
				if(err<min_error)
				{
					min_error=err;
					best=q;
				}
			}
			pixel_indices = scramble[best];

			PUTBITS( pixel_indices_MSB, (pixel_indices >> 1), 1, i);
			PUTBITS( pixel_indices_LSB, (pixel_indices & 1) , 1, i);
			i++;

			// In order to simplify hardware, the table {-12, -4, 4, 12} is indexed {11, 10, 00, 01}
			// so that first bit is sign bit and the other bit is size bit (4 or 12). 
			// This means that we have to scramble the bits before storing them. 

			sum_error+=min_error;
		}
		i+=2;
	}

	pixel_indices_MSBp = pixel_indices_MSB;//note pointer assignment
	pixel_indices_LSBp = pixel_indices_LSB;

	return sum_error;
}

//Finds all pixel indices for a 4x2 block using perceptual weighting of error.
//Done using fixed point arithmetics where 1000 corresponds to 1.0.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int[] pixel_indices_MSBp, int[] pixel_indices_LSBp
static int compressBlockWithTable4x2percep1000(byte[] img,int width,int height,int startx,int starty,byte[] avg_color,int table,int[] pixel_indices_MSBp, int[] pixel_indices_LSBp)
{
	byte[] orig= new byte[3],approx= new byte[3];
	int[] pixel_indices_MSB=new int[] {0}, pixel_indices_LSB=new int[] {0};
	int pixel_indices=0;
	int sum_error=0;
	int q;
	int i;

	i = 0;
	for(int x=startx; x<startx+4; x++)
	{
		for(int y=starty; y<starty+2; y++)
		{
			int err;
			int best=0;
			int min_error=MAXERR1000;
			orig[0]=RED(img,width,x,y);
			orig[1]=GREEN(img,width,x,y);
			orig[2]=BLUE(img,width,x,y);

			for(q=0;q<4;q++)
			{
				approx[0]=(byte)CLAMP(0, (avg_color[0]&0xff)+compressParams[table][q],255);
				approx[1]=(byte)CLAMP(0, (avg_color[1]&0xff)+compressParams[table][q],255);
				approx[2]=(byte)CLAMP(0, (avg_color[2]&0xff)+compressParams[table][q],255);

				// Here we just use equal weights to R, G and B. Although this will
				// give visually worse results, it will give a better PSNR score. 
				err = PERCEPTUAL_WEIGHT_R_SQUARED_TIMES1000*SQUARE((approx[0]&0xff)-(orig[0]&0xff)) 
					+ PERCEPTUAL_WEIGHT_G_SQUARED_TIMES1000*SQUARE((approx[1]&0xff)-(orig[1]&0xff)) 
					+ PERCEPTUAL_WEIGHT_B_SQUARED_TIMES1000*SQUARE((approx[2]&0xff)-(orig[2]&0xff));
				if(err<min_error)
				{
					min_error=err;
					best=q;
				}
			}
			pixel_indices = scramble[best];

			PUTBITS( pixel_indices_MSB, (pixel_indices >> 1), 1, i);
			PUTBITS( pixel_indices_LSB, (pixel_indices & 1) , 1, i);
			i++;

			// In order to simplify hardware, the table {-12, -4, 4, 12} is indexed {11, 10, 00, 01}
			// so that first bit is sign bit and the other bit is size bit (4 or 12). 
			// This means that we have to scramble the bits before storing them. 

			sum_error+=min_error;
		}
		i+=2;

	}

	pixel_indices_MSBp = pixel_indices_MSB;//note pointer assignment
	pixel_indices_LSBp = pixel_indices_LSB;

	return sum_error;
}

//Finds all pixel indices for a 4x2 block using perceptual weighting of error.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int[] pixel_indices_MSBp, int[] pixel_indices_LSBp
static float compressBlockWithTable4x2percep(byte[] img,int width,int height,int startx,int starty,byte[] avg_color,int table,int[] pixel_indices_MSBp, int[] pixel_indices_LSBp)
{
	byte[] orig= new byte[3],approx= new byte[3];
	int[] pixel_indices_MSB=new int[] {0}, pixel_indices_LSB=new int[] {0};
	int pixel_indices=0;
	float sum_error=0;
	int q;
	int i;
	float wR2 = (float) PERCEPTUAL_WEIGHT_R_SQUARED;
	float wG2 = (float) PERCEPTUAL_WEIGHT_G_SQUARED;
	float wB2 = (float) PERCEPTUAL_WEIGHT_B_SQUARED;

	i = 0;
	for(int x=startx; x<startx+4; x++)
	{
		for(int y=starty; y<starty+2; y++)
		{
			float err;
			int best=0;
			float min_error=255*255*3*16;
			orig[0]=RED(img,width,x,y);
			orig[1]=GREEN(img,width,x,y);
			orig[2]=BLUE(img,width,x,y);

			for(q=0;q<4;q++)
			{
				approx[0]=(byte)CLAMP(0, (avg_color[0]&0xff)+compressParams[table][q],255);
				approx[1]=(byte)CLAMP(0, (avg_color[1]&0xff)+compressParams[table][q],255);
				approx[2]=(byte)CLAMP(0, (avg_color[2]&0xff)+compressParams[table][q],255);

				// Here we just use equal weights to R, G and B. Although this will
				// give visually worse results, it will give a better PSNR score. 
				err=(float) wR2*SQUARE((approx[0]&0xff)-(orig[0]&0xff)) + (float)wG2*SQUARE((approx[1]&0xff)-(orig[1]&0xff)) + (float)wB2*SQUARE((approx[2]&0xff)-(orig[2]&0xff));
				if(err<min_error)
				{
					min_error=err;
					best=q;
				}
			}
			pixel_indices = scramble[best];

			PUTBITS( pixel_indices_MSB, (pixel_indices >> 1), 1, i);
			PUTBITS( pixel_indices_LSB, (pixel_indices & 1) , 1, i);
			i++;

			// In order to simplify hardware, the table {-12, -4, 4, 12} is indexed {11, 10, 00, 01}
			// so that first bit is sign bit and the other bit is size bit (4 or 12). 
			// This means that we have to scramble the bits before storing them. 

			sum_error+=min_error;
		}
		i+=2;
	}

	pixel_indices_MSBp = pixel_indices_MSB;//note pointer assignment
	pixel_indices_LSBp = pixel_indices_LSB;

	return sum_error;
}

//Table for fast implementation of clamping to the interval [0,255] followed by addition of 255.
static final int[] clamp_table_plus_255 =new int[] {0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 0+255, 
                       0+255, 1+255, 2+255, 3+255, 4+255, 5+255, 6+255, 7+255, 8+255, 9+255, 10+255, 11+255, 12+255, 13+255, 14+255, 15+255, 16+255, 17+255, 18+255, 19+255, 20+255, 21+255, 22+255, 23+255, 24+255, 25+255, 26+255, 27+255, 28+255, 29+255, 30+255, 31+255, 32+255, 33+255, 34+255, 35+255, 36+255, 37+255, 38+255, 39+255, 40+255, 41+255, 42+255, 43+255, 44+255, 45+255, 46+255, 47+255, 48+255, 49+255, 50+255, 51+255, 52+255, 53+255, 54+255, 55+255, 56+255, 57+255, 58+255, 59+255, 60+255, 61+255, 62+255, 63+255, 64+255, 65+255, 66+255, 67+255, 68+255, 69+255, 70+255, 71+255, 72+255, 73+255, 74+255, 75+255, 76+255, 77+255, 78+255, 79+255, 80+255, 81+255, 82+255, 83+255, 84+255, 85+255, 86+255, 87+255, 88+255, 89+255, 90+255, 91+255, 92+255, 93+255, 94+255, 95+255, 96+255, 97+255, 98+255, 99+255, 100+255, 101+255, 102+255, 103+255, 104+255, 105+255, 106+255, 107+255, 108+255, 109+255, 110+255, 111+255, 112+255, 113+255, 114+255, 115+255, 116+255, 117+255, 118+255, 119+255, 120+255, 121+255, 122+255, 123+255, 124+255, 125+255, 126+255, 127+255, 128+255, 129+255, 130+255, 131+255, 132+255, 133+255, 134+255, 135+255, 136+255, 137+255, 138+255, 139+255, 140+255, 141+255, 142+255, 143+255, 144+255, 145+255, 146+255, 147+255, 148+255, 149+255, 150+255, 151+255, 152+255, 153+255, 154+255, 155+255, 156+255, 157+255, 158+255, 159+255, 160+255, 161+255, 162+255, 163+255, 164+255, 165+255, 166+255, 167+255, 168+255, 169+255, 170+255, 171+255, 172+255, 173+255, 174+255, 175+255, 176+255, 177+255, 178+255, 179+255, 180+255, 181+255, 182+255, 183+255, 184+255, 185+255, 186+255, 187+255, 188+255, 189+255, 190+255, 191+255, 192+255, 193+255, 194+255, 195+255, 196+255, 197+255, 198+255, 199+255, 200+255, 201+255, 202+255, 203+255, 204+255, 205+255, 206+255, 207+255, 208+255, 209+255, 210+255, 211+255, 
						212+255, 213+255, 214+255, 215+255, 216+255, 217+255, 218+255, 219+255, 220+255, 221+255, 222+255, 223+255, 224+255, 225+255, 226+255, 227+255, 228+255, 229+255, 230+255, 231+255, 232+255, 233+255, 234+255, 235+255, 236+255, 237+255, 238+255, 239+255, 240+255, 241+255, 242+255, 243+255, 244+255, 245+255, 246+255, 247+255, 248+255, 249+255, 250+255, 251+255, 252+255, 253+255, 254+255, 255+255,
						255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 
						255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255, 255+255};

//Table for fast implementation of clamping to the interval [0,255]
static final int[] clamp_table=new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
                       0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255,
						255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255};

//Table for fast implementation of squaring for numbers in the interval [-255, 255]
static final int[] square_table=new int[] {65025, 64516, 64009, 63504, 63001, 62500, 62001, 61504, 61009, 60516, 60025, 59536, 59049, 58564, 58081, 57600, 
						 57121, 56644, 56169, 55696, 55225, 54756, 54289, 53824, 53361, 52900, 52441, 51984, 51529, 51076, 50625, 50176, 
						 49729, 49284, 48841, 48400, 47961, 47524, 47089, 46656, 46225, 45796, 45369, 44944, 44521, 44100, 43681, 43264, 
						 42849, 42436, 42025, 41616, 41209, 40804, 40401, 40000, 39601, 39204, 38809, 38416, 38025, 37636, 37249, 36864, 
						 36481, 36100, 35721, 35344, 34969, 34596, 34225, 33856, 33489, 33124, 32761, 32400, 32041, 31684, 31329, 30976, 
						 30625, 30276, 29929, 29584, 29241, 28900, 28561, 28224, 27889, 27556, 27225, 26896, 26569, 26244, 25921, 25600, 
						 25281, 24964, 24649, 24336, 24025, 23716, 23409, 23104, 22801, 22500, 22201, 21904, 21609, 21316, 21025, 20736, 
						 20449, 20164, 19881, 19600, 19321, 19044, 18769, 18496, 18225, 17956, 17689, 17424, 17161, 16900, 16641, 16384, 
						 16129, 15876, 15625, 15376, 15129, 14884, 14641, 14400, 14161, 13924, 13689, 13456, 13225, 12996, 12769, 12544, 
						 12321, 12100, 11881, 11664, 11449, 11236, 11025, 10816, 10609, 10404, 10201, 10000, 9801, 9604, 9409, 9216, 
						 9025, 8836, 8649, 8464, 8281, 8100, 7921, 7744, 7569, 7396, 7225, 7056, 6889, 6724, 6561, 6400, 
						 6241, 6084, 5929, 5776, 5625, 5476, 5329, 5184, 5041, 4900, 4761, 4624, 4489, 4356, 4225, 4096, 
						 3969, 3844, 3721, 3600, 3481, 3364, 3249, 3136, 3025, 2916, 2809, 2704, 2601, 2500, 2401, 2304, 
						 2209, 2116, 2025, 1936, 1849, 1764, 1681, 1600, 1521, 1444, 1369, 1296, 1225, 1156, 1089, 1024, 
						 961, 900, 841, 784, 729, 676, 625, 576, 529, 484, 441, 400, 361, 324, 289, 256,
						 225, 196, 169, 144, 121, 100, 81, 64, 49, 36, 25, 16, 9, 4, 1, 
					     0, 1, 4, 9, 16, 25, 36, 49, 64, 81, 100, 121, 144, 169, 196, 225, 
                         256, 289, 324, 361, 400, 441, 484, 529, 576, 625, 676, 729, 784, 841, 900, 961, 
						 1024, 1089, 1156, 1225, 1296, 1369, 1444, 1521, 1600, 1681, 1764, 1849, 1936, 2025, 2116, 2209, 
						 2304, 2401, 2500, 2601, 2704, 2809, 2916, 3025, 3136, 3249, 3364, 3481, 3600, 3721, 3844, 3969, 
						 4096, 4225, 4356, 4489, 4624, 4761, 4900, 5041, 5184, 5329, 5476, 5625, 5776, 5929, 6084, 6241, 
						 6400, 6561, 6724, 6889, 7056, 7225, 7396, 7569, 7744, 7921, 8100, 8281, 8464, 8649, 8836, 9025, 
						 9216, 9409, 9604, 9801, 10000, 10201, 10404, 10609, 10816, 11025, 11236, 11449, 11664, 11881, 12100, 12321,
						 12544, 12769, 12996, 13225, 13456, 13689, 13924, 14161, 14400, 14641, 14884, 15129, 15376, 15625, 15876, 16129,
						 16384, 16641, 16900, 17161, 17424, 17689, 17956, 18225, 18496, 18769, 19044, 19321, 19600, 19881, 20164, 20449, 
						 20736, 21025, 21316, 21609, 21904, 22201, 22500, 22801, 23104, 23409, 23716, 24025, 24336, 24649, 24964, 25281, 
						 25600, 25921, 26244, 26569, 26896, 27225, 27556, 27889, 28224, 28561, 28900, 29241, 29584, 29929, 30276, 30625, 
						 30976, 31329, 31684, 32041, 32400, 32761, 33124, 33489, 33856, 34225, 34596, 34969, 35344, 35721, 36100, 36481, 
						 36864, 37249, 37636, 38025, 38416, 38809, 39204, 39601, 40000, 40401, 40804, 41209, 41616, 42025, 42436, 42849, 
						 43264, 43681, 44100, 44521, 44944, 45369, 45796, 46225, 46656, 47089, 47524, 47961, 48400, 48841, 49284, 49729, 
						 50176, 50625, 51076, 51529, 51984, 52441, 52900, 53361, 53824, 54289, 54756, 55225, 55696, 56169, 56644, 57121, 
						 57600, 58081, 58564, 59049, 59536, 60025, 60516, 61009, 61504, 62001, 62500, 63001, 63504, 64009, 64516, 65025}; 

//Abbreviated variable names to make below tables smaller in source code size
static final int KR =PERCEPTUAL_WEIGHT_R_SQUARED_TIMES1000;
static final int KG =PERCEPTUAL_WEIGHT_G_SQUARED_TIMES1000;
static final int KB =PERCEPTUAL_WEIGHT_B_SQUARED_TIMES1000;

//Table for fast implementation of squaring for numbers in the interval [-255, 255] multiplied by the perceptual weight for red.
static final int[] square_table_percep_red  = new int[]{
                        65025*KR, 64516*KR, 64009*KR, 63504*KR, 63001*KR, 62500*KR, 62001*KR, 61504*KR, 61009*KR, 60516*KR, 60025*KR, 59536*KR, 59049*KR, 58564*KR, 58081*KR, 57600*KR, 
						 57121*KR, 56644*KR, 56169*KR, 55696*KR, 55225*KR, 54756*KR, 54289*KR, 53824*KR, 53361*KR, 52900*KR, 52441*KR, 51984*KR, 51529*KR, 51076*KR, 50625*KR, 50176*KR, 
						 49729*KR, 49284*KR, 48841*KR, 48400*KR, 47961*KR, 47524*KR, 47089*KR, 46656*KR, 46225*KR, 45796*KR, 45369*KR, 44944*KR, 44521*KR, 44100*KR, 43681*KR, 43264*KR, 
						 42849*KR, 42436*KR, 42025*KR, 41616*KR, 41209*KR, 40804*KR, 40401*KR, 40000*KR, 39601*KR, 39204*KR, 38809*KR, 38416*KR, 38025*KR, 37636*KR, 37249*KR, 36864*KR, 
						 36481*KR, 36100*KR, 35721*KR, 35344*KR, 34969*KR, 34596*KR, 34225*KR, 33856*KR, 33489*KR, 33124*KR, 32761*KR, 32400*KR, 32041*KR, 31684*KR, 31329*KR, 30976*KR, 
						 30625*KR, 30276*KR, 29929*KR, 29584*KR, 29241*KR, 28900*KR, 28561*KR, 28224*KR, 27889*KR, 27556*KR, 27225*KR, 26896*KR, 26569*KR, 26244*KR, 25921*KR, 25600*KR, 
						 25281*KR, 24964*KR, 24649*KR, 24336*KR, 24025*KR, 23716*KR, 23409*KR, 23104*KR, 22801*KR, 22500*KR, 22201*KR, 21904*KR, 21609*KR, 21316*KR, 21025*KR, 20736*KR, 
						 20449*KR, 20164*KR, 19881*KR, 19600*KR, 19321*KR, 19044*KR, 18769*KR, 18496*KR, 18225*KR, 17956*KR, 17689*KR, 17424*KR, 17161*KR, 16900*KR, 16641*KR, 16384*KR, 
						 16129*KR, 15876*KR, 15625*KR, 15376*KR, 15129*KR, 14884*KR, 14641*KR, 14400*KR, 14161*KR, 13924*KR, 13689*KR, 13456*KR, 13225*KR, 12996*KR, 12769*KR, 12544*KR, 
						 12321*KR, 12100*KR, 11881*KR, 11664*KR, 11449*KR, 11236*KR, 11025*KR, 10816*KR, 10609*KR, 10404*KR, 10201*KR, 10000*KR, 9801*KR, 9604*KR, 9409*KR, 9216*KR, 
						 9025*KR, 8836*KR, 8649*KR, 8464*KR, 8281*KR, 8100*KR, 7921*KR, 7744*KR, 7569*KR, 7396*KR, 7225*KR, 7056*KR, 6889*KR, 6724*KR, 6561*KR, 6400*KR, 
						 6241*KR, 6084*KR, 5929*KR, 5776*KR, 5625*KR, 5476*KR, 5329*KR, 5184*KR, 5041*KR, 4900*KR, 4761*KR, 4624*KR, 4489*KR, 4356*KR, 4225*KR, 4096*KR, 
						 3969*KR, 3844*KR, 3721*KR, 3600*KR, 3481*KR, 3364*KR, 3249*KR, 3136*KR, 3025*KR, 2916*KR, 2809*KR, 2704*KR, 2601*KR, 2500*KR, 2401*KR, 2304*KR, 
						 2209*KR, 2116*KR, 2025*KR, 1936*KR, 1849*KR, 1764*KR, 1681*KR, 1600*KR, 1521*KR, 1444*KR, 1369*KR, 1296*KR, 1225*KR, 1156*KR, 1089*KR, 1024*KR, 
						 961*KR, 900*KR, 841*KR, 784*KR, 729*KR, 676*KR, 625*KR, 576*KR, 529*KR, 484*KR, 441*KR, 400*KR, 361*KR, 324*KR, 289*KR, 256*KR,
						 225*KR, 196*KR, 169*KR, 144*KR, 121*KR, 100*KR, 81*KR, 64*KR, 49*KR, 36*KR, 25*KR, 16*KR, 9*KR, 4*KR, 1*KR, 
						 0*KR, 1*KR, 4*KR, 9*KR, 16*KR, 25*KR, 36*KR, 49*KR, 64*KR, 81*KR, 100*KR, 121*KR, 144*KR, 169*KR, 196*KR, 225*KR, 
						 256*KR, 289*KR, 324*KR, 361*KR, 400*KR, 441*KR, 484*KR, 529*KR, 576*KR, 625*KR, 676*KR, 729*KR, 784*KR, 841*KR, 900*KR, 961*KR, 
						 1024*KR, 1089*KR, 1156*KR, 1225*KR, 1296*KR, 1369*KR, 1444*KR, 1521*KR, 1600*KR, 1681*KR, 1764*KR, 1849*KR, 1936*KR, 2025*KR, 2116*KR, 2209*KR, 
						 2304*KR, 2401*KR, 2500*KR, 2601*KR, 2704*KR, 2809*KR, 2916*KR, 3025*KR, 3136*KR, 3249*KR, 3364*KR, 3481*KR, 3600*KR, 3721*KR, 3844*KR, 3969*KR, 
						 4096*KR, 4225*KR, 4356*KR, 4489*KR, 4624*KR, 4761*KR, 4900*KR, 5041*KR, 5184*KR, 5329*KR, 5476*KR, 5625*KR, 5776*KR, 5929*KR, 6084*KR, 6241*KR, 
						 6400*KR, 6561*KR, 6724*KR, 6889*KR, 7056*KR, 7225*KR, 7396*KR, 7569*KR, 7744*KR, 7921*KR, 8100*KR, 8281*KR, 8464*KR, 8649*KR, 8836*KR, 9025*KR, 
						 9216*KR, 9409*KR, 9604*KR, 9801*KR, 10000*KR, 10201*KR, 10404*KR, 10609*KR, 10816*KR, 11025*KR, 11236*KR, 11449*KR, 11664*KR, 11881*KR, 12100*KR, 12321*KR,
						 12544*KR, 12769*KR, 12996*KR, 13225*KR, 13456*KR, 13689*KR, 13924*KR, 14161*KR, 14400*KR, 14641*KR, 14884*KR, 15129*KR, 15376*KR, 15625*KR, 15876*KR, 16129*KR,
						 16384*KR, 16641*KR, 16900*KR, 17161*KR, 17424*KR, 17689*KR, 17956*KR, 18225*KR, 18496*KR, 18769*KR, 19044*KR, 19321*KR, 19600*KR, 19881*KR, 20164*KR, 20449*KR, 
						 20736*KR, 21025*KR, 21316*KR, 21609*KR, 21904*KR, 22201*KR, 22500*KR, 22801*KR, 23104*KR, 23409*KR, 23716*KR, 24025*KR, 24336*KR, 24649*KR, 24964*KR, 25281*KR, 
						 25600*KR, 25921*KR, 26244*KR, 26569*KR, 26896*KR, 27225*KR, 27556*KR, 27889*KR, 28224*KR, 28561*KR, 28900*KR, 29241*KR, 29584*KR, 29929*KR, 30276*KR, 30625*KR, 
						 30976*KR, 31329*KR, 31684*KR, 32041*KR, 32400*KR, 32761*KR, 33124*KR, 33489*KR, 33856*KR, 34225*KR, 34596*KR, 34969*KR, 35344*KR, 35721*KR, 36100*KR, 36481*KR, 
						 36864*KR, 37249*KR, 37636*KR, 38025*KR, 38416*KR, 38809*KR, 39204*KR, 39601*KR, 40000*KR, 40401*KR, 40804*KR, 41209*KR, 41616*KR, 42025*KR, 42436*KR, 42849*KR, 
						 43264*KR, 43681*KR, 44100*KR, 44521*KR, 44944*KR, 45369*KR, 45796*KR, 46225*KR, 46656*KR, 47089*KR, 47524*KR, 47961*KR, 48400*KR, 48841*KR, 49284*KR, 49729*KR, 
						 50176*KR, 50625*KR, 51076*KR, 51529*KR, 51984*KR, 52441*KR, 52900*KR, 53361*KR, 53824*KR, 54289*KR, 54756*KR, 55225*KR, 55696*KR, 56169*KR, 56644*KR, 57121*KR, 
						 57600*KR, 58081*KR, 58564*KR, 59049*KR, 59536*KR, 60025*KR, 60516*KR, 61009*KR, 61504*KR, 62001*KR, 62500*KR, 63001*KR, 63504*KR, 64009*KR, 64516*KR, 65025*KR}; 

//Table for fast implementation of squaring for numbers in the interval [-255, 255] multiplied by the perceptual weight for green.
static final int[] square_table_percep_green=new int[] {
                        65025*KG, 64516*KG, 64009*KG, 63504*KG, 63001*KG, 62500*KG, 62001*KG, 61504*KG, 61009*KG, 60516*KG, 60025*KG, 59536*KG, 59049*KG, 58564*KG, 58081*KG, 57600*KG, 
						 57121*KG, 56644*KG, 56169*KG, 55696*KG, 55225*KG, 54756*KG, 54289*KG, 53824*KG, 53361*KG, 52900*KG, 52441*KG, 51984*KG, 51529*KG, 51076*KG, 50625*KG, 50176*KG, 
						 49729*KG, 49284*KG, 48841*KG, 48400*KG, 47961*KG, 47524*KG, 47089*KG, 46656*KG, 46225*KG, 45796*KG, 45369*KG, 44944*KG, 44521*KG, 44100*KG, 43681*KG, 43264*KG, 
						 42849*KG, 42436*KG, 42025*KG, 41616*KG, 41209*KG, 40804*KG, 40401*KG, 40000*KG, 39601*KG, 39204*KG, 38809*KG, 38416*KG, 38025*KG, 37636*KG, 37249*KG, 36864*KG, 
						 36481*KG, 36100*KG, 35721*KG, 35344*KG, 34969*KG, 34596*KG, 34225*KG, 33856*KG, 33489*KG, 33124*KG, 32761*KG, 32400*KG, 32041*KG, 31684*KG, 31329*KG, 30976*KG, 
						 30625*KG, 30276*KG, 29929*KG, 29584*KG, 29241*KG, 28900*KG, 28561*KG, 28224*KG, 27889*KG, 27556*KG, 27225*KG, 26896*KG, 26569*KG, 26244*KG, 25921*KG, 25600*KG, 
						 25281*KG, 24964*KG, 24649*KG, 24336*KG, 24025*KG, 23716*KG, 23409*KG, 23104*KG, 22801*KG, 22500*KG, 22201*KG, 21904*KG, 21609*KG, 21316*KG, 21025*KG, 20736*KG, 
						 20449*KG, 20164*KG, 19881*KG, 19600*KG, 19321*KG, 19044*KG, 18769*KG, 18496*KG, 18225*KG, 17956*KG, 17689*KG, 17424*KG, 17161*KG, 16900*KG, 16641*KG, 16384*KG, 
						 16129*KG, 15876*KG, 15625*KG, 15376*KG, 15129*KG, 14884*KG, 14641*KG, 14400*KG, 14161*KG, 13924*KG, 13689*KG, 13456*KG, 13225*KG, 12996*KG, 12769*KG, 12544*KG, 
						 12321*KG, 12100*KG, 11881*KG, 11664*KG, 11449*KG, 11236*KG, 11025*KG, 10816*KG, 10609*KG, 10404*KG, 10201*KG, 10000*KG, 9801*KG, 9604*KG, 9409*KG, 9216*KG, 
						 9025*KG, 8836*KG, 8649*KG, 8464*KG, 8281*KG, 8100*KG, 7921*KG, 7744*KG, 7569*KG, 7396*KG, 7225*KG, 7056*KG, 6889*KG, 6724*KG, 6561*KG, 6400*KG, 
						 6241*KG, 6084*KG, 5929*KG, 5776*KG, 5625*KG, 5476*KG, 5329*KG, 5184*KG, 5041*KG, 4900*KG, 4761*KG, 4624*KG, 4489*KG, 4356*KG, 4225*KG, 4096*KG, 
						 3969*KG, 3844*KG, 3721*KG, 3600*KG, 3481*KG, 3364*KG, 3249*KG, 3136*KG, 3025*KG, 2916*KG, 2809*KG, 2704*KG, 2601*KG, 2500*KG, 2401*KG, 2304*KG, 
						 2209*KG, 2116*KG, 2025*KG, 1936*KG, 1849*KG, 1764*KG, 1681*KG, 1600*KG, 1521*KG, 1444*KG, 1369*KG, 1296*KG, 1225*KG, 1156*KG, 1089*KG, 1024*KG, 
						 961*KG, 900*KG, 841*KG, 784*KG, 729*KG, 676*KG, 625*KG, 576*KG, 529*KG, 484*KG, 441*KG, 400*KG, 361*KG, 324*KG, 289*KG, 256*KG,
						 225*KG, 196*KG, 169*KG, 144*KG, 121*KG, 100*KG, 81*KG, 64*KG, 49*KG, 36*KG, 25*KG, 16*KG, 9*KG, 4*KG, 1*KG, 
						 0*KG, 1*KG, 4*KG, 9*KG, 16*KG, 25*KG, 36*KG, 49*KG, 64*KG, 81*KG, 100*KG, 121*KG, 144*KG, 169*KG, 196*KG, 225*KG, 
						 256*KG, 289*KG, 324*KG, 361*KG, 400*KG, 441*KG, 484*KG, 529*KG, 576*KG, 625*KG, 676*KG, 729*KG, 784*KG, 841*KG, 900*KG, 961*KG, 
						 1024*KG, 1089*KG, 1156*KG, 1225*KG, 1296*KG, 1369*KG, 1444*KG, 1521*KG, 1600*KG, 1681*KG, 1764*KG, 1849*KG, 1936*KG, 2025*KG, 2116*KG, 2209*KG, 
						 2304*KG, 2401*KG, 2500*KG, 2601*KG, 2704*KG, 2809*KG, 2916*KG, 3025*KG, 3136*KG, 3249*KG, 3364*KG, 3481*KG, 3600*KG, 3721*KG, 3844*KG, 3969*KG, 
						 4096*KG, 4225*KG, 4356*KG, 4489*KG, 4624*KG, 4761*KG, 4900*KG, 5041*KG, 5184*KG, 5329*KG, 5476*KG, 5625*KG, 5776*KG, 5929*KG, 6084*KG, 6241*KG, 
						 6400*KG, 6561*KG, 6724*KG, 6889*KG, 7056*KG, 7225*KG, 7396*KG, 7569*KG, 7744*KG, 7921*KG, 8100*KG, 8281*KG, 8464*KG, 8649*KG, 8836*KG, 9025*KG, 
						 9216*KG, 9409*KG, 9604*KG, 9801*KG, 10000*KG, 10201*KG, 10404*KG, 10609*KG, 10816*KG, 11025*KG, 11236*KG, 11449*KG, 11664*KG, 11881*KG, 12100*KG, 12321*KG,
						 12544*KG, 12769*KG, 12996*KG, 13225*KG, 13456*KG, 13689*KG, 13924*KG, 14161*KG, 14400*KG, 14641*KG, 14884*KG, 15129*KG, 15376*KG, 15625*KG, 15876*KG, 16129*KG,
						 16384*KG, 16641*KG, 16900*KG, 17161*KG, 17424*KG, 17689*KG, 17956*KG, 18225*KG, 18496*KG, 18769*KG, 19044*KG, 19321*KG, 19600*KG, 19881*KG, 20164*KG, 20449*KG, 
						 20736*KG, 21025*KG, 21316*KG, 21609*KG, 21904*KG, 22201*KG, 22500*KG, 22801*KG, 23104*KG, 23409*KG, 23716*KG, 24025*KG, 24336*KG, 24649*KG, 24964*KG, 25281*KG, 
						 25600*KG, 25921*KG, 26244*KG, 26569*KG, 26896*KG, 27225*KG, 27556*KG, 27889*KG, 28224*KG, 28561*KG, 28900*KG, 29241*KG, 29584*KG, 29929*KG, 30276*KG, 30625*KG, 
						 30976*KG, 31329*KG, 31684*KG, 32041*KG, 32400*KG, 32761*KG, 33124*KG, 33489*KG, 33856*KG, 34225*KG, 34596*KG, 34969*KG, 35344*KG, 35721*KG, 36100*KG, 36481*KG, 
						 36864*KG, 37249*KG, 37636*KG, 38025*KG, 38416*KG, 38809*KG, 39204*KG, 39601*KG, 40000*KG, 40401*KG, 40804*KG, 41209*KG, 41616*KG, 42025*KG, 42436*KG, 42849*KG, 
						 43264*KG, 43681*KG, 44100*KG, 44521*KG, 44944*KG, 45369*KG, 45796*KG, 46225*KG, 46656*KG, 47089*KG, 47524*KG, 47961*KG, 48400*KG, 48841*KG, 49284*KG, 49729*KG, 
						 50176*KG, 50625*KG, 51076*KG, 51529*KG, 51984*KG, 52441*KG, 52900*KG, 53361*KG, 53824*KG, 54289*KG, 54756*KG, 55225*KG, 55696*KG, 56169*KG, 56644*KG, 57121*KG, 
						 57600*KG, 58081*KG, 58564*KG, 59049*KG, 59536*KG, 60025*KG, 60516*KG, 61009*KG, 61504*KG, 62001*KG, 62500*KG, 63001*KG, 63504*KG, 64009*KG, 64516*KG, 65025*KG}; 

//Table for fast implementation of squaring for numbers in the interval [-255, 255] multiplied by the perceptual weight for blue.
static final int[] square_table_percep_blue=new int[] {
                        65025*KB, 64516*KB, 64009*KB, 63504*KB, 63001*KB, 62500*KB, 62001*KB, 61504*KB, 61009*KB, 60516*KB, 60025*KB, 59536*KB, 59049*KB, 58564*KB, 58081*KB, 57600*KB, 
						 57121*KB, 56644*KB, 56169*KB, 55696*KB, 55225*KB, 54756*KB, 54289*KB, 53824*KB, 53361*KB, 52900*KB, 52441*KB, 51984*KB, 51529*KB, 51076*KB, 50625*KB, 50176*KB, 
						 49729*KB, 49284*KB, 48841*KB, 48400*KB, 47961*KB, 47524*KB, 47089*KB, 46656*KB, 46225*KB, 45796*KB, 45369*KB, 44944*KB, 44521*KB, 44100*KB, 43681*KB, 43264*KB, 
						 42849*KB, 42436*KB, 42025*KB, 41616*KB, 41209*KB, 40804*KB, 40401*KB, 40000*KB, 39601*KB, 39204*KB, 38809*KB, 38416*KB, 38025*KB, 37636*KB, 37249*KB, 36864*KB, 
						 36481*KB, 36100*KB, 35721*KB, 35344*KB, 34969*KB, 34596*KB, 34225*KB, 33856*KB, 33489*KB, 33124*KB, 32761*KB, 32400*KB, 32041*KB, 31684*KB, 31329*KB, 30976*KB, 
						 30625*KB, 30276*KB, 29929*KB, 29584*KB, 29241*KB, 28900*KB, 28561*KB, 28224*KB, 27889*KB, 27556*KB, 27225*KB, 26896*KB, 26569*KB, 26244*KB, 25921*KB, 25600*KB, 
						 25281*KB, 24964*KB, 24649*KB, 24336*KB, 24025*KB, 23716*KB, 23409*KB, 23104*KB, 22801*KB, 22500*KB, 22201*KB, 21904*KB, 21609*KB, 21316*KB, 21025*KB, 20736*KB, 
						 20449*KB, 20164*KB, 19881*KB, 19600*KB, 19321*KB, 19044*KB, 18769*KB, 18496*KB, 18225*KB, 17956*KB, 17689*KB, 17424*KB, 17161*KB, 16900*KB, 16641*KB, 16384*KB, 
						 16129*KB, 15876*KB, 15625*KB, 15376*KB, 15129*KB, 14884*KB, 14641*KB, 14400*KB, 14161*KB, 13924*KB, 13689*KB, 13456*KB, 13225*KB, 12996*KB, 12769*KB, 12544*KB, 
						 12321*KB, 12100*KB, 11881*KB, 11664*KB, 11449*KB, 11236*KB, 11025*KB, 10816*KB, 10609*KB, 10404*KB, 10201*KB, 10000*KB, 9801*KB, 9604*KB, 9409*KB, 9216*KB, 
						 9025*KB, 8836*KB, 8649*KB, 8464*KB, 8281*KB, 8100*KB, 7921*KB, 7744*KB, 7569*KB, 7396*KB, 7225*KB, 7056*KB, 6889*KB, 6724*KB, 6561*KB, 6400*KB, 
						 6241*KB, 6084*KB, 5929*KB, 5776*KB, 5625*KB, 5476*KB, 5329*KB, 5184*KB, 5041*KB, 4900*KB, 4761*KB, 4624*KB, 4489*KB, 4356*KB, 4225*KB, 4096*KB, 
						 3969*KB, 3844*KB, 3721*KB, 3600*KB, 3481*KB, 3364*KB, 3249*KB, 3136*KB, 3025*KB, 2916*KB, 2809*KB, 2704*KB, 2601*KB, 2500*KB, 2401*KB, 2304*KB, 
						 2209*KB, 2116*KB, 2025*KB, 1936*KB, 1849*KB, 1764*KB, 1681*KB, 1600*KB, 1521*KB, 1444*KB, 1369*KB, 1296*KB, 1225*KB, 1156*KB, 1089*KB, 1024*KB, 
						 961*KB, 900*KB, 841*KB, 784*KB, 729*KB, 676*KB, 625*KB, 576*KB, 529*KB, 484*KB, 441*KB, 400*KB, 361*KB, 324*KB, 289*KB, 256*KB,
						 225*KB, 196*KB, 169*KB, 144*KB, 121*KB, 100*KB, 81*KB, 64*KB, 49*KB, 36*KB, 25*KB, 16*KB, 9*KB, 4*KB, 1*KB, 
						 0*KB, 1*KB, 4*KB, 9*KB, 16*KB, 25*KB, 36*KB, 49*KB, 64*KB, 81*KB, 100*KB, 121*KB, 144*KB, 169*KB, 196*KB, 225*KB, 
						 256*KB, 289*KB, 324*KB, 361*KB, 400*KB, 441*KB, 484*KB, 529*KB, 576*KB, 625*KB, 676*KB, 729*KB, 784*KB, 841*KB, 900*KB, 961*KB, 
						 1024*KB, 1089*KB, 1156*KB, 1225*KB, 1296*KB, 1369*KB, 1444*KB, 1521*KB, 1600*KB, 1681*KB, 1764*KB, 1849*KB, 1936*KB, 2025*KB, 2116*KB, 2209*KB, 
						 2304*KB, 2401*KB, 2500*KB, 2601*KB, 2704*KB, 2809*KB, 2916*KB, 3025*KB, 3136*KB, 3249*KB, 3364*KB, 3481*KB, 3600*KB, 3721*KB, 3844*KB, 3969*KB, 
						 4096*KB, 4225*KB, 4356*KB, 4489*KB, 4624*KB, 4761*KB, 4900*KB, 5041*KB, 5184*KB, 5329*KB, 5476*KB, 5625*KB, 5776*KB, 5929*KB, 6084*KB, 6241*KB, 
						 6400*KB, 6561*KB, 6724*KB, 6889*KB, 7056*KB, 7225*KB, 7396*KB, 7569*KB, 7744*KB, 7921*KB, 8100*KB, 8281*KB, 8464*KB, 8649*KB, 8836*KB, 9025*KB, 
						 9216*KB, 9409*KB, 9604*KB, 9801*KB, 10000*KB, 10201*KB, 10404*KB, 10609*KB, 10816*KB, 11025*KB, 11236*KB, 11449*KB, 11664*KB, 11881*KB, 12100*KB, 12321*KB,
						 12544*KB, 12769*KB, 12996*KB, 13225*KB, 13456*KB, 13689*KB, 13924*KB, 14161*KB, 14400*KB, 14641*KB, 14884*KB, 15129*KB, 15376*KB, 15625*KB, 15876*KB, 16129*KB,
						 16384*KB, 16641*KB, 16900*KB, 17161*KB, 17424*KB, 17689*KB, 17956*KB, 18225*KB, 18496*KB, 18769*KB, 19044*KB, 19321*KB, 19600*KB, 19881*KB, 20164*KB, 20449*KB, 
						 20736*KB, 21025*KB, 21316*KB, 21609*KB, 21904*KB, 22201*KB, 22500*KB, 22801*KB, 23104*KB, 23409*KB, 23716*KB, 24025*KB, 24336*KB, 24649*KB, 24964*KB, 25281*KB, 
						 25600*KB, 25921*KB, 26244*KB, 26569*KB, 26896*KB, 27225*KB, 27556*KB, 27889*KB, 28224*KB, 28561*KB, 28900*KB, 29241*KB, 29584*KB, 29929*KB, 30276*KB, 30625*KB, 
						 30976*KB, 31329*KB, 31684*KB, 32041*KB, 32400*KB, 32761*KB, 33124*KB, 33489*KB, 33856*KB, 34225*KB, 34596*KB, 34969*KB, 35344*KB, 35721*KB, 36100*KB, 36481*KB, 
						 36864*KB, 37249*KB, 37636*KB, 38025*KB, 38416*KB, 38809*KB, 39204*KB, 39601*KB, 40000*KB, 40401*KB, 40804*KB, 41209*KB, 41616*KB, 42025*KB, 42436*KB, 42849*KB, 
						 43264*KB, 43681*KB, 44100*KB, 44521*KB, 44944*KB, 45369*KB, 45796*KB, 46225*KB, 46656*KB, 47089*KB, 47524*KB, 47961*KB, 48400*KB, 48841*KB, 49284*KB, 49729*KB, 
						 50176*KB, 50625*KB, 51076*KB, 51529*KB, 51984*KB, 52441*KB, 52900*KB, 53361*KB, 53824*KB, 54289*KB, 54756*KB, 55225*KB, 55696*KB, 56169*KB, 56644*KB, 57121*KB, 
						 57600*KB, 58081*KB, 58564*KB, 59049*KB, 59536*KB, 60025*KB, 60516*KB, 61009*KB, 61504*KB, 62001*KB, 62500*KB, 63001*KB, 63504*KB, 64009*KB, 64516*KB, 65025*KB}; 

//Find the best table to use for a 2x4 area by testing all.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &best_table, int &best_pixel_indices_MSB,  int &best_pixel_indices_LSB)
static int tryalltables_3bittable2x4(byte[] img,int width,int height,int startx,int starty,byte[] avg_color, int[] best_table, int[] best_pixel_indices_MSB,  int[] best_pixel_indices_LSB)
{
	int min_error = 3*255*255*16;
	int q;
	int err;
	int[] pixel_indices_MSB = new int[1], pixel_indices_LSB = new int[1];

	for(q=0;q<16;q+=2)		// try all the 8 tables. 
	{
		err=compressBlockWithTable2x4(img,width,height,startx,starty,avg_color,q,pixel_indices_MSB, pixel_indices_LSB);

		if(err<min_error)
		{
			min_error=err;
			best_pixel_indices_MSB[0] = pixel_indices_MSB[0];
			best_pixel_indices_LSB[0] = pixel_indices_LSB[0];
			best_table[0]=q >> 1;
		}
	}
	return min_error;
}

//Find the best table to use for a 2x4 area by testing all.
//Uses perceptual weighting. 
//Uses fixed point implementation where 1000 equals 1.0
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//addressint &best_table, int &best_pixel_indices_MSB,  int &best_pixel_indices_LSB
static int tryalltables_3bittable2x4percep1000(byte[] img,int width,int height,int startx,int starty,byte[] avg_color,  int[] best_table, int[] best_pixel_indices_MSB,  int[] best_pixel_indices_LSB)
{
	int min_error = MAXERR1000;
	int q;
	int err;
	int[] pixel_indices_MSB = new int[1], pixel_indices_LSB = new int[1];

	for(q=0;q<16;q+=2)		// try all the 8 tables. 
	{

		err=compressBlockWithTable2x4percep1000(img,width,height,startx,starty,avg_color,q,pixel_indices_MSB, pixel_indices_LSB);

		if(err<min_error)
		{

			min_error=err;
			best_pixel_indices_MSB[0] = pixel_indices_MSB[0];
			best_pixel_indices_LSB[0] = pixel_indices_LSB[0];
			best_table[0]=q >> 1;

		}
	}
	return min_error;
}

//Find the best table to use for a 2x4 area by testing all.
//Uses perceptual weighting. 
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//addressint &best_table, int &best_pixel_indices_MSB,  int &best_pixel_indices_LSB
static int tryalltables_3bittable2x4percep(byte[] img,int width,int height,int startx,int starty,byte[] avg_color,  int[] best_table,int[] best_pixel_indices_MSB,  int[] best_pixel_indices_LSB)
{
	float min_error = 3*255*255*16;
	int q;
	float err;
	int[] pixel_indices_MSB = new int[1], pixel_indices_LSB = new int[1];

	for(q=0;q<16;q+=2)		// try all the 8 tables. 
	{
		err=compressBlockWithTable2x4percep(img,width,height,startx,starty,avg_color,q,pixel_indices_MSB, pixel_indices_LSB);

		if(err<min_error)
		{

			min_error=err;
			best_pixel_indices_MSB[0] = pixel_indices_MSB[0];
			best_pixel_indices_LSB[0] = pixel_indices_LSB[0];
			best_table[0]=q >> 1;
		}
	}
	return (int) min_error;
}

//Find the best table to use for a 4x2 area by testing all.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//addressint &best_table, int &best_pixel_indices_MSB,  int &best_pixel_indices_LSB
static int tryalltables_3bittable4x2(byte[] img,int width,int height,int startx,int starty,byte[] avg_color,   int[] best_table,int[] best_pixel_indices_MSB,  int[] best_pixel_indices_LSB)
{
	int min_error = 3*255*255*16;
	int q;
	int err;
	int[] pixel_indices_MSB = new int[1], pixel_indices_LSB = new int[1];

	for(q=0;q<16;q+=2)		// try all the 8 tables. 
	{
		err=compressBlockWithTable4x2(img,width,height,startx,starty,avg_color,q,pixel_indices_MSB, pixel_indices_LSB);

		if(err<min_error)
		{

			min_error=err;
			best_pixel_indices_MSB[0] = pixel_indices_MSB[0];
			best_pixel_indices_LSB[0] = pixel_indices_LSB[0];
			best_table[0]=q >> 1;
		}
	}
	return min_error;
}

//Find the best table to use for a 4x2 area by testing all.
//Uses perceptual weighting. 
//Uses fixed point implementation where 1000 equals 1.0
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//addressint &best_table, int &best_pixel_indices_MSB,  int &best_pixel_indices_LSB
static int tryalltables_3bittable4x2percep1000(byte[] img,int width,int height,int startx,int starty,byte[] avg_color,   int[] best_table,int[] best_pixel_indices_MSB,  int[] best_pixel_indices_LSB)
{
	int min_error = MAXERR1000;
	int q;
	int err;
	int[] pixel_indices_MSB = new int[1], pixel_indices_LSB = new int[1];

	for(q=0;q<16;q+=2)		// try all the 8 tables. 
	{
		err=compressBlockWithTable4x2percep1000(img,width,height,startx,starty,avg_color,q,pixel_indices_MSB, pixel_indices_LSB);

		if(err<min_error)
		{
			min_error=err;
			best_pixel_indices_MSB[0] = pixel_indices_MSB[0];
			best_pixel_indices_LSB[0] = pixel_indices_LSB[0];
			best_table[0]=q >> 1;
		}
	}
	return min_error;
}

//Find the best table to use for a 4x2 area by testing all.
//Uses perceptual weighting. 
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//addressint &best_table, int &best_pixel_indices_MSB,  int &best_pixel_indices_LSB
static int tryalltables_3bittable4x2percep(byte[] img,int width,int height,int startx,int starty,byte[] avg_color,    int[] best_table,int[] best_pixel_indices_MSB,  int[] best_pixel_indices_LSB)
{
	float min_error = 3*255*255*16;
	int q;
	float err;
	int[] pixel_indices_MSB = new int[1], pixel_indices_LSB = new int[1];

	for(q=0;q<16;q+=2)		// try all the 8 tables. 
	{
		err=compressBlockWithTable4x2percep(img,width,height,startx,starty,avg_color,q,pixel_indices_MSB, pixel_indices_LSB);

		if(err<min_error)
		{
			min_error=err;
			best_pixel_indices_MSB[0] = pixel_indices_MSB[0];
			best_pixel_indices_LSB[0] = pixel_indices_LSB[0];
			best_table[0]=q >> 1;
		}
	}
	return (int) min_error;
}

//The below code quantizes a float RGB value to RGB444. 
//
//The format often allows a pixel to completely compensate an intensity error of the base
//color. Hence the closest RGB444 point may not be the best, and the code below uses
//this fact to find a better RGB444 color as the base color.
//
//(See the presentation http://www.jacobstrom.com/publications/PACKMAN.ppt for more info.) 
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void quantize444ColorCombined(float[] avg_col_in, int[] enc_color, byte[] avg_color)
{
	float dr, dg, db;
	float kr, kg, kb;
	float wR2, wG2, wB2;
	byte[] low_color= new byte[3];
	byte[] high_color= new byte[3];
	float min_error=255*255*8*3;
	float[] lowhightable= new float[8];
	int best_table=0;
	int best_index=0;
	int q;
	float kval = (float) (255.0/15.0);

	// These are the values that we want to have:
	float red_average, green_average, blue_average;

	int red_4bit_low, green_4bit_low, blue_4bit_low;
	int red_4bit_high, green_4bit_high, blue_4bit_high;
	
	// These are the values that we approximate with:
	int red_low, green_low, blue_low;
	int red_high, green_high, blue_high;

	red_average = avg_col_in[0];
	green_average = avg_col_in[1];
	blue_average = avg_col_in[2];

	// Find the 5-bit reconstruction levels red_low, red_high
	// so that red_average is in interval [red_low, red_high].
	// (The same with green and blue.)

	red_4bit_low = (int) (red_average/kval);
	green_4bit_low = (int) (green_average/kval);
	blue_4bit_low = (int) (blue_average/kval);

	red_4bit_high = CLAMP(0, red_4bit_low + 1, 15);
	green_4bit_high  = CLAMP(0, green_4bit_low + 1, 15);
	blue_4bit_high = CLAMP(0, blue_4bit_low + 1, 15);

	red_low   = (red_4bit_low << 4) | (red_4bit_low >> 0);
	green_low = (green_4bit_low << 4) | (green_4bit_low >> 0);
	blue_low = (blue_4bit_low << 4) | (blue_4bit_low >> 0);

	red_high   = (red_4bit_high << 4) | (red_4bit_high >> 0);
	green_high = (green_4bit_high << 4) | (green_4bit_high >> 0);
	blue_high = (blue_4bit_high << 4) | (blue_4bit_high >> 0);

	kr = (float)red_high - (float)red_low;
	kg = (float)green_high - (float)green_low;
	kb = (float)blue_high - (float)blue_low;

	// Note that dr, dg, and db are all negative.
	dr = red_low - red_average;
	dg = green_low - green_average;
	db = blue_low - blue_average;

	// Use straight (nonperceptive) weights.
	wR2 = (float) 1.0;
	wG2 = (float) 1.0;
	wB2 = (float) 1.0;

	lowhightable[0] = wR2*wG2*SQUARE( (dr+ 0) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+ 0) );
	lowhightable[1] = wR2*wG2*SQUARE( (dr+kr) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+kr) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+ 0) );
	lowhightable[2] = wR2*wG2*SQUARE( (dr+ 0) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+kg) - (db+ 0) );
	lowhightable[3] = wR2*wG2*SQUARE( (dr+ 0) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+kb) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+kb) );
	lowhightable[4] = wR2*wG2*SQUARE( (dr+kr) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+kr) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+kg) - (db+ 0) );
	lowhightable[5] = wR2*wG2*SQUARE( (dr+kr) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+kr) - (db+kb) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+kb) );
	lowhightable[6] = wR2*wG2*SQUARE( (dr+ 0) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+kb) ) + wG2*wB2*SQUARE( (dg+kg) - (db+kb) );
	lowhightable[7] = wR2*wG2*SQUARE( (dr+kr) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+kr) - (db+kb) ) + wG2*wB2*SQUARE( (dg+kg) - (db+kb) );

	float min_value = lowhightable[0];
	int min_index = 0;

	for(q = 1; q<8; q++)
	{
		if(lowhightable[q] < min_value)
		{
			min_value = lowhightable[q];
			min_index = q;
		}
	}

	float drh = red_high-red_average;
	float dgh = green_high-green_average;
	float dbh = blue_high-blue_average;

	low_color[0] = (byte)red_4bit_low;
	low_color[1] = (byte)green_4bit_low;
	low_color[2] = (byte)blue_4bit_low;

	high_color[0] = (byte)red_4bit_high;
	high_color[1] = (byte)green_4bit_high;
	high_color[2] = (byte)blue_4bit_high;

	switch(min_index)
	{
	case 0:
		// Since the step size is always 17 in RGB444 format (15*17=255),
		// kr = kg = kb = 17, which means that case 0 and case 7 will
		// always have equal projected error. Choose the one that is
		// closer to the desired color. 
		if(dr*dr + dg*dg + db*db > 3*8*8)
		{
			enc_color[0] = high_color[0]&0xff;
			enc_color[1] = high_color[1]&0xff;
			enc_color[2] = high_color[2]&0xff;
		}
		else
		{
			enc_color[0] = low_color[0]&0xff;
			enc_color[1] = low_color[1]&0xff;
			enc_color[2] = low_color[2]&0xff;
		}
		break;
	case 1:
		enc_color[0] = high_color[0]&0xff;
		enc_color[1] = low_color[1]&0xff;
		enc_color[2] = low_color[2]&0xff;
		break;
	case 2:	
		enc_color[0] = low_color[0]&0xff;
		enc_color[1] = high_color[1]&0xff;
		enc_color[2] = low_color[2]&0xff;
		break;
	case 3:	
		enc_color[0] = low_color[0]&0xff;
		enc_color[1] = low_color[1]&0xff;
		enc_color[2] = high_color[2]&0xff;
		break;
	case 4:	
		enc_color[0] = high_color[0]&0xff;
		enc_color[1] = high_color[1]&0xff;
		enc_color[2] = low_color[2]&0xff;
		break;
	case 5:	
		enc_color[0] = high_color[0]&0xff;
		enc_color[1] = low_color[1]&0xff;
		enc_color[2] = high_color[2]&0xff;
		break;
	case 6:	
		enc_color[0] = low_color[0]&0xff;
		enc_color[1] = high_color[1]&0xff;
		enc_color[2] = high_color[2]&0xff;
		break;
	case 7:	
		if(dr*dr + dg*dg + db*db > 3*8*8)
		{
			enc_color[0] = high_color[0]&0xff;
			enc_color[1] = high_color[1]&0xff;
			enc_color[2] = high_color[2]&0xff;
		}
		else
		{
			enc_color[0] = low_color[0]&0xff;
			enc_color[1] = low_color[1]&0xff;
			enc_color[2] = low_color[2]&0xff;
		}
		break;
	}
	// Expand 5-bit encoded color to 8-bit color
	avg_color[0] = (byte)((enc_color[0] << 3) | (enc_color[0] >> 2));
	avg_color[1] = (byte)((enc_color[1] << 3) | (enc_color[1] >> 2));
	avg_color[2] = (byte)((enc_color[2] << 3) | (enc_color[2] >> 2));	
}

//The below code quantizes a float RGB value to RGB555. 
//
//The format often allows a pixel to completely compensate an intensity error of the base
//color. Hence the closest RGB555 point may not be the best, and the code below uses
//this fact to find a better RGB555 color as the base color.
//
//(See the presentation http://www.jacobstrom.com/publications/PACKMAN.ppt for more info.) 
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void quantize555ColorCombined(float[] avg_col_in, int[] enc_color, float[] avg_color)
{
	float dr, dg, db;
	float kr, kg, kb;
	float wR2, wG2, wB2;
	byte[] low_color= new byte[3];
	byte[] high_color= new byte[3];
	float min_error=255*255*8*3;
	float[] lowhightable= new float[8];
	int best_table=0;
	int best_index=0;
	int q;
	float kval = (float) (255.0/31.0);

	// These are the values that we want to have:
	float red_average, green_average, blue_average;

	int red_5bit_low, green_5bit_low, blue_5bit_low;
	int red_5bit_high, green_5bit_high, blue_5bit_high;
	
	// These are the values that we approximate with:
	int red_low, green_low, blue_low;
	int red_high, green_high, blue_high;

	red_average = avg_col_in[0];
	green_average = avg_col_in[1];
	blue_average = avg_col_in[2];

	// Find the 5-bit reconstruction levels red_low, red_high
	// so that red_average is in interval [red_low, red_high].
	// (The same with green and blue.)

	red_5bit_low = (int) (red_average/kval);
	green_5bit_low = (int) (green_average/kval);
	blue_5bit_low = (int) (blue_average/kval);

	red_5bit_high = CLAMP(0, red_5bit_low + 1, 31);
	green_5bit_high  = CLAMP(0, green_5bit_low + 1, 31);
	blue_5bit_high = CLAMP(0, blue_5bit_low + 1, 31);

	red_low   = (red_5bit_low << 3) | (red_5bit_low >> 2);
	green_low = (green_5bit_low << 3) | (green_5bit_low >> 2);
	blue_low = (blue_5bit_low << 3) | (blue_5bit_low >> 2);

	red_high   = (red_5bit_high << 3) | (red_5bit_high >> 2);
	green_high = (green_5bit_high << 3) | (green_5bit_high >> 2);
	blue_high = (blue_5bit_high << 3) | (blue_5bit_high >> 2);

	kr = (float)red_high - (float)red_low;
	kg = (float)green_high - (float)green_low;
	kb = (float)blue_high - (float)blue_low;

	// Note that dr, dg, and db are all negative.
	dr = red_low - red_average;
	dg = green_low - green_average;
	db = blue_low - blue_average;

	// Use straight (nonperceptive) weights.
	wR2 = (float) 1.0;
	wG2 = (float) 1.0;
	wB2 = (float) 1.0;

	lowhightable[0] = wR2*wG2*SQUARE( (dr+ 0) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+ 0) );
	lowhightable[1] = wR2*wG2*SQUARE( (dr+kr) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+kr) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+ 0) );
	lowhightable[2] = wR2*wG2*SQUARE( (dr+ 0) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+kg) - (db+ 0) );
	lowhightable[3] = wR2*wG2*SQUARE( (dr+ 0) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+kb) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+kb) );
	lowhightable[4] = wR2*wG2*SQUARE( (dr+kr) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+kr) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+kg) - (db+ 0) );
	lowhightable[5] = wR2*wG2*SQUARE( (dr+kr) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+kr) - (db+kb) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+kb) );
	lowhightable[6] = wR2*wG2*SQUARE( (dr+ 0) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+kb) ) + wG2*wB2*SQUARE( (dg+kg) - (db+kb) );
	lowhightable[7] = wR2*wG2*SQUARE( (dr+kr) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+kr) - (db+kb) ) + wG2*wB2*SQUARE( (dg+kg) - (db+kb) );

	float	min_value = lowhightable[0];
	int min_index = 0;

	for(q = 1; q<8; q++)
	{
		if(lowhightable[q] < min_value)
		{
			min_value = lowhightable[q];
			min_index = q;
		}
	}

	float drh = red_high-red_average;
	float dgh = green_high-green_average;
	float dbh = blue_high-blue_average;

	low_color[0] = (byte)red_5bit_low;
	low_color[1] = (byte)green_5bit_low;
	low_color[2] = (byte)blue_5bit_low;

	high_color[0] = (byte)red_5bit_high;
	high_color[1] = (byte)green_5bit_high;
	high_color[2] = (byte)blue_5bit_high;

	switch(min_index)
	{
	case 0:
		enc_color[0] = low_color[0]&0xff;
		enc_color[1] = low_color[1]&0xff;
		enc_color[2] = low_color[2]&0xff;
		break;
	case 1:
		enc_color[0] = high_color[0]&0xff;
		enc_color[1] = low_color[1]&0xff;
		enc_color[2] = low_color[2]&0xff;
		break;
	case 2:	
		enc_color[0] = low_color[0]&0xff;
		enc_color[1] = high_color[1]&0xff;
		enc_color[2] = low_color[2]&0xff;
		break;
	case 3:	
		enc_color[0] = low_color[0]&0xff;
		enc_color[1] = low_color[1]&0xff;
		enc_color[2] = high_color[2]&0xff;
		break;
	case 4:	
		enc_color[0] = high_color[0]&0xff;
		enc_color[1] = high_color[1]&0xff;
		enc_color[2] = low_color[2]&0xff;
		break;
	case 5:	
		enc_color[0] = high_color[0]&0xff;
		enc_color[1] = low_color[1]&0xff;
		enc_color[2] = high_color[2]&0xff;
		break;
	case 6:	
		enc_color[0] = low_color[0]&0xff;
		enc_color[1] = high_color[1]&0xff;
		enc_color[2] = high_color[2]&0xff;
		break;
	case 7:	
		enc_color[0] = high_color[0]&0xff;
		enc_color[1] = high_color[1]&0xff;
		enc_color[2] = high_color[2]&0xff;
		break;
	}

	// Expand 5-bit encoded color to 8-bit color
	avg_color[0] = (byte)((enc_color[0] << 3) | (enc_color[0] >> 2));
	avg_color[1] = (byte)((enc_color[1] << 3) | (enc_color[1] >> 2));
	avg_color[2] = (byte)((enc_color[2] << 3) | (enc_color[2] >> 2));	
	
}

//The below code quantizes a float RGB value to RGB444. 
//
//The format often allows a pixel to completely compensate an intensity error of the base
//color. Hence the closest RGB444 point may not be the best, and the code below uses
//this fact to find a better RGB444 color as the base color.
//
//(See the presentation http://www.jacobstrom.com/publications/PACKMAN.ppt for more info.) 
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void quantize444ColorCombinedPerceptual(float[] avg_col_in, int[] enc_color, byte[] avg_color)
{
	float dr, dg, db;
	float kr, kg, kb;
	float wR2, wG2, wB2;
	byte[] low_color=new byte[3];
	byte[] high_color= new byte[3];
	float min_error=255*255*8*3;
	float[] lowhightable= new float[8];
	int best_table=0;
	int best_index=0;
	int q;
	float kval = (float) (255.0/15.0);

	// These are the values that we want to have:
	float red_average, green_average, blue_average;

	int red_4bit_low, green_4bit_low, blue_4bit_low;
	int red_4bit_high, green_4bit_high, blue_4bit_high;
	
	// These are the values that we approximate with:
	int red_low, green_low, blue_low;
	int red_high, green_high, blue_high;

	red_average = avg_col_in[0];
	green_average = avg_col_in[1];
	blue_average = avg_col_in[2];

	// Find the 5-bit reconstruction levels red_low, red_high
	// so that red_average is in interval [red_low, red_high].
	// (The same with green and blue.)

	red_4bit_low = (int) (red_average/kval);
	green_4bit_low = (int) (green_average/kval);
	blue_4bit_low = (int) (blue_average/kval);

	red_4bit_high = CLAMP(0, red_4bit_low + 1, 15);
	green_4bit_high  = CLAMP(0, green_4bit_low + 1, 15);
	blue_4bit_high = CLAMP(0, blue_4bit_low + 1, 15);

	red_low   = (red_4bit_low << 4) | (red_4bit_low >> 0);
	green_low = (green_4bit_low << 4) | (green_4bit_low >> 0);
	blue_low = (blue_4bit_low << 4) | (blue_4bit_low >> 0);

	red_high   = (red_4bit_high << 4) | (red_4bit_high >> 0);
	green_high = (green_4bit_high << 4) | (green_4bit_high >> 0);
	blue_high = (blue_4bit_high << 4) | (blue_4bit_high >> 0);

	low_color[0] = (byte)red_4bit_low;
	low_color[1] = (byte)green_4bit_low;
	low_color[2] = (byte)blue_4bit_low;

	high_color[0] = (byte)red_4bit_high;
	high_color[1] = (byte)green_4bit_high;
	high_color[2] = (byte)blue_4bit_high;

	kr = (float)red_high - (float)red_low;
	kg = (float)green_high - (float)green_low;
	kb = (float)blue_high- (float)blue_low;

	// Note that dr, dg, and db are all negative.
	dr = red_low - red_average;
	dg = green_low - green_average;
	db = blue_low - blue_average;

	// Perceptual weights to use
	wR2 = (float) PERCEPTUAL_WEIGHT_R_SQUARED; 
	wG2 = (float) PERCEPTUAL_WEIGHT_G_SQUARED; 
	wB2 = (float) PERCEPTUAL_WEIGHT_B_SQUARED;

	lowhightable[0] = wR2*wG2*SQUARE( (dr+ 0) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+ 0) );
	lowhightable[1] = wR2*wG2*SQUARE( (dr+kr) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+kr) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+ 0) );
	lowhightable[2] = wR2*wG2*SQUARE( (dr+ 0) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+kg) - (db+ 0) );
	lowhightable[3] = wR2*wG2*SQUARE( (dr+ 0) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+kb) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+kb) );
	lowhightable[4] = wR2*wG2*SQUARE( (dr+kr) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+kr) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+kg) - (db+ 0) );
	lowhightable[5] = wR2*wG2*SQUARE( (dr+kr) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+kr) - (db+kb) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+kb) );
	lowhightable[6] = wR2*wG2*SQUARE( (dr+ 0) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+kb) ) + wG2*wB2*SQUARE( (dg+kg) - (db+kb) );
	lowhightable[7] = wR2*wG2*SQUARE( (dr+kr) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+kr) - (db+kb) ) + wG2*wB2*SQUARE( (dg+kg) - (db+kb) );

	float min_value = lowhightable[0];
	int min_index = 0;

	for(q = 1; q<8; q++)
	{
		if(lowhightable[q] < min_value)
		{
			min_value = lowhightable[q];
			min_index = q;
		}
	}

	float drh = red_high-red_average;
	float dgh = green_high-green_average;
	float dbh = blue_high-blue_average;

	switch(min_index)
	{
	case 0:
		enc_color[0] = low_color[0]&0xff;
		enc_color[1] = low_color[1]&0xff;
		enc_color[2] = low_color[2]&0xff;
		break;
	case 1:
		enc_color[0] = high_color[0]&0xff;
		enc_color[1] = low_color[1]&0xff;
		enc_color[2] = low_color[2]&0xff;
		break;
	case 2:	
		enc_color[0] = low_color[0]&0xff;
		enc_color[1] = high_color[1]&0xff;
		enc_color[2] = low_color[2]&0xff;
		break;
	case 3:	
		enc_color[0] = low_color[0]&0xff;
		enc_color[1] = low_color[1]&0xff;
		enc_color[2] = high_color[2]&0xff;
		break;
	case 4:	
		enc_color[0] = high_color[0]&0xff;
		enc_color[1] = high_color[1]&0xff;
		enc_color[2] = low_color[2]&0xff;
		break;
	case 5:	
		enc_color[0] = high_color[0]&0xff;
		enc_color[1] = low_color[1]&0xff;
		enc_color[2] = high_color[2]&0xff;
		break;
	case 6:	
		enc_color[0] = low_color[0]&0xff;
		enc_color[1] = high_color[1]&0xff;
		enc_color[2] = high_color[2]&0xff;
		break;
	case 7:	
		enc_color[0] = high_color[0]&0xff;
		enc_color[1] = high_color[1]&0xff;
		enc_color[2] = high_color[2]&0xff;
		break;
	}

	// Expand encoded color to eight bits
	avg_color[0] = (byte)((enc_color[0] << 4) | enc_color[0]);
	avg_color[1] = (byte)((enc_color[1] << 4) | enc_color[1]);
	avg_color[2] = (byte)((enc_color[2] << 4) | enc_color[2]);
}

//The below code quantizes a float RGB value to RGB555. 
//
//The format often allows a pixel to completely compensate an intensity error of the base
//color. Hence the closest RGB555 point may not be the best, and the code below uses
//this fact to find a better RGB555 color as the base color.
//
//(See the presentation http://www.jacobstrom.com/publications/PACKMAN.ppt for more info.) 
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void quantize555ColorCombinedPerceptual(float[] avg_col_in, int[] enc_color, byte[] avg_color)
{
	float dr, dg, db;
	float kr, kg, kb;
	float wR2, wG2, wB2;
	byte[] low_color= new byte[3];
	byte[] high_color= new byte[3];
	float min_error=255*255*8*3;
	float[] lowhightable= new float[8];
	int best_table=0;
	int best_index=0;
	int q;
	float kval = (float) (255.0/31.0);

	// These are the values that we want to have:
	float red_average, green_average, blue_average;

	int red_5bit_low, green_5bit_low, blue_5bit_low;
	int red_5bit_high, green_5bit_high, blue_5bit_high;
	
	// These are the values that we approximate with:
	int red_low, green_low, blue_low;
	int red_high, green_high, blue_high;

	red_average = avg_col_in[0];
	green_average = avg_col_in[1];
	blue_average = avg_col_in[2];

	// Find the 5-bit reconstruction levels red_low, red_high
	// so that red_average is in interval [red_low, red_high].
	// (The same with green and blue.)

	red_5bit_low = (int) (red_average/kval);
	green_5bit_low = (int) (green_average/kval);
	blue_5bit_low = (int) (blue_average/kval);

	red_5bit_high = CLAMP(0, red_5bit_low + 1, 31);
	green_5bit_high  = CLAMP(0, green_5bit_low + 1, 31);
	blue_5bit_high = CLAMP(0, blue_5bit_low + 1, 31);

	red_low   = (red_5bit_low << 3) | (red_5bit_low >> 2);
	green_low = (green_5bit_low << 3) | (green_5bit_low >> 2);
	blue_low = (blue_5bit_low << 3) | (blue_5bit_low >> 2);

	red_high   = (red_5bit_high << 3) | (red_5bit_high >> 2);
	green_high = (green_5bit_high << 3) | (green_5bit_high >> 2);
	blue_high = (blue_5bit_high << 3) | (blue_5bit_high >> 2);

	low_color[0] = (byte)red_5bit_low;
	low_color[1] = (byte)green_5bit_low;
	low_color[2] = (byte)blue_5bit_low;

	high_color[0] = (byte)red_5bit_high;
	high_color[1] = (byte)green_5bit_high;
	high_color[2] = (byte)blue_5bit_high;

	kr = (float)red_high - (float)red_low;
	kg = (float)green_high - (float)green_low;
	kb = (float)blue_high - (float)blue_low;

	// Note that dr, dg, and db are all negative.
	dr = red_low - red_average;
	dg = green_low - green_average;
	db = blue_low - blue_average;

	// Perceptual weights to use
	wR2 = (float) PERCEPTUAL_WEIGHT_R_SQUARED; 
	wG2 = (float) PERCEPTUAL_WEIGHT_G_SQUARED; 
	wB2 = (float) PERCEPTUAL_WEIGHT_B_SQUARED;

	lowhightable[0] = wR2*wG2*SQUARE( (dr+ 0) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+ 0) );
	lowhightable[1] = wR2*wG2*SQUARE( (dr+kr) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+kr) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+ 0) );
	lowhightable[2] = wR2*wG2*SQUARE( (dr+ 0) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+kg) - (db+ 0) );
	lowhightable[3] = wR2*wG2*SQUARE( (dr+ 0) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+kb) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+kb) );
	lowhightable[4] = wR2*wG2*SQUARE( (dr+kr) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+kr) - (db+ 0) ) + wG2*wB2*SQUARE( (dg+kg) - (db+ 0) );
	lowhightable[5] = wR2*wG2*SQUARE( (dr+kr) - (dg+ 0) ) + wR2*wB2*SQUARE( (dr+kr) - (db+kb) ) + wG2*wB2*SQUARE( (dg+ 0) - (db+kb) );
	lowhightable[6] = wR2*wG2*SQUARE( (dr+ 0) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+ 0) - (db+kb) ) + wG2*wB2*SQUARE( (dg+kg) - (db+kb) );
	lowhightable[7] = wR2*wG2*SQUARE( (dr+kr) - (dg+kg) ) + wR2*wB2*SQUARE( (dr+kr) - (db+kb) ) + wG2*wB2*SQUARE( (dg+kg) - (db+kb) );

	float min_value = lowhightable[0];
	int min_index = 0;

	for(q = 1; q<8; q++)
	{
		if(lowhightable[q] < min_value)
		{
			min_value = lowhightable[q];
			min_index = q;
		}
	}

	float drh = red_high-red_average;
	float dgh = green_high-green_average;
	float dbh = blue_high-blue_average;

	switch(min_index)
	{
	case 0:
		enc_color[0] = low_color[0]&0xff;
		enc_color[1] = low_color[1]&0xff;
		enc_color[2] = low_color[2]&0xff;
		break;
	case 1:
		enc_color[0] = high_color[0]&0xff;
		enc_color[1] = low_color[1]&0xff;
		enc_color[2] = low_color[2]&0xff;
		break;
	case 2:	
		enc_color[0] = low_color[0]&0xff;
		enc_color[1] = high_color[1]&0xff;
		enc_color[2] = low_color[2]&0xff;
		break;
	case 3:	
		enc_color[0] = low_color[0]&0xff;
		enc_color[1] = low_color[1]&0xff;
		enc_color[2] = high_color[2]&0xff;
		break;
	case 4:	
		enc_color[0] = high_color[0]&0xff;
		enc_color[1] = high_color[1]&0xff;
		enc_color[2] = low_color[2]&0xff;
		break;
	case 5:	
		enc_color[0] = high_color[0]&0xff;
		enc_color[1] = low_color[1]&0xff;
		enc_color[2] = high_color[2]&0xff;
		break;
	case 6:	
		enc_color[0] = low_color[0]&0xff;
		enc_color[1] = high_color[1]&0xff;
		enc_color[2] = high_color[2]&0xff;
		break;
	case 7:	
		enc_color[0] = high_color[0]&0xff;
		enc_color[1] = high_color[1]&0xff;
		enc_color[2] = high_color[2]&0xff;
		break;

	}

	// Expand 5-bit encoded color to 8-bit color
	avg_color[0] = (byte)((enc_color[0] << 3) | (enc_color[0] >> 2));
	avg_color[1] = (byte)((enc_color[1] << 3) | (enc_color[1] >> 2));
	avg_color[2] = (byte)((enc_color[2] << 3) | (enc_color[2] >> 2));
}

//Compresses the block using only the individual mode in ETC1/ETC2 using the average color as the base color.
//Uses a perceptual error metric.
//Uses fixed point arithmetics where 1000 equals 1.0
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &compressed1,  int &compressed2, int &best_flip,  int &best_err_upper,  int &best_err_lower,  int &best_err_left,  int &best_err_right
static int compressBlockOnlyIndividualAveragePerceptual1000(byte[] img,int width,int height,int startx,int starty,  int[] compressed1,  int[] compressed2, int[] best_enc_color1, int[] best_enc_color2, int[] best_flip,  int[] best_err_upper,  int[] best_err_lower,  int[] best_err_left,  int[] best_err_right, int[] best_color_upper, int[] best_color_lower, int[] best_color_left, int[] best_color_right)
{
	int[] compressed1_norm= new int[1], compressed2_norm= new int[1];
	int[] compressed1_flip= new int[1], compressed2_flip= new int[1];
	byte[] avg_color_quant1=new byte[3], avg_color_quant2=new byte[3];

	float[] avg_color_float1= new float[3],avg_color_float2= new float[3];
	int[] enc_color1= new int[3], enc_color2= new int[3];
	int best_table_indices1=0, best_table_indices2=0;
	int[] best_table1=new int[1], best_table2=new int[1];
	int diffbit;

	int norm_err=0;
	int flip_err=0;
	int best_err;

	// First try normal blocks 2x4:

	computeAverageColor2x4noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor2x4noQuantFloat(img,width,height,startx+2,starty,avg_color_float2);

	enc_color1[0] = (int)( JAS_ROUND(15.0*avg_color_float1[0]/255.0) );
	enc_color1[1] = (int)( JAS_ROUND(15.0*avg_color_float1[1]/255.0) );
	enc_color1[2] = (int)( JAS_ROUND(15.0*avg_color_float1[2]/255.0) );
	enc_color2[0] = (int)( JAS_ROUND(15.0*avg_color_float2[0]/255.0) );
	enc_color2[1] = (int)( JAS_ROUND(15.0*avg_color_float2[1]/255.0) );
	enc_color2[2] = (int)( JAS_ROUND(15.0*avg_color_float2[2]/255.0) );

	diffbit = 0;

	avg_color_quant1[0] = (byte)(enc_color1[0] << 4 | (enc_color1[0] ));
	avg_color_quant1[1] = (byte)(enc_color1[1] << 4 | (enc_color1[1] ));
	avg_color_quant1[2] = (byte)(enc_color1[2] << 4 | (enc_color1[2] ));
	avg_color_quant2[0] = (byte)(enc_color2[0] << 4 | (enc_color2[0] ));
	avg_color_quant2[1] = (byte)(enc_color2[1] << 4 | (enc_color2[1] ));
	avg_color_quant2[2] = (byte)(enc_color2[2] << 4 | (enc_color2[2] ));

	// Pack bits into the first word. 

	//     ETC1_RGB8_OES:
	// 
	//     a) bit layout in bits 63 through 32 if diffbit = 0
	// 
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
	//      ---------------------------------------------------------------------------------------------------
	//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
	//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
	//      ---------------------------------------------------------------------------------------------------
	//     
	//     b) bit layout in bits 63 through 32 if diffbit = 1
	// 
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
	//      ---------------------------------------------------------------------------------------------------
	//     | base col1    | dcol 2 | base col1    | dcol 2 | base col 1   | dcol 2 | table  | table  |diff|flip|
	//     | R1' (5 bits) | dR2    | G1' (5 bits) | dG2    | B1' (5 bits) | dB2    | cw 1   | cw 2   |bit |bit |
	//      ---------------------------------------------------------------------------------------------------
	// 
	//     c) bit layout in bits 31 through 0 (in both cases)
	// 
	//      31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3   2   1  0
	//      --------------------------------------------------------------------------------------------------
	//     |       most significant pixel index bits       |         least significant pixel index bits       |  
	//     | p| o| n| m| l| k| j| i| h| g| f| e| d| c| b| a| p| o| n| m| l| k| j| i| h| g| f| e| d| c | b | a |
	//      --------------------------------------------------------------------------------------------------      

	compressed1_norm[0] = 0;
	PUTBITSHIGH( compressed1_norm, diffbit,       1, 33);
	PUTBITSHIGH( compressed1_norm, enc_color1[0], 4, 63);
	PUTBITSHIGH( compressed1_norm, enc_color1[1], 4, 55);
	PUTBITSHIGH( compressed1_norm, enc_color1[2], 4, 47);
	PUTBITSHIGH( compressed1_norm, enc_color2[0], 4, 59);
	PUTBITSHIGH( compressed1_norm, enc_color2[1], 4, 51);
	PUTBITSHIGH( compressed1_norm, enc_color2[1], 4, 43);

	int[] best_pixel_indices1_MSB=new int[1];
	int[] best_pixel_indices1_LSB=new int[1];
	int[] best_pixel_indices2_MSB=new int[1];
	int[] best_pixel_indices2_LSB=new int[1];

	best_enc_color1[0] = enc_color1[0];
	best_enc_color1[1] = enc_color1[1];
	best_enc_color1[2] = enc_color1[2];
	best_enc_color2[0] = enc_color2[0];
	best_enc_color2[1] = enc_color2[1];
	best_enc_color2[2] = enc_color2[2];
	
	best_color_left[0] = enc_color1[0];
	best_color_left[1] = enc_color1[1];
	best_color_left[2] = enc_color1[2];
	best_color_right[0] = enc_color2[0];
	best_color_right[1] = enc_color2[1];
	best_color_right[2] = enc_color2[2];

	norm_err = 0;

	// left part of block
	best_err_left[0] = tryalltables_3bittable2x4percep1000(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);
	norm_err = best_err_left[0];

	// right part of block
	best_err_right[0] = tryalltables_3bittable2x4percep1000(img,width,height,startx+2,starty,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);
	norm_err += best_err_right[0];

	PUTBITSHIGH( compressed1_norm, best_table1[0],   3, 39);
	PUTBITSHIGH( compressed1_norm, best_table2[0],   3, 36);
	PUTBITSHIGH( compressed1_norm,           0,   1, 32);

	compressed2_norm[0] = 0;
	PUTBITS( compressed2_norm, (best_pixel_indices1_MSB[0]     ), 8, 23);
	PUTBITS( compressed2_norm, (best_pixel_indices2_MSB[0]     ), 8, 31);
	PUTBITS( compressed2_norm, (best_pixel_indices1_LSB[0]     ), 8, 7);
	PUTBITS( compressed2_norm, (best_pixel_indices2_LSB[0]     ), 8, 15);

	// Now try flipped blocks 4x2:

	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty+2,avg_color_float2);

	// First test if avg_color1 is similar enough to avg_color2 so that
	// we can use differential coding of colors. 

	enc_color1[0] = (int)( JAS_ROUND(15.0*avg_color_float1[0]/255.0) );
	enc_color1[1] = (int)( JAS_ROUND(15.0*avg_color_float1[1]/255.0) );
	enc_color1[2] = (int)( JAS_ROUND(15.0*avg_color_float1[2]/255.0) );
	enc_color2[0] = (int)( JAS_ROUND(15.0*avg_color_float2[0]/255.0) );
	enc_color2[1] = (int)( JAS_ROUND(15.0*avg_color_float2[1]/255.0) );
	enc_color2[2] = (int)( JAS_ROUND(15.0*avg_color_float2[2]/255.0) );

	best_color_upper[0] = enc_color1[0];
	best_color_upper[1] = enc_color1[1];
	best_color_upper[2] = enc_color1[2];
	best_color_lower[0] = enc_color2[0];
	best_color_lower[1] = enc_color2[1];
	best_color_lower[2] = enc_color2[2];

	diffbit = 0;

	avg_color_quant1[0] = (byte)(enc_color1[0] << 4 | (enc_color1[0] ));
	avg_color_quant1[1] = (byte)(enc_color1[1] << 4 | (enc_color1[1] ));
	avg_color_quant1[2] = (byte)(enc_color1[2] << 4 | (enc_color1[2] ));
	avg_color_quant2[0] = (byte)(enc_color2[0] << 4 | (enc_color2[0] ));
	avg_color_quant2[1] = (byte)(enc_color2[1] << 4 | (enc_color2[1] ));
	avg_color_quant2[2] = (byte)(enc_color2[2] << 4 | (enc_color2[2] ));

	// Pack bits into the first word. 

	compressed1_flip[0] = 0;
	PUTBITSHIGH( compressed1_flip, diffbit,       1, 33);
	PUTBITSHIGH( compressed1_flip, enc_color1[0], 4, 63);
	PUTBITSHIGH( compressed1_flip, enc_color1[1], 4, 55);
	PUTBITSHIGH( compressed1_flip, enc_color1[2], 4, 47);
	PUTBITSHIGH( compressed1_flip, enc_color2[0], 4, 49);
	PUTBITSHIGH( compressed1_flip, enc_color2[1], 4, 51);
	PUTBITSHIGH( compressed1_flip, enc_color2[2], 4, 43);

	// upper part of block
	best_err_upper[0] = tryalltables_3bittable4x2percep1000(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);
	flip_err = best_err_upper[0];
	// lower part of block
	best_err_lower[0] = tryalltables_3bittable4x2percep1000(img,width,height,startx,starty+2,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);
	flip_err += best_err_lower[0];

	PUTBITSHIGH( compressed1_flip, best_table1[0],   3, 39);
	PUTBITSHIGH( compressed1_flip, best_table2[0],   3, 36);
	PUTBITSHIGH( compressed1_flip,           1,   1, 32);

	best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
	best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);
	
	compressed2_flip[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16) | (best_pixel_indices1_LSB[0] & 0xffff);

	// Now lets see which is the best table to use. Only 8 tables are possible. 

	if(norm_err <= flip_err)
	{
		compressed1[0] = compressed1_norm[0] | 0;
		compressed2[0] = compressed2_norm[0];
		best_err = norm_err;
		best_flip[0] = 0;
	}
	else
	{
		compressed1[0] = compressed1_flip[0] | 1;
		compressed2[0] = compressed2_flip[0];
		best_err = flip_err;
		best_enc_color1[0] = enc_color1[0];
		best_enc_color1[1] = enc_color1[1];
		best_enc_color1[2] = enc_color1[2];
		best_enc_color2[0] = enc_color2[0];
		best_enc_color2[1] = enc_color2[1];
		best_enc_color2[2] = enc_color2[2];
		best_flip[0] = 1;
	}
	return best_err;
}

//Compresses the block using only the individual mode in ETC1/ETC2 using the average color as the base color.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &compressed1,  int &compressed2, int &best_flip,  int &best_err_upper,  int &best_err_lower,  int &best_err_left,  int &best_err_right
static int compressBlockOnlyIndividualAverage(byte[] img,int width,int height,int startx,int starty,  int[] compressed1,  int[] compressed2, int[] best_enc_color1, int[] best_enc_color2, int[] best_flip,  int[] best_err_upper,  int[] best_err_lower,  int[] best_err_left,  int[] best_err_right, int[] best_color_upper, int[] best_color_lower, int[] best_color_left, int[] best_color_right)
{
	int[] compressed1_norm= new int[1], compressed2_norm= new int[1];
	int[] compressed1_flip= new int[1], compressed2_flip= new int[1];
	byte[] avg_color_quant1= new byte[3], avg_color_quant2= new byte[3];

	float[] avg_color_float1= new float[3],avg_color_float2= new float[3];
	int[] enc_color1= new int[3], enc_color2= new int[3];
	int min_error=255*255*8*3;
	int best_table_indices1=0, best_table_indices2=0;
	int[] best_table1=new int[1], best_table2=new int[1];
	int diffbit;

	int norm_err=0;
	int flip_err=0;
	int best_err;

	// First try normal blocks 2x4:

	computeAverageColor2x4noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor2x4noQuantFloat(img,width,height,startx+2,starty,avg_color_float2);

	enc_color1[0] = (int)( JAS_ROUND(15.0*avg_color_float1[0]/255.0) );
	enc_color1[1] = (int)( JAS_ROUND(15.0*avg_color_float1[1]/255.0) );
	enc_color1[2] = (int)( JAS_ROUND(15.0*avg_color_float1[2]/255.0) );
	enc_color2[0] = (int)( JAS_ROUND(15.0*avg_color_float2[0]/255.0) );
	enc_color2[1] = (int)( JAS_ROUND(15.0*avg_color_float2[1]/255.0) );
	enc_color2[2] = (int)( JAS_ROUND(15.0*avg_color_float2[2]/255.0) );

	diffbit = 0;

	avg_color_quant1[0] = (byte)(enc_color1[0] << 4 | (enc_color1[0] ));
	avg_color_quant1[1] = (byte)(enc_color1[1] << 4 | (enc_color1[1] ));
	avg_color_quant1[2] = (byte)(enc_color1[2] << 4 | (enc_color1[2] ));
	avg_color_quant2[0] = (byte)(enc_color2[0] << 4 | (enc_color2[0] ));
	avg_color_quant2[1] = (byte)(enc_color2[1] << 4 | (enc_color2[1] ));
	avg_color_quant2[2] = (byte)(enc_color2[2] << 4 | (enc_color2[2] ));

	// Pack bits into the first word. 

	//     ETC1_RGB8_OES:
	// 
	//     a) bit layout in bits 63 through 32 if diffbit = 0
	// 
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
	//      ---------------------------------------------------------------------------------------------------
	//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
	//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
	//      ---------------------------------------------------------------------------------------------------
	//     
	//     b) bit layout in bits 63 through 32 if diffbit = 1
	// 
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
	//      ---------------------------------------------------------------------------------------------------
	//     | base col1    | dcol 2 | base col1    | dcol 2 | base col 1   | dcol 2 | table  | table  |diff|flip|
	//     | R1' (5 bits) | dR2    | G1' (5 bits) | dG2    | B1' (5 bits) | dB2    | cw 1   | cw 2   |bit |bit |
	//      ---------------------------------------------------------------------------------------------------
	// 
	//     c) bit layout in bits 31 through 0 (in both cases)
	// 
	//      31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3   2   1  0
	//      --------------------------------------------------------------------------------------------------
	//     |       most significant pixel index bits       |         least significant pixel index bits       |  
	//     | p| o| n| m| l| k| j| i| h| g| f| e| d| c| b| a| p| o| n| m| l| k| j| i| h| g| f| e| d| c | b | a |
	//      --------------------------------------------------------------------------------------------------      

	compressed1_norm[0] = 0;
	PUTBITSHIGH( compressed1_norm, diffbit,       1, 33);
	PUTBITSHIGH( compressed1_norm, enc_color1[0], 4, 63);
	PUTBITSHIGH( compressed1_norm, enc_color1[1], 4, 55);
	PUTBITSHIGH( compressed1_norm, enc_color1[2], 4, 47);
	PUTBITSHIGH( compressed1_norm, enc_color2[0], 4, 59);
	PUTBITSHIGH( compressed1_norm, enc_color2[1], 4, 51);
	PUTBITSHIGH( compressed1_norm, enc_color2[1], 4, 43);

	int[] best_pixel_indices1_MSB=new int[1];
	int[] best_pixel_indices1_LSB=new int[1];
	int[] best_pixel_indices2_MSB=new int[1];
	int[] best_pixel_indices2_LSB=new int[1];

	best_enc_color1[0] = enc_color1[0];
	best_enc_color1[1] = enc_color1[1];
	best_enc_color1[2] = enc_color1[2];
	best_enc_color2[0] = enc_color2[0];
	best_enc_color2[1] = enc_color2[1];
	best_enc_color2[2] = enc_color2[2];
	best_color_left[0] = enc_color1[0];
	best_color_left[1] = enc_color1[1];
	best_color_left[2] = enc_color1[2];
	best_color_right[0] = enc_color2[0];
	best_color_right[1] = enc_color2[1];
	best_color_right[2] = enc_color2[2];

	norm_err = 0;

	// left part of block
	best_err_left[0] = tryalltables_3bittable2x4(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);
	norm_err = best_err_left[0];

	// right part of block
	best_err_right[0] = tryalltables_3bittable2x4(img,width,height,startx+2,starty,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);
	norm_err += best_err_right[0];

	PUTBITSHIGH( compressed1_norm, best_table1[0],   3, 39);
	PUTBITSHIGH( compressed1_norm, best_table2[0],   3, 36);
	PUTBITSHIGH( compressed1_norm,           0,   1, 32);

	compressed2_norm[0] = 0;
	PUTBITS( compressed2_norm, (best_pixel_indices1_MSB[0]     ), 8, 23);
	PUTBITS( compressed2_norm, (best_pixel_indices2_MSB[0]     ), 8, 31);
	PUTBITS( compressed2_norm, (best_pixel_indices1_LSB[0]     ), 8, 7);
	PUTBITS( compressed2_norm, (best_pixel_indices2_LSB[0]     ), 8, 15);


	// Now try flipped blocks 4x2:

	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty+2,avg_color_float2);

	// First test if avg_color1 is similar enough to avg_color2 so that
	// we can use differential coding of colors. 

	enc_color1[0] = (int)( JAS_ROUND(15.0*avg_color_float1[0]/255.0) );
	enc_color1[1] = (int)( JAS_ROUND(15.0*avg_color_float1[1]/255.0) );
	enc_color1[2] = (int)( JAS_ROUND(15.0*avg_color_float1[2]/255.0) );
	enc_color2[0] = (int)( JAS_ROUND(15.0*avg_color_float2[0]/255.0) );
	enc_color2[1] = (int)( JAS_ROUND(15.0*avg_color_float2[1]/255.0) );
	enc_color2[2] = (int)( JAS_ROUND(15.0*avg_color_float2[2]/255.0) );

	best_color_upper[0] = enc_color1[0];
	best_color_upper[1] = enc_color1[1];
	best_color_upper[2] = enc_color1[2];
	best_color_lower[0] = enc_color2[0];
	best_color_lower[1] = enc_color2[1];
	best_color_lower[2] = enc_color2[2];

	diffbit = 0;

	avg_color_quant1[0] = (byte)(enc_color1[0] << 4 | (enc_color1[0] ));
	avg_color_quant1[1] = (byte)(enc_color1[1] << 4 | (enc_color1[1] ));
	avg_color_quant1[2] = (byte)(enc_color1[2] << 4 | (enc_color1[2] ));
	avg_color_quant2[0] = (byte)(enc_color2[0] << 4 | (enc_color2[0] ));
	avg_color_quant2[1] = (byte)(enc_color2[1] << 4 | (enc_color2[1] ));
	avg_color_quant2[2] = (byte)(enc_color2[2] << 4 | (enc_color2[2] ));

	// Pack bits into the first word. 

	compressed1_flip[0] = 0;
	PUTBITSHIGH( compressed1_flip, diffbit,       1, 33);
	PUTBITSHIGH( compressed1_flip, enc_color1[0], 4, 63);
	PUTBITSHIGH( compressed1_flip, enc_color1[1], 4, 55);
	PUTBITSHIGH( compressed1_flip, enc_color1[2], 4, 47);
	PUTBITSHIGH( compressed1_flip, enc_color2[0], 4, 49);
	PUTBITSHIGH( compressed1_flip, enc_color2[1], 4, 51);
	PUTBITSHIGH( compressed1_flip, enc_color2[2], 4, 43);

	// upper part of block
	best_err_upper[0] = tryalltables_3bittable4x2(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);
	flip_err = best_err_upper[0];
	// lower part of block
	best_err_lower[0] = tryalltables_3bittable4x2(img,width,height,startx,starty+2,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);
	flip_err += best_err_lower[0];

	PUTBITSHIGH( compressed1_flip, best_table1[0],   3, 39);
	PUTBITSHIGH( compressed1_flip, best_table2[0],   3, 36);
	PUTBITSHIGH( compressed1_flip,           1,   1, 32);

	best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
	best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);
	
	compressed2_flip[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16) | (best_pixel_indices1_LSB[0] & 0xffff);

	// Now lets see which is the best table to use. Only 8 tables are possible. 

	if(norm_err <= flip_err)
	{
		compressed1[0] = compressed1_norm[0] | 0;
		compressed2[0] = compressed2_norm[0];
		best_err = norm_err;
		best_flip[0] = 0;
	}
	else
	{
		compressed1[0] = compressed1_flip[0] | 1;
		compressed2[0] = compressed2_flip[0];
		best_err = flip_err;
		best_enc_color1[0] = enc_color1[0];
		best_enc_color1[1] = enc_color1[1];
		best_enc_color1[2] = enc_color1[2];
		best_enc_color2[0] = enc_color2[0];
		best_enc_color2[1] = enc_color2[1];
		best_enc_color2[2] = enc_color2[2];
		best_flip[0] = 1;
	}
	return best_err;
}

//Compresses the block using either the individual or differential mode in ETC1/ETC2
//Uses the average color as the base color in each half-block.
//Tries both flipped and unflipped.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &compressed1, int &compressed2
static void compressBlockDiffFlipAverage(byte[] img,int width,int height,int startx,int starty, int[] compressed1, int[] compressed2)
{
	int[] compressed1_norm= new int[1], compressed2_norm= new int[1];
	int[] compressed1_flip= new int[1], compressed2_flip= new int[1];
	byte[] avg_color_quant1= new byte[3], avg_color_quant2= new byte[3];

	float[] avg_color_float1= new float[3],avg_color_float2= new float[3];
	int[] enc_color1= new int[3], enc_color2= new int[3], diff= new int[3];
	int min_error=255*255*8*3;
	int best_table_indices1=0, best_table_indices2=0;
	int[] best_table1=new int[1], best_table2=new int[1];
	int diffbit;

	int norm_err=0;
	int flip_err=0;

	// First try normal blocks 2x4:
	computeAverageColor2x4noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor2x4noQuantFloat(img,width,height,startx+2,starty,avg_color_float2);

	// First test if avg_color1 is similar enough to avg_color2 so that
	// we can use differential coding of colors. 

	float eps;

	enc_color1[0] = (int)( JAS_ROUND(31.0*avg_color_float1[0]/255.0) );
	enc_color1[1] = (int)( JAS_ROUND(31.0*avg_color_float1[1]/255.0) );
	enc_color1[2] = (int)( JAS_ROUND(31.0*avg_color_float1[2]/255.0) );
	enc_color2[0] = (int)( JAS_ROUND(31.0*avg_color_float2[0]/255.0) );
	enc_color2[1] = (int)( JAS_ROUND(31.0*avg_color_float2[1]/255.0) );
	enc_color2[2] = (int)( JAS_ROUND(31.0*avg_color_float2[2]/255.0) );

	diff[0] = enc_color2[0]-enc_color1[0];	
	diff[1] = enc_color2[1]-enc_color1[1];	
	diff[2] = enc_color2[2]-enc_color1[2];

	if( (diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4) && (diff[2] <= 3) )
	{
		diffbit = 1;

		// The difference to be coded:

		diff[0] = enc_color2[0]-enc_color1[0];	
		diff[1] = enc_color2[1]-enc_color1[1];	
		diff[2] = enc_color2[2]-enc_color1[2];

		avg_color_quant1[0] = (byte)(enc_color1[0] << 3 | (enc_color1[0] >> 2));
		avg_color_quant1[1] = (byte)(enc_color1[1] << 3 | (enc_color1[1] >> 2));
		avg_color_quant1[2] = (byte)(enc_color1[2] << 3 | (enc_color1[2] >> 2));
		avg_color_quant2[0] = (byte)(enc_color2[0] << 3 | (enc_color2[0] >> 2));
		avg_color_quant2[1] = (byte)(enc_color2[1] << 3 | (enc_color2[1] >> 2));
		avg_color_quant2[2] = (byte)(enc_color2[2] << 3 | (enc_color2[2] >> 2));

		// Pack bits into the first word. 

		//     ETC1_RGB8_OES:
		// 
		//     a) bit layout in bits 63 through 32 if diffbit = 0
		// 
		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
		//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------
		//     
		//     b) bit layout in bits 63 through 32 if diffbit = 1
		// 
		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1    | dcol 2 | base col1    | dcol 2 | base col 1   | dcol 2 | table  | table  |diff|flip|
		//     | R1' (5 bits) | dR2    | G1' (5 bits) | dG2    | B1' (5 bits) | dB2    | cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------
		// 
		//     c) bit layout in bits 31 through 0 (in both cases)
		// 
		//      31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3   2   1  0
		//      --------------------------------------------------------------------------------------------------
		//     |       most significant pixel index bits       |         least significant pixel index bits       |  
		//     | p| o| n| m| l| k| j| i| h| g| f| e| d| c| b| a| p| o| n| m| l| k| j| i| h| g| f| e| d| c | b | a |
		//      --------------------------------------------------------------------------------------------------      

		compressed1_norm[0] = 0;
		PUTBITSHIGH( compressed1_norm, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_norm, enc_color1[0], 5, 63);
		PUTBITSHIGH( compressed1_norm, enc_color1[1], 5, 55);
		PUTBITSHIGH( compressed1_norm, enc_color1[2], 5, 47);
		PUTBITSHIGH( compressed1_norm, diff[0],       3, 58);
		PUTBITSHIGH( compressed1_norm, diff[1],       3, 50);
		PUTBITSHIGH( compressed1_norm, diff[2],       3, 42);

		int[] best_pixel_indices1_MSB= new int[1];
		int[] best_pixel_indices1_LSB= new int[1];
		int[] best_pixel_indices2_MSB= new int[1];
		int[] best_pixel_indices2_LSB= new int[1];

		norm_err = 0;

		// left part of block
		norm_err = tryalltables_3bittable2x4(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);

		// right part of block
		norm_err += tryalltables_3bittable2x4(img,width,height,startx+2,starty,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_norm, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_norm, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_norm,           0,   1, 32);

		compressed2_norm[0] = 0;
		PUTBITS( compressed2_norm, (best_pixel_indices1_MSB[0]     ), 8, 23);
		PUTBITS( compressed2_norm, (best_pixel_indices2_MSB[0]     ), 8, 31);
		PUTBITS( compressed2_norm, (best_pixel_indices1_LSB[0]     ), 8, 7);
		PUTBITS( compressed2_norm, (best_pixel_indices2_LSB[0]     ), 8, 15);
	}
	else
	{
		diffbit = 0;
		// The difference is bigger than what fits in 555 plus delta-333, so we will have
		// to deal with 444 444.

		eps = (float) 0.0001;

		enc_color1[0] = (int)( ((float) avg_color_float1[0] / (17.0)) +0.5 + eps);
		enc_color1[1] = (int)( ((float) avg_color_float1[1] / (17.0)) +0.5 + eps);
		enc_color1[2] = (int)( ((float) avg_color_float1[2] / (17.0)) +0.5 + eps);
		enc_color2[0] = (int)( ((float) avg_color_float2[0] / (17.0)) +0.5 + eps);
		enc_color2[1] = (int)( ((float) avg_color_float2[1] / (17.0)) +0.5 + eps);
		enc_color2[2] = (int)( ((float) avg_color_float2[2] / (17.0)) +0.5 + eps);
		
		avg_color_quant1[0] = (byte)(enc_color1[0] << 4 | enc_color1[0]); 
		avg_color_quant1[1] = (byte)(enc_color1[1] << 4 | enc_color1[1]); 
		avg_color_quant1[2] = (byte)(enc_color1[2] << 4 | enc_color1[2]);
		avg_color_quant2[0] = (byte)(enc_color2[0] << 4 | enc_color2[0]); 
		avg_color_quant2[1] = (byte)(enc_color2[1] << 4 | enc_color2[1]); 
		avg_color_quant2[2] = (byte)(enc_color2[2] << 4 | enc_color2[2]);

		// Pack bits into the first word. 

		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
		//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------

		compressed1_norm[0] = 0;
		PUTBITSHIGH( compressed1_norm, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_norm, enc_color1[0], 4, 63);
		PUTBITSHIGH( compressed1_norm, enc_color1[1], 4, 55);
		PUTBITSHIGH( compressed1_norm, enc_color1[2], 4, 47);
		PUTBITSHIGH( compressed1_norm, enc_color2[0], 4, 59);
		PUTBITSHIGH( compressed1_norm, enc_color2[1], 4, 51);
		PUTBITSHIGH( compressed1_norm, enc_color2[2], 4, 43);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];
		
		// left part of block
		norm_err = tryalltables_3bittable2x4(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);

		// right part of block
		norm_err += tryalltables_3bittable2x4(img,width,height,startx+2,starty,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_norm, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_norm, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_norm,           0,   1, 32);

		compressed2_norm[0] = 0;
		PUTBITS( compressed2_norm, (best_pixel_indices1_MSB[0]     ), 8, 23);
		PUTBITS( compressed2_norm, (best_pixel_indices2_MSB[0]     ), 8, 31);
		PUTBITS( compressed2_norm, (best_pixel_indices1_LSB[0]     ), 8, 7);
		PUTBITS( compressed2_norm, (best_pixel_indices2_LSB[0]     ), 8, 15);
	}

	// Now try flipped blocks 4x2:

	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty+2,avg_color_float2);

	// First test if avg_color1 is similar enough to avg_color2 so that
	// we can use differential coding of colors. 

	enc_color1[0] = (int)( JAS_ROUND(31.0*avg_color_float1[0]/255.0) );
	enc_color1[1] = (int)( JAS_ROUND(31.0*avg_color_float1[1]/255.0) );
	enc_color1[2] = (int)( JAS_ROUND(31.0*avg_color_float1[2]/255.0) );
	enc_color2[0] = (int)( JAS_ROUND(31.0*avg_color_float2[0]/255.0) );
	enc_color2[1] = (int)( JAS_ROUND(31.0*avg_color_float2[1]/255.0) );
	enc_color2[2] = (int)( JAS_ROUND(31.0*avg_color_float2[2]/255.0) );

	diff[0] = enc_color2[0]-enc_color1[0];	
	diff[1] = enc_color2[1]-enc_color1[1];	
	diff[2] = enc_color2[2]-enc_color1[2];

	if( (diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4) && (diff[2] <= 3) )
	{
		diffbit = 1;

		// The difference to be coded:

		diff[0] = enc_color2[0]-enc_color1[0];	
		diff[1] = enc_color2[1]-enc_color1[1];	
		diff[2] = enc_color2[2]-enc_color1[2];

		avg_color_quant1[0] = (byte)(enc_color1[0] << 3 | (enc_color1[0] >> 2));
		avg_color_quant1[1] = (byte)(enc_color1[1] << 3 | (enc_color1[1] >> 2));
		avg_color_quant1[2] = (byte)(enc_color1[2] << 3 | (enc_color1[2] >> 2));
		avg_color_quant2[0] = (byte)(enc_color2[0] << 3 | (enc_color2[0] >> 2));
		avg_color_quant2[1] = (byte)(enc_color2[1] << 3 | (enc_color2[1] >> 2));
		avg_color_quant2[2] = (byte)(enc_color2[2] << 3 | (enc_color2[2] >> 2));

		// Pack bits into the first word. 

		compressed1_flip[0] = 0;
		PUTBITSHIGH( compressed1_flip, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_flip, enc_color1[0], 5, 63);
		PUTBITSHIGH( compressed1_flip, enc_color1[1], 5, 55);
		PUTBITSHIGH( compressed1_flip, enc_color1[2], 5, 47);
		PUTBITSHIGH( compressed1_flip, diff[0],       3, 58);
		PUTBITSHIGH( compressed1_flip, diff[1],       3, 50);
		PUTBITSHIGH( compressed1_flip, diff[2],       3, 42);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];

		// upper part of block
		flip_err = tryalltables_3bittable4x2(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);
		// lower part of block
		flip_err += tryalltables_3bittable4x2(img,width,height,startx,starty+2,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_flip, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_flip, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_flip,           1,   1, 32);

		best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
		best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);
		
		compressed2_flip[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16) | (best_pixel_indices1_LSB[0] & 0xffff);
	}
	else
	{
		diffbit = 0;
		// The difference is bigger than what fits in 555 plus delta-333, so we will have
		// to deal with 444 444.
		eps = (float) 0.0001;

		enc_color1[0] = (int)( ((float) avg_color_float1[0] / (17.0)) +0.5 + eps);
		enc_color1[1] = (int)( ((float) avg_color_float1[1] / (17.0)) +0.5 + eps);
		enc_color1[2] = (int)( ((float) avg_color_float1[2] / (17.0)) +0.5 + eps);
		enc_color2[0] = (int)( ((float) avg_color_float2[0] / (17.0)) +0.5 + eps);
		enc_color2[1] = (int)( ((float) avg_color_float2[1] / (17.0)) +0.5 + eps);
		enc_color2[2] = (int)( ((float) avg_color_float2[2] / (17.0)) +0.5 + eps);

		avg_color_quant1[0] = (byte)(enc_color1[0] << 4 | enc_color1[0]); 
		avg_color_quant1[1] = (byte)(enc_color1[1] << 4 | enc_color1[1]); 
		avg_color_quant1[2] = (byte)(enc_color1[2] << 4 | enc_color1[2]);
		avg_color_quant2[0] = (byte)(enc_color2[0] << 4 | enc_color2[0]); 
		avg_color_quant2[1] = (byte)(enc_color2[1] << 4 | enc_color2[1]); 
		avg_color_quant2[2] = (byte)(enc_color2[2] << 4 | enc_color2[2]);

		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
		//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------


		// Pack bits into the first word. 

		compressed1_flip[0] = 0;
		PUTBITSHIGH( compressed1_flip, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_flip, enc_color1[0], 4, 63);
		PUTBITSHIGH( compressed1_flip, enc_color1[1], 4, 55);
		PUTBITSHIGH( compressed1_flip, enc_color1[2], 4, 47);
		PUTBITSHIGH( compressed1_flip, enc_color2[0], 4, 59);
		PUTBITSHIGH( compressed1_flip, enc_color2[1], 4, 51);
		PUTBITSHIGH( compressed1_flip, enc_color2[2], 4, 43);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];

		// upper part of block
		flip_err = tryalltables_3bittable4x2(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);
		// lower part of block
		flip_err += tryalltables_3bittable4x2(img,width,height,startx,starty+2,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_flip, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_flip, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_flip,           1,   1, 32);

		best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
		best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);
		
		compressed2_flip[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16) | (best_pixel_indices1_LSB[0] & 0xffff);
	}

	// Now lets see which is the best table to use. Only 8 tables are possible. 

	if(norm_err <= flip_err)
	{
		compressed1[0] = compressed1_norm[0] | 0;
		compressed2[0] = compressed2_norm[0];
	}
	else
	{
		compressed1[0] = compressed1_flip[0] | 1;
		compressed2[0] = compressed2_flip[0];
	}
}

//Compresses the block using only the differential mode in ETC1/ETC2
//Uses the average color as the base color in each half-block.
//If average colors are too different, use the average color of the entire block in both half-blocks.
//Tries both flipped and unflipped.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &compressed1,  int &compressed2,  int &best_flip
static int compressBlockOnlyDiffFlipAverage(byte[] img,int width,int height,int startx,int starty,  int[] compressed1,  int[] compressed2, int[] best_enc_color1, int[] best_enc_color2, int[] best_flip)
{
	int[] compressed1_norm= new int[1], compressed2_norm= new int[1];
	int[] compressed1_flip= new int[1], compressed2_flip= new int[1];
	byte[] avg_color_quant1 = new byte[3], avg_color_quant2 = new byte[3];

	float[] avg_color_float1 = new float[3],avg_color_float2 = new float[3];
	int[] enc_color1 = new int[3], enc_color2 = new int[3], diff = new int[3];
	int min_error=255*255*8*3;
	int best_table_indices1=0, best_table_indices2=0;
	int[] best_table1=new int[1], best_table2=new int[1];
	int diffbit;

	int norm_err=0;
	int flip_err=0;
	int best_err;

	// First try normal blocks 2x4:

	computeAverageColor2x4noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor2x4noQuantFloat(img,width,height,startx+2,starty,avg_color_float2);

	// First test if avg_color1 is similar enough to avg_color2 so that
	// we can use differential coding of colors. 

	enc_color1[0] = (int)( JAS_ROUND(31.0*avg_color_float1[0]/255.0) );
	enc_color1[1] = (int)( JAS_ROUND(31.0*avg_color_float1[1]/255.0) );
	enc_color1[2] = (int)( JAS_ROUND(31.0*avg_color_float1[2]/255.0) );
	enc_color2[0] = (int)( JAS_ROUND(31.0*avg_color_float2[0]/255.0) );
	enc_color2[1] = (int)( JAS_ROUND(31.0*avg_color_float2[1]/255.0) );
	enc_color2[2] = (int)( JAS_ROUND(31.0*avg_color_float2[2]/255.0) );

	diff[0] = enc_color2[0]-enc_color1[0];	
	diff[1] = enc_color2[1]-enc_color1[1];	
	diff[2] = enc_color2[2]-enc_color1[2];

	if( !((diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4) && (diff[2] <= 3)) )
	{
		// The colors are too different. Use the same color in both blocks.
		enc_color1[0] = (int)( JAS_ROUND(31.0*((avg_color_float1[0]+avg_color_float2[0])/2.0)/255.0) );
		enc_color1[1] = (int)( JAS_ROUND(31.0*((avg_color_float1[1]+avg_color_float2[1])/2.0)/255.0) );
		enc_color1[2] = (int)( JAS_ROUND(31.0*((avg_color_float1[2]+avg_color_float2[2])/2.0)/255.0) );
		enc_color2[0] = enc_color1[0];
		enc_color2[1] = enc_color1[1];
		enc_color2[2] = enc_color1[2];
		diff[0] = enc_color2[0]-enc_color1[0];	
		diff[1] = enc_color2[1]-enc_color1[1];	
		diff[2] = enc_color2[2]-enc_color1[2];
	}

	diffbit = 1;

	// The difference to be coded:

	diff[0] = enc_color2[0]-enc_color1[0];	
	diff[1] = enc_color2[1]-enc_color1[1];	
	diff[2] = enc_color2[2]-enc_color1[2];

	avg_color_quant1[0] = (byte)(enc_color1[0] << 3 | (enc_color1[0] >> 2));
	avg_color_quant1[1] = (byte)(enc_color1[1] << 3 | (enc_color1[1] >> 2));
	avg_color_quant1[2] = (byte)(enc_color1[2] << 3 | (enc_color1[2] >> 2));
	avg_color_quant2[0] = (byte)(enc_color2[0] << 3 | (enc_color2[0] >> 2));
	avg_color_quant2[1] = (byte)(enc_color2[1] << 3 | (enc_color2[1] >> 2));
	avg_color_quant2[2] = (byte)(enc_color2[2] << 3 | (enc_color2[2] >> 2));

	// Pack bits into the first word. 

	//     ETC1_RGB8_OES:
	// 
	//     a) bit layout in bits 63 through 32 if diffbit = 0
	// 
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
	//      ---------------------------------------------------------------------------------------------------
	//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
	//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
	//      ---------------------------------------------------------------------------------------------------
	//     
	//     b) bit layout in bits 63 through 32 if diffbit = 1
	// 
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
	//      ---------------------------------------------------------------------------------------------------
	//     | base col1    | dcol 2 | base col1    | dcol 2 | base col 1   | dcol 2 | table  | table  |diff|flip|
	//     | R1' (5 bits) | dR2    | G1' (5 bits) | dG2    | B1' (5 bits) | dB2    | cw 1   | cw 2   |bit |bit |
	//      ---------------------------------------------------------------------------------------------------
	// 
	//     c) bit layout in bits 31 through 0 (in both cases)
	// 
	//      31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3   2   1  0
	//      --------------------------------------------------------------------------------------------------
	//     |       most significant pixel index bits       |         least significant pixel index bits       |  
	//     | p| o| n| m| l| k| j| i| h| g| f| e| d| c| b| a| p| o| n| m| l| k| j| i| h| g| f| e| d| c | b | a |
	//      --------------------------------------------------------------------------------------------------      

	compressed1_norm[0] = 0;
	PUTBITSHIGH( compressed1_norm, diffbit,       1, 33);
	PUTBITSHIGH( compressed1_norm, enc_color1[0], 5, 63);
	PUTBITSHIGH( compressed1_norm, enc_color1[1], 5, 55);
	PUTBITSHIGH( compressed1_norm, enc_color1[2], 5, 47);
	PUTBITSHIGH( compressed1_norm, diff[0],       3, 58);
	PUTBITSHIGH( compressed1_norm, diff[1],       3, 50);
	PUTBITSHIGH( compressed1_norm, diff[2],       3, 42);

	int[] best_pixel_indices1_MSB = new int[1];
	int[] best_pixel_indices1_LSB = new int[1];
	int[] best_pixel_indices2_MSB = new int[1];
	int[] best_pixel_indices2_LSB = new int[1];

	best_enc_color1[0] = enc_color1[0];
	best_enc_color1[1] = enc_color1[1];
	best_enc_color1[2] = enc_color1[2];
	best_enc_color2[0] = enc_color2[0];
	best_enc_color2[1] = enc_color2[1];
	best_enc_color2[2] = enc_color2[2];

	norm_err = 0;

	// left part of block
	norm_err = tryalltables_3bittable2x4(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);

	// right part of block
	norm_err += tryalltables_3bittable2x4(img,width,height,startx+2,starty,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

	PUTBITSHIGH( compressed1_norm, best_table1[0],   3, 39);
	PUTBITSHIGH( compressed1_norm, best_table2[0],   3, 36);
	PUTBITSHIGH( compressed1_norm,           0,   1, 32);

	compressed2_norm[0] = 0;
	PUTBITS( compressed2_norm, (best_pixel_indices1_MSB[0]     ), 8, 23);
	PUTBITS( compressed2_norm, (best_pixel_indices2_MSB[0]     ), 8, 31);
	PUTBITS( compressed2_norm, (best_pixel_indices1_LSB[0]     ), 8, 7);
	PUTBITS( compressed2_norm, (best_pixel_indices2_LSB[0]     ), 8, 15);

	// Now try flipped blocks 4x2:

	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty+2,avg_color_float2);

	// First test if avg_color1 is similar enough to avg_color2 so that
	// we can use differential coding of colors. 

	enc_color1[0] = (int)( JAS_ROUND(31.0*avg_color_float1[0]/255.0) );
	enc_color1[1] = (int)( JAS_ROUND(31.0*avg_color_float1[1]/255.0) );
	enc_color1[2] = (int)( JAS_ROUND(31.0*avg_color_float1[2]/255.0) );
	enc_color2[0] = (int)( JAS_ROUND(31.0*avg_color_float2[0]/255.0) );
	enc_color2[1] = (int)( JAS_ROUND(31.0*avg_color_float2[1]/255.0) );
	enc_color2[2] = (int)( JAS_ROUND(31.0*avg_color_float2[2]/255.0) );

	diff[0] = enc_color2[0]-enc_color1[0];	
	diff[1] = enc_color2[1]-enc_color1[1];	
	diff[2] = enc_color2[2]-enc_color1[2];

	if( !((diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4) && (diff[2] <= 3)) )
	{
		// The colors are too different. Use the same color in both blocks.
		enc_color1[0] = (int)( JAS_ROUND(31.0*((avg_color_float1[0]+avg_color_float2[0])/2.0)/255.0) );
		enc_color1[1] = (int)( JAS_ROUND(31.0*((avg_color_float1[1]+avg_color_float2[1])/2.0)/255.0) );
		enc_color1[2] = (int)( JAS_ROUND(31.0*((avg_color_float1[2]+avg_color_float2[2])/2.0)/255.0) );
		enc_color2[0] = enc_color1[0];
		enc_color2[1] = enc_color1[1];
		enc_color2[2] = enc_color1[2];
		diff[0] = enc_color2[0]-enc_color1[0];	
		diff[1] = enc_color2[1]-enc_color1[1];	
		diff[2] = enc_color2[2]-enc_color1[2];
	}
	diffbit = 1;

	// The difference to be coded:

	diff[0] = enc_color2[0]-enc_color1[0];	
	diff[1] = enc_color2[1]-enc_color1[1];	
	diff[2] = enc_color2[2]-enc_color1[2];

	avg_color_quant1[0] = (byte)(enc_color1[0] << 3 | (enc_color1[0] >> 2));
	avg_color_quant1[1] = (byte)(enc_color1[1] << 3 | (enc_color1[1] >> 2));
	avg_color_quant1[2] = (byte)(enc_color1[2] << 3 | (enc_color1[2] >> 2));
	avg_color_quant2[0] = (byte)(enc_color2[0] << 3 | (enc_color2[0] >> 2));
	avg_color_quant2[1] = (byte)(enc_color2[1] << 3 | (enc_color2[1] >> 2));
	avg_color_quant2[2] = (byte)(enc_color2[2] << 3 | (enc_color2[2] >> 2));

	// Pack bits into the first word. 

	compressed1_flip[0] = 0;
	PUTBITSHIGH( compressed1_flip, diffbit,       1, 33);
	PUTBITSHIGH( compressed1_flip, enc_color1[0], 5, 63);
	PUTBITSHIGH( compressed1_flip, enc_color1[1], 5, 55);
	PUTBITSHIGH( compressed1_flip, enc_color1[2], 5, 47);
	PUTBITSHIGH( compressed1_flip, diff[0],       3, 58);
	PUTBITSHIGH( compressed1_flip, diff[1],       3, 50);
	PUTBITSHIGH( compressed1_flip, diff[2],       3, 42);

	// upper part of block
	flip_err = tryalltables_3bittable4x2(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);
	// lower part of block
	flip_err += tryalltables_3bittable4x2(img,width,height,startx,starty+2,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

	PUTBITSHIGH( compressed1_flip, best_table1[0],   3, 39);
	PUTBITSHIGH( compressed1_flip, best_table2[0],   3, 36);
	PUTBITSHIGH( compressed1_flip,           1,   1, 32);

	best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
	best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);
	
	compressed2_flip[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16) | (best_pixel_indices1_LSB[0] & 0xffff);

	// Now lets see which is the best table to use. Only 8 tables are possible. 

	if(norm_err <= flip_err)
	{
		compressed1[0] = compressed1_norm[0] | 0;
		compressed2[0] = compressed2_norm[0];
		best_err = norm_err;
		best_flip[0] = 0;
	}
	else
	{
		compressed1[0] = compressed1_flip[0] | 1;
		compressed2[0] = compressed2_flip[0];
		best_err = flip_err;
		best_enc_color1[0] = enc_color1[0];
		best_enc_color1[1] = enc_color1[1];
		best_enc_color1[2] = enc_color1[2];
		best_enc_color2[0] = enc_color2[0];
		best_enc_color2[1] = enc_color2[1];
		best_enc_color2[2] = enc_color2[2];
		best_flip[0] = 1;
	}
	return best_err;
}

//Compresses the block using only the differential mode in ETC1/ETC2
//Uses the average color as the base color in each half-block.
//If average colors are too different, use the average color of the entire block in both half-blocks.
//Tries both flipped and unflipped.
//Uses fixed point arithmetics where 1000 represents 1.0.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address  int &compressed1, int &compressed2
static int compressBlockOnlyDiffFlipAveragePerceptual1000(byte[] img,int width,int height,int startx,int starty, int[] compressed1, int[] compressed2)
{
	int[] compressed1_norm= new int[1], compressed2_norm= new int[1];
	int[] compressed1_flip= new int[1], compressed2_flip= new int[1];
	byte[] avg_color_quant1 = new byte[3], avg_color_quant2 = new byte[3];

	float[] avg_color_float1 = new float[3],avg_color_float2 = new float[3];
	int[] enc_color1 = new int[3],enc_color2 = new int[3],diff = new int[3];
	int min_error=MAXERR1000;
	int best_table_indices1=0, best_table_indices2=0;
	int[] best_table1=new int[1], best_table2=new int[1];
	int diffbit;

	int norm_err=0;
	int flip_err=0;

	// First try normal blocks 2x4:

	computeAverageColor2x4noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor2x4noQuantFloat(img,width,height,startx+2,starty,avg_color_float2);

	// First test if avg_color1 is similar enough to avg_color2 so that
	// we can use differential coding of colors. 

	enc_color1[0] = (int)( JAS_ROUND(31.0*avg_color_float1[0]/255.0) );
	enc_color1[1] = (int)( JAS_ROUND(31.0*avg_color_float1[1]/255.0) );
	enc_color1[2] = (int)( JAS_ROUND(31.0*avg_color_float1[2]/255.0) );
	enc_color2[0] = (int)( JAS_ROUND(31.0*avg_color_float2[0]/255.0) );
	enc_color2[1] = (int)( JAS_ROUND(31.0*avg_color_float2[1]/255.0) );
	enc_color2[2] = (int)( JAS_ROUND(31.0*avg_color_float2[2]/255.0) );

	diff[0] = enc_color2[0]-enc_color1[0];	
	diff[1] = enc_color2[1]-enc_color1[1];	
	diff[2] = enc_color2[2]-enc_color1[2];

	if( !((diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4) && (diff[2] <= 3)) )
	{
		enc_color1[0] = (enc_color1[0] + enc_color2[0]) >> 1;
		enc_color1[1] = (enc_color1[1] + enc_color2[1]) >> 1;
		enc_color1[2] = (enc_color1[2] + enc_color2[2]) >> 1;

		enc_color2[0] = enc_color1[0];
		enc_color2[1] = enc_color1[1];
		enc_color2[2] = enc_color1[2];

	}

	{
		diffbit = 1;

		// The difference to be coded:

		diff[0] = enc_color2[0]-enc_color1[0];	
		diff[1] = enc_color2[1]-enc_color1[1];	
		diff[2] = enc_color2[2]-enc_color1[2];

		avg_color_quant1[0] = (byte)(enc_color1[0] << 3 | (enc_color1[0] >> 2));
		avg_color_quant1[1] = (byte)(enc_color1[1] << 3 | (enc_color1[1] >> 2));
		avg_color_quant1[2] = (byte)(enc_color1[2] << 3 | (enc_color1[2] >> 2));
		avg_color_quant2[0] = (byte)(enc_color2[0] << 3 | (enc_color2[0] >> 2));
		avg_color_quant2[1] = (byte)(enc_color2[1] << 3 | (enc_color2[1] >> 2));
		avg_color_quant2[2] = (byte)(enc_color2[2] << 3 | (enc_color2[2] >> 2));

		// Pack bits into the first word. 

		//     ETC1_RGB8_OES:
		// 
		//     a) bit layout in bits 63 through 32 if diffbit = 0
		// 
		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
		//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------
		//     
		//     b) bit layout in bits 63 through 32 if diffbit = 1
		// 
		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1    | dcol 2 | base col1    | dcol 2 | base col 1   | dcol 2 | table  | table  |diff|flip|
		//     | R1' (5 bits) | dR2    | G1' (5 bits) | dG2    | B1' (5 bits) | dB2    | cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------
		// 
		//     c) bit layout in bits 31 through 0 (in both cases)
		// 
		//      31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3   2   1  0
		//      --------------------------------------------------------------------------------------------------
		//     |       most significant pixel index bits       |         least significant pixel index bits       |  
		//     | p| o| n| m| l| k| j| i| h| g| f| e| d| c| b| a| p| o| n| m| l| k| j| i| h| g| f| e| d| c | b | a |
		//      --------------------------------------------------------------------------------------------------      

		compressed1_norm[0] = 0;
		PUTBITSHIGH( compressed1_norm, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_norm, enc_color1[0], 5, 63);
		PUTBITSHIGH( compressed1_norm, enc_color1[1], 5, 55);
		PUTBITSHIGH( compressed1_norm, enc_color1[2], 5, 47);
		PUTBITSHIGH( compressed1_norm, diff[0],       3, 58);
		PUTBITSHIGH( compressed1_norm, diff[1],       3, 50);
		PUTBITSHIGH( compressed1_norm, diff[2],       3, 42);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];

		norm_err = 0;

		// left part of block 
		norm_err = tryalltables_3bittable2x4percep1000(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);

		// right part of block
		norm_err += tryalltables_3bittable2x4percep1000(img,width,height,startx+2,starty,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_norm, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_norm, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_norm,           0,   1, 32);

		compressed2_norm[0] = 0;
		PUTBITS( compressed2_norm, (best_pixel_indices1_MSB[0]     ), 8, 23);
		PUTBITS( compressed2_norm, (best_pixel_indices2_MSB[0]     ), 8, 31);
		PUTBITS( compressed2_norm, (best_pixel_indices1_LSB[0]     ), 8, 7);
		PUTBITS( compressed2_norm, (best_pixel_indices2_LSB[0]     ), 8, 15);

	}
	// Now try flipped blocks 4x2:

	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty+2,avg_color_float2);

	// First test if avg_color1 is similar enough to avg_color2 so that
	// we can use differential coding of colors. 

	enc_color1[0] = (int)( JAS_ROUND(31.0*avg_color_float1[0]/255.0) );
	enc_color1[1] = (int)( JAS_ROUND(31.0*avg_color_float1[1]/255.0) );
	enc_color1[2] = (int)( JAS_ROUND(31.0*avg_color_float1[2]/255.0) );
	enc_color2[0] = (int)( JAS_ROUND(31.0*avg_color_float2[0]/255.0) );
	enc_color2[1] = (int)( JAS_ROUND(31.0*avg_color_float2[1]/255.0) );
	enc_color2[2] = (int)( JAS_ROUND(31.0*avg_color_float2[2]/255.0) );

	diff[0] = enc_color2[0]-enc_color1[0];	
	diff[1] = enc_color2[1]-enc_color1[1];	
	diff[2] = enc_color2[2]-enc_color1[2];

	if( !((diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4) && (diff[2] <= 3)) )
	{
		enc_color1[0] = (enc_color1[0] + enc_color2[0]) >> 1;
		enc_color1[1] = (enc_color1[1] + enc_color2[1]) >> 1;
		enc_color1[2] = (enc_color1[2] + enc_color2[2]) >> 1;

		enc_color2[0] = enc_color1[0];
		enc_color2[1] = enc_color1[1];
		enc_color2[2] = enc_color1[2];
	}

	{
		diffbit = 1;

		// The difference to be coded:

		diff[0] = enc_color2[0]-enc_color1[0];	
		diff[1] = enc_color2[1]-enc_color1[1];	
		diff[2] = enc_color2[2]-enc_color1[2];

		avg_color_quant1[0] = (byte)(enc_color1[0] << 3 | (enc_color1[0] >> 2));
		avg_color_quant1[1] = (byte)(enc_color1[1] << 3 | (enc_color1[1] >> 2));
		avg_color_quant1[2] = (byte)(enc_color1[2] << 3 | (enc_color1[2] >> 2));
		avg_color_quant2[0] = (byte)(enc_color2[0] << 3 | (enc_color2[0] >> 2));
		avg_color_quant2[1] = (byte)(enc_color2[1] << 3 | (enc_color2[1] >> 2));
		avg_color_quant2[2] = (byte)(enc_color2[2] << 3 | (enc_color2[2] >> 2));

		// Pack bits into the first word. 

		compressed1_flip[0] = 0;
		PUTBITSHIGH( compressed1_flip, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_flip, enc_color1[0], 5, 63);
		PUTBITSHIGH( compressed1_flip, enc_color1[1], 5, 55);
		PUTBITSHIGH( compressed1_flip, enc_color1[2], 5, 47);
		PUTBITSHIGH( compressed1_flip, diff[0],       3, 58);
		PUTBITSHIGH( compressed1_flip, diff[1],       3, 50);
		PUTBITSHIGH( compressed1_flip, diff[2],       3, 42);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];

		// upper part of block
		flip_err = tryalltables_3bittable4x2percep1000(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);
		// lower part of block
		flip_err += tryalltables_3bittable4x2percep1000(img,width,height,startx,starty+2,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_flip, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_flip, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_flip,           1,   1, 32);

		best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
		best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);
		
		compressed2_flip[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16) | (best_pixel_indices1_LSB[0] & 0xffff);
	}
	int best_err;

	if(norm_err <= flip_err)
	{
		compressed1[0] = compressed1_norm[0] | 0;
		compressed2[0] = compressed2_norm[0];
		best_err = norm_err;
	}
	else
	{
		compressed1[0] = compressed1_flip[0] | 1;
		compressed2[0] = compressed2_flip[0];
		best_err = flip_err;
	}
	return best_err;
}

//Compresses the block using both the individual and the differential mode in ETC1/ETC2
//Uses the average color as the base color in each half-block.
//Uses a perceptual error metric.
//Tries both flipped and unflipped.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
// address unsigned int &compressed1, unsigned int &compressed2)
static double compressBlockDiffFlipAveragePerceptual(byte[] img,int width,int height,int startx,int starty,  int[] compressed1,  int[] compressed2)
{
	int[] compressed1_norm= new int[1], compressed2_norm= new int[1];
	int[] compressed1_flip= new int[1], compressed2_flip= new int[1];
	byte[] avg_color_quant1 = new byte[3], avg_color_quant2 = new byte[3];

	float[] avg_color_float1 = new float[3],avg_color_float2 = new float[3];
	int[] enc_color1 = new int[3], enc_color2 = new int[3], diff = new int[3];
	int min_error=255*255*8*3;
	int best_table_indices1=0, best_table_indices2=0;
	int[] best_table1=new int[1], best_table2=new int[1];
	int diffbit;

	int norm_err=0;
	int flip_err=0;

	// First try normal blocks 2x4:

	computeAverageColor2x4noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor2x4noQuantFloat(img,width,height,startx+2,starty,avg_color_float2);

	// First test if avg_color1 is similar enough to avg_color2 so that
	// we can use differential coding of colors. 

	float eps;

	enc_color1[0] = (int)( JAS_ROUND(31.0*avg_color_float1[0]/255.0) );
	enc_color1[1] = (int)( JAS_ROUND(31.0*avg_color_float1[1]/255.0) );
	enc_color1[2] = (int)( JAS_ROUND(31.0*avg_color_float1[2]/255.0) );
	enc_color2[0] = (int)( JAS_ROUND(31.0*avg_color_float2[0]/255.0) );
	enc_color2[1] = (int)( JAS_ROUND(31.0*avg_color_float2[1]/255.0) );
	enc_color2[2] = (int)( JAS_ROUND(31.0*avg_color_float2[2]/255.0) );

	diff[0] = enc_color2[0]-enc_color1[0];	
	diff[1] = enc_color2[1]-enc_color1[1];	
	diff[2] = enc_color2[2]-enc_color1[2];

	if( (diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4) && (diff[2] <= 3) )
	{
		diffbit = 1;

		// The difference to be coded:
		diff[0] = enc_color2[0]-enc_color1[0];	
		diff[1] = enc_color2[1]-enc_color1[1];	
		diff[2] = enc_color2[2]-enc_color1[2];

		avg_color_quant1[0] = (byte)(enc_color1[0] << 3 | (enc_color1[0] >> 2));
		avg_color_quant1[1] = (byte)(enc_color1[1] << 3 | (enc_color1[1] >> 2));
		avg_color_quant1[2] = (byte)(enc_color1[2] << 3 | (enc_color1[2] >> 2));
		avg_color_quant2[0] = (byte)(enc_color2[0] << 3 | (enc_color2[0] >> 2));
		avg_color_quant2[1] = (byte)(enc_color2[1] << 3 | (enc_color2[1] >> 2));
		avg_color_quant2[2] = (byte)(enc_color2[2] << 3 | (enc_color2[2] >> 2));

		// Pack bits into the first word. 

		//     ETC1_RGB8_OES:
		// 
		//     a) bit layout in bits 63 through 32 if diffbit = 0
		// 
		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
		//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------
		//     
		//     b) bit layout in bits 63 through 32 if diffbit = 1
		// 
		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1    | dcol 2 | base col1    | dcol 2 | base col 1   | dcol 2 | table  | table  |diff|flip|
		//     | R1' (5 bits) | dR2    | G1' (5 bits) | dG2    | B1' (5 bits) | dB2    | cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------
		// 
		//     c) bit layout in bits 31 through 0 (in both cases)
		// 
		//      31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3   2   1  0
		//      --------------------------------------------------------------------------------------------------
		//     |       most significant pixel index bits       |         least significant pixel index bits       |  
		//     | p| o| n| m| l| k| j| i| h| g| f| e| d| c| b| a| p| o| n| m| l| k| j| i| h| g| f| e| d| c | b | a |
		//      --------------------------------------------------------------------------------------------------      

		compressed1_norm[0] = 0;
		PUTBITSHIGH( compressed1_norm, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_norm, enc_color1[0], 5, 63);
		PUTBITSHIGH( compressed1_norm, enc_color1[1], 5, 55);
		PUTBITSHIGH( compressed1_norm, enc_color1[2], 5, 47);
		PUTBITSHIGH( compressed1_norm, diff[0],       3, 58);
		PUTBITSHIGH( compressed1_norm, diff[1],       3, 50);
		PUTBITSHIGH( compressed1_norm, diff[2],       3, 42);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];

		norm_err = 0;

		// left part of block 
		norm_err = tryalltables_3bittable2x4percep(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);

		// right part of block
		norm_err += tryalltables_3bittable2x4percep(img,width,height,startx+2,starty,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_norm, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_norm, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_norm,           0,   1, 32);

		compressed2_norm[0] = 0;
		PUTBITS( compressed2_norm, (best_pixel_indices1_MSB[0]     ), 8, 23);
		PUTBITS( compressed2_norm, (best_pixel_indices2_MSB[0]     ), 8, 31);
		PUTBITS( compressed2_norm, (best_pixel_indices1_LSB[0]     ), 8, 7);
		PUTBITS( compressed2_norm, (best_pixel_indices2_LSB[0]     ), 8, 15);
	}
	else
	{
		diffbit = 0;
		// The difference is bigger than what fits in 555 plus delta-333, so we will have
		// to deal with 444 444.

		eps = (float) 0.0001;

		enc_color1[0] = (int)( ((float) avg_color_float1[0] / (17.0)) +0.5 + eps);
		enc_color1[1] = (int)( ((float) avg_color_float1[1] / (17.0)) +0.5 + eps);
		enc_color1[2] = (int)( ((float) avg_color_float1[2] / (17.0)) +0.5 + eps);
		enc_color2[0] = (int)( ((float) avg_color_float2[0] / (17.0)) +0.5 + eps);
		enc_color2[1] = (int)( ((float) avg_color_float2[1] / (17.0)) +0.5 + eps);
		enc_color2[2] = (int)( ((float) avg_color_float2[2] / (17.0)) +0.5 + eps);
		
		avg_color_quant1[0] = (byte)(enc_color1[0] << 4 | enc_color1[0]); 
		avg_color_quant1[1] = (byte)(enc_color1[1] << 4 | enc_color1[1]); 
		avg_color_quant1[2] = (byte)(enc_color1[2] << 4 | enc_color1[2]);
		avg_color_quant2[0] = (byte)(enc_color2[0] << 4 | enc_color2[0]); 
		avg_color_quant2[1] = (byte)(enc_color2[1] << 4 | enc_color2[1]); 
		avg_color_quant2[2] = (byte)(enc_color2[2] << 4 | enc_color2[2]);
	
		// Pack bits into the first word. 

		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
		//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------

		compressed1_norm[0] = 0;
		PUTBITSHIGH( compressed1_norm, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_norm, enc_color1[0], 4, 63);
		PUTBITSHIGH( compressed1_norm, enc_color1[1], 4, 55);
		PUTBITSHIGH( compressed1_norm, enc_color1[2], 4, 47);
		PUTBITSHIGH( compressed1_norm, enc_color2[0], 4, 59);
		PUTBITSHIGH( compressed1_norm, enc_color2[1], 4, 51);
		PUTBITSHIGH( compressed1_norm, enc_color2[2], 4, 43);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];
		
		// left part of block
		norm_err = tryalltables_3bittable2x4percep(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);

		// right part of block
		norm_err += tryalltables_3bittable2x4percep(img,width,height,startx+2,starty,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_norm, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_norm, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_norm,           0,   1, 32);

		compressed2_norm[0] = 0;
		PUTBITS( compressed2_norm, (best_pixel_indices1_MSB[0]     ), 8, 23);
		PUTBITS( compressed2_norm, (best_pixel_indices2_MSB[0]     ), 8, 31);
		PUTBITS( compressed2_norm, (best_pixel_indices1_LSB[0]     ), 8, 7);
		PUTBITS( compressed2_norm, (best_pixel_indices2_LSB[0]     ), 8, 15);
	}

	// Now try flipped blocks 4x2:

	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty+2,avg_color_float2);

	// First test if avg_color1 is similar enough to avg_color2 so that
	// we can use differential coding of colors. 

	enc_color1[0] = (int)( JAS_ROUND(31.0*avg_color_float1[0]/255.0) );
	enc_color1[1] = (int)( JAS_ROUND(31.0*avg_color_float1[1]/255.0) );
	enc_color1[2] = (int)( JAS_ROUND(31.0*avg_color_float1[2]/255.0) );
	enc_color2[0] = (int)( JAS_ROUND(31.0*avg_color_float2[0]/255.0) );
	enc_color2[1] = (int)( JAS_ROUND(31.0*avg_color_float2[1]/255.0) );
	enc_color2[2] = (int)( JAS_ROUND(31.0*avg_color_float2[2]/255.0) );

	diff[0] = enc_color2[0]-enc_color1[0];	
	diff[1] = enc_color2[1]-enc_color1[1];	
	diff[2] = enc_color2[2]-enc_color1[2];

	if( (diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4) && (diff[2] <= 3) )
	{
		diffbit = 1;

		// The difference to be coded:

		diff[0] = enc_color2[0]-enc_color1[0];	
		diff[1] = enc_color2[1]-enc_color1[1];	
		diff[2] = enc_color2[2]-enc_color1[2];

		avg_color_quant1[0] = (byte)(enc_color1[0] << 3 | (enc_color1[0] >> 2));
		avg_color_quant1[1] = (byte)(enc_color1[1] << 3 | (enc_color1[1] >> 2));
		avg_color_quant1[2] = (byte)(enc_color1[2] << 3 | (enc_color1[2] >> 2));
		avg_color_quant2[0] = (byte)(enc_color2[0] << 3 | (enc_color2[0] >> 2));
		avg_color_quant2[1] = (byte)(enc_color2[1] << 3 | (enc_color2[1] >> 2));
		avg_color_quant2[2] = (byte)(enc_color2[2] << 3 | (enc_color2[2] >> 2));

		// Pack bits into the first word. 

		compressed1_flip[0] = 0;
		PUTBITSHIGH( compressed1_flip, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_flip, enc_color1[0], 5, 63);
		PUTBITSHIGH( compressed1_flip, enc_color1[1], 5, 55);
		PUTBITSHIGH( compressed1_flip, enc_color1[2], 5, 47);
		PUTBITSHIGH( compressed1_flip, diff[0],       3, 58);
		PUTBITSHIGH( compressed1_flip, diff[1],       3, 50);
		PUTBITSHIGH( compressed1_flip, diff[2],       3, 42);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];

		// upper part of block
		flip_err = tryalltables_3bittable4x2percep(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);
		// lower part of block
		flip_err += tryalltables_3bittable4x2percep(img,width,height,startx,starty+2,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_flip, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_flip, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_flip,           1,   1, 32);

		best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
		best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);
		
		compressed2_flip[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16) | (best_pixel_indices1_LSB[0] & 0xffff);
	}
	else
	{
		diffbit = 0;
		// The difference is bigger than what fits in 555 plus delta-333, so we will have
		// to deal with 444 444.
		eps = (float) 0.0001;

		enc_color1[0] = (int)( ((float) avg_color_float1[0] / (17.0)) +0.5 + eps);
		enc_color1[1] = (int)( ((float) avg_color_float1[1] / (17.0)) +0.5 + eps);
		enc_color1[2] = (int)( ((float) avg_color_float1[2] / (17.0)) +0.5 + eps);
		enc_color2[0] = (int)( ((float) avg_color_float2[0] / (17.0)) +0.5 + eps);
		enc_color2[1] = (int)( ((float) avg_color_float2[1] / (17.0)) +0.5 + eps);
		enc_color2[2] = (int)( ((float) avg_color_float2[2] / (17.0)) +0.5 + eps);

		avg_color_quant1[0] = (byte)(enc_color1[0] << 4 | enc_color1[0]); 
		avg_color_quant1[1] = (byte)(enc_color1[1] << 4 | enc_color1[1]); 
		avg_color_quant1[2] = (byte)(enc_color1[2] << 4 | enc_color1[2]);
		avg_color_quant2[0] = (byte)(enc_color2[0] << 4 | enc_color2[0]); 
		avg_color_quant2[1] = (byte)(enc_color2[1] << 4 | enc_color2[1]); 
		avg_color_quant2[2] = (byte)(enc_color2[2] << 4 | enc_color2[2]);

		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
		//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------

		// Pack bits into the first word. 

		compressed1_flip[0] = 0;
		PUTBITSHIGH( compressed1_flip, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_flip, enc_color1[0], 4, 63);
		PUTBITSHIGH( compressed1_flip, enc_color1[1], 4, 55);
		PUTBITSHIGH( compressed1_flip, enc_color1[2], 4, 47);
		PUTBITSHIGH( compressed1_flip, enc_color2[0], 4, 59);
		PUTBITSHIGH( compressed1_flip, enc_color2[1], 4, 51);
		PUTBITSHIGH( compressed1_flip, enc_color2[2], 4, 43);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];

		// upper part of block
		flip_err = tryalltables_3bittable4x2percep(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);
		// lower part of block
		flip_err += tryalltables_3bittable4x2percep(img,width,height,startx,starty+2,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_flip, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_flip, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_flip,           1,   1, 32);

		best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
		best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);
		
		compressed2_flip[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16) | (best_pixel_indices1_LSB[0] & 0xffff);
	}

	// Now lets see which is the best table to use. Only 8 tables are possible. 
	
	double best_err;

	if(norm_err <= flip_err)
	{
		compressed1[0] = compressed1_norm[0] | 0;
		compressed2[0] = compressed2_norm[0];
		best_err = norm_err;
	}
	else
	{
		compressed1[0] = compressed1_flip[0] | 1;
		compressed2[0] = compressed2_flip[0];
		best_err = flip_err;
	}
	return best_err;
}

//This is our structure for matrix data
static class dMatrix
{
	int width;			// The number of columns in the matrix
	int height;			// The number of rows in the matrix
	double[] data;		// The matrix data in row order
}

//Multiplies two matrices
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static dMatrix multiplyMatrices( dMatrix Amat, dMatrix Bmat)
{
	int xx,yy, q;
	dMatrix resmatrix;

	if(Amat.width != Bmat.height)
	{
		System.out.println("Cannot multiply matrices -- dimensions do not agree.");
		System.exit(1);
	}

	// Allocate space for result
	resmatrix = new dMatrix();
	resmatrix.width = Bmat.width;
	resmatrix.height = Amat.height;
	resmatrix.data = new double[(resmatrix.width)*(resmatrix.height)];

	for(yy = 0; yy<resmatrix.height; yy++)
		for(xx = 0; xx<resmatrix.width; xx++)
			for(q=0, resmatrix.data[yy*resmatrix.width+xx] = 0.0; q<Amat.width; q++)
				resmatrix.data[yy*resmatrix.width+xx] += Amat.data[yy*Amat.width + q] * Bmat.data[q*Bmat.width+xx];

	return(resmatrix);

}

//Transposes a matrix
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void transposeMatrix( dMatrix mat)
{
	int xx, yy, zz;
	double[] temp;
	int newwidth, newheight;

	temp = new double[(mat.width)*(mat.height)];

	for(zz = 0; zz<((mat.width)*(mat.height)); zz++)
		temp[zz] = mat.data[zz];

	newwidth = mat.height;
	newheight= mat.width;

	for(yy = 0; yy<newheight; yy++)
		for(xx = 0; xx<newwidth; xx++)
			mat.data[yy*newwidth+xx] = temp[xx*(mat.width)+yy];

	mat.height = newheight;
	mat.width = newwidth;
	//free(temp);
}

//In the planar mode in ETC2, the block can be partitioned as follows:
//
//O A  A  A  H
//B D1 D3 C3
//B D2 C2 D5
//B C1 D4 D6
//V
//Here A-pixels, B-pixels and C-pixels only depend on two values. For instance, B-pixels only depend on O and V.
//This can be used to quickly rule out combinations of colors.
//Here we calculate the minimum error for the block if we know the red component for O and V.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcBBBred(byte[] block, int colorO, int colorV)
{
	colorO = (colorO << 2) | (colorO >> 4);
	colorV = (colorV << 2) | (colorV >> 4);

	int error = 0;
	
	// Now first column: B B B 
	/* unroll loop for( yy=0; (yy<4) && (error <= best_error_sofar); yy++)*/
	{
		error = error + square_table[((block[4*4 + 0]&0xff) - clamp_table[ ((((colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table[((block[4*4*2 + 0]&0xff) - clamp_table[ (((((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table[((block[4*4*3 + 0]&0xff) - clamp_table[ (((3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
	}

	return error;
}

//Calculating the minimum error for the block if we know the red component for H and V.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcCCCred(byte[] block, int colorH, int colorV)
{
   colorH = (colorH << 2) | (colorH >> 4);
   colorV = (colorV << 2) | (colorV >> 4);

	int error=0;

	error = error + square_table[((block[4*4*3 + 4 + 0]&0xff) - clamp_table[ (((colorH + 3*colorV)+2)>>2) + 255])+255];
	error = error + square_table[((block[4*4*2 + 4*2 + 0]&0xff) - clamp_table[ (((2*colorH + 2*colorV)+2)>>2) + 255])+255];
	error = error + square_table[((block[4*4 + 4*3 + 0]&0xff) - clamp_table[ (((3*colorH + colorV)+2)>>2) + 255])+255];
	
	return error;
}

//Calculating the minimum error for the block if we know the red component for O and H.
//Uses perceptual error metric.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcLowestPossibleRedOHperceptual(byte[] block, int colorO, int colorH, int best_error_sofar)
{
	colorO = (colorO << 2) | (colorO >> 4);
	colorH = (colorH << 2) | (colorH >> 4);

	int error;

	error = square_table_percep_red[((block[0]&0xff) - colorO) + 255];
	error = error + square_table_percep_red[((block[4]&0xff) - clamp_table[ (((   (colorH-colorO) + 4*colorO)+2)>>2) + 255])+255];
	if(error <= best_error_sofar)
	{
		error = error + square_table_percep_red[((block[4*2]&0xff) - clamp_table[ (((  ((colorH-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table_percep_red[((block[4*3]&0xff) - clamp_table[ ((( 3*(colorH-colorO) + 4*colorO)+2)>>2) + 255])+255];
	}
	
	return error;
}

//Calculating the minimum error for the block (in planar mode) if we know the red component for O and H.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcLowestPossibleRedOH(byte[] block, int colorO, int colorH, int best_error_sofar)
{
	colorO = (colorO << 2) | (colorO >> 4);
	colorH = (colorH << 2) | (colorH >> 4);

	int error;

	error = square_table[((block[0]&0xff) - colorO) + 255];
	error = error + square_table[((block[4]&0xff) - clamp_table[ (((   (colorH-colorO) + 4*colorO)+2)>>2) + 255])+255];
	if(error <= best_error_sofar)
	{
		error = error + square_table[((block[4*2]&0xff) - clamp_table[ (((  ((colorH-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table[((block[4*3]&0xff) - clamp_table[ ((( 3*(colorH-colorO) + 4*colorO)+2)>>2) + 255])+255];
	}
	
	return error;
}

//Calculating the minimum error for the block (in planar mode) if we know the red component for O and H and V.
//Uses perceptual error metric. 
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcErrorPlanarOnlyRedPerceptual(byte[] block, int colorO, int colorH, int colorV, int lowest_possible_error, int BBBvalue, int CCCvalue, int best_error_sofar)
{
	colorO = (colorO << 2) | (colorO >> 4);
	colorH = (colorH << 2) | (colorH >> 4);
	colorV = (colorV << 2) | (colorV >> 4);

	int error;

	// The block can be partitioned into: O A  A  A
	//                                    B D1 D3 C3
	//                                    B D2 C2 D5
	//                                    B C1 D4 D6
	int xpart_times_4;

	// The first part: O A A A. It equals lowest_possible_error previously calculated. 
	// lowest_possible_error is OAAA, BBBvalue is BBB and CCCvalue is C1C2C3.
	error = lowest_possible_error + BBBvalue + CCCvalue;

	// The remaining pixels to cover are D1 through D6.
	if(error <= best_error_sofar)
	{
		// Second column: D1 D2  but not C1
		xpart_times_4 = (colorH-colorO);
		error = error + square_table_percep_red[((block[4*4 + 4 + 0]&0xff) - clamp_table[ (((xpart_times_4 + (colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table_percep_red[((block[4*4*2 + 4 + 0]&0xff) - clamp_table[ (((xpart_times_4 + ((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		// Third column: D3 notC2 D4
		xpart_times_4 = (colorH-colorO) << 1;
		error = error + square_table_percep_red[((block[4*4 + 4*2 + 0]&0xff) - clamp_table[ (((xpart_times_4 + (colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		if(error <= best_error_sofar)
		{
			error = error + square_table_percep_red[((block[4*4*3 + 4*2 + 0]&0xff) - clamp_table[ (((xpart_times_4 + 3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
			// Forth column: notC3 D5 D6
			xpart_times_4 = 3*(colorH-colorO);
			error = error + square_table_percep_red[((block[4*4*2 + 4*3 + 0]&0xff) - clamp_table[ (((xpart_times_4 + ((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
			error = error + square_table_percep_red[((block[4*4*3 + 4*3 + 0]&0xff) - clamp_table[ (((xpart_times_4 + 3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		}
	}
	return error;
} 

//Calculating the minimum error for the block (in planar mode) if we know the red component for O and H and V.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcErrorPlanarOnlyRed(byte[] block, int colorO, int colorH, int colorV, int lowest_possible_error, int BBBvalue, int CCCvalue, int best_error_sofar)
{
	colorO = (colorO << 2) | (colorO >> 4);
	colorH = (colorH << 2) | (colorH >> 4);
	colorV = (colorV << 2) | (colorV >> 4);

	int error;

	// The block can be partitioned into: O A  A  A
	//                                    B D1 D3 C3
	//                                    B D2 C2 D5
	//                                    B C1 D4 D6
	int xpart_times_4;

	// The first part: O A A A. It equals lowest_possible_error previously calculated. 
	// lowest_possible_error is OAAA, BBBvalue is BBB and CCCvalue is C1C2C3.
	error = lowest_possible_error + BBBvalue + CCCvalue;

	// The remaining pixels to cover are D1 through D6.
	if(error <= best_error_sofar)
	{
		// Second column: D1 D2  but not C1
		xpart_times_4 = (colorH-colorO);
		error = error + square_table[((block[4*4 + 4 + 0]&0xff) - clamp_table[ (((xpart_times_4 + (colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table[((block[4*4*2 + 4 + 0]&0xff) - clamp_table[ (((xpart_times_4 + ((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		// Third column: D3 notC2 D4
		xpart_times_4 = (colorH-colorO) << 1;
		error = error + square_table[((block[4*4 + 4*2 + 0]&0xff) - clamp_table[ (((xpart_times_4 + (colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		if(error <= best_error_sofar)
		{
			error = error + square_table[((block[4*4*3 + 4*2 + 0]&0xff) - clamp_table[ (((xpart_times_4 + 3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
			// Forth column: notC3 D5 D6
			xpart_times_4 = 3*(colorH-colorO);
			error = error + square_table[((block[4*4*2 + 4*3 + 0]&0xff) - clamp_table[ (((xpart_times_4 + ((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
			error = error + square_table[((block[4*4*3 + 4*3 + 0]&0xff) - clamp_table[ (((xpart_times_4 + 3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		}
	}
	return error;
}

//Calculating the minimum error for the block (in planar mode) if we know the red component for O and H.
//Uses perceptual error metrics.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcLowestPossibleGreenOHperceptual(byte[] block, int colorO, int colorH, int best_error_sofar)
{
	colorO = (colorO << 1) | (colorO >> 6);
	colorH = (colorH << 1) | (colorH >> 6);

	int error;

	error = square_table_percep_green[((block[1]&0xff) - colorO) + 255];
	error = error + square_table_percep_green[((block[4 + 1]&0xff) - clamp_table[ (((   (colorH-colorO) + 4*colorO)+2)>>2) + 255])+255];
	if(error <= best_error_sofar)
	{
		error = error + square_table_percep_green[((block[4*2 + 1]&0xff) - clamp_table[ (((  ((colorH-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table_percep_green[((block[4*3 + 1]&0xff) - clamp_table[ ((( 3*(colorH-colorO) + 4*colorO)+2)>>2) + 255])+255];
	}
	return error;
}

//Calculating the minimum error for the block (in planar mode) if we know the red component for O and H.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcLowestPossibleGreenOH(byte[] block, int colorO, int colorH, int best_error_sofar)
{
	colorO = (colorO << 1) | (colorO >> 6);
	colorH = (colorH << 1) | (colorH >> 6);

	int error;

	error = square_table[((block[1]&0xff) - colorO) + 255];
	error = error + square_table[((block[4 + 1]&0xff) - clamp_table[ (((   (colorH-colorO) + 4*colorO)+2)>>2) + 255])+255];
	if(error <= best_error_sofar)
	{
		error = error + square_table[((block[4*2 + 1]&0xff) - clamp_table[ (((  ((colorH-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table[((block[4*3 + 1]&0xff) - clamp_table[ ((( 3*(colorH-colorO) + 4*colorO)+2)>>2) + 255])+255];
	}
	return error;
}

//Calculating the minimum error for the block (in planar mode) if we know the green component for O and V.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcBBBgreen(byte[] block, int colorO, int colorV)
{
	colorO = (colorO << 1) | (colorO >> 6);
	colorV = (colorV << 1) | (colorV >> 6);

	int error = 0;
	
	// Now first column: B B B 
	/* unroll loop for( yy=0; (yy<4) && (error <= best_error_sofar); yy++)*/
	{
		error = error + square_table[((block[4*4 + 1]&0xff) - clamp_table[ ((((colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table[((block[4*4*2 + 1]&0xff) - clamp_table[ (((((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table[((block[4*4*3 + 1]&0xff) - clamp_table[ (((3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
	}

	return error;

}

//Calculating the minimum error for the block (in planar mode) if we know the green component for H and V.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcCCCgreen(byte[] block, int colorH, int colorV)
{
	colorH = (colorH << 1) | (colorH >> 6);
	colorV = (colorV << 1) | (colorV >> 6);

	int error=0;

	error = error + square_table[((block[4*4*3 + 4 + 1]&0xff) - clamp_table[ (((colorH + 3*colorV)+2)>>2) + 255])+255];
	error = error + square_table[((block[4*4*2 + 4*2 + 1]&0xff) - clamp_table[ (((2*colorH + 2*colorV)+2)>>2) + 255])+255];
	error = error + square_table[((block[4*4 + 4*3 + 1]&0xff) - clamp_table[ (((3*colorH + colorV)+2)>>2) + 255])+255];
	
	return error;
}

//Calculating the minimum error for the block (in planar mode) if we know the green component for H V and O.
//Uses perceptual error metric.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcErrorPlanarOnlyGreenPerceptual(byte[] block, int colorO, int colorH, int colorV, int lowest_possible_error, int BBBvalue, int CCCvalue, int best_error_sofar)
{
	colorO = (colorO << 1) | (colorO >> 6);
	colorH = (colorH << 1) | (colorH >> 6);
	colorV = (colorV << 1) | (colorV >> 6);

	int error;

	// The block can be partitioned into: O A  A  A
	//                                    B D1 D3 C3
	//                                    B D2 C2 D5
	//                                    B C1 D4 D6

	int xpart_times_4;

	// The first part: O A A A. It equals lowest_possible_error previously calculated. 
	// lowest_possible_error is OAAA, BBBvalue is BBB and CCCvalue is C1C2C3.
	error = lowest_possible_error + BBBvalue + CCCvalue;

	// The remaining pixels to cover are D1 through D6.
	if(error <= best_error_sofar)
	{
		// Second column: D1 D2  but not C1
		xpart_times_4 = (colorH-colorO);
		error = error + square_table_percep_green[((block[4*4 + 4 + 1]&0xff) - clamp_table[ (((xpart_times_4 + (colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table_percep_green[((block[4*4*2 + 4 + 1]&0xff) - clamp_table[ (((xpart_times_4 + ((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		// Third column: D3 notC2 D4
		xpart_times_4 = (colorH-colorO) << 1;
		error = error + square_table_percep_green[((block[4*4 + 4*2 + 1]&0xff) - clamp_table[ (((xpart_times_4 + (colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		if(error <= best_error_sofar)
		{
			error = error + square_table_percep_green[((block[4*4*3 + 4*2 + 1]&0xff) - clamp_table[ (((xpart_times_4 + 3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
			// Forth column: notC3 D5 D6
			xpart_times_4 = 3*(colorH-colorO);
			error = error + square_table_percep_green[((block[4*4*2 + 4*3 + 1]&0xff) - clamp_table[ (((xpart_times_4 + ((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
			error = error + square_table_percep_green[((block[4*4*3 + 4*3 + 1]&0xff) - clamp_table[ (((xpart_times_4 + 3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		}
	}
	return error;
}

//Calculating the minimum error for the block (in planar mode) if we know the green component for H V and O.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcErrorPlanarOnlyGreen(byte[] block, int colorO, int colorH, int colorV, int lowest_possible_error, int BBBvalue, int CCCvalue, int best_error_sofar)
{
	colorO = (colorO << 1) | (colorO >> 6);
	colorH = (colorH << 1) | (colorH >> 6);
	colorV = (colorV << 1) | (colorV >> 6);

	int error;

	// The block can be partitioned into: O A  A  A
	//                                    B D1 D3 C3
	//                                    B D2 C2 D5
	//                                    B C1 D4 D6
	int xpart_times_4;

	// The first part: O A A A. It equals lowest_possible_error previously calculated. 
	// lowest_possible_error is OAAA, BBBvalue is BBB and CCCvalue is C1C2C3.
	error = lowest_possible_error + BBBvalue + CCCvalue;

	// The remaining pixels to cover are D1 through D6.
	if(error <= best_error_sofar)
	{
		// Second column: D1 D2  but not C1
		xpart_times_4 = (colorH-colorO);
		error = error + square_table[((block[4*4 + 4 + 1]&0xff) - clamp_table[ (((xpart_times_4 + (colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table[((block[4*4*2 + 4 + 1]&0xff) - clamp_table[ (((xpart_times_4 + ((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		// Third column: D3 notC2 D4
		xpart_times_4 = (colorH-colorO) << 1;
		error = error + square_table[((block[4*4 + 4*2 + 1]&0xff) - clamp_table[ (((xpart_times_4 + (colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		if(error <= best_error_sofar)
		{
			error = error + square_table[((block[4*4*3 + 4*2 + 1]&0xff) - clamp_table[ (((xpart_times_4 + 3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
			// Forth column: notC3 D5 D6
			xpart_times_4 = 3*(colorH-colorO);
			error = error + square_table[((block[4*4*2 + 4*3 + 1]&0xff) - clamp_table[ (((xpart_times_4 + ((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
			error = error + square_table[((block[4*4*3 + 4*3 + 1]&0xff) - clamp_table[ (((xpart_times_4 + 3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		}
	}
	return error;
}

//Calculating the minimum error for the block (in planar mode) if we know the blue component for O and V.
//Uses perceptual error metric.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcBBBbluePerceptual(byte[] block, int colorO, int colorV)
{
	colorO = (colorO << 2) | (colorO >> 4);
	colorV = (colorV << 2) | (colorV >> 4);

	int error = 0;
	
	// Now first column: B B B 
	/* unroll loop for( yy=0; (yy<4) && (error <= best_error_sofar); yy++)*/
	{
		error = error + square_table_percep_blue[((block[4*4 + 2]&0xff) - clamp_table[ ((((colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table_percep_blue[((block[4*4*2 + 2]&0xff) - clamp_table[ (((((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table_percep_blue[((block[4*4*3 + 2]&0xff) - clamp_table[ (((3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
	}

	return error;
}

//Calculating the minimum error for the block (in planar mode) if we know the blue component for O and V.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcBBBblue(byte[] block, int colorO, int colorV)
{
	colorO = (colorO << 2) | (colorO >> 4);
	colorV = (colorV << 2) | (colorV >> 4);

	int error = 0;
	
	// Now first column: B B B 
	/* unroll loop for( yy=0; (yy<4) && (error <= best_error_sofar); yy++)*/
	{
		error = error + square_table[((block[4*4 + 2]&0xff) - clamp_table[ ((((colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table[((block[4*4*2 + 2]&0xff) - clamp_table[ (((((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table[((block[4*4*3 + 2]&0xff) - clamp_table[ (((3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
	}

	return error;
}

//Calculating the minimum error for the block (in planar mode) if we know the blue component for H and V.
//Uses perceptual error metric.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcCCCbluePerceptual(byte[] block, int colorH, int colorV)
{
	colorH = (colorH << 2) | (colorH >> 4);
	colorV = (colorV << 2) | (colorV >> 4);

	int error=0;

	error = error + square_table_percep_blue[((block[4*4*3 + 4 + 2]&0xff) - clamp_table[ (((colorH + 3*colorV)+2)>>2) + 255])+255];
	error = error + square_table_percep_blue[((block[4*4*2 + 4*2 + 2]&0xff) - clamp_table[ (((2*colorH + 2*colorV)+2)>>2) + 255])+255];
	error = error + square_table_percep_blue[((block[4*4 + 4*3 + 2]&0xff) - clamp_table[ (((3*colorH + colorV)+2)>>2) + 255])+255];
	
	return error;
}

//Calculating the minimum error for the block (in planar mode) if we know the blue component for O and V.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcCCCblue(byte[] block, int colorH, int colorV)
{
	colorH = (colorH << 2) | (colorH >> 4);
	colorV = (colorV << 2) | (colorV >> 4);

	int error=0;

	error = error + square_table[((block[4*4*3 + 4 + 2]&0xff) - clamp_table[ (((colorH + 3*colorV)+2)>>2) + 255])+255];
	error = error + square_table[((block[4*4*2 + 4*2 + 2]&0xff) - clamp_table[ (((2*colorH + 2*colorV)+2)>>2) + 255])+255];
	error = error + square_table[((block[4*4 + 4*3 + 2]&0xff) - clamp_table[ (((3*colorH + colorV)+2)>>2) + 255])+255];
	
	return error;
}

//Calculating the minimum error for the block (in planar mode) if we know the blue component for O and H.
//Uses perceptual error metric.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcLowestPossibleBlueOHperceptual(byte[] block, int colorO, int colorH, int best_error_sofar)
{
	colorO = (colorO << 2) | (colorO >> 4);
	colorH = (colorH << 2) | (colorH >> 4);

	int error;

	error = square_table_percep_blue[((block[2]&0xff) - colorO) + 255];
	error = error + square_table_percep_blue[((block[4+2]&0xff) - clamp_table[ (((   (colorH-colorO) + 4*colorO)+2)>>2) + 255])+255];
	if(error <= best_error_sofar)
	{
		error = error + square_table_percep_blue[((block[4*2+2]&0xff) - clamp_table[ (((  ((colorH-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table_percep_blue[((block[4*3+2]&0xff) - clamp_table[ ((( 3*(colorH-colorO) + 4*colorO)+2)>>2) + 255])+255];
	}
	
	return error;
}

//Calculating the minimum error for the block (in planar mode) if we know the blue component for O and H.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcLowestPossibleBlueOH(byte[] block, int colorO, int colorH, int best_error_sofar)
{
	colorO = (colorO << 2) | (colorO >> 4);
	colorH = (colorH << 2) | (colorH >> 4);

	int error;

	error = square_table[((block[2]&0xff) - colorO) + 255];
	error = error + square_table[((block[4+2]&0xff) - clamp_table[ (((   (colorH-colorO) + 4*colorO)+2)>>2) + 255])+255];
	if(error <= best_error_sofar)
	{
		error = error + square_table[((block[4*2+2]&0xff) - clamp_table[ (((  ((colorH-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table[((block[4*3+2]&0xff) - clamp_table[ ((( 3*(colorH-colorO) + 4*colorO)+2)>>2) + 255])+255];
	}
	
	return error;
}

//Calculating the minimum error for the block (in planar mode) if we know the blue component for O, V and H.
//Uses perceptual error metric.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcErrorPlanarOnlyBluePerceptual(byte[] block, int colorO, int colorH, int colorV, int lowest_possible_error, int BBBvalue, int CCCvalue, int best_error_sofar)
{
	colorO = (colorO << 2) | (colorO >> 4);
	colorH = (colorH << 2) | (colorH >> 4);
	colorV = (colorV << 2) | (colorV >> 4);

	int error;

	// The block can be partitioned into: O A  A  A
	//                                    B D1 D3 C3
	//                                    B D2 C2 D5
	//                                    B C1 D4 D6
	int xpart_times_4;

	// The first part: O A A A. It equals lowest_possible_error previously calculated. 
	// lowest_possible_error is OAAA, BBBvalue is BBB and CCCvalue is C1C2C3.
	error = lowest_possible_error + BBBvalue + CCCvalue;

	// The remaining pixels to cover are D1 through D6.
	if(error <= best_error_sofar)
	{
		// Second column: D1 D2  but not C1
		xpart_times_4 = (colorH-colorO);
		error = error + square_table_percep_blue[((block[4*4 + 4 + 2]&0xff) - clamp_table[ (((xpart_times_4 + (colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table_percep_blue[((block[4*4*2 + 4 + 2]&0xff) - clamp_table[ (((xpart_times_4 + ((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		// Third column: D3 notC2 D4
		xpart_times_4 = (colorH-colorO) << 1;
		error = error + square_table_percep_blue[((block[4*4 + 4*2 + 2]&0xff) - clamp_table[ (((xpart_times_4 + (colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		if(error <= best_error_sofar)
		{
			error = error + square_table_percep_blue[((block[4*4*3 + 4*2 + 2]&0xff) - clamp_table[ (((xpart_times_4 + 3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
			// Forth column: notC3 D5 D6
			xpart_times_4 = 3*(colorH-colorO);
			error = error + square_table_percep_blue[((block[4*4*2 + 4*3 + 2]&0xff) - clamp_table[ (((xpart_times_4 + ((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
			error = error + square_table_percep_blue[((block[4*4*3 + 4*3 + 2]&0xff) - clamp_table[ (((xpart_times_4 + 3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		}
	}
	
	return error;
}

//Calculating the minimum error for the block (in planar mode) if we know the blue component for O, V and H.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int calcErrorPlanarOnlyBlue(byte[] block, int colorO, int colorH, int colorV, int lowest_possible_error, int BBBvalue, int CCCvalue, int best_error_sofar)
{
	colorO = (colorO << 2) | (colorO >> 4);
	colorH = (colorH << 2) | (colorH >> 4);
	colorV = (colorV << 2) | (colorV >> 4);

	int error;

	// The block can be partitioned into: O A  A  A
	//                                    B D1 D3 C3
	//                                    B D2 C2 D5
	//                                    B C1 D4 D6
	int xpart_times_4;

	// The first part: O A A A. It equals lowest_possible_error previously calculated. 
	// lowest_possible_error is OAAA, BBBvalue is BBB and CCCvalue is C1C2C3.
	error = lowest_possible_error + BBBvalue + CCCvalue;

	// The remaining pixels to cover are D1 through D6.
	if(error <= best_error_sofar)
	{
		// Second column: D1 D2  but not C1
		xpart_times_4 = (colorH-colorO);
		error = error + square_table[((block[4*4 + 4 + 2]&0xff) - clamp_table[ (((xpart_times_4 + (colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		error = error + square_table[((block[4*4*2 + 4 + 2]&0xff) - clamp_table[ (((xpart_times_4 + ((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
		// Third column: D3 notC2 D4
		xpart_times_4 = (colorH-colorO) << 1;
		error = error + square_table[((block[4*4 + 4*2 + 2]&0xff) - clamp_table[ (((xpart_times_4 + (colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		if(error <= best_error_sofar)
		{
			error = error + square_table[((block[4*4*3 + 4*2 + 2]&0xff) - clamp_table[ (((xpart_times_4 + 3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
			// Forth column: notC3 D5 D6
			xpart_times_4 = 3*(colorH-colorO);
			error = error + square_table[((block[4*4*2 + 4*3 + 2]&0xff) - clamp_table[ (((xpart_times_4 + ((colorV-colorO)<<1) + 4*colorO)+2)>>2) + 255])+255];
			error = error + square_table[((block[4*4*3 + 4*3 + 2]&0xff) - clamp_table[ (((xpart_times_4 + 3*(colorV-colorO) + 4*colorO)+2)>>2) + 255])+255];
		}
	}

	return error;
}

static final double[] coeffsA= new double[] { 1.00, 0.00, 0.00, 
    0.75, 0.25, 0.00,
	0.50, 0.50, 0.00, 
	0.25, 0.75, 0.00, 
    0.75, 0.00, 0.25, 
    0.50, 0.25, 0.25,
	0.25, 0.50, 0.25, 
	0.00, 0.75, 0.25,
	0.50, 0.00, 0.50, 
    0.25, 0.25, 0.50,
	0.00, 0.50, 0.50, 
	-0.25, 0.75, 0.50, 
	0.25, 0.00, 0.75, 
    0.00, 0.25, 0.75,
	-0.25, 0.50, 0.75, 
	-0.50, 0.75, 0.75};

static final double[] coeffsC= new double[]  {0.2875, -0.0125, -0.0125, -0.0125, 0.4875, -0.3125, -0.0125, -0.3125, 0.4875};

//This function uses least squares in order to determine the best values of the plane. 
//This is close to optimal, but not quite, due to nonlinearities in the expansion from 6 and 7 bits to 8, and
//in the clamping to a number between 0 and the maximum. 
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &compressed57_1,  int &compressed57_2
static void compressBlockPlanar57(byte[] img, int width,int height,int startx,int starty,  int[] compressed57_1,  int[] compressed57_2)
{
	// Use least squares to find the solution with the smallest error.
	// That is, find the vector x so that |Ax-b|^2 is minimized, where
	// x = [Ro Rr Rv]';
	// A = [1 3/4 2/4 1/4 3/4 2/4 1/4  0  2/4 1/4  0  -1/4  1/4  0  -1/4 -2/4 ; 
   //      0 1/4 2/4 3/4  0  1/4 2/4 3/4  0  1/4 2/4  3/4   0  1/4  2/4  3/4 ;
	//      0  0   0   0  1/4 1/4 1/4 1/4 2/4 2/4 2/4  2/4; 3/4 3/4  3/4  3/4]';
	// b = [r11 r12 r13 r14 r21 r22 r23 r24 r31 r32 r33 r34 r41 r42 r43 r44];
	//
	// That is, find solution x = inv(A' * A) * A' * b
	//                          = C * A' * b;
	// C is always the same, so we have calculated it off-line here.
	//                          = C * D
	int xx,yy, cc;

	double[] colorO= new double[3], colorH= new double[3], colorV= new double[3];
	byte[] colorO8= new byte[3], colorH8= new byte[3], colorV8= new byte[3];
	
	dMatrix D_matrix = new dMatrix();
	dMatrix x_vector = new dMatrix();

	dMatrix A_matrix = new dMatrix(); A_matrix.width = 3; A_matrix.height = 16; 
	A_matrix.data = coeffsA;
	dMatrix C_matrix = new dMatrix(); C_matrix.width = 3; C_matrix.height = 3; 
	C_matrix.data = coeffsC;
	dMatrix b_vector = new dMatrix(); b_vector.width = 1; b_vector.height = 16; 
    b_vector.data = new double[b_vector.width*b_vector.height];
	transposeMatrix(A_matrix);

	// Red component

	// Load color data into vector b:
	for(cc = 0, yy = 0; yy<4; yy++)
	   for(xx = 0; xx<4; xx++)
		   b_vector.data[cc++] = img[3*width*(starty+yy) + 3*(startx+xx) + 0]&0xff;

	D_matrix = multiplyMatrices(A_matrix, b_vector);
	x_vector = multiplyMatrices(C_matrix, D_matrix);

	colorO[0] = CLAMP(0.0, x_vector.data[0], 255.0);
	colorH[0] = CLAMP(0.0, x_vector.data[1], 255.0);
	colorV[0] = CLAMP(0.0, x_vector.data[2], 255.0);

	//free(D_matrix.data); free(D_matrix);
	//free(x_vector.data); free(x_vector);

	// Green component

	// Load color data into vector b:
	for(cc = 0, yy = 0; yy<4; yy++)
	   for(xx = 0; xx<4; xx++)
		   b_vector.data[cc++] = img[3*width*(starty+yy) + 3*(startx+xx) + 1]&0xff;

	D_matrix = multiplyMatrices(A_matrix, b_vector);
	x_vector = multiplyMatrices(C_matrix, D_matrix);

	colorO[1] = CLAMP(0.0, x_vector.data[0], 255.0);
	colorH[1] = CLAMP(0.0, x_vector.data[1], 255.0);
	colorV[1] = CLAMP(0.0, x_vector.data[2], 255.0);

	//free(D_matrix->data); free(D_matrix);
	//free(x_vector->data); free(x_vector);

	// Blue component

	// Load color data into vector b:
	for(cc = 0, yy = 0; yy<4; yy++)
	   for(xx = 0; xx<4; xx++)
		   b_vector.data[cc++] = img[3*width*(starty+yy) + 3*(startx+xx) + 2]&0xff;

	D_matrix = multiplyMatrices(A_matrix, b_vector);
	x_vector = multiplyMatrices(C_matrix, D_matrix);

	colorO[2] = CLAMP(0.0, x_vector.data[0], 255.0);
	colorH[2] = CLAMP(0.0, x_vector.data[1], 255.0);
	colorV[2] = CLAMP(0.0, x_vector.data[2], 255.0);

	//free(D_matrix->data); free(D_matrix);
	//free(x_vector->data); free(x_vector);

	// Quantize to 6 bits
	double D = 255*(1.0/((1<<6)-1.0) );
	colorO8[0] = (byte)JAS_ROUND((1.0*colorO[0])/D);
	colorO8[2] = (byte)JAS_ROUND((1.0*colorO[2])/D);
	colorH8[0] = (byte)JAS_ROUND((1.0*colorH[0])/D);
	colorH8[2] = (byte)JAS_ROUND((1.0*colorH[2])/D);
	colorV8[0] = (byte)JAS_ROUND((1.0*colorV[0])/D);
	colorV8[2] = (byte)JAS_ROUND((1.0*colorV[2])/D);

	// Quantize to 7 bits
	D = 255*(1.0/((1<<7)-1.0) );
	colorO8[1] = (byte)JAS_ROUND((1.0*colorO[1])/D);
	colorH8[1] = (byte)JAS_ROUND((1.0*colorH[1])/D);
	colorV8[1] = (byte)JAS_ROUND((1.0*colorV[1])/D);

	// Pack bits in 57 bits

	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34 33 32 
	//      ------------------------------------------------------------------------------------------------
	//     | R0              | G0                 | B0              | RH              | GH                  |
	//      ------------------------------------------------------------------------------------------------
	//
	//      31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3  2  1  0
	//      ------------------------------------------------------------------------------------------------
	//     | BH              | RV              |  GV                | BV               | not used           |   
	//      ------------------------------------------------------------------------------------------------

	compressed57_1[0] = 0;
	compressed57_2[0] = 0;
	PUTBITSHIGH( compressed57_1, colorO8[0], 6, 63);
	PUTBITSHIGH( compressed57_1, colorO8[1], 7, 57);
	PUTBITSHIGH( compressed57_1, colorO8[2], 6, 50);
	PUTBITSHIGH( compressed57_1, colorH8[0], 6, 44);
	PUTBITSHIGH( compressed57_1, colorH8[1], 7, 38);
	PUTBITS(     compressed57_2, colorH8[2], 6, 31);
	PUTBITS(     compressed57_2, colorV8[0], 6, 25);
	PUTBITS(     compressed57_2, colorV8[1], 7, 19);
	PUTBITS(     compressed57_2, colorV8[2], 6, 12);
}

//During search it is not convenient to store the bits the way they are stored in the 
//file format. Hence, after search, it is converted to this format.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address  int &planar_word1,  int &planar_word2
static void stuff57bits(int planar57_word1, int planar57_word2,  int[] planar_word1,  int[] planar_word2)
{
	// Put bits in twotimer configuration for 57 bits (red and green dont overflow, blue does)
	// 
	// Go from this bit layout:
	//
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34 33 32 
	//      -----------------------------------------------------------------------------------------------
	//     |R0               |G01G02              |B01B02  ;B03     |RH1           |RH2|GH                 |
	//      -----------------------------------------------------------------------------------------------
	//
	//      31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3  2  1  0
	//      -----------------------------------------------------------------------------------------------
	//     |BH               |RV               |GV                  |BV                | not used          |   
	//      -----------------------------------------------------------------------------------------------
	//
	//  To this:
	// 
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34 33 32 
	//      ------------------------------------------------------------------------------------------------
	//     |//|R0               |G01|/|G02              |B01|/ // //|B02  |//|B03     |RH1           |df|RH2|
	//      ------------------------------------------------------------------------------------------------
	//
	//      31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3  2  1  0
	//      -----------------------------------------------------------------------------------------------
	//     |GH                  |BH               |RV               |GV                   |BV              |
	//      -----------------------------------------------------------------------------------------------
	//
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
	//      ---------------------------------------------------------------------------------------------------
	//     | base col1    | dcol 2 | base col1    | dcol 2 | base col 1   | dcol 2 | table  | table  |diff|flip|
	//     | R1' (5 bits) | dR2    | G1' (5 bits) | dG2    | B1' (5 bits) | dB2    | cw 1   | cw 2   |bit |bit |
	//      ---------------------------------------------------------------------------------------------------

	byte RO, GO1, GO2, BO1, BO2, BO3, RH1, RH2, GH, BH, RV, GV, BV;
	byte bit, a, b, c, d, bits;

	RO = (byte)GETBITSHIGH( planar57_word1, 6, 63);
	GO1= (byte)GETBITSHIGH( planar57_word1, 1, 57);
	GO2= (byte)GETBITSHIGH( planar57_word1, 6, 56);
	BO1= (byte)GETBITSHIGH( planar57_word1, 1, 50);
	BO2= (byte)GETBITSHIGH( planar57_word1, 2, 49);
	BO3= (byte)GETBITSHIGH( planar57_word1, 3, 47);
	RH1= (byte)GETBITSHIGH( planar57_word1, 5, 44);
	RH2= (byte)GETBITSHIGH( planar57_word1, 1, 39);
	GH = (byte)GETBITSHIGH( planar57_word1, 7, 38);
	BH = (byte)GETBITS(     planar57_word2, 6, 31);
	RV = (byte)GETBITS(     planar57_word2, 6, 25);
	GV = (byte)GETBITS(     planar57_word2, 7, 19);
	BV = (byte)GETBITS(     planar57_word2, 6, 12);

	planar_word1[0] = 0; planar_word2[0] = 0;
	PUTBITSHIGH( planar_word1, RO,  6, 62);
	PUTBITSHIGH( planar_word1, GO1, 1, 56);
	PUTBITSHIGH( planar_word1, GO2, 6, 54);
	PUTBITSHIGH( planar_word1, BO1, 1, 48);
	PUTBITSHIGH( planar_word1, BO2, 2, 44);
	PUTBITSHIGH( planar_word1, BO3, 3, 41);
	PUTBITSHIGH( planar_word1, RH1, 5, 38);
	PUTBITSHIGH( planar_word1, RH2, 1, 32);
	PUTBITS(     planar_word2, GH,  7, 31);
	PUTBITS(     planar_word2, BH,  6, 24);
	PUTBITS(     planar_word2, RV,  6, 18);
	PUTBITS(     planar_word2, GV,  7, 12);
	PUTBITS(     planar_word2, BV,  6,  5);

	// Make sure that red does not overflow:
	bit = (byte)GETBITSHIGH( planar_word1[0], 1, 62);
	PUTBITSHIGH( planar_word1, (bit==0?1:0),  1, 63);

	// Make sure that green does not overflow:
	bit = (byte)GETBITSHIGH( planar_word1[0], 1, 54);
	PUTBITSHIGH( planar_word1, (bit==0?1:0),  1, 55);

	// Make sure that blue overflows:
	a = (byte)GETBITSHIGH( planar_word1[0], 1, 44);
	b = (byte)GETBITSHIGH( planar_word1[0], 1, 43);
	c = (byte)GETBITSHIGH( planar_word1[0], 1, 41);
	d = (byte)GETBITSHIGH( planar_word1[0], 1, 40);
	// The following bit abcd bit sequences should be padded with ones: 0111, 1010, 1011, 1101, 1110, 1111
	// The following logical expression checks for the presence of any of those:
	bit = (byte)((a & c) | ((a==0?1:0) & b & c & d) | (a & b & (c==0?1:0) & d));
	bits = (byte)(0xf*bit);
	PUTBITSHIGH( planar_word1, bits,  3, 47);
	PUTBITSHIGH( planar_word1, (bit==0?1:0),  1, 42);

	// Set diffbit
	PUTBITSHIGH( planar_word1, 1,  1, 33);
}

//During search it is not convenient to store the bits the way they are stored in the 
//file format. Hence, after search, it is converted to this format.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address unsigned int &thumbH_word1, unsigned int &thumbH_word2
static void stuff58bits(int thumbH58_word1, int thumbH58_word2, int[] thumbH_word1, int[] thumbH_word2)
{
	// Put bits in twotimer configuration for 58 (red doesn't overflow, green does)
	// 
	// Go from this bit layout:
	//
	//
	//     |63 62 61 60 59 58|57 56 55 54|53 52 51 50|49 48 47 46|45 44 43 42|41 40 39 38|37 36 35 34|33 32|
	//     |-------empty-----|---red 0---|--green 0--|--blue 0---|---red 1---|--green 1--|--blue 1---|d2 d1|
	//
	//     |31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
	//     |---------------------------------------index bits----------------------------------------------|
	//
	//  To this:
	// 
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34 33 32 
	//      -----------------------------------------------------------------------------------------------
	//     |//|R0         |G0      |// // //|G0|B0|//|B0      |R1         |G1         |B1         |d2|df|d1|
	//      -----------------------------------------------------------------------------------------------
	//
	//     |31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
	//     |---------------------------------------index bits----------------------------------------------|
	//
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34 33 32 
	//      -----------------------------------------------------------------------------------------------
	//     | base col1    | dcol 2 | base col1    | dcol 2 | base col 1   | dcol 2 | table  | table  |df|fp|
	//     | R1' (5 bits) | dR2    | G1' (5 bits) | dG2    | B1' (5 bits) | dB2    | cw 1   | cw 2   |bt|bt|
	//      -----------------------------------------------------------------------------------------------
	//
	//
	// Thus, what we are really doing is going from this bit layout:
	//
	//
	//     |63 62 61 60 59 58|57 56 55 54 53 52 51|50 49|48 47 46 45 44 43 42 41 40 39 38 37 36 35 34 33|32   |
	//     |-------empty-----|part0---------------|part1|part2------------------------------------------|part3|
	//
	//  To this:
	// 
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34 33 32 
	//      --------------------------------------------------------------------------------------------------|
	//     |//|part0               |// // //|part1|//|part2                                          |df|part3|
	//      --------------------------------------------------------------------------------------------------|

	int part0, part1, part2, part3;
	byte bit, a, b, c, d, bits;

	// move parts
	part0 = GETBITSHIGH( thumbH58_word1, 7, 57);
	part1 = GETBITSHIGH( thumbH58_word1, 2, 50);
	part2 = GETBITSHIGH( thumbH58_word1,16, 48);
	part3 = GETBITSHIGH( thumbH58_word1, 1, 32);
	thumbH_word1[0] = 0;
	PUTBITSHIGH( thumbH_word1, part0,  7, 62);
	PUTBITSHIGH( thumbH_word1, part1,  2, 52);
	PUTBITSHIGH( thumbH_word1, part2, 16, 49);
	PUTBITSHIGH( thumbH_word1, part3,  1, 32);

	// Make sure that red does not overflow:
	bit = (byte)GETBITSHIGH( thumbH_word1[0], 1, 62);
	PUTBITSHIGH( thumbH_word1, (bit==0?1:0),  1, 63);

	// Make sure that green overflows:
	a = (byte)GETBITSHIGH( thumbH_word1[0], 1, 52);
	b = (byte)GETBITSHIGH( thumbH_word1[0], 1, 51);
	c = (byte)GETBITSHIGH( thumbH_word1[0], 1, 49);
	d = (byte)GETBITSHIGH( thumbH_word1[0], 1, 48);
	// The following bit abcd bit sequences should be padded with ones: 0111, 1010, 1011, 1101, 1110, 1111
	// The following logical expression checks for the presence of any of those:
	bit = (byte)((a & c) | ((a==0?1:0) & b & c & d) | (a & b & (c==0?1:0) & d));
	bits = (byte)(0xf*bit);
	PUTBITSHIGH( thumbH_word1, bits,  3, 55);
	PUTBITSHIGH( thumbH_word1, (bit==0?1:0),  1, 50);

	// Set diffbit
	PUTBITSHIGH( thumbH_word1, 1,  1, 33);
	thumbH_word2[0] = thumbH58_word2;

}

//copy of above, but diffbit is 0
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address unsigned int &thumbH_word1, unsigned int &thumbH_word2
static void stuff58bitsDiffFalse(int thumbH58_word1, int thumbH58_word2, int[] thumbH_word1, int[] thumbH_word2)
{
	int part0, part1, part2, part3;
	byte bit, a, b, c, d, bits;

	// move parts
	part0 = GETBITSHIGH( thumbH58_word1, 7, 57);
	part1 = GETBITSHIGH( thumbH58_word1, 2, 50);
	part2 = GETBITSHIGH( thumbH58_word1,16, 48);
	part3 = GETBITSHIGH( thumbH58_word1, 1, 32);
	thumbH_word1[0] = 0;
	PUTBITSHIGH( thumbH_word1, part0,  7, 62);
	PUTBITSHIGH( thumbH_word1, part1,  2, 52);
	PUTBITSHIGH( thumbH_word1, part2, 16, 49);
	PUTBITSHIGH( thumbH_word1, part3,  1, 32);

	// Make sure that red does not overflow:
	bit = (byte)GETBITSHIGH( thumbH_word1[0], 1, 62);
	PUTBITSHIGH( thumbH_word1, (bit==0?1:0),  1, 63);

	// Make sure that green overflows:
	a = (byte)GETBITSHIGH( thumbH_word1[0], 1, 52);
	b = (byte)GETBITSHIGH( thumbH_word1[0], 1, 51);
	c = (byte)GETBITSHIGH( thumbH_word1[0], 1, 49);
	d = (byte)GETBITSHIGH( thumbH_word1[0], 1, 48);
	// The following bit abcd bit sequences should be padded with ones: 0111, 1010, 1011, 1101, 1110, 1111
	// The following logical expression checks for the presence of any of those:
	bit = (byte)((a & c) | ((a==0?1:0) & b & c & d) | (a & b & (c==0?1:0) & d));
	bits = (byte)(0xf*bit);
	PUTBITSHIGH( thumbH_word1, bits,  3, 55);
	PUTBITSHIGH( thumbH_word1, (bit==0?1:0),  1, 50);

	// Set diffbit
	PUTBITSHIGH( thumbH_word1, 0,  1, 33);
	thumbH_word2[0] = thumbH58_word2;

}

//During search it is not convenient to store the bits the way they are stored in the 
//file format. Hence, after search, it is converted to this format.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address unsigned int &thumbT_word1, unsigned int &thumbT_word2
static void stuff59bits(int thumbT59_word1, int thumbT59_word2, int[] thumbT_word1, int[] thumbT_word2)
{
	// Put bits in twotimer configuration for 59 (red overflows)
	// 
	// Go from this bit layout:
	//
	//     |63 62 61 60 59|58 57 56 55|54 53 52 51|50 49 48 47|46 45 44 43|42 41 40 39|38 37 36 35|34 33 32|
	//     |----empty-----|---red 0---|--green 0--|--blue 0---|---red 1---|--green 1--|--blue 1---|--dist--|
	//
	//     |31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
	//     |----------------------------------------index bits---------------------------------------------|
	//
	//
	//  To this:
	// 
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34 33 32 
	//      -----------------------------------------------------------------------------------------------
	//     |// // //|R0a  |//|R0b  |G0         |B0         |R1         |G1         |B1          |da  |df|db|
	//      -----------------------------------------------------------------------------------------------
	//
	//     |31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
	//     |----------------------------------------index bits---------------------------------------------|
	//
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34 33 32 
	//      -----------------------------------------------------------------------------------------------
	//     | base col1    | dcol 2 | base col1    | dcol 2 | base col 1   | dcol 2 | table  | table  |df|fp|
	//     | R1' (5 bits) | dR2    | G1' (5 bits) | dG2    | B1' (5 bits) | dB2    | cw 1   | cw 2   |bt|bt|
	//      ------------------------------------------------------------------------------------------------

	byte R0a;
	byte bit, a, b, c, d, bits;

	R0a = (byte)GETBITSHIGH( thumbT59_word1, 2, 58);

	// Fix middle part
	thumbT_word1[0] = thumbT59_word1 << 1;
	// Fix R0a (top two bits of R0)
	PUTBITSHIGH( thumbT_word1, R0a,  2, 60);
	// Fix db (lowest bit of d)
	PUTBITSHIGH( thumbT_word1, thumbT59_word1,  1, 32);
	// 
	// Make sure that red overflows:
	a = (byte)GETBITSHIGH( thumbT_word1[0], 1, 60);
	b = (byte)GETBITSHIGH( thumbT_word1[0], 1, 59);
	c = (byte)GETBITSHIGH( thumbT_word1[0], 1, 57);
	d = (byte)GETBITSHIGH( thumbT_word1[0], 1, 56);
	// The following bit abcd bit sequences should be padded with ones: 0111, 1010, 1011, 1101, 1110, 1111
	// The following logical expression checks for the presence of any of those:
	bit = (byte)((a & c) | ((a==0?1:0) & b & c & d) | (a & b & (c==0?1:0) & d));
	bits = (byte)(0xf*bit);
	PUTBITSHIGH( thumbT_word1, bits,  3, 63);
	PUTBITSHIGH( thumbT_word1, (bit==0?1:0),  1, 58);

	// Set diffbit
	PUTBITSHIGH( thumbT_word1, 1,  1, 33);
	thumbT_word2[0] = thumbT59_word2;
}


//Decompress the planar mode and calculate the error per component compared to original image.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &error_red, int &error_green, int &error_blue
static void decompressBlockPlanar57errorPerComponent(int compressed57_1, int compressed57_2, byte[] img,int width,int height,int startx,int starty, byte[] srcimg,  int[] error_red, int[] error_green, int[] error_blue)
{
	byte[] colorO =new byte[3], colorH =new byte[3], colorV =new byte[3];

	colorO[0] = (byte)GETBITSHIGH( compressed57_1, 6, 63);
	colorO[1] = (byte)GETBITSHIGH( compressed57_1, 7, 57);
	colorO[2] = (byte)GETBITSHIGH( compressed57_1, 6, 50);
	colorH[0] = (byte)GETBITSHIGH( compressed57_1, 6, 44);
	colorH[1] = (byte)GETBITSHIGH( compressed57_1, 7, 38);
	colorH[2] = (byte)GETBITS(     compressed57_2, 6, 31);
	colorV[0] = (byte)GETBITS(     compressed57_2, 6, 25);
	colorV[1] = (byte)GETBITS(     compressed57_2, 7, 19);
	colorV[2] = (byte)GETBITS(     compressed57_2, 6, 12);

	colorO[0] = (byte)((colorO[0] << 2) | (colorO[0] >> 4));
	colorO[1] = (byte)((colorO[1] << 1) | (colorO[1] >> 6));
	colorO[2] = (byte)((colorO[2] << 2) | (colorO[2] >> 4));

	colorH[0] = (byte)((colorH[0] << 2) | (colorH[0] >> 4));
	colorH[1] = (byte)((colorH[1] << 1) | (colorH[1] >> 6));
	colorH[2] = (byte)((colorH[2] << 2) | (colorH[2] >> 4));

	colorV[0] = (byte)((colorV[0] << 2) | (colorV[0] >> 4));
	colorV[1] = (byte)((colorV[1] << 1) | (colorV[1] >> 6));
	colorV[2] = (byte)((colorV[2] << 2) | (colorV[2] >> 4));
	
	int xx, yy;

	for( xx=0; xx<4; xx++)
	{
		for( yy=0; yy<4; yy++)
		{
			img[3*width*(starty+yy) + 3*(startx+xx) + 0] = (byte)CLAMP(0, JAS_ROUND((xx*((colorH[0]&0xff)-(colorO[0]&0xff))/4.0 + yy*((colorV[0]&0xff)-(colorO[0]&0xff))/4.0 + (colorO[0]&0xff))), 255);
			img[3*width*(starty+yy) + 3*(startx+xx) + 1] = (byte)CLAMP(0, JAS_ROUND((xx*((colorH[1]&0xff)-(colorO[1]&0xff))/4.0 + yy*((colorV[1]&0xff)-(colorO[1]&0xff))/4.0 + (colorO[1]&0xff))), 255);
			img[3*width*(starty+yy) + 3*(startx+xx) + 2] = (byte)CLAMP(0, JAS_ROUND((xx*((colorH[2]&0xff)-(colorO[2]&0xff))/4.0 + yy*((colorV[2]&0xff)-(colorO[2]&0xff))/4.0 + (colorO[2]&0xff))), 255);
		}
	}

	error_red[0] = 0;
	error_green[0]= 0;
	error_blue[0] = 0;
	for( xx=0; xx<4; xx++)
	{
		for( yy=0; yy<4; yy++)
		{
			error_red[0] = error_red[0] + SQUARE((srcimg[3*width*(starty+yy) + 3*(startx+xx) + 0]&0xff) - (img[3*width*(starty+yy) + 3*(startx+xx) + 0]&0xff)); 
			error_green[0] = error_green[0] + SQUARE((srcimg[3*width*(starty+yy) + 3*(startx+xx) + 1]&0xff) - (img[3*width*(starty+yy) + 3*(startx+xx) + 1]&0xff));
			error_blue[0] = error_blue[0] + SQUARE((srcimg[3*width*(starty+yy) + 3*(startx+xx) + 2]&0xff) - (img[3*width*(starty+yy) + 3*(startx+xx) + 2]&0xff));

		}
	}
}

//Compress using both individual and differential mode in ETC1/ETC2 using combined color 
//quantization. Both flip modes are tried. 
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &compressed1, int &compressed2
static void compressBlockDiffFlipCombined(byte[] img,int width,int height,int startx,int starty, int[] compressed1, int[] compressed2)
{
	int[] compressed1_norm=new int[1], compressed2_norm=new int[1];
	int[] compressed1_flip=new int[1], compressed2_flip=new int[1];
	byte[] avg_color_quant1 =new byte[3], avg_color_quant2 =new byte[3];

	float[] avg_color_float1 =new float[3],avg_color_float2 =new float[3];
	int[] enc_color1 =new int[3], enc_color2 =new int[3], diff =new int[3];
	int min_error=255*255*8*3;
	int best_table_indices1=0, best_table_indices2=0;
	int[] best_table1=new int[1], best_table2=new int[1];
	int diffbit;

	int norm_err=0;
	int flip_err=0;

	// First try normal blocks 2x4:

	computeAverageColor2x4noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor2x4noQuantFloat(img,width,height,startx+2,starty,avg_color_float2);

	// First test if avg_color1 is similar enough to avg_color2 so that
	// we can use differential coding of colors. 

	float eps;

	float[] dummy =new float[3];

	quantize555ColorCombined(avg_color_float1, enc_color1, dummy);
	quantize555ColorCombined(avg_color_float2, enc_color2, dummy);

	diff[0] = enc_color2[0]-enc_color1[0];	
	diff[1] = enc_color2[1]-enc_color1[1];	
	diff[2] = enc_color2[2]-enc_color1[2];

	if( (diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4) && (diff[2] <= 3) )
	{
		diffbit = 1;

		// The difference to be coded:

		diff[0] = enc_color2[0]-enc_color1[0];	
		diff[1] = enc_color2[1]-enc_color1[1];	
		diff[2] = enc_color2[2]-enc_color1[2];

		avg_color_quant1[0] = (byte)(enc_color1[0] << 3 | (enc_color1[0] >> 2));
		avg_color_quant1[1] = (byte)(enc_color1[1] << 3 | (enc_color1[1] >> 2));
		avg_color_quant1[2] = (byte)(enc_color1[2] << 3 | (enc_color1[2] >> 2));
		avg_color_quant2[0] = (byte)(enc_color2[0] << 3 | (enc_color2[0] >> 2));
		avg_color_quant2[1] = (byte)(enc_color2[1] << 3 | (enc_color2[1] >> 2));
		avg_color_quant2[2] = (byte)(enc_color2[2] << 3 | (enc_color2[2] >> 2));

		// Pack bits into the first word. 

		//     ETC1_RGB8_OES:
		// 
		//     a) bit layout in bits 63 through 32 if diffbit = 0
		// 
		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
		//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------
		//     
		//     b) bit layout in bits 63 through 32 if diffbit = 1
		// 
		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1    | dcol 2 | base col1    | dcol 2 | base col 1   | dcol 2 | table  | table  |diff|flip|
		//     | R1' (5 bits) | dR2    | G1' (5 bits) | dG2    | B1' (5 bits) | dB2    | cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------
		// 
		//     c) bit layout in bits 31 through 0 (in both cases)
		// 
		//      31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3   2   1  0
		//      --------------------------------------------------------------------------------------------------
		//     |       most significant pixel index bits       |         least significant pixel index bits       |  
		//     | p| o| n| m| l| k| j| i| h| g| f| e| d| c| b| a| p| o| n| m| l| k| j| i| h| g| f| e| d| c | b | a |
		//      --------------------------------------------------------------------------------------------------      

		compressed1_norm[0] = 0;
		PUTBITSHIGH( compressed1_norm, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_norm, enc_color1[0], 5, 63);
		PUTBITSHIGH( compressed1_norm, enc_color1[1], 5, 55);
		PUTBITSHIGH( compressed1_norm, enc_color1[2], 5, 47);
		PUTBITSHIGH( compressed1_norm, diff[0],       3, 58);
		PUTBITSHIGH( compressed1_norm, diff[1],       3, 50);
		PUTBITSHIGH( compressed1_norm, diff[2],       3, 42);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];

		norm_err = 0;

		// left part of block
		norm_err = tryalltables_3bittable2x4(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);

		// right part of block
		norm_err += tryalltables_3bittable2x4(img,width,height,startx+2,starty,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_norm, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_norm, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_norm,           0,   1, 32);

		compressed2_norm[0] = 0;
		PUTBITS( compressed2_norm, (best_pixel_indices1_MSB[0]     ), 8, 23);
		PUTBITS( compressed2_norm, (best_pixel_indices2_MSB[0]     ), 8, 31);
		PUTBITS( compressed2_norm, (best_pixel_indices1_LSB[0]     ), 8, 7);
		PUTBITS( compressed2_norm, (best_pixel_indices2_LSB[0]     ), 8, 15);

	}
	else
	{
		diffbit = 0;
		// The difference is bigger than what fits in 555 plus delta-333, so we will have
		// to deal with 444 444.

		eps = (float) 0.0001;

		byte[] dummy2 =new byte[3];
		quantize444ColorCombined(avg_color_float1, enc_color1, dummy2);
		quantize444ColorCombined(avg_color_float2, enc_color2, dummy2);

		avg_color_quant1[0] = (byte)(enc_color1[0] << 4 | enc_color1[0]); 
		avg_color_quant1[1] = (byte)(enc_color1[1] << 4 | enc_color1[1]); 
		avg_color_quant1[2] = (byte)(enc_color1[2] << 4 | enc_color1[2]);
		avg_color_quant2[0] = (byte)(enc_color2[0] << 4 | enc_color2[0]); 
		avg_color_quant2[1] = (byte)(enc_color2[1] << 4 | enc_color2[1]); 
		avg_color_quant2[2] = (byte)(enc_color2[2] << 4 | enc_color2[2]);
	

		// Pack bits into the first word. 

		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
		//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------

		compressed1_norm[0] = 0;
		PUTBITSHIGH( compressed1_norm, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_norm, enc_color1[0], 4, 63);
		PUTBITSHIGH( compressed1_norm, enc_color1[1], 4, 55);
		PUTBITSHIGH( compressed1_norm, enc_color1[2], 4, 47);
		PUTBITSHIGH( compressed1_norm, enc_color2[0], 4, 59);
		PUTBITSHIGH( compressed1_norm, enc_color2[1], 4, 51);
		PUTBITSHIGH( compressed1_norm, enc_color2[2], 4, 43);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];

		// left part of block
		norm_err = tryalltables_3bittable2x4(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);

		// right part of block
		norm_err += tryalltables_3bittable2x4(img,width,height,startx+2,starty,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_norm, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_norm, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_norm,           0,   1, 32);

		compressed2_norm[0] = 0;
		PUTBITS( compressed2_norm, (best_pixel_indices1_MSB[0]     ), 8, 23);
		PUTBITS( compressed2_norm, (best_pixel_indices2_MSB[0]     ), 8, 31);
		PUTBITS( compressed2_norm, (best_pixel_indices1_LSB[0]     ), 8, 7);
		PUTBITS( compressed2_norm, (best_pixel_indices2_LSB[0]     ), 8, 15);
	}

	// Now try flipped blocks 4x2:

	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty+2,avg_color_float2);

	// First test if avg_color1 is similar enough to avg_color2 so that
	// we can use differential coding of colors. 

	quantize555ColorCombined(avg_color_float1, enc_color1, dummy);
	quantize555ColorCombined(avg_color_float2, enc_color2, dummy);

	diff[0] = enc_color2[0]-enc_color1[0];	
	diff[1] = enc_color2[1]-enc_color1[1];	
	diff[2] = enc_color2[2]-enc_color1[2];

	if( (diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4) && (diff[2] <= 3) )
	{
		diffbit = 1;

		// The difference to be coded:

		diff[0] = enc_color2[0]-enc_color1[0];	
		diff[1] = enc_color2[1]-enc_color1[1];	
		diff[2] = enc_color2[2]-enc_color1[2];

		avg_color_quant1[0] = (byte)(enc_color1[0] << 3 | (enc_color1[0] >> 2));
		avg_color_quant1[1] = (byte)(enc_color1[1] << 3 | (enc_color1[1] >> 2));
		avg_color_quant1[2] = (byte)(enc_color1[2] << 3 | (enc_color1[2] >> 2));
		avg_color_quant2[0] = (byte)(enc_color2[0] << 3 | (enc_color2[0] >> 2));
		avg_color_quant2[1] = (byte)(enc_color2[1] << 3 | (enc_color2[1] >> 2));
		avg_color_quant2[2] = (byte)(enc_color2[2] << 3 | (enc_color2[2] >> 2));

		// Pack bits into the first word. 

		compressed1_flip[0] = 0;
		PUTBITSHIGH( compressed1_flip, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_flip, enc_color1[0], 5, 63);
		PUTBITSHIGH( compressed1_flip, enc_color1[1], 5, 55);
		PUTBITSHIGH( compressed1_flip, enc_color1[2], 5, 47);
		PUTBITSHIGH( compressed1_flip, diff[0],       3, 58);
		PUTBITSHIGH( compressed1_flip, diff[1],       3, 50);
		PUTBITSHIGH( compressed1_flip, diff[2],       3, 42);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];

		// upper part of block
		flip_err = tryalltables_3bittable4x2(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);
		// lower part of block
		flip_err += tryalltables_3bittable4x2(img,width,height,startx,starty+2,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_flip, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_flip, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_flip,           1,   1, 32);

		best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
		best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);
		
		compressed2_flip[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16) | (best_pixel_indices1_LSB[0] & 0xffff);
	}
	else
	{
		diffbit = 0;
		// The difference is bigger than what fits in 555 plus delta-333, so we will have
		// to deal with 444 444.
		eps = (float) 0.0001;

		byte[] dummy2 =new byte[3];
		quantize444ColorCombined(avg_color_float1, enc_color1, dummy2);
		quantize444ColorCombined(avg_color_float2, enc_color2, dummy2);

		avg_color_quant1[0] = (byte)(enc_color1[0] << 4 | enc_color1[0]); 
		avg_color_quant1[1] = (byte)(enc_color1[1] << 4 | enc_color1[1]); 
		avg_color_quant1[2] = (byte)(enc_color1[2] << 4 | enc_color1[2]);
		avg_color_quant2[0] = (byte)(enc_color2[0] << 4 | enc_color2[0]); 
		avg_color_quant2[1] = (byte)(enc_color2[1] << 4 | enc_color2[1]); 
		avg_color_quant2[2] = (byte)(enc_color2[2] << 4 | enc_color2[2]);

		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
		//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------


		// Pack bits into the first word. 
		compressed1_flip[0] = 0;
		PUTBITSHIGH( compressed1_flip, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_flip, enc_color1[0], 4, 63);
		PUTBITSHIGH( compressed1_flip, enc_color1[1], 4, 55);
		PUTBITSHIGH( compressed1_flip, enc_color1[2], 4, 47);
		PUTBITSHIGH( compressed1_flip, enc_color2[0], 4, 59);
		PUTBITSHIGH( compressed1_flip, enc_color2[1], 4, 51);
		PUTBITSHIGH( compressed1_flip, enc_color2[2], 4, 43);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];

		// upper part of block
		flip_err = tryalltables_3bittable4x2(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);
		// lower part of block
		flip_err += tryalltables_3bittable4x2(img,width,height,startx,starty+2,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_flip, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_flip, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_flip,           1,   1, 32);

		best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
		best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);
		
		compressed2_flip[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16) | (best_pixel_indices1_LSB[0] & 0xffff);
	}

	// Now lets see which is the best table to use. Only 8 tables are possible. 

	if(norm_err <= flip_err)
	{
		compressed1[0] = compressed1_norm[0] | 0;
		compressed2[0] = compressed2_norm[0];
	}
	else
	{
		compressed1[0] = compressed1_flip[0] | 1;
		compressed2[0] = compressed2_flip[0];
	}
}

//Calculation of the two block colors using the LBG-algorithm
//The following method scales down the intensity, since this can be compensated for anyway by both the H and T mode.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(LBG_colors)[2][3]


void computeColorLBGHalfIntensityFast(byte[] img,int width,int startx,int starty, byte[][] LBG_colors) 
{
	byte[][] block_mask = new byte[4][4];

	// reset rand so that we get predictable output per block
	//srand(10000);
	rand.setSeed(10000);
	//LBG-algorithm
	double D = 0, oldD, bestD = MAXIMUM_ERROR, eps = 0.0000000001;
	double error_a, error_b;
	int number_of_iterations = 10;
	double[][] t_color= new double[2][3];
	double[][][] original_colors= new double[4][4][3];
	double[][] current_colors= new double[2][3];
	double[][] best_colors= new double[2][3];
	double[] max_v= new double[3];
	double[] min_v= new double[3];
	int x,y,i;
	double red, green, blue;
	boolean continue_seeding;
	int maximum_number_of_seedings = 10;
	int seeding;
	boolean continue_iterate;

	max_v[R] = -512.0;   max_v[G] = -512.0;   max_v[B] = -512.0; 
	min_v[R] =  512.0;   min_v[G] =  512.0;   min_v[B] =  512.0;
	

	
	// resolve trainingdata
	for (y = 0; y < BLOCKHEIGHT; ++y) 
	{
		for (x = 0; x < BLOCKWIDTH; ++x) 
		{
			red = img[3*((starty+y)*width+startx+x)+R]&0xff;
			green = img[3*((starty+y)*width+startx+x)+G]&0xff;
			blue = img[3*((starty+y)*width+startx+x)+B]&0xff;

			// Use qrs representation instead of rgb
			// qrs = Q * rgb where Q = [a a a ; b -b 0 ; c c -2c]; a = 1/sqrt(3), b= 1/sqrt(2), c = 1/sqrt(6);
			// rgb = inv(Q)*qrs  = Q' * qrs where ' denotes transpose.
			// The q variable holds intensity. r and s hold chrominance.
			// q = [0, sqrt(3)*255], r = [-255/sqrt(2), 255/sqrt(2)], s = [-2*255/sqrt(6), 2*255/sqrt(6)];
			//
			// The LGB algorithm will only act on the r and s variables and not on q.
			// 
			

			
			
			original_colors[x][y][R] = sqrtW13*red + sqrtW13*green + sqrtW13*blue;
			original_colors[x][y][G] = sqrtW12*red - sqrtW12*green;
			original_colors[x][y][B] = sqrtW16*red + sqrtW16*green - sqrtW26*blue;
		
			// find max
			if (original_colors[x][y][R] > max_v[R]) max_v[R] = original_colors[x][y][R];
			if (original_colors[x][y][G] > max_v[G]) max_v[G] = original_colors[x][y][G];
			if (original_colors[x][y][B] > max_v[B]) max_v[B] = original_colors[x][y][B];
			// find min
			if (original_colors[x][y][R] < min_v[R]) min_v[R] = original_colors[x][y][R];
			if (original_colors[x][y][G] < min_v[G]) min_v[G] = original_colors[x][y][G];
			if (original_colors[x][y][B] < min_v[B]) min_v[B] = original_colors[x][y][B];
		}
	}

	D = 512*512*3*16.0; 
	bestD = 512*512*3*16.0; 
	
	//I note this appears to be either 1 loop through seeds or max number, nothing in between

	continue_seeding = true;
	// loop seeds
	for (seeding = 0; (seeding < maximum_number_of_seedings) && continue_seeding; seeding++)
	{
		// hopefully we will not need more seedings:
		continue_seeding = false;

		// calculate seeds
		for (byte s = 0; s < 2; ++s) 
		{
			for (byte c = 0; c < 3; ++c) 
			{ 
				current_colors[s][c] = (double)(((double)(rand.nextDouble()))*(max_v[c]-min_v[c])) + min_v[c];
			}
		}

		// divide into two quantization sets and calculate distortion

		continue_iterate = true;
		for(i = 0; (i < number_of_iterations) && continue_iterate; i++)
		{
			oldD = D;
			D = 0;
			int n = 0;
			for (y = 0; y < BLOCKHEIGHT; ++y) 
			{
				for (x = 0; x < BLOCKWIDTH; ++x) 
				{
					error_a = 0.5*SQUARE(original_colors[x][y][R] - current_colors[0][R]) + 
							  SQUARE(original_colors[x][y][G] - current_colors[0][G]) +
							  SQUARE(original_colors[x][y][B] - current_colors[0][B]);
					error_b = 0.5*SQUARE(original_colors[x][y][R] - current_colors[1][R]) + 
							  SQUARE(original_colors[x][y][G] - current_colors[1][G]) +
							  SQUARE(original_colors[x][y][B] - current_colors[1][B]);
					if (error_a < error_b) 
					{
						block_mask[x][y] = 0;
						D += error_a; 
						++n;
					} 
					else 
					{
						block_mask[x][y] = 1;
						D += error_b;
					}
				}
			}

			// compare with old distortion
			if (D == 0) 
			{
				// Perfect score -- we dont need to go further iterations.
				continue_iterate = false;
				continue_seeding = false;
			}
			if (D == oldD)
			{
				// Same score as last round -- no need to go for further iterations.
				continue_iterate = false;
				continue_seeding = false;
			}
			if (D < bestD) 
			{
				bestD = D;
				for(byte s = 0; s < 2; ++s) 
				{
					for(byte c = 0; c < 3; ++c) 
					{
						best_colors[s][c] = current_colors[s][c];
					}
				}
			}
			if (n == 0 || n == BLOCKWIDTH*BLOCKHEIGHT) 
			{
				// All colors end up in the same voroni region. We need to reseed.
				continue_iterate = false;
				continue_seeding = true;
			}
			else
			{
				// Calculate new reconstruction points using the centroids

				// Find new construction values from average
				t_color[0][R] = 0;
				t_color[0][G] = 0;
				t_color[0][B] = 0;
				t_color[1][R] = 0;
				t_color[1][G] = 0;
				t_color[1][B] = 0;

				for (y = 0; y < BLOCKHEIGHT; ++y) 
				{
					for (x = 0; x < BLOCKWIDTH; ++x) 
					{
						// use dummy value for q-parameter
						t_color[block_mask[x][y]][R] += original_colors[x][y][R];
						t_color[block_mask[x][y]][G] += original_colors[x][y][G];
						t_color[block_mask[x][y]][B] += original_colors[x][y][B];
					}
				}
				current_colors[0][R] = t_color[0][R] / n;
				current_colors[1][R] = t_color[1][R] / (BLOCKWIDTH*BLOCKHEIGHT - n);
				current_colors[0][G] = t_color[0][G] / n;
				current_colors[1][G] = t_color[1][G] / (BLOCKWIDTH*BLOCKHEIGHT - n);
				current_colors[0][B] = t_color[0][B] / n;
				current_colors[1][B] = t_color[1][B] / (BLOCKWIDTH*BLOCKHEIGHT - n);
			}
		}
	}

	for(x=0;x<2;x++)
	{
		double qq, rr, ss;

		qq = best_colors[x][0];
		rr = best_colors[x][1];
		ss = best_colors[x][2];

		current_colors[x][0] = CLAMP(0, sqrtW13*qq + sqrtW12*rr + sqrtW16*ss, 255);
		current_colors[x][1] = CLAMP(0, sqrtW13*qq - sqrtW12*rr + sqrtW16*ss, 255);
		current_colors[x][2] = CLAMP(0, sqrtW13*qq + (0.0        )*rr - sqrtW26*ss, 255);
	}

	for(x=0;x<2;x++)
		for(y=0;y<3;y++)
			LBG_colors[x][y] = (byte)JAS_ROUND(current_colors[x][y]);
}

//Calculation of the two block colors using the LBG-algorithm
//The following method scales down the intensity, since this can be compensated for anyway by both the H and T mode.
//Faster version
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
// byte(LBG_colors)[2][3]
void computeColorLBGNotIntensityFast(byte[] img,int width,int startx,int starty, byte[][] LBG_colors) 
{
	byte[][] block_mask= new byte[4][4];

	// reset rand so that we get predictable output per block
	//srand(10000);
	rand.setSeed(10000);
	//LBG-algorithm
	double D = 0, oldD, bestD = MAXIMUM_ERROR, eps = 0.0000000001;
	double error_a, error_b;
	int number_of_iterations = 10;
	double[][] t_color= new double[2][3];
	double[][][] original_colors= new double[4][4][3];
	double[][] current_colors= new double[2][3];
	double[][] best_colors= new double[2][3];
	double[] max_v= new double[3];
	double[] min_v= new double[3];
	int x,y,i;
	double red, green, blue;
	boolean continue_seeding;
	int maximum_number_of_seedings = 10;
	int seeding;
	boolean continue_iterate;

	max_v[R] = -512.0;   max_v[G] = -512.0;   max_v[B] = -512.0; 
	min_v[R] =  512.0;   min_v[G] =  512.0;   min_v[B] =  512.0;

	// resolve trainingdata
	for (y = 0; y < BLOCKHEIGHT; ++y) 
	{
		for (x = 0; x < BLOCKWIDTH; ++x) 
		{
			red = img[3*((starty+y)*width+startx+x)+R]&0xff;
			green = img[3*((starty+y)*width+startx+x)+G]&0xff;
			blue = img[3*((starty+y)*width+startx+x)+B]&0xff;

			// Use qrs representation instead of rgb
			// qrs = Q * rgb where Q = [a a a ; b -b 0 ; c c -2c]; a = 1/sqrt(1.0*3), b= 1/sqrt(1.0*2), c = 1/sqrt(1.0*6);
			// rgb = inv(Q)*qrs  = Q' * qrs where ' denotes transpose.
			// The q variable holds intensity. r and s hold chrominance.
			// q = [0, sqrt(1.0*3)*255], r = [-255/sqrt(1.0*2), 255/sqrt(1.0*2)], s = [-2*255/sqrt(1.0*6), 2*255/sqrt(1.0*6)];
			//
			// The LGB algorithm will only act on the r and s variables and not on q.
			// 
			original_colors[x][y][R] = sqrtW13*red + sqrtW13*green + sqrtW13*blue;
			original_colors[x][y][G] = sqrtW12*red - sqrtW12*green;
			original_colors[x][y][B] = sqrtW16*red + sqrtW13*green - sqrtW26*blue;
		
			// find max
			if (original_colors[x][y][R] > max_v[R]) max_v[R] = original_colors[x][y][R];
			if (original_colors[x][y][G] > max_v[G]) max_v[G] = original_colors[x][y][G];
			if (original_colors[x][y][B] > max_v[B]) max_v[B] = original_colors[x][y][B];
			// find min
			if (original_colors[x][y][R] < min_v[R]) min_v[R] = original_colors[x][y][R];
			if (original_colors[x][y][G] < min_v[G]) min_v[G] = original_colors[x][y][G];
			if (original_colors[x][y][B] < min_v[B]) min_v[B] = original_colors[x][y][B];
		}
	}

	D = 512*512*3*16.0; 
	bestD = 512*512*3*16.0; 

	continue_seeding = true;

	// loop seeds
	for (seeding = 0; (seeding < maximum_number_of_seedings) && continue_seeding; seeding++)
	{
		// hopefully we will not need more seedings:
		continue_seeding = false;

		// calculate seeds
		for (byte s = 0; s < 2; ++s) 
		{
			for (byte c = 0; c < 3; ++c) 
			{ 
				current_colors[s][c] = (double)(((double)(rand.nextDouble()))*(max_v[c]-min_v[c])) + min_v[c];
			}
		}
		// divide into two quantization sets and calculate distortion

		continue_iterate = true;
		for(i = 0; (i < number_of_iterations) && continue_iterate; i++)
		{
			oldD = D;
			D = 0;
			int n = 0;
			for (y = 0; y < BLOCKHEIGHT; ++y) 
			{
				for (x = 0; x < BLOCKWIDTH; ++x) 
				{
					error_a = 0.0*SQUARE(original_colors[x][y][R] - current_colors[0][R]) + 
							  SQUARE(original_colors[x][y][G] - current_colors[0][G]) +
							  SQUARE(original_colors[x][y][B] - current_colors[0][B]);
					error_b = 0.0*SQUARE(original_colors[x][y][R] - current_colors[1][R]) + 
							  SQUARE(original_colors[x][y][G] - current_colors[1][G]) +
							  SQUARE(original_colors[x][y][B] - current_colors[1][B]);
					if (error_a < error_b) 
					{
						block_mask[x][y] = 0;
						D += error_a; 
						++n;
					} 
					else 
					{
						block_mask[x][y] = 1;
						D += error_b;
					}
				}
			}

			// compare with old distortion
			if (D == 0) 
			{
				// Perfect score -- we dont need to go further iterations.
				continue_iterate = false;
				continue_seeding = false;
			}
			if (D == oldD)
			{
				// Same score as last round -- no need to go for further iterations.
				continue_iterate = false;
				continue_seeding = false;
			}
			if (D < bestD) 
			{
				bestD = D;
				for(byte s = 0; s < 2; ++s) 
				{
					for(byte c = 0; c < 3; ++c) 
					{
						best_colors[s][c] = current_colors[s][c];
					}
				}
			}
			if (n == 0 || n == BLOCKWIDTH*BLOCKHEIGHT) 
			{
				// All colors end up in the same voroni region. We need to reseed.
				continue_iterate = false;
				continue_seeding = true;
			}
			else
			{
				// Calculate new reconstruction points using the centroids

				// Find new construction values from average
				t_color[0][R] = 0;
				t_color[0][G] = 0;
				t_color[0][B] = 0;
				t_color[1][R] = 0;
				t_color[1][G] = 0;
				t_color[1][B] = 0;

				for (y = 0; y < BLOCKHEIGHT; ++y) 
				{
					for (x = 0; x < BLOCKWIDTH; ++x) 
					{
						// use dummy value for q-parameter
						t_color[block_mask[x][y]][R] += original_colors[x][y][R];
						t_color[block_mask[x][y]][G] += original_colors[x][y][G];
						t_color[block_mask[x][y]][B] += original_colors[x][y][B];
					}
				}
				current_colors[0][R] = t_color[0][R] / n;
				current_colors[1][R] = t_color[1][R] / (BLOCKWIDTH*BLOCKHEIGHT - n);
				current_colors[0][G] = t_color[0][G] / n;
				current_colors[1][G] = t_color[1][G] / (BLOCKWIDTH*BLOCKHEIGHT - n);
				current_colors[0][B] = t_color[0][B] / n;
				current_colors[1][B] = t_color[1][B] / (BLOCKWIDTH*BLOCKHEIGHT - n);
			}
		}
	}

	for(x=0;x<2;x++)
	{
		double qq, rr, ss;

		qq = best_colors[x][0];
		rr = best_colors[x][1];
		ss = best_colors[x][2];

		current_colors[x][0] = CLAMP(0, sqrtW13*qq + sqrtW12*rr + sqrtW16*ss, 255);
		current_colors[x][1] = CLAMP(0, sqrtW13*qq - sqrtW12*rr + sqrtW16*ss, 255);
		current_colors[x][2] = CLAMP(0, sqrtW13*qq + (0.0        )*rr - sqrtW26*ss, 255);
	}

	for(x=0;x<2;x++)
		for(y=0;y<3;y++)
			LBG_colors[x][y] = (byte)JAS_ROUND(current_colors[x][y]);
}

//Calculation of the two block colors using the LBG-algorithm
//The following method completely ignores the intensity, since this can be compensated for anyway by both the H and T mode.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(LBG_colors)[2][3]
void computeColorLBGNotIntensity(byte[] img,int width,int startx,int starty, byte[][] LBG_colors) 
{
	byte[][] block_mask= new byte[4][4];

	// reset rand so that we get predictable output per block
	//srand(10000);
	rand.setSeed(10000);
	//LBG-algorithm
	float D = 0, oldD, bestD = MAXIMUM_ERROR, eps = 0.0000000001f;
	float error_a, error_b;
	int number_of_iterations = 10;
	float[][] t_color= new float[2][3];
	float[][][] original_colors= new float[4][4][3];
	float[][] current_colors= new float[2][3];
	float[][] best_colors= new float[2][3];
	float[] max_v= new float[3];
	float[] min_v= new float[3];
	int x,y,i;
	float red, green, blue;
	boolean continue_seeding;
	int maximum_number_of_seedings = 10;
	int seeding;
	boolean continue_iterate;

	max_v[R] = -512.0f;   max_v[G] = -512.0f;   max_v[B] = -512.0f; 
	min_v[R] =  512.0f;   min_v[G] =  512.0f;   min_v[B] =  512.0f;

	// resolve trainingdata
	for (y = 0; y < BLOCKHEIGHT; ++y) 
	{
		for (x = 0; x < BLOCKWIDTH; ++x) 
		{
			red = img[3*((starty+y)*width+startx+x)+R]&0xff;
			green = img[3*((starty+y)*width+startx+x)+G]&0xff;
			blue = img[3*((starty+y)*width+startx+x)+B]&0xff;

			// Use qrs representation instead of rgb
			// qrs = Q * rgb where Q = [a a a ; b -b 0 ; c c -2c]; a = 1/sqrt(1.0*3), b= 1/sqrt(1.0*2), c = 1/sqrt(1.0*6);
			// rgb = inv(Q)*qrs  = Q' * qrs where ' denotes transpose.
			// The q variable holds intensity. r and s hold chrominance.
			// q = [0, sqrt(1.0*3)*255], r = [-255/sqrt(1.0*2), 255/sqrt(1.0*2)], s = [-2*255/sqrt(1.0*6), 2*255/sqrt(1.0*6)];
			//
			// The LGB algorithm will only act on the r and s variables and not on q.
			// 
			original_colors[x][y][R] = sqrtW13*red + sqrtW13*green + sqrtW13*blue;
			original_colors[x][y][G] = sqrtW12*red - sqrtW12*green;
			original_colors[x][y][B] = sqrtW16*red + sqrtW16*green - sqrtW26*blue;

			// find max
			if (original_colors[x][y][R] > max_v[R]) max_v[R] = original_colors[x][y][R];
			if (original_colors[x][y][G] > max_v[G]) max_v[G] = original_colors[x][y][G];
			if (original_colors[x][y][B] > max_v[B]) max_v[B] = original_colors[x][y][B];
			// find min
			if (original_colors[x][y][R] < min_v[R]) min_v[R] = original_colors[x][y][R];
			if (original_colors[x][y][G] < min_v[G]) min_v[G] = original_colors[x][y][G];
			if (original_colors[x][y][B] < min_v[B]) min_v[B] = original_colors[x][y][B];
		}
	}

	D = 512*512*3*16.0f; 
	bestD = 512*512*3*16.0f; 

	continue_seeding = true;

	// loop seeds
	for (seeding = 0; (seeding < maximum_number_of_seedings) && continue_seeding; seeding++)
	{
		// hopefully we will not need more seedings:
		continue_seeding = false;

		// calculate seeds
		for (byte s = 0; s < 2; ++s) 
		{
			for (byte c = 0; c < 3; ++c) 
			{ 
				current_colors[s][c] = (float)(rand.nextDouble()*(max_v[c]-min_v[c])) + min_v[c];
			}
		}
		
		// divide into two quantization sets and calculate distortion

		continue_iterate = true;
		for(i = 0; (i < number_of_iterations) && continue_iterate; i++)
		{
			oldD = D;
			D = 0;
			int n = 0;
			for (y = 0; y < BLOCKHEIGHT; ++y) 
			{
				for (x = 0; x < BLOCKWIDTH; ++x) 
				{
					error_a = (float)0.0*SQUARE(original_colors[x][y][R] - current_colors[0][R]) + 
							  SQUARE(original_colors[x][y][G] - current_colors[0][G]) +
							  SQUARE(original_colors[x][y][B] - current_colors[0][B]);
					error_b = (float)0.0*SQUARE(original_colors[x][y][R] - current_colors[1][R]) + 
							  SQUARE(original_colors[x][y][G] - current_colors[1][G]) +
							  SQUARE(original_colors[x][y][B] - current_colors[1][B]);
					if (error_a < error_b) 
					{
						block_mask[x][y] = 0;
						D += error_a; 
						++n;
					} 
					else 
					{
						block_mask[x][y] = 1;
						D += error_b;
					}
				}
			}

			// compare with old distortion
			if (D == 0) 
			{
				// Perfect score -- we dont need to go further iterations.
				continue_iterate = false;
				continue_seeding = false;
			}
			if (D == oldD)
			{
				// Same score as last round -- no need to go for further iterations.
				continue_iterate = false;
				continue_seeding = true;
			}
			if (D < bestD) 
			{
				bestD = D;
				for(byte s = 0; s < 2; ++s) 
				{
					for(byte c = 0; c < 3; ++c) 
					{
						best_colors[s][c] = current_colors[s][c];
					}
				}
			}
			if (n == 0 || n == BLOCKWIDTH*BLOCKHEIGHT) 
			{
				// All colors end up in the same voroni region. We need to reseed.
				continue_iterate = false;
				continue_seeding = true;
			}
			else
			{
				// Calculate new reconstruction points using the centroids

				// Find new construction values from average
				t_color[0][R] = 0;
				t_color[0][G] = 0;
				t_color[0][B] = 0;
				t_color[1][R] = 0;
				t_color[1][G] = 0;
				t_color[1][B] = 0;

				for (y = 0; y < BLOCKHEIGHT; ++y) 
				{
					for (x = 0; x < BLOCKWIDTH; ++x) 
					{
						// use dummy value for q-parameter
						t_color[block_mask[x][y]][R] += original_colors[x][y][R];
						t_color[block_mask[x][y]][G] += original_colors[x][y][G];
						t_color[block_mask[x][y]][B] += original_colors[x][y][B];
					}
				}
				current_colors[0][R] = t_color[0][R] / n;
				current_colors[1][R] = t_color[1][R] / (BLOCKWIDTH*BLOCKHEIGHT - n);
				current_colors[0][G] = t_color[0][G] / n;
				current_colors[1][G] = t_color[1][G] / (BLOCKWIDTH*BLOCKHEIGHT - n);
				current_colors[0][B] = t_color[0][B] / n;
				current_colors[1][B] = t_color[1][B] / (BLOCKWIDTH*BLOCKHEIGHT - n);
			}
		}
	}

	for(x=0;x<2;x++)
	{
		double qq, rr, ss;

		qq = best_colors[x][0];
		rr = best_colors[x][1];
		ss = best_colors[x][2];

		current_colors[x][0] = (float)CLAMP(0, sqrtW13*qq + sqrtW12*rr + sqrtW16*ss, 255);
		current_colors[x][1] = (float)CLAMP(0, sqrtW13*qq - sqrtW12*rr + sqrtW16*ss, 255);
		current_colors[x][2] = (float)CLAMP(0, sqrtW13*qq + (0.0        )*rr - sqrtW26*ss, 255);
	}

	for(x=0;x<2;x++)
		for(y=0;y<3;y++)
			LBG_colors[x][y] = (byte)JAS_ROUND(current_colors[x][y]);
}

//Calculation of the two block colors using the LBG-algorithm
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(LBG_colors)[2][3]
void computeColorLBG(byte[] img,int width,int startx,int starty, byte[][] LBG_colors) 
{
	byte[][] block_mask= new byte[4][4];

	// reset rand so that we get predictable output per block
	//srand(10000);
	rand.setSeed(10000);
	//LBG-algorithm
	double D = 0, oldD, bestD = MAXIMUM_ERROR, eps = 0.0000000001;
	double error_a, error_b;
	int number_of_iterations = 10;
	double[][] t_color= new double[2][3];
	double[][][] original_colors= new double[4][4][3];
	double[][] current_colors= new double[2][3];
	double[][] best_colors= new double[2][3];
	double[] max_v= new double[3];
	double[] min_v= new double[3];
	int x,y,i;
	double red, green, blue;
	boolean continue_seeding;
	int maximum_number_of_seedings = 10;
	int seeding;
	boolean continue_iterate;

	max_v[R] = -512.0;   max_v[G] = -512.0;   max_v[B] = -512.0; 
	min_v[R] =  512.0;   min_v[G] =  512.0;   min_v[B] =  512.0;

	// resolve trainingdata
	for (y = 0; y < BLOCKHEIGHT; ++y) 
	{
		for (x = 0; x < BLOCKWIDTH; ++x) 
		{
			red = img[3*((starty+y)*width+startx+x)+R]&0xff;
			green = img[3*((starty+y)*width+startx+x)+G]&0xff;
			blue = img[3*((starty+y)*width+startx+x)+B]&0xff;

			original_colors[x][y][R] = red;
			original_colors[x][y][G] = green;
			original_colors[x][y][B] = blue;

			// find max
			if (original_colors[x][y][R] > max_v[R]) max_v[R] = original_colors[x][y][R];
			if (original_colors[x][y][G] > max_v[G]) max_v[G] = original_colors[x][y][G];
			if (original_colors[x][y][B] > max_v[B]) max_v[B] = original_colors[x][y][B];
			// find min
			if (original_colors[x][y][R] < min_v[R]) min_v[R] = original_colors[x][y][R];
			if (original_colors[x][y][G] < min_v[G]) min_v[G] = original_colors[x][y][G];
			if (original_colors[x][y][B] < min_v[B]) min_v[B] = original_colors[x][y][B];
		}
	}

	D = 512*512*3*16.0; 
	bestD = 512*512*3*16.0; 

	continue_seeding = true;

	// loop seeds
	for (seeding = 0; (seeding < maximum_number_of_seedings) && continue_seeding; seeding++)
	{
		// hopefully we will not need more seedings:
		continue_seeding = false;

		// calculate seeds
		for (byte s = 0; s < 2; ++s) 
		{
			for (byte c = 0; c < 3; ++c) 
			{ 
				current_colors[s][c] = (double)(((double)(rand.nextDouble()))*(max_v[c]-min_v[c])) + min_v[c];
			}
		}
		
		// divide into two quantization sets and calculate distortion

		continue_iterate = true;
		for(i = 0; (i < number_of_iterations) && continue_iterate; i++)
		{
			oldD = D;
			D = 0;
			int n = 0;
			for (y = 0; y < BLOCKHEIGHT; ++y) 
			{
				for (x = 0; x < BLOCKWIDTH; ++x) 
				{
					error_a = SQUARE(original_colors[x][y][R] - JAS_ROUND(current_colors[0][R])) + 
							  SQUARE(original_colors[x][y][G] - JAS_ROUND(current_colors[0][G])) +
							  SQUARE(original_colors[x][y][B] - JAS_ROUND(current_colors[0][B]));
					error_b = SQUARE(original_colors[x][y][R] - JAS_ROUND(current_colors[1][R])) + 
							  SQUARE(original_colors[x][y][G] - JAS_ROUND(current_colors[1][G])) +
							  SQUARE(original_colors[x][y][B] - JAS_ROUND(current_colors[1][B]));
					if (error_a < error_b) 
					{
						block_mask[x][y] = 0;
						D += error_a; 
						++n;
					} 
					else 
					{
						block_mask[x][y] = 1;
						D += error_b;
					}
				}
			}

			// compare with old distortion
			if (D == 0) 
			{
				// Perfect score -- we dont need to go further iterations.
				continue_iterate = false;
				continue_seeding = false;
			}
			if (D == oldD)
			{
				// Same score as last round -- no need to go for further iterations.
				continue_iterate = false;
				continue_seeding = true;
			}
			if (D < bestD) 
			{
				bestD = D;
				for(byte s = 0; s < 2; ++s) 
				{
					for(byte c = 0; c < 3; ++c) 
					{
						best_colors[s][c] = current_colors[s][c];
					}
				}
			}
			if (n == 0 || n == BLOCKWIDTH*BLOCKHEIGHT) 
			{
				// All colors end up in the same voroni region. We need to reseed.
				continue_iterate = false;
				continue_seeding = true;
			}
			else
			{
				// Calculate new reconstruction points using the centroids

				// Find new construction values from average
				t_color[0][R] = 0;
				t_color[0][G] = 0;
				t_color[0][B] = 0;
				t_color[1][R] = 0;
				t_color[1][G] = 0;
				t_color[1][B] = 0;

				for (y = 0; y < BLOCKHEIGHT; ++y) 
				{
					for (x = 0; x < BLOCKWIDTH; ++x) 
					{
						// use dummy value for q-parameter
						t_color[block_mask[x][y]][R] += original_colors[x][y][R];
						t_color[block_mask[x][y]][G] += original_colors[x][y][G];
						t_color[block_mask[x][y]][B] += original_colors[x][y][B];
					}
				}
				current_colors[0][R] = t_color[0][R] / n;
				current_colors[1][R] = t_color[1][R] / (BLOCKWIDTH*BLOCKHEIGHT - n);
				current_colors[0][G] = t_color[0][G] / n;
				current_colors[1][G] = t_color[1][G] / (BLOCKWIDTH*BLOCKHEIGHT - n);
				current_colors[0][B] = t_color[0][B] / n;
				current_colors[1][B] = t_color[1][B] / (BLOCKWIDTH*BLOCKHEIGHT - n);
			}
		}
	}

	// Set the best colors as the final block colors
	for(int s = 0; s < 2; ++s) 
	{
		for(byte c = 0; c < 3; ++c) 
		{
			current_colors[s][c] = best_colors[s][c];
		}
	}		

	for(x=0;x<2;x++)
		for(y=0;y<3;y++)
			LBG_colors[x][y] = (byte)JAS_ROUND(current_colors[x][y]);
}

//Calculation of the two block colors using the LBG-algorithm
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(LBG_colors)[2][3]
void computeColorLBGfast(byte[] img,int width,int startx,int starty, byte[][] LBG_colors) 
{
	byte[][] block_mask= new byte[4][4];

	// reset rand so that we get predictable output per block
	//srand(10000);
	rand.setSeed(10000);
	//LBG-algorithm
	float D = 0, oldD, bestD = MAXIMUM_ERROR, eps = 0.0000000001f;
	float error_a, error_b;
	int number_of_iterations = 10;
	float[][] t_color= new float[2][3];
	byte[][][] original_colors = new byte[4][4][3];
	float[][] current_colors= new float[2][3];
	float[][] best_colors= new float[2][3];
	float[] max_v= new float[3];
	float[] min_v= new float[3];
	int x,y,i;
	boolean continue_seeding;
	int maximum_number_of_seedings = 10;
	int seeding;
	boolean continue_iterate;

	max_v[R] = -512.0f;   max_v[G] = -512.0f;   max_v[B] = -512.0f; 
	min_v[R] =  512.0f;   min_v[G] =  512.0f;   min_v[B] =  512.0f;

	// resolve trainingdata
	for (y = 0; y < BLOCKHEIGHT; ++y) 
	{
		for (x = 0; x < BLOCKWIDTH; ++x) 
		{
			original_colors[x][y][R] = img[3*((starty+y)*width+startx+x)+R];
			original_colors[x][y][G] = img[3*((starty+y)*width+startx+x)+G];
			original_colors[x][y][B] = img[3*((starty+y)*width+startx+x)+B];
		
			// find max
			if (original_colors[x][y][R] > max_v[R]) max_v[R] = original_colors[x][y][R]&0xff;
			if (original_colors[x][y][G] > max_v[G]) max_v[G] = original_colors[x][y][G]&0xff;
			if (original_colors[x][y][B] > max_v[B]) max_v[B] = original_colors[x][y][B]&0xff;
			// find min
			if (original_colors[x][y][R] < min_v[R]) min_v[R] = original_colors[x][y][R]&0xff;
			if (original_colors[x][y][G] < min_v[G]) min_v[G] = original_colors[x][y][G]&0xff;
			if (original_colors[x][y][B] < min_v[B]) min_v[B] = original_colors[x][y][B]&0xff;
		}
	}

	D = 512*512*3*16.0f; 
	bestD = 512*512*3*16.0f; 

	continue_seeding = true;

	// loop seeds
	for (seeding = 0; (seeding < maximum_number_of_seedings) && continue_seeding; seeding++)
	{
		// hopefully we will not need more seedings:
		continue_seeding = false;

		// calculate seeds
		for (byte s = 0; s < 2; ++s) 
		{
			for (byte c = 0; c < 3; ++c) 
			{ 
				current_colors[s][c] = (float)(rand.nextDouble()*(max_v[c]-min_v[c])) + min_v[c];
			}
		}
		
		// divide into two quantization sets and calculate distortion
		continue_iterate = true;
		for(i = 0; (i < number_of_iterations) && continue_iterate; i++)
		{
			oldD = D;
			D = 0;
			int n = 0;
			for (y = 0; y < BLOCKHEIGHT; ++y) 
			{
				for (x = 0; x < BLOCKWIDTH; ++x) 
				{
					error_a = (float)(SQUARE((original_colors[x][y][R]&0xff) - JAS_ROUND(current_colors[0][R])) + 
							  SQUARE((original_colors[x][y][G]&0xff) - JAS_ROUND(current_colors[0][G])) +
							  SQUARE((original_colors[x][y][B]&0xff) - JAS_ROUND(current_colors[0][B])));
					error_b = (float)(SQUARE((original_colors[x][y][R]&0xff) - JAS_ROUND(current_colors[1][R])) + 
							  SQUARE((original_colors[x][y][G]&0xff) - JAS_ROUND(current_colors[1][G])) +
							  SQUARE((original_colors[x][y][B]&0xff) - JAS_ROUND(current_colors[1][B])));
					if (error_a < error_b) 
					{
						block_mask[x][y] = 0;
						D += error_a; 
						++n;
					} 
					else 
					{
						block_mask[x][y] = 1;
						D += error_b;
					}
				}
			}

			// compare with old distortion
			if (D == 0) 
			{
				// Perfect score -- we dont need to go further iterations.
				continue_iterate = false;
				continue_seeding = false;
			}
			if (D == oldD)
			{
				// Same score as last round -- no need to go for further iterations.
				continue_iterate = false;
				continue_seeding = false;
			}
			if (D < bestD) 
			{
				bestD = D;
				for(byte s = 0; s < 2; ++s) 
				{
					for(byte c = 0; c < 3; ++c) 
					{
						best_colors[s][c] = current_colors[s][c];
					}
				}
			}
			if (n == 0 || n == BLOCKWIDTH*BLOCKHEIGHT) 
			{
				// All colors end up in the same voroni region. We need to reseed.
				continue_iterate = false;
				continue_seeding = true;
			}
			else
			{
				// Calculate new reconstruction points using the centroids

				// Find new construction values from average
				t_color[0][R] = 0;
				t_color[0][G] = 0;
				t_color[0][B] = 0;
				t_color[1][R] = 0;
				t_color[1][G] = 0;
				t_color[1][B] = 0;

				for (y = 0; y < BLOCKHEIGHT; ++y) 
				{
					for (x = 0; x < BLOCKWIDTH; ++x) 
					{
						// use dummy value for q-parameter
						t_color[block_mask[x][y]][R] += (original_colors[x][y][R]&0xff);
						t_color[block_mask[x][y]][G] += (original_colors[x][y][G]&0xff);
						t_color[block_mask[x][y]][B] += (original_colors[x][y][B]&0xff);
					}
				}
				current_colors[0][R] = t_color[0][R] / n;
				current_colors[1][R] = t_color[1][R] / (BLOCKWIDTH*BLOCKHEIGHT - n);
				current_colors[0][G] = t_color[0][G] / n;
				current_colors[1][G] = t_color[1][G] / (BLOCKWIDTH*BLOCKHEIGHT - n);
				current_colors[0][B] = t_color[0][B] / n;
				current_colors[1][B] = t_color[1][B] / (BLOCKWIDTH*BLOCKHEIGHT - n);
			}
		}
	}

	// Set the best colors as the final block colors
	for(int s = 0; s < 2; ++s) 
	{
		for(byte c = 0; c < 3; ++c) 
		{
			current_colors[s][c] = best_colors[s][c];
		}
	}		

	for(x=0;x<2;x++)
		for(y=0;y<3;y++)
			LBG_colors[x][y] = (byte)JAS_ROUND(current_colors[x][y]);
}

//Each color component is compressed to fit in its specified number of bits
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(current_color)[2][3], byte(quantized_color)[2][3])
static void compressColor(int R_B, int G_B, int B_B, byte[][] current_color, byte[][] quantized_color) 
{
	//
	//	The color is calculated as:
	//
	//  c = (c + (2^(8-b))/2) / (255 / (2^b - 1)) where b is the number of bits
	//                                            to code color c with
	//  For instance, if b = 3:
	//
	//  c = (c + 16) / (255 / 7) = 7 * (c + 16) / 255
	//

	quantized_color[0][R] = (byte)CLAMP(0,(BINPOW(R_B)-1) * ((current_color[0][R]&0xff) + BINPOW(8-R_B-1)) / 255,255);
	quantized_color[0][G] = (byte)CLAMP(0,(BINPOW(G_B)-1) * ((current_color[0][G]&0xff) + BINPOW(8-G_B-1)) / 255,255);
	quantized_color[0][B] = (byte)CLAMP(0,(BINPOW(B_B)-1) * ((current_color[0][B]&0xff) + BINPOW(8-B_B-1)) / 255,255);

	quantized_color[1][R] = (byte)CLAMP(0,(BINPOW(R_B)-1) * ((current_color[1][R]&0xff) + BINPOW(8-R_B-1)) / 255,255);
	quantized_color[1][G] = (byte)CLAMP(0,(BINPOW(G_B)-1) * ((current_color[1][G]&0xff) + BINPOW(8-G_B-1)) / 255,255);
	quantized_color[1][B] = (byte)CLAMP(0,(BINPOW(B_B)-1) * ((current_color[1][B]&0xff) + BINPOW(8-B_B-1)) / 255,255);
}

//Swapping two RGB-colors
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(current_color)[2][3], byte(quantized_color)[2][3])
static void swapColors(byte[][] colors) 
{
	byte temp = colors[0][R];
	colors[0][R] = colors[1][R];
	colors[1][R] = temp;

	temp = colors[0][G];
	colors[0][G] = colors[1][G];
	colors[1][G] = temp;

	temp = colors[0][B];
	colors[0][B] = colors[1][B];
	colors[1][B] = temp;
}


//Calculate the paint colors from the block colors 
//using a distance d and one of the H- or T-patterns.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.

//Calculate the error for the block at position (startx,starty)
//The parameters needed for reconstruction are calculated as well
//
//Please note that the function can change the order between the two colors in colorsRGB444
//
//In the 59T bit mode, we only have pattern T.
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(colorsRGB444)[2][3]
//address  byte &distance, int &pixel_indices
static int calculateError59Tperceptual1000(byte[] srcimg, int width, int startx, int starty, byte[][] colorsRGB444, byte[] distance, int[] pixel_indices) 
{

	int block_error = 0, 
		   best_block_error = MAXERR1000,
		   pixel_error, 
		   best_pixel_error;
	int[] diff= new int[3];
	byte best_sw =0;
	int pixel_colors;
	byte[][] colors= new byte[2][3];
	byte[][] possible_colors= new byte[4][3];

	// First use the colors as they are, then swap them
	for (byte sw = 0; sw <2; ++sw) 
	{ 
		if (sw == 1) 
		{
			swapColors(colorsRGB444);
		}
		decompressColor(R_BITS59T, G_BITS59T, B_BITS59T, colorsRGB444, colors);

		// Test all distances
		for (byte d = 0; d < BINPOW(TABLE_BITS_59T); ++d) 
		{
			calculatePaintColors59T(d,PATTERN.PATTERN_T, colors, possible_colors);
			
			block_error = 0;	
			pixel_colors = 0;

			// Loop block
			for (int y = 0; y < BLOCKHEIGHT; ++y) 
			{
				for (int x = 0; x < BLOCKWIDTH; ++x) 
				{
					best_pixel_error = MAXERR1000;
					pixel_colors <<=2; // Make room for next value

					// Loop possible block colors
					for (byte c = 0; c < 4; ++c) 
					{
					
						diff[R] = (srcimg[3*((starty+y)*width+startx+x)+R]&0xff) - (CLAMP(0,possible_colors[c][R],255)&0xff);
						diff[G] = (srcimg[3*((starty+y)*width+startx+x)+G]&0xff) - (CLAMP(0,possible_colors[c][G],255)&0xff);
						diff[B] = (srcimg[3*((starty+y)*width+startx+x)+B]&0xff) - (CLAMP(0,possible_colors[c][B],255)&0xff);

						pixel_error =	PERCEPTUAL_WEIGHT_R_SQUARED_TIMES1000*SQUARE(diff[R]) +
										PERCEPTUAL_WEIGHT_G_SQUARED_TIMES1000*SQUARE(diff[G]) +
										PERCEPTUAL_WEIGHT_B_SQUARED_TIMES1000*SQUARE(diff[B]);

						// Choose best error
						if (pixel_error < best_pixel_error) 
						{
							best_pixel_error = pixel_error;
							pixel_colors ^= (pixel_colors & 3); // Reset the two first bits
							pixel_colors = pixel_colors | c;
						} 
					}
					block_error += best_pixel_error;
				}
			}
			if (block_error < best_block_error) 
			{
				best_block_error = block_error;
				distance[0] = d;
				pixel_indices[0] = pixel_colors;
				best_sw = sw;
			}
		}
		
		if (sw == 1 && best_sw == 0) 
		{
			swapColors(colorsRGB444);
		}
		decompressColor(R_BITS59T, G_BITS59T, B_BITS59T, colorsRGB444, colors);
	}
	return best_block_error;
}

//Calculate the error for the block at position (startx,starty)
//The parameters needed for reconstruction is calculated as well
//
//Please note that the function can change the order between the two colors in colorsRGB444
//
//In the 59T bit mode, we only have pattern T.
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(colorsRGB444)[2][3]
//address byte &distance,  int &pixel_indices
static double calculateError59T(byte[] srcimg, int width, int startx, int starty, byte[][] colorsRGB444, byte[] distance,  int[] pixel_indices) 
{
	double block_error = 0, 
		     best_block_error = MAXIMUM_ERROR, 
				 pixel_error, 
				 best_pixel_error;
	int[] diff = new int[3];
	byte best_sw = 0;
	int pixel_colors;
	byte[][] colors = new byte[2][3];
	byte[][] possible_colors= new byte[4][3];

	// First use the colors as they are, then swap them
	for (byte sw = 0; sw <2; ++sw) 
	{ 
		if (sw == 1) 
		{
			swapColors(colorsRGB444);
		}
		decompressColor(R_BITS59T, G_BITS59T, B_BITS59T, colorsRGB444, colors);

		// Test all distances
		for (byte d = 0; d < BINPOW(TABLE_BITS_59T); ++d) 
		{
			calculatePaintColors59T(d,PATTERN.PATTERN_T, colors, possible_colors);
			
			block_error = 0;	
			pixel_colors = 0;

			// Loop block
			for (int y = 0; y < BLOCKHEIGHT; ++y) 
			{
				for (int x = 0; x < BLOCKWIDTH; ++x) 
				{
					best_pixel_error = MAXIMUM_ERROR;
					pixel_colors <<=2; // Make room for next value

					// Loop possible block colors
					for (int c = 0; c < 4; ++c) 
					{
					
						diff[R] = (srcimg[3*((starty+y)*width+startx+x)+R]&0xff) - (CLAMP(0,possible_colors[c][R],255)&0xff);
						diff[G] = (srcimg[3*((starty+y)*width+startx+x)+G]&0xff) - (CLAMP(0,possible_colors[c][G],255)&0xff);
						diff[B] = (srcimg[3*((starty+y)*width+startx+x)+B]&0xff) - (CLAMP(0,possible_colors[c][B],255)&0xff);

						pixel_error =	weight[R]*SQUARE(diff[R]) +
										weight[G]*SQUARE(diff[G]) +
										weight[B]*SQUARE(diff[B]);

						// Choose best error
						if (pixel_error < best_pixel_error) 
						{
							best_pixel_error = pixel_error;
							pixel_colors ^= (pixel_colors & 3); // Reset the two first bits
							pixel_colors |= c;
						} 
					}
					block_error += best_pixel_error;
				}
			}
			if (block_error < best_block_error) 
			{
				best_block_error = block_error;
				distance[0] = d;
				pixel_indices[0] = pixel_colors;
				best_sw = sw;
			}
		}
		
		if (sw == 1 && best_sw == 0) 
		{
			swapColors(colorsRGB444);
		}
		decompressColor(R_BITS59T, G_BITS59T, B_BITS59T, colorsRGB444, colors);
	}
	return best_block_error;
}

//Calculate the error for the block at position (startx,starty)
//The parameters needed for reconstruction is calculated as well
//
//In the 59T bit mode, we only have pattern T.
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(colorsRGB444)[2][3]
//address  byte &distance, int &pixel_indices
static int calculateError59TnoSwapPerceptual1000(byte[] srcimg, int width, int startx, int starty, byte[][] colorsRGB444, byte[] distance, int[] pixel_indices) 
{

	int block_error = 0, 
		   best_block_error = MAXERR1000,
		   pixel_error, 
		   best_pixel_error;
	int[] diff = new int[3];
	int pixel_colors;
	byte[][] colors= new byte[2][3];
	byte[][] possible_colors= new byte[4][3];
	int thebestintheworld;

	// First use the colors as they are, then swap them
		decompressColor(R_BITS59T, G_BITS59T, B_BITS59T, colorsRGB444, colors);

		// Test all distances
		for (byte d = 0; d < BINPOW(TABLE_BITS_59T); ++d) 
		{
			calculatePaintColors59T(d,PATTERN.PATTERN_T, colors, possible_colors);
			
			block_error = 0;	
			pixel_colors = 0;

			// Loop block
			for (int y = 0; y < BLOCKHEIGHT; ++y) 
			{
				for (int x = 0; x < BLOCKWIDTH; ++x) 
				{
					best_pixel_error = MAXERR1000;
					pixel_colors <<=2; // Make room for next value

					// Loop possible block colors
					for (int c = 0; c < 4; ++c) 
					{
					
						diff[R] = (srcimg[3*((starty+y)*width+startx+x)+R]&0xff) - (CLAMP(0,possible_colors[c][R],255)&0xff);
						diff[G] = (srcimg[3*((starty+y)*width+startx+x)+G]&0xff) - (CLAMP(0,possible_colors[c][G],255)&0xff);
						diff[B] = (srcimg[3*((starty+y)*width+startx+x)+B]&0xff) - (CLAMP(0,possible_colors[c][B],255)&0xff);

						pixel_error =	PERCEPTUAL_WEIGHT_R_SQUARED_TIMES1000*SQUARE(diff[R]) +
										PERCEPTUAL_WEIGHT_G_SQUARED_TIMES1000*SQUARE(diff[G]) +
										PERCEPTUAL_WEIGHT_B_SQUARED_TIMES1000*SQUARE(diff[B]);

						// Choose best error
						if (pixel_error < best_pixel_error) 
						{
							best_pixel_error = pixel_error;
							pixel_colors ^= (pixel_colors & 3); // Reset the two first bits
							pixel_colors |= c;
							thebestintheworld = c;
						} 
					}
					block_error += best_pixel_error;
				}
			}
			if (block_error < best_block_error) 
			{
				best_block_error = block_error;
				distance[0] = d;
				pixel_indices[0] = pixel_colors;
			}
		}
		
	decompressColor(R_BITS59T, G_BITS59T, B_BITS59T, colorsRGB444, colors);
	return best_block_error;
}

//Calculate the error for the block at position (startx,starty)
//The parameters needed for reconstruction is calculated as well
//
//In the 59T bit mode, we only have pattern T.
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(colorsRGB444)[2][3]
//byte &distance, int &pixel_indices
static double calculateError59TnoSwap(byte[] srcimg, int width, int startx, int starty, byte[][] colorsRGB444, byte[] distance, int[] pixel_indices) 
{
	double block_error = 0, 
		     best_block_error = MAXIMUM_ERROR, 
				 pixel_error, 
				 best_pixel_error;
	int[] diff= new int[3];
	int pixel_colors;
	byte[][] colors= new byte[2][3];
	byte[][] possible_colors= new byte[4][3];
	int thebestintheworld;

	// First use the colors as they are, then swap them
	decompressColor(R_BITS59T, G_BITS59T, B_BITS59T, colorsRGB444, colors);

	// Test all distances
	for (byte d = 0; d < BINPOW(TABLE_BITS_59T); ++d) 
	{
		calculatePaintColors59T(d,PATTERN.PATTERN_T, colors, possible_colors);
			
		block_error = 0;	
		pixel_colors = 0;

		// Loop block
		for (int y = 0; y < BLOCKHEIGHT; ++y) 
		{
			for (int x = 0; x < BLOCKWIDTH; ++x) 
			{
				best_pixel_error = MAXIMUM_ERROR;
				pixel_colors <<=2; // Make room for next value

				// Loop possible block colors
				for (int c = 0; c < 4; ++c) 
				{
					diff[R] = (srcimg[3*((starty+y)*width+startx+x)+R]&0xff) - (CLAMP(0,possible_colors[c][R],255)&0xff);
					diff[G] = (srcimg[3*((starty+y)*width+startx+x)+G]&0xff) - (CLAMP(0,possible_colors[c][G],255)&0xff);
					diff[B] = (srcimg[3*((starty+y)*width+startx+x)+B]&0xff) - (CLAMP(0,possible_colors[c][B],255)&0xff);

					pixel_error =	weight[R]*SQUARE(diff[R]) +
						            weight[G]*SQUARE(diff[G]) +
												weight[B]*SQUARE(diff[B]);

					// Choose best error
					if (pixel_error < best_pixel_error) 
					{
						best_pixel_error = pixel_error;
						pixel_colors ^= (pixel_colors & 3); // Reset the two first bits
						pixel_colors |= c;
						thebestintheworld = c;
					} 
				}
				block_error += best_pixel_error;
			}
		}
		if (block_error < best_block_error) 
		{
			best_block_error = block_error;
			distance[0] = d;
			pixel_indices[0] = pixel_colors;
		}
	}
		
	decompressColor(R_BITS59T, G_BITS59T, B_BITS59T, colorsRGB444, colors);
	return best_block_error;
}

//Put the compress params into the compression block 
//
//
//|63 62 61 60 59|58 57 56 55|54 53 52 51|50 49 48 47|46 45 44 43|42 41 40 39|38 37 36 35|34 33 32|
//|----empty-----|---red 0---|--green 0--|--blue 0---|---red 1---|--green 1--|--blue 1---|--dist--|
//
//|31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
//|----------------------------------------index bits---------------------------------------------|
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(colors)[2][3]
//address int &compressed1, int &compressed2
static void packBlock59T(byte[][] colors, byte d, int pixel_indices, int[] compressed1, int[] compressed2) 
{ 
	
	compressed1[0] = 0;

	PUTBITSHIGH( compressed1, colors[0][R], 4, 58);
	PUTBITSHIGH( compressed1, colors[0][G], 4, 54);
	PUTBITSHIGH( compressed1, colors[0][B], 4, 50);
	PUTBITSHIGH( compressed1, colors[1][R], 4, 46);
	PUTBITSHIGH( compressed1, colors[1][G], 4, 42);
	PUTBITSHIGH( compressed1, colors[1][B], 4, 38);	
	PUTBITSHIGH( compressed1, d, TABLE_BITS_59T, 34);
	pixel_indices=indexConversion(pixel_indices);
	compressed2[0] = 0;
	PUTBITS( compressed2, pixel_indices, 32, 31);
}

//Copy colors from source to dest
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(source)[2][3], byte(dest)[2][3]
static void copyColors(byte[][] source, byte[][] dest)
{
	int x,y;

	for (x=0; x<2; x++)
		for (y=0; y<3; y++)
			dest[x][y] = source[x][y];
}

//The below code should compress the block to 59 bits. 
//
//|63 62 61 60 59|58 57 56 55|54 53 52 51|50 49 48 47|46 45 44 43|42 41 40 39|38 37 36 35|34 33 32|
//|----empty-----|---red 0---|--green 0--|--blue 0---|---red 1---|--green 1--|--blue 1---|--dist--|
//
//|31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
//|----------------------------------------index bits---------------------------------------------|
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//int (best_colorsRGB444_packed)[2]
int compressBlockTHUMB59TFastestOnlyColorPerceptual1000(byte[] img,int width,int height,int startx,int starty, int[] best_colorsRGB444_packed)
{
	int best_error = MAXERR1000;
	int best_pixel_indices;
	byte best_distance;

	int error_no_i;
	byte[][] colorsRGB444_no_i= new byte[2][3];
	int[] pixel_indices_no_i= new int[1];
	byte[] distance_no_i= new byte[1];

	byte[][] colors= new byte[2][3];

	// Calculate average color using the LBG-algorithm
	computeColorLBGHalfIntensityFast(img,width,startx,starty, colors);
	compressColor(R_BITS59T, G_BITS59T, B_BITS59T, colors, colorsRGB444_no_i);

	// Determine the parameters for the lowest error
	error_no_i = calculateError59Tperceptual1000(img, width, startx, starty, colorsRGB444_no_i, distance_no_i, pixel_indices_no_i);			

	best_error = error_no_i;
	best_distance = distance_no_i[0];
	best_pixel_indices = pixel_indices_no_i[0];

	best_colorsRGB444_packed[0] = (colorsRGB444_no_i[0][0] << 8) + (colorsRGB444_no_i[0][1] << 4) + (colorsRGB444_no_i[0][2] << 0);
	best_colorsRGB444_packed[1] = (colorsRGB444_no_i[1][0] << 8) + (colorsRGB444_no_i[1][1] << 4) + (colorsRGB444_no_i[1][2] << 0);

	return best_error;
}


//The below code should compress the block to 59 bits. 
//This is supposed to match the first of the three modes in TWOTIMER.
//
//|63 62 61 60 59|58 57 56 55|54 53 52 51|50 49 48 47|46 45 44 43|42 41 40 39|38 37 36 35|34 33 32|
//|----empty-----|---red 0---|--green 0--|--blue 0---|---red 1---|--green 1--|--blue 1---|--dist--|
//
//|31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
//|----------------------------------------index bits---------------------------------------------|
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//int (best_colorsRGB444_packed)[2]
double compressBlockTHUMB59TFastestOnlyColor(byte[] img,int width,int height,int startx,int starty, int[] best_colorsRGB444_packed)
{
	double best_error = MAXIMUM_ERROR;
	int best_pixel_indices;
	byte best_distance;

	double error_no_i;
	byte[][] colorsRGB444_no_i= new byte[2][3];
	int[] pixel_indices_no_i= new int[1];
	byte[] distance_no_i= new byte[1];

	byte[][] colors= new byte[2][3];

	// Calculate average color using the LBG-algorithm
	computeColorLBGHalfIntensityFast(img,width,startx,starty, colors);
	compressColor(R_BITS59T, G_BITS59T, B_BITS59T, colors, colorsRGB444_no_i);

	// Determine the parameters for the lowest error
	error_no_i = calculateError59T(img, width, startx, starty, colorsRGB444_no_i, distance_no_i, pixel_indices_no_i);			

	best_error = error_no_i;
	best_distance = distance_no_i[0];
	best_pixel_indices = pixel_indices_no_i[0];

	best_colorsRGB444_packed[0] = (colorsRGB444_no_i[0][0] << 8) + (colorsRGB444_no_i[0][1] << 4) + (colorsRGB444_no_i[0][2] << 0);
	best_colorsRGB444_packed[1] = (colorsRGB444_no_i[1][0] << 8) + (colorsRGB444_no_i[1][1] << 4) + (colorsRGB444_no_i[1][2] << 0);

	return best_error;
}

//The below code should compress the block to 59 bits. 
//This is supposed to match the first of the three modes in TWOTIMER.
//
//|63 62 61 60 59|58 57 56 55|54 53 52 51|50 49 48 47|46 45 44 43|42 41 40 39|38 37 36 35|34 33 32|
//|----empty-----|---red 0---|--green 0--|--blue 0---|---red 1---|--green 1--|--blue 1---|--dist--|
//
//|31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
//|----------------------------------------index bits---------------------------------------------|
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &compressed1, int &compressed2
double compressBlockTHUMB59TFastestPerceptual1000(byte[] img,int width,int height,int startx,int starty, int[] compressed1, int[] compressed2) 
{
	double best_error = MAXIMUM_ERROR;
	byte[][] best_colorsRGB444= new byte[2][3];
	int best_pixel_indices;
	byte best_distance;

	double error_no_i;
	byte[][] colorsRGB444_no_i = new byte[2][3];
	int[] pixel_indices_no_i= new int[1];
	byte[] distance_no_i= new byte[1];

	byte[][] colors = new byte[2][3];

	// Calculate average color using the LBG-algorithm
	computeColorLBGHalfIntensityFast(img,width,startx,starty, colors);
	compressColor(R_BITS59T, G_BITS59T, B_BITS59T, colors, colorsRGB444_no_i);

	// Determine the parameters for the lowest error
	error_no_i = calculateError59Tperceptual1000(img, width, startx, starty, colorsRGB444_no_i, distance_no_i, pixel_indices_no_i);			

	best_error = error_no_i;
	best_distance = distance_no_i[0];
	best_pixel_indices = pixel_indices_no_i[0];
	copyColors(colorsRGB444_no_i, best_colorsRGB444);

	// Put the compress params into the compression block 
	packBlock59T(best_colorsRGB444, best_distance, best_pixel_indices, compressed1, compressed2);

	return best_error;
}

//The below code should compress the block to 59 bits. 
//This is supposed to match the first of the three modes in TWOTIMER.
//
//|63 62 61 60 59|58 57 56 55|54 53 52 51|50 49 48 47|46 45 44 43|42 41 40 39|38 37 36 35|34 33 32|
//|----empty-----|---red 0---|--green 0--|--blue 0---|---red 1---|--green 1--|--blue 1---|--dist--|
//
//|31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
//|----------------------------------------index bits---------------------------------------------|
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &compressed1, int &compressed2)
double compressBlockTHUMB59TFastest(byte[] img,int width,int height,int startx,int starty, int[] compressed1, int[] compressed2) 
{
	double best_error = MAXIMUM_ERROR;
	byte[][] best_colorsRGB444 = new byte[2][3];
	int best_pixel_indices;
	byte best_distance;

	double error_no_i;
	byte[][] colorsRGB444_no_i= new byte[2][3];
	int[] pixel_indices_no_i= new int[1];
	byte[] distance_no_i= new byte[1];

	byte[][] colors= new byte[2][3];

	// Calculate average color using the LBG-algorithm
	computeColorLBGHalfIntensityFast(img,width,startx,starty, colors);
	compressColor(R_BITS59T, G_BITS59T, B_BITS59T, colors, colorsRGB444_no_i);

	// Determine the parameters for the lowest error
	error_no_i = calculateError59T(img, width, startx, starty, colorsRGB444_no_i, distance_no_i, pixel_indices_no_i);			

	best_error = error_no_i;
	best_distance = distance_no_i[0];
	best_pixel_indices = pixel_indices_no_i[0];
	copyColors(colorsRGB444_no_i, best_colorsRGB444);

	// Put the compress params into the compression block 
	packBlock59T(best_colorsRGB444, best_distance, best_pixel_indices, compressed1, compressed2);

	return best_error;
}

//The below code should compress the block to 59 bits. 
//This is supposed to match the first of the three modes in TWOTIMER.
//
//|63 62 61 60 59|58 57 56 55|54 53 52 51|50 49 48 47|46 45 44 43|42 41 40 39|38 37 36 35|34 33 32|
//|----empty-----|---red 0---|--green 0--|--blue 0---|---red 1---|--green 1--|--blue 1---|--dist--|
//
//|31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
//|----------------------------------------index bits---------------------------------------------|
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &compressed1, int &compressed2) 
double compressBlockTHUMB59TFast(byte[] img,int width,int height,int startx,int starty, int[] compressed1, int[] compressed2) 
{
	double best_error = MAXIMUM_ERROR;
	byte[][] best_colorsRGB444= new byte[2][3];
	int best_pixel_indices;
	byte best_distance;

	double error_no_i;
	byte[][] colorsRGB444_no_i= new byte[2][3];
	int[] pixel_indices_no_i= new int[1];
	byte[] distance_no_i= new byte[1];

	double error_half_i;
	byte[][] colorsRGB444_half_i= new byte[2][3];
	int[] pixel_indices_half_i= new int[1];
	byte[] distance_half_i= new byte[1];
	
	double error;
	byte[][] colorsRGB444 = new byte[2][3];
	int[] pixel_indices= new int[1];
	byte[] distance= new byte[1];

	byte[][] colors = new byte[2][3];

	// Calculate average color using the LBG-algorithm
	computeColorLBGNotIntensityFast(img,width,startx,starty, colors);
	compressColor(R_BITS59T, G_BITS59T, B_BITS59T, colors, colorsRGB444_no_i);
	// Determine the parameters for the lowest error
	error_no_i = calculateError59T(img, width, startx, starty, colorsRGB444_no_i, distance_no_i, pixel_indices_no_i);			

	// Calculate average color using the LBG-algorithm
	computeColorLBGHalfIntensityFast(img,width,startx,starty, colors);
	compressColor(R_BITS59T, G_BITS59T, B_BITS59T, colors, colorsRGB444_half_i);
	// Determine the parameters for the lowest error
	error_half_i = calculateError59T(img, width, startx, starty, colorsRGB444_half_i, distance_half_i, pixel_indices_half_i);			

	// Calculate average color using the LBG-algorithm
	computeColorLBGfast(img,width,startx,starty, colors);
	compressColor(R_BITS59T, G_BITS59T, B_BITS59T, colors, colorsRGB444);
	// Determine the parameters for the lowest error
	error = calculateError59T(img, width, startx, starty, colorsRGB444, distance, pixel_indices);			
	
	best_error = error_no_i;
	best_distance = distance_no_i[0];
	best_pixel_indices = pixel_indices_no_i[0];
	copyColors(colorsRGB444_no_i, best_colorsRGB444);

	if(error_half_i < best_error)
	{
		best_error = error_half_i;
		best_distance = distance_half_i[0];
		best_pixel_indices = pixel_indices_half_i[0];
		copyColors (colorsRGB444_half_i, best_colorsRGB444);
	}
	if(error < best_error)
	{
		best_error = error;
		best_distance = distance[0];
		best_pixel_indices = pixel_indices[0];
		copyColors (colorsRGB444, best_colorsRGB444);
	}

	// Put the compress params into the compression block 
	packBlock59T(best_colorsRGB444, best_distance, best_pixel_indices, compressed1, compressed2);

	return best_error;
}

//Calculate the error for the block at position (startx,starty)
//The parameters needed for reconstruction is calculated as well
//
//In the 58H bit mode, we only have pattern H.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(colorsRGB444)[2][3]
//address byte &distance, int &pixel_indices)
static int calculateErrorAndCompress58Hperceptual1000(byte[] srcimg, int width, int startx, int starty, byte[][] colorsRGB444, byte[] distance, int[] pixel_indices) 
{
	int block_error = 0, 
		           best_block_error = MAXERR1000, 
							 pixel_error, 
							 best_pixel_error;
	int[] diff= new int[3];
	int pixel_colors;
	byte[][] possible_colors= new byte[4][3];
	byte[][] colors= new byte[2][3];

	decompressColor(R_BITS58H, G_BITS58H, B_BITS58H, colorsRGB444, colors);

	// Test all distances
	for (byte d = 0; d < BINPOW(TABLE_BITS_58H); ++d) 
	{
		calculatePaintColors58H(d, PATTERN.PATTERN_H, colors, possible_colors);

		block_error = 0;	
		pixel_colors = 0;

		// Loop block
		for (int y = 0; y < BLOCKHEIGHT; ++y) 
		{
			for (int x = 0; x < BLOCKWIDTH; ++x) 
			{
				best_pixel_error = MAXERR1000;
				pixel_colors <<=2; // Make room for next value

				// Loop possible block colors
				for (int c = 0; c < 4; ++c) 
				{
					diff[R] = (srcimg[3*((starty+y)*width+startx+x)+R]&0xff) - (CLAMP(0,possible_colors[c][R],255)&0xff);
					diff[G] = (srcimg[3*((starty+y)*width+startx+x)+G]&0xff) - (CLAMP(0,possible_colors[c][G],255)&0xff);
					diff[B] = (srcimg[3*((starty+y)*width+startx+x)+B]&0xff) - (CLAMP(0,possible_colors[c][B],255)&0xff);

					pixel_error =	PERCEPTUAL_WEIGHT_R_SQUARED_TIMES1000*SQUARE(diff[R]) +
									PERCEPTUAL_WEIGHT_G_SQUARED_TIMES1000*SQUARE(diff[G]) +
									PERCEPTUAL_WEIGHT_B_SQUARED_TIMES1000*SQUARE(diff[B]);

					// Choose best error
					if (pixel_error < best_pixel_error) 
					{
						best_pixel_error = pixel_error;
						pixel_colors ^= (pixel_colors & 3); // Reset the two first bits
						pixel_colors |= c;
					} 
				}
				block_error += best_pixel_error;
			}
		}
		
		if (block_error < best_block_error) 
		{
			best_block_error = block_error;
			distance[0] = d;
			pixel_indices[0] = pixel_colors;
		}
	}
	return best_block_error;
}

//The H-mode but with punchthrough alpha
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(colorsRGB444)[2][3]
//address  byte &distance,  int &pixel_indices) 
static double calculateErrorAndCompress58HAlpha(byte[] srcimg, byte[] alphaimg,int width, int startx, int starty, byte[][] colorsRGB444, byte[] distance,  int[] pixel_indices) 
{
	double block_error = 0, 
		   best_block_error = MAXIMUM_ERROR, 
		   pixel_error, 
		   best_pixel_error;
	int[] diff= new int[3];
	int pixel_colors;
	byte[][] possible_colors= new byte[4][3];
	byte[][] colors= new byte[2][3];
	int alphaindex;
	int[] colorsRGB444_packed= new int[2];
	colorsRGB444_packed[0] = (colorsRGB444[0][R] << 8) + (colorsRGB444[0][G] << 4) + (colorsRGB444[0][B]&0xff);
	colorsRGB444_packed[1] = (colorsRGB444[1][R] << 8) + (colorsRGB444[1][G] << 4) + (colorsRGB444[1][B]&0xff);
	
	decompressColor(R_BITS58H, G_BITS58H, B_BITS58H, colorsRGB444, colors);
	
	// Test all distances
	for (byte d = 0; d < BINPOW(TABLE_BITS_58H); ++d) 
	{
		alphaindex=2;
		if( (colorsRGB444_packed[0] >= colorsRGB444_packed[1]) ^ ((d & 1)==1) )
		{
			//we're going to have to swap the colors to be able to choose this distance.. that means
			//that the indices will be swapped as well, so C1 will be the one with alpha instead of C3..
			alphaindex=0;
		}

		calculatePaintColors58H(d, PATTERN.PATTERN_H, colors, possible_colors);

		block_error = 0;	
		pixel_colors = 0;

		// Loop block
		for (int y = 0; y < BLOCKHEIGHT; ++y) 
		{
			for (int x = 0; x < BLOCKWIDTH; ++x) 
			{
				byte alpha=0;
				if((alphaimg[((starty+y)*width+startx+x)]&0xff)>0)
					alpha=1;
				if((alphaimg[((starty+y)*width+startx+x)]&0xff)>0&&(alphaimg[((starty+y)*width+startx+x)]&0xff)<255)
					System.out.println("INVALID ALPHA DATA!!");
				best_pixel_error = MAXIMUM_ERROR;
				pixel_colors <<=2; // Make room for next value

				// Loop possible block colors
				for (int c = 0; c < 4; ++c) 
				{
					if(c==(alphaindex&alpha)) 
					{
						pixel_error=0;
					}
					else if(c==(alphaindex|alpha)) 
					{
						pixel_error=MAXIMUM_ERROR;
					}
					else 
					{
						diff[R] = (srcimg[3*((starty+y)*width+startx+x)+R]&0xff) - (CLAMP(0,possible_colors[c][R],255)&0xff);
						diff[G] = (srcimg[3*((starty+y)*width+startx+x)+G]&0xff) - (CLAMP(0,possible_colors[c][G],255)&0xff);
						diff[B] = (srcimg[3*((starty+y)*width+startx+x)+B]&0xff) - (CLAMP(0,possible_colors[c][B],255)&0xff);

						pixel_error =	weight[R]*SQUARE(diff[R]) +
										weight[G]*SQUARE(diff[G]) +
										weight[B]*SQUARE(diff[B]);
					}

					// Choose best error
					if (pixel_error < best_pixel_error) 
					{
						best_pixel_error = pixel_error;
						pixel_colors ^= (pixel_colors & 3); // Reset the two first bits
						pixel_colors |= c;
					} 
				}
				block_error += best_pixel_error;
			}
		}
		if (block_error < best_block_error) 
		{
			best_block_error = block_error;
			distance[0] = d;
			pixel_indices[0] = pixel_colors;
		}
	}
	return best_block_error;
}

//Calculate the error for the block at position (startx,starty)
//The parameters needed for reconstruction is calculated as well
//
//In the 58H bit mode, we only have pattern H.
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(colorsRGB444)[2][3]
//address byte &distance, int &pixel_indices) 
static double calculateErrorAndCompress58H(byte[] srcimg, int width, int startx, int starty, byte[][] colorsRGB444, byte[] distance, int[] pixel_indices) 
{
	double block_error = 0, 
	       best_block_error = MAXIMUM_ERROR, 
				 pixel_error, 
				 best_pixel_error;
	int[] diff= new int[3];
	int pixel_colors;
	byte[][] possible_colors = new byte[4][3];
	byte[][] colors= new byte[2][3];

	
	decompressColor(R_BITS58H, G_BITS58H, B_BITS58H, colorsRGB444, colors);

	// Test all distances
	for (byte d = 0; d < BINPOW(TABLE_BITS_58H); ++d) 
	{
		calculatePaintColors58H(d, PATTERN.PATTERN_H, colors, possible_colors);

		block_error = 0;	
		pixel_colors = 0;

		// Loop block
		for (int y = 0; y < BLOCKHEIGHT; ++y) 
		{
			for (int x = 0; x < BLOCKWIDTH; ++x) 
			{
				best_pixel_error = MAXIMUM_ERROR;
				pixel_colors <<=2; // Make room for next value

				// Loop possible block colors
				for (int c = 0; c < 4; ++c) 
				{
					diff[R] = (srcimg[3*((starty+y)*width+startx+x)+R]&0xff) - (CLAMP(0,possible_colors[c][R],255)&0xff);
					diff[G] = (srcimg[3*((starty+y)*width+startx+x)+G]&0xff) - (CLAMP(0,possible_colors[c][G],255)&0xff);
					diff[B] = (srcimg[3*((starty+y)*width+startx+x)+B]&0xff) - (CLAMP(0,possible_colors[c][B],255)&0xff);

					pixel_error =	weight[R]*SQUARE(diff[R]) +
									weight[G]*SQUARE(diff[G]) +
									weight[B]*SQUARE(diff[B]);

					// Choose best error
					if (pixel_error < best_pixel_error) 
					{
						best_pixel_error = pixel_error;
						pixel_colors ^= (pixel_colors & 3); // Reset the two first bits
						pixel_colors |= c;
					} 
				}
				block_error += best_pixel_error;
			}
		}
		
		if (block_error < best_block_error) 
		{
			best_block_error = block_error;
			distance[0] = d;
			pixel_indices[0] = pixel_colors;
		}
	}
		
	return best_block_error;
}

//Makes sure that col0 < col1;
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(colorsRGB444)[2][3]
static void sortColorsRGB444(byte[][] colorsRGB444)
{
	int col0, col1, tcol;

	// sort colors
	col0 = 16*16*(colorsRGB444[0][R]&0xff) + 16*(colorsRGB444[0][G]&0xff) + (colorsRGB444[0][B]&0xff);
	col1 = 16*16*(colorsRGB444[1][R]&0xff) + 16*(colorsRGB444[1][G]&0xff) + (colorsRGB444[1][B]&0xff);

	// After this, col0 should be smaller than col1 (col0 < col1)
	if( col0 > col1)
	{
		tcol = col0;
		col0 = col1;
		col1 = tcol;
	}
	else
	{
		if(col0 == col1)
		{	
			// Both colors are the same. That is useless. If they are both black,
			// col1 can just as well be (0,0,1). Else, col0 can be col1 - 1.
			if(col0 == 0)
				col1 = col0+1;
			else
				col0 = col1-1;
		}
	}
	
	colorsRGB444[0][R] = (byte)GETBITS(col0, 4, 11);
	colorsRGB444[0][G] = (byte)GETBITS(col0, 4, 7);
	colorsRGB444[0][B] = (byte)GETBITS(col0, 4, 3);
	colorsRGB444[1][R] = (byte)GETBITS(col1, 4, 11);
	colorsRGB444[1][G] = (byte)GETBITS(col1, 4, 7);
	colorsRGB444[1][B] = (byte)GETBITS(col1, 4, 3);
}

//The below code should compress the block to 58 bits. 
//The bit layout is thought to be:
//
//|63 62 61 60 59 58|57 56 55 54|53 52 51 50|49 48 47 46|45 44 43 42|41 40 39 38|37 36 35 34|33 32|
//|-------empty-----|---red 0---|--green 0--|--blue 0---|---red 1---|--green 1--|--blue 1---|d2 d1|
//
//|31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
//|----------------------------------------index bits---------------------------------------------|
//
//The distance d is three bits, d2 (MSB), d1 and d0 (LSB). d0 is not stored explicitly. 
//Instead if the 12-bit word red0,green0,blue0 < red1,green1,blue1, d0 is assumed to be 0.
//Else, it is assumed to be 1.
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &compressed1,  int &compressed2) 
int compressBlockTHUMB58HFastestPerceptual1000(byte[] img,int width,int height,int startx,int starty,  int[] compressed1,  int[] compressed2) 
{
	int best_error = MAXERR1000;
	byte[][] best_colorsRGB444= new byte[2][3];
	int best_pixel_indices;
	byte best_distance;

	int error_no_i;
	byte[][] colorsRGB444_no_i= new byte[2][3];
	int[] pixel_indices_no_i= new int[1];
	byte[] distance_no_i= new byte[1];
	byte[][] colors= new byte[2][3];
	
	// Calculate average color using the LBG-algorithm but discarding the intensity in the error function
	computeColorLBGHalfIntensityFast(img, width, startx, starty, colors);
	compressColor(R_BITS58H, G_BITS58H, B_BITS58H, colors, colorsRGB444_no_i);
	sortColorsRGB444(colorsRGB444_no_i);

	error_no_i = calculateErrorAndCompress58Hperceptual1000(img, width, startx, starty, colorsRGB444_no_i, distance_no_i, pixel_indices_no_i);

	best_error = error_no_i;	
	best_distance = distance_no_i[0]; 
	best_pixel_indices = pixel_indices_no_i[0];
	copyColors(colorsRGB444_no_i, best_colorsRGB444);

	//                   | col0 >= col1      col0 < col1
	//------------------------------------------------------
	// (dist & 1) = 1    | no need to swap | need to swap
	//                   |-----------------+----------------
	// (dist & 1) = 0    | need to swap    | no need to swap
	//
	// This can be done with an xor test.

	int[] best_colorsRGB444_packed= new int[2];
	best_colorsRGB444_packed[0] = (best_colorsRGB444[0][R] << 8) + (best_colorsRGB444[0][G] << 4) + (best_colorsRGB444[0][B]&0xff);
	best_colorsRGB444_packed[1] = (best_colorsRGB444[1][R] << 8) + (best_colorsRGB444[1][G] << 4) + (best_colorsRGB444[1][B]&0xff);
	if( (best_colorsRGB444_packed[0] >= best_colorsRGB444_packed[1]) ^ ((best_distance & 1)==1) )
	{
		swapColors(best_colorsRGB444);

		// Reshuffle pixel indices to to exchange C1 with C3, and C2 with C4
		best_pixel_indices = (0x55555555 & best_pixel_indices) | (0xaaaaaaaa & (~best_pixel_indices));
	}
	
	// Put the compress params into the compression block 

	compressed1[0] = 0;

	PUTBITSHIGH( compressed1, best_colorsRGB444[0][R], 4, 57);
	PUTBITSHIGH( compressed1, best_colorsRGB444[0][G], 4, 53);
	PUTBITSHIGH( compressed1, best_colorsRGB444[0][B], 4, 49);
	PUTBITSHIGH( compressed1, best_colorsRGB444[1][R], 4, 45);
	PUTBITSHIGH( compressed1, best_colorsRGB444[1][G], 4, 41);
	PUTBITSHIGH( compressed1, best_colorsRGB444[1][B], 4, 37);
	PUTBITSHIGH( compressed1, (best_distance >> 1), 2, 33);

	compressed2[0] = 0;
	best_pixel_indices=indexConversion(best_pixel_indices);
	PUTBITS( compressed2, best_pixel_indices, 32, 31);

	return best_error;
}

//The below code should compress the block to 58 bits. 
//This is supposed to match the first of the three modes in TWOTIMER.
//The bit layout is thought to be:
//
//|63 62 61 60 59 58|57 56 55 54|53 52 51 50|49 48 47 46|45 44 43 42|41 40 39 38|37 36 35 34|33 32|
//|-------empty-----|---red 0---|--green 0--|--blue 0---|---red 1---|--green 1--|--blue 1---|d2 d1|
//
//|31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
//|----------------------------------------index bits---------------------------------------------|
//
//The distance d is three bits, d2 (MSB), d1 and d0 (LSB). d0 is not stored explicitly. 
//Instead if the 12-bit word red0,green0,blue0 < red1,green1,blue1, d0 is assumed to be 0.
//Else, it is assumed to be 1.
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &compressed1,  int &compressed2)
double compressBlockTHUMB58HFastest(byte[] img,int width,int height,int startx,int starty,  int[] compressed1,  int[] compressed2) 
{
	double best_error = MAXIMUM_ERROR;
	byte[][] best_colorsRGB444= new byte[2][3];
	int best_pixel_indices;
	byte best_distance;

	double error_no_i;
	byte[][] colorsRGB444_no_i= new byte[2][3];
	int[] pixel_indices_no_i= new int[1];
	byte[] distance_no_i= new byte[1];
	byte[][] colors= new byte[2][3];
	
	// Calculate average color using the LBG-algorithm but discarding the intensity in the error function
	computeColorLBGHalfIntensityFast(img, width, startx, starty, colors);
	compressColor(R_BITS58H, G_BITS58H, B_BITS58H, colors, colorsRGB444_no_i);
	sortColorsRGB444(colorsRGB444_no_i);

	error_no_i = calculateErrorAndCompress58H(img, width, startx, starty, colorsRGB444_no_i, distance_no_i, pixel_indices_no_i);

	best_error = error_no_i;	
	best_distance = distance_no_i[0]; 
	best_pixel_indices = pixel_indices_no_i[0];
	copyColors(colorsRGB444_no_i, best_colorsRGB444);

	//                   | col0 >= col1      col0 < col1
	//------------------------------------------------------
	// (dist & 1) = 1    | no need to swap | need to swap
	//                   |-----------------+----------------
	// (dist & 1) = 0    | need to swap    | no need to swap
	//
	// This can be done with an xor test.

	int[] best_colorsRGB444_packed=new int[2];
	best_colorsRGB444_packed[0] = (best_colorsRGB444[0][R] << 8) + (best_colorsRGB444[0][G] << 4) + (best_colorsRGB444[0][B]&0xff);
	best_colorsRGB444_packed[1] = (best_colorsRGB444[1][R] << 8) + (best_colorsRGB444[1][G] << 4) + (best_colorsRGB444[1][B]&0xff);
	if( (best_colorsRGB444_packed[0] >= best_colorsRGB444_packed[1]) ^ ((best_distance & 1)==1) )
	{
		swapColors(best_colorsRGB444);

		// Reshuffle pixel indices to to exchange C1 with C3, and C2 with C4
		best_pixel_indices = (0x55555555 & best_pixel_indices) | (0xaaaaaaaa & (~best_pixel_indices));
	}

	// Put the compress params into the compression block 

	compressed1[0] = 0;

	PUTBITSHIGH( compressed1, best_colorsRGB444[0][R], 4, 57);
	PUTBITSHIGH( compressed1, best_colorsRGB444[0][G], 4, 53);
	PUTBITSHIGH( compressed1, best_colorsRGB444[0][B], 4, 49);
	PUTBITSHIGH( compressed1, best_colorsRGB444[1][R], 4, 45);
	PUTBITSHIGH( compressed1, best_colorsRGB444[1][G], 4, 41);
	PUTBITSHIGH( compressed1, best_colorsRGB444[1][B], 4, 37);
	PUTBITSHIGH( compressed1, (best_distance >> 1), 2, 33);
	best_pixel_indices=indexConversion(best_pixel_indices);
	compressed2[0] = 0;
	PUTBITS( compressed2, best_pixel_indices, 32, 31);

	return best_error;
}

//same as above, but with 1-bit alpha
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address  int &compressed1,  int &compressed2) 
double compressBlockTHUMB58HAlpha(byte[] img, byte[] alphaimg, int width,int height,int startx,int starty,  int[] compressed1,  int[] compressed2) 
{
	double best_error = MAXIMUM_ERROR;
	byte[][] best_colorsRGB444= new byte[2][3];
	int best_pixel_indices;
	byte best_distance;

	double error_no_i;
	byte[][] colorsRGB444_no_i= new byte[2][3];
	int[] pixel_indices_no_i= new int[1];
	byte[] distance_no_i= new byte[1];
	byte[][] colors= new byte[2][3];
	
	// Calculate average color using the LBG-algorithm but discarding the intensity in the error function
	computeColorLBGHalfIntensityFast(img, width, startx, starty, colors);
	compressColor(R_BITS58H, G_BITS58H, B_BITS58H, colors, colorsRGB444_no_i);
	sortColorsRGB444(colorsRGB444_no_i);

	error_no_i = calculateErrorAndCompress58HAlpha(img, alphaimg,width, startx, starty, colorsRGB444_no_i, distance_no_i, pixel_indices_no_i);

	best_error = error_no_i;	
	best_distance = distance_no_i[0]; 
	best_pixel_indices = pixel_indices_no_i[0];
	copyColors(colorsRGB444_no_i, best_colorsRGB444);

	//                   | col0 >= col1      col0 < col1
	//------------------------------------------------------
	// (dist & 1) = 1    | no need to swap | need to swap
	//                   |-----------------+----------------
	// (dist & 1) = 0    | need to swap    | no need to swap
	//
	// This can be done with an xor test.

	int[] best_colorsRGB444_packed=new int[2];
	best_colorsRGB444_packed[0] = (best_colorsRGB444[0][R] << 8) + (best_colorsRGB444[0][G] << 4) + (best_colorsRGB444[0][B]&0xff);
	best_colorsRGB444_packed[1] = (best_colorsRGB444[1][R] << 8) + (best_colorsRGB444[1][G] << 4) + (best_colorsRGB444[1][B]&0xff);
	if( (best_colorsRGB444_packed[0] >= best_colorsRGB444_packed[1]) ^ ((best_distance & 1)==1) )
	{
		swapColors(best_colorsRGB444);

		// Reshuffle pixel indices to to exchange C1 with C3, and C2 with C4
		best_pixel_indices = (0x55555555 & best_pixel_indices) | (0xaaaaaaaa & (~best_pixel_indices));
	}

	// Put the compress params into the compression block 

	compressed1[0] = 0;

	PUTBITSHIGH( compressed1, best_colorsRGB444[0][R], 4, 57);
	PUTBITSHIGH( compressed1, best_colorsRGB444[0][G], 4, 53);
	PUTBITSHIGH( compressed1, best_colorsRGB444[0][B], 4, 49);
	PUTBITSHIGH( compressed1, best_colorsRGB444[1][R], 4, 45);
	PUTBITSHIGH( compressed1, best_colorsRGB444[1][G], 4, 41);
	PUTBITSHIGH( compressed1, best_colorsRGB444[1][B], 4, 37);
	PUTBITSHIGH( compressed1, (best_distance >> 1), 2, 33);
	best_pixel_indices=indexConversion(best_pixel_indices);
	compressed2[0] = 0;
	PUTBITS( compressed2, best_pixel_indices, 32, 31);

	return best_error;
}

//The below code should compress the block to 58 bits. 
//This is supposed to match the first of the three modes in TWOTIMER.
//The bit layout is thought to be:
//
//|63 62 61 60 59 58|57 56 55 54|53 52 51 50|49 48 47 46|45 44 43 42|41 40 39 38|37 36 35 34|33 32|
//|-------empty-----|---red 0---|--green 0--|--blue 0---|---red 1---|--green 1--|--blue 1---|d2 d1|
//
//|31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
//|----------------------------------------index bits---------------------------------------------|
//
//The distance d is three bits, d2 (MSB), d1 and d0 (LSB). d0 is not stored explicitly. 
//Instead if the 12-bit word red0,green0,blue0 < red1,green1,blue1, d0 is assumed to be 0.
//Else, it is assumed to be 1.
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address  int &compressed1,  int &compressed2) 
double compressBlockTHUMB58HFast(byte[] img,int width,int height,int startx,int starty,   int[] compressed1,  int[] compressed2) 
{
	double best_error = MAXIMUM_ERROR;
	byte[][] best_colorsRGB444= new byte[2][3];
	int best_pixel_indices;
	byte best_distance;

	double error_no_i;
	byte[][] colorsRGB444_no_i= new byte[2][3];
	int[] pixel_indices_no_i= new int[1];
	byte[] distance_no_i= new byte[1];

	double error_half_i;
	byte[][] colorsRGB444_half_i= new byte[2][3];
	int[] pixel_indices_half_i= new int[1];
	byte[] distance_half_i= new byte[1];

	double error;
	byte[][] colorsRGB444= new byte[2][3];
	int[] pixel_indices= new int[1];
	byte[] distance= new byte[1];

	byte[][] colors= new byte[2][3];
	
	// Calculate average color using the LBG-algorithm but discarding the intensity in the error function
	computeColorLBGNotIntensity(img, width, startx, starty, colors);
	compressColor(R_BITS58H, G_BITS58H, B_BITS58H, colors, colorsRGB444_no_i);
	sortColorsRGB444(colorsRGB444_no_i);
	error_no_i = calculateErrorAndCompress58H(img, width, startx, starty, colorsRGB444_no_i, distance_no_i, pixel_indices_no_i);

	// Calculate average color using the LBG-algorithm but halfing the influence of the intensity in the error function
	computeColorLBGNotIntensity(img, width, startx, starty, colors);
	compressColor(R_BITS58H, G_BITS58H, B_BITS58H, colors, colorsRGB444_half_i);
	sortColorsRGB444(colorsRGB444_half_i);
	error_half_i = calculateErrorAndCompress58H(img, width, startx, starty, colorsRGB444_half_i, distance_half_i, pixel_indices_half_i);

	// Calculate average color using the LBG-algorithm
	computeColorLBG(img, width, startx, starty, colors);
	compressColor(R_BITS58H, G_BITS58H, B_BITS58H, colors, colorsRGB444);
	sortColorsRGB444(colorsRGB444);
	error = calculateErrorAndCompress58H(img, width, startx, starty, colorsRGB444, distance, pixel_indices);

	best_error = error_no_i;	
	best_distance = distance_no_i[0]; 
	best_pixel_indices = pixel_indices_no_i[0];
	copyColors(colorsRGB444_no_i, best_colorsRGB444);

	if(error_half_i < best_error)
	{
		best_error = error_half_i;
		best_distance = distance_half_i[0];
		best_pixel_indices = pixel_indices_half_i[0];
		copyColors(colorsRGB444_half_i, best_colorsRGB444);
	}

	if(error < best_error)
	{
		best_error = error;	
		best_distance = distance[0]; 
		best_pixel_indices = pixel_indices[0];
		copyColors(colorsRGB444, best_colorsRGB444);
	}

	//                   | col0 >= col1      col0 < col1
	//------------------------------------------------------
	// (dist & 1) = 1    | no need to swap | need to swap
	//                   |-----------------+----------------
	// (dist & 1) = 0    | need to swap    | no need to swap
	//
	// This can be done with an xor test.

	int[] best_colorsRGB444_packed= new int[2];
	best_colorsRGB444_packed[0] = (best_colorsRGB444[0][R] << 8) + (best_colorsRGB444[0][G] << 4) + (best_colorsRGB444[0][B]&0xff);
	best_colorsRGB444_packed[1] = (best_colorsRGB444[1][R] << 8) + (best_colorsRGB444[1][G] << 4) + (best_colorsRGB444[1][B]&0xff);
	if( (best_colorsRGB444_packed[0] >= best_colorsRGB444_packed[1]) ^ ((best_distance & 1)==1) )
	{
		swapColors(best_colorsRGB444);

		// Reshuffle pixel indices to to exchange C1 with C3, and C2 with C4
		best_pixel_indices = (0x55555555 & best_pixel_indices) | (0xaaaaaaaa & (~best_pixel_indices));
	}

	// Put the compress params into the compression block 
	compressed1[0] = 0;

	PUTBITSHIGH( compressed1, best_colorsRGB444[0][R], 4, 57);
	PUTBITSHIGH( compressed1, best_colorsRGB444[0][G], 4, 53);
	PUTBITSHIGH( compressed1, best_colorsRGB444[0][B], 4, 49);
	PUTBITSHIGH( compressed1, best_colorsRGB444[1][R], 4, 45);
	PUTBITSHIGH( compressed1, best_colorsRGB444[1][G], 4, 41);
	PUTBITSHIGH( compressed1, best_colorsRGB444[1][B], 4, 37);
	PUTBITSHIGH( compressed1, (best_distance >> 1), 2, 33);
	best_pixel_indices=indexConversion(best_pixel_indices);
	compressed2[0] = 0;
	PUTBITS( compressed2, best_pixel_indices, 32, 31);

	return best_error;
}

//Compress block testing both individual and differential mode.
//Perceptual error metric.
//Combined quantization for colors.
//Both flipped and unflipped tested.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address  int &compressed1,  int &compressed2) 
static void compressBlockDiffFlipCombinedPerceptual(byte[] img,int width,int height,int startx,int starty,    int[] compressed1,  int[] compressed2) 
{

	int[] compressed1_norm= new int[1], compressed2_norm= new int[1];
	int[] compressed1_flip= new int[1], compressed2_flip= new int[1];
	byte[] avg_color_quant1= new byte[3], avg_color_quant2= new byte[3];

	float[] avg_color_float1= new float[3],avg_color_float2= new float[3];
	int[] enc_color1= new int[3], enc_color2= new int[3], diff= new int[3];
	int min_error=255*255*8*3;
	int best_table_indices1=0, best_table_indices2=0;
	int[] best_table1=new int[1], best_table2=new int[1];
	int diffbit;

	int norm_err=0;
	int flip_err=0;

	// First try normal blocks 2x4:

	computeAverageColor2x4noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor2x4noQuantFloat(img,width,height,startx+2,starty,avg_color_float2);

	// First test if avg_color1 is similar enough to avg_color2 so that
	// we can use differential coding of colors. 

	float eps;

	byte[] dummy= new byte[3];

	quantize555ColorCombinedPerceptual(avg_color_float1, enc_color1, dummy);
	quantize555ColorCombinedPerceptual(avg_color_float2, enc_color2, dummy);

	diff[0] = enc_color2[0]-enc_color1[0];	
	diff[1] = enc_color2[1]-enc_color1[1];	
	diff[2] = enc_color2[2]-enc_color1[2];

	if( (diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4) && (diff[2] <= 3) )
	{
		diffbit = 1;

		// The difference to be coded:

		diff[0] = enc_color2[0]-enc_color1[0];	
		diff[1] = enc_color2[1]-enc_color1[1];	
		diff[2] = enc_color2[2]-enc_color1[2];

		avg_color_quant1[0] = (byte)(enc_color1[0] << 3 | (enc_color1[0] >> 2));
		avg_color_quant1[1] = (byte)(enc_color1[1] << 3 | (enc_color1[1] >> 2));
		avg_color_quant1[2] = (byte)(enc_color1[2] << 3 | (enc_color1[2] >> 2));
		avg_color_quant2[0] = (byte)(enc_color2[0] << 3 | (enc_color2[0] >> 2));
		avg_color_quant2[1] = (byte)(enc_color2[1] << 3 | (enc_color2[1] >> 2));
		avg_color_quant2[2] = (byte)(enc_color2[2] << 3 | (enc_color2[2] >> 2));

		// Pack bits into the first word. 

		//     ETC1_RGB8_OES:
		// 
		//     a) bit layout in bits 63 through 32 if diffbit = 0
		// 
		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
		//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------
		//     
		//     b) bit layout in bits 63 through 32 if diffbit = 1
		// 
		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1    | dcol 2 | base col1    | dcol 2 | base col 1   | dcol 2 | table  | table  |diff|flip|
		//     | R1' (5 bits) | dR2    | G1' (5 bits) | dG2    | B1' (5 bits) | dB2    | cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------
		// 
		//     c) bit layout in bits 31 through 0 (in both cases)
		// 
		//      31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3   2   1  0
		//      --------------------------------------------------------------------------------------------------
		//     |       most significant pixel index bits       |         least significant pixel index bits       |  
		//     | p| o| n| m| l| k| j| i| h| g| f| e| d| c| b| a| p| o| n| m| l| k| j| i| h| g| f| e| d| c | b | a |
		//      --------------------------------------------------------------------------------------------------      

		compressed1_norm[0] = 0;
		PUTBITSHIGH( compressed1_norm, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_norm, enc_color1[0], 5, 63);
		PUTBITSHIGH( compressed1_norm, enc_color1[1], 5, 55);
		PUTBITSHIGH( compressed1_norm, enc_color1[2], 5, 47);
		PUTBITSHIGH( compressed1_norm, diff[0],       3, 58);
		PUTBITSHIGH( compressed1_norm, diff[1],       3, 50);
		PUTBITSHIGH( compressed1_norm, diff[2],       3, 42);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];

		norm_err = 0;

		// left part of block
		norm_err = tryalltables_3bittable2x4percep(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);

		// right part of block
		norm_err += tryalltables_3bittable2x4percep(img,width,height,startx+2,starty,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_norm, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_norm, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_norm,           0,   1, 32);

		compressed2_norm[0] = 0;
		PUTBITS( compressed2_norm, (best_pixel_indices1_MSB[0]     ), 8, 23);
		PUTBITS( compressed2_norm, (best_pixel_indices2_MSB[0]     ), 8, 31);
		PUTBITS( compressed2_norm, (best_pixel_indices1_LSB[0]     ), 8, 7);
		PUTBITS( compressed2_norm, (best_pixel_indices2_LSB[0]     ), 8, 15);
	}
	else
	{
		diffbit = 0;
		// The difference is bigger than what fits in 555 plus delta-333, so we will have
		// to deal with 444 444.

		eps = (float) 0.0001;

		quantize444ColorCombinedPerceptual(avg_color_float1, enc_color1, dummy);
		quantize444ColorCombinedPerceptual(avg_color_float2, enc_color2, dummy);

		avg_color_quant1[0] = (byte)(enc_color1[0] << 4 | enc_color1[0]); 
		avg_color_quant1[1] = (byte)(enc_color1[1] << 4 | enc_color1[1]); 
		avg_color_quant1[2] = (byte)(enc_color1[2] << 4 | enc_color1[2]);
		avg_color_quant2[0] = (byte)(enc_color2[0] << 4 | enc_color2[0]); 
		avg_color_quant2[1] = (byte)(enc_color2[1] << 4 | enc_color2[1]); 
		avg_color_quant2[2] = (byte)(enc_color2[2] << 4 | enc_color2[2]);

		// Pack bits into the first word. 

		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
		//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------

		compressed1_norm[0] = 0;
		PUTBITSHIGH( compressed1_norm, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_norm, enc_color1[0], 4, 63);
		PUTBITSHIGH( compressed1_norm, enc_color1[1], 4, 55);
		PUTBITSHIGH( compressed1_norm, enc_color1[2], 4, 47);
		PUTBITSHIGH( compressed1_norm, enc_color2[0], 4, 59);
		PUTBITSHIGH( compressed1_norm, enc_color2[1], 4, 51);
		PUTBITSHIGH( compressed1_norm, enc_color2[2], 4, 43);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];
		
		// left part of block
		norm_err = tryalltables_3bittable2x4percep(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);

		// right part of block
		norm_err += tryalltables_3bittable2x4percep(img,width,height,startx+2,starty,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_norm, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_norm, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_norm,           0,   1, 32);

		compressed2_norm[0] = 0;
		PUTBITS( compressed2_norm, (best_pixel_indices1_MSB[0]     ), 8, 23);
		PUTBITS( compressed2_norm, (best_pixel_indices2_MSB[0]     ), 8, 31);
		PUTBITS( compressed2_norm, (best_pixel_indices1_LSB[0]     ), 8, 7);
		PUTBITS( compressed2_norm, (best_pixel_indices2_LSB[0]     ), 8, 15);
	}

	// Now try flipped blocks 4x2:
	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty,avg_color_float1);
	computeAverageColor4x2noQuantFloat(img,width,height,startx,starty+2,avg_color_float2);

	// First test if avg_color1 is similar enough to avg_color2 so that
	// we can use differential coding of colors. 
	quantize555ColorCombinedPerceptual(avg_color_float1, enc_color1, dummy);
	quantize555ColorCombinedPerceptual(avg_color_float2, enc_color2, dummy);

	diff[0] = enc_color2[0]-enc_color1[0];	
	diff[1] = enc_color2[1]-enc_color1[1];	
	diff[2] = enc_color2[2]-enc_color1[2];

	if( (diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4) && (diff[2] <= 3) )
	{
		diffbit = 1;

		// The difference to be coded:
		diff[0] = enc_color2[0]-enc_color1[0];	
		diff[1] = enc_color2[1]-enc_color1[1];	
		diff[2] = enc_color2[2]-enc_color1[2];

		avg_color_quant1[0] = (byte)(enc_color1[0] << 3 | (enc_color1[0] >> 2));
		avg_color_quant1[1] = (byte)(enc_color1[1] << 3 | (enc_color1[1] >> 2));
		avg_color_quant1[2] = (byte)(enc_color1[2] << 3 | (enc_color1[2] >> 2));
		avg_color_quant2[0] = (byte)(enc_color2[0] << 3 | (enc_color2[0] >> 2));
		avg_color_quant2[1] = (byte)(enc_color2[1] << 3 | (enc_color2[1] >> 2));
		avg_color_quant2[2] = (byte)(enc_color2[2] << 3 | (enc_color2[2] >> 2));

		// Pack bits into the first word. 
		compressed1_flip[0] = 0;
		PUTBITSHIGH( compressed1_flip, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_flip, enc_color1[0], 5, 63);
		PUTBITSHIGH( compressed1_flip, enc_color1[1], 5, 55);
		PUTBITSHIGH( compressed1_flip, enc_color1[2], 5, 47);
		PUTBITSHIGH( compressed1_flip, diff[0],       3, 58);
		PUTBITSHIGH( compressed1_flip, diff[1],       3, 50);
		PUTBITSHIGH( compressed1_flip, diff[2],       3, 42);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];

		// upper part of block
		flip_err = tryalltables_3bittable4x2percep(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);
		// lower part of block
		flip_err += tryalltables_3bittable4x2percep(img,width,height,startx,starty+2,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_flip, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_flip, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_flip,           1,   1, 32);

		best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
		best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);
		
		compressed2_flip[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16) | (best_pixel_indices1_LSB[0] & 0xffff);
	}
	else
	{
		diffbit = 0;
		// The difference is bigger than what fits in 555 plus delta-333, so we will have
		// to deal with 444 444.
		eps = (float) 0.0001;

		quantize444ColorCombinedPerceptual(avg_color_float1, enc_color1, dummy);
		quantize444ColorCombinedPerceptual(avg_color_float2, enc_color2, dummy);

		avg_color_quant1[0] = (byte)(enc_color1[0] << 4 | enc_color1[0]); 
		avg_color_quant1[1] = (byte)(enc_color1[1] << 4 | enc_color1[1]); 
		avg_color_quant1[2] = (byte)(enc_color1[2] << 4 | enc_color1[2]);
		avg_color_quant2[0] = (byte)(enc_color2[0] << 4 | enc_color2[0]); 
		avg_color_quant2[1] = (byte)(enc_color2[1] << 4 | enc_color2[1]); 
		avg_color_quant2[2] = (byte)(enc_color2[2] << 4 | enc_color2[2]);

		//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
		//      ---------------------------------------------------------------------------------------------------
		//     | base col1 | base col2 | base col1 | base col2 | base col1 | base col2 | table  | table  |diff|flip|
		//     | R1 (4bits)| R2 (4bits)| G1 (4bits)| G2 (4bits)| B1 (4bits)| B2 (4bits)| cw 1   | cw 2   |bit |bit |
		//      ---------------------------------------------------------------------------------------------------

		// Pack bits into the first word. 
		compressed1_flip[0] = 0;
		PUTBITSHIGH( compressed1_flip, diffbit,       1, 33);
		PUTBITSHIGH( compressed1_flip, enc_color1[0], 4, 63);
		PUTBITSHIGH( compressed1_flip, enc_color1[1], 4, 55);
		PUTBITSHIGH( compressed1_flip, enc_color1[2], 4, 47);
		PUTBITSHIGH( compressed1_flip, enc_color2[0], 4, 59);
		PUTBITSHIGH( compressed1_flip, enc_color2[1], 4, 51);
		PUTBITSHIGH( compressed1_flip, enc_color2[2], 4, 43);

		int[] best_pixel_indices1_MSB = new int[1];
		int[] best_pixel_indices1_LSB = new int[1];
		int[] best_pixel_indices2_MSB = new int[1];
		int[] best_pixel_indices2_LSB = new int[1];

		// upper part of block
		flip_err = tryalltables_3bittable4x2percep(img,width,height,startx,starty,avg_color_quant1,best_table1,best_pixel_indices1_MSB, best_pixel_indices1_LSB);
		// lower part of block
		flip_err += tryalltables_3bittable4x2percep(img,width,height,startx,starty+2,avg_color_quant2,best_table2,best_pixel_indices2_MSB, best_pixel_indices2_LSB);

		PUTBITSHIGH( compressed1_flip, best_table1[0],   3, 39);
		PUTBITSHIGH( compressed1_flip, best_table2[0],   3, 36);
		PUTBITSHIGH( compressed1_flip,           1,   1, 32);

		best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
		best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);
		
		compressed2_flip[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16) | (best_pixel_indices1_LSB[0] & 0xffff);
	}

	// Now lets see which is the best table to use. Only 8 tables are possible. 
	if(norm_err <= flip_err)
	{
		compressed1[0] = compressed1_norm[0] | 0;
		compressed2[0] = compressed2_norm[0];
	}
	else
	{
		compressed1[0] = compressed1_flip[0] | 1;
		compressed2[0] = compressed2_flip[0];
	}
}

//Calculate the error of a block
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static double calcBlockErrorRGB(byte[] img, byte[] imgdec, int width, int height, int startx, int starty)
{
	int xx,yy;
	double err;

	err = 0;

	for(xx = startx; xx< startx+4; xx++)
	{
		for(yy = starty; yy<starty+4; yy++)
		{
			err += SQUARE(1.0*(RED(img,width,xx,yy)&0xff)  - 1.0*(RED(imgdec, width, xx,yy)&0xff));
			err += SQUARE(1.0*(GREEN(img,width,xx,yy)&0xff)- 1.0*(GREEN(imgdec, width, xx,yy)&0xff));
			err += SQUARE(1.0*(BLUE(img,width,xx,yy)&0xff) - 1.0*(BLUE(imgdec, width, xx,yy)&0xff));
		}
	}

	return err;
}

//Calculate the perceptually weighted error of a block
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static double calcBlockPerceptualErrorRGB(byte[] img, byte[] imgdec, int width, int height, int startx, int starty)
{
	int xx,yy;
	double err;

	err = 0;

	for(xx = startx; xx< startx+4; xx++)
	{
		for(yy = starty; yy<starty+4; yy++)
		{			
			err += PERCEPTUAL_WEIGHT_R_SQUARED*SQUARE(1.0*(RED(img,width,xx,yy)&0xff)  - 1.0*(RED(imgdec, width, xx,yy)&0xff));
			err += PERCEPTUAL_WEIGHT_G_SQUARED*SQUARE(1.0*(GREEN(img,width,xx,yy)&0xff)- 1.0*(GREEN(imgdec, width, xx,yy)&0xff));
			err += PERCEPTUAL_WEIGHT_B_SQUARED*SQUARE(1.0*(BLUE(img,width,xx,yy)&0xff) - 1.0*(BLUE(imgdec, width, xx,yy)&0xff));
		}
	}

	return err;
}

//Compress an ETC1 block (or the individual and differential modes of an ETC2 block)
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//address int &compressed1,  int &compressed2)
static double compressBlockDiffFlipFast(byte[] img, byte[] imgdec,int width,int height,int startx,int starty,  int[] compressed1,  int[] compressed2)
{
	int[] average_block1 = new int[1];
	int[] average_block2 = new int[1];
	double error_average;

	int[] combined_block1 = new int[1];
	int[] combined_block2 = new int[1];
	double error_combined;

	double best_error;

	// First quantize the average color to the nearest neighbor.
	compressBlockDiffFlipAverage(img, width, height, startx, starty, average_block1, average_block2);
	decompressBlockDiffFlip(average_block1[0], average_block2[0], imgdec, width, height, startx, starty);
	error_average = calcBlockErrorRGB(img, imgdec, width, height, startx, starty);

	// Then quantize the average color taking into consideration that intensity can change
	compressBlockDiffFlipCombined(img, width, height, startx, starty, combined_block1, combined_block2);
	decompressBlockDiffFlip(combined_block1[0], combined_block2[0], imgdec, width, height, startx, starty);
	error_combined = calcBlockErrorRGB(img, imgdec, width, height, startx, starty);

	if(error_combined < error_average)
	{
		compressed1[0] = combined_block1[0];
		compressed2[0] = combined_block2[0];
		best_error = error_combined;
	}
	else
	{
		compressed1[0] = average_block1[0];
		compressed2[0] = average_block2[0];
		best_error = error_average;
	}
	return best_error;
}

//Compress an ETC1 block (or the individual and differential modes of an ETC2 block)
//Uses perceptual error metric.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//address  int &compressed1,  int &compressed2)
static void compressBlockDiffFlipFastPerceptual(byte[] img, byte[] imgdec,int width,int height,int startx,int starty,  int[] compressed1,  int[] compressed2)
{
	int[] average_block1 = new int[1];
	int[] average_block2 = new int[1];
	double error_average;

	int[] combined_block1 = new int[1];
	int[] combined_block2 = new int[1];
	double error_combined;

	// First quantize the average color to the nearest neighbor.
	compressBlockDiffFlipAveragePerceptual(img, width, height, startx, starty, average_block1, average_block2);
	decompressBlockDiffFlip(average_block1[0], average_block2[0], imgdec, width, height, startx, starty);
	error_average = calcBlockPerceptualErrorRGB(img, imgdec, width, height, startx, starty);

	// Then quantize the average color taking into consideration that intensity can change 
	compressBlockDiffFlipCombinedPerceptual(img, width, height, startx, starty, combined_block1, combined_block2);
	decompressBlockDiffFlip(combined_block1[0], combined_block2[0], imgdec, width, height, startx, starty);
	error_combined = calcBlockPerceptualErrorRGB(img, imgdec, width, height, startx, starty);

	if(error_combined < error_average)
	{
		compressed1[0] = combined_block1[0];
		compressed2[0] = combined_block2[0];
	}
	else
	{
		compressed1[0] = average_block1[0];
		compressed2[0] = average_block2[0];
	}
}

//Compresses the differential mode of an ETC2 block with punchthrough alpha
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//address int &etc1_word1,  int &etc1_word2) 
static void compressBlockDifferentialWithAlpha(boolean isTransparent, byte[] img, byte[] alphaimg, byte[] imgdec, int width, int height, int startx, int starty,  int[] etc1_word1,  int[] etc1_word2) 
{
	int[] compressed1_norm= new int[1], compressed2_norm= new int[1];
	int[] compressed1_flip= new int[1], compressed2_flip= new int[1];
	int[] compressed1_temp= new int[1], compressed2_temp= new int[1];
	byte[] avg_color_quant1= new byte[3], avg_color_quant2= new byte[3];

	float[] avg_color_float1= new float[3],avg_color_float2= new float[3];
	int[] enc_color1= new int[3], enc_color2= new int[3], diff= new int[3];
	int min_error=255*255*8*3;
	
	int norm_err=0;
	int flip_err=0;
	int temp_err=0;
	for(int flipbit=0; flipbit<2; flipbit++) 
	{
		//compute average color for each half.
		for(int c=0; c<3; c++) 
		{
			avg_color_float1[c]=0;
			avg_color_float2[c]=0;
			float sum1=0;
			float sum2=0;
			for(int x=0; x<4; x++) 
			{
				for(int y=0; y<4; y++) 
				{
					float fac=1;
					int index = x+startx+(y+starty)*width;
					//transparent pixels are only barely figured into the average. This ensures that they DO matter if we have only
					//transparent pixels in one half of the block, and not otherwise. A bit ugly perhaps.
					if((alphaimg[index]&0xff)<128)
						fac=0.0001f;
					float col = fac*(img[index*3+c]&0xff);
					if( (flipbit==0&&x<2) || (flipbit==1&&y<2) ) 
					{
						sum1+=fac;
						avg_color_float1[c]+=col;
					}
					else 
					{
						sum2+=fac;
						avg_color_float2[c]+=col;
					}
				}
			}
			avg_color_float1[c]/=sum1;
			avg_color_float2[c]/=sum2;
		}
		float[] dummy= new float[3];
		quantize555ColorCombined(avg_color_float1, enc_color1, dummy);
		quantize555ColorCombined(avg_color_float2, enc_color2, dummy);

		diff[0] = enc_color2[0]-enc_color1[0];	
		diff[1] = enc_color2[1]-enc_color1[1];	
		diff[2] = enc_color2[2]-enc_color1[2];

		//make sure diff is small enough for diff-coding
		for(int c=0; c<3; c++) 
		{
				if(diff[c]<-4)
					diff[c]=-4;
				if(diff[c]>3)
					diff[c]=3;
				enc_color2[c]=enc_color1[c]+diff[c];
		}

		avg_color_quant1[0] = (byte)(enc_color1[0] << 3 | (enc_color1[0] >> 2));
		avg_color_quant1[1] = (byte)(enc_color1[1] << 3 | (enc_color1[1] >> 2));
		avg_color_quant1[2] = (byte)(enc_color1[2] << 3 | (enc_color1[2] >> 2));
		avg_color_quant2[0] = (byte)(enc_color2[0] << 3 | (enc_color2[0] >> 2));
		avg_color_quant2[1] = (byte)(enc_color2[1] << 3 | (enc_color2[1] >> 2));
		avg_color_quant2[2] = (byte)(enc_color2[2] << 3 | (enc_color2[2] >> 2));

		// Pack bits into the first word. 
		// see regular compressblockdiffflipfast for details

		compressed1_temp[0] = 0;
		PUTBITSHIGH( compressed1_temp, !isTransparent ? 1 : 0,       1, 33);
		PUTBITSHIGH( compressed1_temp, enc_color1[0], 5, 63);
		PUTBITSHIGH( compressed1_temp, enc_color1[1], 5, 55);
		PUTBITSHIGH( compressed1_temp, enc_color1[2], 5, 47);
		PUTBITSHIGH( compressed1_temp, diff[0],       3, 58);
		PUTBITSHIGH( compressed1_temp, diff[1],       3, 50);
		PUTBITSHIGH( compressed1_temp, diff[2],       3, 42);

		temp_err = 0;
		
		int[] besterror= new int[2];
		besterror[0]=255*255*3*16;
		besterror[1]=255*255*3*16;
		int[] besttable= new int[2];
		int[] best_indices_LSB= new int[16];
		int[] best_indices_MSB= new int[16];
		//for each table, we're going to compute the indices required to get minimum error in each half.
		//then we'll check if this was the best table for either half, and set besterror/besttable accordingly.
		for(int table=0; table<8; table++) 
		{
			int[] taberror= new int[2];//count will be sort of an index of each pixel within a half, determining where the index will be placed in the bitstream.
			
			int[] pixel_indices_LSB= new int[16],pixel_indices_MSB= new int[16];
			
			for(int i=0; i<2; i++) 
			{
				taberror[i]=0;
			}
			for(int x=0; x<4; x++) 
			{
				for(int y=0; y<4; y++) 
				{
					int index = x+startx+(y+starty)*width;
					byte[] basecol= new byte[3];
					boolean transparentPixel=(alphaimg[index]&0xff)<128;
					//determine which half of the block this pixel is in, based on the flipbit.
					int half=0;
					if( (flipbit==0&&x<2) || (flipbit!=0&&y<2) ) 
					{
						basecol[0]=avg_color_quant1[0];
						basecol[1]=avg_color_quant1[1];
						basecol[2]=avg_color_quant1[2];
					}
					else 
					{
						half=1;
						basecol[0]=avg_color_quant2[0];
						basecol[1]=avg_color_quant2[1];
						basecol[2]=avg_color_quant2[2];
					}
					int besterri=255*255*3*2;
					int besti=0;
					int erri;
					for(int i=0; i<4; i++) 
					{
						if(i==1&&isTransparent)
							continue;
						erri=0;
						for(int c=0; c<3; c++) 
						{
							int col=CLAMP(0,((int)(basecol[c]&0xff))+compressParams[table*2][i],255);
							if(i==2&&isTransparent) 
							{
								 col=(int)(basecol[c]&0xff);
							}
							int errcol=col-((int)((img[index*3+c]&0xff)));
							erri=erri+(errcol*errcol);
						}
						if(erri<besterri) 
						{
							besterri=erri;
							besti=i;
						}
					}
					if(transparentPixel) 
					{
						besterri=0;
						besti=1;
					}
					//the best index for this pixel using this table for its half is known.
					//add its error to error for this table and half.
					taberror[half]+=besterri;
					//store the index! we might toss it later though if this was a bad table.

					int pixel_index = scramble[besti];

					pixel_indices_MSB[x*4+y]=(pixel_index >> 1);
					pixel_indices_LSB[x*4+y]=(pixel_index & 1);
				}
			}
			for(int half=0; half<2; half++) 
			{
				if(taberror[half]<besterror[half]) 
				{
					besterror[half]=taberror[half];
					besttable[half]=table;
					for(int i=0; i<16; i++) 
					{
						int thishalf=0;
						int y=i%4;
						int x=i/4;
						if( !(flipbit==0&&x<2) && !(flipbit!=0&&y<2) )
							thishalf=1;
						if(half!=thishalf) //this pixel is not in given half, don't update best index!
							continue;
						best_indices_MSB[i]=pixel_indices_MSB[i];
						best_indices_LSB[i]=pixel_indices_LSB[i];
					}
				}
			}
		}
		PUTBITSHIGH( compressed1_temp, besttable[0],   3, 39);
		PUTBITSHIGH( compressed1_temp, besttable[1],   3, 36);
		PUTBITSHIGH( compressed1_temp,      0,   1, 32);

		compressed2_temp[0] = 0;
		for(int i=0; i<16; i++) 
		{
			PUTBITS( compressed2_temp, (best_indices_MSB[i]  ), 1, 16+i);
			PUTBITS( compressed2_temp, (best_indices_LSB[i]  ), 1, i);
		}
		
		if(flipbit!=0) 
		{
			flip_err=besterror[0]+besterror[1];
			compressed1_flip[0]=compressed1_temp[0];
			compressed2_flip[0]=compressed2_temp[0];
		}
		else 
		{
			norm_err=besterror[0]+besterror[1];
			compressed1_norm[0]=compressed1_temp[0];
			compressed2_norm[0]=compressed2_temp[0];
		}
	}
	// Now to find out if flipping was a good idea or not

	if(norm_err <= flip_err)
	{
		etc1_word1[0] = compressed1_norm[0] | 0;
		etc1_word2[0] = compressed2_norm[0];
	}
	else
	{
		etc1_word1[0] = compressed1_flip[0] | 1;
		etc1_word2[0] = compressed2_flip[0];
	}
}


//Calculate RGBA error --- only count non-transparent pixels (alpha > 128)
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static double calcBlockErrorRGBA(byte[] img, byte[] imgdec, byte[] alpha, int width, int height, int startx, int starty)
{
	int xx,yy;
	double err;

	err = 0;

	for(xx = startx; xx< startx+4; xx++)
	{
		for(yy = starty; yy<starty+4; yy++)
		{
			//only count non-transparent pixels.
			if((alpha[yy*width+xx]&0xff)>128)	
			{
				err += SQUARE(1.0*(RED(img,width,xx,yy)&0xff)  - 1.0*(RED(imgdec, width, xx,yy)&0xff));
				err += SQUARE(1.0*(GREEN(img,width,xx,yy)&0xff)- 1.0*(GREEN(imgdec, width, xx,yy)&0xff));
				err += SQUARE(1.0*(BLUE(img,width,xx,yy)&0xff) - 1.0*(BLUE(imgdec, width, xx,yy)&0xff));
			}
		}
	}
	return err;
}

//calculates the error for a block using the given colors, and the paremeters required to obtain the error. This version uses 1-bit punch-through alpha.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte(colorsRGB444)[2][3]
	//address byte &distance,  int &pixel_indices) 
static double calculateError59TAlpha(byte[] srcimg, byte[] alpha,int width, int startx, int starty, byte[][] colorsRGB444, byte[] distance,  int[] pixel_indices) 
{

	double block_error = 0, 
		   best_block_error = MAXIMUM_ERROR, 
		   pixel_error, 
		   best_pixel_error;
	int[] diff= new int[3];
	byte best_sw=0;
	int pixel_colors;
	byte[][] colors= new byte[2][3];
	byte[][] possible_colors= new byte[4][3];

	// First use the colors as they are, then swap them
	for (byte sw = 0; sw <2; ++sw) 
	{ 
		if (sw == 1) 
		{
			swapColors(colorsRGB444);
		}
		decompressColor(R_BITS59T, G_BITS59T, B_BITS59T, colorsRGB444, colors);

		// Test all distances
		for (byte d = 0; d < BINPOW(TABLE_BITS_59T); ++d) 
		{
			calculatePaintColors59T(d,PATTERN.PATTERN_T, colors, possible_colors);
			
			block_error = 0;	
			pixel_colors = 0;

			// Loop block
			for (int y = 0; y < BLOCKHEIGHT; ++y) 
			{
				for (int x = 0; x < BLOCKWIDTH; ++x) 
				{
					best_pixel_error = MAXIMUM_ERROR;
					pixel_colors <<=2; // Make room for next value

					// Loop possible block colors
					if(alpha[x+startx+(y+starty)*width]==0) 
					{
						best_pixel_error=0;
						pixel_colors ^= (pixel_colors & 3); // Reset the two first bits
						pixel_colors |= 2; //insert the index for this pixel, two meaning transparent.
					}
					else 
					{
						for (int c = 0; c < 4; ++c) 
						{
							
							if(c==2) 
								continue; //don't use this, because we don't have alpha here and index 2 means transparent.
							diff[R] = (srcimg[3*((starty+y)*width+startx+x)+R]&0xff) - (CLAMP(0,possible_colors[c][R],255)&0xff);
							diff[G] = (srcimg[3*((starty+y)*width+startx+x)+G]&0xff) - (CLAMP(0,possible_colors[c][G],255)&0xff);
							diff[B] = (srcimg[3*((starty+y)*width+startx+x)+B]&0xff) - (CLAMP(0,possible_colors[c][B],255)&0xff);

							pixel_error =	weight[R]*SQUARE(diff[R]) +
											weight[G]*SQUARE(diff[G]) +
											weight[B]*SQUARE(diff[B]);

							// Choose best error
							if (pixel_error < best_pixel_error) 
							{
								best_pixel_error = pixel_error;
								pixel_colors ^= (pixel_colors & 3); // Reset the two first bits
								pixel_colors |= c; //insert the index for this pixel
							} 
						}
					}
					block_error += best_pixel_error;
				}
			}
			if (block_error < best_block_error) 
			{
				best_block_error = block_error;
				distance[0] = d;
				pixel_indices[0] = pixel_colors;
				best_sw = sw;
			}
		}
		
		if (sw == 1 && best_sw == 0) 
		{
			swapColors(colorsRGB444);
		}
		decompressColor(R_BITS59T, G_BITS59T, B_BITS59T, colorsRGB444, colors);
	}
	return best_block_error;
}

//same as fastest t-mode compressor above, but here one of the colors (the central one in the T) is used to also signal that the pixel is transparent.
//the only difference is that calculateError has been swapped out to one that considers alpha.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//address int &compressed1,  int &compressed2) 
double compressBlockTHUMB59TAlpha(byte[] img, byte[] alpha, int width,int height,int startx,int starty,  int[] compressed1,  int[] compressed2) 
{
	double best_error = MAXIMUM_ERROR;
	byte[][] best_colorsRGB444= new byte[2][3];
	int best_pixel_indices;
	byte best_distance;

	double error_no_i;
	byte[][] colorsRGB444_no_i= new byte[2][3];
	int[] pixel_indices_no_i= new int[1];
	byte[] distance_no_i= new byte[1];

	byte[][] colors= new byte[2][3];

	// Calculate average color using the LBG-algorithm
	computeColorLBGHalfIntensityFast(img,width,startx,starty, colors);
	compressColor(R_BITS59T, G_BITS59T, B_BITS59T, colors, colorsRGB444_no_i);

	// Determine the parameters for the lowest error
	error_no_i = calculateError59TAlpha(img, alpha, width, startx, starty, colorsRGB444_no_i, distance_no_i, pixel_indices_no_i);			

	best_error = error_no_i;
	best_distance = distance_no_i[0];
	best_pixel_indices = pixel_indices_no_i[0];
	copyColors(colorsRGB444_no_i, best_colorsRGB444);

	// Put the compress params into the compression block 
	packBlock59T(best_colorsRGB444, best_distance, best_pixel_indices, compressed1, compressed2);

	return best_error;
}

//Put bits in order for the format.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//address int &thumbT_word1,  int &thumbT_word2)
static void stuff59bitsDiffFalse(int thumbT59_word1, int thumbT59_word2,  int[] thumbT_word1,  int[] thumbT_word2)
{
	// Put bits in twotimer configuration for 59 (red overflows)
	// 
	// Go from this bit layout:
	//
	//     |63 62 61 60 59|58 57 56 55|54 53 52 51|50 49 48 47|46 45 44 43|42 41 40 39|38 37 36 35|34 33 32|
	//     |----empty-----|---red 0---|--green 0--|--blue 0---|---red 1---|--green 1--|--blue 1---|--dist--|
	//
	//     |31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
	//     |----------------------------------------index bits---------------------------------------------|
	//
	//
	//  To this:
	// 
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34 33 32 
	//      -----------------------------------------------------------------------------------------------
	//     |// // //|R0a  |//|R0b  |G0         |B0         |R1         |G1         |B1          |da  |df|db|
	//      -----------------------------------------------------------------------------------------------
	//
	//     |31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
	//     |----------------------------------------index bits---------------------------------------------|
	//
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34 33 32 
	//      -----------------------------------------------------------------------------------------------
	//     | base col1    | dcol 2 | base col1    | dcol 2 | base col 1   | dcol 2 | table  | table  |df|fp|
	//     | R1' (5 bits) | dR2    | G1' (5 bits) | dG2    | B1' (5 bits) | dB2    | cw 1   | cw 2   |bt|bt|
	//      ------------------------------------------------------------------------------------------------

	byte R0a;
	byte bit, a, b, c, d, bits;

	R0a = (byte)GETBITSHIGH( thumbT59_word1, 2, 58);

	// Fix middle part
	thumbT_word1[0] = thumbT59_word1 << 1;
	// Fix R0a (top two bits of R0)
	PUTBITSHIGH( thumbT_word1, R0a,  2, 60);
	// Fix db (lowest bit of d)
	PUTBITSHIGH( thumbT_word1, thumbT59_word1,  1, 32);
	// 
	// Make sure that red overflows:
	a = (byte)GETBITSHIGH( thumbT_word1[0], 1, 60);
	b = (byte)GETBITSHIGH( thumbT_word1[0], 1, 59);
	c = (byte)GETBITSHIGH( thumbT_word1[0], 1, 57);
	d = (byte)GETBITSHIGH( thumbT_word1[0], 1, 56);
	// The following bit abcd bit sequences should be padded with ones: 0111, 1010, 1011, 1101, 1110, 1111
	// The following logical expression checks for the presence of any of those:
	bit = (byte)((a & c) | ((a==0?1:0) & b & c & d) | (a & b & (c==0?1:0) & d));
	bits = (byte)(0xf*bit);
	PUTBITSHIGH( thumbT_word1, bits,  3, 63);
	PUTBITSHIGH( thumbT_word1, (bit==0?1:0),  1, 58);

	// Set diffbit
	PUTBITSHIGH( thumbT_word1, 0,  1, 33);
	thumbT_word2[0] = thumbT59_word2;
}

//Tests if there is at least one pixel in the image which would get alpha = 0 in punchthrough mode.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static boolean hasAlpha(byte[] alphaimg, int ix, int iy, int width) 
{
	for(int x=ix; x<ix+4; x++) 
	{
		for(int y=iy; y<iy+4; y++) 
		{
			int index = x+y*width;
			if((alphaimg[index]&0xff)<128) 
			{
				return true;
			}
		}
	}
	return false;
}


//Compress a block with ETC2 RGB
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//address  int &compressed1,  int &compressed2
void compressBlockETC2Fast(byte[] img, byte[] alphaimg, byte[] imgdec,int width,int height,int startx,int starty,   int[] compressed1,  int[] compressed2)
{
	int[] etc1_word1= new int[1];
	int[] etc1_word2= new int[1];
	double error_etc1;

	int[] planar57_word1= new int[1];
	int[] planar57_word2= new int[1];
	int[] planar_word1= new int[1];
	int[] planar_word2= new int[1];
	double error_planar;

	int[] thumbT59_word1= new int[1];
	int[] thumbT59_word2= new int[1];
	int[] thumbT_word1= new int[1];
	int[] thumbT_word2= new int[1];
	double error_thumbT;
	
	int[] thumbH58_word1= new int[1];
	int[] thumbH58_word2= new int[1];
	int[] thumbH_word1= new int[1];
	int[] thumbH_word2= new int[1];
	double error_thumbH;

	double error_best;
	char best_char; //???
	MODE1 best_mode;
	
	if(format==FORMAT.ETC2PACKAGE_RGBA1||format==FORMAT.ETC2PACKAGE_sRGBA1)
	{
		/*                if we have one-bit alpha, we never use the individual mode,
		                  instead that bit flags that one of our four offsets will instead
						          mean transparent (with 0 offset for color channels) */

		/*                the regular ETC individual mode is disabled, but the old T, H and planar modes
						          are kept unchanged and may be used for blocks without transparency.
						          Introduced are old ETC with only differential coding,
						          ETC differential but with 3 offsets and transparent,
						          and T-mode with 3 colors plus transparent.*/

		/*                in a fairly hackish manner, error_etc1, etc1_word1 and etc1_word2 will
		                  represent the best out of the three introduced modes, to be compared
						          with the three kept modes in the old code*/

		int[] tempword1=new int[1], tempword2=new int[1];
		double temperror;
		byte[] alphadec = new byte[width*height];
		
		//try regular differential transparent mode
		compressBlockDifferentialWithAlpha(true,img,alphaimg, imgdec,width,height,startx,starty,etc1_word1,etc1_word2);		
		decompressBlockDifferentialWithAlpha(etc1_word1[0], etc1_word2[0], imgdec, alphadec,width, height, startx, starty);
		error_etc1 = calcBlockErrorRGBA(img, imgdec, alphaimg,width, height, startx, starty);
		
		//try T-mode with transparencies
		//for now, skip this...
		compressBlockTHUMB59TAlpha(img,alphaimg,width,height,startx,starty,tempword1,tempword2);
		decompressBlockTHUMB59TAlpha(tempword1[0],tempword2[0],imgdec, alphadec, width,height,startx,starty);
		temperror=calcBlockErrorRGBA(img, imgdec, alphaimg, width, height, startx, starty);
		if(temperror<error_etc1) 
		{
			error_etc1=temperror;
			stuff59bitsDiffFalse(tempword1[0],tempword2[0],etc1_word1,etc1_word2);
		}
		compressBlockTHUMB58HAlpha(img,alphaimg,width,height,startx,starty,tempword1,tempword2);
		decompressBlockTHUMB58HAlpha(tempword1[0],tempword2[0],imgdec, alphadec, width,height,startx,starty);
		temperror=calcBlockErrorRGBA(img, imgdec, alphaimg, width, height, startx, starty);
		if(temperror<error_etc1) 
		{
			error_etc1=temperror;
			stuff58bitsDiffFalse(tempword1[0],tempword2[0],etc1_word1,etc1_word2);
		}
		//if we have transparency in these pixels, we know that one of these two modes was best..
		if(hasAlpha(alphaimg,startx,starty,width)) 
		{
			compressed1[0]=etc1_word1[0];
			compressed2[0]=etc1_word2[0];
			//delete[] alphadec;
			return;
		}
		//error_etc1=255*255*1000;
		//otherwise, they MIGHT have been the best, although that's unlikely.. anyway, try old differential mode now
		
		compressBlockDifferentialWithAlpha(false,img,alphaimg,imgdec,width,height,startx,starty,tempword1,tempword2);
		decompressBlockDiffFlip(tempword1[0], tempword2[0], imgdec, width, height, startx, starty);
		temperror = calcBlockErrorRGB(img, imgdec, width, height, startx, starty);
		decompressBlockDifferentialWithAlpha(tempword1[0],tempword2[0],imgdec,alphadec,width,height,startx,starty);
		if(temperror<error_etc1) 
		{
			error_etc1=temperror;
			etc1_word1[0]=tempword1[0];
			etc1_word2[0]=tempword2[0];
		}
		//delete[] alphadec;
		//drop out of this if, and test old T, H and planar modes (we have already returned if there are transparent pixels in this block)
	}
	else 
	{
		//this includes individual mode, and therefore doesn't apply in case of punch-through alpha.
		compressBlockDiffFlipFast(img, imgdec, width, height, startx, starty, etc1_word1, etc1_word2);
		decompressBlockDiffFlip(etc1_word1[0], etc1_word2[0], imgdec, width, height, startx, starty);
		error_etc1 = calcBlockErrorRGB(img, imgdec, width, height, startx, starty);
	}
	//these modes apply regardless of whether we want punch-through alpha or not.
	//error etc_1 and etc1_word1/etc1_word2 contain previous best candidate.
	compressBlockPlanar57(img, width, height, startx, starty, planar57_word1, planar57_word2);
	decompressBlockPlanar57(planar57_word1[0], planar57_word2[0], imgdec, width, height, startx, starty);
	error_planar = calcBlockErrorRGB(img, imgdec, width, height, startx, starty);
	stuff57bits(planar57_word1[0], planar57_word2[0], planar_word1, planar_word2);

	compressBlockTHUMB59TFastest(img,width, height, startx, starty, thumbT59_word1, thumbT59_word2);
	decompressBlockTHUMB59T(thumbT59_word1[0], thumbT59_word2[0], imgdec, width, height, startx, starty);			
	error_thumbT = calcBlockErrorRGB(img, imgdec, width, height, startx, starty);
	stuff59bits(thumbT59_word1[0], thumbT59_word2[0], thumbT_word1, thumbT_word2);

	compressBlockTHUMB58HFastest(img,width,height,startx, starty, thumbH58_word1, thumbH58_word2);
	decompressBlockTHUMB58H(thumbH58_word1[0], thumbH58_word2[0], imgdec, width, height, startx, starty);			
	error_thumbH = calcBlockErrorRGB(img, imgdec, width, height, startx, starty);
	stuff58bits(thumbH58_word1[0], thumbH58_word2[0], thumbH_word1, thumbH_word2);

	error_best = error_etc1;
	compressed1[0] = etc1_word1[0];
	compressed2[0] = etc1_word2[0];
	best_char = '.';
	best_mode = MODE1.MODE_ETC1;

	if(error_planar < error_best)
	{
		compressed1[0] = planar_word1[0];
        compressed2[0] = planar_word2[0];
		best_char = 'p';
		error_best = error_planar;	
		best_mode = MODE1.MODE_PLANAR;
	}
	if(error_thumbT < error_best)
	{
		compressed1[0] = thumbT_word1[0];
        compressed2[0] = thumbT_word2[0];
		best_char = 'T';
		error_best = error_thumbT;
		best_mode = MODE1.MODE_THUMB_T;
	}
	if(error_thumbH < error_best)
	{
		compressed1[0] = thumbH_word1[0];
        compressed2[0] = thumbH_word2[0];
		best_char = 'H';
		error_best = error_thumbH;
		best_mode = MODE1.MODE_THUMB_H;
	}
	
	switch(best_mode)
	{
		// Now see which mode won and compress that a little bit harder
	case MODE_THUMB_T:
		compressBlockTHUMB59TFast(img,width, height, startx, starty, thumbT59_word1, thumbT59_word2);
		decompressBlockTHUMB59T(thumbT59_word1[0], thumbT59_word2[0], imgdec, width, height, startx, starty);			
		error_thumbT = calcBlockErrorRGB(img, imgdec, width, height, startx, starty);
		stuff59bits(thumbT59_word1[0], thumbT59_word2[0], thumbT_word1, thumbT_word2);
		if(error_thumbT < error_best)
		{
			compressed1[0] = thumbT_word1[0];
			compressed2[0] = thumbT_word2[0];
		}
		break;
	case MODE_THUMB_H:
		compressBlockTHUMB58HFast(img,width,height,startx, starty, thumbH58_word1, thumbH58_word2);
		decompressBlockTHUMB58H(thumbH58_word1[0], thumbH58_word2[0], imgdec, width, height, startx, starty);			
		error_thumbH = calcBlockErrorRGB(img, imgdec, width, height, startx, starty);
		stuff58bits(thumbH58_word1[0], thumbH58_word2[0], thumbH_word1, thumbH_word2);
		if(error_thumbH < error_best)
		{
			compressed1[0] = thumbH_word1[0];
			compressed2[0] = thumbH_word2[0];
		}
		break;
	default:
		break;
	}
}

//Compress an ETC2 RGB block using perceptual error metric
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address  int &compressed1,  int &compressed2
void compressBlockETC2FastPerceptual(byte[] img, byte[] imgdec,int width,int height,int startx,int starty,  int[] compressed1,  int[] compressed2)
{
	int[] etc1_word1=new int[1];
	int[] etc1_word2=new int[1];
	double error_etc1;

	int[] planar57_word1=new int[1];
	int[] planar57_word2=new int[1];
	int[] planar_word1=new int[1];
	int[] planar_word2=new int[1];
	double error_planar;

	int[] thumbT59_word1=new int[1];
	int[] thumbT59_word2=new int[1];
	int[] thumbT_word1=new int[1];
	int[] thumbT_word2=new int[1];
	double error_thumbT;
	
	int[] thumbH58_word1=new int[1];
	int[] thumbH58_word2=new int[1];
	int[] thumbH_word1=new int[1];
	int[] thumbH_word2=new int[1];
	double error_thumbH;

	double error_best;
	char best_char = ' ';
	//MODE1 best_mode;
	
	
	//This ETC1 style compression uses 35% of the cpu and NEVER results in a better error value! 
	//compressBlockDiffFlipFastPerceptual(img, imgdec, width, height, startx, starty, etc1_word1, etc1_word2);
	//decompressBlockDiffFlip(etc1_word1[0], etc1_word2[0], imgdec, width, height, startx, starty);
	//error_etc1 = 1000*calcBlockPerceptualErrorRGB(img, imgdec, width, height, startx, starty);
	// set it really high and use it as a last ditch effort
	error_etc1 = 1000*10000*10000;

	compressBlockPlanar57(img, width, height, startx, starty, planar57_word1, planar57_word2);
	decompressBlockPlanar57(planar57_word1[0], planar57_word2[0], imgdec, width, height, startx, starty);
	error_planar = 1000*calcBlockPerceptualErrorRGB(img, imgdec, width, height, startx, starty);

	compressBlockTHUMB59TFastestPerceptual1000(img,width, height, startx, starty, thumbT59_word1, thumbT59_word2);
	decompressBlockTHUMB59T(thumbT59_word1[0], thumbT59_word2[0], imgdec, width, height, startx, starty);			
	error_thumbT = 1000*calcBlockPerceptualErrorRGB(img, imgdec, width, height, startx, starty);

	compressBlockTHUMB58HFastestPerceptual1000(img,width,height,startx, starty, thumbH58_word1, thumbH58_word2);
	decompressBlockTHUMB58H(thumbH58_word1[0], thumbH58_word2[0], imgdec, width, height, startx, starty);			
	error_thumbH = 1000*calcBlockPerceptualErrorRGB(img, imgdec, width, height, startx, starty);
		
	if(error_planar < error_etc1 && error_planar < error_thumbT && error_planar < error_thumbH)
	{	
		stuff57bits(planar57_word1[0], planar57_word2[0], planar_word1, planar_word2);
		compressed1[0] = planar_word1[0];
		compressed2[0] = planar_word2[0];
		best_char = 'p';
		//System.out.print("p");
		error_best = error_planar;	
		//best_mode = MODE1.MODE_PLANAR;
	}
	else if(error_thumbT < error_etc1 && error_thumbT < error_thumbH)
	{
		stuff59bits(thumbT59_word1[0], thumbT59_word2[0], thumbT_word1, thumbT_word2);
		compressed1[0] = thumbT_word1[0];
		compressed2[0] = thumbT_word2[0];
		best_char = 'T';
		//System.out.print("T");
		error_best = error_thumbT;
		//best_mode = MODE1.MODE_THUMB_T;
		
		//Now compress that a little bit harder
		compressBlockTHUMB59TFast(img,width, height, startx, starty, thumbT59_word1, thumbT59_word2);
		decompressBlockTHUMB59T(thumbT59_word1[0], thumbT59_word2[0], imgdec, width, height, startx, starty);			
		error_thumbT = calcBlockErrorRGB(img, imgdec, width, height, startx, starty);
		stuff59bits(thumbT59_word1[0], thumbT59_word2[0], thumbT_word1, thumbT_word2);
		if(error_thumbT < error_best)
		{
			compressed1[0] = thumbT_word1[0];
			compressed2[0] = thumbT_word2[0];
		}
	}
	else if(error_thumbH < error_etc1)
	{
		stuff58bits(thumbH58_word1[0], thumbH58_word2[0], thumbH_word1, thumbH_word2);
		compressed1[0] = thumbH_word1[0];
		compressed2[0] = thumbH_word2[0];
		best_char = 'H';
		//System.out.print("H");
		error_best = error_thumbH;
		//best_mode = MODE1.MODE_THUMB_H;
		
		//Now compress that a little bit harder
		compressBlockTHUMB58HFast(img,width,height,startx, starty, thumbH58_word1, thumbH58_word2);
		decompressBlockTHUMB58H(thumbH58_word1[0], thumbH58_word2[0], imgdec, width, height, startx, starty);			
		error_thumbH = calcBlockErrorRGB(img, imgdec, width, height, startx, starty);
		stuff58bits(thumbH58_word1[0], thumbH58_word2[0], thumbH_word1, thumbH_word2);
		if(error_thumbH < error_best)
		{
			compressed1[0] = thumbH_word1[0];
			compressed2[0] = thumbH_word2[0];
		}
	} 
	else
	{	
		// Not calculated above, a last ditch effort
		compressBlockDiffFlipFastPerceptual(img, imgdec, width, height, startx, starty, etc1_word1, etc1_word2);
		decompressBlockDiffFlip(etc1_word1[0], etc1_word2[0], imgdec, width, height, startx, starty);
		
		compressed1[0] = etc1_word1[0];
		compressed2[0] = etc1_word2[0];
		//best_char = '.';
		//System.out.print(".");
		//best_mode = MODE1.MODE_ETC1;
	}	
	// some intersting debugs, note there is no relationship here so far, note also that compressing a decompressed image will find patterns
	//possibly a planar < 100000 will be better compressed by an H than a T
	//System.out.print(best_char);
	//if(error_planar < 100000 && (error_thumbT < error_planar || error_thumbH < error_planar) )
	//	System.out.println("error_planar " + error_planar + " error_thumbT " +error_thumbT + " error_thumbH "+error_thumbH);

}


//Write a word in big endian style
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void write_big_endian_2byte_word(short blockadr, FileChannel f) throws IOException
{
	byte[] bytes= new byte[2];
	short block;

	block = blockadr;

	bytes[0] = (byte)((block >> 8) & 0xff);
	bytes[1] = (byte)((block >> 0) & 0xff);

	//fwrite(&bytes[0],1,1,f);
	//fwrite(&bytes[1],1,1,f);
	f.write(ByteBuffer.wrap(bytes));
}
static void write_big_endian_2byte_word(short blockadr, ByteBuffer bb) throws IOException
{
	byte[] bytes= new byte[2];
	short block;

	block = blockadr;

	bytes[0] = (byte)((block >> 8) & 0xff);
	bytes[1] = (byte)((block >> 0) & 0xff);

	//fwrite(&bytes[0],1,1,f);
	//fwrite(&bytes[1],1,1,f);
	bb.put(bytes);
}


//Write a word in big endian style
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void write_big_endian_4byte_word(int[] blockadr, FileChannel f) throws IOException
{
	byte[] bytes = new byte[4];
	int block;

	block = blockadr[0];

	bytes[0] = (byte)((block >> 24) & 0xff);
	bytes[1] = (byte)((block >> 16) & 0xff);
	bytes[2] = (byte)((block >> 8) & 0xff);
	bytes[3] = (byte)((block >> 0) & 0xff);

	fwrite(bytes[0],1,f);
	fwrite(bytes[1],1,f);
	fwrite(bytes[2],1,f);
	fwrite(bytes[3],1,f);
}
static void write_big_endian_4byte_word(int[] blockadr, ByteBuffer bb) throws IOException
{
	byte[] bytes = new byte[4];
	int block;

	block = blockadr[0];

	bytes[0] = (byte)((block >> 24) & 0xff);
	bytes[1] = (byte)((block >> 16) & 0xff);
	bytes[2] = (byte)((block >> 8) & 0xff);
	bytes[3] = (byte)((block >> 0) & 0xff);

	fwrite(bytes[0],1,bb);
	fwrite(bytes[1],1,bb);
	fwrite(bytes[2],1,bb);
	fwrite(bytes[3],1,bb);
}

//int[][] alphaTable = new int[256][8];
//int[][] alphaBase = new int[16][4];

//valtab holds precalculated data used for compressing using EAC2.
//Note that valtab is constructed using get16bits11bits, which means
//that it already is expanded to 16 bits.
//Note also that it its contents will depend on the value of formatSigned.
int[] valtab;// pointer to one of the below

static int[] valtabsigned;
static int[] valtabunsigned;

static boolean valtabTableInitialized = false;


void setupAlphaTableAndValtab()
{
	setupAlphaTable();
	
	//point to which ever is appropriate
	if(formatSigned!=0)
		valtab=valtabsigned;
	else
		valtab=valtabunsigned;
	
	//initialize once
    if(valtabTableInitialized)
    	return;
    valtabTableInitialized = true;

    valtabsigned = new int[1024*512];
    valtabunsigned = new int[1024*512];
    short val16;
	int count=0;
	for(int base=0; base<256; base++) 
	{
		for(int tab=0; tab<16; tab++) 
		{
			for(int mul=0; mul<16; mul++) 
			{
				for(int index=0; index<8; index++) 
				{
					val16=get16bits11signed(base,tab,mul,index);
					valtabsigned[count] = val16 + 256*128;
					valtabunsigned[count]=get16bits11bits(base,tab,mul,index);
					count++;
				}
			}
		}
	}
}

//Reads alpha data
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address byte[] &data, int &width, int &height, int &extendedwidth, int &extendedheight) 
void readAlpha(byte[][] data, byte[] imgalpha, int width, int height, int[] extendedwidth, int[] extendedheight) 
{
	//width and height are already known..?
	//byte[][] tempdata = new byte[1][0];
	int wantedBitDepth=0;
	if(format==FORMAT.ETC2PACKAGE_RGBA||format==FORMAT.ETC2PACKAGE_RGBA1||format==FORMAT.ETC2PACKAGE_sRGBA||format==FORMAT.ETC2PACKAGE_sRGBA1) 
	{
		wantedBitDepth=8;
	}
	else if(format==FORMAT.ETC2PACKAGE_R) 
	{
		wantedBitDepth=16;
	}
	else 
	{
		System.out.println("invalid format for alpha reading!");
		System.exit(1);
	}
	//fReadPGM("alpha.pgm",width,height,tempdata,wantedBitDepth);
	//NOTE! that alpha channel comes from the readSrcFile now in void compressFile(String srcfile,String dstfile)
	extendedwidth[0]=4*((width+3)/4);
	extendedheight[0]=4*((height+3)/4);

	if(width==extendedwidth[0]&&height==extendedheight[0]) 
	{
		data[0]=imgalpha;
	}
	else 
	{
		data[0] = new byte[extendedwidth[0]*extendedheight[0]*wantedBitDepth/8];
		byte last=0;
		byte lastlast=0;
		for(int x=0; x<extendedwidth[0]; x++) 
		{
			for(int y=0; y<extendedheight[0]; y++) 
			{
				if(wantedBitDepth==8) 
				{
					if(x<width&&y<height) 
					{
						last = imgalpha[x+y*width];
					}
					data[0][x+y*extendedwidth[0]]=last;
				}
				else 
				{
					if(x<width&&y<height) 
					{
						last = imgalpha[(x+y*width)*2];
						lastlast = imgalpha[(x+y*width)*2+1];						
					}
					data[0][(x+y*extendedwidth[0])*2]=last;
					data[0][(x+y*extendedwidth[0])*2+1]=lastlast;
				}
			}
		}
	}
	if(format==FORMAT.ETC2PACKAGE_RGBA1||format==FORMAT.ETC2PACKAGE_sRGBA1) 
	{
		for(int x=0; x<extendedwidth[0]; x++) 
		{
			for(int y=0; y<extendedheight[0]; y++) 
			{
				if((data[0][x+y*extendedwidth[0]]&0xff)<128)
					data[0][x+y*extendedwidth[0]]=0;
				else
					data[0][x+y*extendedwidth[0]]=(byte)255;
			}
		}
	}
}



//Compresses the alpha part of a GL_COMPRESSED_RGBA8_ETC2_EAC block.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void compressBlockAlphaFast(byte[] data, int ix, int iy, int width, int height, byte[] returnData) 
{
	int alphasum=0;
	int maxdist=-2;
	for(int x=0; x<4; x++) 
	{
		for(int y=0; y<4; y++) 
		{
			alphasum+=(data[ix+x+(iy+y)*width]&0xff);
		}
	}
	int alpha = (int)( ((float)alphasum)/16.0f+0.5f); //average pixel value, used as guess for base value.
	for(int x=0; x<4; x++) 
	{
		for(int y=0; y<4; y++) 
		{
			if(Math.abs(alpha-(data[ix+x+(iy+y)*width]&0xff))>maxdist)
				maxdist=Math.abs(alpha-(data[ix+x+(iy+y)*width]&0xff)); //maximum distance from average
		}
	}
	int approxPos = (maxdist*255)/160-4;  //experimentally derived formula for calculating approximate table position given a max distance from average
	if(approxPos>255)
		approxPos=255;
	int startTable=approxPos-15; //first table to be tested
	if(startTable<0)
		startTable=0;
	int endTable=clamp(approxPos+15);  //last table to be tested

	int bestsum=1000000000;
	int besttable=-3; 
	int bestalpha=128;
	int prevalpha=alpha;

	//main loop: determine best base alpha value and offset table to use for compression
	//try some different alpha tables.
	for(int table = startTable; table<endTable&&bestsum>0; table++)
	{
		int tablealpha=prevalpha;
		int tablebestsum=1000000000;
		//test some different alpha values, trying to find the best one for the given table.	
		for(int alphascale=16; alphascale>0; alphascale/=4) 
		{
			int startalpha;
			int endalpha;
			if(alphascale==16) 
			{
				startalpha = clamp(tablealpha-alphascale*4);
				endalpha = clamp(tablealpha+alphascale*4);
			}
			else 
			{
				startalpha = clamp(tablealpha-alphascale*2);
				endalpha = clamp(tablealpha+alphascale*2);
			}
			for(alpha=startalpha; alpha<=endalpha; alpha+=alphascale) 
			{
				int sum=0;
				int val,diff,bestdiff=10000000,index;
				for(int x=0; x<4; x++) 
				{
					for(int y=0; y<4; y++) 
					{
						//compute best offset here, add square difference to sum..
						val=(data[ix+x+(iy+y)*width]&0xff);
						bestdiff=1000000000;
						//the values are always ordered from small to large, with the first 4 being negative and the last 4 positive
						//search is therefore made in the order 0-1-2-3 or 7-6-5-4, stopping when error increases compared to the previous entry tested.
						if(val>alpha) 
						{ 
							for(index=7; index>3; index--) 
							{
								diff=clamp_table[alpha+(int)(ETCDec.alphaTable[table][index])+255]-val;
								diff*=diff;
								if(diff<=bestdiff) 
								{
									bestdiff=diff;
								}
								else
									break;
							}
						}
						else 
						{
							for(index=0; index<4; index++) 
							{
								diff=clamp_table[alpha+(int)(ETCDec.alphaTable[table][index])+255]-val;
								diff*=diff;
								if(diff<bestdiff) 
								{
									bestdiff=diff;
								}
								else
									break;
							}
						}

						//best diff here is bestdiff, add it to sum!
						sum+=bestdiff;
						//if the sum here is worse than previously best already, there's no use in continuing the count..
						//note that tablebestsum could be used for more precise estimation, but the speedup gained here is deemed more important.
						if(sum>bestsum) 
						{ 
							x=9999; //just to make it large and get out of the x<4 loop
							break;
						}
					}
				}
				if(sum<tablebestsum) 
				{
					tablebestsum=sum;
					tablealpha=alpha;
				}
				if(sum<bestsum) 
				{
					bestsum=sum;
					besttable=table;
					bestalpha=alpha;
			}
		}
		if(alphascale<=2)
			alphascale=0;
		}
	}

	alpha=bestalpha;	

	//"good" alpha value and table are known!
	//store them, then loop through the pixels again and print indices.

	returnData[0]=(byte)alpha;
	returnData[1]=(byte)besttable;
	for(int pos=2; pos<8; pos++) 
	{
		returnData[pos]=0;
	}
	int byte_=2;
	int bit=0;
	for(int x=0; x<4; x++) 
	{
		for(int y=0; y<4; y++) 
		{
			//find correct index
			int besterror=1000000;
			int bestindex=99;
			for(int index=0; index<8; index++) //no clever ordering this time, as this loop is only run once per block anyway
			{ 
				int error= (clamp(alpha +(int)(ETCDec.alphaTable[besttable][index]))-(data[ix+x+(iy+y)*width]&0xff));
				error *= error;
				if(error<besterror) 
				{
					besterror=error;
					bestindex=index;
				}
			}
			//best table index has been determined.
			//pack 3-bit index into compressed data, one bit at a time
			for(int numbit=0; numbit<3; numbit++) 
			{
				returnData[byte_] =(byte)((returnData[byte_]&0xff) | getbit(bestindex,2-numbit,7-bit));

				bit++;
				if(bit>7) 
				{
					bit=0;
					byte_++;
				}
			}
		}
	}
}

//Helper function for the below function
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int getPremulIndex(int base, int tab, int mul, int index) 
{
	return (base<<11)+(tab<<7)+(mul<<3)+index;
}

//Calculates the error used in compressBlockAlpha16()
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
double calcError(byte[] data, int ix, int iy, int width, int height, int base, int tab, int mul, double prevbest) 
{
	int offset = getPremulIndex(base,tab,mul,0);
	double error=0;
	for (int y=0; y<4; y++) 
	{
		for(int x=0; x<4; x++) 
		{
			double besthere = (1<<20);
			besthere*=besthere;
			byte byte1 = data[2*(x+ix+(y+iy)*width)];
			byte byte2 = data[2*(x+ix+(y+iy)*width)+1];
			int alpha = (byte1<<8)+(byte2&0xff);
			for(int index=0; index<8; index++) 
			{
				double indexError;
				indexError = alpha-valtab[offset+index];
				indexError*=indexError;
				if(indexError<besthere)
					besthere=indexError;
			}
			error+=besthere;
			if(error>=prevbest)
				return prevbest+(1<<30);
		}
	}
	return error;
}

//compressBlockAlpha16
//
//Compresses a block using the 11-bit EAC formats.
//Depends on the global variable formatSigned.
//
//COMPRESSED_R11_EAC (if formatSigned = 0)
//This is an 11-bit unsigned format. Since we do not have a good 11-bit file format, we use 16-bit pgm instead.
//Here we assume that, in the input 16-bit pgm file, 0 represents 0.0 and 65535 represents 1.0. The function compressBlockAlpha16 
//will find the compressed block which best matches the data. In detail, it will find the compressed block, which 
//if decompressed, will generate an 11-bit block that after bit replication to 16-bits will generate the closest 
//block to the original 16-bit pgm block.
//
//COMPRESSED_SIGNED_R11_EAC (if formatSigned = 1)
//This is an 11-bit signed format. Since we do not have any signed file formats, we use unsigned 16-bit pgm instead.
//Hence we assume that, in the input 16-bit pgm file, 1 represents -1.0, 32768 represents 0.0 and 65535 represents 1.0. 
//The function compresseBlockAlpha16 will find the compressed block, which if decompressed, will generate a signed
//11-bit block that after bit replication to 16-bits and conversion to unsigned (1 equals -1.0, 32768 equals 0.0 and 
//65535 equals 1.0) will generate the closest block to the original 16-bit pgm block. 
//
//COMPRESSED_RG11_EAC is compressed by calling the function twice, dito for COMPRESSED_SIGNED_RG11_EAC.
//
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
void compressBlockAlpha16(byte[] data, int ix, int iy, int width, int height, byte[] returnData) 
{
	int bestbase=0, besttable=0, bestmul=0;
	double besterror;
	besterror=1<<20;
	besterror*=besterror;
	for(int base=0; base<256; base++) 
	{
		for(int table=0; table<16; table++) 
		{
			for(int mul=0; mul<16; mul++) 
			{
				double e = calcError(data, ix, iy, width, height,base,table,mul,besterror);
				if(e<besterror) 
				{
					bestbase=base;
					besttable=table;
					bestmul=mul;
					besterror=e;
				}
			}
		}
	}
	returnData[0]=(byte)bestbase;
	returnData[1]=(byte)((bestmul<<4)+besttable);
	if(formatSigned!=0) 
	{
		//if we have a signed format, the base value should be given as a signed byte. 
		byte signedbase = (byte)(bestbase-128);
		returnData[0]=signedbase;//WTF?? *((uint8*)(&signedbase));
	}
	
	for(int i=2; i<8; i++) 
	{
		returnData[i]=0;
	}

	int byte_=2;
	int bit=0;
	for (int x=0; x<4; x++) 
	{
		for(int y=0; y<4; y++) 
		{
			besterror=255*255;
			besterror*=besterror;
			int bestindex=99;
			byte byte1 = data[2*(x+ix+(y+iy)*width)];
			byte byte2 = data[2*(x+ix+(y+iy)*width)+1];
			int alpha = (byte1<<8)+(byte2&0xff);
			for(int index=0; index<8; index++) 
			{
				double indexError;
				if(formatSigned!=0)
				{
					int val16;
					int val;
                    val16 = get16bits11signed(bestbase,besttable,bestmul,index);
                    val = val16 + 256*128;
					indexError = alpha-val;
				}
				else
					indexError = alpha-get16bits11bits(bestbase,besttable,bestmul,index);

				indexError*=indexError;
				if(indexError<besterror) 
				{
					besterror=indexError;
					bestindex=index;
				}
			}
			
			for(int numbit=0; numbit<3; numbit++) 
			{
				returnData[byte_] = (byte)((returnData[byte_]&0xff) |getbit(bestindex,2-numbit,7-bit));
				bit++;
				if(bit>7) 
				{
					bit=0;
					byte_++;
				}
			}
		}
	}
}

//Exhaustive compression of alpha compression in a GL_COMPRESSED_RGB8_ETC2 block
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void compressBlockAlphaSlow(byte[] data, int ix, int iy, int width, int height, byte[] returnData) 
{
	//determine the best table and base alpha value for this block using MSE
	int alphasum=0;
	int maxdist=-2;
	for(int x=0; x<4; x++) 
	{
		for(int y=0; y<4; y++) 
		{
			alphasum+=(data[ix+x+(iy+y)*width]&0xff);
		}
	}
	int alpha = (int)( ((float)alphasum)/16.0f+0.5f); //average pixel value, used as guess for base value.

	int bestsum=1000000000;
	int besttable=-3; 
	int bestalpha=128;
	int prevalpha=alpha;

	//main loop: determine best base alpha value and offset table to use for compression
	//try some different alpha tables.
	for(int table = 0; table<256&&bestsum>0; table++)
	{
		int tablealpha=prevalpha;
		int tablebestsum=1000000000;
		//test some different alpha values, trying to find the best one for the given table.
		for(int alphascale=32; alphascale>0; alphascale/=8) 
		{
			
			int startalpha = clamp(tablealpha-alphascale*4);
			int endalpha = clamp(tablealpha+alphascale*4);
			
			for(alpha=startalpha; alpha<=endalpha; alpha+=alphascale) {
				int sum=0;
				int val,diff,bestdiff=10000000,index;
				for(int x=0; x<4; x++) 
				{
					for(int y=0; y<4; y++) 
					{
						//compute best offset here, add square difference to sum..
						val=data[ix+x+(iy+y)*width]&0xff;
						bestdiff=1000000000;
						//the values are always ordered from small to large, with the first 4 being negative and the last 4 positive
						//search is therefore made in the order 0-1-2-3 or 7-6-5-4, stopping when error increases compared to the previous entry tested.
						if(val>alpha) 
						{ 
							for(index=7; index>3; index--) 
							{
								diff=clamp_table[alpha+(ETCDec.alphaTable[table][index])+255]-val;
								diff*=diff;
								if(diff<=bestdiff) 
								{
									bestdiff=diff;
								}
								else
									break;
							}
						}
						else 
						{
							for(index=0; index<5; index++) 
							{
								diff=clamp_table[alpha+(ETCDec.alphaTable[table][index])+255]-val;
								diff*=diff;
								if(diff<bestdiff) 
								{
									bestdiff=diff;
								}
								else
									break;
							}
						}

						//best diff here is bestdiff, add it to sum!
						sum+=bestdiff;
						//if the sum here is worse than previously best already, there's no use in continuing the count..
						if(sum>tablebestsum) 
						{ 
							x=9999; //just to make it large and get out of the x<4 loop
							break;
						}
					}
				}
				if(sum<tablebestsum) 
				{
					tablebestsum=sum;
					tablealpha=alpha;
				}
				if(sum<bestsum) 
				{
					bestsum=sum;
					besttable=table;
					bestalpha=alpha;
				}
			}
			if(alphascale==4)
				alphascale=8;
		}
	}

	alpha=bestalpha;	
	//the best alpha value and table are known!
	//store them, then loop through the pixels again and print indices.
	returnData[0]=(byte)alpha;
	returnData[1]=(byte)besttable;
	for(int pos=2; pos<8; pos++) 
	{
		returnData[pos]=0;
	}
	int byte_=2;
	int bit=0;
	for(int x=0; x<4; x++) 
	{
		for(int y=0; y<4; y++) 
		{
			//find correct index
			int besterror=1000000;
			int bestindex=99;
			for(int index=0; index<8; index++) //no clever ordering this time, as this loop is only run once per block anyway
			{ 
				int error= (clamp(alpha +(int)(ETCDec.alphaTable[besttable][index]))-(data[ix+x+(iy+y)*width]&0xff));
				error *= error;
				if(error<besterror) 
				{
					besterror=error;
					bestindex=index;
				}
			}
			//best table index has been determined.
			//pack 3-bit index into compressed data, one bit at a time
			for(int numbit=0; numbit<3; numbit++) 
			{
				returnData[byte_]=(byte)((returnData[byte_]&0xff)|getbit(bestindex,2-numbit,7-bit));

				bit++;
				if(bit>7) 
				{
					bit=0;
					byte_++;
				}
			}
		}
	}
}

//Calculate weighted PSNR
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static double calculateWeightedPSNR(byte[] lossyimg, byte[] origimg, int width, int height, double w1, double w2, double w3)
{
	// Note: This calculation of PSNR uses the formula
	//
	// PSNR = 10 * log_10 ( 255^2 / wMSE ) 
	// 
	// where the wMSE is calculated as
	//
	// 1/(N*M) * sum ( ( w1*(R' - R)^2 + w2*(G' - G)^2 + w3*(B' - B)^2) ) 
	//
	// typical weights are  0.299,   0.587,   0.114  for perceptually weighted PSNR and
 //                     1.0/3.0, 1.0/3.0, 1.0/3.0 for nonweighted PSNR

	int x,y;
	double wMSE;
	double PSNR;
	double err;
	wMSE = 0;

	for(y=0;y<height;y++)
	{
		for(x=0;x<width;x++)
		{
			err = (lossyimg[y*width*3+x*3+0]&0xff) - (origimg[y*width*3+x*3+0]&0xff);
		    wMSE = wMSE + (w1*(err * err));
			err = (lossyimg[y*width*3+x*3+1]&0xff) - (origimg[y*width*3+x*3+1]&0xff);
		    wMSE = wMSE + (w2*(err * err));
			err = (lossyimg[y*width*3+x*3+2]&0xff) - (origimg[y*width*3+x*3+2]&0xff);
		    wMSE = wMSE + (w3*(err * err));
		}
	}
	wMSE = wMSE / (width * height);
	if(wMSE == 0)
	{
		System.out.println("********************************************************************");
		System.out.println("There is no difference at all between image files --- infinite PSNR.");
		System.out.println("********************************************************************");
	}
	PSNR = 10*Math.log((1.0*255*255)/wMSE)/Math.log(10.0);
	return PSNR;
}

//Calculate unweighted PSNR (weights are (1,1,1))
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static double calculatePSNR(byte[] lossyimg, byte[] origimg, int width, int height)
{
	// Note: This calculation of PSNR uses the formula
	//
	// PSNR = 10 * log_10 ( 255^2 / MSE ) 
	// 
	// where the MSE is calculated as
	//
	// 1/(N*M) * sum ( 1/3 * ((R' - R)^2 + (G' - G)^2 + (B' - B)^2) ) 
	//
	// The reason for having the 1/3 factor is the following:
	// Presume we have a grayscale image, that is actually just the red component 
	// of a color image.. The squared error is then (R' - R)^2.
	// Assume that we have a certain signal to noise ratio, say 30 dB. If we add
	// another two components (say green and blue) with the same signal to noise 
	// ratio, we want the total signal to noise ratio be the same. For the
	// squared error to remain constant we must divide by three after adding
	// together the squared errors of the components. 

	return calculateWeightedPSNR(lossyimg, origimg, width, height, (1.0/3.0), (1.0/3.0), (1.0/3.0));
}

/*
//Decompresses a file
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
void uncompressFile(char *srcfile, uint8* &img, uint8 *&alphaimg, int& active_width, int& active_height)
{	
	FILE *f;
	int width,height;
	unsigned int block_part1, block_part2;
	uint8 *newimg, *newalphaimg, *alphaimg2;
	unsigned short w, h;
	int xx, yy;
	unsigned char magic[4];
	unsigned char version[2];
	unsigned short texture_type;
	if(f=fopen(srcfile,"rb"))
	{
		// Load table
		readCompressParams();
		if(ktxFile) 
		{
			//read ktx header..
			KTX_header header;
			fread(&header,sizeof(KTX_header),1,f);
			//read size parameter, which we don't actually need..
			unsigned int bitsize;
			fread(&bitsize,sizeof(unsigned int),1,f);
	
			active_width=header.pixelWidth;
			active_height = header.pixelHeight;
			w = ((active_width+3)/4)*4;
			h = ((active_height+3)/4)*4;
			width=w;
			height=h;

			if(header.glInternalFormat==GL_COMPRESSED_SIGNED_R11_EAC) 
			{
				format=ETC2PACKAGE_R_NO_MIPMAPS;
				formatSigned=1;
			}
			else if(header.glInternalFormat==GL_COMPRESSED_R11_EAC) 
			{
				format=ETC2PACKAGE_R_NO_MIPMAPS;
			}
			else if(header.glInternalFormat==GL_COMPRESSED_SIGNED_RG11_EAC) 
			{
				format=ETC2PACKAGE_RG_NO_MIPMAPS;
				formatSigned=1;
			}
			else if(header.glInternalFormat==GL_COMPRESSED_RG11_EAC) 
			{
				format=ETC2PACKAGE_RG_NO_MIPMAPS;
			}
			else if(header.glInternalFormat==GL_COMPRESSED_RGB8_ETC2) 
			{
				format=ETC2PACKAGE_RGB_NO_MIPMAPS;
			}
			else if(header.glInternalFormat==GL_COMPRESSED_SRGB8_ETC2) 
			{
				format=ETC2PACKAGE_sRGB_NO_MIPMAPS;
			}
			else if(header.glInternalFormat==GL_COMPRESSED_RGBA8_ETC2_EAC) 
			{
				format=ETC2PACKAGE_RGBA_NO_MIPMAPS;
			}
			else if(header.glInternalFormat==GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC) 
			{
				format=ETC2PACKAGE_sRGBA_NO_MIPMAPS;
			}
			else if(header.glInternalFormat==GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2) 
			{
				format=ETC2PACKAGE_RGBA1_NO_MIPMAPS;
			}
			else if(header.glInternalFormat==GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2) 
			{
				format=ETC2PACKAGE_sRGBA1_NO_MIPMAPS;
			}
			else if(header.glInternalFormat==GL_ETC1_RGB8_OES) 
			{
				format=ETC1_RGB_NO_MIPMAPS;
				codec=CODEC_ETC;
			}
			else 
			{
				System.out.println("ktx file has unknown glInternalFormat (not etc compressed)!");
				exit(1);
			}
		}
		else 
		{
			// Read magic number
			fread(&magic[0], sizeof(unsigned char), 1, f);
			fread(&magic[1], sizeof(unsigned char), 1, f);
			fread(&magic[2], sizeof(unsigned char), 1, f);
			fread(&magic[3], sizeof(unsigned char), 1, f);
			if(!(magic[0] == 'P' && magic[1] == 'K' && magic[2] == 'M' && magic[3] == ' '))
			{
				System.out.println("\n The file %s is not a .pkm file.\n",srcfile);
				exit(1);
			}
		
			// Read version
			fread(&version[0], sizeof(unsigned char), 1, f);
			fread(&version[1], sizeof(unsigned char), 1, f);
			if( version[0] == '1' && version[1] == '0' )
			{

				// Read texture type
				read_big_endian_2byte_word(&texture_type, f);
				if(!(texture_type == ETC1_RGB_NO_MIPMAPS))
				{
					System.out.println("\n\n The file %s (of version %c.%c) does not contain a texture of known format.", srcfile, version[0],version[1]);
					System.out.println("Known formats: ETC1_RGB_NO_MIPMAPS.", srcfile);
					exit(1);
				}
			}
			else if( version[0] == '2' && version[1] == '0' )
			{
				// Read texture type
				read_big_endian_2byte_word(&texture_type, f);
				if(texture_type==ETC2PACKAGE_RG_SIGNED_NO_MIPMAPS) 
				{
					texture_type=ETC2PACKAGE_RG_NO_MIPMAPS;
					formatSigned=1;
					//System.out.println("Decompressing 2-channel signed data");
				}
				if(texture_type==ETC2PACKAGE_R_SIGNED_NO_MIPMAPS) 
				{
					texture_type=ETC2PACKAGE_R_NO_MIPMAPS;
					formatSigned=1;
					//System.out.println("Decompressing 1-channel signed data");
				}
		       if(texture_type==ETC2PACKAGE_sRGB_NO_MIPMAPS)
		       {
		         // The SRGB formats are decoded just as RGB formats -- use RGB format for decompression.
		         texture_type=ETC2PACKAGE_RGB_NO_MIPMAPS;
		       }
		       if(texture_type==ETC2PACKAGE_sRGBA_NO_MIPMAPS)
		       {
		         // The SRGB formats are decoded just as RGB formats -- use RGB format for decompression.
		         texture_type=ETC2PACKAGE_RGBA_NO_MIPMAPS;
		       }
		       if(texture_type==ETC2PACKAGE_sRGBA1_NO_MIPMAPS)
		       {
		         // The SRGB formats are decoded just as RGB formats -- use RGB format for decompression.
		         texture_type=ETC2PACKAGE_sRGBA1_NO_MIPMAPS;
		       }
				if(texture_type==ETC2PACKAGE_RGBA_NO_MIPMAPS_OLD) 
				{
					System.out.println("\n\nThe file %s contains a compressed texture created using an old version of ETCPACK.",srcfile);
					System.out.println("decompression is not supported in this version.");
					exit(1);
				}
				if(!(texture_type==ETC2PACKAGE_RGB_NO_MIPMAPS||texture_type==ETC2PACKAGE_sRGB_NO_MIPMAPS||texture_type==ETC2PACKAGE_RGBA_NO_MIPMAPS||texture_type==ETC2PACKAGE_sRGBA_NO_MIPMAPS||texture_type==ETC2PACKAGE_R_NO_MIPMAPS||texture_type==ETC2PACKAGE_RG_NO_MIPMAPS||texture_type==ETC2PACKAGE_RGBA1_NO_MIPMAPS||texture_type==ETC2PACKAGE_sRGBA1_NO_MIPMAPS))
				{
					System.out.println("\n\n The file %s does not contain a texture of known format.", srcfile);
					System.out.println("Known formats: ETC2PACKAGE_RGB_NO_MIPMAPS.", srcfile);
					exit(1);
				}
			}
			else
			{
				System.out.println("\n\n The file %s is not of version 1.0 or 2.0 but of version %c.%c.",srcfile, version[0], version[1]);
				System.out.println("Aborting.");
				exit(1);
			}
			format=texture_type;
			System.out.println("textype: %d",texture_type);
			// ETC2 is backwards compatible, which means that an ETC2-capable decompressor can also handle
			// old ETC1 textures without any problems. Thus a version 1.0 file with ETC1_RGB_NO_MIPMAPS and a 
			// version 2.0 file with ETC2PACKAGE_RGB_NO_MIPMAPS can be handled by the same ETC2-capable decompressor

			// Read how many pixels the blocks make up

			read_big_endian_2byte_word(&w, f);
			read_big_endian_2byte_word(&h, f);
			width = w;
			height = h;

			// Read how many pixels contain active data (the rest are just
			// for making sure we have a 4*a x 4*b size).

			read_big_endian_2byte_word(&w, f);
			read_big_endian_2byte_word(&h, f);
			active_width = w;
			active_height = h;
		}
		System.out.println("Width = %d, Height = %d",width, height);
		System.out.println("active pixel area: top left %d x %d area.",active_width, active_height);

		if(format==ETC2PACKAGE_RG_NO_MIPMAPS)
			img=(uint8*)malloc(3*width*height*2);
		else
			img=(uint8*)malloc(3*width*height);
		if(!img)
		{
			System.out.println("Error: could not allocate memory");
			exit(0);
		}
		if(format==ETC2PACKAGE_RGBA_NO_MIPMAPS||format==ETC2PACKAGE_R_NO_MIPMAPS||format==ETC2PACKAGE_RG_NO_MIPMAPS||format==ETC2PACKAGE_RGBA1_NO_MIPMAPS||format==ETC2PACKAGE_sRGBA_NO_MIPMAPS||format==ETC2PACKAGE_sRGBA1_NO_MIPMAPS)
		{
			//System.out.println("alpha channel decompression");
			alphaimg=(uint8*)malloc(width*height*2);
			setupAlphaTableAndValtab();
			if(!alphaimg)
			{
				System.out.println("Error: could not allocate memory for alpha");
				exit(0);
			}
		}
		if(format==ETC2PACKAGE_RG_NO_MIPMAPS) 
		{
			alphaimg2=(uint8*)malloc(width*height*2);
			if(!alphaimg2)
			{
				System.out.println("Error: could not allocate memory");
				exit(0);
			}
		}

		for(int y=0;y<height/4;y++)
		{
			for(int x=0;x<width/4;x++)
			{
				//decode alpha channel for RGBA
				if(format==ETC2PACKAGE_RGBA_NO_MIPMAPS||format==ETC2PACKAGE_sRGBA_NO_MIPMAPS) 
				{
					uint8 alphablock[8];
					fread(alphablock,1,8,f);
					decompressBlockAlpha(alphablock,alphaimg,width,height,4*x,4*y);
				}
				//color channels for most normal modes
				if(format!=ETC2PACKAGE_R_NO_MIPMAPS&&format!=ETC2PACKAGE_RG_NO_MIPMAPS) 
				{
					//we have normal ETC2 color channels, decompress these
					read_big_endian_4byte_word(&block_part1,f);
					read_big_endian_4byte_word(&block_part2,f);
					if(format==ETC2PACKAGE_RGBA1_NO_MIPMAPS||format==ETC2PACKAGE_sRGBA1_NO_MIPMAPS)
						decompressBlockETC21BitAlpha(block_part1, block_part2,img,alphaimg,width,height,4*x,4*y);
					else
						decompressBlockETC2(block_part1, block_part2,img,width,height,4*x,4*y);		
				}
				//one or two 11-bit alpha channels for R or RG.
				if(format==ETC2PACKAGE_R_NO_MIPMAPS||format==ETC2PACKAGE_RG_NO_MIPMAPS) 
				{
					uint8 alphablock[8];
					fread(alphablock,1,8,f);
					decompressBlockAlpha16bit(alphablock,alphaimg,width,height,4*x,4*y);
				}
				if(format==ETC2PACKAGE_RG_NO_MIPMAPS) 
				{
					uint8 alphablock[8];
					fread(alphablock,1,8,f);
					decompressBlockAlpha16bit(alphablock,alphaimg2,width,height,4*x,4*y);
				}
			}
		}
		if(format==ETC2PACKAGE_RG_NO_MIPMAPS) 
		{
			for(int y=0;y<height;y++)
			{
				for(int x=0;x<width;x++)
				{
					img[6*(y*width+x)]=alphaimg[2*(y*width+x)];
					img[6*(y*width+x)+1]=alphaimg[2*(y*width+x)+1];
					img[6*(y*width+x)+2]=alphaimg2[2*(y*width+x)];
					img[6*(y*width+x)+3]=alphaimg2[2*(y*width+x)+1];
					img[6*(y*width+x)+4]=0;
					img[6*(y*width+x)+5]=0;
				}
			}
		}

		// Ok, and now only write out the active pixels to the .ppm file.
		// (But only if the active pixels differ from the total pixels)

		if( !(height == active_height && width == active_width) )
		{
			if(format==ETC2PACKAGE_RG_NO_MIPMAPS)
				newimg=(uint8*)malloc(3*active_width*active_height*2);
			else
				newimg=(uint8*)malloc(3*active_width*active_height);
			
			if(format==ETC2PACKAGE_RGBA_NO_MIPMAPS||format==ETC2PACKAGE_RGBA1_NO_MIPMAPS||format==ETC2PACKAGE_R_NO_MIPMAPS||format==ETC2PACKAGE_sRGBA_NO_MIPMAPS||format==ETC2PACKAGE_sRGBA1_NO_MIPMAPS)
			{
				newalphaimg = (uint8*)malloc(active_width*active_height*2);
			}

			if(!newimg)
			{
				System.out.println("Error: could not allocate memory");
				exit(0);
			}
			
			// Convert from total area to active area:

			for(yy = 0; yy<active_height; yy++)
			{
				for(xx = 0; xx< active_width; xx++)
				{
					if(format!=ETC2PACKAGE_R_NO_MIPMAPS&&format!=ETC2PACKAGE_RG_NO_MIPMAPS) 
					{
						newimg[ (yy*active_width)*3 + xx*3 + 0 ] = img[ (yy*width)*3 + xx*3 + 0];
						newimg[ (yy*active_width)*3 + xx*3 + 1 ] = img[ (yy*width)*3 + xx*3 + 1];
						newimg[ (yy*active_width)*3 + xx*3 + 2 ] = img[ (yy*width)*3 + xx*3 + 2];
					}
					else if(format==ETC2PACKAGE_RG_NO_MIPMAPS) 
					{
						newimg[ (yy*active_width)*6 + xx*6 + 0 ] = img[ (yy*width)*6 + xx*6 + 0];
						newimg[ (yy*active_width)*6 + xx*6 + 1 ] = img[ (yy*width)*6 + xx*6 + 1];
						newimg[ (yy*active_width)*6 + xx*6 + 2 ] = img[ (yy*width)*6 + xx*6 + 2];
						newimg[ (yy*active_width)*6 + xx*6 + 3 ] = img[ (yy*width)*6 + xx*6 + 3];
						newimg[ (yy*active_width)*6 + xx*6 + 4 ] = img[ (yy*width)*6 + xx*6 + 4];
						newimg[ (yy*active_width)*6 + xx*6 + 5 ] = img[ (yy*width)*6 + xx*6 + 5];
					}
					if(format==ETC2PACKAGE_R_NO_MIPMAPS) 
					{
						newalphaimg[ ((yy*active_width) + xx)*2]   = alphaimg[2*((yy*width) + xx)];
						newalphaimg[ ((yy*active_width) + xx)*2+1] = alphaimg[2*((yy*width) + xx)+1];
					}
					if(format==ETC2PACKAGE_RGBA_NO_MIPMAPS||format==ETC2PACKAGE_RGBA1_NO_MIPMAPS||format==ETC2PACKAGE_sRGBA_NO_MIPMAPS||format==ETC2PACKAGE_sRGBA1_NO_MIPMAPS) 
					{
						newalphaimg[ ((yy*active_width) + xx)]   = alphaimg[((yy*width) + xx)];
					}
				}
			}

			free(img);
			img = newimg;
			if(format==ETC2PACKAGE_RGBA_NO_MIPMAPS||format==ETC2PACKAGE_RGBA1_NO_MIPMAPS||format==ETC2PACKAGE_R_NO_MIPMAPS||format==ETC2PACKAGE_sRGBA_NO_MIPMAPS||format==ETC2PACKAGE_sRGBA1_NO_MIPMAPS)
			{
				free(alphaimg);
				alphaimg=newalphaimg;
			}
			if(format==ETC2PACKAGE_RG_NO_MIPMAPS) 
			{
				free(alphaimg);
				free(alphaimg2);
       alphaimg = NULL;
       alphaimg2 = NULL;
			}
		}
	}
	else
 {
		System.out.println("Error: could not open <%s>.",srcfile);
   exit(1);
 }
	height=active_height;
	width=active_width;
	fclose(f);
}*/

/*
//Writes output file 
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
void writeOutputFile(char *dstfile, uint8* img, uint8* alphaimg, int width, int height) 
{
	char str[300];

	if(format!=ETC2PACKAGE_R_NO_MIPMAPS&&format!=ETC2PACKAGE_RG_NO_MIPMAPS) 
	{
		fWritePPM("tmp.ppm",width,height,img,8,false);
		System.out.println("Saved file tmp.ppm ");
	}
	else if(format==ETC2PACKAGE_RG_NO_MIPMAPS) 
	{
		fWritePPM("tmp.ppm",width,height,img,16,false);
	}
	if(format==ETC2PACKAGE_RGBA_NO_MIPMAPS||format==ETC2PACKAGE_RGBA1_NO_MIPMAPS||format==ETC2PACKAGE_sRGBA_NO_MIPMAPS||format==ETC2PACKAGE_sRGBA1_NO_MIPMAPS)
		fWritePGM("alphaout.pgm",width,height,alphaimg,false,8);
	if(format==ETC2PACKAGE_R_NO_MIPMAPS)
		fWritePGM("alphaout.pgm",width,height,alphaimg,false,16);

	// Delete destination file if it exists
	if(fileExist(dstfile))
	{
		sprintf(str, "del %s",dstfile);	
		system(str);
	}

	int q = find_pos_of_extension(dstfile);
	if(!strcmp(&dstfile[q],".ppm")&&format!=ETC2PACKAGE_R_NO_MIPMAPS) 
	{
		// Already a .ppm file. Just rename. 
		sprintf(str,"move tmp.ppm %s",dstfile);
		System.out.println("Renaming destination file to %s",dstfile);
	}
	else
	{
		// Converting from .ppm to other file format
		// 
		// Use your favorite command line image converter program,
		// for instance Image Magick. Just make sure the syntax can
		// be written as below:
		// 
		// C:\magick convert source.ppm dest.jpg
		//
		if(format==ETC2PACKAGE_RGBA_NO_MIPMAPS||format==ETC2PACKAGE_RGBA1_NO_MIPMAPS||format==ETC2PACKAGE_sRGBA_NO_MIPMAPS||format==ETC2PACKAGE_sRGBA1_NO_MIPMAPS) 
		{
           // Somewhere after version 6.7.1-2 of ImageMagick the following command gives the wrong result due to a bug. 
			// sprintf(str,"composite -compose CopyOpacity alphaout.pgm tmp.ppm %s",dstfile);
           // Instead we read the file and write a tga.

			System.out.println("Converting destination file from .ppm/.pgm to %s with alpha",dstfile);
           int rw, rh;
           unsigned char *pixelsRGB;
           unsigned char *pixelsA;
			fReadPPM("tmp.ppm", rw, rh, pixelsRGB, 8);
           fReadPGM("alphaout.pgm", rw, rh, pixelsA, 8);
			fWriteTGAfromRGBandA(dstfile, rw, rh, pixelsRGB, pixelsA, true);
           free(pixelsRGB);
           free(pixelsA);
           sprintf(str,""); // Nothing to execute.
		}
		else if(format==ETC2PACKAGE_R_NO_MIPMAPS) 
		{
			sprintf(str,"magick convert alphaout.pgm %s",dstfile);
			System.out.println("Converting destination file from .pgm to %s",dstfile);
		}
		else 
		{
			sprintf(str,"magick convert tmp.ppm %s",dstfile);
			System.out.println("Converting destination file from .ppm to %s",dstfile);
		}
	}
	// Execute system call
	system(str);
	
	free(img);
	if(alphaimg!=NULL)
		free(alphaimg);
}*/
/*
//Calculates the PSNR between two files
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
double calculatePSNRfile(String srcfile, byte[] origimg, byte[] origalpha)
{
	byte[] alphaimg, img;
	int active_width, active_height;
	uncompressFile(srcfile,img,alphaimg,active_width,active_height);

	// calculate Mean Square Error (MSE)
	double MSER=0,MSEG=0,MSEB=0,MSEA, PSNRR,PSNRG,PSNRA;
	double MSE;
	double wMSE;
	double PSNR=0;
	double wPSNR;
	double err;
	MSE = 0;
	MSEA=0;
	wMSE = 0;
	int width=((active_width+3)/4)*4;
	int height=((active_height+3)/4)*4;
	int numpixels = 0;
	for(int y=0;y<active_height;y++)
	{
		for(int x=0;x<active_width;x++)
		{
			if(format!=FORMAT.ETC2PACKAGE_R_NO_MIPMAPS&&format!=FORMAT.ETC2PACKAGE_RG_NO_MIPMAPS) 
			{
				//we have regular color channels..
				if((format != FORMAT.ETC2PACKAGE_RGBA1_NO_MIPMAPS && format != FORMAT.ETC2PACKAGE_sRGBA1_NO_MIPMAPS) || alphaimg[y*width + x] > 0)
				{
					err = img[y*active_width*3+x*3+0] - origimg[y*width*3+x*3+0];
					MSE  += ((err * err)/3.0);
					wMSE += PERCEPTUAL_WEIGHT_R_SQUARED * (err*err);
					err = img[y*active_width*3+x*3+1] - origimg[y*width*3+x*3+1];
					MSE  += ((err * err)/3.0);
					wMSE += PERCEPTUAL_WEIGHT_G_SQUARED * (err*err);
					err = img[y*active_width*3+x*3+2] - origimg[y*width*3+x*3+2];
					MSE  += ((err * err)/3.0);
					wMSE += PERCEPTUAL_WEIGHT_B_SQUARED * (err*err);
					numpixels++;
				}
			}
			else if(format==FORMAT.ETC2PACKAGE_RG_NO_MIPMAPS) 
			{
				int rorig = (origimg[6*(y*width+x)+0]<<8)+origimg[6*(y*width+x)+1];
				int rnew =  (    img[6*(y*active_width+x)+0]<<8)+    img[6*(y*active_width+x)+1];
				int gorig = (origimg[6*(y*width+x)+2]<<8)+origimg[6*(y*width+x)+3];
				int gnew =  (    img[6*(y*active_width+x)+2]<<8)+    img[6*(y*active_width+x)+3];
				err=rorig-rnew;
				MSER+=(err*err);
				err=gorig-gnew;
				MSEG+=(err*err);
			}
			else if(format==FORMAT.ETC2PACKAGE_R_NO_MIPMAPS) 
			{
				int aorig = (((int)origalpha[2*(y*width+x)+0])<<8)+origalpha[2*(y*width+x)+1];
				int anew =  (((int)alphaimg[2*(y*active_width+x)+0])<<8)+alphaimg[2*(y*active_width+x)+1];
				err=aorig-anew;
				MSEA+=(err*err);
			}
		}
	}
	if(format == FORMAT.ETC2PACKAGE_RGBA1_NO_MIPMAPS || format == FORMAT.ETC2PACKAGE_sRGBA1_NO_MIPMAPS)
	{
		MSE = MSE / (1.0 * numpixels);
		wMSE = wMSE / (1.0 * numpixels);
		PSNR = 10*Math.log((1.0*255*255)/MSE)/Math.log(10.0);
		wPSNR = 10*Math.log((1.0*255*255)/wMSE)/Math.log(10.0);
		System.out.println("PSNR only calculated on pixels where compressed alpha > 0");
		System.out.println("color PSNR: "+PSNR+"weighted PSNR: "+wPSNR+"");
	}
	else if(format!=FORMAT.ETC2PACKAGE_R_NO_MIPMAPS&&format!=FORMAT.ETC2PACKAGE_RG_NO_MIPMAPS) 
	{
		MSE = MSE / (active_width * active_height);
		wMSE = wMSE / (active_width * active_height);
		PSNR = 10*Math.log((1.0*255*255)/MSE)/Math.log(10.0);
		wPSNR = 10*Math.log((1.0*255*255)/wMSE)/Math.log(10.0);
		if(format == FORMAT.ETC2PACKAGE_RGBA_NO_MIPMAPS || format == FORMAT.ETC2PACKAGE_sRGBA_NO_MIPMAPS)
			System.out.println("PSNR only calculated on RGB, not on alpha");
		System.out.println("color PSNR: "+PSNR+"\nweighted PSNR: "+wPSNR+"");
	}
	else if(format==FORMAT.ETC2PACKAGE_RG_NO_MIPMAPS) 
	{
		MSER = MSER / (active_width * active_height);
		MSEG = MSEG / (active_width * active_height);
		PSNRR = 10*Math.log((1.0*65535*65535)/MSER)/Math.log(10.0);
		PSNRG = 10*Math.log((1.0*65535*65535)/MSEG)/Math.log(10.0);
		System.out.println("red PSNR: "+PSNRR+"\ngreen PSNR: "+PSNRG+"");
	}
	else if(format==FORMAT.ETC2PACKAGE_R_NO_MIPMAPS) 
	{
		MSEA = MSEA / (active_width * active_height);
		PSNRA = 10*Math.log((1.0*65535.0*65535.0)/MSEA)/Math.log(10.0);
		System.out.println("PSNR: "+PSNRA+"");
	}
	//free(img);
	return PSNR;
}
*/
////Exhaustive code starts here.
////Exhaustive code ends here.


//Compress an image file.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
void compressImageFile(byte[] img, byte[] alphaimg,int width,int height,String dstfile, int expandedwidth, int expandedheight) throws IOException
{
	RandomAccessFile file;
	FileChannel f;
	int x,y,w,h;
	int[] block1=new int[1], block2=new int[1];
	short wi, hi;
	char[] magic=new char[4];
	char[] version=new char[2];
	FORMAT texture_type=format;
	byte[] imgdec;
	byte[] alphaimg2=null;
	imgdec = new byte[expandedwidth*expandedheight*3];
	if(imgdec==null)
	{
		System.out.println("Could not allocate decompression buffer --- exiting");
	}

	magic[0]   = 'P'; magic[1]   = 'K'; magic[2] = 'M'; magic[3] = ' '; 

	if(codec==CODEC.CODEC_ETC2)
	{
		version[0] = '2'; version[1] = '0';
	}
	else
	{
		version[0] = '1'; version[1] = '0';
	}
	 
	file = new RandomAccessFile(dstfile, "rw");
	f = file.getChannel();
	if(f.isOpen())
	{
		w=expandedwidth/4;  w*=4;
		h=expandedheight/4; h*=4;
		wi = (short)w;
		hi = (short)h;
		
		int mipMapCount = Math.min(computeLog(expandedwidth), computeLog(expandedheight)) + 1;
		int halfbytes = 1; 
		
		if(ktxFile) 
		{
			//.ktx file: KTX header followed by compressed binary data.
			KTX_header header = new KTX_header();			
			//endianess int.. if this comes out reversed, all of the other ints will too.
			header.endianness=KTX_ENDIAN_REF;
			
			//these values are always 0/1 for compressed textures.
			header.glType=0;
			header.glTypeSize=1;
			header.glFormat=0;

			header.pixelWidth=width;
			header.pixelHeight=height;
			header.pixelDepth=0;

			//we only support single non-mipmapped non-cubemap textures..
			header.numberOfArrayElements=0;
			header.numberOfFaces=1;
			header.numberOfMipmapLevels = generateMipMaps ? mipMapCount : 1;

			//and no metadata..
			header.bytesOfKeyValueData=0;
			
			//header.glInternalFormat=?
			//header.glBaseInternalFormat=?
			if(format==FORMAT.ETC2PACKAGE_R) 
			{
				header.glBaseInternalFormat=GL_R;
				if(formatSigned!=0)
					header.glInternalFormat=GL_COMPRESSED_SIGNED_R11_EAC;
				else
					header.glInternalFormat=GL_COMPRESSED_R11_EAC;
			}
			else if(format==FORMAT.ETC2PACKAGE_RG) 
			{
				halfbytes=2;
				header.glBaseInternalFormat=GL_RG;
				if(formatSigned!=0)
					header.glInternalFormat=GL_COMPRESSED_SIGNED_RG11_EAC;
				else
					header.glInternalFormat=GL_COMPRESSED_RG11_EAC;
			}
			else if(format==FORMAT.ETC2PACKAGE_RGB) 
			{
				header.glBaseInternalFormat=GL_RGB;
				header.glInternalFormat=GL_COMPRESSED_RGB8_ETC2;
			}
			else if(format==FORMAT.ETC2PACKAGE_sRGB) 
			{
				header.glBaseInternalFormat=GL_SRGB;
				header.glInternalFormat=GL_COMPRESSED_SRGB8_ETC2;
			}
			else if(format==FORMAT.ETC2PACKAGE_RGBA) 
			{
				halfbytes=2;
				header.glBaseInternalFormat=GL_RGBA;
				header.glInternalFormat=GL_COMPRESSED_RGBA8_ETC2_EAC;
			}
			else if(format==FORMAT.ETC2PACKAGE_sRGBA) 
			{
				halfbytes=2;
				header.glBaseInternalFormat=GL_SRGB8_ALPHA8;
				header.glInternalFormat=GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC;
			}
			else if(format==FORMAT.ETC2PACKAGE_RGBA1) 
			{
				header.glBaseInternalFormat=GL_RGBA;
				header.glInternalFormat=GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2;
			}
			else if(format==FORMAT.ETC2PACKAGE_sRGBA1) 
			{
				header.glBaseInternalFormat=GL_SRGB8_ALPHA8;
				header.glInternalFormat=GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2;
			}
			else if(format==FORMAT.ETC1_RGB) 
			{
				header.glBaseInternalFormat=GL_RGB;
				header.glInternalFormat=GL_ETC1_RGB8_OES;
			}
			else 
			{
				System.out.println("internal error: bad format!");
				System.exit(1);
			}
			//write header
			fwrite( header, 1,f);			
		}
		else 
		{
			if(generateMipMaps)
				System.out.println("PKM does not support mipmaps");
			
			//pkm headers are NOT ok with mipmaps
			this.generateMipMaps = false;
			//.pkm file, contains small header..

			// Write magic number
			fwrite(magic[0], 1, f);
			fwrite(magic[1], 1, f);
			fwrite(magic[2], 1, f);
			fwrite(magic[3], 1, f);
		
			// Write version
			fwrite(version[0], 1, f);
			fwrite(version[1], 1, f);

			// Write texture type
			if(texture_type==FORMAT.ETC2PACKAGE_RG&&formatSigned!=0) 
			{
				short temp = (short)FORMAT.ETC2PACKAGE_RG_SIGNED.ordinal();
				write_big_endian_2byte_word(temp,f);
			}
			else if(texture_type==FORMAT.ETC2PACKAGE_R&&formatSigned!=0) 
			{
				short temp = (short)FORMAT.ETC2PACKAGE_R_SIGNED.ordinal();
				write_big_endian_2byte_word(temp,f);
			}
			else
				write_big_endian_2byte_word((short)texture_type.ordinal(), f);

			// Write binary header: the width and height as unsigned 16-bit words
			write_big_endian_2byte_word(wi, f);
			write_big_endian_2byte_word(hi, f);

			// Also write the active pixels. For instance, if we want to compress
			// a 128 x 129 image, we have to extend it to 128 x 132 pixels.
			// Then the wi and hi written above will be 128 and 132, but the
			// additional information that we write below will be 128 and 129,
			// to indicate that it is only the top 129 lines of data in the 
			// decompressed image that will be valid data, and the rest will
			// be just garbage. 

			short activew, activeh;
			activew = (short)width;
			activeh = (short)height;

			write_big_endian_2byte_word(activew, f);
			write_big_endian_2byte_word(activeh, f);
		}
		int totblocks = expandedheight/4 * expandedwidth/4;
		int countblocks = 0;
		double percentageblocks=-1.0;
		double oldpercentageblocks;
		
		if(format==FORMAT.ETC2PACKAGE_RG) 
		{
			//extract data from red and green channel into two alpha channels.
			//note that the image will be 16-bit per channel in this case.
			alphaimg= new byte[expandedwidth*expandedheight*2];
			alphaimg2=new byte[expandedwidth*expandedheight*2];
			setupAlphaTableAndValtab();
			if(alphaimg==null||alphaimg2==null) 
			{
				System.out.println("failed allocating space for alpha buffers!");
				System.exit(1);
			}
			for(y=0;y<expandedheight;y++)
			{
				for(x=0;x<expandedwidth;x++)
				{
					alphaimg[2*(y*expandedwidth+x)]=img[6*(y*expandedwidth+x)];
					alphaimg[2*(y*expandedwidth+x)+1]=img[6*(y*expandedwidth+x)+1];
					alphaimg2[2*(y*expandedwidth+x)]=img[6*(y*expandedwidth+x)+2];
					alphaimg2[2*(y*expandedwidth+x)+1]=img[6*(y*expandedwidth+x)+3];
				}
			}
		}
		
		
		if(generateMipMaps) {
			for(int m = 0; m < mipMapCount; m++) {
				int imagesize = (expandedwidth * expandedheight * halfbytes) / 2;// minimum halfbytes*8 bytes (16 or 32)
				
				// write this mipmaps image size 
				fwrite(imagesize, 1, f);
				// compress and write the mip map
				compressImageToFile(f, img, alphaimg, expandedwidth, expandedheight);
				
				// use a 4 pixel gaussian from the previous mip to make the next
				
				int widthDropFactor = expandedwidth/2 < 4 ? 1 : 2;// only drop down if new size would be half current (don't drop below 4x4)
				int heightDropFactor = expandedheight/2 < 4 ? 1 : 2;
				
				expandedwidth = expandedwidth/widthDropFactor;
				expandedheight = expandedheight/heightDropFactor;
				
				byte[] currentImg = img;
				byte[] currentAlphaimg = alphaimg;
				
				int newmipmapdatasize = img.length/(widthDropFactor*heightDropFactor);		
				
				img = new byte[newmipmapdatasize];
				alphaimg = alphaimg == null ? null : new byte[newmipmapdatasize/3];
				
				for (int y2 = 0; y2 < expandedheight; y2++) {
					for (int x2 = 0; x2 < expandedwidth; x2++) {									
		
						img[(y2*expandedwidth+x2)*3 +0] = (byte)((
								 (currentImg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*3 +0]&0xff)
								+(currentImg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*3 +0]&0xff)
								+(currentImg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*3 +0]&0xff)
								+(currentImg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*3 +0]&0xff))/4);
						img[(y2*expandedwidth+x2)*3 +1] = (byte)((
								 (currentImg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*3 +1]&0xff)
								+(currentImg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*3 +1]&0xff)
								+(currentImg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*3 +1]&0xff)
								+(currentImg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*3 +1]&0xff))/4);
						img[(y2*expandedwidth+x2)*3 +2] = (byte)((
								 (currentImg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*3 +2]&0xff)
								+(currentImg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*3 +2]&0xff)
								+(currentImg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*3 +2]&0xff)
								+(currentImg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*3 +2]&0xff))/4);
		
					}
				}
				if(alphaimg != null) {
					for (int y2 = 0; y2 < expandedheight; y2++) {
						for (int x2 = 0; x2 < expandedwidth; x2++) {
							if (format == FORMAT.ETC2PACKAGE_RGBA || format == FORMAT.ETC2PACKAGE_sRGBA) {
								alphaimg[y2*(expandedwidth)+x2] = (byte)((
										 (currentAlphaimg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*1 +0]&0xff)
										+(currentAlphaimg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*1 +0]&0xff)
										+(currentAlphaimg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*1 +0]&0xff)
										+(currentAlphaimg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*1 +0]&0xff))/4);
							}else if(format == FORMAT.ETC2PACKAGE_RGBA1 || format == FORMAT.ETC2PACKAGE_sRGBA1) {
								int a = ((
										 (currentAlphaimg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*1 +0]&0xff)
										+(currentAlphaimg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*1 +0]&0xff)
										+(currentAlphaimg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*1 +0]&0xff)
										+(currentAlphaimg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*1 +0]&0xff))/4);
								a = a <= 127 ? 0 : 255;
								alphaimg[y2*(expandedwidth)+x2] = (byte)a;
							}
						}
					}	
				}
			}
		} else {
			int imagesize = (expandedwidth * expandedheight * halfbytes) / 2;
			
			// write this mipmaps image size 
			fwrite(imagesize, 1, f);
			// compress and write the mip map
			compressImageToFile(f, img, alphaimg, expandedwidth, expandedheight);
		}
		
		System.out.println("");
		f.close();
		System.out.println("Saved file <"+dstfile+">.");
	}
}
		
void compressImageToFile(FileChannel f, byte[] img, byte[] alphaimg, int expandedwidth, int expandedheight)
		throws IOException {	
	
// can only compress a 4x4 block of RGB
	if(img.length<4*4*3)
		return;
	int x, y, w, h;
	int[] block1 = new int[1], block2 = new int[1];
	byte[] imgdec;
	byte[] alphaimg2 = null;
	imgdec = new byte[expandedwidth * expandedheight * 3];

	int totblocks = expandedheight / 4 * expandedwidth / 4;
	totblocks = totblocks < 1 ? 1: totblocks;
	int countblocks = 0;
	double percentageblocks = -1.0;
	double oldpercentageblocks;
	
	int ymax = expandedheight / 4;
	ymax = ymax < 1 ? 1 : ymax;
	int xmax = expandedwidth / 4;
	xmax = xmax < 1 ? 1 : xmax;
	
	for(y=0;y<ymax;y++)
	{
		for(x=0;x<xmax;x++)
		{
			countblocks++;
			oldpercentageblocks = percentageblocks;
			percentageblocks = 100.0*countblocks/(1.0*totblocks);
			//compress color channels
			if(codec==CODEC.CODEC_ETC) 
			{
				if(metric==METRIC.METRIC_NONPERCEPTUAL) 
				{
					if(speed==SPEED.SPEED_FAST)
						compressBlockDiffFlipFast(img, imgdec, expandedwidth, expandedheight, 4*x, 4*y, block1, block2);
					else
//#if EXHAUSTIVE_CODE_ACTIVE
//							compressBlockETC1Exhaustive(img, imgdec, expandedwidth, expandedheight, 4*x, 4*y, block1, block2);		
//#else
						System.out.println("Not implemented in this version");
//#endif
				}
				else 
				{
					if(speed==SPEED.SPEED_FAST)
						compressBlockDiffFlipFastPerceptual(img, imgdec, expandedwidth, expandedheight, 4*x, 4*y, block1, block2);
					else
//#if EXHAUSTIVE_CODE_ACTIVE
//							compressBlockETC1ExhaustivePerceptual(img, imgdec, expandedwidth, expandedheight, 4*x, 4*y, block1, block2);	
//#else
						System.out.println("Not implemented in this version");
//#endif
				}
			}
			else 
			{
				byte[] alphadata= new byte[8];
				//compression of alpha channel in case of 4-bit alpha. Uses 8-bit alpha channel as input, and has 8-bit precision.
				if(format==FORMAT.ETC2PACKAGE_RGBA||format==FORMAT.ETC2PACKAGE_sRGBA) 
				{					
					if(speed==SPEED.SPEED_SLOW)
						compressBlockAlphaSlow(alphaimg,4*x,4*y,expandedwidth,expandedheight,alphadata);
					else
						compressBlockAlphaFast(alphaimg,4*x,4*y,expandedwidth,expandedheight,alphadata);
					//write the 8 bytes of alphadata into f.
					fwrite(alphadata,1,8,f);
				}
				
				if(format==FORMAT.ETC2PACKAGE_R||format==FORMAT.ETC2PACKAGE_RG) 
				{
					//don't compress color
				}
				else if(format==FORMAT.ETC2PACKAGE_RGBA1||format==FORMAT.ETC2PACKAGE_sRGBA1) 
				{
					//this is only available for fast/nonperceptual
					if(speed == SPEED.SPEED_SLOW && first_time_message)
					{
						System.out.println("Slow codec not implemented for RGBA1 --- using fast codec instead.");
						first_time_message = false;
					}
					compressBlockETC2Fast(img, alphaimg,imgdec, expandedwidth, expandedheight, 4*x, 4*y, block1, block2);
				}
				else if(metric==METRIC.METRIC_NONPERCEPTUAL) 
				{
					if(speed==SPEED.SPEED_FAST ) 
						compressBlockETC2Fast(img, alphaimg,imgdec, expandedwidth, expandedheight, 4*x, 4*y, block1, block2);
					else
//#if EXHAUSTIVE_CODE_ACTIVE
//							compressBlockETC2Exhaustive(img, imgdec, expandedwidth, expandedheight, 4*x, 4*y, block1, block2);		
//#else
						System.out.println("Not implemented in this version");
//#endif
				}
				else 
				{
					if(speed==SPEED.SPEED_FAST ) 
						compressBlockETC2FastPerceptual(img, imgdec, expandedwidth, expandedheight, 4*x, 4*y, block1, block2);
					else
//#if EXHAUSTIVE_CODE_ACTIVE
//							compressBlockETC2ExhaustivePerceptual(img, imgdec, expandedwidth, expandedheight, 4*x, 4*y, block1, block2);	
//#else
						System.out.println("Not implemented in this version");
//#endif
				}
			}
			
			

			//store compressed color channels
			if(format!=FORMAT.ETC2PACKAGE_R&&format!=FORMAT.ETC2PACKAGE_RG) 
			{
				write_big_endian_4byte_word(block1, f);
				write_big_endian_4byte_word(block2, f);
			}

			//1-channel or 2-channel alpha compression: uses 16-bit data as input, and has 11-bit precision
			if(format==FORMAT.ETC2PACKAGE_R||format==FORMAT.ETC2PACKAGE_RG) 
			{ 
				byte[] alphadata= new byte[8];
				compressBlockAlpha16(alphaimg,4*x,4*y,expandedwidth,expandedheight,alphadata);
				fwrite(alphadata,1,8,f);
			}
			//compression of second alpha channel in RG-compression
			if(format==FORMAT.ETC2PACKAGE_RG) 
			{
				byte[] alphadata = new byte[8];
				compressBlockAlpha16(alphaimg2,4*x,4*y,expandedwidth,expandedheight,alphadata);
				fwrite(alphadata,1,8,f);
			}
//#if 1
			if(verbose)
			{
				if(speed==SPEED.SPEED_FAST) 
				{
					if( ((int)(percentageblocks) != (int)(oldpercentageblocks) ) || percentageblocks == 100.0)
						System.out.println("Compressed "+countblocks+" of "+totblocks+" blocks, "+(100.0*countblocks/(1.0*totblocks))+" finished.");
				}
				else
					System.out.println("Compressed "+countblocks+" of "+totblocks+" blocks, "+(100.0*countblocks/(1.0*totblocks))+" finished." );
			}
//#endif
		}
	}	
}




//Compress an file.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
void compressFile(String srcfile, String dstfile)
{
	byte[][] srcimg=new byte[1][];
	int[] width= new int[1],height= new int[1];
	int[] extendedwidth= new int[1], extendedheight= new int[1];
	//long tstruct;
	long tstart;
	long tstop;

	System.out.print("\n");
	if(codec==CODEC.CODEC_ETC)
		System.out.print("ETC codec, ");
	else
		System.out.print("ETC2 codec, ");
	if(speed==SPEED.SPEED_FAST)
		System.out.print("using FAST compression mode, ");
	else if(speed==SPEED.SPEED_MEDIUM) {
		System.out.print(" MEDIUM compression not supported using FAST, ");
		speed=SPEED.SPEED_FAST;
	}
	else {
		System.out.print("SLOW compression not supported using FAST, ");
		speed=SPEED.SPEED_FAST;
	}
	if(generateMipMaps)
		System.out.print("generating MipMaps, ");
	else
		System.out.print("no MipMaps, ");
	if(metric==METRIC.METRIC_PERCEPTUAL)
		System.out.print("PERCEPTUAL error metric, ");
	else
		System.out.print("NONPERCEPTUAL error metric, ");
	if(format==FORMAT.ETC2PACKAGE_RGBA)
		System.out.print("in RGBA format");
	else if(format==FORMAT.ETC2PACKAGE_sRGBA)
		System.out.print("in sRGBA format");
	else if(format==FORMAT.ETC2PACKAGE_RGBA1)
		System.out.print("in RGB + punch-through alpha format");
	else if(format==FORMAT.ETC2PACKAGE_sRGBA1)
		System.out.print("in sRGB + punch-through alpha format");
	else if(format==FORMAT.ETC2PACKAGE_R)
		System.out.print("in R format");
	else if(format==FORMAT.ETC2PACKAGE_RGB||format==FORMAT.ETC1_RGB)
		System.out.print("in RGB format");
	else if(format==FORMAT.ETC2PACKAGE_RG)
		System.out.print("in RG format");
	else
		System.out.print("in OTHER format");
	System.out.print("\n");
	//if(readCompressParams())
	{
		
		//make sure that alphasrcimg contains the alpha channel or is null here, and pass it to compressimagefile
		byte[][] alphatemp=new byte[1][];
		
		//I only have one readerr now, not eh magick PPM/POGM splitter, so hamd the alph into the targa reader
		if(format==FORMAT.ETC2PACKAGE_R||readSrcFile(srcfile,srcimg,alphatemp,width,height,extendedwidth, extendedheight))
		{
			//make sure that alphasrcimg contains the alpha channel or is null here, and pass it to compressimagefile
			byte[][] alphaimg=new byte[1][];
			if(format==FORMAT.ETC2PACKAGE_RGBA||format==FORMAT.ETC2PACKAGE_RGBA1||format==FORMAT.ETC2PACKAGE_sRGBA||format==FORMAT.ETC2PACKAGE_sRGBA1) 
			{
				// If the input file has alpha it will have been read in by readSrcFile above
				//System.out.println("reading alpha channel....");
				//String str ="magick convert "+srcfile+" -alpha extract alpha.pgm\n";
				//system(str); //fire a commandline !
				//System.err.println("I'd would like to fire: " +str);
				readAlpha(alphaimg,alphatemp[0],width[0],height[0],extendedwidth,extendedheight);
				//System.out.println("ok!");
				setupAlphaTableAndValtab();
			}
			else if(format==FORMAT.ETC2PACKAGE_R) 
			{
				throw new UnsupportedOperationException();
				//TODO: This format assumes the macick exe has put the R data into the odd seperate pgm alpha file
				//String str ="magick convert "+srcfile+" alpha.pgm\n";
				//system(str);
				//System.err.println("I'd would like to fire: " +str);
				//readAlpha(alphaimg,alphatemp[0],width[0],height[0],extendedwidth,extendedheight);
				//System.out.println("read alpha ok, size is "+width+","+height+" ("+extendedwidth+","+extendedheight+")");
				//setupAlphaTableAndValtab();
			}
			System.out.print("Compressing...");

			tstart=System.currentTimeMillis();
			try {
				compressImageFile(srcimg[0],alphaimg[0],width[0],height[0],dstfile,extendedwidth[0], extendedheight[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}			
			tstop=System.currentTimeMillis();
			System.out.println( "It took "+(tstop - tstart)+" milliseconds to compress" );
			
			// do I ned to call this one, looks like it's just handing out the final quality
			//calculatePSNRfile(dstfile,srcimg[0],alphaimg);
		}
	}
}





/**
 * 
 * @param srcimg only RGB  size = width*height*3
 * @param srcimgalpha only A size = width*height*1 or null
 * @param width must be divisible by 4 for now
 * @param height must be divisible by 4 for now
 * @param format ETC2PACKAGE_RGBA, ETC2PACKAGE_sRGBA, 
 * ETC2PACKAGE_RGBA1, ETC2PACKAGE_sRGBA1 must have a valid alphaimg array
 * ETC2PACKAGE_RGB alphaimage should be null
 * @param mipmaps true to create mip maps
 * @return a ByteBuffer that looks exactly like a loaded ktx file with a KTX header and formatted data as requested, along with mip maps if requested
 */
public ByteBuffer compressImageToByteBuffer(byte[] srcimg, byte[] srcimgalpha, int width, int height, FORMAT format, boolean mipmaps)
{
	return compressImageBytes(srcimg, srcimgalpha,  width,  height, 
			CODEC.CODEC_ETC2, SPEED.SPEED_FAST, METRIC.METRIC_PERCEPTUAL, format, true, false, mipmaps);
}
/**
 * 
 * @param srcimg only RGB  size = width*height*3
 * @param srcimgalpha only A size = width*height*1 or null
 * @param width must be divisible by 4 for now
 * @param height must be divisible by 4 for now
 * @param codec CODEC_ETC2 is best
 * @param speed SPEED_FAST is best
 * @param metric METRIC_PERCEPTUAL is best
 * @param format ETC2PACKAGE_RGBA, ETC2PACKAGE_sRGBA, 
 * ETC2PACKAGE_RGBA1, ETC2PACKAGE_sRGBA1 must have a valid alphaimg array
 * ETC2PACKAGE_RGB alphaimage should be null
 *  * @param mipmaps true to create mip maps
 * @return a ByteBuffer that looks exactly like a loaded ktx file with a KTX header and formatted data as requested
 */
public ByteBuffer compressImageBytes(byte[] srcimg, byte[] srcimgalpha, int width, int height, 
                                           CODEC codec, SPEED speed, METRIC metric, FORMAT format, boolean ktxFile, boolean verbose, boolean mipmaps)
{
	this.ktxFile = ktxFile;
	this.verbose = verbose;
	this.generateMipMaps = mipmaps;
	this.codec = codec;
	this.speed = speed;
	this.metric = metric;
	this.format = format;

	int[] extendedwidth = new int[1], extendedheight = new int[1];

	int[] width2=new int[] {width};
	int[] height2=new int[] {height};
	byte[][] img = new byte[][] {srcimg};
 
	
	//make sure width and height are div 4 able
	extendedwidth [0] = width2[0];
	extendedheight [0] = height2[0];
	int wdiv4 = width2[0] / 4;
	int hdiv4 = height2[0] / 4;
 
	int bitrate=8;
	if(format==FORMAT.ETC2PACKAGE_RG)
		bitrate=16;
	
	if (!(wdiv4 * 4 == width2[0])) {
		if (verbose) 
			System.out.print(" Width = " + width2[0] + " is not divisible by four expanding image in x-dir... ");
		if(expandToWidthDivByFour(img, width2[0], height2[0], extendedwidth, extendedheight, bitrate)) {
			if (verbose) 
				System.out.println("OK.");
		} else {
			System.out.println("Error: could not expand image");
			return null;
		}
	}
	if (!(hdiv4 * 4 == height2[0])) {
		if (verbose) 
			System.out.print(" Height = " + height2[0] + " is not divisible by four expanding image in y-dir... ");
		if(expandToHeightDivByFour(img, extendedwidth[0], height2[0], extendedwidth, extendedheight, bitrate)) {
			if (verbose) 
				System.out.println("OK.");
		} else {
			System.out.println("Error: could not expand image");
			return null;
		}
	}

	//make sure that alphasrcimg contains the alpha channel or is null here, and pass it to compressimagefile
	byte[][] alphaimg = new byte[1][];
	if (format == FORMAT.ETC2PACKAGE_RGBA || format == FORMAT.ETC2PACKAGE_RGBA1
		|| format == FORMAT.ETC2PACKAGE_sRGBA || format == FORMAT.ETC2PACKAGE_sRGBA1) {
		readAlpha(alphaimg, srcimgalpha, width2[0], height2[0], extendedwidth, extendedheight);
		setupAlphaTableAndValtab();
	} else if (format == FORMAT.ETC2PACKAGE_R) {
		throw new UnsupportedOperationException();
	}

	try {
		return compressImageBytes(img[0], alphaimg [0], width2[0], height2[0], extendedwidth [0], extendedheight [0]);
	} catch (IOException e) {
		e.printStackTrace();
	}

	System.out.println("No image decompressed, soz");
	return null;
}
protected static int computeLog(int value) {
	int i = 0;

	if (value == 0)
		return -1;
	for (;;) {
		if (value == 1)
			return i;
		value >>= 1;
		i++;
	}
}
ByteBuffer compressImageBytes(byte[] img, byte[] alphaimg, int width, int height, int expandedwidth,	int expandedheight)
		throws IOException {
	ByteBuffer dstBB;
	long tstart = System.currentTimeMillis();
	int w, h;
	short wi, hi;
	
	char[] version = new char[2];
	FORMAT texture_type = format;	

	if (codec == CODEC.CODEC_ETC2) {
		version [0] = '2';
		version [1] = '0';
	} else {
		version [0] = '1';
		version [1] = '0';
	}

	w = expandedwidth / 4;
	w *= 4;
	h = expandedheight / 4;
	h *= 4;
	wi = (short)w;
	hi = (short)h;
	
	int mipMapCount = Math.min(computeLog(expandedwidth), computeLog(expandedheight)) + 1;
	int halfbytes = 1; 

	if (ktxFile) {
		//.ktx file: KTX header followed by compressed binary data.
		KTX_header header = new KTX_header();
		
		//endianess int.. if this comes out reversed, all of the other ints will too.
		header.endianness = KTX_ENDIAN_REF;

		//these values are always 0/1 for compressed textures.
		header.glType = 0;
		header.glTypeSize = 1;
		header.glFormat = 0;
		
		header.pixelWidth = width;
		header.pixelHeight = height;
		header.pixelDepth = 0;

		//we only support single non-mipmapped non-cubemap textures..
		header.numberOfArrayElements = 0;
		header.numberOfFaces = 1;
		header.numberOfMipmapLevels = generateMipMaps ? mipMapCount : 1;

		//and no metadata..
		header.bytesOfKeyValueData = 0;

		if (format == FORMAT.ETC2PACKAGE_R) {
			header.glBaseInternalFormat = GL_R;
			if (formatSigned != 0)
				header.glInternalFormat = GL_COMPRESSED_SIGNED_R11_EAC;
			else
				header.glInternalFormat = GL_COMPRESSED_R11_EAC;
		} else if (format == FORMAT.ETC2PACKAGE_RG) {
			halfbytes = 2;
			header.glBaseInternalFormat = GL_RG;
			if (formatSigned != 0)
				header.glInternalFormat = GL_COMPRESSED_SIGNED_RG11_EAC;
			else
				header.glInternalFormat = GL_COMPRESSED_RG11_EAC;
		} else if (format == FORMAT.ETC2PACKAGE_RGB) {
			header.glBaseInternalFormat = GL_RGB;
			header.glInternalFormat = GL_COMPRESSED_RGB8_ETC2;
		} else if (format == FORMAT.ETC2PACKAGE_sRGB) {
			header.glBaseInternalFormat = GL_SRGB;
			header.glInternalFormat = GL_COMPRESSED_SRGB8_ETC2;
		} else if (format == FORMAT.ETC2PACKAGE_RGBA) {
			halfbytes = 2;
			header.glBaseInternalFormat = GL_RGBA;
			header.glInternalFormat = GL_COMPRESSED_RGBA8_ETC2_EAC;
		} else if (format == FORMAT.ETC2PACKAGE_sRGBA) {
			halfbytes = 2;
			header.glBaseInternalFormat = GL_SRGB8_ALPHA8;
			header.glInternalFormat = GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC;
		} else if (format == FORMAT.ETC2PACKAGE_RGBA1) {
			header.glBaseInternalFormat = GL_RGBA;
			header.glInternalFormat = GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2;
		} else if (format == FORMAT.ETC2PACKAGE_sRGBA1) {
			header.glBaseInternalFormat = GL_SRGB8_ALPHA8;
			header.glInternalFormat = GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2;
		} else if (format == FORMAT.ETC1_RGB) {
			header.glBaseInternalFormat = GL_RGB;
			header.glInternalFormat = GL_ETC1_RGB8_OES;
		} else {
			System.out.println("internal error: bad format!");
			System.exit(1);
		}

		//write size of compressed data.. which depend on the expanded size..
		//one int for level size, then base level
		//add the mipsizes
		int allimagesize = 4 + (w * h * halfbytes) / 2;
		if(generateMipMaps) {
			int mw = w;
			int mh = h;
			for(int m = 1;m < mipMapCount; m++) {		
					
				int widthDropFactor = mw/2 < 4 ? 1 : 2;//half size but don't drop below 4x4
				int heightDropFactor = mh/2 < 4 ? 1 : 2;
				mw=mw/widthDropFactor;
				mh=mh/heightDropFactor;
				
				// same as code in compressImageToBB
				int ymax = mh / 4;
				ymax = ymax < 1 ? 1 : ymax;
				int xmax = mw / 4;
				xmax = xmax < 1 ? 1 : xmax;
				int mipsize = ymax * xmax * halfbytes * 8;

				allimagesize += 4 + mipsize;
			}	
		}
		// note mips can't go below 4x4 in compressed size, 
		//but the mip map sizing sorts that out for actual image size

		// allocate header space, and image data
		dstBB = ByteBuffer.allocateDirect((12 + (13 * 4)) + allimagesize);
		dstBB.order(ByteOrder.BIG_ENDIAN);

		//write header
		fwrite(header, 1, dstBB);		

	} else {
		if(generateMipMaps)
			System.out.println("PKM does not support mipmaps");
		//.pkm doesn't support mipmaps
		this.generateMipMaps = false;
		
		//.pkm file, contains small header..
		char[] magic = new char[4];
		magic [0] = 'P';
		magic [1] = 'K';
		magic [2] = 'M';
		magic [3] = ' ';
		
		if (format == FORMAT.ETC2PACKAGE_RGBA || format == FORMAT.ETC2PACKAGE_sRGBA)
			halfbytes = 2;

		int imagesize = (w * h * halfbytes) / 2;
		// allocate header and image data
		dstBB = ByteBuffer.allocateDirect((4 + 2 + 2 + 4 + 4) + imagesize);

		// Write magic number
		fwrite(magic [0], 1, dstBB);
		fwrite(magic [1], 1, dstBB);
		fwrite(magic [2], 1, dstBB);
		fwrite(magic [3], 1, dstBB);

		// Write version
		fwrite(version [0], 1, dstBB);
		fwrite(version [1], 1, dstBB);

		// Write texture type
		if (texture_type == FORMAT.ETC2PACKAGE_RG && formatSigned != 0) {
			short temp = (short)FORMAT.ETC2PACKAGE_RG_SIGNED.ordinal();
			write_big_endian_2byte_word(temp, dstBB);
		} else if (texture_type == FORMAT.ETC2PACKAGE_R && formatSigned != 0) {
			short temp = (short)FORMAT.ETC2PACKAGE_R_SIGNED.ordinal();
			write_big_endian_2byte_word(temp, dstBB);
		} else
			write_big_endian_2byte_word((short)texture_type.ordinal(), dstBB);

		// Write binary header: the width and height as unsigned 16-bit words
		write_big_endian_2byte_word(wi, dstBB);
		write_big_endian_2byte_word(hi, dstBB);

		// Also write the active pixels. For instance, if we want to compress
		// a 128 x 129 image, we have to extend it to 128 x 132 pixels.
		// Then the wi and hi written above will be 128 and 132, but the
		// additional information that we write below will be 128 and 129,
		// to indicate that it is only the top 129 lines of data in the 
		// decompressed image that will be valid data, and the rest will
		// be just garbage. 

		short activew, activeh;
		activew = (short)width;
		activeh = (short)height;

		write_big_endian_2byte_word(activew, dstBB);
		write_big_endian_2byte_word(activeh, dstBB);
	}
	
	
	int prevpos=0;
	if(generateMipMaps) {
		for(int m = 0; m < mipMapCount; m++) {
			// same as code in compressImageToBB
			int ymax = expandedheight / 4;
			ymax = ymax < 1 ? 1 : ymax;
			int xmax = expandedwidth / 4;
			xmax = xmax < 1 ? 1 : xmax;
			int mipsize = ymax * xmax * halfbytes * 8;
			

			// write this mipmaps image size 
			fwrite(mipsize, 1, dstBB);
			// compress and write the mip map
			int wrote = compressImageToBB(dstBB, img, alphaimg, expandedwidth, expandedheight);
			
			if(mipsize != wrote)
				System.out.println("Bad Mip Size Calc!");
			
			// use a 4 pixel gaussian from the previous mip to make the next
			
			int widthDropFactor = expandedwidth/2 < 4 ? 1 : 2;// half size but don't drop below 4x4
			int heightDropFactor = expandedheight/2 < 4 ? 1 : 2;
			
			expandedwidth = expandedwidth/widthDropFactor;
			expandedheight = expandedheight/heightDropFactor;
			
			byte[] currentImg = img;
			byte[] currentAlphaimg = alphaimg;
			
			int newmipmapdatasize = img.length/(widthDropFactor*heightDropFactor);		
			
			img = new byte[newmipmapdatasize];
			alphaimg = alphaimg == null ? null : new byte[newmipmapdatasize/3];
			
			for (int y2 = 0; y2 < expandedheight; y2++) {
				for (int x2 = 0; x2 < expandedwidth; x2++) {									
	
					img[(y2*expandedwidth+x2)*3 +0] = (byte)((
							 (currentImg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*3 +0]&0xff)
							+(currentImg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*3 +0]&0xff)
							+(currentImg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*3 +0]&0xff)
							+(currentImg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*3 +0]&0xff))/4);
					img[(y2*expandedwidth+x2)*3 +1] = (byte)((
							 (currentImg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*3 +1]&0xff)
							+(currentImg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*3 +1]&0xff)
							+(currentImg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*3 +1]&0xff)
							+(currentImg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*3 +1]&0xff))/4);
					img[(y2*expandedwidth+x2)*3 +2] = (byte)((
							 (currentImg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*3 +2]&0xff)
							+(currentImg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*3 +2]&0xff)
							+(currentImg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*3 +2]&0xff)
							+(currentImg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*3 +2]&0xff))/4);
	
				}
			}
			if(alphaimg != null) {
				for (int y2 = 0; y2 < expandedheight; y2++) {
					for (int x2 = 0; x2 < expandedwidth; x2++) {
						if (format == FORMAT.ETC2PACKAGE_RGBA || format == FORMAT.ETC2PACKAGE_sRGBA) {
							alphaimg[y2*(expandedwidth)+x2] = (byte)((
									 (currentAlphaimg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*1 +0]&0xff)
									+(currentAlphaimg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*1 +0]&0xff)
									+(currentAlphaimg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*1 +0]&0xff)
									+(currentAlphaimg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*1 +0]&0xff))/4);
						}else if(format == FORMAT.ETC2PACKAGE_RGBA1 || format == FORMAT.ETC2PACKAGE_sRGBA1) {
							int a = ((
									 (currentAlphaimg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*1 +0]&0xff)
									+(currentAlphaimg [(((y2*heightDropFactor + (0*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*1 +0]&0xff)
									+(currentAlphaimg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (0*(widthDropFactor-1)) ))*1 +0]&0xff)
									+(currentAlphaimg [(((y2*heightDropFactor + (1*(heightDropFactor-1))) * (expandedwidth*widthDropFactor)) + ((x2*widthDropFactor) + (1*(widthDropFactor-1)) ))*1 +0]&0xff))/4);
							a = a <= 127 ? 0 : 255;
							alphaimg[y2*(expandedwidth)+x2] = (byte)a;
						}
					}
				}	
			}
		}
	} else {
		int imagesize = (expandedwidth * expandedheight * halfbytes) / 2;
		imagesize = imagesize < halfbytes * 8 ? halfbytes * 8 : imagesize;// minimum halfbytes*8 bytes (8 or 16)
		
		// write this mipmaps image size 
		fwrite(imagesize, 1, dstBB);
		// compress and write the mip map
		compressImageToBB(dstBB, img, alphaimg, expandedwidth, expandedheight);
	}
	

	System.out.println("It took " + (System.currentTimeMillis() - tstart) + " milliseconds to compress");
	//dstBB is all loaded up, now set it up for reading
	dstBB.rewind();
	return dstBB;
}

int compressImageToBB(ByteBuffer dstBB, byte[] img, byte[] alphaimg, int expandedwidth, int expandedheight)
		throws IOException {
	
	int posAtStart = dstBB.position();
	// can only compress a 4x4 block of RGB
	if(img.length<4*4*3)
		return 0;
	int x, y, w, h;
	int[] block1 = new int[1], block2 = new int[1];
	byte[] imgdec;
	byte[] alphaimg2 = null;
	imgdec = new byte[expandedwidth * expandedheight * 3];

	int totblocks = expandedheight / 4 * expandedwidth / 4;
	totblocks = totblocks < 1 ? 1: totblocks;
	int countblocks = 0;
	double percentageblocks = -1.0;
	double oldpercentageblocks;

	if (format == FORMAT.ETC2PACKAGE_RG) {
		//extract data from red and green channel into two alpha channels.
		//note that the image will be 16-bit per channel in this case.
		alphaimg = new byte[expandedwidth * expandedheight * 2];
		alphaimg2 = new byte[expandedwidth * expandedheight * 2];
		setupAlphaTableAndValtab();
		if (alphaimg == null || alphaimg2 == null) {
			System.out.println("failed allocating space for alpha buffers!");
			System.exit(1);
		}
		for (y = 0; y < expandedheight; y++) {
			for (x = 0; x < expandedwidth; x++) {
				alphaimg [2 * (y * expandedwidth + x)] = img [6 * (y * expandedwidth + x)];
				alphaimg [2 * (y * expandedwidth + x) + 1] = img [6 * (y * expandedwidth + x) + 1];
				alphaimg2 [2 * (y * expandedwidth + x)] = img [6 * (y * expandedwidth + x) + 2];
				alphaimg2 [2 * (y * expandedwidth + x) + 1] = img [6 * (y * expandedwidth + x) + 3];
			}
		}
	}

	int ymax = expandedheight / 4;
	ymax = ymax < 1 ? 1 : ymax;
	int xmax = expandedwidth / 4;
	xmax = xmax < 1 ? 1 : xmax;
	for (y = 0; y < ymax; y++) {
		for (x = 0; x < xmax; x++) {
			countblocks++;
			oldpercentageblocks = percentageblocks;
			percentageblocks = 100.0 * countblocks / (1.0 * totblocks);
			//compress color channels
			if (codec == CODEC.CODEC_ETC) {
				if (metric == METRIC.METRIC_NONPERCEPTUAL) {
					if (speed == SPEED.SPEED_FAST)
						compressBlockDiffFlipFast(img, imgdec, expandedwidth, expandedheight, 4 * x, 4 * y, block1,
								block2);
					else
						System.out.println("Not implemented in this version");
				} else {
					if (speed == SPEED.SPEED_FAST)
						compressBlockDiffFlipFastPerceptual(img, imgdec, expandedwidth, expandedheight, 4 * x, 4 * y,
								block1, block2);
					else
						System.out.println("Not implemented in this version");
				}
			} else {
				
				byte[] alphadata = new byte[8];
				//compression of alpha channel in case of 4-bit alpha. Uses 8-bit alpha channel as input, and has 8-bit precision.
				if (format == FORMAT.ETC2PACKAGE_RGBA || format == FORMAT.ETC2PACKAGE_sRGBA) {
					
					if (speed == SPEED.SPEED_SLOW)
						compressBlockAlphaSlow(alphaimg, 4 * x, 4 * y, expandedwidth, expandedheight, alphadata);
					else
						compressBlockAlphaFast(alphaimg, 4 * x, 4 * y, expandedwidth, expandedheight, alphadata);
					//write the 8 bytes of alphadata into dstBB.
					fwrite(alphadata, 1, 8, dstBB);
				}				
				
				if (format == FORMAT.ETC2PACKAGE_R || format == FORMAT.ETC2PACKAGE_RG) {
					//don't compress color
				} else if (format == FORMAT.ETC2PACKAGE_RGBA1
							|| format == FORMAT.ETC2PACKAGE_sRGBA1) {
					//this is only available for fast/nonperceptual
					if (speed == SPEED.SPEED_SLOW && first_time_message) {
						System.out.println("Slow codec not implemented for RGBA1 --- using fast codec instead.");
						first_time_message = false;
					}
					compressBlockETC2Fast(img, alphaimg, imgdec, expandedwidth, expandedheight, 4 * x, 4 * y, block1,
							block2);
				} else if (metric == METRIC.METRIC_NONPERCEPTUAL) {
					if (speed == SPEED.SPEED_FAST) {
							compressBlockETC2Fast(img, alphaimg, imgdec, expandedwidth, expandedheight, 4 * x, 4 * y, block1, block2);
					} else
						System.out.println("Not implemented in this version");
				} else {
					if(speed==SPEED.SPEED_FAST ) {
							compressBlockETC2FastPerceptual(img, imgdec, expandedwidth, expandedheight, 4*x, 4*y, block1, block2);
					} else
						System.out.println("Not implemented in this version");
				}
			}

			

			//store compressed color channels
			if (format != FORMAT.ETC2PACKAGE_R && format != FORMAT.ETC2PACKAGE_RG) {
				write_big_endian_4byte_word(block1, dstBB);
				write_big_endian_4byte_word(block2, dstBB);
			}

			//1-channel or 2-channel alpha compression: uses 16-bit data as input, and has 11-bit precision
			if (format == FORMAT.ETC2PACKAGE_R || format == FORMAT.ETC2PACKAGE_RG) {
				byte[] alphadata = new byte[8];
				compressBlockAlpha16(alphaimg, 4 * x, 4 * y, expandedwidth, expandedheight, alphadata);
				fwrite(alphadata, 1, 8, dstBB);
			}
			//compression of second alpha channel in RG-compression
			if (format == FORMAT.ETC2PACKAGE_RG) {
				byte[] alphadata = new byte[8];
				compressBlockAlpha16(alphaimg2, 4 * x, 4 * y, expandedwidth, expandedheight, alphadata);
				fwrite(alphadata, 1, 8, dstBB);
			}
			if (verbose) {
				if (speed == SPEED.SPEED_FAST) {
					if (((int)(percentageblocks) != (int)(oldpercentageblocks)) || percentageblocks == 100.0)
						System.out.println("Compressed "	+ countblocks + " of " + totblocks + " blocks, "
											+ (100.0 * countblocks / (1.0 * totblocks)) + " finished.");
				} else
					System.out.println("Compressed "	+ countblocks + " of " + totblocks + " blocks, "
										+ (100.0 * countblocks / (1.0 * totblocks)) + " finished.");
			}
		}

	}
	return dstBB.position() - posAtStart;
}

//Calculates the PSNR between two files.
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
public static double calculatePSNRTwoFiles(String srcfile1,String srcfile2)
{
	byte[][] srcimg1 = new byte[1][];;
	byte[][] srcimg2 = new byte[1][];
	int[] width1=new int[1], height1=new int[1];
	int[] width2=new int[1], height2=new int[1];
	double PSNR = 0;
	double perceptually_weighted_PSNR;

	if(readSrcFileNoExpand(srcfile1,srcimg1,width1,height1))
	{
		if(readSrcFileNoExpand(srcfile2,srcimg2,width2,height2))
		{
			if((width1 == width2) && (height1 == height2))
			{
				PSNR = calculatePSNR(srcimg1[0], srcimg2[0], width1[0], height1[0]);
				System.out.println("PSNR:" +PSNR);
				perceptually_weighted_PSNR = calculateWeightedPSNR(srcimg1[0], srcimg2[0], width1[0], height1[0], 0.299, 0.587, 0.114);
			}
			else
			{
				System.out.println(" Width and height do no not match for image: width, height = ("+width1+", "+height1+") and ("+width2+", "+height2+")");
			}
		}
		else
		{
			System.out.println("Couldn't open file "+srcfile2+".");
		}
	}
	else
	{
		System.out.println("Couldn't open file "+srcfile1+".");
	}

	return PSNR;
}

//Main function
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
public static void main(String args[])
{
	if(args.length == 0 ) {
		
	//args = new String[] {"D:\\game_media\\Morrowind\\Icons_tga\\gold.tga", "D:\\game_media\\Morrowind\\Icons_tga\\gold2.ktx", "-f", "RGBA"};
	//	new ETCPack( new String[] {"D:\\game_media\\Morrowind\\Icons_tga\\handtohand.tga", 
	//		"D:\\game_media\\Morrowind\\Icons_tga\\handtohand2.ktx", "-f", "RGB"});
	
	//new ETCPack(new String[] {"D:\\game_media\\Morrowind\\Morrowind - Textures_tga\\textures\\tx_dwrv_trim06.tga", 
	//	"D:\\temp\\Textures\\tx_dwrv_trim06.ktx", "-f", "RGB", "-mipmap"});
	
	new ETCPack(new String[] {"D:\\temp\\neoclassicalmaintile03.tga", 
		"D:\\temp\\neoclassicalmaintile03.ktx", "-f", "RGBA", "-mipmap"});
	
	/*new ETCPack(new String[] {"D:\\game_media\\FalloutNV\\Fallout - Textures2_tga\\textures\\pimpboy3billion\\pimpboy3billion.tga", 
		"D:\\game_media\\FalloutNV\\Fallout - Textures2_tga\\textures\\pimpboy3billion\\pimpboy3billion.ktx", 
		"-f", "RGBA"});
	new ETCPack(new String[] {"D:\\game_media\\FalloutNV\\Fallout - Textures2_tga\\textures\\pimpboy3billion\\pimpboy3billion_m.tga", 
		"D:\\game_media\\FalloutNV\\Fallout - Textures2_tga\\textures\\pimpboy3billion\\pimpboy3billion_m.ktx", 
		"-f", "RGBA"});
	new ETCPack(new String[] {"D:\\game_media\\FalloutNV\\Fallout - Textures2_tga\\textures\\pimpboy3billion\\pimpboy3billion_n.tga", 
		"D:\\game_media\\FalloutNV\\Fallout - Textures2_tga\\textures\\pimpboy3billion\\pimpboy3billion_n.ktx",
		"-f", "RGBA"});*/
	
	/*new ETCPack(new String[] {"D:\\game_media\\Fallout4\\texture_archives\\Fallout4_Textures5_ba2_out_tga\\Textures\\Actors\\Dogmeat\\DogmeatBody_d2k.tga", 
		"D:\\game_media\\Fallout4\\texture_archives\\Fallout4_Textures5_ba2_out_tga\\Textures\\Actors\\Dogmeat\\DogmeatBody_d2k.ktx", 
		"-f", "RGBA"});
	new ETCPack(new String[] {"D:\\game_media\\Fallout4\\texture_archives\\Fallout4_Textures5_ba2_out_tga\\Textures\\Actors\\Dogmeat\\DogmeatBody_n2k.tga", 
		"D:\\game_media\\Fallout4\\texture_archives\\Fallout4_Textures5_ba2_out_tga\\Textures\\Actors\\Dogmeat\\DogmeatBody_n2k.ktx",  
		"-f", "RGBA"});*/
/*	new ETCPack(new String[] {"D:\\game_media\\Fallout4\\texture_archives\\Fallout4_Textures5_ba2_out_tga\\Textures\\Actors\\Dogmeat\\DogmeatBody_s2k.tga", 
		"D:\\game_media\\Fallout4\\texture_archives\\Fallout4_Textures5_ba2_out_tga\\Textures\\Actors\\Dogmeat\\DogmeatBody_s2k.ktx", 
		"-f", "RGBA"});
	
	
	new ETCPack(new String[] {"D:\\game_media\\Fallout3\\Fallout - Textures_tga\\textures\\architecture\\urban\\concretedamage01.tga", 
		"D:\\game_media\\Fallout3\\Fallout - Textures_tga\\textures\\architecture\\urban\\concretedamage01.ktx",  
		"-f", "RGBA1"});
	*/
	
	} else {
		new ETCPack(args);
	}
}
/**
 * Constructor to allow buffer based interactions, presumably the next call is 
 * ByteBuffer compressImageFile
 */
public ETCPack() {

}
public ETCPack(String args[]) {
	
	// The source file is always the second last one. 
	String[] srcfile= new String[1];
	String[] dstfile= new String[1];
	if(readArguments(args,srcfile,dstfile))
	{		
		int q = find_pos_of_extension(srcfile[0]);
		int q2 = find_pos_of_extension(dstfile[0]);
		
		if(!fileExist(srcfile[0]))
		{
			System.out.println("Error: file <"+srcfile[0]+"> does not exist.");
			System.exit(0);
		}
		
		if(mode==MODE2.MODE_UNCOMPRESS)
		{
			System.out.println("Decompressing .pkm/.ktx file ...");
			System.err.println("MODE2.MODE_UNCOMPRESS not implemented");
			byte[] alphaimg=null, img;
			int w, h;
			//uncompressFile(srcfile[0],img,alphaimg,w,h);
			//writeOutputFile(dstfile[0],img,alphaimg,w,h);
		}
		else if(mode==MODE2.MODE_PSNR)
		{
			calculatePSNRTwoFiles(srcfile[0],dstfile[0]);
		}
		else
		{
			compressFile(srcfile[0], dstfile[0]);
		}
	}
	else
	{
		System.out.println("ETCPACK v2.74 For ETC and ETC2");
		System.out.println("Compresses and decompresses images using the Ericsson Texture Compression (ETC) version 1.0 and 2.0.\n\nUsage: etcpack srcfile dstfile\n");
		System.out.println("      -s {fast|slow}                     Compression speed. Slow = exhaustive ");
		System.out.println("                                         search for optimal quality");
		System.out.println("                                         (default: fast)");
		System.out.println("      -e {perceptual|nonperceptual}      Error metric: Perceptual (nicest) or ");
		System.out.println("                                         nonperceptual (highest PSNR)");
		System.out.println("                                         (default: perceptual)");
		System.out.println("      -c {etc1|etc2}                     Codec: etc1 (most compatible) or ");
		System.out.println("                                         etc2 (highest quality)");
		System.out.println("                                         (default: etc2)");
		System.out.println("      -f {R|R_signed|RG|RG_signed|       Format: one, two, three or four ");
		System.out.println("          RGB|RGBA1|RGBA8|RGBA|               channels, and 1 or 8 bits(default) for alpha");
        System.out.println("          sRGB|sRGBA1|sRGBA8|sRGBA}           RGB or sRGB.");
		System.out.println("                                         (1 equals punchthrough)");
		System.out.println("                                         (default: RGB)");
		System.out.println("      -v {on|off}                        Detailed progress info. (default on)");
		System.out.println("      -mipmaps                           Generate Mip Maps. (default off)");
		System.out.println("                                                            ");
		System.out.println("Examples: ");
		System.out.println("  etcpack img.ppm img.pkm                Compresses img.ppm to img.pkm in"); 
		System.out.println("                                         ETC2 RGB format");
		System.out.println("  etcpack img.ppm img.ktx                Compresses img.ppm to img.ktx in"); 
		System.out.println("                                         ETC2 RGB format");
		System.out.println("  etcpack img.pkm img_copy.ppm           Decompresses img.pkm to img_copy.ppm");
		System.out.println("  etcpack -s slow img.ppm img.pkm        Compress using the slow mode.");
		System.out.println("  etcpack -p orig.ppm copy.ppm           Calculate PSNR between orig and copy");
		System.out.println("  etcpack -f RGBA8 img.tga img.pkm       Compresses img.tga to img.pkm, using ");
		System.out.println("                                         etc2 + alpha.");
		System.out.println("  etcpack -f RG img.ppm img.pkm          Compresses red and green channels of");
		System.out.println("                                         img.ppm");
	}
	return;
}




//speed ideas
//https://medium.com/@duhroach/building-a-blazing-fast-etc2-compressor-307f3e9aad99

//see if these help
//https://stackoverflow.com/questions/18638743/is-it-better-to-use-system-arraycopy-than-a-for-loop-for-copying-arrays

//mali compression tool suggests:
//https://github.com/google/etc2comp

//https://github.com/wolfpld/etcpak much faster

// my etcpack is 4.0.1!! but where is the code?
//ETCPACK v4.0.1 for ETC and ETC2

/*
Java Convert Bytes to Unsigned Bytes
In Java, byte is data type. It is 8-bit signed (+ ive or - ive) values from -128 to 127. 
The range of unsigned byte is 0 to 255. Note that Java does not provide unsigned byte. 
If we need to represent a number as unsigned byte, we must cast byte to int and mask (&) the new int with a &0xff. 
It gives the last 8-bits or prevents sign extension.
Example:
byte b = -1;
int num = b &0xff //converts byte to unsigned byte in an integer
 */

//NOTICE the brackets are mandatory!
public static byte badd(byte a, byte b) {
    return (byte)((a&0xff)+(b&0xff));
}
public static byte bsub(byte a, byte b) {
    return (byte)((a&0xff)-(b&0xff));
}

//below show how it should be done, don't use just an assurance
public static int ub(byte x) {
    return x&0xff;
}

//notice narrowing means 255 = -1
public static byte ub(int x) {
	//https://stackoverflow.com/questions/7401550/how-to-convert-int-to-unsigned-byte-and-back
	return (byte)x;
}


/*
 the below proves signed byte to unsign int is just (b&0xff)
  System.out.println("lets see here");
		byte b1 = 1;
		byte b2 = 1;
		byte b127 = 127;		
		byte bn128 = -128;
		byte bn127 = -127;
		byte bn126 = -126;
		byte bn1 = -1;
		byte bn2 = -2;
		
		System.out.println("" + b1 + " " + Byte.toUnsignedInt(b1));
		System.out.println("" + b2 + " " + Byte.toUnsignedInt(b2));
		System.out.println("" + b127 + " " + Byte.toUnsignedInt(b127));
		System.out.println("" + bn128 + " " + Byte.toUnsignedInt(bn128));
		System.out.println("" + bn127 + " " + Byte.toUnsignedInt(bn127));
		System.out.println("" + bn126 + " " + Byte.toUnsignedInt(bn126));
		System.out.println("" + bn1 + " " + Byte.toUnsignedInt(bn1));
		System.out.println("" + bn2 + " " + Byte.toUnsignedInt(bn2));
		int x = 255;			
		byte bx = (byte)x;
		System.out.println("" + x + " " + bx);
		
		
		byte a = (byte)-5;
		byte b = 127;
		byte c = (byte)(a&0xff-b&0xff);
		System.out.println("" + a + " " + b + " " + c);// = -128
		byte d = (byte)((a&0xff)-(b&0xff));
		System.out.println("" + a + " " + b + " " + d);// = 124
 */
}