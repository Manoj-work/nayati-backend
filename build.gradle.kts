plugins {
    alias(libs.plugins.versions)
}

group = "com.medhir"
version = "0.0.1"

tasks.register<Exec>("facerecog") {
    group = "application"
    description = "Run the Python-based face recognition service using Uvicorn"
    workingDir = file("face-recognition-service")
    commandLine = listOf(
        "python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8090"
    )
}