# Live Kviz Aplikacija  
**Projekt za kolegij:** *Programska potpora komunikacijskim sustavima*

## 📋 Opis projekta

Ova aplikacija omogućuje **stvaranje, održavanje i sudjelovanje u live kvizovima** u stvarnom vremenu.  
Razvijena je kao dio kolegija *Programska potpora komunikacijskim sustavima*.

Aplikacija koristi moderne web tehnologije i real-time komunikacijske protokole:

- **Backend:** Java Spring Boot  
- **Frontend:** Angular  
- **Real-time komunikacija:** WebSocket  
- **Standardna komunikacija:** REST API
- **Baza podataka:** H2 (in-memory, za development)

---

## 🚀 Pokretanje projekta

### Backend (Java Spring Boot)

📌 Preduvjeti: Instalirani Java 21 i Apache Maven

```bash
# Kloniraj repozitorij (ako još nisi)
# Idi u backend direktorij
cd backend

# Build projekta
mvn clean install

# Pokretanje aplikacije
mvn spring-boot:run
```

### Frontend (Angular)
📌 Preduvjeti: Instaliran Node.js i npm

```bash
# Kloniraj repozitorij (ako još nisi)
# Idi u frontend direktorij
cd frontend

# Instalacija ovisnosti
npm install

# Pokretanje aplikacije
ng serve
```
