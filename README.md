# nayati

## Prerequisites

- **Git** (to clone the repository)
- **Java 21+** (for the Java backend services)
- **Gradle** (**not required to install manually; use the provided Gradle Wrapper**)
- **Python 3.10+** (for the face recognition service)
- **pip** (Python package manager)
- **MongoDB** (required for the face recognition service)
- **Docker** (optional, for containerized runs)

> **Note:** You do **not** need to install Gradle manually. Use the provided Gradle Wrapper (`./gradlew` for Linux/Mac, `gradlew` for Windows) from the project root to run all Gradle tasks.

---

## Setup Instructions

### 1. Clone the Repository

```sh
git clone https://github.com/Manoj-work/nayati-backend
cd nayati-main-backend
```

---

### 2. Java Backend Setup (API, Attendance, MinioService)

#### a. Install Java (if not already installed)
- Download and install Java 17 or higher from [AdoptOpenJDK](https://adoptopenjdk.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/).
- Verify installation:
  ```sh
  java -version
  ```

#### b. Build and Run Java Services
From the project root:

##### On Linux/Mac

```sh
  ./gradlew api:bootRun                # Start API service
  ./gradlew attendance:bootRun         # Start Attendance service
  ./gradlew minioService:bootRun       # Start Minio service
  ./gradlew runFaceRecognitionService  # start the python face-recognition-model
```
##### On Windows

```sh
  gradlew api:bootRun
  gradlew attendance:bootRun
  gradlew minioService:bootRun
  gradlew runFaceRecognitionService
  ```

> **Note:** All Gradle commands should be run from the project root using the Gradle Wrapper (`./gradlew` or `gradlew`).

---

### 3. Python Face Recognition Service Setup

#### a. Install Python (if not already installed)
- Download and install Python 3.10+ from [python.org](https://www.python.org/downloads/).
- Verify installation:
  ```sh
  python --version
  ```

#### b. Install Python Dependencies

```sh
cd face-recognition-service
python -m venv venv         # (optional, but recommended)
# On Windows:
venv\Scripts\activate
# On Mac/Linux:
source venv/bin/activate

pip install -r requirements.txt
cd ..
```

#### c. Start the Face Recognition Service

From the project root:

```sh
# On Linux/Mac
./gradlew runFaceRecognitionService
# On Windows
gradlew runFaceRecognitionService
```

#### d. (Optional) Using Docker

```sh
cd face-recognition-service
docker build -t face-recognition-service .
docker run -p 8090:8090 face-recognition-service
```

---

## 4. MongoDB Setup
- Make sure MongoDB is running locally or accessible remotely.
- Default connection settings may need to be configured in the respective service's configuration files.
---


