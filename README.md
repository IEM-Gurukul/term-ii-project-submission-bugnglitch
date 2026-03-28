[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/pG3gvzt-)
# PCCCS495 – Term II Project

## Project Title
Emergency Room Triage System
---

## Problem Statement (max 150 words)

Emergency rooms face life-or-death decisions every minute. Without a structured triage system, staff risk treating minor injuries while critical patients deteriorate in the waiting room. This system digitises the 5-level Emergency Severity Index (ESI) protocol, automatically assessing incoming patients — General, Trauma, and Cardiac — based on clinical vitals, assigning a priority level, and managing a sorted queue so the most critical patients are always seen first. ER nurses and coordinators can admit, track, and discharge patients through a full Java Swing GUI, while a background monitoring thread continuously alerts staff when critical patients exceed safe wait times.

---

## Target User

ER nurses and triage coordinators in hospital emergency departments who need a fast, structured system to manage patient intake and priority ordering under time pressure.
---

## Core Features

- **Automated ESI Triage Assessment** — Clinical rules assign levels 1–5 from patient vitals automatically
- **Priority Queue Management** — Patients sorted by severity, not arrival time
- **Three Patient Types** — General, Trauma, and Cardiac with type-specific triage logic
- **Background Monitoring Thread** — Alerts staff when critical patients exceed safe wait times
- **Patient Lookup & Discharge** — Search by ID, update status, remove from queue
- **File Logging & CSV Export** — All activity logged to `records/er_log.txt`
- **Full Java Swing GUI** — Dark-themed, multi-panel interface with sidebar navigation
- **Input Validation** — All clinical inputs validated with descriptive error messages

---

## OOP Concepts Used

- **Abstraction:** `Patient` is an abstract class — it defines `assessTriageLevel()` and `getClinicalSummary()` as abstract methods, forcing every patient subclass to provide its own triage logic. This prevents incomplete patient types from being admitted.

- **Inheritance:** `GeneralPatient`, `TraumaPatient`, and `CardiacPatient` all extend `Patient`, inheriting shared fields (name, age, patientId, arrivalTime, status) and overriding only what is specific to their type.

- **Polymorphism:** `PriorityQueue<Patient>` holds all three patient types together. `compareTo()` and `getClinicalSummary()` automatically dispatch to the correct subclass version at runtime without the queue needing to know which type it holds.

- **Exception Handling:** Three custom exceptions — `PatientNotFoundException` (checked), `DuplicatePatientException` (checked), and `InvalidTriageDataException` (unchecked RuntimeException). Checked exceptions force all call sites to handle errors explicitly via try-catch.

- **Collections / Threads:** `PriorityQueue<Patient>` for auto-sorted waiting list, `HashMap<String, Patient>` for O(1) ID lookup, `ArrayList<Patient>` for full history and CSV export. `ERMonitorThread` implements `Runnable` with a `volatile` boolean flag, running as a daemon thread every 5 seconds.

---

## Proposed Architecture Description

The system follows a layered architecture — **UI → Service → Model** — with `util` and `thread` as cross-cutting concerns. The `ERMainWindow` (UI layer) handles all user interaction and calls into `TriageService` (Service layer), which owns the `PriorityQueue`, `HashMap`, and `ArrayList` that manage patient state. `TriageService` operates on `Patient` objects (Model layer), which are abstract — each concrete subclass implements its own triage rules. `FileService` handles all file I/O independently. `ERMonitorThread` runs as a background daemon thread, checking wait times every 5 seconds and logging alerts through `FileService`. `InputValidator` is a static utility shared across layers.

```
ER-Triage/
├── src/
│   ├── Main.java              — entry point, wires services, launches GUI
│   ├── model/                 — Patient (abstract), TriageLevel, 3 subclasses
│   ├── service/               — TriageService, FileService
│   ├── exception/             — 3 custom exception classes
│   ├── util/                  — InputValidator
│   ├── thread/                — ERMonitorThread
│   └── ui/                    — ERMainWindow, ERComponents, ERTheme
├── records/                   — log and CSV output files
├── compile.bat
└── run.bat
```
---

## How to Run
### Requirements
- Java 11 or higher
- No external libraries required

### Windows
```bat
compile.bat
run.bat
```

### Mac / Linux
```bash
mkdir -p out
javac -sourcepath src -d out src/Main.java src/model/*.java src/service/*.java src/exception/*.java src/util/*.java src/thread/*.java src/ui/*.java
java -cp out Main
```

### Usage
Once the GUI opens, use the sidebar to navigate between panels:

| Panel | Description |
|---|---|
| **Admit Patient** | Select General / Trauma / Cardiac, fill in clinical data, click Admit & Triage |
| **Waiting Queue** | View all waiting patients sorted by ESI priority; call or discharge patients |
| **ER Dashboard** | Live ESI-level counts and full patient history table |
| **Lookup Patient** | Search any patient by their auto-generated ID |

### Output Files

| File | Contents |
|---|---|
| `records/er_log.txt` | Timestamped activity log |
| `records/patients_export_YYYY-MM-DD.csv` | Full patient data export |

---

## Git Discipline Notes
Minimum 10 meaningful commits required.
