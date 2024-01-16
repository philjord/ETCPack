package etcpack;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import etcpack.ETCPack.KTX_header;
import etcpack.ETCPack.PATTERN;

/**
 
@~English
@page licensing Licensing
 
@section etcdec etcdec.cxx License
 
etcdec.cxx is made available under the terms and conditions of the following
License Agreement.

Software License Agreement

PLEASE REVIEW THE FOLLOWING TERMS AND CONDITIONS PRIOR TO USING THE
ERICSSON TEXTURE COMPRESSION CODEC SOFTWARE (THE "SOFTWARE"). THE USE
OF THE SOFTWARE IS SUBJECT TO THE TERMS AND CONDITIONS OF THE
FOLLOWING SOFTWARE LICENSE AGREEMENT (THE "SLA"). IF YOU DO NOT ACCEPT
SUCH TERMS AND CONDITIONS YOU MAY NOT USE THE SOFTWARE.

Subject to the terms and conditions of the SLA, the licensee of the
Software (the "Licensee") hereby, receives a non-exclusive,
non-transferable, limited, free-of-charge, perpetual and worldwide
license, to copy, use, distribute and modify the Software, but only
for the purpose of developing, manufacturing, selling, using and
distributing products including the Software in binary form, which
products are used for compression and/or decompression according to
the Khronos standard specifications OpenGL, OpenGL ES and
WebGL. Notwithstanding anything of the above, Licensee may distribute
[etcdec.cxx] in source code form provided (i) it is in unmodified
form; and (ii) it is included in software owned by Licensee.

If Licensee institutes, or threatens to institute, patent litigation
against Ericsson or Ericsson's affiliates for using the Software for
developing, having developed, manufacturing, having manufactured,
selling, offer for sale, importing, using, leasing, operating,
repairing and/or distributing products (i) within the scope of the
Khronos framework; or (ii) using software or other intellectual
property rights owned by Ericsson or its affiliates and provided under
the Khronos framework, Ericsson shall have the right to terminate this
SLA with immediate effect. Moreover, if Licensee institutes, or
threatens to institute, patent litigation against any other licensee
of the Software for using the Software in products within the scope of
the Khronos framework, Ericsson shall have the right to terminate this
SLA with immediate effect. However, should Licensee institute, or
threaten to institute, patent litigation against any other licensee of
the Software based on such other licensee's use of any other software
together with the Software, then Ericsson shall have no right to
terminate this SLA.

This SLA does not transfer to Licensee any ownership to any Ericsson
or third party intellectual property rights. All rights not expressly
granted by Ericsson under this SLA are hereby expressly
reserved. Furthermore, nothing in this SLA shall be construed as a
right to use or sell products in a manner which conveys or purports to
convey whether explicitly, by principles of implied license, or
otherwise, any rights to any third party, under any patent of Ericsson
or of Ericsson's affiliates covering or relating to any combination of
the Software with any other software or product (not licensed
hereunder) where the right applies specifically to the combination and
not to the software or product itself.

THE SOFTWARE IS PROVIDED "AS IS". ERICSSON MAKES NO REPRESENTATIONS OF
ANY KIND, EXTENDS NO WARRANTIES OR CONDITIONS OF ANY KIND, EITHER
EXPRESS, IMPLIED OR STATUTORY; INCLUDING, BUT NOT LIMITED TO, EXPRESS,
IMPLIED OR STATUTORY WARRANTIES OR CONDITIONS OF TITLE,
MERCHANTABILITY, SATISFACTORY QUALITY, SUITABILITY, AND FITNESS FOR A
PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE
OF THE SOFTWARE IS WITH THE LICENSEE. SHOULD THE SOFTWARE PROVE
DEFECTIVE, THE LICENSEE ASSUMES THE COST OF ALL NECESSARY SERVICING,
REPAIR OR CORRECTION. ERICSSON MAKES NO WARRANTY THAT THE MANUFACTURE,
SALE, OFFERING FOR SALE, DISTRIBUTION, LEASE, USE OR IMPORTATION UNDER
THE SLA WILL BE FREE FROM INFRINGEMENT OF PATENTS, COPYRIGHTS OR OTHER
INTELLECTUAL PROPERTY RIGHTS OF OTHERS, AND THE VALIDITY OF THE
LICENSE AND THE SLA ARE SUBJECT TO LICENSEE'S SOLE RESPONSIBILITY TO
MAKE SUCH DETERMINATION AND ACQUIRE SUCH LICENSES AS MAY BE NECESSARY
WITH RESPECT TO PATENTS, COPYRIGHT AND OTHER INTELLECTUAL PROPERTY OF
THIRD PARTIES.

THE LICENSEE ACKNOWLEDGES AND ACCEPTS THAT THE SOFTWARE (I) IS NOT
LICENSED FOR; (II) IS NOT DESIGNED FOR OR INTENDED FOR; AND (III) MAY
NOT BE USED FOR; ANY MISSION CRITICAL APPLICATIONS SUCH AS, BUT NOT
LIMITED TO OPERATION OF NUCLEAR OR HEALTHCARE COMPUTER SYSTEMS AND/OR
NETWORKS, AIRCRAFT OR TRAIN CONTROL AND/OR COMMUNICATION SYSTEMS OR
ANY OTHER COMPUTER SYSTEMS AND/OR NETWORKS OR CONTROL AND/OR
COMMUNICATION SYSTEMS ALL IN WHICH CASE THE FAILURE OF THE SOFTWARE
COULD LEAD TO DEATH, PERSONAL INJURY, OR SEVERE PHYSICAL, MATERIAL OR
ENVIRONMENTAL DAMAGE. LICENSEE'S RIGHTS UNDER THIS LICENSE WILL
TERMINATE AUTOMATICALLY AND IMMEDIATELY WITHOUT NOTICE IF LICENSEE
FAILS TO COMPLY WITH THIS PARAGRAPH.

IN NO EVENT SHALL ERICSSON BE LIABLE FOR ANY DAMAGES WHATSOEVER,
INCLUDING BUT NOT LIMITED TO PERSONAL INJURY, ANY GENERAL, SPECIAL,
INDIRECT, INCIDENTAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF OR IN
CONNECTION WITH THE USE OR INABILITY TO USE THE SOFTWARE (INCLUDING
BUT NOT LIMITED TO LOSS OF PROFITS, BUSINESS INTERUPTIONS, OR ANY
OTHER COMMERCIAL DAMAGES OR LOSSES, LOSS OF DATA OR DATA BEING
RENDERED INACCURATE OR LOSSES SUSTAINED BY THE LICENSEE OR THIRD
PARTIES OR A FAILURE OF THE SOFTWARE TO OPERATE WITH ANY OTHER
SOFTWARE) REGARDLESS OF THE THEORY OF LIABILITY (CONTRACT, TORT, OR
OTHERWISE), EVEN IF THE LICENSEE OR ANY OTHER PARTY HAS BEEN ADVISED
OF THE POSSIBILITY OF SUCH DAMAGES.

Licensee acknowledges that "ERICSSON ///" is the corporate trademark
of Telefonaktiebolaget LM Ericsson and that both "Ericsson" and the
figure "///" are important features of the trade names of
Telefonaktiebolaget LM Ericsson. Nothing contained in these terms and
conditions shall be deemed to grant Licensee any right, title or
interest in the word "Ericsson" or the figure "///". No delay or
omission by Ericsson to exercise any right or power shall impair any
such right or power to be construed to be a waiver thereof. Consent by
Ericsson to, or waiver of, a breach by the Licensee shall not
constitute consent to, waiver of, or excuse for any other different or
subsequent breach.

This SLA shall be governed by the substantive law of Sweden. Any
dispute, controversy or claim arising out of or in connection with
this SLA, or the breach, termination or invalidity thereof, shall be
submitted to the exclusive jurisdiction of the Swedish Courts.

*/

//// etcpack v2.74
//// 
//// NO WARRANTY 
//// 
//// BECAUSE THE PROGRAM IS LICENSED FREE OF CHARGE THE PROGRAM IS PROVIDED
//// "AS IS". ERICSSON MAKES NO REPRESENTATIONS OF ANY KIND, EXTENDS NO
//// WARRANTIES OR CONDITIONS OF ANY KIND; EITHER EXPRESS, IMPLIED OR
//// STATUTORY; INCLUDING, BUT NOT LIMITED TO, EXPRESS, IMPLIED OR
//// STATUTORY WARRANTIES OR CONDITIONS OF TITLE, MERCHANTABILITY,
//// SATISFACTORY QUALITY, SUITABILITY AND FITNESS FOR A PARTICULAR
//// PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE
//// PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE, YOU ASSUME
//// THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION. ERICSSON
//// MAKES NO WARRANTY THAT THE MANUFACTURE, SALE, OFFERING FOR SALE,
//// DISTRIBUTION, LEASE, USE OR IMPORTATION UNDER THE LICENSE WILL BE FREE
//// FROM INFRINGEMENT OF PATENTS, COPYRIGHTS OR OTHER INTELLECTUAL
//// PROPERTY RIGHTS OF OTHERS, AND THE VALIDITY OF THE LICENSE IS SUBJECT
//// TO YOUR SOLE RESPONSIBILITY TO MAKE SUCH DETERMINATION AND ACQUIRE
//// SUCH LICENSES AS MAY BE NECESSARY WITH RESPECT TO PATENTS, COPYRIGHT
//// AND OTHER INTELLECTUAL PROPERTY OF THIRD PARTIES.
//// 
//// FOR THE AVOIDANCE OF DOUBT THE PROGRAM (I) IS NOT LICENSED FOR; (II)
//// IS NOT DESIGNED FOR OR INTENDED FOR; AND (III) MAY NOT BE USED FOR;
//// ANY MISSION CRITICAL APPLICATIONS SUCH AS, BUT NOT LIMITED TO
//// OPERATION OF NUCLEAR OR HEALTHCARE COMPUTER SYSTEMS AND/OR NETWORKS,
//// AIRCRAFT OR TRAIN CONTROL AND/OR COMMUNICATION SYSTEMS OR ANY OTHER
//// COMPUTER SYSTEMS AND/OR NETWORKS OR CONTROL AND/OR COMMUNICATION
//// SYSTEMS ALL IN WHICH CASE THE FAILURE OF THE PROGRAM COULD LEAD TO
//// DEATH, PERSONAL INJURY, OR SEVERE PHYSICAL, MATERIAL OR ENVIRONMENTAL
//// DAMAGE. YOUR RIGHTS UNDER THIS LICENSE WILL TERMINATE AUTOMATICALLY
//// AND IMMEDIATELY WITHOUT NOTICE IF YOU FAIL TO COMPLY WITH THIS
//// PARAGRAPH.
//// 
//// IN NO EVENT WILL ERICSSON, BE LIABLE FOR ANY DAMAGES WHATSOEVER,
//// INCLUDING BUT NOT LIMITED TO PERSONAL INJURY, ANY GENERAL, SPECIAL,
//// INDIRECT, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN
//// CONNECTION WITH THE USE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT
//// NOT LIMITED TO LOSS OF PROFITS, BUSINESS INTERUPTIONS, OR ANY OTHER
//// COMMERCIAL DAMAGES OR LOSSES, LOSS OF DATA OR DATA BEING RENDERED
//// INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A FAILURE OF
//// THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS) REGARDLESS OF THE
//// THEORY OF LIABILITY (CONTRACT, TORT OR OTHERWISE), EVEN IF SUCH HOLDER
//// OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
//// 
//// (C) Ericsson AB 2005-2013. All Rights Reserved.
//// 

public class ETCDec {


// Typedefs
//typedef unsigned char uint8;
//typedef unsigned short uint16;
//typedef short int16;

// Macros to help with bit extraction/insertion
static int SHIFT(int size,int startpos){return  ((startpos)-(size)+1);}
static int MASK(int size, int startpos){return  (((2<<(size-1))-1) << SHIFT(size,startpos));}
static int PUTBITS( int dest[], int data, int size, int startpos){return  dest[0] = ((dest[0] & ~MASK(size, startpos)) | ((data << SHIFT(size, startpos)) & MASK(size,startpos)));}
static int SHIFTHIGH(int size, int startpos){return  (((startpos)-32)-(size)+1);}
static int MASKHIGH(int size, int startpos){return  (((1<<(size))-1) << SHIFTHIGH(size,startpos));}
static int PUTBITSHIGH(int[] dest, int data, int size, int startpos){return  dest[0] = ((dest[0] & ~MASKHIGH(size, startpos)) | ((data << SHIFTHIGH(size, startpos)) & MASKHIGH(size,startpos)));}
//Return is an int as more than 8 bits can be asked for
static int GETBITS(int source, int size, int startpos){return   (( (source) >> ((startpos)-(size)+1) ) & ((1<<(size)) -1));}
//Return is an int as more than 8 bits can be asked for
static int GETBITSHIGH(int source, int size, int startpos){return   (( (source) >> (((startpos)-32)-(size)+1) ) & ((1<<(size)) -1));}

static int PGMOUT = 1;

//Thumb macros and definitions
static int R_BITS59T =4;
static int G_BITS59T =4;
static int B_BITS59T =4;
static int R_BITS58H =4;
static int G_BITS58H =4;
static int B_BITS58H =4;
static int MAXIMUM_ERROR =(255*255*16*1000);
static int R =0;
static int G =1;
static int B =2;
static int BLOCKHEIGHT =4;
static int BLOCKWIDTH =4;
static int BINPOW(int power) {return(1<<(power));}
static int TABLE_BITS_59T =3;
static int TABLE_BITS_58H =3;

// Helper Macros

static int CLAMP(int ll, int x, int ul) { return (((x)<(ll)) ? (ll) : (((x)>(ul)) ? (ul) : (x))); }
//TODO: any clamping wanting a range 0-255 needs  & 0xFF on the byte coming inas an int, so! check it
static byte CLAMP(int ll, byte x, int ul) { return (byte)(((x&0xff)<(ll)) ? (ll) : (((x&0xff)>(ul)) ? (ul) : (x&0xff))); }
static double CLAMP(double ll, double x, double ul) { return (((x)<(ll)) ? (ll) : (((x)>(ul)) ? (ul) : (x))); }
//TODO: careful with byte inputs and cast outputs
static int JAS_ROUND(int x){return (((x) < 0.0 ) ? ((int)((x)-0.5)) : ((int)((x)+0.5)));}
static double JAS_ROUND(double x){return (((x) < 0.0 ) ? ((int)((x)-0.5)) : ((int)((x)+0.5)));}

// these are for putting values into the indexed array element!
//byte RED_CHANNEL(byte[] img,int width,int x,int y,int channels){return  img[channels*(y*width+x)+0];}
//byte GREEN_CHANNEL(byte[] img,int width,int x,int y,int channels){return  img[channels*(y*width+x)+1];}
//byte BLUE_CHANNEL(byte[] img,int width,int x,int y,int channels){return   img[channels*(y*width+x)+2];}
//byte ALPHA_CHANNEL(byte[] img,int width,int x,int y,int channels){return   img[channels*(y*width+x)+3];}


// Global tables
static byte[] table59T= new byte[]{3,6,11,16,23,32,41,64};  // 3-bit table for the 59 bit T-mode
static byte[] table58H = new byte[]{3,6,11,16,23,32,41,64};  // 3-bit table for the 58 bit H-mode
static int[][] compressParams = new int[][]{{-8, -2,  2, 8}, {-8, -2,  2, 8}, {-17, -5, 5, 17}, {-17, -5, 5, 17}, {-29, -9, 9, 29}, {-29, -9, 9, 29}, {-42, -13, 13, 42}, {-42, -13, 13, 42}, {-60, -18, 18, 60}, {-60, -18, 18, 60}, {-80, -24, 24, 80}, {-80, -24, 24, 80}, {-106, -33, 33, 106}, {-106, -33, 33, 106}, {-183, -47, 47, 183}, {-183, -47, 47, 183}};
static int[] unscramble= new int[]{2, 3, 1, 0};
static boolean alphaTableInitialized = false;
static int[][] alphaTable=new int[256][8];
static int[][] alphaBase = new int[][]{	
              {-15,-9,-6,-3},
							{-13,-10,-7,-3},
							{-13,-8,-5,-2},
							{-13,-6,-4,-2},
							{-12,-8,-6,-3},
							{-11,-9,-7,-3},
							{-11,-8,-7,-4},
							{-11,-8,-5,-3},
							{ -10,-8,-6,-2},
							{ -10,-8,-5,-2},
							{ -10,-8,-4,-2},
							{ -10,-7,-5,-2},
							{ -10,-7,-4,-3},
							{ -10,-3,-2, -1},
							{ -9,-8,-6,-4},
							{ -9,-7,-5,-3}
											};

// Global variables
static int formatSigned = 0;

//Enums
//static enum PATTERN{PATTERN_H, 	PATTERN_T};


// Code used to create the valtab
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void setupAlphaTable() 
{
    if(alphaTableInitialized)
    	return;
    alphaTableInitialized = true;

	//read table used for alpha compression
	int buf;
	for(int i = 16; i<32; i++) 
	{
		for(int j=0; j<8; j++) 
		{
			buf=alphaBase[i-16][3-j%4];
			if(j<4)
				alphaTable[i][j]=buf;
			else
				alphaTable[i][j]=(-buf-1);
		}
	}
	
	//beyond the first 16 values, the rest of the table is implicit.. so calculate that!
	for(int i=0; i<256; i++) 
	{
		//fill remaining slots in table with multiples of the first ones.
		int mul = i/16;
		int old = 16+i%16;
		for(int j = 0; j<8; j++) 
		{
			alphaTable[i][j]=alphaTable[old][j]*mul;
			//note: we don't do clamping here, though we could, because we'll be clamped afterwards anyway.
		}
	}
}

// Read a word in big endian style
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//unsigned so notice int not short
public static void read_big_endian_2byte_word(int[] blockadr, FileChannel f)
{
	byte[] bytes = new byte[2];
	int block;

	fread(bytes, f);

	block = 0;
	block = block | (bytes[0]&0xff);
	block = block << 8;
	block = block | (bytes[1]&0xff);

	blockadr[0] = block;
}

//NOTE!!!! the i value here is an offset into the dest array, NOT the number of bytes per item like the cpp function
public static void fread(byte[] c, FileChannel f) {
	try {
		f.read(ByteBuffer.wrap(c));
	} catch (IOException e) {
		e.printStackTrace();
	}
}

//Read a word in big endian style
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
public static void read_big_endian_4byte_word(int[] blockadr, FileChannel f)
{
	byte[] bytes = new byte[4];
	int block;

	fread(bytes, f);

	block = 0;
	block = block | (bytes[0]&0xff);
	block = block << 8;
	block = block | (bytes[1]&0xff);
	block = block << 8;
	block = block | (bytes[2]&0xff);
	block = block << 8;
	block = block | (bytes[3]&0xff);

	blockadr[0] = block;
}


//Read a word in big endian style
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//unsigned so notice int not short
public static void read_big_endian_2byte_word(int[] blockadr, ByteBuffer f)
{
	byte[] bytes = new byte[2];
	int block;

	fread(bytes, f);

	block = 0;
	block = (block | (bytes[0]&0xff));
	block = (block << 8);
	block = (block | (bytes[1]&0xff));

	blockadr[0] = block;
}


public static void fread(byte[] c, ByteBuffer f) {
	f.get(c);	
}


//Read a word in big endian style
//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
public static void read_big_endian_4byte_word(int[] blockadr, ByteBuffer f)
{
	byte[] bytes = new byte[4];
	int block;

	fread(bytes, f);

	block = 0;
	block = block | (bytes[0]&0xff);
	block = block << 8;
	block = block | (bytes[1]&0xff);
	block = block << 8;
	block = block | (bytes[2]&0xff);
	block = block << 8;
	block = block | (bytes[3]&0xff);

	blockadr[0] = block;
}


public static void fread(ETCPack.KTX_header header, ByteBuffer bb) {
	bb.get(KTX_header.KTX_IDENTIFIER_REF);//12
	// now 13*4 bytes => 12+(13*4) = 64bytes
	header.endianness = bb.getInt();
	header.glType = bb.getInt();
	header.glTypeSize = bb.getInt();
	header.glFormat = bb.getInt();
	header.glInternalFormat = bb.getInt();
	header.glBaseInternalFormat = bb.getInt();
	header.pixelWidth = bb.getInt();
	header.pixelHeight = bb.getInt();
	header.pixelDepth = bb.getInt();
	header.numberOfArrayElements = bb.getInt();
	header.numberOfFaces = bb.getInt();
	header.numberOfMipmapLevels = bb.getInt();
	header.bytesOfKeyValueData = bb.getInt();
}

// The format stores the bits for the three extra modes in a roundabout way to be able to
// fit them without increasing the bit rate. This function converts them into something
// that is easier to work with. 
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &planar57_word1, int &planar57_word2
static void unstuff57bits(int planar_word1, int planar_word2, int[] planar57_word1, int[] planar57_word2)
{
	// Get bits from twotimer configuration for 57 bits
	// 
	// Go to this bit layout:
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
	//  From this:
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

	RO  = (byte)GETBITSHIGH( planar_word1, 6, 62);
	GO1 = (byte)GETBITSHIGH( planar_word1, 1, 56);
	GO2 = (byte)GETBITSHIGH( planar_word1, 6, 54);
	BO1 = (byte)GETBITSHIGH( planar_word1, 1, 48);
	BO2 = (byte)GETBITSHIGH( planar_word1, 2, 44);
	BO3 = (byte)GETBITSHIGH( planar_word1, 3, 41);
	RH1 = (byte)GETBITSHIGH( planar_word1, 5, 38);
	RH2 = (byte)GETBITSHIGH( planar_word1, 1, 32);
	GH  = (byte)GETBITS(     planar_word2, 7, 31);
	BH  = (byte)GETBITS(     planar_word2, 6, 24);
	RV  = (byte)GETBITS(     planar_word2, 6, 18);
	GV  = (byte)GETBITS(     planar_word2, 7, 12);
	BV  = (byte)GETBITS(     planar_word2, 6,  5);

	planar57_word1[0] = 0; planar57_word2[0] = 0;
	PUTBITSHIGH( planar57_word1, RO,  6, 63);
	PUTBITSHIGH( planar57_word1, GO1, 1, 57);
	PUTBITSHIGH( planar57_word1, GO2, 6, 56);
	PUTBITSHIGH( planar57_word1, BO1, 1, 50);
	PUTBITSHIGH( planar57_word1, BO2, 2, 49);
	PUTBITSHIGH( planar57_word1, BO3, 3, 47);
	PUTBITSHIGH( planar57_word1, RH1, 5, 44);
	PUTBITSHIGH( planar57_word1, RH2, 1, 39);
	PUTBITSHIGH( planar57_word1, GH, 7, 38);
	PUTBITS(     planar57_word2, BH, 6, 31);
	PUTBITS(     planar57_word2, RV, 6, 25);
	PUTBITS(     planar57_word2, GV, 7, 19);
	PUTBITS(     planar57_word2, BV, 6, 12);
}

// The format stores the bits for the three extra modes in a roundabout way to be able to
// fit them without increasing the bit rate. This function converts them into something
// that is easier to work with. 
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &thumbH58_word1, int &thumbH58_word2
static void unstuff58bits(int thumbH_word1, int thumbH_word2, int[] thumbH58_word1, int[] thumbH58_word2)
{
	// Go to this layout:
	//
	//     |63 62 61 60 59 58|57 56 55 54 53 52 51|50 49|48 47 46 45 44 43 42 41 40 39 38 37 36 35 34 33|32   |
	//     |-------empty-----|part0---------------|part1|part2------------------------------------------|part3|
	//
	//  from this:
	// 
	//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34 33 32 
	//      --------------------------------------------------------------------------------------------------|
	//     |//|part0               |// // //|part1|//|part2                                          |df|part3|
	//      --------------------------------------------------------------------------------------------------|

	int part0, part1, part2, part3;

	// move parts
	part0 = GETBITSHIGH( thumbH_word1, 7, 62);
	part1 = GETBITSHIGH( thumbH_word1, 2, 52);
	part2 = GETBITSHIGH( thumbH_word1,16, 49);
	part3 = GETBITSHIGH( thumbH_word1, 1, 32);
	thumbH58_word1[0] = 0;
	PUTBITSHIGH( thumbH58_word1, part0,  7, 57);
	PUTBITSHIGH( thumbH58_word1, part1,  2, 50);
	PUTBITSHIGH( thumbH58_word1, part2, 16, 48);
	PUTBITSHIGH( thumbH58_word1, part3,  1, 32);

	thumbH58_word2[0] = thumbH_word2;
}

// The format stores the bits for the three extra modes in a roundabout way to be able to
// fit them without increasing the bit rate. This function converts them into something
// that is easier to work with. 
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//address int &thumbT59_word1, int &thumbT59_word2
static void unstuff59bits(int thumbT_word1, int thumbT_word2, int[] thumbT59_word1, int[] thumbT59_word2)
{
	// Get bits from twotimer configuration 59 bits. 
	// 
	// Go to this bit layout:
	//
	//     |63 62 61 60 59|58 57 56 55|54 53 52 51|50 49 48 47|46 45 44 43|42 41 40 39|38 37 36 35|34 33 32|
	//     |----empty-----|---red 0---|--green 0--|--blue 0---|---red 1---|--green 1--|--blue 1---|--dist--|
	//
	//     |31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
	//     |----------------------------------------index bits---------------------------------------------|
	//
	//
	//  From this:
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

	// Fix middle part
	thumbT59_word1[0] = thumbT_word1 >> 1;
	// Fix db (lowest bit of d)
	PUTBITSHIGH( thumbT59_word1, thumbT_word1,  1, 32);
	// Fix R0a (top two bits of R0)
	R0a = (byte)GETBITSHIGH( thumbT_word1, 2, 60);
	PUTBITSHIGH( thumbT59_word1, R0a,  2, 58);

	// Zero top part (not needed)
	PUTBITSHIGH( thumbT59_word1, 0,  5, 63);

	thumbT59_word2[0] = thumbT_word2;
}

// The color bits are expanded to the full color
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte (colors_RGB444)[2][3], byte (colors)[2][3]
static void decompressColor(int R_B, int G_B, int B_B, byte[][] colors_RGB444, byte[][] colors) 
{
	// The color should be retrieved as:
	//
	// c = round(255/(r_bits^2-1))*comp_color
	//
	// This is similar to bit replication
	// 
	// Note -- this code only work for bit replication from 4 bits and up --- 3 bits needs
	// two copy operations.

 	colors[0][R] = (byte)((colors_RGB444[0][R] << (8 - R_B)) | (colors_RGB444[0][R] >> (R_B - (8-R_B)) ));
 	colors[0][G] = (byte)((colors_RGB444[0][G] << (8 - G_B)) | (colors_RGB444[0][G] >> (G_B - (8-G_B)) ));
 	colors[0][B] = (byte)((colors_RGB444[0][B] << (8 - B_B)) | (colors_RGB444[0][B] >> (B_B - (8-B_B)) ));
 	colors[1][R] = (byte)((colors_RGB444[1][R] << (8 - R_B)) | (colors_RGB444[1][R] >> (R_B - (8-R_B)) ));
 	colors[1][G] = (byte)((colors_RGB444[1][G] << (8 - G_B)) | (colors_RGB444[1][G] >> (G_B - (8-G_B)) ));
 	colors[1][B] = (byte)((colors_RGB444[1][B] << (8 - B_B)) | (colors_RGB444[1][B] >> (B_B - (8-B_B)) ));
}

//byte (colors)[2][3], byte (possible_colors)[4][3]
static void calculatePaintColors59T(byte d, PATTERN p, byte[][] colors, byte[][] possible_colors) 
{
	//////////////////////////////////////////////
	//
	//		C3      C1		C4----C1---C2
	//		|		|			  |
	//		|		|			  |
	//		|-------|			  |
	//		|		|			  |
	//		|		|			  |
	//		C4      C2			  C3
	//
	//////////////////////////////////////////////

	// C4
	possible_colors[3][R] = (byte)CLAMP(0,(colors[1][R]&0xff) - (table59T[d]&0xff),255);
	possible_colors[3][G] = (byte)CLAMP(0,(colors[1][G]&0xff) - (table59T[d]&0xff),255);
	possible_colors[3][B] = (byte)CLAMP(0,(colors[1][B]&0xff) - (table59T[d]&0xff),255);
	
	if (p == PATTERN.PATTERN_T) 
	{
		// C3
		possible_colors[0][R] = colors[0][R];
		possible_colors[0][G] = colors[0][G];
		possible_colors[0][B] = colors[0][B];
		// C2
		possible_colors[1][R] = (byte)CLAMP(0,(colors[1][R]&0xff) + (table59T[d]&0xff),255);
		possible_colors[1][G] = (byte)CLAMP(0,(colors[1][G]&0xff) + (table59T[d]&0xff),255);
		possible_colors[1][B] = (byte)CLAMP(0,(colors[1][B]&0xff) + (table59T[d]&0xff),255);
		// C1
		possible_colors[2][R] = colors[1][R];
		possible_colors[2][G] = colors[1][G];
		possible_colors[2][B] = colors[1][B];

	} 
	else 
	{
		System.err.println("Invalid pattern. Terminating");
		System.exit(1);
	}
}
// Decompress a T-mode block (simple packing)
// Simple 59T packing:
//|63 62 61 60 59|58 57 56 55|54 53 52 51|50 49 48 47|46 45 44 43|42 41 40 39|38 37 36 35|34 33 32|
//|----empty-----|---red 0---|--green 0--|--blue 0---|---red 1---|--green 1--|--blue 1---|--dist--|
//
//|31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00|
//|----------------------------------------index bits---------------------------------------------|
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void decompressBlockTHUMB59Tc(int block_part1, int block_part2, byte[] img,int width,int height,int startx,int starty, int channels)
{
	boolean singleBlockDest = img.length == 4 * 4 * 3; // special indicator for a small decompression target
	
	byte[][] colorsRGB444 = new byte[2][3];
	byte[][] colors = new byte[2][3];
	byte[][] paint_colors = new byte[4][3];
	byte distance;
	byte[][] block_mask = new byte[4][4];

	// First decode left part of block.
	colorsRGB444[0][R]= (byte)GETBITSHIGH(block_part1, 4, 58);
	colorsRGB444[0][G]= (byte)GETBITSHIGH(block_part1, 4, 54);
	colorsRGB444[0][B]= (byte)GETBITSHIGH(block_part1, 4, 50);

	colorsRGB444[1][R]= (byte)GETBITSHIGH(block_part1, 4, 46);
	colorsRGB444[1][G]= (byte)GETBITSHIGH(block_part1, 4, 42);
	colorsRGB444[1][B]= (byte)GETBITSHIGH(block_part1, 4, 38);

	distance   = (byte)GETBITSHIGH(block_part1, TABLE_BITS_59T, 34);

	// Extend the two colors to RGB888	
	decompressColor(R_BITS59T, G_BITS59T, B_BITS59T, colorsRGB444, colors);	
	calculatePaintColors59T(distance, PATTERN.PATTERN_T, colors, paint_colors);
	
	// Choose one of the four paint colors for each texel
	for (byte x = 0; x < BLOCKWIDTH; ++x) 
	{
		for (byte y = 0; y < BLOCKHEIGHT; ++y) 
		{
			//block_mask[x][y] = GETBITS(block_part2,2,31-(y*4+x)*2);
			block_mask[x][y] = (byte)(GETBITS(block_part2,1,(y+x*4)+16)<<1);
			block_mask[x][y] = (byte)(block_mask[x][y] | GETBITS(block_part2,1,(y+x*4)));
			
			int idx = singleBlockDest ? channels*((y)*BLOCKWIDTH+x): channels*((starty+y)*width+startx+x);			
			img[idx+R] =
				CLAMP(0,paint_colors[block_mask[x][y]][R],255); // RED
			img[idx+G] =
				CLAMP(0,paint_colors[block_mask[x][y]][G],255); // GREEN
			img[idx+B] =
				CLAMP(0,paint_colors[block_mask[x][y]][B],255); // BLUE
		}
	}
}

static void decompressBlockTHUMB59T(int block_part1, int block_part2, byte[] img, int width, int height, int startx, int starty)
{
  decompressBlockTHUMB59Tc(block_part1, block_part2, img, width, height, startx, starty, 3);
}

// Calculate the paint colors from the block colors 
// using a distance d and one of the H- or T-patterns.
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
//byte (colors)[2][3], byte (possible_colors)[4][3]
static void calculatePaintColors58H(byte d, PATTERN p, byte[][] colors, byte[][] possible_colors) 
{
	
	//////////////////////////////////////////////
	//
	//		C3      C1		C4----C1---C2
	//		|		|			  |
	//		|		|			  |
	//		|-------|			  |
	//		|		|			  |
	//		|		|			  |
	//		C4      C2			  C3
	//
	//////////////////////////////////////////////

	// C4
	possible_colors[3][R] = (byte)CLAMP(0,(colors[1][R]&0xff) - (table58H[d]&0xff),255);
	possible_colors[3][G] = (byte)CLAMP(0,(colors[1][G]&0xff) - (table58H[d]&0xff),255);
	possible_colors[3][B] = (byte)CLAMP(0,(colors[1][B]&0xff) - (table58H[d]&0xff),255);
	
	if (p == PATTERN.PATTERN_H) 
	{ 
		// C1
		possible_colors[0][R] = (byte)CLAMP(0,(colors[0][R]&0xff) + (table58H[d]&0xff),255);
		possible_colors[0][G] = (byte)CLAMP(0,(colors[0][G]&0xff) + (table58H[d]&0xff),255);
		possible_colors[0][B] = (byte)CLAMP(0,(colors[0][B]&0xff) + (table58H[d]&0xff),255);
		// C2
		possible_colors[1][R] = (byte)CLAMP(0,(colors[0][R]&0xff) - (table58H[d]&0xff),255);
		possible_colors[1][G] = (byte)CLAMP(0,(colors[0][G]&0xff) - (table58H[d]&0xff),255);
		possible_colors[1][B] = (byte)CLAMP(0,(colors[0][B]&0xff) - (table58H[d]&0xff),255);
		// C3
		possible_colors[2][R] = (byte)CLAMP(0,(colors[1][R]&0xff) + (table58H[d]&0xff),255);
		possible_colors[2][G] = (byte)CLAMP(0,(colors[1][G]&0xff) + (table58H[d]&0xff),255);
		possible_colors[2][B] = (byte)CLAMP(0,(colors[1][B]&0xff) + (table58H[d]&0xff),255);
	} 
	else 
	{
		System.out.println("Invalid pattern. Terminating");
		System.exit(1);
	}
}

// Decompress an H-mode block 
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void decompressBlockTHUMB58Hc(int block_part1, int block_part2, byte[] img, int width, int height, int startx, int starty, int channels)
{
	boolean singleBlockDest = img.length == 4 * 4 * 3; // special indicator for a small decompression target
	
	int col0, col1;
	byte[][] colors = new byte[2][3];
	byte[][] colorsRGB444 = new byte[2][3];
	byte[][] paint_colors = new byte[4][3];
	byte distance;
	byte[][] block_mask = new byte[4][4];
	
	// First decode left part of block.
	colorsRGB444[0][R]= (byte)GETBITSHIGH(block_part1, 4, 57);
	colorsRGB444[0][G]= (byte)GETBITSHIGH(block_part1, 4, 53);
	colorsRGB444[0][B]= (byte)GETBITSHIGH(block_part1, 4, 49);

	colorsRGB444[1][R]= (byte)GETBITSHIGH(block_part1, 4, 45);
	colorsRGB444[1][G]= (byte)GETBITSHIGH(block_part1, 4, 41);
	colorsRGB444[1][B]= (byte)GETBITSHIGH(block_part1, 4, 37);

	distance = 0;
	distance = (byte)((GETBITSHIGH(block_part1, 2, 33)) << 1);

	col0 = GETBITSHIGH(block_part1, 12, 57);
	col1 = GETBITSHIGH(block_part1, 12, 45);

	if(col0 >= col1)
	{
		distance = (byte)(distance | 1);
	}

	// Extend the two colors to RGB888	
	decompressColor(R_BITS58H, G_BITS58H, B_BITS58H, colorsRGB444, colors);	
	
	calculatePaintColors58H(distance, PATTERN.PATTERN_H, colors, paint_colors);
	
	// Choose one of the four paint colors for each texel
	for (byte x = 0; x < BLOCKWIDTH; ++x) 
	{
		for (byte y = 0; y < BLOCKHEIGHT; ++y) 
		{
			//block_mask[x][y] = GETBITS(block_part2,2,31-(y*4+x)*2);
			block_mask[x][y] = (byte)(GETBITS(block_part2,1,(y+x*4)+16)<<1);
			block_mask[x][y] = (byte)(block_mask[x][y] | GETBITS(block_part2,1,(y+x*4)));
			
			int idx = singleBlockDest ? channels*((y)*BLOCKWIDTH+x): channels*((starty+y)*width+startx+x);
			img[idx+R] =
				CLAMP(0,paint_colors[block_mask[x][y]][R],255); // RED
			img[idx+G] =
				CLAMP(0,paint_colors[block_mask[x][y]][G],255); // GREEN
			img[idx+B] =
				CLAMP(0,paint_colors[block_mask[x][y]][B],255); // BLUE
		}
	}
}
static void decompressBlockTHUMB58H(int block_part1, int block_part2, byte[] img, int width, int height, int startx, int starty)
{
  decompressBlockTHUMB58Hc(block_part1, block_part2, img, width, height, startx, starty, 3);
}

// Decompress the planar mode.
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void decompressBlockPlanar57c(int compressed57_1, int compressed57_2, byte[] img, int width, int height, int startx, int starty, int channels)
{
	boolean singleBlockDest = img.length == 4 * 4 * 3; // special indicator for a small decompression target
	
	byte[] colorO = new byte[3], colorH = new byte[3], colorV = new byte[3];

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
			int idx = singleBlockDest ? channels*BLOCKWIDTH*(yy) + channels*(xx) : channels*width*(starty+yy) + channels*(startx+xx);
			img[idx + 0] = (byte)CLAMP(0, ((xx*((colorH[0]&0xff)-(colorO[0]&0xff)) + yy*((colorV[0]&0xff)-(colorO[0]&0xff)) + 4*(colorO[0]&0xff) + 2) >> 2),255);
			img[idx + 1] = (byte)CLAMP(0, ((xx*((colorH[1]&0xff)-(colorO[1]&0xff)) + yy*((colorV[1]&0xff)-(colorO[1]&0xff)) + 4*(colorO[1]&0xff) + 2) >> 2),255);
			img[idx + 2] = (byte)CLAMP(0, ((xx*((colorH[2]&0xff)-(colorO[2]&0xff)) + yy*((colorV[2]&0xff)-(colorO[2]&0xff)) + 4*(colorO[2]&0xff) + 2) >> 2),255);

			//Equivalent method
			/*img[channels*width*(starty+yy) + channels*(startx+xx) + 0] = (int)CLAMP(0, JAS_ROUND((xx*(colorH[0]-colorO[0])/4.0 + yy*(colorV[0]-colorO[0])/4.0 + colorO[0])), 255);
			img[channels*width*(starty+yy) + channels*(startx+xx) + 1] = (int)CLAMP(0, JAS_ROUND((xx*(colorH[1]-colorO[1])/4.0 + yy*(colorV[1]-colorO[1])/4.0 + colorO[1])), 255);
			img[channels*width*(starty+yy) + channels*(startx+xx) + 2] = (int)CLAMP(0, JAS_ROUND((xx*(colorH[2]-colorO[2])/4.0 + yy*(colorV[2]-colorO[2])/4.0 + colorO[2])), 255);*/
			
		}
	}
}
static void decompressBlockPlanar57(int compressed57_1, int compressed57_2, byte[] img, int width, int height, int startx, int starty)
{
  decompressBlockPlanar57c(compressed57_1, compressed57_2, img, width, height, startx, starty, 3);
}
// Decompress an ETC1 block (or ETC2 using individual or differential mode).
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void decompressBlockDiffFlipC(int block_part1, int block_part2, byte[] img, int width, int height, int startx, int starty, int channels)
{
	boolean singleBlockDest = img.length == 4 * 4 * 3; // special indicator for a small decompression target
	
	byte[] avg_color = new byte[3], enc_color1 = new byte[3], enc_color2 = new byte[3];
	byte[] diff = new byte[3];
	int table;
	int index,shift;
	//int r,g,b;
	int diffbit;
	int flipbit;

	diffbit = (GETBITSHIGH(block_part1, 1, 33));
	flipbit = (GETBITSHIGH(block_part1, 1, 32));
	
	if( diffbit == 0)
	{
		// We have diffbit = 0.

		// First decode left part of block.
		avg_color[0]= (byte)GETBITSHIGH(block_part1, 4, 63);
		avg_color[1]= (byte)GETBITSHIGH(block_part1, 4, 55);
		avg_color[2]= (byte)GETBITSHIGH(block_part1, 4, 47);

		// Here, we should really multiply by 17 instead of 16. This can
		// be done by just copying the four lower bits to the upper ones
		// while keeping the lower bits.
		avg_color[0] = (byte)(avg_color[0] | (avg_color[0] <<4));
		avg_color[1] = (byte)(avg_color[1] | (avg_color[1] <<4));
		avg_color[2] = (byte)(avg_color[2] | (avg_color[2] <<4));

		table = GETBITSHIGH(block_part1, 3, 39) << 1;

		int pixel_indices_MSB, pixel_indices_LSB;
			
		pixel_indices_MSB = GETBITS(block_part2, 16, 31);
		pixel_indices_LSB = GETBITS(block_part2, 16, 15);

		if( (flipbit) == 0 )
		{
			// We should not flip
			shift = 0;
			for(int x=startx; x<startx+2; x++)
			{
				for(int y=starty; y<starty+4; y++)
				{
					index  = ((pixel_indices_MSB >> shift) & 1) << 1;
					index |= ((pixel_indices_LSB >> shift) & 1);
					shift++;
					index=unscramble[index];
										
					int idx = singleBlockDest ? (y - starty) * 4 + (x - startx) : (y * width + x);
					img[channels*idx+0]  =(byte)CLAMP(0,(avg_color[0]&0xff)+compressParams[table][index],255);
					img[channels*idx+1]  =(byte)CLAMP(0,(avg_color[1]&0xff)+compressParams[table][index],255);
					img[channels*idx+2]  =(byte)CLAMP(0,(avg_color[2]&0xff)+compressParams[table][index],255);
				}
			}
		}
		else
		{
			// We should flip
			shift = 0;
			for(int x=startx; x<startx+4; x++)
			{
				for(int y=starty; y<starty+2; y++)
				{
					index  = ((pixel_indices_MSB >> shift) & 1) << 1;
					index |= ((pixel_indices_LSB >> shift) & 1);
					shift++;
					index=unscramble[index];

					int idx = singleBlockDest ? (y - starty) * 4 + (x - startx) : (y * width + x);
					img[channels*idx+0]  =(byte)CLAMP(0,(avg_color[0]&0xff)+compressParams[table][index],255);
					img[channels*idx+1]  =(byte)CLAMP(0,(avg_color[1]&0xff)+compressParams[table][index],255);
					img[channels*idx+2]  =(byte)CLAMP(0,(avg_color[2]&0xff)+compressParams[table][index],255);
				}
				shift+=2;
			}
		}

		// Now decode other part of block. 
		avg_color[0]= (byte)GETBITSHIGH(block_part1, 4, 59);
		avg_color[1]= (byte)GETBITSHIGH(block_part1, 4, 51);
		avg_color[2]= (byte)GETBITSHIGH(block_part1, 4, 43);

		// Here, we should really multiply by 17 instead of 16. This can
		// be done by just copying the four lower bits to the upper ones
		// while keeping the lower bits.
		avg_color[0] = (byte)(avg_color[0] | (avg_color[0] <<4));
		avg_color[1] = (byte)(avg_color[1] | (avg_color[1] <<4));
		avg_color[2] = (byte)(avg_color[2] | (avg_color[2] <<4));

		table = GETBITSHIGH(block_part1, 3, 36) << 1;
		pixel_indices_MSB = GETBITS(block_part2, 16, 31);
		pixel_indices_LSB = GETBITS(block_part2, 16, 15);

		if( (flipbit) == 0 )
		{
			// We should not flip
			shift=8;
			for(int x=startx+2; x<startx+4; x++)
			{
				for(int y=starty; y<starty+4; y++)
				{
					index  = ((pixel_indices_MSB >> shift) & 1) << 1;
					index |= ((pixel_indices_LSB >> shift) & 1);
					shift++;
					index=unscramble[index];

					int idx = singleBlockDest ? (y - starty) * 4 + (x - startx) : (y * width + x);
					img[channels*idx+0]  =(byte)CLAMP(0,(avg_color[0]&0xff)+compressParams[table][index],255);
					img[channels*idx+1]  =(byte)CLAMP(0,(avg_color[1]&0xff)+compressParams[table][index],255);
					img[channels*idx+2]  =(byte)CLAMP(0,(avg_color[2]&0xff)+compressParams[table][index],255);
				}
			}
		}
		else
		{
			// We should flip
			shift=2;
			for(int x=startx; x<startx+4; x++)
			{
				for(int y=starty+2; y<starty+4; y++)
				{
					index  = ((pixel_indices_MSB >> shift) & 1) << 1;
					index |= ((pixel_indices_LSB >> shift) & 1);
					shift++;
					index=unscramble[index];

					int idx = singleBlockDest ? (y - starty) * 4 + (x - startx) : (y * width + x);
					img[channels*idx+0]  =(byte)CLAMP(0,(avg_color[0]&0xff)+compressParams[table][index],255);
					img[channels*idx+1]  =(byte)CLAMP(0,(avg_color[1]&0xff)+compressParams[table][index],255);
					img[channels*idx+2]  =(byte)CLAMP(0,(avg_color[2]&0xff)+compressParams[table][index],255);
				}
				shift += 2;
			}
		}
	}
	else
	{
		// We have diffbit = 1. 

//      63 62 61 60 59 58 57 56 55 54 53 52 51 50 49 48 47 46 45 44 43 42 41 40 39 38 37 36 35 34  33  32 
//      ---------------------------------------------------------------------------------------------------
//     | base col1    | dcol 2 | base col1    | dcol 2 | base col 1   | dcol 2 | table  | table  |diff|flip|
//     | R1' (5 bits) | dR2    | G1' (5 bits) | dG2    | B1' (5 bits) | dB2    | cw 1   | cw 2   |bit |bit |
//      ---------------------------------------------------------------------------------------------------
// 
// 
//     c) bit layout in bits 31 through 0 (in both cases)
// 
//      31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3   2   1  0
//      --------------------------------------------------------------------------------------------------
//     |       most significant pixel index bits       |         least significant pixel index bits       |  
//     | p| o| n| m| l| k| j| i| h| g| f| e| d| c| b| a| p| o| n| m| l| k| j| i| h| g| f| e| d| c | b | a |
//      --------------------------------------------------------------------------------------------------      

		// First decode left part of block.
		enc_color1[0]= (byte)GETBITSHIGH(block_part1, 5, 63);
		enc_color1[1]= (byte)GETBITSHIGH(block_part1, 5, 55);
		enc_color1[2]= (byte)GETBITSHIGH(block_part1, 5, 47);

		// Expand from 5 to 8 bits
		avg_color[0] = (byte)((enc_color1[0] <<3) | (enc_color1[0] >> 2));
		avg_color[1] = (byte)((enc_color1[1] <<3) | (enc_color1[1] >> 2));
		avg_color[2] = (byte)((enc_color1[2] <<3) | (enc_color1[2] >> 2));

		table = GETBITSHIGH(block_part1, 3, 39) << 1;

		int pixel_indices_MSB, pixel_indices_LSB;
			
		pixel_indices_MSB = GETBITS(block_part2, 16, 31);
		pixel_indices_LSB = GETBITS(block_part2, 16, 15);

		if( (flipbit) == 0 )
		{
			// We should not flip
			shift = 0;
			for(int x=startx; x<startx+2; x++)
			{
				for(int y=starty; y<starty+4; y++)
				{
					index  = ((pixel_indices_MSB >> shift) & 1) << 1;
					index |= ((pixel_indices_LSB >> shift) & 1);
					shift++;
					index=unscramble[index];

					int idx = singleBlockDest ? (y - starty) * 4 + (x - startx) : (y * width + x);
					img[channels*idx+0]  =(byte)CLAMP(0,(avg_color[0]&0xff)+compressParams[table][index],255);
					img[channels*idx+1]  =(byte)CLAMP(0,(avg_color[1]&0xff)+compressParams[table][index],255);
					img[channels*idx+2]  =(byte)CLAMP(0,(avg_color[2]&0xff)+compressParams[table][index],255);
				}
			}
		}
		else
		{
			// We should flip
			shift = 0;
			for(int x=startx; x<startx+4; x++)
			{
				for(int y=starty; y<starty+2; y++)
				{
					index  = ((pixel_indices_MSB >> shift) & 1) << 1;
					index |= ((pixel_indices_LSB >> shift) & 1);
					shift++;
					index=unscramble[index];

					int idx = singleBlockDest ? (y - starty) * 4 + (x - startx) : (y * width + x);
					img[channels*idx+0]  =(byte)CLAMP(0,(avg_color[0]&0xff)+compressParams[table][index],255);
					img[channels*idx+1]  =(byte)CLAMP(0,(avg_color[1]&0xff)+compressParams[table][index],255);
					img[channels*idx+2]  =(byte)CLAMP(0,(avg_color[2]&0xff)+compressParams[table][index],255);
				}
				shift+=2;
			}
		}

		// Now decode right part of block. 
		diff[0]= (byte)GETBITSHIGH(block_part1, 3, 58);
		diff[1]= (byte)GETBITSHIGH(block_part1, 3, 50);
		diff[2]= (byte)GETBITSHIGH(block_part1, 3, 42);

		// Extend sign bit to entire byte. 
		diff[0] = (byte)(diff[0] << 5);
		diff[1] = (byte)(diff[1] << 5);
		diff[2] = (byte)(diff[2] << 5);
		diff[0] = (byte)(diff[0] >> 5);
		diff[1] = (byte)(diff[1] >> 5);
		diff[2] = (byte)(diff[2] >> 5);

		//  Calculale second color
		enc_color2[0]= (byte)((enc_color1[0]&0xff) + (diff[0]&0xff));
		enc_color2[1]= (byte)((enc_color1[1]&0xff) + (diff[1]&0xff));
		enc_color2[2]= (byte)((enc_color1[2]&0xff) + (diff[2]&0xff));

		// Expand from 5 to 8 bits
		avg_color[0] = (byte)((enc_color2[0] <<3) | (enc_color2[0] >> 2));
		avg_color[1] = (byte)((enc_color2[1] <<3) | (enc_color2[1] >> 2));
		avg_color[2] = (byte)((enc_color2[2] <<3) | (enc_color2[2] >> 2));

		table = GETBITSHIGH(block_part1, 3, 36) << 1;
		pixel_indices_MSB = GETBITS(block_part2, 16, 31);
		pixel_indices_LSB = GETBITS(block_part2, 16, 15);

		if( (flipbit) == 0 )
		{
			// We should not flip
			shift=8;
			for(int x=startx+2; x<startx+4; x++)
			{
				for(int y=starty; y<starty+4; y++)
				{
					index  = ((pixel_indices_MSB >> shift) & 1) << 1;
					index |= ((pixel_indices_LSB >> shift) & 1);
					shift++;
					index=unscramble[index];

					int idx = singleBlockDest ? (y - starty) * 4 + (x - startx) : (y * width + x);
					img[channels*idx+0]  =(byte)CLAMP(0,(avg_color[0]&0xff)+compressParams[table][index],255);
					img[channels*idx+1]  =(byte)CLAMP(0,(avg_color[1]&0xff)+compressParams[table][index],255);
					img[channels*idx+2]  =(byte)CLAMP(0,(avg_color[2]&0xff)+compressParams[table][index],255);
				}
			}
		}
		else
		{
			// We should flip
			shift=2;
			for(int x=startx; x<startx+4; x++)
			{
				for(int y=starty+2; y<starty+4; y++)
				{
					index  = ((pixel_indices_MSB >> shift) & 1) << 1;
					index |= ((pixel_indices_LSB >> shift) & 1);
					shift++;
					index=unscramble[index];

					int idx = singleBlockDest ? (y - starty) * 4 + (x - startx) : (y * width + x);
					img[channels*idx+0]  =(byte)CLAMP(0,(avg_color[0]&0xff)+compressParams[table][index],255);
					img[channels*idx+1]  =(byte)CLAMP(0,(avg_color[1]&0xff)+compressParams[table][index],255);
					img[channels*idx+2]  =(byte)CLAMP(0,(avg_color[2]&0xff)+compressParams[table][index],255);
				}
				shift += 2;
			}
		}
	}
		
}
static void decompressBlockDiffFlip(int block_part1, int block_part2, byte[] img, int width, int height, int startx, int starty)
{
  decompressBlockDiffFlipC(block_part1, block_part2, img, width, height, startx, starty, 3);
}

// Decompress an ETC2 RGB block
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void decompressBlockETC2c(int block_part1, int block_part2, byte[] img, int width, int height, int startx, int starty, int channels)
{
	int diffbit;
	byte[]  color1 = new byte[3];
	byte[]  diff = new byte[3];
	byte red, green, blue;

	diffbit = (GETBITSHIGH(block_part1, 1, 33));

	if( diffbit != 0)
	{
		// We have diffbit = 1;

		// Base color
		color1[0]= (byte)GETBITSHIGH(block_part1, 5, 63);
		color1[1]= (byte)GETBITSHIGH(block_part1, 5, 55);
		color1[2]= (byte)GETBITSHIGH(block_part1, 5, 47);

		// Diff color
		diff[0]= (byte)GETBITSHIGH(block_part1, 3, 58);
		diff[1]= (byte)GETBITSHIGH(block_part1, 3, 50);
		diff[2]= (byte)GETBITSHIGH(block_part1, 3, 42);

		// Extend sign bit to entire byte. 
		diff[0] = (byte)(diff[0] << 5);
		diff[1] = (byte)(diff[1] << 5);
		diff[2] = (byte)(diff[2] << 5);
		diff[0] = (byte)(diff[0] >> 5);
		diff[1] = (byte)(diff[1] >> 5);
		diff[2] = (byte)(diff[2] >> 5);

		red   = (byte)((color1[0]&0xff) + (diff[0]&0xff));
		green = (byte)((color1[1]&0xff) + (diff[1]&0xff));
		blue  = (byte)((color1[2]&0xff) + (diff[2]&0xff));

		if(red < 0 || red > 31)
		{
			int[] block59_part1=new int[1], block59_part2=new int[1];
			unstuff59bits(block_part1, block_part2, block59_part1, block59_part2);
			decompressBlockTHUMB59Tc(block59_part1[0], block59_part2[0], img, width, height, startx, starty, channels);
		}
		else if (green < 0 || green > 31)
		{
			int[] block58_part1=new int[1], block58_part2=new int[1];
			unstuff58bits(block_part1, block_part2, block58_part1, block58_part2);
			decompressBlockTHUMB58Hc(block58_part1[0], block58_part2[0], img, width, height, startx, starty, channels);
		}
		else if(blue < 0 || blue > 31)
		{
			int[] block57_part1=new int[1], block57_part2=new int[1];
			unstuff57bits(block_part1, block_part2, block57_part1, block57_part2);
			decompressBlockPlanar57c(block57_part1[0], block57_part2[0], img, width, height, startx, starty, channels);
		}
		else
		{
 			decompressBlockDiffFlipC(block_part1, block_part2, img, width, height, startx, starty, channels);
		}
	}
	else
	{
		// We have diffbit = 0;
		decompressBlockDiffFlipC(block_part1, block_part2, img, width, height, startx, starty, channels);
	}
}
static void decompressBlockETC2(int block_part1, int block_part2, byte[] img, int width, int height, int startx, int starty)
{
  decompressBlockETC2c(block_part1, block_part2, img, width, height, startx, starty, 3);
}
// Decompress an ETC2 block with punchthrough alpha
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void decompressBlockDifferentialWithAlphaC(int block_part1, int block_part2, byte[] img, byte[] alpha, int width, int height, int startx, int starty, int channelsRGB)
{
	boolean singleBlockDest = img.length == 4 * 4 * 3; // special indicator for a small decompression target
	byte[] avg_color = new byte[3], enc_color1 = new byte[3], enc_color2 = new byte[3];
	byte[] diff = new byte[3];
	int table;
	int index,shift;
	//int r,g,b;
	int diffbit;
	int flipbit;
    int channelsA;

    if(channelsRGB == 3)
    {
      // We will decode the alpha data to a separate memory area. 
      channelsA = 1;
    }
    else
    {
      // We will decode the RGB data and the alpha data to the same memory area, 
      // interleaved as RGBA. 
      channelsA = 4;
      
      // I don't have any code lines that use 4 so I can't test how the user uses this, so just ... edit the img data!
      // the calls with channelsRGB == 3 seem to set up a w x x h 1 byte array ready for filling
      // all other similar methods only come in with 3 channels!
      //alpha = img[0+3];
    }

	//the diffbit now encodes whether or not the entire alpha channel is 255.
	diffbit = (GETBITSHIGH(block_part1, 1, 33));
 	flipbit = (GETBITSHIGH(block_part1, 1, 32));

	// First decode left part of block.
	enc_color1[0]= (byte)GETBITSHIGH(block_part1, 5, 63);
	enc_color1[1]= (byte)GETBITSHIGH(block_part1, 5, 55);
	enc_color1[2]= (byte)GETBITSHIGH(block_part1, 5, 47);

	// Expand from 5 to 8 bits
	avg_color[0] = (byte)((enc_color1[0] <<3) | (enc_color1[0] >> 2));
	avg_color[1] = (byte)((enc_color1[1] <<3) | (enc_color1[1] >> 2));
	avg_color[2] = (byte)((enc_color1[2] <<3) | (enc_color1[2] >> 2));

	table = GETBITSHIGH(block_part1, 3, 39) << 1;

	int pixel_indices_MSB, pixel_indices_LSB;
		
	pixel_indices_MSB = GETBITS(block_part2, 16, 31);
	pixel_indices_LSB = GETBITS(block_part2, 16, 15);

	if( (flipbit) == 0 )
	{
		
		// We should not flip
		shift = 0;
		for(int x=startx; x<startx+2; x++)
		{
			for(int y=starty; y<starty+4; y++)
			{
				index  = ((pixel_indices_MSB >> shift) & 1) << 1;
				index |= ((pixel_indices_LSB >> shift) & 1);
				shift++;
				index=unscramble[index];

				int mod = compressParams[table][index];
				if(diffbit==0&&(index==1||index==2)) 
				{
					mod=0;
				}
				
				int idx = singleBlockDest ? (y - starty) * 4 + (x - startx) : (y * width + x);				
				img[channelsRGB*idx+0]  =(byte)CLAMP(0,(avg_color[0]&0xff)+mod,255);
				img[channelsRGB*idx+1]  =(byte)CLAMP(0,(avg_color[1]&0xff)+mod,255);
				img[channelsRGB*idx+2]  =(byte)CLAMP(0,(avg_color[2]&0xff)+mod,255);
				if(diffbit==0&&index==1) 
				{					
					alpha[idx*channelsA]=0;					
					img[channelsRGB*idx+0]=0;
					img[channelsRGB*idx+1]=0;
					img[channelsRGB*idx+2]=0;
				}
				else 
				{
					alpha[idx*channelsA]=(byte)255;
				}

			}
		}
	}
	else
	{
		// We should flip
		shift = 0;
		for(int x=startx; x<startx+4; x++)
		{
			for(int y=starty; y<starty+2; y++)
			{
				index  = ((pixel_indices_MSB >> shift) & 1) << 1;
				index |= ((pixel_indices_LSB >> shift) & 1);
				shift++;
				index=unscramble[index];
				int mod = compressParams[table][index];
				if(diffbit==0&&(index==1||index==2)) 
				{
					mod=0;
				}
				
				int idx = singleBlockDest ? (y - starty) * 4 + (x - startx) : (y * width + x);
				img[channelsRGB*idx+0]  =(byte)CLAMP(0,(avg_color[0]&0xff)+mod,255);
				img[channelsRGB*idx+1]  =(byte)CLAMP(0,(avg_color[1]&0xff)+mod,255);
				img[channelsRGB*idx+2]  =(byte)CLAMP(0,(avg_color[2]&0xff)+mod,255);
				if(diffbit==0&&index==1) 
				{
					alpha[idx*channelsA]=0;
					img[channelsRGB*idx+0]=0;
					img[channelsRGB*idx+1]=0;
					img[channelsRGB*idx+2]=0;
				}
				else 
				{
					alpha[idx*channelsA]=(byte)255;
				}
			}
			shift+=2;
		}
	}
	// Now decode right part of block. 
	diff[0]= (byte)GETBITSHIGH(block_part1, 3, 58);
	diff[1]= (byte)GETBITSHIGH(block_part1, 3, 50);
	diff[2]= (byte)GETBITSHIGH(block_part1, 3, 42);

	// Extend sign bit to entire byte. 
	diff[0] = (byte)(diff[0] << 5);
	diff[1] = (byte)(diff[1] << 5);
	diff[2] = (byte)(diff[2] << 5);
	diff[0] = (byte)(diff[0] >> 5);
	diff[1] = (byte)(diff[1] >> 5);
	diff[2] = (byte)(diff[2] >> 5);

	//  Calculate second color
	enc_color2[0]= (byte)((enc_color1[0]&0xff) + (diff[0]&0xff));
	enc_color2[1]= (byte)((enc_color1[1]&0xff) + (diff[1]&0xff));
	enc_color2[2]= (byte)((enc_color1[2]&0xff) + (diff[2]&0xff));

	// Expand from 5 to 8 bits
	avg_color[0] = (byte)((enc_color2[0] <<3) | (enc_color2[0] >> 2));
	avg_color[1] = (byte)((enc_color2[1] <<3) | (enc_color2[1] >> 2));
	avg_color[2] = (byte)((enc_color2[2] <<3) | (enc_color2[2] >> 2));

	table = GETBITSHIGH(block_part1, 3, 36) << 1;
	pixel_indices_MSB = GETBITS(block_part2, 16, 31);
	pixel_indices_LSB = GETBITS(block_part2, 16, 15);

	if( (flipbit) == 0 )
	{
		// We should not flip
		shift=8;
		for(int x=startx+2; x<startx+4; x++)
		{
			for(int y=starty; y<starty+4; y++)
			{
				index  = ((pixel_indices_MSB >> shift) & 1) << 1;
				index |= ((pixel_indices_LSB >> shift) & 1);
				shift++;
				index=unscramble[index];
				int mod = compressParams[table][index];
				if(diffbit==0&&(index==1||index==2)) 
				{
					mod=0;
				}
				
				int idx = singleBlockDest ? (y - starty) * 4 + (x - startx) : (y * width + x);
				img[channelsRGB*idx+0]  =(byte)CLAMP(0,(avg_color[0]&0xff)+mod,255);
				img[channelsRGB*idx+1]  =(byte)CLAMP(0,(avg_color[1]&0xff)+mod,255);
				img[channelsRGB*idx+2]  =(byte)CLAMP(0,(avg_color[2]&0xff)+mod,255);
				if(diffbit==0&&index==1) 
				{
					alpha[idx*channelsA]=0;
					img[channelsRGB*idx+0]=0;
					img[channelsRGB*idx+1]=0;
					img[channelsRGB*idx+2]=0;
				}
				else 
				{
					alpha[idx*channelsA]=(byte)255;
				}
			}
		}
	}
	else
	{
		// We should flip
		shift=2;
		for(int x=startx; x<startx+4; x++)
		{
			for(int y=starty+2; y<starty+4; y++)
			{
				index  = ((pixel_indices_MSB >> shift) & 1) << 1;
				index |= ((pixel_indices_LSB >> shift) & 1);
				shift++;
				index=unscramble[index];
				int mod = compressParams[table][index];
				if(diffbit==0&&(index==1||index==2)) 
				{
					mod=0;
				}
				
				int idx = singleBlockDest ? (y - starty) * 4 + (x - startx) : (y * width + x);
				img[channelsRGB*idx+0]  =(byte)CLAMP(0,(avg_color[0]&0xff)+mod,255);
				img[channelsRGB*idx+1]  =(byte)CLAMP(0,(avg_color[1]&0xff)+mod,255);
				img[channelsRGB*idx+2]  =(byte)CLAMP(0,(avg_color[2]&0xff)+mod,255);
				if(diffbit==0&&index==1) 
				{
					alpha[idx*channelsA]=0;
					img[channelsRGB*idx+0]=0;
					img[channelsRGB*idx+1]=0;
					img[channelsRGB*idx+2]=0;
				}
				else 
				{
					alpha[idx*channelsA]=(byte)255;
				}
			}
			shift += 2;
		}
	}
}
static void decompressBlockDifferentialWithAlpha(int block_part1, int block_part2, byte[] img, byte[] alpha, int width, int height, int startx, int starty)
{
  decompressBlockDifferentialWithAlphaC(block_part1, block_part2, img, alpha, width, height, startx, starty, 3);
}


// similar to regular decompression, but alpha channel is set to 0 if pixel index is 2, otherwise 255.
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void decompressBlockTHUMB59TAlphaC(int block_part1, int block_part2, byte[] img, byte[] alpha, int width, int height, int startx, int starty, int channelsRGB)
{
	boolean singleBlockDest = img.length == 4 * 4 * 3; // special indicator for a small decompression target
	
	byte[][] colorsRGB444 = new byte[2][3];
	byte[][] colors = new byte[2][3];
	byte[][] paint_colors = new byte[4][3];
	byte distance;
	byte[][] block_mask = new byte[4][4];
    int channelsA;

    if(channelsRGB == 3)
    {
      // We will decode the alpha data to a separate memory area. 
      channelsA = 1;
    }
    else
    {
      // We will decode the RGB data and the alpha data to the same memory area, 
      // interleaved as RGBA. 
    	channelsA = 4;
      // I don't have any code lines that use 4 so I can't test how teh user uses this, so just ... edit the img data!
      // the calls with channelsRGB == 3 seem to set up a w x x h 1 byte array ready for filling
      // all other similar methods only come in with 3 channels!
      //alpha = img[0+3];
    }

	// First decode left part of block.
	colorsRGB444[0][R]= (byte)GETBITSHIGH(block_part1, 4, 58);
	colorsRGB444[0][G]= (byte)GETBITSHIGH(block_part1, 4, 54);
	colorsRGB444[0][B]= (byte)GETBITSHIGH(block_part1, 4, 50);

	colorsRGB444[1][R]= (byte)GETBITSHIGH(block_part1, 4, 46);
	colorsRGB444[1][G]= (byte)GETBITSHIGH(block_part1, 4, 42);
	colorsRGB444[1][B]= (byte)GETBITSHIGH(block_part1, 4, 38);

	distance   = (byte)GETBITSHIGH(block_part1, TABLE_BITS_59T, 34);

	// Extend the two colors to RGB888	
	decompressColor(R_BITS59T, G_BITS59T, B_BITS59T, colorsRGB444, colors);	
	calculatePaintColors59T(distance, PATTERN.PATTERN_T, colors, paint_colors);
	
	// Choose one of the four paint colors for each texel
	for (byte x = 0; x < BLOCKWIDTH; ++x) 
	{
		for (byte y = 0; y < BLOCKHEIGHT; ++y) 
		{
			//block_mask[x][y] = GETBITS(block_part2,2,31-(y*4+x)*2);
			block_mask[x][y] = (byte)(GETBITS(block_part2,1,(y+x*4)+16)<<1);
			block_mask[x][y] = (byte)(block_mask[x][y] | GETBITS(block_part2,1,(y+x*4)));
			
			int idx = singleBlockDest ? channelsRGB*((y)*BLOCKWIDTH+x) : channelsRGB*((starty+y)*width+startx+x);
			int aIdx = singleBlockDest ? 1 *((y)*BLOCKWIDTH+x) : 1 *((starty+y)*width+startx+x);
			
			img[idx+R] = 
				 CLAMP(0,paint_colors[block_mask[x][y]][R],255); // RED
			img[idx+G] =
				 CLAMP(0,paint_colors[block_mask[x][y]][G],255); // GREEN
			img[idx+B] =
				 CLAMP(0,paint_colors[block_mask[x][y]][B],255); // BLUE
			if(block_mask[x][y]==2)  
			{
				alpha[aIdx]=0;
				
				img[idx+R] =0;
				img[idx+G] =0;
				img[idx+B] =0;
			}
			else {
				alpha[aIdx]=(byte)255;
			}
		}
	}
}
static void decompressBlockTHUMB59TAlpha(int block_part1, int block_part2, byte[] img, byte[] alpha, int width, int height, int startx, int starty)
{
  decompressBlockTHUMB59TAlphaC(block_part1, block_part2, img, alpha, width, height, startx, starty, 3);
}


// Decompress an H-mode block with alpha
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void decompressBlockTHUMB58HAlphaC(int block_part1, int block_part2, byte[] img, byte[] alpha, int width, int height, int startx, int starty, int channelsRGB)
{
	boolean singleBlockDest = img.length == 4 * 4 * 3; // special indicator for a small decompression target
	
	int col0, col1;
	byte[][] colors = new byte[2][3];
	byte[][] colorsRGB444 = new byte[2][3];
	byte[][] paint_colors = new byte[4][3];
	byte distance;
	byte[][] block_mask = new byte[4][4];
	int channelsA;	
	
	if(channelsRGB == 3)
	{
		// We will decode the alpha data to a separate memory area. 
		channelsA = 1;
	}
	else
	{
	    // We will decode the RGB data and the alpha data to the same memory area, 
		// interleaved as RGBA. 
		channelsA = 4;
		
		// I don't have any code lines that use 4 so I can't test how teh user uses this, so just ... edit the img data!
		// the calls with channelsRGB == 3 seem to set up a w x x h 1 byte array ready for filling
		// all other similar methods only come in with 3 channels!
		//alpha = img[0+3];
	  }

	// First decode left part of block.
	colorsRGB444[0][R]= (byte)GETBITSHIGH(block_part1, 4, 57);
	colorsRGB444[0][G]= (byte)GETBITSHIGH(block_part1, 4, 53);
	colorsRGB444[0][B]= (byte)GETBITSHIGH(block_part1, 4, 49);

	colorsRGB444[1][R]= (byte)GETBITSHIGH(block_part1, 4, 45);
	colorsRGB444[1][G]= (byte)GETBITSHIGH(block_part1, 4, 41);
	colorsRGB444[1][B]= (byte)GETBITSHIGH(block_part1, 4, 37);

	distance = 0;
	distance = (byte)((GETBITSHIGH(block_part1, 2, 33)) << 1);

	col0 = GETBITSHIGH(block_part1, 12, 57);
	col1 = GETBITSHIGH(block_part1, 12, 45);

	if(col0 >= col1)
	{
		distance = (byte)(distance | 1);
	}

	// Extend the two colors to RGB888	
	decompressColor(R_BITS58H, G_BITS58H, B_BITS58H, colorsRGB444, colors);	
	
	calculatePaintColors58H(distance, PATTERN.PATTERN_H, colors, paint_colors);
	
	// Choose one of the four paint colors for each texel
	for (byte x = 0; x < BLOCKWIDTH; ++x) 
	{
		for (byte y = 0; y < BLOCKHEIGHT; ++y) 
		{
			//block_mask[x][y] = GETBITS(block_part2,2,31-(y*4+x)*2);
			block_mask[x][y] = (byte)(GETBITS(block_part2,1,(y+x*4)+16)<<1);
			block_mask[x][y] = (byte)(block_mask[x][y] | GETBITS(block_part2,1,(y+x*4)));
			
			int idx = singleBlockDest ? channelsRGB*((y)*BLOCKWIDTH+x) : channelsRGB*((starty+y)*width+startx+x);			
			int aIdx = singleBlockDest ? 1 *((y)*BLOCKWIDTH+x) : 1 *((starty+y)*width+startx+x);
			
			img[idx+R] =
				CLAMP(0,paint_colors[block_mask[x][y]][R],255); // RED
			img[idx+G] =
				CLAMP(0,paint_colors[block_mask[x][y]][G],255); // GREEN
			img[idx+B] =
				CLAMP(0,paint_colors[block_mask[x][y]][B],255); // BLUE
			
			if(block_mask[x][y]==2)  
			{
				alpha[aIdx]=0;
				
				img[idx+R] =0;
				img[idx+G] =0;
				img[idx+B] =0;
			}
			else {
				alpha[aIdx]=(byte)255;
			}
		}
	}
}
static void decompressBlockTHUMB58HAlpha(int block_part1, int block_part2, byte[] img, byte[] alpha, int width, int height, int startx, int starty)
{
  decompressBlockTHUMB58HAlphaC(block_part1, block_part2, img, alpha, width, height, startx, starty, 3);
}
// Decompression function for ETC2_RGBA1 format.
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void decompressBlockETC21BitAlphaC(int block_part1, int block_part2, byte[] img, byte[] alphaimg, int width, int height, int startx, int starty, int channelsRGB)
{
	boolean singleBlockDest = img.length == 4 * 4 * 3; // special indicator for a small decompression target
	
	int diffbit;
	byte[] color1 = new byte[3];
	byte[] diff = new byte[3];
	byte red, green, blue;
	int channelsA;	
	
	if(channelsRGB == 3)
	{
	  // We will decode the alpha data to a separate memory area. 
	  channelsA = 1;
	}
	else
	{
	    // We will decode the RGB data and the alpha data to the same memory area, 
		// interleaved as RGBA. 
		channelsA = 4;
		
		// I don't have any code lines that use 4 so I can't test how teh user uses this, so just ... edit the img data!
		// the calls with channelsRGB == 3 seem to set up a w x x h 1 byte array ready for filling
		// all other similar methods only come in with 3 channels!    
		//alphaimg = img[0+3];
	}

	diffbit = (GETBITSHIGH(block_part1, 1, 33));

	if( diffbit != 0 )
	{
		// We have diffbit = 1, meaning no transparent pixels. regular decompression.

		// Base color
		color1[0]= (byte)GETBITSHIGH(block_part1, 5, 63);
		color1[1]= (byte)GETBITSHIGH(block_part1, 5, 55);
		color1[2]= (byte)GETBITSHIGH(block_part1, 5, 47);

		// Diff color
		diff[0]= (byte)GETBITSHIGH(block_part1, 3, 58);
		diff[1]= (byte)GETBITSHIGH(block_part1, 3, 50);
		diff[2]= (byte)GETBITSHIGH(block_part1, 3, 42);

		// Extend sign bit to entire byte. 
		diff[0] = (byte)(diff[0] << 5);
		diff[1] = (byte)(diff[1] << 5);
		diff[2] = (byte)(diff[2] << 5);
		diff[0] = (byte)(diff[0] >> 5);
		diff[1] = (byte)(diff[1] >> 5);
		diff[2] = (byte)(diff[2] >> 5);

		red   = (byte)((color1[0]&0xff) + (diff[0]&0xff));
		green = (byte)((color1[1]&0xff) + (diff[1]&0xff));
		blue  = (byte)((color1[2]&0xff) + (diff[2]&0xff));

		if(red < 0 || red > 31)
		{
			int[] block59_part1=new int[1], block59_part2=new int[1];
			unstuff59bits(block_part1, block_part2, block59_part1, block59_part2);
			decompressBlockTHUMB59Tc(block59_part1[0], block59_part2[0], img, width, height, startx, starty, channelsRGB);
		}
		else if (green < 0 || green > 31)
		{
			int[] block58_part1=new int[1], block58_part2=new int[1];
			unstuff58bits(block_part1, block_part2, block58_part1, block58_part2);
			decompressBlockTHUMB58Hc(block58_part1[0], block58_part2[0], img, width, height, startx, starty, channelsRGB);
		}
		else if(blue < 0 || blue > 31)
		{
			int[] block57_part1=new int[1], block57_part2=new int[1];

			unstuff57bits(block_part1, block_part2, block57_part1, block57_part2);
			decompressBlockPlanar57c(block57_part1[0], block57_part2[0], img, width, height, startx, starty, channelsRGB);
		}
		else
		{
 			decompressBlockDifferentialWithAlphaC(block_part1, block_part2, img, alphaimg, width, height, startx, starty, channelsRGB);
		}
		for(int x=startx; x<startx+4; x++) 
		{
			for(int y=starty; y<starty+4; y++) 
			{
				int aIdx = singleBlockDest ? channelsA*(x+y*BLOCKWIDTH) : channelsA*(x+y*width);
				alphaimg[aIdx]=(byte)255;
			}
		}
	}
	else
	{
		// We have diffbit = 0, transparent pixels. Only T-, H- or regular diff-mode possible.
		
		// Base color
		color1[0]= (byte)GETBITSHIGH(block_part1, 5, 63);
		color1[1]= (byte)GETBITSHIGH(block_part1, 5, 55);
		color1[2]= (byte)GETBITSHIGH(block_part1, 5, 47);

		// Diff color
		diff[0]= (byte)GETBITSHIGH(block_part1, 3, 58);
		diff[1]= (byte)GETBITSHIGH(block_part1, 3, 50);
		diff[2]= (byte)GETBITSHIGH(block_part1, 3, 42);

		// Extend sign bit to entire byte. 
		diff[0] = (byte)(diff[0] << 5);
		diff[1] = (byte)(diff[1] << 5);
		diff[2] = (byte)(diff[2] << 5);
		diff[0] = (byte)(diff[0] >> 5);
		diff[1] = (byte)(diff[1] >> 5);
		diff[2] = (byte)(diff[2] >> 5);

		red   = (byte)((color1[0]&0xff) + (diff[0]&0xff));
		green = (byte)((color1[1]&0xff) + (diff[1]&0xff));
		blue  = (byte)((color1[2]&0xff) + (diff[2]&0xff));
		if(red < 0 || red > 31)
		{
			int[] block59_part1=new int[1], block59_part2=new int[1];
			unstuff59bits(block_part1, block_part2, block59_part1, block59_part2);
			decompressBlockTHUMB59TAlphaC(block59_part1[0], block59_part2[0], img, alphaimg, width, height, startx, starty, channelsRGB);
		}
		else if(green < 0 || green > 31) 
		{
			int[] block58_part1=new int[1], block58_part2=new int[1];
			unstuff58bits(block_part1, block_part2, block58_part1, block58_part2);
			decompressBlockTHUMB58HAlphaC(block58_part1[0], block58_part2[0], img, alphaimg, width, height, startx, starty, channelsRGB);
		}
		else if(blue < 0 || blue > 31)
		{
			int[] block57_part1=new int[1], block57_part2=new int[1];

			unstuff57bits(block_part1, block_part2, block57_part1, block57_part2);
			decompressBlockPlanar57c(block57_part1[0], block57_part2[0], img, width, height, startx, starty, channelsRGB);
			for(int x=startx; x<startx+4; x++) 
			{
				for(int y=starty; y<starty+4; y++) 
				{
					int aIdx = singleBlockDest ? channelsA*(x+y*BLOCKWIDTH) : channelsA*(x+y*width);
					alphaimg[aIdx]=(byte)255;
				}
			}
		}
		else
			decompressBlockDifferentialWithAlphaC(block_part1, block_part2, img,alphaimg, width, height, startx, starty, channelsRGB);
	}
}
public static void decompressBlockETC21BitAlpha(int block_part1, int block_part2, byte[] img, byte[] alphaimg, int width, int height, int startx, int starty)
{
  decompressBlockETC21BitAlphaC(block_part1, block_part2, img, alphaimg, width, height, startx, starty, 3);
}
//
//	Utility functions used for alpha compression
//

// bit number frompos is extracted from input, and moved to bit number topos in the return value.
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static byte getbit(byte input, int frompos, int topos) 
{
	//byte output=0;
	if(frompos>topos)
		return (byte)(((1<<frompos)&input)>>(frompos-topos));
	return (byte)(((1<<frompos)&input)<<(topos-frompos));
}
static int getbit(int input, int frompos, int topos) 
{
	//int output=0;
	if(frompos>topos)
		return (((1<<frompos)&input)>>(frompos-topos));
	return (((1<<frompos)&input)<<(topos-frompos));
}

// takes as input a value, returns the value clamped to the interval [0,255].
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static int clamp(int val) 
{
	if(val<0)
		val=0;
	if(val>255)
		val=255;
	return val;
}

// Decodes the alpha component in a block coded with GL_COMPRESSED_RGBA8_ETC2_EAC.
// Note that this decoding is slightly different from that of GL_COMPRESSED_R11_EAC.
// However, a hardware decoder can share gates between the two formats as explained
// in the specification under GL_COMPRESSED_R11_EAC.
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void decompressBlockAlphaC(byte[] data, byte[] img, int width, int height, int ix, int iy, int channels) 
{
	boolean singleBlockDest = img.length == 4 * 4 * 3; // special indicator for a small decompression target
	
	byte alpha = data[0];
	int table = data[1]&0xff;
	
	int bit=0;
	int byte_=2;
	//extract an alpha value for each pixel.
	for(int x=0; x<4; x++) 
	{
		for(int y=0; y<4; y++) 
		{
			//Extract table index
			int index=0;
			for(int bitpos=0; bitpos<3; bitpos++) 
			{
				index|=getbit(data[byte_],7-bit,2-bitpos);
				bit++;
				if(bit>7) 
				{
					bit=0;
					byte_++;
				}
			}
			int idx = singleBlockDest ? (ix+x+(iy+y)*BLOCKWIDTH)*channels : (ix+x+(iy+y)*width)*channels;
			img[idx]=(byte)clamp((alpha&0xff) +alphaTable[table][index]);
		}
	}
}
static void decompressBlockAlpha(byte[] data, byte[] img, int width, int height, int ix, int iy) 
{
  decompressBlockAlphaC(data, img, width, height, ix, iy, 1);
}

// Does decompression and then immediately converts from 11 bit signed to a 16-bit format.
// 
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static short get16bits11signed(int base, int table, int mul, int index) 
{
	int elevenbase = base-128;
	if(elevenbase==-128)
		elevenbase=-127;
	elevenbase*=8;
	//i want the positive value here
	int tabVal = -alphaBase[table][3-index%4]-1;
	//and the sign, please
	int sign = 1-(index/4);
	
	if(sign != 0)
		tabVal=tabVal+1;
	int elevenTabVal = tabVal*8;

	if(mul!=0)
		elevenTabVal*=mul;
	else
		elevenTabVal/=8;

	if(sign != 0)
		elevenTabVal=-elevenTabVal;

	//calculate sum
	int elevenbits = elevenbase+elevenTabVal;

	//clamp..
	if(elevenbits>=1024)
		elevenbits=1023;
	else if(elevenbits<-1023)
		elevenbits=-1023;
	//this is the value we would actually output.. 
	//but there aren't any good 11-bit file or uncompressed GL formats
	//so we extend to 15 bits signed.
	sign = elevenbits<0 ? 1 : 0;
	elevenbits=Math.abs(elevenbits);
	short fifteenbits = (short)((elevenbits<<5)+(elevenbits>>5));
	short sixteenbits=fifteenbits;

	if(sign != 0)
		sixteenbits=(short)-sixteenbits;
	
	return sixteenbits;
}

// Does decompression and then immediately converts from 11 bit signed to a 16-bit format 
// Calculates the 11 bit value represented by base, table, mul and index, and extends it to 16 bits.
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static short get16bits11bits(int base, int table, int mul, int index) 
{
	int elevenbase = base*8+4;

	//i want the positive value here
	int tabVal = -alphaBase[table][3-index%4]-1;
	//and the sign, please
	int sign = 1-(index/4);
	
	if(sign != 0)
		tabVal=tabVal+1;
	int elevenTabVal = tabVal*8;

	if(mul!=0)
		elevenTabVal*=mul;
	else
		elevenTabVal/=8;

	if(sign != 0)
		elevenTabVal=-elevenTabVal;

	//calculate sum
	int elevenbits = elevenbase+elevenTabVal;

	//clamp..
	if(elevenbits>=256*8)
		elevenbits=256*8-1;
	else if(elevenbits<0)
		elevenbits=0;
	//elevenbits now contains the 11 bit alpha value as defined in the spec.

	//extend to 16 bits before returning, since we don't have any good 11-bit file formats.
	short sixteenbits = (short)((elevenbits<<5)+(elevenbits>>6));

	return sixteenbits;
}

// Decompresses a block using one of the GL_COMPRESSED_R11_EAC or GL_COMPRESSED_SIGNED_R11_EAC-formats
// NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
static void decompressBlockAlpha16bitC(byte[] data, byte[] img, int width, int height, int ix, int iy, int channels) 
{
	int alpha = data[0];
	int table = data[1];

	if(formatSigned != 0) 
	{
		//if we have a signed format, the base value is given as a signed byte. We convert it to (0-255) here,
		//so more code can be shared with the unsigned mode.
		alpha = (data[0]&0xff);//WTF?*((signed char*)(&data[0]));
		alpha = alpha+128;
	}

	int bit=0;
	int byte_=2;
	//extract an alpha value for each pixel.
	for(int x=0; x<4; x++) 
	{
		for(int y=0; y<4; y++) 
		{
			//Extract table index
			int index=0;
			for(int bitpos=0; bitpos<3; bitpos++) 
			{
				index|=getbit(data[byte_],7-bit,2-bitpos);
				bit++;
				if(bit>7) 
				{
					bit=0;
					byte_++;
				}
			}
			int windex = channels*(2*(ix+x+(iy+y)*width));
if(PGMOUT == 0) {
			if(formatSigned != 0)
			{
				//*(int16 *)&img[windex] = get16bits11signed(alpha,(table%16),(table/16),index); 
				ByteBuffer.wrap(img,windex,2).putShort(get16bits11signed(alpha,(table%16),(table/16),index));

			}
			else
			{
				//*(uint16 *)&img[windex] = get16bits11bits(alpha,(table%16),(table/16),index);
				ByteBuffer.wrap(img,windex,2).putShort(get16bits11bits(alpha,(table%16),(table/16),index)); 
			}
}else {
			//make data compatible with the .pgm format. See the comment in compressBlockAlpha16() for details.
			short uSixteen;
			if (formatSigned != 0)
			{
				//the pgm-format only allows unsigned images,
				//so we add 2^15 to get a 16-bit value.
				uSixteen = (short)(get16bits11signed(alpha,(table%16),(table/16),index) + 256*128);
			}
			else
			{
				uSixteen = get16bits11bits(alpha,(table%16),(table/16),index);
			}
			//byte swap for pgm
			img[windex] = (byte)(uSixteen/256);
			img[windex+1] = (byte)(uSixteen%256);
}

		}
	}			
}

static void decompressBlockAlpha16bit(byte[] data, byte[] img, int width, int height, int ix, int iy)
{
  decompressBlockAlpha16bitC(data, img, width, height, ix, iy, 1);
}
}