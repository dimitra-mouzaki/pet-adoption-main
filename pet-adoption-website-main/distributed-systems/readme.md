## 🗄️ Database Setup (PostgreSQL on Render)

Για να δημιουργήσετε μια PostgreSQL βάση δεδομένων στο [Render](https://render.com):

1. **Σύνδεση στο Render**
   - Μπείτε στο [https://render.com](https://render.com) και κάντε login.

2. **Δημιουργία νέας βάσης**
   - Στο dashboard επιλέξτε **New +** → **Database**.
   - Επιλέξτε **PostgreSQL**.
   - Ορίστε το όνομα της βάσης (π.χ. `distributed-systems-db`).
   - Επιλέξτε περιοχή (region) κοντά σας.
   - Επιλέξτε το free plan (ή όποιο ταιριάζει στο project σας).
   - Πατήστε **Create Database**.

3. **Αναμονή για Provisioning**
   - Περιμένετε μερικά λεπτά μέχρι να ολοκληρωθεί η δημιουργία.

4. **Στοιχεία Σύνδεσης**
   - Από το dashboard της βάσης αντιγράψτε το **Internal Database URL** ή το **External Database URL** (ανάλογα με το αν θα τρέχετε την εφαρμογή μέσα στο Render ή τοπικά).
   - Το URL έχει μορφή:
     ```
     postgres://username:password@host:port/dbname
     ```

5. **Προσθήκη στα Environment Variables**
    Για να συνδεθεί η εφαρμογή με τη βάση PostgreSQL στο Render, ρυθμίστε το αρχείο `src/main/resources/application.properties`:

    ```properties
    # Στοιχεία σύνδεσης στη βάση
    spring.datasource.url=${DATABASE_URL}
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.datasource.driver-class-name=org.postgresql.Driver
    ---

## 🚀 Backend Setup & Run (Local)

Ακολουθήστε τα παρακάτω βήματα για να τρέξετε το backend τοπικά:

```bash
# 1. Προαπαιτούμενα
# - Java 21
# - Maven
# - Πρόσβαση σε PostgreSQL

# 2. Ρυθμίσεις Σύνδεσης
# Ανοίξτε το αρχείο src/main/resources/application.properties και βάλτε:
# spring.datasource.url=jdbc:postgresql://host:port/dbname
# spring.datasource.username=your_username
# spring.datasource.password=your_password

# 3. Build του Project
cd .../pet-adoption-website/distributed-systems
mvn clean package

# 4. Εκτέλεση της Εφαρμογής
cd target
java -jar distributed-systems-0.0.1-SNAPSHOT.jar

# Μετά την εκκίνηση, η εφαρμογή θα είναι διαθέσιμη στο:
# http://localhost:8080/swagger-ui/index.html
```

## 🚀 Frontend Setup & Run (Local)

Ακολουθήστε τα παρακάτω βήματα για να τρέξετε το frontend τοπικά:

```bash
# 1. Μετάβαση στο directory του frontend
cd pet-adoption-website/distributed-systems/src/main/resources/src

# 2. Αρχικοποίηση npm project
npm init -y

# 3. Εγκατάσταση εξαρτήσεων
# Αν θέλετε συγκεκριμένα πακέτα, π.χ. express και cors:
npm install express cors
# ή γενική εγκατάσταση όλων από package.json:
# npm install

# 4. Εκκίνηση του server
node serverInit.js

# Μετά την εκκίνηση, η εφαρμογή frontend θα είναι διαθέσιμη στo localhost:7000

```

## 🔑 Accounts για Δοκιμές

| Username   | Password       | Ρόλος          |
|------------|----------------|----------------|
| admin      | admin123       | Admin          |
| shelter1   | shelter123     | Shelter        |
| vet1       | vet123         | Veterinarian   |
| user1      | user123        | Basic User     |

## 📁 Project Structure

Το project είναι οργανωμένο ως εξής:

    pet-adoption-website/
    ├── distributed-systems/ # Backend
    │ ├── src/
    │ │ ├── main/
    │ │ │ ├── java/ # Κώδικας Java (Spring Boot)
    │ │ │ └── resources/ # Application properties, static resources
    │ │ │ └── src/ # Frontend (Node.js / Express)
    │ │ │ ├── serverInit.js
    │ │ │ └── package.json
    │ └── target/ # Εκτελέσιμα jar αρχεία μετά το build

### 🔹 Backend
- Βρίσκεται στο: `pet-adoption-website/distributed-systems`
- Περιέχει όλο τον Spring Boot κώδικα και το `application.properties`.

### 🔹 Frontend
- Βρίσκεται στο: `pet-adoption-website/distributed-systems/src/main/resources/src`
- Περιέχει τον Node.js/Express server (`serverInit.js`) και το `package.json`.