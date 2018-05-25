import java.util.Random;

public class Ice {
	
	public static void main(String[] args) {
		int flag    = Integer.valueOf(args[0]);
		int[] years = new int[162];
		int[] days  = {118,151,121,96,110,117,132,104,125,118,125,123,110,127,131,99,126,
		               144,136,126,91,130,62,112,99,161,78,124,119,124,128,131,113,88,75,
		               111,97,112,101,101,91,110,100,130,111,107,105,89,126,108,97,94,83,
		               106,98,101,108,99,88,115,102,116,115,82,110,81,96,125,104,105,124,
		               103,106,96,107,98,65,115,91,94,101,121,105,97,105,96,82,116,114,92,
		               98,101,104,96,109,122,114,81,85,92,114,111,95,126,105,108,117,112,
		               113,120,65,98,91,108,113,110,105,97,105,107,88,115,123,118,99,93,
		               96,54,111,85,107,89,87,97,93,88,99,108,94,74,119,102,47,82,53,115,
		               21,89,80,101,95,66,106,97,87,109,57,87,117,91,62,65};
		int startYear = 1855;
		
		for (int i = 0; i < 162; i++) {
			years[i] = startYear;
			startYear++;
		}
		
		if (flag == 100) {
			for (int i = 0; i < years.length; i++) {
				System.out.println(years[i] + " " + days[i]);
			}
		}
		else if (flag == 200) {
			int sampleSize = days.length;
			double sampleMean = calculateMean(days, sampleSize);
			double sampleSD = calculateSD(days, sampleMean, sampleSize);
			
			System.out.println(sampleSize);
			System.out.println(String.format("%.2f", sampleMean));
			System.out.println(String.format("%.2f", sampleSD));
		}
		else if (flag == 300) {
			double betaZero = Double.valueOf(args[1]);
			double betaOne  = Double.valueOf(args[2]);
			double meanSquaredError = calculateMSE(betaZero, betaOne, days, years);
			
			System.out.println(String.format("%.2f", meanSquaredError));
		}
		else if (flag == 400) {
			double betaZero = Double.valueOf(args[1]);
			double betaOne  = Double.valueOf(args[2]);
			double gradientOne = gradientOne(betaZero, betaOne, days, years);
			double gradientTwo = gradientTwo(betaZero, betaOne, days, years);
			
			System.out.println(String.format("%.2f", gradientOne));
			System.out.println(String.format("%.2f", gradientTwo));
		}
		else if (flag == 500) {
			double multiplyer = Double.valueOf(args[1]);
			int iterations  = Integer.valueOf(args[2]);
			double[] betaZeroT = new double[iterations + 1];
			double[] betaOneT = new double[iterations + 1];
			double[] MSE = new double[iterations + 1];
			betaZeroT[0] = 0;
			betaOneT[0] = 0;
			
			for (int i = 1; i < betaZeroT.length; i++) {
				betaZeroT[i] = betaZeroT[i - 1] - (multiplyer * gradientOne(betaZeroT[i - 1], betaOneT[i - 1], days, years));
				betaOneT[i] = betaOneT[i - 1] - (multiplyer * gradientTwo(betaZeroT[i - 1], betaOneT[i - 1], days, years));
				MSE[i] = calculateMSE(betaZeroT[i], betaOneT[i], days, years);
			}
			
			for (int j = 1; j < iterations + 1; j++) 
				System.out.println(String.format(j + " " + "%.2f" + " " + "%.2f" + " " + "%.2f", betaZeroT[j], betaOneT[j], MSE[j]));
			
			
		}
		else if (flag == 600) {
			double sampleMeanYears = calculateMean(years, years.length);
			double sampleMeanDays  = calculateMean(days, days.length);
			double betaZeroHat = 0;
			double betaOneHat  = 0;

			betaOneHat = calculateBetaOneHatTop(years, sampleMeanYears, days, sampleMeanDays) / calculateBetaOneHatBottom(years, sampleMeanYears);
			betaZeroHat = sampleMeanDays - (betaOneHat * sampleMeanYears);
			double MSE = calculateMSE(betaZeroHat, betaOneHat, days, years);
			
			System.out.println(String.format("%.2f" + " " + "%.2f" + " " + "%.2f", betaZeroHat, betaOneHat, MSE));
		}
		else if (flag == 700) {
			int year = Integer.valueOf(args[1]);
			double sampleMeanYears = calculateMean(years, years.length);
			double sampleMeanDays  = calculateMean(days, days.length);
			double betaZeroHat = 0;
			double betaOneHat  = 0;

			betaOneHat = calculateBetaOneHatTop(years, sampleMeanYears, days, sampleMeanDays) / calculateBetaOneHatBottom(years, sampleMeanYears);
			betaZeroHat = sampleMeanDays - (betaOneHat * sampleMeanYears);
			double predictedYear = betaZeroHat + (betaOneHat * year);
			
			System.out.println(String.format("%.2f", predictedYear));
		}
		else if (flag == 800) {
			double sampleMean = calculateMean(years, years.length);
			double sampleSD   = calculateSD(years, sampleMean, years.length);
			double[] newYears = new double[years.length];
			
			for (int i = 0; i < years.length; i++) 
				newYears[i] = ((years[i] - sampleMean) / sampleSD);
			
			double multiplyer = Double.valueOf(args[1]);
			int iterations  = Integer.valueOf(args[2]);
			double[] betaZeroT = new double[iterations + 1];
			double[] betaOneT = new double[iterations + 1];
			double[] MSE = new double[iterations + 1];
			betaZeroT[0] = 0;
			betaOneT[0] = 0;
			
			for (int i = 1; i < betaZeroT.length; i++) {
				betaZeroT[i] = betaZeroT[i - 1] - (multiplyer * gradientOne1(betaZeroT[i - 1], betaOneT[i - 1], days, newYears));
				betaOneT[i] = betaOneT[i - 1] - (multiplyer * gradientTwo1(betaZeroT[i - 1], betaOneT[i - 1], days, newYears));
				MSE[i] = calculateMSE1(betaZeroT[i], betaOneT[i], days, newYears);
			}
			
			for (int j = 1; j < iterations + 1; j++) 
				System.out.println(String.format(j + " " + "%.2f" + " " + "%.2f" + " " + "%.2f", betaZeroT[j], betaOneT[j], MSE[j]));
		}
		else if (flag == 900) {
			Random rng = new Random();
			double sampleMean = calculateMean(years, years.length);
			double sampleSD   = calculateSD(years, sampleMean, years.length);
			double[] newYears = new double[years.length];
						
			for (int i = 0; i < years.length; i++) 
				newYears[i] = ((years[i] - sampleMean) / sampleSD);
			
			double multiplyer = Double.valueOf(args[1]);
			int iterations  = Integer.valueOf(args[2]);
			double[] betaZeroT = new double[iterations + 1];
			double[] betaOneT = new double[iterations + 1];
			double[] MSE = new double[iterations + 1];
			betaZeroT[0] = 0;
			betaOneT[0] = 0;
			int[] randomIndex = new int[iterations];
			
			for (int k = 0; k < randomIndex.length; k++)
				randomIndex[k] = rng.nextInt(newYears.length);
			
			for (int i = 1; i < betaZeroT.length; i++) { 
				betaZeroT[i] = betaZeroT[i - 1] - (multiplyer * gradientOne2(betaZeroT[i - 1], betaOneT[i - 1], days[randomIndex[i-1]], newYears[randomIndex[i-1]]));
				betaOneT[i] = betaOneT[i - 1] - (multiplyer * gradientTwo2(betaZeroT[i - 1], betaOneT[i - 1], days[randomIndex[i-1]], newYears[randomIndex[i-1]]));
				MSE[i] = calculateMSE2(betaZeroT[i], betaOneT[i], days, newYears);
			}
			
			for (int j = 1; j < iterations + 1; j++) 
				System.out.println(String.format(j + " " + "%.2f" + " " + "%.2f" + " " + "%.2f", betaZeroT[j], betaOneT[j], MSE[j]));
		}
		
	}
	
	private static double calculateMSE2(double betaZero, double betaOne,
			int[] days, double[] newYears) {
		double meanSquaredError = 0;
		
		for (int i = 0; i < days.length; i++) 
			meanSquaredError += Math.pow((betaZero + (betaOne * newYears[i]) - days[i]), 2);
		
		return meanSquaredError / days.length;
	}

	private static double gradientTwo2(double betaZero, double betaOne,
			int day, double year) {
		double gradientTwo = 0;
		
		gradientTwo = 2 * (betaZero + (betaOne * year) - day) * year;

		return gradientTwo;
	}

	private static double gradientOne2(double betaZero, double betaOne,
			int day, double year) {
		double gradientOne = 0;
		
		gradientOne = 2 * (betaZero + (betaOne * year) - day);

		return gradientOne;
	}

	private static double calculateMSE1(double betaZero, double betaOne,
			int[] days, double[] newYears) {
		double meanSquaredError = 0;
		
		for (int i = 0; i < days.length; i++) 
			meanSquaredError += Math.pow((betaZero + (betaOne * newYears[i]) - days[i]), 2);
		
		return meanSquaredError / days.length;
	}

	private static double gradientTwo1(double betaZero, double betaOne,
			int[] days, double[] newYears) {
		double gradientTwo = 0;
		
		for(int i = 0; i < days.length; i++)
			gradientTwo += (betaZero + (betaOne * newYears[i]) - days[i]) * newYears[i];

		return (gradientTwo * 2) / days.length;
	}

	private static double gradientOne1(double betaZero, double betaOne,
			int[] days, double[] newYears) {
		double gradientOne = 0;
		
		for(int i = 0; i < days.length; i++)
			gradientOne += (betaZero + (betaOne * newYears[i]) - days[i]);
		
		return (gradientOne * 2) / days.length;
	}

	private static double calculateBetaOneHatBottom(int[] years, double sampleMeanYears) {
		double betaOneHatBottom  = 0;
		
		for (int i = 0; i < years.length; i++) {
			betaOneHatBottom += (Math.pow((years[i] - sampleMeanYears), 2));
		}		
		
		return betaOneHatBottom;
	}

	private static double calculateBetaOneHatTop(int[] years, double sampleMeanYears, int[] days, double sampleMeanDays) {
		double betaOneHatTop  = 0;
		
		for (int i = 0; i < days.length; i++) {
			betaOneHatTop += (((years[i] - sampleMeanYears)*(days[i] - sampleMeanDays)));
		}		
		
		return betaOneHatTop;
	}

	private static double calculateSD(int[] days, double sampleMean,
			int sampleSize) {
		double sampleSD = 0;
		
		for (int j = 0; j < days.length; j++) {
			sampleSD += Math.pow(days[j] - sampleMean, 2);
		}
		sampleSD = sampleSD / (sampleSize - 1);
		
		return Math.sqrt(sampleSD);
	}

	private static double calculateMean(int[] values, int sampleSize) {
		double sampleMean = 0;
		
		for (int i = 0; i < values.length; i++)
			sampleMean += values[i];

		return sampleMean / sampleSize;
	}

	private static double calculateMSE(double betaZero, double betaOne,
			int[] days, int[] years) {
		double meanSquaredError = 0;
		
		for (int i = 0; i < days.length; i++) 
			meanSquaredError += Math.pow((betaZero + (betaOne * years[i]) - days[i]), 2);
		
		return meanSquaredError / days.length;
	}

	private static double gradientOne(double betaZero, double betaOne, int[] days,
			int[] years) {
		double gradientOne = 0;
		
		for(int i = 0; i < days.length; i++)
			gradientOne += (betaZero + (betaOne * years[i]) - days[i]);
		
		return (gradientOne * 2) / days.length;
	}

	private static double gradientTwo(double betaZero, double betaOne, int[] days,
			int[] years) {
		double gradientTwo = 0;
		
		for(int i = 0; i < days.length; i++)
			gradientTwo += (betaZero + (betaOne * years[i]) - days[i]) * years[i];

		return (gradientTwo * 2) / days.length;
	}
	
}
