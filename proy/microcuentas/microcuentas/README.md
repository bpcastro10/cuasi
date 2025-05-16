# Microservicio: microcuentas

Este microservicio gestiona las cuentas bancarias y los movimientos asociados.

---

## Instrucciones para ejecutar y probar el microservicio

### 1. Requisitos previos
- Java 17 o superior
- Maven (o usa el wrapper `mvnw.cmd`)
- Docker (para la base de datos PostgreSQL)

### 2. Levantar la base de datos con Docker
Ejecuta este comando para iniciar PostgreSQL:
```bash
docker run --name basedb -e POSTGRES_PASSWORD=admin123 -p 5432:5432 -d postgres:latest
```

### 3. Crear la base de datos
Conéctate al contenedor y crea la base de datos:
```bash
docker exec -it basedb psql -U postgres
CREATE DATABASE microcuentasdb;
\q
```

### 4. Configura la conexión en `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/microcuentasdb
spring.datasource.username=postgres
spring.datasource.password=admin123
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 5. Compila y ejecuta el microservicio
En Windows PowerShell:
```powershell
cd C:\Users\Usuario\Downloads\microcuentas\microcuentas
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

### 6. Probar los endpoints con Postman
- El microservicio estará disponible en: `http://localhost:8080` (o el puerto configurado)
- Usa los ejemplos de este README para probar los endpoints de cuentas y movimientos.

---

## Endpoints principales y ejemplos de uso en Postman

### Cuentas

#### 1. Crear una cuenta (POST)
- **URL:** `http://localhost:8080/cuentas`
- **Body (raw, JSON):**
```json
{
  "numeroCuenta": "1002",
  "tipoCuenta": "CORRIENTE",
  "saldoInicial": 1000.0,
  "estado": "ACTIVA"
}
```

#### 2. Listar todas las cuentas (GET)
- **URL:** `http://localhost:8080/cuentas`

#### 3. Obtener una cuenta por número (GET)
- **URL:** `http://localhost:8080/cuentas/1002`

#### 4. Actualizar una cuenta (PUT)
- **URL:** `http://localhost:8080/cuentas/1002`
- **Body (raw, JSON):**
```json
{
  "numeroCuenta": "1002",
  "tipoCuenta": "CORRIENTE",
  "saldoInicial": 1200.0,
  "estado": "INACTIVA"
}
```

#### 5. Eliminar una cuenta (DELETE)
- **URL:** `http://localhost:8080/cuentas/1002`

---

### Movimientos

#### 1. Crear un movimiento (POST)
- **URL:** `http://localhost:8080/movimientos`
- **Body (raw, JSON):**
```json
{
  "fecha": "2024-06-01",
  "tipoMovimiento": "DEPOSITO",
  "valor": 200.0,
  "saldo": 1200.0,
  "cuenta": {
    "numeroCuenta": "1002",
    "tipoCuenta": "CORRIENTE",
    "saldoInicial": 1000.0,
    "estado": "ACTIVA"
  }
}
```

#### 2. Listar todos los movimientos (GET)
- **URL:** `http://localhost:8080/movimientos`

#### 3. Obtener un movimiento por ID (GET)
- **URL:** `http://localhost:8080/movimientos/1`

#### 4. Actualizar un movimiento (PUT)
- **URL:** `http://localhost:8080/movimientos/1`
- **Body (raw, JSON):**
```json
{
  "fecha": "2024-06-01",
  "tipoMovimiento": "RETIRO",
  "valor": 100.0,
  "saldo": 1100.0,
  "cuenta": {
    "numeroCuenta": "1002",
    "tipoCuenta": "CORRIENTE",
    "saldoInicial": 1000.0,
    "estado": "ACTIVA"
  }
}
```

#### 5. Eliminar un movimiento (DELETE)
- **URL:** `http://localhost:8080/movimientos/1`

#### 6. Reporte de movimientos por fecha (GET)
- **URL:** `http://localhost:8080/movimientos/reportes?fecha=2024-06-01`
- **Nota:** Actualmente este endpoint devuelve todos los movimientos. Puedes personalizar el filtrado por fecha en el repositorio si lo necesitas.

---

## Notas y recomendaciones
- **La base de datos debe estar creada y el contenedor Docker corriendo antes de iniciar el microservicio.**
- Si cambias el nombre de la base de datos, usuario o contraseña, actualízalo también en `application.properties`.
- Si tienes errores de conexión, revisa que el contenedor esté activo y el puerto 5432 no esté ocupado.
- Puedes omitir los tests al compilar con: `-DskipTests`
- Si necesitas limpiar la base, puedes borrar el contenedor y crearlo de nuevo:
  ```bash
  docker rm -f basedb
  docker run --name basedb -e POSTGRES_PASSWORD=admin123 -p 5432:5432 -d postgres:latest
  ```
- Si recibes errores como "No static resource cuentas" o "No static resource movimientos", asegúrate de que los controladores y repositorios existen y están correctamente implementados.

---

## Troubleshooting
- **Error de conexión a la base de datos:** Verifica las credenciales y que el servicio de PostgreSQL esté activo.
- **Puerto ocupado:** Cambia el puerto en `application.properties`.
- **Problemas de CORS:** Configura los orígenes permitidos en tu aplicación Spring Boot.
- **Error de relación entre entidades:** Asegúrate de que los nombres de columnas y las anotaciones `@JoinColumn` y `@Column` coinciden entre las entidades relacionadas.

---

## Contacto y soporte
Para dudas o soporte, contacta al equipo de desarrollo o abre un issue en el repositorio.

---

## Estructura de entidades y relación (JPA)

- **Cuenta**: entidad principal, numeroCuenta es la PK.
- **Movimiento**: tiene una relación ManyToOne con Cuenta a través de la clave foránea `numeroCuenta`.

Ejemplo de relación en la entidad Movimiento:
```java
@ManyToOne
@JoinColumn(name = "numero_cuenta", referencedColumnName = "numero_cuenta")
private Cuenta cuenta;
```

---

## Pruebas automáticas

Puedes ejecutar las pruebas unitarias y de integración con:
```bash
mvn test
```

---

## Recomendaciones de seguridad
- No expongas datos sensibles en producción.
- Usa HTTPS en ambientes reales.
- Limita el acceso a los endpoints sensibles.
- Valida y sanitiza todos los datos recibidos.

---

## Cómo probar

Puedes usar [Postman](https://www.postman.com/) o `