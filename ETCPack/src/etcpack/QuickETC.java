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
		
		
		// get a list of 16 luma values		
		// and at the same time work out if there exists a punch through alpha
		boolean alphaExists = false;
		
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
			   float luma = ((img[3*width*(starty+y) + 3*(startx+x) + R]&0xff)/255f)*LR
					   +((img[3*width*(starty+y) + 3*(startx+x) + G]&0xff)/255f)*LG
					   +((img[3*width*(starty+y) + 3*(startx+x) + B]&0xff)/255f)*LB;
			   
			   if(luma < lmin)
				   lmin = luma;
			   
			   if(luma > lmax)
				   lmax = luma;
			   
			   lumas[l++] = luma;
			   
			   
			   if((format==FORMAT.ETC2PACKAGE_RGBA1||format==FORMAT.ETC2PACKAGE_sRGBA1) && alphaimg[1*width*(starty+y) + 1*(startx+x) + 0]<127) {
				   alphaExists = true;
			   }
				   
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
				
				
		// go alpha paths if there is an alpha 
		if (alphaExists) {
			//If a punch through exists we can only use ETC diff or T or H
			
			int[] etc1_word1=new int[1];
			int[] etc1_word2=new int[1];
			compressBlockDifferentialWithAlpha(true,img,alphaimg, imgdec,width,height,startx,starty,etc1_word1,etc1_word2);	
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