# Watermark Overlay Engine (Kotlin)

A command-line image processing utility built in Kotlin that handles dynamic bitmap blending, color-channel extraction, and alpha-weight compositing.

This application was developed as a milestone project within the **Hyperskill / JetBrains Academy** Kotlin Core curriculum.

- **Course Project Page:** https://hyperskill.org/projects/222
- **My Hyperskill Profile:** https://hyperskill.org/profile/629496713

---

## Key Features
- **Pixel-Level Matrix Blending:** Implemented custom mathematical transparency algorithms to blend background and watermark images based on adjustable opacity percentage weights.
- **Alpha-Channel Recognition:** Processes complex 24-bit (RGB) and 32-bit (ARGB) images, dynamically reading alpha transparency layers to apply clean overlays.
- **Custom Color Masking:** Built a background-transparency feature allowing users to specify a specific "key color" (e.g., pure green/chroma key) on a watermark to mask out completely during blending.
- **Robust File-System Sanitization:** Features a highly resilient input pipeline validating file existence, dimensions mismatch, pixel bounds, and unsupported color configurations to prevent runtime crashes.
- **Dynamic Positioning:** Supports both standard full-screen tiled watermarking and precise single-point grid coordinate mapping (X, Y coordinate calculations).

## Tech Stack
- Kotlin (JVM)
- Java Advanced Imaging API (`java.awt.image.BufferedImage`, `javax.imageio.ImageIO`)

## How to Run
1. Open the project root folder in IntelliJ IDEA.
2. Run the `main()` function inside `Main.kt`.
3. Follow the console prompts to input your background image path, watermark path, and transparency settings.