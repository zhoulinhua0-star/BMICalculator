import java.util.Scanner;

public class BMICalculator {
    private static final Scanner scanner = new Scanner(System.in);
    private int input;
    private double height;
    private double weight;
    private double bmi;

    public static void main(String[] args) {
        BMICalculator calc = new BMICalculator();
        calc.readUserInput();
        calc.calculateBMI();
    }
    public void readUserInput() {
        System.out.println("------ BMI Calculator ------\n");
        System.out.println("BMI measures body fat based on height and weight. It is commonly used to classify underweight, overweight, and obesity.\n");
        System.out.println("Select unit system:\n1. Metric (cm, kg)\n2. US (inches, lbs)");

        input = scanner.nextInt();
        System.out.print("\nEnter height: ");
        height = scanner.nextDouble();
        System.out.print("Enter weight: ");
        weight = scanner.nextDouble();
    }

    public void calculateBMI() {
        if (input == 1) {
            bmi = weight / Math.pow(height / 100, 2);
        } else if (input == 2) {
            bmi = (703 * weight) / Math.pow(height, 2);
        } else {
            System.out.println("Invalid input. Try again!");
        }
        System.out.println("\nYour BMI is: " + String.format("%.2f", bmi));
        System.out.println("\nCategory: " + getCategory(bmi));
        System.out.println("\nHealth Advice:\n" + getAdvice(getCategory(bmi)));
    }

    public String getCategory(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } 
        if (bmi >= 18.5 && bmi < 25) {
            return "Normal weight";
        } 
        if (bmi >= 25 && bmi < 30) {
            return "Overweight";
        } 
        if (bmi >= 30 && bmi < 35) {
            return "Obesity Class I";
        }
        if (bmi >= 35 && bmi < 40) {
            return "Obesity Class II";
        }
        return "Obesity Class III";
    }

    public String getAdvice(String category) {
        if (category.equals("Underweight")) {
            return "Your BMI indicates that you are underweight.\n" +
                    "It may suggest insufficient nutrition or low body fat.\n" +
                    "Consider increasing calorie intake and engaging in strength training.\n" +
                    "Consult a healthcare professional if necessary.";
        }
        if (category.equals("Normal weight")) {
            return "Your BMI falls within the normal weight range.\n" +
                    "Maintain a balanced diet and regular physical activity to keep a healthy lifestyle.";
        }
        if (category.equals("Overweight")) {
            return "Your BMI indicates that you are overweight.\n" +
                    "Consider improving diet quality and increasing physical activity.\n" +
                    "Weight management may reduce potential health risks.";
        }
        if (category.equals("Obesity Class I")) {
            return "Your BMI falls into Obesity Class I.\n" +
                   "Lifestyle changes including improved diet and regular exercise are recommended.\n" +
                   "Consider consulting a healthcare professional.";
        }
        if (category.equals("Obesity Class II")) {
            return "Your BMI indicates Obesity Class II.\n" +
                 "There is an increased risk of serious health conditions.\n" +
                 "Professional medical advice is strongly recommended.";
        }
        if (category.equals("Obesity Class III")) {
            return "Your BMI indicates Obesity Class III (severe obesity).\n" +
                    "This level significantly increases the risk of major health problems.\n" +
                    "Immediate medical consultation is recommended.";
        }
        return null;
    }

}
