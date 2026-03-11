const $ = (id) => document.getElementById(id);

function calculateBmi(unit, height, weight) {
  if (unit === "metric") {
    return weight / Math.pow(height / 100.0, 2);
  }
  return (703.0 * weight) / Math.pow(height, 2);
}

function getCategory(bmi) {
  if (bmi < 18.5) return "Underweight";
  if (bmi >= 18.5 && bmi < 25) return "Normal weight";
  if (bmi >= 25 && bmi < 30) return "Overweight";
  if (bmi >= 30 && bmi < 35) return "Obesity Class I";
  if (bmi >= 35 && bmi < 40) return "Obesity Class II";
  return "Obesity Class III";
}

function getAdvice(category) {
  if (category === "Underweight") {
    return (
      "Your BMI indicates that you are underweight.\n" +
      "It may suggest insufficient nutrition or low body fat.\n" +
      "Consider increasing calorie intake and engaging in strength training.\n" +
      "Consult a healthcare professional if necessary."
    );
  }
  if (category === "Normal weight") {
    return (
      "Your BMI falls within the normal weight range.\n" +
      "Maintain a balanced diet and regular physical activity to keep a healthy lifestyle."
    );
  }
  if (category === "Overweight") {
    return (
      "Your BMI indicates that you are overweight.\n" +
      "Consider improving diet quality and increasing physical activity.\n" +
      "Weight management may reduce potential health risks."
    );
  }
  if (category === "Obesity Class I") {
    return (
      "Your BMI falls into Obesity Class I.\n" +
      "Lifestyle changes including improved diet and regular exercise are recommended.\n" +
      "Consider consulting a healthcare professional."
    );
  }
  if (category === "Obesity Class II") {
    return (
      "Your BMI indicates Obesity Class II.\n" +
      "There is an increased risk of serious health conditions.\n" +
      "Professional medical advice is strongly recommended."
    );
  }
  if (category === "Obesity Class III") {
    return (
      "Your BMI indicates Obesity Class III (severe obesity).\n" +
      "This level significantly increases the risk of major health problems.\n" +
      "Immediate medical consultation is recommended."
    );
  }
  return "";
}

function setHints(unit) {
  const metric = unit === "metric";
  $("heightHint").textContent = metric ? "(cm)" : "(inches)";
  $("weightHint").textContent = metric ? "(kg)" : "(lbs)";
  $("height").placeholder = metric ? "e.g. 175" : "e.g. 69";
  $("weight").placeholder = metric ? "e.g. 72" : "e.g. 160";
}

function showError(msg) {
  $("error").textContent = msg || "";
}

function showResult({ bmi, category, advice }) {
  $("resultCard").hidden = false;
  $("bmiValue").textContent = bmi;
  $("categoryPill").textContent = category;
  $("advice").textContent = advice;

  // Light touch category coloring
  const pill = $("categoryPill");
  pill.dataset.cat = category;
}

function hideResult() {
  $("resultCard").hidden = true;
  $("bmiValue").textContent = "—";
  $("categoryPill").textContent = "—";
  $("advice").textContent = "";
  $("categoryPill").dataset.cat = "";
}

function normalizeNumberString(s) {
  // Accept commas or extra spaces: "1,75" -> "1.75" would be ambiguous in some locales
  // For this simple app: just remove spaces and replace comma with dot.
  return String(s).trim().replace(/\s+/g, "").replace(",", ".");
}

async function calculate() {
  showError("");

  const unit = $("unit").value;
  const heightRaw = normalizeNumberString($("height").value);
  const weightRaw = normalizeNumberString($("weight").value);

  if (!heightRaw || !weightRaw) {
    showError("Please enter both height and weight.");
    hideResult();
    return;
  }

  const height = Number(heightRaw);
  const weight = Number(weightRaw);

  if (!Number.isFinite(height) || !Number.isFinite(weight)) {
    showError("Please enter valid numbers for height and weight.");
    hideResult();
    return;
  }
  if (height <= 0 || weight <= 0) {
    showError("Height and weight must be greater than 0.");
    hideResult();
    return;
  }

  $("calcBtn").disabled = true;
  $("calcBtn").textContent = "Calculating…";

  try {
    const bmiNum = calculateBmi(unit, height, weight);
    const category = getCategory(bmiNum);
    const advice = getAdvice(category);
    showResult({ bmi: bmiNum.toFixed(2), category, advice });
  } catch (e) {
    showError("Something went wrong. Please try again.");
    hideResult();
  } finally {
    $("calcBtn").disabled = false;
    $("calcBtn").textContent = "Calculate BMI";
  }
}

function resetAll() {
  showError("");
  hideResult();
  $("height").value = "";
  $("weight").value = "";
  $("height").focus();
}

document.addEventListener("DOMContentLoaded", () => {
  setHints($("unit").value);
  $("unit").addEventListener("change", (e) => setHints(e.target.value));
  $("bmiForm").addEventListener("submit", (e) => {
    e.preventDefault();
    calculate();
  });
  $("resetBtn").addEventListener("click", resetAll);

  // Nice UX: focus first field
  $("height").focus();
});
