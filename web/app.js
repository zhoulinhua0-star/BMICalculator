const $ = (id) => document.getElementById(id);

const HISTORY_KEY = "bmi_history";
const MAX_HISTORY = 5;
// Visible range on the BMI scale bar
const SCALE_MIN = 10;
const SCALE_MAX = 45;

// ── Scroll Reveal via Intersection Observer ──────────────────
function initReveal() {
  const io = new IntersectionObserver(
    (entries) => {
      entries.forEach((e) => {
        if (e.isIntersecting) {
          e.target.classList.add("visible");
        }
      });
    },
    { threshold: 0.08 }
  );
  document.querySelectorAll(".reveal").forEach((el) => io.observe(el));
}

// Animate a card into view when it is shown programmatically
function revealCard(el) {
  el.hidden = false;
  void el.offsetHeight; // flush layout so the CSS transition fires from opacity:0
  el.classList.add("visible");
  el.scrollIntoView({ behavior: "smooth", block: "nearest" });
}

// ── BMI calculation logic ────────────────────────────────────
function calculateBmi(unit, height, weight) {
  if (unit === "metric") return weight / Math.pow(height / 100.0, 2);
  return (703.0 * weight) / Math.pow(height, 2);
}

function getCategory(bmi) {
  if (bmi < 18.5) return "Underweight";
  if (bmi < 25)   return "Normal weight";
  if (bmi < 30)   return "Overweight";
  if (bmi < 35)   return "Obesity Class I";
  if (bmi < 40)   return "Obesity Class II";
  return "Obesity Class III";
}

const ADVICE = {
  "Underweight":
    "Your BMI indicates that you are underweight.\n" +
    "It may suggest insufficient nutrition or low body fat.\n" +
    "Consider increasing calorie intake and engaging in strength training.\n" +
    "Consult a healthcare professional if necessary.",
  "Normal weight":
    "Your BMI falls within the normal weight range.\n" +
    "Maintain a balanced diet and regular physical activity to keep a healthy lifestyle.",
  "Overweight":
    "Your BMI indicates that you are overweight.\n" +
    "Consider improving diet quality and increasing physical activity.\n" +
    "Weight management may reduce potential health risks.",
  "Obesity Class I":
    "Your BMI falls into Obesity Class I.\n" +
    "Lifestyle changes including improved diet and regular exercise are recommended.\n" +
    "Consider consulting a healthcare professional.",
  "Obesity Class II":
    "Your BMI indicates Obesity Class II.\n" +
    "There is an increased risk of serious health conditions.\n" +
    "Professional medical advice is strongly recommended.",
  "Obesity Class III":
    "Your BMI indicates Obesity Class III (severe obesity).\n" +
    "This level significantly increases the risk of major health problems.\n" +
    "Immediate medical consultation is recommended.",
};

function getAdvice(category) {
  return ADVICE[category] || "";
}

function getHealthyWeightRange(unit, height) {
  let minW, maxW, weightUnit;
  if (unit === "metric") {
    const hm = height / 100;
    minW = (18.5 * hm * hm).toFixed(1);
    maxW = (24.9 * hm * hm).toFixed(1);
    weightUnit = "kg";
  } else {
    minW = (18.5 * height * height / 703).toFixed(1);
    maxW = (24.9 * height * height / 703).toFixed(1);
    weightUnit = "lbs";
  }
  return `${minW} – ${maxW} ${weightUnit}`;
}

// ── UI helpers ───────────────────────────────────────────────
function renderScale(bmi) {
  const pct = Math.min(100, Math.max(0, ((bmi - SCALE_MIN) / (SCALE_MAX - SCALE_MIN)) * 100));
  $("scaleMark").style.left = pct + "%";
}

function setHints(unit) {
  const metric = unit === "metric";
  $("heightHint").textContent = metric ? "(cm)"  : "(in)";
  $("weightHint").textContent = metric ? "(kg)"  : "(lbs)";
  $("height").placeholder     = metric ? "e.g. 175" : "e.g. 69";
  $("weight").placeholder     = metric ? "e.g. 72"  : "e.g. 160";
}

function showError(msg) {
  $("error").textContent = msg || "";
}

function showResult({ bmi, category, advice, healthyRange }) {
  $("bmiValue").textContent       = bmi;
  $("categoryPill").textContent   = category;
  $("categoryPill").dataset.cat   = category;
  $("advice").textContent         = advice;
  $("healthyRange").textContent   = healthyRange;
  renderScale(Number(bmi));
  revealCard($("resultCard"));
}

function hideResult() {
  const card = $("resultCard");
  card.hidden = true;
  card.classList.remove("visible");
  $("bmiValue").textContent     = "—";
  $("categoryPill").textContent = "—";
  $("categoryPill").dataset.cat = "";
  $("advice").textContent       = "";
  $("healthyRange").textContent = "—";
}

// ── History ──────────────────────────────────────────────────
function saveToHistory(entry) {
  const history = JSON.parse(localStorage.getItem(HISTORY_KEY) || "[]");
  history.unshift(entry);
  if (history.length > MAX_HISTORY) history.length = MAX_HISTORY;
  localStorage.setItem(HISTORY_KEY, JSON.stringify(history));
}

function renderHistory() {
  const history = JSON.parse(localStorage.getItem(HISTORY_KEY) || "[]");
  const card = $("historyCard");
  if (!history.length) {
    card.hidden = true;
    card.classList.remove("visible");
    return;
  }
  $("historyList").innerHTML = history
    .map((e) => {
      const date = new Date(e.ts).toLocaleString(undefined, {
        month: "short", day: "numeric",
        hour: "2-digit", minute: "2-digit",
      });
      return `<div class="historyItem">
        <span class="historyDate">${date}</span>
        <span class="historyBmi">${e.bmi}</span>
        <span class="historyPill" data-cat="${e.category}">${e.category}</span>
      </div>`;
    })
    .join("");
  revealCard(card);
}

// ── Input normalisation ──────────────────────────────────────
function normalizeNum(s) {
  return String(s).trim().replace(/\s+/g, "").replace(",", ".");
}

// ── Calculate ────────────────────────────────────────────────
async function calculate() {
  showError("");

  const unit      = $("unit").value;
  const heightRaw = normalizeNum($("height").value);
  const weightRaw = normalizeNum($("weight").value);

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

  $("calcBtn").disabled    = true;
  $("calcBtn").textContent = "Calculating…";
  try {
    const bmiNum      = calculateBmi(unit, height, weight);
    const category    = getCategory(bmiNum);
    const advice      = getAdvice(category);
    const healthyRange = getHealthyWeightRange(unit, height);
    saveToHistory({ ts: Date.now(), bmi: bmiNum.toFixed(2), category, unit, height, weight });
    renderHistory();
    showResult({ bmi: bmiNum.toFixed(2), category, advice, healthyRange });
  } catch {
    showError("Something went wrong. Please try again.");
    hideResult();
  } finally {
    $("calcBtn").disabled    = false;
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

// ── Init ─────────────────────────────────────────────────────
document.addEventListener("DOMContentLoaded", () => {
  initReveal();
  setHints("metric");

  // Segmented unit control
  document.querySelectorAll(".seg").forEach((btn) => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".seg").forEach((b) => b.classList.remove("active"));
      btn.classList.add("active");
      const val = btn.dataset.val;
      $("unit").value = val;
      setHints(val);
    });
  });

  $("bmiForm").addEventListener("submit", (e) => {
    e.preventDefault();
    calculate();
  });
  $("resetBtn").addEventListener("click", resetAll);
  $("clearHistoryBtn").addEventListener("click", () => {
    localStorage.removeItem(HISTORY_KEY);
    renderHistory();
  });

  renderHistory();
  $("height").focus();
});
