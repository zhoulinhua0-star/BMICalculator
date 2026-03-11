# BMICalculator

A pragmatic BMI calculator that accurately calculates BMI and provides personal health advice based on height and weight.

## Web version (open in your browser)

### Option A (works everywhere, even offline): open the standalone website file

Anyone can open this file directly (no server needed):

- `BMICalculator/web/index.html`

You can zip the `web/` folder and share it—any random computer can open `index.html` and use the calculator.

### Option B: run the Java web server (gives you a URL like a “real” site)

After you start the server, open the website here (on the same computer):

- **Website**: [http://localhost:8080/](http://localhost:8080/)

### Run (Web)

From the `BMICalculator/` folder:

```bash
javac BMICalculator.java BMICalculatorWeb.java
java BMICalculatorWeb
```

### Access from another device (same Wi‑Fi / LAN)

1. Run the server (default binds to `0.0.0.0`, which allows LAN access)
2. On your phone/another computer on the same Wi‑Fi, open:

- `http://<your-computer-ip>:8080/`

When you start the server, it will print one or more “Open (same Wi‑Fi / LAN)” URLs you can click/copy.

If it still won’t load, it’s usually because of firewall settings on the host machine/router.

### Make it accessible to everyone (public internet)

To let anyone on the internet access it, you need **one** of these:

- **Port forwarding**: forward your router’s public port `8080` to your computer’s local port `8080` (and allow it through your firewall)
- **A tunnel** (recommended): run a tunneling tool (e.g. Cloudflare Tunnel / ngrok) which gives you a public HTTPS URL pointing to your local server

### Option C (recommended for “anyone, anytime”): publish a permanent public URL (GitHub Pages)

Because the `web/` version is now fully static, you can host it as a permanent public site.
High-level steps:

- Put this project on GitHub
- In GitHub: Settings → Pages
- Set **Deploy from branch**, and choose the `/web` folder as the site root (GitHub Pages)
- You’ll get a public URL that anyone can open

### Optional: change port

```bash
java BMICalculatorWeb --port 8081
```

### Optional: bind host address

- Listen on all interfaces (LAN): `java BMICalculatorWeb --host 0.0.0.0`
- Listen only on this computer: `java BMICalculatorWeb --host 127.0.0.1`

### What it includes (Web)

- Metric: cm + kg
- US: inches + lbs
- BMI value (2 decimals), category, and health advice matching the console version

## Console version

This project is a simple console-based BMI (Body Mass Index) calculator written in Java.
It allows users to calculate their BMI using either the Metric system (cm, kg) or the US system (inches, lbs).
