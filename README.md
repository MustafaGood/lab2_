# Laboration 2 – REST API i Spring Boot

Detta projekt är ett REST API utvecklat med Spring Boot 3, Java 23 och MySQL. API:et hanterar intressepunkter (platser) kopplade till kategorier, med stöd för användare, roller och säkerhetsregler.

## 🛠 Teknikstack
- Java 23
- Spring Boot 3.x
- Maven
- Spring Security
- Spring Data JPA
- MySQL
- Spring Validation
- Postman (för testning)

## 📁 Funktionalitet
- Inloggning via formulär (login.html) och Basic Auth
- Endast administratörer kan skapa nya kategorier
- Inloggade användare kan skapa platser
- Anonyma användare får bara se publika platser
- Lösenord krypteras med BCrypt

## 📦 Endpoints
### Kategorier
- `GET /api/kategorier` – hämta alla kategorier
- `POST /api/kategorier` – skapa ny kategori (**endast admin**)

### Platser
- `GET /api/platser` – hämta alla publika platser
- `POST /api/platser` – skapa ny plats (**kräver inloggning**)

## 👤 Roller
| Användare | Lösenord   | Roll   |
|-----------|------------|--------|
| admin     | admin123   | ADMIN  |

## ✅ G-nivå – uppfyllda krav
- REST API med kategorier och platser
- Spring Security med rollstyrd åtkomst
- JSON-data i request och response
- CRUD för plats och kategori (POST, GET)
- Felhantering med validering
- Login via formulär och testat i Postman
- Inloggningsskyddade endpoints
- Snygg index.html med knappar till `/api/platser`, `/api/kategorier` och `/login`

## 🥇 VG-nivå – uppfyllda krav
- Minst 2 enhetstester (`PlatsControllerTest`, `KategoriControllerTest`)
- MockMvc används med `@WithMockUser`
- (valfritt) Integrering med geokodning via extern API

## 🧪 Testa med Postman
Exempel på POST till `/api/platser`:
```json
{
  "namn": "Gamla Stan",
  "beskrivning": "Historisk plats i centrala Stockholm",
  "koordinater": "59.325,18.070",
  "kategori": { "id": 1 },
  "anvandare": { "id": 1 }
}
```

## ▶️ Kom igång
1. Klona repot och navigera till projektmappen.
2. Skapa en MySQL-databas och uppdatera `application.properties` med dina inställningar.
3. Bygg och starta projektet:
   ```powershell
   ./mvnw spring-boot:run
   ```
4. Öppna [http://localhost:8081](http://localhost:8081) i webbläsaren. Här hittar du knappar till `/api/platser`, `/api/kategorier` och `/login`.

## 🧪 Köra tester
```powershell
./mvnw clean install
.\mvnw spring-boot:run
```

## 📄 Licens
MIT
