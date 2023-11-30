package etcpack;


/**
 * Amazing...
 * https://github.com/wolfpld/etcpak/commit/da85020e690890f4356d42ab5802e4f957f220fd?diff=unified
 * https://nahjaeho.github.io/papers/SA20/QUICKETC2_SA20.pdf
 * https://nahjaeho.github.io/papers/SA20/QUICKETC2_SA20_slides.pdf
 * 
 */
public class QuickETC  extends ETCPack {

	//Compress a block with ETC2 RGB
	//NO WARRANTY --- SEE STATEMENT IN TOP OF FILE (C) Ericsson AB 2005-2013. All Rights Reserved.
	//address  int &compressed1,  int &compressed2
	@Override
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
	@Override
	void compressBlockETC2FastPerceptual(byte[] img, byte[] imgdec,int width,int height,int startx,int starty,  int[] compressed1,  int[] compressed2)
	{
		
		// get a list of 16 luma values			
		
		//Y = 0.299R + 0.587G + 0.114B
		float LR=0.299f;
		float LG=0.587f;
		float LB=0.114f;		
		float[] lumas = new float[16];
		float lmin = 1f;
		float lmax = 0f;
		// Load luma data into 1D array
		for(int l = 0, y = 0; y<4; y++)
		   for(int x = 0; x<4; x++) {
			   
			   byte r= img[3*width*(starty+y) + 3*(startx+x) + R];
			   int ri = r&0xff;
			   float fr = (ri/255f);
			   
			   float luma = ((img[3*width*(starty+y) + 3*(startx+x) + R]&0xff)/255f)*LR
					   +((img[3*width*(starty+y) + 3*(startx+x) + G]&0xff)/255f)*LG
					   +((img[3*width*(starty+y) + 3*(startx+x) + B]&0xff)/255f)*LB;
			   
			   if(luma < lmin)
				   lmin = luma;
			   
			   if(luma > lmax)
				   lmax = luma;
			   
			   lumas[l++] = luma;
		   }
		
		float LD = lmax - lmin;
		
		// modify it into a luma differnce range (top value - bottom value)		
		// luma_diff		
		
		float T1=0.03f;
		float T2=0.09f;
		float T3=0.38f;
				
		//very-low contrast (luma_diff≤T1),
		//low-contrast (T1<luma_diff≤T2),
		//mid-contrast (T2<luma_diff<T3), 
		//and high-contrast (luma_diff≥T3
				
		

		// mid = etc1 
		
		// high = ETC1 or T/H do both and see which is better
		// high can be set to ETC1 for the non best quality system
		
		boolean planar = false;
		boolean etc1 = false;
						 
		if(LD <= T1) {//very-low contrast 
			// very low = planar mode
			planar = true;
		} else if(T1<LD && LD<=T2) {//low-contrast
			// In the case of low-contrast blocks, we check whether a block can be smoothly expressed by the base colors at the corners of the block.
			//If a pair of two corresponding corner pixels (top-left and bottom-right, or bottom-left and top-right) has the min and max luma 
			//values, we exploit a high possibility that the other pixels can be properly interpolated in the planar mode. 
			//Otherwise, we perform traditional ETC1 compression. 
			//The corner index pairs {(0, 15) & (3, 12)} and the pixel indices corresponding to
			//the min/max value
			
			if(((lumas[0]==lmin && lumas[15]==lmax) || (lumas[0]==lmax && lumas[15]==lmin))
				|| ((lumas[3]==lmin && lumas[12]==lmax) || (lumas[3]==lmax && lumas[12]==lmin))) {
				planar = true;
			} else {
				etc1 = true;
			}			
		} else if(T2<LD && LD<T3) {//mid-contrast
			// mid = etc1 
			etc1 = true;
		} else if(LD>=T3) {//and high-contrast
			// high = ETC1 or T/H do both and see which is better
			// high can be set to ETC1 for the non best quality system
			etc1 = true;
		}
		
		if(planar) {
			int[] planar57_word1=new int[1];
			int[] planar57_word2=new int[1];
			int[] planar_word1=new int[1];
			int[] planar_word2=new int[1];
			compressBlockPlanar57(img, width, height, startx, starty, planar57_word1, planar57_word2);
			stuff57bits(planar57_word1[0], planar57_word2[0], planar_word1, planar_word2);
			compressed1[0] = planar_word1[0];
			compressed2[0] = planar_word2[0];
		} else {
			int[] etc1_word1=new int[1];
			int[] etc1_word2=new int[1];
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
}