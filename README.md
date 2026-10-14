# Smart Public Resource Allocator
### SEVA First Innovation Challenge — Undergraduate Team Project

A citizen-friendly civic guidance platform and deterministic public service staff workload allocation system built for Android using **Kotlin**, **Jetpack Compose**, and **Room Database**.

---

## 🏛️ Project Purpose & Overview

The **Smart Public Resource Allocator** addresses two critical civic bottlenecks in public service delivery:

1. **For Citizens:** Finding the right government department, understanding prerequisite documents, accessing verified official government portals (such as AP MeeSeva), and navigating procedures in their native language (**English & Telugu**).
2. **For Public Service Supervisors:** Intelligently and transparently allocating incoming citizen service requests across municipal and revenue staff using a **100% classical, deterministic greedy algorithm** that balances workloads, respects certifications, and adheres strictly to daily capacity limits.

> **Civic Platform Notice:** This application is a guidance and planning platform. It does not pretend to be an official government portal, issue certificates, or claim unauthorized government endorsement.

---

## 🚀 Key Features

### 1. AI-Powered Government Service Finder
- Natural-language query interface with bilingual support (English & Telugu - తెలుగు).
- Resolves questions such as:
  - *"Which department issues an Income Certificate in AP?"*
  - *"Where can I apply for a caste certificate?"*
  - *"How do I get a birth certificate?"*
  - *"What documents do I need for a ration card?"*
- **Strict Anti-Hallucination Guardrail:** All official government links and document requirements are grounded strictly in a controlled, verified service database. The AI never invents official URLs.
- **Zero-Downtime Resilience:** Automatically falls back to a high-precision classical keyword/department search when offline or if the AI API is unavailable.

### 2. Andhra Pradesh Income Certificate Demonstration
- **State:** Andhra Pradesh (All 26 districts).
- **Responsible Department:** Revenue Department.
- **Issuing Authority:** Tahsildar / Revenue Divisional Officer (RDO).
- **Official Portal:** AP MeeSeva Citizen Portal (`onlineap.meeseva.gov.in`).
- **Official Information Source:** National Government Services Portal (`services.india.gov.in`, Service #4369).
- **Statutory Fee & Time:** ₹35 user charge; 7–14 working days.
- **Actionable Link:** One-tap *"Open Official Portal"* button launching the verified government website.
- **Prominent Warning:** *"Requirements may change. Confirm latest details on official portal or with an authorized MeeSeva centre."*

### 3. Interactive Document Readiness Checklist
- Interactive *"Ready / Missing"* checklist for each service.
- Real-time progress tracking: *"You have marked X of Y items as ready."*
- Explanations on where and how to obtain each prerequisite document.
- Missing documents summary card with next steps.
- **Privacy Assurance:** Zero sensitive documents (Aadhaar, income slips) are collected or uploaded.

### 4. Application Guidance & Simulated Kendra Token Demo
- Clear, numbered application steps from document preparation to Tahsildar approval.
- Verified application-status tracking guidance.
- Simulated appointment/token booking workflow for demonstration with a mandatory notice:
  `DEMO ONLY — This is not a real government appointment.`

### 5. Classical Staff Workload Allocation Algorithm
- **100% Classical Computing:** Deterministic rule-based greedy algorithm (zero quantum or speculative claims).
- **Dataset:** 50 synthetic citizen requests across 5 categories (Revenue, Civil Supplies, Municipal, Transport, Social Welfare) scheduled across 5 public service staff members.
- **Strict Constraint Enforcement:**
  1. Department certification eligibility.
  2. Availability status.
  3. Priority weighting (Urgent > High > Medium > Low).
  4. 8.0-hour maximum daily capacity limit.
- **Comparative Benchmarking:** Compares against a naive sequential round-robin baseline, achieving **0 rule violations** and balanced workload distribution.
- **Transparent Audit Trail:** Complete human-readable rationale recorded for every recommendation.
- **Human Oversight:** Requires supervisor review and approval before finalization.

### 6. Room Database Persistence
- **`ServiceEntity`** (`services` table): Service ID, English & Telugu names, department, authority, verified URLs, fees, and processing times.
- **`DocumentChecklistItemEntity`** (`service_documents` table): Relational document requirements linked via foreign key with cascading deletes.
- **`ServiceWithChecklist`**: 1-to-many relation model with domain mappers.
- **`ServiceDao`**: Reactive `Flow` queries, multi-field search, and transactional full-service upserts.
- **Automatic Prepopulation:** Pre-seeds verified Andhra Pradesh services on first launch.

### 7. Testing & Measurement Bench (Section 14 Protocol)
- Interactive testing page with 9 structured scenarios.
- Allows team members to record and audit empirical benchmarks:
  - Search latency (< 1.0s)
  - Department accuracy (100%)
  - Link authenticity (0 invented URLs)
  - Telugu bilingual coverage (100%)
  - Workload balance improvement (Std Dev reduction)
  - Scheduling rule violations (0 violations)

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Platform** | Android (Min SDK 24, Target SDK 36) |
| **Language** | Kotlin 2.2 |
| **UI Framework** | Jetpack Compose (Material Design 3) |
| **Local Persistence** | Android Room Database 2.7 with KSP |
| **AI Integration** | Google Gemini 3.5 Flash REST API |
| **Networking** | OkHttp 4.10, Retrofit 2.12 |
| **Testing** | Robolectric 4.16, JUnit 4 |

---

## 📦 Setup & Build Instructions

### Prerequisites
- Android Studio Ladybug or later / Gradle 8+
- JDK 11 or higher
- Android SDK 36

### Build Steps
```bash
# Clone the repository
git clone <repository-url>
cd smart-public-resource-allocator

# Build the Debug APK
gradle assembleDebug

# Run Unit and Robolectric Tests
gradle :app:testDebugUnitTest
```

### API Key Configuration
The application accesses the Gemini API key via `BuildConfig.GEMINI_API_KEY` injected by the Secrets Gradle Plugin:
1. In Google AI Studio, enter your API key in the **Secrets panel** under `GEMINI_API_KEY`.
2. For local builds outside AI Studio, copy `.env.example` to `.env` and add:
   ```properties
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   ```
   *(Note: `.env` is strictly git-ignored to prevent leaking credentials).*
3. If no key is configured, the application automatically runs in **Offline Classical Mode** with full local search and allocation functionality.

---

## 👥 SEVA First Innovation Challenge Team

Developed by a 5-member undergraduate engineering team committed to citizen-centric civic technology, accessibility, and operational transparency.
