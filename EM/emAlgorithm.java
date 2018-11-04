import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class emAlgorithm {
	//global variables
	private static int K = 3;
	private static double[] pi = new double[K];
	private static double[] miu = new double[K];
	private static double[] sigma = new double[K];
	
	public static void main(String args[]){
		double[] dataArray = parseData().stream().mapToDouble(Double::doubleValue).toArray();		
		long startTime = System.currentTimeMillis();
		System.out.println("------The EM Begins------");
		//the variance is not fixed
		executeEm(true, dataArray);
		//the variance is fixed
		executeEm(false, dataArray);
		long endTime = System.currentTimeMillis();
		System.out.println("------The EM Ends-----");
		System.out.println("The Comssumption Time is: " + (endTime - startTime));
	}
	
	//read data from the text, and then store them into a list
	public static ArrayList<Double> parseData(){
		
		ArrayList<Double> data = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("em_data.txt"));
			String line = "";
			try {
				while((line = br.readLine()) != null){
					data.add(Double.parseDouble(line));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	
	public static void executeEm(boolean fixed, double[] data){
		int its = 50;
		int method1 = 1;
		int method2 = 2;
		EM(its, fixed, data,method1);
		EM(its, fixed, data,method2);
		System.out.println("The Log Likelihood Is: " + logLikelihood(data));
		System.out.println();
		System.out.println("************************");
	}
	
	//
	public static void EM(int its, boolean fixed, double[] data, int method){
		int size = data.length;
        double[][] rsp = new double[size][K];
        initialize(fixed, method, data);
        while (its-- > 0) {
            //2. E Step -- Evaluate the responsibilities using current parameters
            for (int k = 0; k < K; k++) {
                for (int n = 0; n < size; n++) {
                    double diff = data[n] - miu[k];
                    rsp[n][k] = pi[k] * Math.exp(-diff * diff / (2 * sigma[k])) / Math.sqrt(sigma[k]);
                }
            }
            for (int n = 0; n < size; n++) {
                double denominator = 0;
                for (int k = 0; k < K; k++) denominator += rsp[n][k];
                for (int k = 0; k < K; k++) rsp[n][k] /= denominator;
            }

            //3. M Step -- Re-estimate the parameters using the current responsibilities.
            //Calculate N[k]
            double[] N = new double[K];
            for (int k = 0; k < K; k++) {
                for (int n = 0; n < size; n++) N[k] += rsp[n][k];
            }
            //Calculate the mean for each cluster component
            for (int k = 0; k < K; k++) {
                for (int n = 0; n < size; n++) miu[k] += rsp[n][k] * data[n];
                miu[k] /= N[k];
            }
            //Calculate the covariance for each cluster component
            if (!fixed) {
                for (int k = 0; k < K; k++) {
                    for (int n = 0; n < size; n++) {
                        double diff = data[n] - miu[k];
                        sigma[k] += rsp[n][k] * diff * diff;
                    }
                    sigma[k] /= N[k];
                }
            }
            //Calculate the mixing coefficient for each cluster component
            for (int k = 0; k < K; k++) pi[k] = N[k] / size;
        }
	}
	// compute the log likehood
	public static double logLikelihood(double[] data) {
        double likehood = 0;
        for (double x : data) {
            double tmp = 0;
            for (int k = 0; k < K; k++) {
                double diff = x - miu[k];
                tmp += pi[k] * Math.exp(-diff * diff / (2 * sigma[k])) / Math.sqrt(2 * Math.PI * sigma[k]);
            }
            likehood += Math.log(tmp);
        }
        return likehood;
    }
	
	//initialize all the parameters
	public static void initialize(boolean fixed, int method, double[] data){
		Random rand = new Random();
		//double sum = 0.0;
		//initialize the miu
		if(method == 1){
			System.out.println("The First Way of Initialization miu");
			for(int i = 0; i < K; i++){
				miu[i] = data[rand.nextInt(data.length)];
				System.out.print("miu[" + i + "] : " + miu[i]);
				System.out.println();
			}
		}else{
			System.out.println();
			System.out.println("The Second Way of Initialization miu");
			for(int i = 0; i < K; i++){
				miu[i] = 0.9 * data[rand.nextInt(data.length)] + 0.1;
				System.out.print("miu[" + i + "] : " + miu[i]);
				System.out.println();
			}
		}
		//initialize the pi
		System.out.println();
		System.out.println("The Initialization of the pi");
		for(int i = 0; i < K; i++){
			pi[i] = rand.nextDouble();			
		}
		double sum = Arrays.stream(pi).sum();
		for(int i = 0; i < K; i++){
			pi[i] /= sum;		
			System.out.print("pi[" + i + "] : " + pi[i]);
			System.out.println();
		}					
		// initialize the sigma
		System.out.println();
		System.out.println("The Initialization of the sigma");
		for(int i = 0; i < K; i++){
			double mean = miu[i];			
			sigma[i] = fixed ? 1 : Arrays.stream(data).map(d -> (d - mean) * (d - mean)).average().orElse(1);
			System.out.print("sigma[" + i + "] : " + sigma[i] + " ");
			System.out.println();
		}
	}

}
