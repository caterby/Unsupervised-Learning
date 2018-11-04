/*** Author :Vibhav Gogate
The University of Texas at Dallas
*****/


import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
 

public class KMeans {
    public static void main(String [] args){
    //Kmeans begins
    long startTime = System.currentTimeMillis();  
    System.out.println("------Kmeans Begins------");       
	if (args.length < 3){
	    System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
	    return;
	}
	try{
	    BufferedImage originalImage = ImageIO.read(new File(args[0]));
	    int k=Integer.parseInt(args[1]);
	    if(k <= 0 || k > originalImage.getHeight() * originalImage.getWidth()){
	    	System.out.println("input the wrong cluster number");
	    }	
	    BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
	    ImageIO.write(kmeansJpg, "jpg", new File(args[2])); 
	    double compRate = compressionRate(originalImage,k);
	    System.out.println("The comprssion rate: " + "K = " + k + " is " + (float)compRate);
	    
	}catch(IOException e){
	    System.out.println(e.getMessage());
	}
	//Kmeans ends
	long endTime = System.currentTimeMillis(); 
	System.out.println("Kmeans Running Time = " + (endTime - startTime) + "ms");
	System.out.println("------Kmeans Ends------"); 
	System.out.println();
    }
    
    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
	   int w=originalImage.getWidth();
	   int h=originalImage.getHeight();
	   BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
	   Graphics2D g = kmeansImage.createGraphics();
	   g.drawImage(originalImage, 0, 0, w, h, null);
	   // Read rgb values from the image
	   int[] rgb=new int[w*h];
	   int count=0;
	   for(int i=0;i<w;i++){
	       for(int j=0;j<h;j++){
		   rgb[count++]=kmeansImage.getRGB(i,j);
	    }
	}
	// Call kmeans algorithm: update the rgb values
	kmeans(rgb,k);

	// Write the new rgb values to the image
	count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		kmeansImage.setRGB(i,j,rgb[count++]);
	    }
	}
	return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k){
    	
    	//initialize the centers and clusters
      Random rand = new Random();
    	int[] centers = new int[rgb.length];
		for (int i = 0; i < rgb.length; i++){
    		centers[i] = (int) rand.nextInt(k);
    	}  
    	double[] clusters = new double[k];
		for (int i = 0; i < k; i++){
    		clusters[i] = rgb[rand.nextInt(rgb.length)];
    	}
    	int iterate =20;
    	while(iterate-- > 0){
    		for(int i = 0; i < rgb.length; i++){
    			double distance = Math.abs(rgb[i] - clusters[centers[i]]);
    			for(int j = 0; j < k; j++){
    				if(Math.abs(rgb[i] - clusters[j]) < distance)
    					centers[i] = j;
    			}
    		}
    		
    		for(int i = 0; i < k; i++){
    			double average = 0;
    			int count = 0;
    			for(int j = 0; j < rgb.length; j++){
    				if(centers[j] == i){
    					average += rgb[j];
    					count++;
    				}
    			}
    			clusters[i] = average / count;
    		}
    	}
    	
    	for(int i = 0; i < rgb.length; i++) 
    		rgb[i] = (int) clusters[centers[i]]; 		
    }
    
    // compute the compression rate of the k-means
    private static double compressionRate(BufferedImage originalImage, int k){
    	double rate = 0.0;
    	int w=originalImage.getWidth();
    	int h=originalImage.getHeight();
    	int N = w*h;
    	rate = (k * 32 + N * Math.ceil(Math.log(k) / Math.log(2))) / (N * 32);
    	return rate;
    }
  
    //initialize the centers
	/*
    private static int[] init_centers(int[] rgb, int k){
    	
    	Random rand = new Random();
    	int[] initCenters = new int[rgb.length];
    	for (int i = 0; i < k; i++){
    		initCenters[i] = rgb[rand.nextInt(rgb.length)];
    	}   	
    	return initCenters;
    }
    
    //initialize the clusters
    private static double[] init_clusters(int[] rgb, int k){
    	Random rand = new Random();
    	double[] initClusters = new double[k];
    	for (int i = 0; i < rgb.length; i++){
    		initClusters[i] = (int) rand.nextInt(k);
    	}	
    	return initClusters;
    }
	*/
}
