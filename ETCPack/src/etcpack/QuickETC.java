package etcpack;

/**
 * Amazing... https://github.com/wolfpld/etcpak/commit/da85020e690890f4356d42ab5802e4f957f220fd?diff=unified
 * https://nahjaeho.github.io/papers/SA20/QUICKETC2_SA20.pdf
 * https://nahjaeho.github.io/papers/SA20/QUICKETC2_SA20_slides.pdf
 * 
 */
public class QuickETC extends ETCPack {

	//Compress a block with ETC2 RGB
	//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//address  int &compressed1,  int &compressed2
	@Override
	void compressBlockETC2Fast(	byte[] img, byte[] alphaimg, byte[] imgdec, int width, int height, int startx,
								int starty, int[] compressed1, int[] compressed2) {

		// get a list of 16 luma values		
		// and at the same time work out if there exists a punch through alpha
		boolean alphaExists = false;

		//Y = 0.299R + 0.587G + 0.114B
		float LR = 0.299f;
		float LG = 0.587f;
		float LB = 0.114f;
		float[] lumas = new float[16];
		float lmin = 1f;
		float lmax = 0f;
		// Load luma data into 1D array
		for (int l = 0, y = 0; y < 4; y++)
			for (int x = 0; x < 4; x++) {
				float luma = ((img[3 * width * (starty + y) + 3 * (startx + x) + R] & 0xff) / 255f) * LR
								+ ((img[3 * width * (starty + y) + 3 * (startx + x) + G] & 0xff) / 255f) * LG
								+ ((img[3 * width * (starty + y) + 3 * (startx + x) + B] & 0xff) / 255f) * LB;

				if (luma < lmin)
					lmin = luma;

				if (luma > lmax)
					lmax = luma;

				lumas[l++] = luma;

				if ((format == FORMAT.ETC2PACKAGE_RGBA1 || format == FORMAT.ETC2PACKAGE_sRGBA1)
					&& alphaimg[1 * width * (starty + y) + 1 * (startx + x) + 0] < 127) {
					alphaExists = true;
				}

			}

		float LD = lmax - lmin;

		// modify it into a luma differnce range (top value - bottom value)		
		// luma_diff		

		float T1 = 0.03f;
		float T2 = 0.09f;
		float T3 = 0.38f;

		//very-low contrast (luma_diff≤T1),
		//low-contrast (T1<luma_diff≤T2),
		//mid-contrast (T2<luma_diff<T3), 
		//and high-contrast (luma_diff≥T3

		// mid = etc1 

		// high = ETC1 or T/H do both and see which is better
		// high can be set to ETC1 for the non best quality system

		boolean planar = false;
		boolean etc1 = false;

		if (LD <= T1) {//very-low contrast 
			// very low = planar mode
			planar = true;
		} else if (T1 < LD && LD <= T2) {//low-contrast
			// In the case of low-contrast blocks, we check whether a block can be smoothly expressed by the base colors at the corners of the block.
			//If a pair of two corresponding corner pixels (top-left and bottom-right, or bottom-left and top-right) has the min and max luma 
			//values, we exploit a high possibility that the other pixels can be properly interpolated in the planar mode. 
			//Otherwise, we perform traditional ETC1 compression. 
			//The corner index pairs {(0, 15) & (3, 12)} and the pixel indices corresponding to
			//the min/max value

			if (((lumas[0] == lmin && lumas[15] == lmax) || (lumas[0] == lmax && lumas[15] == lmin))
				|| ((lumas[3] == lmin && lumas[12] == lmax) || (lumas[3] == lmax && lumas[12] == lmin))) {
				planar = true;
			} else {
				etc1 = true;
			}
		} else if (T2 < LD && LD < T3) {//mid-contrast
			// mid = etc1 
			etc1 = true;
		} else if (LD >= T3) {//and high-contrast
			// high = ETC1 or T/H do both and see which is better
			// high can be set to ETC1 for the non best quality system
			etc1 = true;
		}

		// go alpha paths if there is an alpha 
		if (alphaExists) {
			//If a punch through exists we can only use ETC diff or T or H

			int[] etc1_word1 = new int[1];
			int[] etc1_word2 = new int[1];
			compressBlockDifferentialWithAlpha(true, img, alphaimg, imgdec, width, height, startx, starty, etc1_word1,
					etc1_word2);
			compressed1[0] = etc1_word1[0];
			compressed2[0] = etc1_word2[0];

			/*
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
				
			
			*/

		} else {

			if (planar) {
				int[] planar57_word1 = new int[1];
				int[] planar57_word2 = new int[1];
				int[] planar_word1 = new int[1];
				int[] planar_word2 = new int[1];
				compressBlockPlanar57(img, width, height, startx, starty, planar57_word1, planar57_word2);
				stuff57bits(planar57_word1[0], planar57_word2[0], planar_word1, planar_word2);
				compressed1[0] = planar_word1[0];
				compressed2[0] = planar_word2[0];
			} else {
				int[] etc1_word1 = new int[1];
				int[] etc1_word2 = new int[1];
				compressBlockDiffFlipFast(img, imgdec, width, height, startx, starty, etc1_word1, etc1_word2);
				compressed1[0] = etc1_word1[0];
				compressed2[0] = etc1_word2[0];
			}

			/*
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
			
					
				
			if(error_thumbT < error_etc1 && error_thumbT < error_thumbH)
			{
				
				compressBlockTHUMB59TFastest(img,width, height, startx, starty, thumbT59_word1, thumbT59_word2);
				decompressBlockTHUMB59T(thumbT59_word1[0], thumbT59_word2[0], imgdec, width, height, startx, starty);			
				error_thumbT = calcBlockErrorRGB(img, imgdec, width, height, startx, starty);
						
				
				stuff59bits(thumbT59_word1[0], thumbT59_word2[0], thumbT_word1, thumbT_word2);
				compressed1[0] = thumbT_word1[0];
				compressed2[0] = thumbT_word2[0];
			
				
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
				
				compressBlockTHUMB58HFastest(img,width,height,startx, starty, thumbH58_word1, thumbH58_word2);
				decompressBlockTHUMB58H(thumbH58_word1[0], thumbH58_word2[0], imgdec, width, height, startx, starty);			
				error_thumbH = calcBlockErrorRGB(img, imgdec, width, height, startx, starty);
				
				stuff58bits(thumbH58_word1[0], thumbH58_word2[0], thumbH_word1, thumbH_word2);
				compressed1[0] = thumbH_word1[0];
				compressed2[0] = thumbH_word2[0];
				
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
			*/
		}

	}

	//Compress an ETC2 RGB block using perceptual error metric
	//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//address  int &compressed1,  int &compressed2
	@Override
	void compressBlockETC2FastPerceptual(	byte[] img, byte[] imgdec, int width, int height, int startx, int starty,
											int[] compressed1, int[] compressed2) {

		// get a list of 16 luma values			

		//Y = 0.299R + 0.587G + 0.114B
		float LR = 0.299f;
		float LG = 0.587f;
		float LB = 0.114f;
		float[] lumas = new float[16];
		float lmin = 1f;
		float lmax = 0f;
		// Load luma data into 1D array
		for (int l = 0, y = 0; y < 4; y++)
			for (int x = 0; x < 4; x++) {
				float luma = ((img[3 * width * (starty + y) + 3 * (startx + x) + R] & 0xff) / 255f) * LR
								+ ((img[3 * width * (starty + y) + 3 * (startx + x) + G] & 0xff) / 255f) * LG
								+ ((img[3 * width * (starty + y) + 3 * (startx + x) + B] & 0xff) / 255f) * LB;

				if (luma < lmin)
					lmin = luma;

				if (luma > lmax)
					lmax = luma;

				lumas[l++] = luma;
			}

		float LD = lmax - lmin;

		// modify it into a luma differnce range (top value - bottom value)		
		// luma_diff		

		float T1 = 0.03f;
		float T2 = 0.09f;
		float T3 = 0.38f;

		//very-low contrast (luma_diff≤T1),
		//low-contrast (T1<luma_diff≤T2),
		//mid-contrast (T2<luma_diff<T3), 
		//and high-contrast (luma_diff≥T3

		// mid = etc1 

		// high = ETC1 or T/H do both and see which is better
		// high can be set to ETC1 for the non best quality system

		boolean planar = false;
		boolean etc1 = false;

		if (LD <= T1) {//very-low contrast 
			// very low = planar mode
			planar = true;
		} else if (T1 < LD && LD <= T2) {//low-contrast
			// In the case of low-contrast blocks, we check whether a block can be smoothly expressed by the base colors at the corners of the block.
			//If a pair of two corresponding corner pixels (top-left and bottom-right, or bottom-left and top-right) has the min and max luma 
			//values, we exploit a high possibility that the other pixels can be properly interpolated in the planar mode. 
			//Otherwise, we perform traditional ETC1 compression. 
			//The corner index pairs {(0, 15) & (3, 12)} and the pixel indices corresponding to
			//the min/max value

			if (((lumas[0] == lmin && lumas[15] == lmax) || (lumas[0] == lmax && lumas[15] == lmin))
				|| ((lumas[3] == lmin && lumas[12] == lmax) || (lumas[3] == lmax && lumas[12] == lmin))) {
				planar = true;
			} else {
				etc1 = true;
			}
		} else if (T2 < LD && LD < T3) {//mid-contrast
			// mid = etc1 
			etc1 = true;
		} else if (LD >= T3) {//and high-contrast
			// high = ETC1 or T/H do both and see which is better
			// high can be set to ETC1 for the non best quality system
			etc1 = true;
		}

		if (planar) {
			int[] planar57_word1 = new int[1];
			int[] planar57_word2 = new int[1];
			int[] planar_word1 = new int[1];
			int[] planar_word2 = new int[1];
			compressBlockPlanar57(img, width, height, startx, starty, planar57_word1, planar57_word2);
			stuff57bits(planar57_word1[0], planar57_word2[0], planar_word1, planar_word2);
			compressed1[0] = planar_word1[0];
			compressed2[0] = planar_word2[0];
		} else {
			int[] etc1_word1 = new int[1];
			int[] etc1_word2 = new int[1];
			compressBlockDiffFlipFastPerceptual(img, imgdec, width, height, startx, starty, etc1_word1, etc1_word2);
			compressed1[0] = etc1_word1[0];
			compressed2[0] = etc1_word2[0];
		}

		/*
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
		
				
			
		if(error_thumbT < error_etc1 && error_thumbT < error_thumbH)
		{
			
			compressBlockTHUMB59TFastestPerceptual1000(img,width, height, startx, starty, thumbT59_word1, thumbT59_word2);
			decompressBlockTHUMB59T(thumbT59_word1[0], thumbT59_word2[0], imgdec, width, height, startx, starty);			
			error_thumbT = 1000*calcBlockPerceptualErrorRGB(img, imgdec, width, height, startx, starty);
					
			
			stuff59bits(thumbT59_word1[0], thumbT59_word2[0], thumbT_word1, thumbT_word2);
			compressed1[0] = thumbT_word1[0];
			compressed2[0] = thumbT_word2[0];
		
			
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
			
			compressBlockTHUMB58HFastestPerceptual1000(img,width,height,startx, starty, thumbH58_word1, thumbH58_word2);
			decompressBlockTHUMB58H(thumbH58_word1[0], thumbH58_word2[0], imgdec, width, height, startx, starty);			
			error_thumbH = 1000*calcBlockPerceptualErrorRGB(img, imgdec, width, height, startx, starty);
			
			stuff58bits(thumbH58_word1[0], thumbH58_word2[0], thumbH_word1, thumbH_word2);
			compressed1[0] = thumbH_word1[0];
			compressed2[0] = thumbH_word2[0];
			
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
		*/

	}

	//Compress an ETC1 block (or the individual and differential modes of an ETC2 block)
	//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//address int &compressed1,  int &compressed2)
	@Override
	void compressBlockDiffFlipFast(	byte[] img, byte[] imgdec, int width, int height, int startx, int starty,
									int[] compressed1, int[] compressed2) {
		int[] combined_both1 = new int[1];
		int[] combined_both2 = new int[1];
		compressBlockDiffFlipAverageCombined(img, width, height, startx, starty, combined_both1, combined_both2, false);
		compressed1[0] = combined_both1[0];
		compressed2[0] = combined_both2[0];
	}

	//Compress an ETC1 block (or the individual and differential modes of an ETC2 block)
	//Uses perceptual error metric.
	//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//address  int &compressed1,  int &compressed2)
	@Override
	void compressBlockDiffFlipFastPerceptual(	byte[] img, byte[] imgdec, int width, int height, int startx, int starty,
												int[] compressed1, int[] compressed2) {
		int[] combined_both1 = new int[1];
		int[] combined_both2 = new int[1];
		compressBlockDiffFlipAverageCombined(img, width, height, startx, starty, combined_both1, combined_both2, true);
		compressed1[0] = combined_both1[0];
		compressed2[0] = combined_both2[0];
	}

	/**
	 * Merge of compressBlockDiffFlipAverage and compressBlockDiffFlipCombined both perceptual and non perceptual by way
	 * of a simple boolean
	 *
	 */
	void compressBlockDiffFlipAverageCombined(	byte[] img, int width, int height, int startx, int starty,
												int[] compressed1, int[] compressed2, boolean perceptual) {
		int[] compressed1_normA = new int[1], compressed2_normA = new int[1];
		int[] compressed1_flipA = new int[1], compressed2_flipA = new int[1];
		byte[] avg_color_quant1 = new byte[3], avg_color_quant2 = new byte[3];

		float[] avg_color24_float1 = new float[3], avg_color24_float2 = new float[3];
		float[] avg_color42_float1 = new float[3], avg_color42_float2 = new float[3];
		int[] enc_color1 = new int[3], enc_color2 = new int[3], diff = new int[3];
		int min_error = 255 * 255 * 8 * 3;
		int best_table_indices1 = 0, best_table_indices2 = 0;
		int[] best_table1 = new int[1], best_table2 = new int[1];
		int diffbit;

		int norm_errA = 0;
		int flip_errA = 0;

		// First try normal blocks 2x4:
		computeAverageColor2x4noQuantFloat(img, width, height, startx, starty, avg_color24_float1);
		computeAverageColor2x4noQuantFloat(img, width, height, startx + 2, starty, avg_color24_float2);

		// First test if avg_color1 is similar enough to avg_color2 so that
		// we can use differential coding of colors. 

		float eps;

		enc_color1[0] = (int)(JAS_ROUND(31.0 * avg_color24_float1[0] / 255.0));
		enc_color1[1] = (int)(JAS_ROUND(31.0 * avg_color24_float1[1] / 255.0));
		enc_color1[2] = (int)(JAS_ROUND(31.0 * avg_color24_float1[2] / 255.0));
		enc_color2[0] = (int)(JAS_ROUND(31.0 * avg_color24_float2[0] / 255.0));
		enc_color2[1] = (int)(JAS_ROUND(31.0 * avg_color24_float2[1] / 255.0));
		enc_color2[2] = (int)(JAS_ROUND(31.0 * avg_color24_float2[2] / 255.0));

		// The difference to be coded, if it will fit
		diff[0] = enc_color2[0] - enc_color1[0];
		diff[1] = enc_color2[1] - enc_color1[1];
		diff[2] = enc_color2[2] - enc_color1[2];

		if ((diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4)
			&& (diff[2] <= 3)) {
			diffbit = 1;

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

			compressed1_normA[0] = 0;
			PUTBITSHIGH(compressed1_normA, diffbit, 1, 33);
			PUTBITSHIGH(compressed1_normA, enc_color1[0], 5, 63);
			PUTBITSHIGH(compressed1_normA, enc_color1[1], 5, 55);
			PUTBITSHIGH(compressed1_normA, enc_color1[2], 5, 47);
			PUTBITSHIGH(compressed1_normA, diff[0], 3, 58);
			PUTBITSHIGH(compressed1_normA, diff[1], 3, 50);
			PUTBITSHIGH(compressed1_normA, diff[2], 3, 42);

			int[] best_pixel_indices1_MSB = new int[1];
			int[] best_pixel_indices1_LSB = new int[1];
			int[] best_pixel_indices2_MSB = new int[1];
			int[] best_pixel_indices2_LSB = new int[1];

			norm_errA = 0;

			// left part of block 
			norm_errA = tryalltables_3bittable2x4percep(img, width, height, startx, starty, avg_color_quant1,
					best_table1, best_pixel_indices1_MSB, best_pixel_indices1_LSB);

			// right part of block
			norm_errA += tryalltables_3bittable2x4percep(img, width, height, startx + 2, starty, avg_color_quant2,
					best_table2, best_pixel_indices2_MSB, best_pixel_indices2_LSB);

			PUTBITSHIGH(compressed1_normA, best_table1[0], 3, 39);
			PUTBITSHIGH(compressed1_normA, best_table2[0], 3, 36);
			PUTBITSHIGH(compressed1_normA, 0, 1, 32);

			compressed2_normA[0] = 0;
			PUTBITS(compressed2_normA, (best_pixel_indices1_MSB[0]), 8, 23);
			PUTBITS(compressed2_normA, (best_pixel_indices2_MSB[0]), 8, 31);
			PUTBITS(compressed2_normA, (best_pixel_indices1_LSB[0]), 8, 7);
			PUTBITS(compressed2_normA, (best_pixel_indices2_LSB[0]), 8, 15);
		} else {
			diffbit = 0;
			// The difference is bigger than what fits in 555 plus delta-333, so we will have
			// to deal with 444 444.

			eps = (float)0.0001;

			enc_color1[0] = (int)(((float)avg_color24_float1[0] / (17.0)) + 0.5f + eps);
			enc_color1[1] = (int)(((float)avg_color24_float1[1] / (17.0)) + 0.5f + eps);
			enc_color1[2] = (int)(((float)avg_color24_float1[2] / (17.0)) + 0.5f + eps);
			enc_color2[0] = (int)(((float)avg_color24_float2[0] / (17.0)) + 0.5f + eps);
			enc_color2[1] = (int)(((float)avg_color24_float2[1] / (17.0)) + 0.5f + eps);
			enc_color2[2] = (int)(((float)avg_color24_float2[2] / (17.0)) + 0.5f + eps);

			avg_color_quant1[0] = (byte)(enc_color1[0] << 4 | enc_color1[0]);
			avg_color_quant1[1] = (byte)(enc_color1[1] << 4 | enc_color1[1]);
			avg_color_quant1[2] = (byte)(enc_color1[2] << 4 | enc_color1[2]);
			avg_color_quant2[0] = (byte)(enc_color2[0] << 4 | enc_color2[0]);
			avg_color_quant2[1] = (byte)(enc_color2[1] << 4 | enc_color2[1]);
			avg_color_quant2[2] = (byte)(enc_color2[2] << 4 | enc_color2[2]);

			// Pack bits into the first word. 
			// see above   

			compressed1_normA[0] = 0;
			PUTBITSHIGH(compressed1_normA, diffbit, 1, 33);
			PUTBITSHIGH(compressed1_normA, enc_color1[0], 4, 63);
			PUTBITSHIGH(compressed1_normA, enc_color1[1], 4, 55);
			PUTBITSHIGH(compressed1_normA, enc_color1[2], 4, 47);
			PUTBITSHIGH(compressed1_normA, enc_color2[0], 4, 59);
			PUTBITSHIGH(compressed1_normA, enc_color2[1], 4, 51);
			PUTBITSHIGH(compressed1_normA, enc_color2[2], 4, 43);

			int[] best_pixel_indices1_MSB = new int[1];
			int[] best_pixel_indices1_LSB = new int[1];
			int[] best_pixel_indices2_MSB = new int[1];
			int[] best_pixel_indices2_LSB = new int[1];

			// left part of block
			norm_errA = tryalltables_3bittable2x4percep(img, width, height, startx, starty, avg_color_quant1,
					best_table1, best_pixel_indices1_MSB, best_pixel_indices1_LSB);

			// right part of block
			norm_errA += tryalltables_3bittable2x4percep(img, width, height, startx + 2, starty, avg_color_quant2,
					best_table2, best_pixel_indices2_MSB, best_pixel_indices2_LSB);

			PUTBITSHIGH(compressed1_normA, best_table1[0], 3, 39);
			PUTBITSHIGH(compressed1_normA, best_table2[0], 3, 36);
			PUTBITSHIGH(compressed1_normA, 0, 1, 32);

			compressed2_normA[0] = 0;
			PUTBITS(compressed2_normA, (best_pixel_indices1_MSB[0]), 8, 23);
			PUTBITS(compressed2_normA, (best_pixel_indices2_MSB[0]), 8, 31);
			PUTBITS(compressed2_normA, (best_pixel_indices1_LSB[0]), 8, 7);
			PUTBITS(compressed2_normA, (best_pixel_indices2_LSB[0]), 8, 15);
		}

		// Now try flipped blocks 4x2:

		computeAverageColor4x2noQuantFloat(img, width, height, startx, starty, avg_color42_float1);
		computeAverageColor4x2noQuantFloat(img, width, height, startx, starty + 2, avg_color42_float2);

		// First test if avg_color1 is similar enough to avg_color2 so that
		// we can use differential coding of colors. 

		enc_color1[0] = (int)(JAS_ROUND(31.0 * avg_color42_float1[0] / 255.0));
		enc_color1[1] = (int)(JAS_ROUND(31.0 * avg_color42_float1[1] / 255.0));
		enc_color1[2] = (int)(JAS_ROUND(31.0 * avg_color42_float1[2] / 255.0));
		enc_color2[0] = (int)(JAS_ROUND(31.0 * avg_color42_float2[0] / 255.0));
		enc_color2[1] = (int)(JAS_ROUND(31.0 * avg_color42_float2[1] / 255.0));
		enc_color2[2] = (int)(JAS_ROUND(31.0 * avg_color42_float2[2] / 255.0));

		// The difference to be coded, if it fits
		diff[0] = enc_color2[0] - enc_color1[0];
		diff[1] = enc_color2[1] - enc_color1[1];
		diff[2] = enc_color2[2] - enc_color1[2];

		if ((diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4)
			&& (diff[2] <= 3)) {
			diffbit = 1;

			avg_color_quant1[0] = (byte)(enc_color1[0] << 3 | (enc_color1[0] >> 2));
			avg_color_quant1[1] = (byte)(enc_color1[1] << 3 | (enc_color1[1] >> 2));
			avg_color_quant1[2] = (byte)(enc_color1[2] << 3 | (enc_color1[2] >> 2));
			avg_color_quant2[0] = (byte)(enc_color2[0] << 3 | (enc_color2[0] >> 2));
			avg_color_quant2[1] = (byte)(enc_color2[1] << 3 | (enc_color2[1] >> 2));
			avg_color_quant2[2] = (byte)(enc_color2[2] << 3 | (enc_color2[2] >> 2));

			// Pack bits into the first word. 
			// see above for format 

			compressed1_flipA[0] = 0;
			PUTBITSHIGH(compressed1_flipA, diffbit, 1, 33);
			PUTBITSHIGH(compressed1_flipA, enc_color1[0], 5, 63);
			PUTBITSHIGH(compressed1_flipA, enc_color1[1], 5, 55);
			PUTBITSHIGH(compressed1_flipA, enc_color1[2], 5, 47);
			PUTBITSHIGH(compressed1_flipA, diff[0], 3, 58);
			PUTBITSHIGH(compressed1_flipA, diff[1], 3, 50);
			PUTBITSHIGH(compressed1_flipA, diff[2], 3, 42);

			int[] best_pixel_indices1_MSB = new int[1];
			int[] best_pixel_indices1_LSB = new int[1];
			int[] best_pixel_indices2_MSB = new int[1];
			int[] best_pixel_indices2_LSB = new int[1];

			// upper part of block
			flip_errA = tryalltables_3bittable4x2percep(img, width, height, startx, starty, avg_color_quant1,
					best_table1, best_pixel_indices1_MSB, best_pixel_indices1_LSB);
			// lower part of block
			flip_errA += tryalltables_3bittable4x2percep(img, width, height, startx, starty + 2, avg_color_quant2,
					best_table2, best_pixel_indices2_MSB, best_pixel_indices2_LSB);

			PUTBITSHIGH(compressed1_flipA, best_table1[0], 3, 39);
			PUTBITSHIGH(compressed1_flipA, best_table2[0], 3, 36);
			PUTBITSHIGH(compressed1_flipA, 1, 1, 32);

			best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
			best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);

			compressed2_flipA[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16)
									| (best_pixel_indices1_LSB[0] & 0xffff);
		} else {
			diffbit = 0;
			// The difference is bigger than what fits in 555 plus delta-333, so we will have
			// to deal with 444 444.
			eps = (float)0.0001;

			enc_color1[0] = (int)(((float)avg_color42_float1[0] / (17.0)) + 0.5f + eps);
			enc_color1[1] = (int)(((float)avg_color42_float1[1] / (17.0)) + 0.5f + eps);
			enc_color1[2] = (int)(((float)avg_color42_float1[2] / (17.0)) + 0.5f + eps);
			enc_color2[0] = (int)(((float)avg_color42_float2[0] / (17.0)) + 0.5f + eps);
			enc_color2[1] = (int)(((float)avg_color42_float2[1] / (17.0)) + 0.5f + eps);
			enc_color2[2] = (int)(((float)avg_color42_float2[2] / (17.0)) + 0.5f + eps);

			avg_color_quant1[0] = (byte)(enc_color1[0] << 4 | enc_color1[0]);
			avg_color_quant1[1] = (byte)(enc_color1[1] << 4 | enc_color1[1]);
			avg_color_quant1[2] = (byte)(enc_color1[2] << 4 | enc_color1[2]);
			avg_color_quant2[0] = (byte)(enc_color2[0] << 4 | enc_color2[0]);
			avg_color_quant2[1] = (byte)(enc_color2[1] << 4 | enc_color2[1]);
			avg_color_quant2[2] = (byte)(enc_color2[2] << 4 | enc_color2[2]);

			// Pack bits into the first word. 
			// see above for format 

			compressed1_flipA[0] = 0;
			PUTBITSHIGH(compressed1_flipA, diffbit, 1, 33);
			PUTBITSHIGH(compressed1_flipA, enc_color1[0], 4, 63);
			PUTBITSHIGH(compressed1_flipA, enc_color1[1], 4, 55);
			PUTBITSHIGH(compressed1_flipA, enc_color1[2], 4, 47);
			PUTBITSHIGH(compressed1_flipA, enc_color2[0], 4, 59);
			PUTBITSHIGH(compressed1_flipA, enc_color2[1], 4, 51);
			PUTBITSHIGH(compressed1_flipA, enc_color2[2], 4, 43);

			int[] best_pixel_indices1_MSB = new int[1];
			int[] best_pixel_indices1_LSB = new int[1];
			int[] best_pixel_indices2_MSB = new int[1];
			int[] best_pixel_indices2_LSB = new int[1];

			// upper part of block
			flip_errA = tryalltables_3bittable4x2percep(img, width, height, startx, starty, avg_color_quant1,
					best_table1, best_pixel_indices1_MSB, best_pixel_indices1_LSB);
			// lower part of block
			flip_errA += tryalltables_3bittable4x2percep(img, width, height, startx, starty + 2, avg_color_quant2,
					best_table2, best_pixel_indices2_MSB, best_pixel_indices2_LSB);

			PUTBITSHIGH(compressed1_flipA, best_table1[0], 3, 39);
			PUTBITSHIGH(compressed1_flipA, best_table2[0], 3, 36);
			PUTBITSHIGH(compressed1_flipA, 1, 1, 32);

			best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
			best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);

			compressed2_flipA[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16)
									| (best_pixel_indices1_LSB[0] & 0xffff);
		}

		int[] compressed1_normC = new int[1], compressed2_normC = new int[1];
		int[] compressed1_flipC = new int[1], compressed2_flipC = new int[1];
		//		byte[] avg_color_quant1= new byte[3], avg_color_quant2= new byte[3];

		//		float[] avg_color_float1= new float[3],avg_color_float2= new float[3];
		//		int[] enc_color1= new int[3], enc_color2= new int[3], diff= new int[3];
		//		int min_error=255*255*8*3;
		//		int best_table_indices1=0, best_table_indices2=0;
		//		int[] best_table1=new int[1], best_table2=new int[1];
		//		int diffbit;

		int norm_errC = 0;
		int flip_errC = 0;

		// First try normal blocks 2x4:
		//		computeAverageColor2x4noQuantFloat(img,width,height,startx,starty,avg_color_float1);
		//		computeAverageColor2x4noQuantFloat(img,width,height,startx+2,starty,avg_color_float2);

		// First test if avg_color1 is similar enough to avg_color2 so that
		// we can use differential coding of colors. 

		//		float eps;

		byte[] dummyB = new byte[3];
		float[] dummyF = new float[3];
		if (perceptual) {
			quantize555ColorCombinedPerceptual(avg_color24_float1, enc_color1, dummyB);
			quantize555ColorCombinedPerceptual(avg_color24_float2, enc_color2, dummyB);
		} else {
			quantize555ColorCombined(avg_color24_float1, enc_color1, dummyF);
			quantize555ColorCombined(avg_color24_float2, enc_color2, dummyF);
		}

		// The difference to be coded, if it fits
		diff[0] = enc_color2[0] - enc_color1[0];
		diff[1] = enc_color2[1] - enc_color1[1];
		diff[2] = enc_color2[2] - enc_color1[2];

		if ((diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4)
			&& (diff[2] <= 3)) {
			diffbit = 1;

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

			compressed1_normC[0] = 0;
			PUTBITSHIGH(compressed1_normC, diffbit, 1, 33);
			PUTBITSHIGH(compressed1_normC, enc_color1[0], 5, 63);
			PUTBITSHIGH(compressed1_normC, enc_color1[1], 5, 55);
			PUTBITSHIGH(compressed1_normC, enc_color1[2], 5, 47);
			PUTBITSHIGH(compressed1_normC, diff[0], 3, 58);
			PUTBITSHIGH(compressed1_normC, diff[1], 3, 50);
			PUTBITSHIGH(compressed1_normC, diff[2], 3, 42);

			int[] best_pixel_indices1_MSB = new int[1];
			int[] best_pixel_indices1_LSB = new int[1];
			int[] best_pixel_indices2_MSB = new int[1];
			int[] best_pixel_indices2_LSB = new int[1];

			norm_errC = 0;

			if (perceptual) {
				// left part of block
				norm_errC = tryalltables_3bittable2x4percep(img, width, height, startx, starty, avg_color_quant1,
						best_table1, best_pixel_indices1_MSB, best_pixel_indices1_LSB);
				// right part of block
				norm_errC += tryalltables_3bittable2x4percep(img, width, height, startx + 2, starty, avg_color_quant2,
						best_table2, best_pixel_indices2_MSB, best_pixel_indices2_LSB);
			} else {
				// left part of block
				norm_errC = tryalltables_3bittable2x4(img, width, height, startx, starty, avg_color_quant1, best_table1,
						best_pixel_indices1_MSB, best_pixel_indices1_LSB);
				// right part of block
				norm_errC += tryalltables_3bittable2x4(img, width, height, startx + 2, starty, avg_color_quant2,
						best_table2, best_pixel_indices2_MSB, best_pixel_indices2_LSB);
			}

			PUTBITSHIGH(compressed1_normC, best_table1[0], 3, 39);
			PUTBITSHIGH(compressed1_normC, best_table2[0], 3, 36);
			PUTBITSHIGH(compressed1_normC, 0, 1, 32);

			compressed2_normC[0] = 0;
			PUTBITS(compressed2_normC, (best_pixel_indices1_MSB[0]), 8, 23);
			PUTBITS(compressed2_normC, (best_pixel_indices2_MSB[0]), 8, 31);
			PUTBITS(compressed2_normC, (best_pixel_indices1_LSB[0]), 8, 7);
			PUTBITS(compressed2_normC, (best_pixel_indices2_LSB[0]), 8, 15);
		} else {
			diffbit = 0;
			// The difference is bigger than what fits in 555 plus delta-333, so we will have
			// to deal with 444 444.

			eps = (float)0.0001;

			if (perceptual) {
				quantize444ColorCombinedPerceptual(avg_color24_float1, enc_color1, dummyB);
				quantize444ColorCombinedPerceptual(avg_color24_float2, enc_color2, dummyB);
			} else {
				quantize444ColorCombined(avg_color24_float1, enc_color1, dummyB);
				quantize444ColorCombined(avg_color24_float2, enc_color2, dummyB);
			}

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

			compressed1_normC[0] = 0;
			PUTBITSHIGH(compressed1_normC, diffbit, 1, 33);
			PUTBITSHIGH(compressed1_normC, enc_color1[0], 4, 63);
			PUTBITSHIGH(compressed1_normC, enc_color1[1], 4, 55);
			PUTBITSHIGH(compressed1_normC, enc_color1[2], 4, 47);
			PUTBITSHIGH(compressed1_normC, enc_color2[0], 4, 59);
			PUTBITSHIGH(compressed1_normC, enc_color2[1], 4, 51);
			PUTBITSHIGH(compressed1_normC, enc_color2[2], 4, 43);

			int[] best_pixel_indices1_MSB = new int[1];
			int[] best_pixel_indices1_LSB = new int[1];
			int[] best_pixel_indices2_MSB = new int[1];
			int[] best_pixel_indices2_LSB = new int[1];

			if (perceptual) {
				// left part of block
				norm_errC = tryalltables_3bittable2x4percep(img, width, height, startx, starty, avg_color_quant1,
						best_table1, best_pixel_indices1_MSB, best_pixel_indices1_LSB);
				// right part of block
				norm_errC += tryalltables_3bittable2x4percep(img, width, height, startx + 2, starty, avg_color_quant2,
						best_table2, best_pixel_indices2_MSB, best_pixel_indices2_LSB);
			} else {
				// left part of block
				norm_errC = tryalltables_3bittable2x4(img, width, height, startx, starty, avg_color_quant1, best_table1,
						best_pixel_indices1_MSB, best_pixel_indices1_LSB);
				// right part of block
				norm_errC += tryalltables_3bittable2x4(img, width, height, startx + 2, starty, avg_color_quant2,
						best_table2, best_pixel_indices2_MSB, best_pixel_indices2_LSB);

			}

			PUTBITSHIGH(compressed1_normC, best_table1[0], 3, 39);
			PUTBITSHIGH(compressed1_normC, best_table2[0], 3, 36);
			PUTBITSHIGH(compressed1_normC, 0, 1, 32);

			compressed2_normC[0] = 0;
			PUTBITS(compressed2_normC, (best_pixel_indices1_MSB[0]), 8, 23);
			PUTBITS(compressed2_normC, (best_pixel_indices2_MSB[0]), 8, 31);
			PUTBITS(compressed2_normC, (best_pixel_indices1_LSB[0]), 8, 7);
			PUTBITS(compressed2_normC, (best_pixel_indices2_LSB[0]), 8, 15);
		}

		// Now try flipped blocks 4x2:
		//		computeAverageColor4x2noQuantFloat(img,width,height,startx,starty,avg_color_float1);
		//		computeAverageColor4x2noQuantFloat(img,width,height,startx,starty+2,avg_color_float2);

		// First test if avg_color1 is similar enough to avg_color2 so that
		// we can use differential coding of colors. 
		if (perceptual) {
			quantize555ColorCombinedPerceptual(avg_color42_float1, enc_color1, dummyB);
			quantize555ColorCombinedPerceptual(avg_color42_float2, enc_color2, dummyB);
		} else {
			quantize555ColorCombined(avg_color42_float1, enc_color1, dummyF);
			quantize555ColorCombined(avg_color42_float2, enc_color2, dummyF);
		}

		// The difference to be coded, if it fits
		diff[0] = enc_color2[0] - enc_color1[0];
		diff[1] = enc_color2[1] - enc_color1[1];
		diff[2] = enc_color2[2] - enc_color1[2];

		if ((diff[0] >= -4) && (diff[0] <= 3) && (diff[1] >= -4) && (diff[1] <= 3) && (diff[2] >= -4)
			&& (diff[2] <= 3)) {
			diffbit = 1;

			avg_color_quant1[0] = (byte)(enc_color1[0] << 3 | (enc_color1[0] >> 2));
			avg_color_quant1[1] = (byte)(enc_color1[1] << 3 | (enc_color1[1] >> 2));
			avg_color_quant1[2] = (byte)(enc_color1[2] << 3 | (enc_color1[2] >> 2));
			avg_color_quant2[0] = (byte)(enc_color2[0] << 3 | (enc_color2[0] >> 2));
			avg_color_quant2[1] = (byte)(enc_color2[1] << 3 | (enc_color2[1] >> 2));
			avg_color_quant2[2] = (byte)(enc_color2[2] << 3 | (enc_color2[2] >> 2));

			// Pack bits into the first word. 
			compressed1_flipC[0] = 0;
			PUTBITSHIGH(compressed1_flipC, diffbit, 1, 33);
			PUTBITSHIGH(compressed1_flipC, enc_color1[0], 5, 63);
			PUTBITSHIGH(compressed1_flipC, enc_color1[1], 5, 55);
			PUTBITSHIGH(compressed1_flipC, enc_color1[2], 5, 47);
			PUTBITSHIGH(compressed1_flipC, diff[0], 3, 58);
			PUTBITSHIGH(compressed1_flipC, diff[1], 3, 50);
			PUTBITSHIGH(compressed1_flipC, diff[2], 3, 42);

			int[] best_pixel_indices1_MSB = new int[1];
			int[] best_pixel_indices1_LSB = new int[1];
			int[] best_pixel_indices2_MSB = new int[1];
			int[] best_pixel_indices2_LSB = new int[1];

			if (perceptual) {
				// upper part of block
				flip_errC = tryalltables_3bittable4x2percep(img, width, height, startx, starty, avg_color_quant1,
						best_table1, best_pixel_indices1_MSB, best_pixel_indices1_LSB);
				// lower part of block
				flip_errC += tryalltables_3bittable4x2percep(img, width, height, startx, starty + 2, avg_color_quant2,
						best_table2, best_pixel_indices2_MSB, best_pixel_indices2_LSB);
			} else {
				// upper part of block
				flip_errC = tryalltables_3bittable4x2(img, width, height, startx, starty, avg_color_quant1, best_table1,
						best_pixel_indices1_MSB, best_pixel_indices1_LSB);
				// lower part of block
				flip_errC += tryalltables_3bittable4x2(img, width, height, startx, starty + 2, avg_color_quant2,
						best_table2, best_pixel_indices2_MSB, best_pixel_indices2_LSB);
			}

			PUTBITSHIGH(compressed1_flipC, best_table1[0], 3, 39);
			PUTBITSHIGH(compressed1_flipC, best_table2[0], 3, 36);
			PUTBITSHIGH(compressed1_flipC, 1, 1, 32);

			best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
			best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);

			compressed2_flipC[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16)
									| (best_pixel_indices1_LSB[0] & 0xffff);
		} else {
			diffbit = 0;
			// The difference is bigger than what fits in 555 plus delta-333, so we will have
			// to deal with 444 444.
			eps = (float)0.0001;

			if (perceptual) {
				quantize444ColorCombinedPerceptual(avg_color42_float1, enc_color1, dummyB);
				quantize444ColorCombinedPerceptual(avg_color42_float2, enc_color2, dummyB);
			} else {
				quantize444ColorCombined(avg_color42_float1, enc_color1, dummyB);
				quantize444ColorCombined(avg_color42_float2, enc_color2, dummyB);
			}

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
			compressed1_flipC[0] = 0;
			PUTBITSHIGH(compressed1_flipC, diffbit, 1, 33);
			PUTBITSHIGH(compressed1_flipC, enc_color1[0], 4, 63);
			PUTBITSHIGH(compressed1_flipC, enc_color1[1], 4, 55);
			PUTBITSHIGH(compressed1_flipC, enc_color1[2], 4, 47);
			PUTBITSHIGH(compressed1_flipC, enc_color2[0], 4, 59);
			PUTBITSHIGH(compressed1_flipC, enc_color2[1], 4, 51);
			PUTBITSHIGH(compressed1_flipC, enc_color2[2], 4, 43);

			int[] best_pixel_indices1_MSB = new int[1];
			int[] best_pixel_indices1_LSB = new int[1];
			int[] best_pixel_indices2_MSB = new int[1];
			int[] best_pixel_indices2_LSB = new int[1];

			if (perceptual) {
				// upper part of block
				flip_errC = tryalltables_3bittable4x2percep(img, width, height, startx, starty, avg_color_quant1,
						best_table1, best_pixel_indices1_MSB, best_pixel_indices1_LSB);
				// lower part of block
				flip_errC += tryalltables_3bittable4x2percep(img, width, height, startx, starty + 2, avg_color_quant2,
						best_table2, best_pixel_indices2_MSB, best_pixel_indices2_LSB);
			} else {
				// upper part of block
				flip_errC = tryalltables_3bittable4x2(img, width, height, startx, starty, avg_color_quant1, best_table1,
						best_pixel_indices1_MSB, best_pixel_indices1_LSB);
				// lower part of block
				flip_errC += tryalltables_3bittable4x2(img, width, height, startx, starty + 2, avg_color_quant2,
						best_table2, best_pixel_indices2_MSB, best_pixel_indices2_LSB);
			}

			PUTBITSHIGH(compressed1_flipC, best_table1[0], 3, 39);
			PUTBITSHIGH(compressed1_flipC, best_table2[0], 3, 36);
			PUTBITSHIGH(compressed1_flipC, 1, 1, 32);

			best_pixel_indices1_MSB[0] |= (best_pixel_indices2_MSB[0] << 2);
			best_pixel_indices1_LSB[0] |= (best_pixel_indices2_LSB[0] << 2);

			compressed2_flipC[0] = ((best_pixel_indices1_MSB[0] & 0xffff) << 16)
									| (best_pixel_indices1_LSB[0] & 0xffff);
		}

		// Now lets see which is the best table to use. Only 16 tables are possible, average and combined 
		if (norm_errA <= flip_errA && norm_errA <= norm_errC && norm_errA <= flip_errC) {
			compressed1[0] = compressed1_normA[0] | 0;
			compressed2[0] = compressed2_normA[0];
		} else if (flip_errA <= norm_errC && flip_errA <= flip_errC) {
			compressed1[0] = compressed1_flipA[0] | 1;
			compressed2[0] = compressed2_flipA[0];
		} else if (norm_errC <= flip_errC) {
			compressed1[0] = compressed1_normC[0] | 0;
			compressed2[0] = compressed2_normC[0];
		} else {
			compressed1[0] = compressed1_flipC[0] | 1;
			compressed2[0] = compressed2_flipC[0];
		}
	}

	//Finds all pixel indices for a 2x4 block using perceptual weighting of error.
	//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//address int[] pixel_indices_MSBp, int[] pixel_indices_LSBp
	@Override
	float compressBlockWithTable2x4percep(	byte[] img, int width, int height, int startx, int starty, byte[] avg_color,
											int table, int[] pixel_indices_MSBp, int[] pixel_indices_LSBp) {

		byte[] orig = new byte[3];
		byte[][] approx = new byte[4][3];
		int[] pixel_indices_MSB = new int[] {0}, pixel_indices_LSB = new int[] {0};
		int pixel_indices = 0;
		float sum_error = 0;
		int q, i;

		double wR2 = PERCEPTUAL_WEIGHT_R_SQUARED;
		double wG2 = PERCEPTUAL_WEIGHT_G_SQUARED;
		double wB2 = PERCEPTUAL_WEIGHT_B_SQUARED;

		//pre-compute
		for (q = 0; q < 4; q++) {
			approx[q][0] = (byte)CLAMP(0, (avg_color[0] & 0xff) + compressParams[table][q], 255);
			approx[q][1] = (byte)CLAMP(0, (avg_color[1] & 0xff) + compressParams[table][q], 255);
			approx[q][2] = (byte)CLAMP(0, (avg_color[2] & 0xff) + compressParams[table][q], 255);
		}

		i = 0;
		for (int x = startx; x < startx + 2; x++) {
			for (int y = starty; y < starty + 4; y++) {
				float err;
				int best = 0;
				float min_error = 255 * 255 * 3 * 16;
				orig[0] = RED(img, width, x, y);
				orig[1] = GREEN(img, width, x, y);
				orig[2] = BLUE(img, width, x, y);

				for (q = 0; q < 4; q++) {
					// Here we just use equal weights to R, G and B. Although this will
					// give visually worse results, it will give a better PSNR score. 
					err = (float)(wR2	* SQUARE(((approx[q][0] & 0xff) - (orig[0] & 0xff)))
									+ (float)wG2 * SQUARE(((approx[q][1] & 0xff) - (orig[1] & 0xff)))
									+ (float)wB2 * SQUARE(((approx[q][2] & 0xff) - (orig[2] & 0xff))));
					if (err < min_error) {
						min_error = err;
						best = q;
					}
				}
				// In order to simplify hardware, the table {-12, -4, 4, 12} is indexed {11, 10, 00, 01}
				// so that first bit is sign bit and the other bit is size bit (4 or 12). 
				// This means that we have to scramble the bits before storing them. 
				pixel_indices = scramble[best];

				PUTBITS(pixel_indices_MSB, (pixel_indices >> 1), 1, i);
				PUTBITS(pixel_indices_LSB, (pixel_indices & 1), 1, i);

				i++;

				sum_error += min_error;
			}
		}

		pixel_indices_MSBp[0] = pixel_indices_MSB[0];
		pixel_indices_LSBp[0] = pixel_indices_LSB[0];

		return sum_error;
	}

	//Finds all pixel indices for a 4x2 block using perceptual weighting of error.
	//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//address int[] pixel_indices_MSBp, int[] pixel_indices_LSBp
	@Override
	float compressBlockWithTable4x2percep(	byte[] img, int width, int height, int startx, int starty, byte[] avg_color,
											int table, int[] pixel_indices_MSBp, int[] pixel_indices_LSBp) {
		byte[] orig = new byte[3];
		byte[][] approx = new byte[4][3];
		int[] pixel_indices_MSB = new int[] {0}, pixel_indices_LSB = new int[] {0};
		int pixel_indices = 0;
		float sum_error = 0;
		int q;
		int i;
		float wR2 = (float)PERCEPTUAL_WEIGHT_R_SQUARED;
		float wG2 = (float)PERCEPTUAL_WEIGHT_G_SQUARED;
		float wB2 = (float)PERCEPTUAL_WEIGHT_B_SQUARED;

		//pre-compute
		for (q = 0; q < 4; q++) {
			approx[q][0] = (byte)CLAMP(0, (avg_color[0] & 0xff) + compressParams[table][q], 255);
			approx[q][1] = (byte)CLAMP(0, (avg_color[1] & 0xff) + compressParams[table][q], 255);
			approx[q][2] = (byte)CLAMP(0, (avg_color[2] & 0xff) + compressParams[table][q], 255);
		}

		i = 0;
		for (int x = startx; x < startx + 4; x++) {
			for (int y = starty; y < starty + 2; y++) {
				float err;
				int best = 0;
				float min_error = 255 * 255 * 3 * 16;
				orig[0] = RED(img, width, x, y);
				orig[1] = GREEN(img, width, x, y);
				orig[2] = BLUE(img, width, x, y);

				for (q = 0; q < 4; q++) {
					// Here we just use equal weights to R, G and B. Although this will
					// give visually worse results, it will give a better PSNR score. 
					err = wR2	* SQUARE((approx[q][0] & 0xff) - (orig[0] & 0xff))
							+ wG2 * SQUARE((approx[q][1] & 0xff) - (orig[1] & 0xff))
							+ wB2 * SQUARE((approx[q][2] & 0xff) - (orig[2] & 0xff));
					if (err < min_error) {
						min_error = err;
						best = q;
					}
				}
				// In order to simplify hardware, the table {-12, -4, 4, 12} is indexed {11, 10, 00, 01}
				// so that first bit is sign bit and the other bit is size bit (4 or 12). 
				// This means that we have to scramble the bits before storing them. 
				pixel_indices = scramble[best];

				PUTBITS(pixel_indices_MSB, (pixel_indices >> 1), 1, i);
				PUTBITS(pixel_indices_LSB, (pixel_indices & 1), 1, i);
				i++;

				sum_error += min_error;
			}
			i += 2;
		}

		pixel_indices_MSBp[0] = pixel_indices_MSB[0];
		pixel_indices_LSBp[0] = pixel_indices_LSB[0];

		return sum_error;
	}

	//Finds all pixel indices for a 2x4 block.
	//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	@Override
	int compressBlockWithTable2x4(	byte[] img, int width, int height, int startx, int starty, byte[] avg_color,
									int table, int[] pixel_indices_MSBp, int[] pixel_indices_LSBp) {
		int[] orig = new int[3];
		byte[][] approx = new byte[4][3];
		int[] pixel_indices_MSB = new int[] {0}, pixel_indices_LSB = new int[] {0};
		int pixel_indices = 0;
		int sum_error = 0;
		int q, i;

		i = 0;
		for (int x = startx; x < startx + 2; x++) {
			for (int y = starty; y < starty + 4; y++) {
				int err;
				int best = 0;
				int min_error = 255 * 255 * 3 * 16;
				orig[0] = RED(img, width, x, y);
				orig[1] = GREEN(img, width, x, y);
				orig[2] = BLUE(img, width, x, y);

				for (q = 0; q < 4; q++) {
					// Here we just use equal weights to R, G and B. Although this will
					// give visually worse results, it will give a better PSNR score. 
					err = SQUARE(approx[q][0] - orig[0])	+ SQUARE(approx[q][1] - orig[1])
							+ SQUARE(approx[q][2] - orig[2]);
					if (err < min_error) {
						min_error = err;
						best = q;
					}

				}
				// In order to simplify hardware, the table {-12, -4, 4, 12} is indexed {11, 10, 00, 01}
				// so that first bit is sign bit and the other bit is size bit (4 or 12). 
				// This means that we have to scramble the bits before storing them. 
				pixel_indices = scramble[best];

				PUTBITS(pixel_indices_MSB, (pixel_indices >> 1), 1, i);
				PUTBITS(pixel_indices_LSB, (pixel_indices & 1), 1, i);

				i++;
 
				sum_error += min_error;
			}
		}

		pixel_indices_MSBp[0] = pixel_indices_MSB[0];
		pixel_indices_LSBp[0] = pixel_indices_LSB[0];
		return sum_error;
	}

	//Finds all pixel indices for a 2x4 block using perceptual weighting of error.
	//Done using fixed poinit arithmetics where weights are multiplied by 1000.
	//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//unsigned 
	@Override
	int compressBlockWithTable2x4percep1000(byte[] img, int width, int height, int startx, int starty, byte[] avg_color,
											int table, int[] pixel_indices_MSBp, int[] pixel_indices_LSBp) {
		int[] orig = new int[3];
		byte[][] approx = new byte[4][3];
		int[] pixel_indices_MSB = new int[] {0}, pixel_indices_LSB = new int[] {0};
		int pixel_indices = 0;
		int sum_error = 0;
		int q, i;

		//pre-compute
		for (q = 0; q < 4; q++) {
			approx[q][0] = (byte)CLAMP(0, (avg_color[0] & 0xff) + compressParams[table][q], 255);
			approx[q][1] = (byte)CLAMP(0, (avg_color[1] & 0xff) + compressParams[table][q], 255);
			approx[q][2] = (byte)CLAMP(0, (avg_color[2] & 0xff) + compressParams[table][q], 255);
		}

		i = 0;
		for (int x = startx; x < startx + 2; x++) {
			for (int y = starty; y < starty + 4; y++) {
				int err;
				int best = 0;
				int min_error = MAXERR1000;
				orig[0] = RED(img, width, x, y);
				orig[1] = GREEN(img, width, x, y);
				orig[2] = BLUE(img, width, x, y);

				for (q = 0; q < 4; q++) {
					// Here we just use equal weights to R, G and B. Although this will
					// give visually worse results, it will give a better PSNR score. 
					err = (PERCEPTUAL_WEIGHT_R_SQUARED_TIMES1000	* SQUARE((approx[q][0] - orig[0]))
							+ PERCEPTUAL_WEIGHT_G_SQUARED_TIMES1000 * SQUARE((approx[q][1] - orig[1]))
							+ PERCEPTUAL_WEIGHT_B_SQUARED_TIMES1000 * SQUARE((approx[q][2] - orig[2])));
					if (err < min_error) {
						min_error = err;
						best = q;
					}

				}
				// In order to simplify hardware, the table {-12, -4, 4, 12} is indexed {11, 10, 00, 01}
				// so that first bit is sign bit and the other bit is size bit (4 or 12). 
				// This means that we have to scramble the bits before storing them. 
				pixel_indices = scramble[best];

				PUTBITS(pixel_indices_MSB, (pixel_indices >> 1), 1, i);
				PUTBITS(pixel_indices_LSB, (pixel_indices & 1), 1, i);

				i++;

				sum_error += min_error;
			}

		}

		pixel_indices_MSBp[0] = pixel_indices_MSB[0];
		pixel_indices_LSBp[0] = pixel_indices_LSB[0];

		return sum_error;
	}

	//Finds all pixel indices for a 4x2 block.
	//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//address int[] pixel_indices_MSBp, int[] pixel_indices_LSBp
	@Override
	int compressBlockWithTable4x2(	byte[] img, int width, int height, int startx, int starty, byte[] avg_color,
									int table, int[] pixel_indices_MSBp, int[] pixel_indices_LSBp) {
		byte[] orig = new byte[3];
		byte[][] approx = new byte[4][3];
		int[] pixel_indices_MSB = new int[] {0}, pixel_indices_LSB = new int[] {0};
		int pixel_indices = 0;
		int sum_error = 0;
		int q;
		int i;

		//pre-compute
		for (q = 0; q < 4; q++) {
			approx[q][0] = (byte)CLAMP(0, (avg_color[0] & 0xff) + compressParams[table][q], 255);
			approx[q][1] = (byte)CLAMP(0, (avg_color[1] & 0xff) + compressParams[table][q], 255);
			approx[q][2] = (byte)CLAMP(0, (avg_color[2] & 0xff) + compressParams[table][q], 255);
		}
		i = 0;
		for (int x = startx; x < startx + 4; x++) {
			for (int y = starty; y < starty + 2; y++) {
				int err;
				int best = 0;
				int min_error = 255 * 255 * 3 * 16;
				orig[0] = RED(img, width, x, y);
				orig[1] = GREEN(img, width, x, y);
				orig[2] = BLUE(img, width, x, y);

				for (q = 0; q < 4; q++) {
					// Here we just use equal weights to R, G and B. Although this will
					// give visually worse results, it will give a better PSNR score. 
					err = SQUARE((approx[q][0] & 0xff) - (orig[0] & 0xff))
							+ SQUARE((approx[q][1] & 0xff) - (orig[1] & 0xff))
							+ SQUARE((approx[q][2] & 0xff) - (orig[2] & 0xff));
					if (err < min_error) {
						min_error = err;
						best = q;
					}
				}
				// In order to simplify hardware, the table {-12, -4, 4, 12} is indexed {11, 10, 00, 01}
				// so that first bit is sign bit and the other bit is size bit (4 or 12). 
				// This means that we have to scramble the bits before storing them. 
				pixel_indices = scramble[best];

				PUTBITS(pixel_indices_MSB, (pixel_indices >> 1), 1, i);
				PUTBITS(pixel_indices_LSB, (pixel_indices & 1), 1, i);
				i++;

				

				sum_error += min_error;
			}
			i += 2;
		}

		pixel_indices_MSBp[0] = pixel_indices_MSB[0];
		pixel_indices_LSBp[0] = pixel_indices_LSB[0];

		return sum_error;
	}

	//Finds all pixel indices for a 4x2 block using perceptual weighting of error.
	//Done using fixed point arithmetics where 1000 corresponds to 1.0.
	//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//address int[] pixel_indices_MSBp, int[] pixel_indices_LSBp
	@Override
	int compressBlockWithTable4x2percep1000(byte[] img, int width, int height, int startx, int starty, byte[] avg_color,
											int table, int[] pixel_indices_MSBp, int[] pixel_indices_LSBp) {
		byte[] orig = new byte[3];
		byte[][] approx = new byte[4][3];
		int[] pixel_indices_MSB = new int[] {0}, pixel_indices_LSB = new int[] {0};
		int pixel_indices = 0;
		int sum_error = 0;
		int q;
		int i;

		//pre-compute
		for (q = 0; q < 4; q++) {
			approx[q][0] = (byte)CLAMP(0, (avg_color[0] & 0xff) + compressParams[table][q], 255);
			approx[q][1] = (byte)CLAMP(0, (avg_color[1] & 0xff) + compressParams[table][q], 255);
			approx[q][2] = (byte)CLAMP(0, (avg_color[2] & 0xff) + compressParams[table][q], 255);
		}

		i = 0;
		for (int x = startx; x < startx + 4; x++) {
			for (int y = starty; y < starty + 2; y++) {
				int err;
				int best = 0;
				int min_error = MAXERR1000;
				orig[0] = RED(img, width, x, y);
				orig[1] = GREEN(img, width, x, y);
				orig[2] = BLUE(img, width, x, y);

				for (q = 0; q < 4; q++) {
					// Here we just use equal weights to R, G and B. Although this will
					// give visually worse results, it will give a better PSNR score. 
					err = PERCEPTUAL_WEIGHT_R_SQUARED_TIMES1000 * SQUARE((approx[q][0] & 0xff) - (orig[0] & 0xff))
							+ PERCEPTUAL_WEIGHT_G_SQUARED_TIMES1000 * SQUARE((approx[q][1] & 0xff) - (orig[1] & 0xff))
							+ PERCEPTUAL_WEIGHT_B_SQUARED_TIMES1000 * SQUARE((approx[q][2] & 0xff) - (orig[2] & 0xff));
					if (err < min_error) {
						min_error = err;
						best = q;
					}
				}
				// In order to simplify hardware, the table {-12, -4, 4, 12} is indexed {11, 10, 00, 01}
				// so that first bit is sign bit and the other bit is size bit (4 or 12). 
				// This means that we have to scramble the bits before storing them. 
				pixel_indices = scramble[best];

				PUTBITS(pixel_indices_MSB, (pixel_indices >> 1), 1, i);
				PUTBITS(pixel_indices_LSB, (pixel_indices & 1), 1, i);
				i++;

				sum_error += min_error;
			}
			i += 2;

		}

		pixel_indices_MSBp[0] = pixel_indices_MSB[0];
		pixel_indices_LSBp[0] = pixel_indices_LSB[0];

		return sum_error;
	}

}