# Task Tracker Service Documentation

This service manages tasks, projects, and programs for individuals. It allows importing data via a JSON file and querying tasks and project associations.




## Technical Requirements
- **Java**: 11
- **Tomcat**: 9.0.133
- **Maven**: 3.6.0+

## Dependencies
- **javax.servlet-api**: 4.0.1
- **gson**: 2.10.1
- **junit**: 3.8.1 (Test)

## How to Run
1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd task-tracker-service
   ```

2. **Build the project**:
   ```bash
   mvn clean package
   ```

3. **Deploy to Tomcat**:
   - Copy the generated WAR file from `target/task-tracker-service.war` to your Tomcat `webapps` directory **renamed as `task_tracker_service_war.war`** (to match the URL).
   - Start Tomcat (e.g., `bin/startup.sh` or `bin/startup.bat`).

4. **Access the Application**:
   - The service will be available at `http://localhost:8080/task_tracker_service_war`.

## Sample Data
You can download the sample `data.json` file from [here](https://axeno1-my.sharepoint.com/:u:/g/personal/kumman_dhakad_axeno_co/IQDex4pMpVVITrdQtXN1XgJFAW9TjKA7gx-t021ZBqqt1Ig?e=idXuYg).

## API Endpoints and Testing

Prerequisite: The server is running at `http://localhost:8080/task_tracker_service_war` (port may vary). The API base URL is `http://localhost:8080/task_tracker_service_war/api/`.

### 1. Import Data
Uploads the `data.json` file to initialize the system.

- **Endpoint**: `POST /api/import`
- **Multipart Param**: `file` (The `data.json` file)

**Example (curl):**
```bash
curl -X POST -F "file=@path/to/data.json" http://localhost:8080/task_tracker_service_war/api/import
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Data imported successfully",
  "data": null
}
```

---

### 2. Get Programs for Individual
Fetches all programs an individual is enrolled in (via projects).

- **Endpoint**: `GET /api/programs`
- **Query Param**: `individualId` (e.g., `U1`)

**Example:**
`GET /api/programs?individualId=U1` -> `http://localhost:8080/task_tracker_service_war/api/programs?individualId=U1`

**Expected Output (for U1):**
```json
{
  "success": true,
  "message": "Programs fetched",
  "data": [
    {
      "id": "P1",
      "name": "Digital Transformation"
    }
  ]
}
```

---

### 3. Get Projects for Individual
Fetches all projects an individual is enrolled in.

- **Endpoint**: `GET /api/projects`
- **Query Param**: `individualId` (e.g., `U1`)

**Example:**
`GET /api/projects?individualId=U1` -> `http://localhost:8080/task_tracker_service_war/api/projects?individualId=U1`

**Expected Output (for U1):**
```json
{
  "success": true,
  "message": "Projects fetched",
  "data": [
    {
      "id": "PR1",
      "name": "Website Revamp",
      "programName": "Digital Transformation"
    },
    {
      "id": "PR2",
      "name": "Mobile App",
      "programName": "Digital Transformation"
    }
  ]
}
```

---

### 4. Get Pending Tasks (All)
Fetches all pending tasks for a user across all programs/projects where they are part of the `team`.

- **Endpoint**: `GET /api/tasks/pending`
- **Query Param**: `individualId` (e.g., `U1`)

**Example:**
`GET /api/tasks/pending?individualId=U1` -> `http://localhost:8080/task_tracker_service_war/api/tasks/pending?individualId=U1`

**Expected Output (for U1):**
```json
{
  "success": true,
  "message": "Pending tasks fetched",
  "data": {
    "count": 2,
    "tasks": [
      {
        "programId": "P1",
        "programName": "Digital Transformation",
        "projectId": "PR1",
        "projectName": "Website Revamp",
        "taskId": "T1",
        "taskTitle": "Design Homepage"
      },
      {
        "programId": "P1",
        "programName": "Digital Transformation",
        "projectId": "PR2",
        "projectName": "Mobile App",
        "taskId": "T4",
        "taskTitle": "App Wireframe"
      }
    ]
  }
}
```

---

### 5. Get Pending Tasks (By Program)
Fetches pending tasks for a user within a specific program.

- **Endpoint**: `GET /api/tasks/pending`
- **Query Params**:
  - `individualId` (e.g., `U1`)
  - `programId` (e.g., `P1`)

**Example:**
`GET /api/tasks/pending?individualId=U1&programId=P1` -> `http://localhost:8080/task_tracker_service_war/api/tasks/pending?individualId=U1&programId=P1`

**Expected Output (for U1, P1):**
```json
{
  "success": true,
  "message": "Pending tasks fetched",
  "data": {
    "programId": "P1",
    "programName": "Digital Transformation",
    "count": 2,
    "tasks": [
      {
        "projectId": "PR1",
        "projectName": "Website Revamp",
        "taskId": "T1",
        "taskTitle": "Design Homepage"
      },
      {
        "projectId": "PR2",
        "projectName": "Mobile App",
        "taskId": "T4",
        "taskTitle": "App Wireframe"
      }
    ]
  }
}
```

---

### 6. Get Pending Tasks (By Project)
Fetches pending tasks for a user within a specific project.

- **Endpoint**: `GET /api/tasks/pending`
- **Query Params**:
  - `individualId` (e.g., `U1`)
  - `projectId` (e.g., `PR1`)

**Example:**
`GET /api/tasks/pending?individualId=U1&projectId=PR1` -> `http://localhost:8080/task_tracker_service_war/api/tasks/pending?individualId=U1&projectId=PR1`

**Expected Output (for U1, PR1):**
```json
{
  "success": true,
  "message": "Pending tasks fetched",
  "data": {
    "programId": "P1",
    "programName": "Digital Transformation",
    "projectId": "PR1",
    "projectName": "Website Revamp",
    "count": 1,
    "tasks": [
      {
        "taskId": "T1",
        "taskTitle": "Design Homepage"
      }
    ]
  }
}
```

---

### 7. Get Program Name by Project
Fetches the program name associated with a specific project.

- **Endpoint**: `GET /api/programs/by-project/{projectId}`
- **Path Param**: `projectId` (e.g., `PR1`)

**Example:**
`GET /api/programs/by-project/PR1` -> `http://localhost:8080/task_tracker_service_war/api/programs/by-project/PR1`

**Expected Output:**
```json
{
  "success": true,
  "message": "Program name fetched",
  "data": "Digital Transformation"
}
```