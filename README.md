
# eSports Arena Manager

## Proyecto de Desarrollo Fullstack II — Evaluación Parcial 3

eSports Arena Manager es una plataforma académica para gestionar
competiciones de videojuegos, usuarios, equipos, torneos,
inscripciones y otras operaciones relacionadas con los eSports.

El proyecto utiliza una arquitectura de microservicios desarrollada
con Java 21 y Spring Boot. Cada microservicio administra sus propios
datos y expone operaciones mediante una API REST.

Durante la Evaluación Parcial 3 (EP3) se avanzó en la integración
del backend con el frontend React, la seguridad mediante JWT,
las validaciones entre microservicios y la persistencia local.

---

## 1. Integrantes

- Kevin Reyes
- Vicente Pizarro
- Martín Vergara

### Organización del proyecto

Para el trabajo colaborativo se utilizaron Git, GitHub y Trello,
permitiendo organizar tareas, registrar avances y mantener
evidencia de las contribuciones realizadas.

**Tablero de Trello:**

https://trello.com/invite/b/6a1511245ca5f4ad188f2c19/ATTI9da34528964cb7cb581e7631591197c361247407/esports-arena-manager

---

## 2. Repositorios

**Backend — Spring Boot**

https://github.com/itskevin02/Ea-Sports-Backend

**Frontend — React**

https://github.com/itskevin02/Ea-Sports-Frontend

Ambos repositorios forman parte del proyecto eSports Arena Manager.

El backend proporciona las API REST y el frontend consume los
servicios correspondientes para mostrar y administrar información.

---

## 3. Tecnologías utilizadas

### Backend

- Java 21
- Spring Boot 4.0.6
- Spring Web
- Spring Security
- Spring Data JPA
- Hibernate
- H2 Database
- Spring Cloud Gateway
- Netflix Eureka
- JWT (JSON Web Token)
- BCrypt para contraseñas
- Maven
- Bean Validation
- RestTemplate
- SLF4J
- JUnit 5
- Mockito
- MockMvc
- Swagger / OpenAPI

### Frontend

- React 19
- Vite
- JavaScript
- HTML5
- CSS3
- Bootstrap 5
- React Testing Library
- Jasmine
- Karma

### Herramientas de desarrollo

- Visual Studio Code
- IntelliJ IDEA
- PowerShell
- Git
- GitHub
- Postman
- Navegador web

---

## 4. Arquitectura del sistema

Se utiliza una arquitectura basada en microservicios.

Cada servicio mantiene una estructura organizada en capas:

- Controller: recibe las peticiones HTTP.
- Service: contiene la lógica de negocio.
- Repository: realiza operaciones con la base de datos.
- Model: representa las entidades.
- DTO: permite transportar información entre componentes.

También se utilizan validaciones, manejo de errores, respuestas
HTTP y registros de ejecución.

### Comunicación general

```text
                FRONTEND REACT
                  Puerto 5173
                       |
                       v
                 API GATEWAY
                  Puerto 8080
                       |
              Descubrimiento
                  mediante
                    EUREKA
                  Puerto 8761
                       |
        +--------------+--------------+
        |              |              |
        v              v              v
    AUTH-SERVICE   USER-SERVICE   GAME-SERVICE
       8081           8083           8082
        |              |              |
        +--------------+--------------+
                       |
             DEMÁS MICROSERVICIOS
                       |
                       v
              BASES DE DATOS H2
```

Gateway centraliza el acceso del frontend a los servicios.

Eureka permite registrar y localizar las instancias de
microservicios utilizadas por Gateway.

Algunas validaciones entre servicios se realizan mediante
peticiones HTTP con RestTemplate.

---

## 5. Microservicios implementados

El backend contiene 12 microservicios de negocio, además
de Eureka y Gateway.

| Microservicio | Puerto | Ruta principal | Función |
|---|---|---|---|
| Auth | 8081 | `/auth` | Cuentas, inicio de sesión y JWT |
| Game | 8082 | `/games` | Gestión de videojuegos |
| User | 8083 | `/users` | Gestión de perfiles |
| Team | 8084 | `/teams` | Gestión de equipos |
| Tournament | 8085 | `/tournaments` | Gestión de torneos |
| Registration | 8086 | `/registrations` | Gestión de inscripciones |
| Match | 8087 | `/matches` | Gestión de partidas |
| Result | 8088 | `/results` | Gestión de resultados |
| Sanction | 8089 | `/sanctions` | Gestión de sanciones |
| Prize | 8090 | `/prizes` | Gestión de premios |
| Notification | 8091 | `/notifications` | Gestión de notificaciones |
| Ranking | 8092 | `/rankings` | Gestión de clasificaciones |
| Gateway | 8080 | Diferentes rutas | Entrada a las API |
| Eureka | 8761 | `/` | Registro y descubrimiento |

### Operaciones REST

Los microservicios de negocio proporcionan operaciones CRUD,
según sus respectivos controladores:

| Método | Función |
|---|---|
| GET | Consultar registros |
| GET /{id} | Consultar un registro |
| POST | Crear un registro |
| PUT /{id} | Actualizar un registro |
| DELETE /{id} | Eliminar un registro |

Algunas operaciones tienen restricciones de acceso o
validaciones adicionales.

---

## 6. Funcionalidades implementadas

### 6.1 Gestión de autenticación — Auth

Se implementó un microservicio encargado de administrar
las cuentas utilizadas para iniciar sesión.

Funcionalidades:

- Registro administrativo de cuentas.
- Inicio de sesión mediante usuario y contraseña.
- Validación de credenciales.
- Contraseñas almacenadas mediante BCrypt.
- Generación de tokens JWT.
- Identificación de roles.
- Protección de operaciones administrativas.
- Creación opcional de un administrador inicial.
- Inclusión del identificador `authId` en el JWT y en el login.

El inicio de sesión utiliza:

```http
POST /auth/login
```

Ejemplo de petición:

```json
{
  "username": "adminLocal",
  "password": "CONTRASENA_CONFIGURADA_LOCALMENTE"
}
```

La respuesta contiene un token JWT y los datos de la cuenta:

```json
{
  "token": "TOKEN_JWT_GENERADO",
  "usuario": {
    "authId": 1,
    "username": "adminLocal",
    "rol": "ADMIN"
  }
}
```

El identificador puede variar según la base de datos.

### 6.2 Seguridad mediante JWT

Se implementó autenticación basada en JSON Web Token.

El JWT contiene información relacionada con:

- Nombre de usuario.
- Rol de la cuenta.
- Identificador `authId`.
- Fecha de emisión y vencimiento.

Los servicios que cuentan con filtros de seguridad
validan el token antes de autorizar determinadas operaciones.

Se configuraron permisos específicos en:

- Auth
- Game
- User
- Tournament
- Sanction

La protección no es uniforme en todos los microservicios;
cada uno mantiene la configuración implementada.

### 6.3 Autorización por roles

Se implementó diferenciación entre cuentas administrativas
y cuentas normales.

**ADMIN**

Puede realizar las operaciones administrativas
habilitadas por los servicios protegidos.

**USUARIO / USER**

Puede utilizar las operaciones permitidas a una cuenta
normal, como consultar su perfil vinculado.

Ejemplos de respuestas de seguridad:

| Código HTTP | Significado |
|---|---|
| 200 | Consulta correcta |
| 201 | Recurso creado |
| 204 | Eliminación correcta |
| 400 | Datos incorrectos |
| 401 | Sesión no válida o vencida |
| 403 | Operación sin permisos |
| 404 | Recurso inexistente |
| 409 | Conflicto de negocio |
| 503 | Servicio requerido no disponible |

Los códigos dependen de la operación y de las
validaciones correspondientes.

### 6.4 Gestión de perfiles — User

Se implementó la administración de perfiles de usuario.

Los perfiles contienen información como:

- ID de perfil.
- Nombre.
- Correo electrónico.
- Nickname.
- Rol.
- Estado.
- Identificador `authId`, cuando existe una vinculación.

Los administradores pueden consultar, crear,
actualizar y eliminar perfiles, respetando las
validaciones implementadas.

### 6.5 Vinculación entre Auth y User

Se implementó una relación explícita entre cuentas
de autenticación y perfiles de usuario.

No se considera que el ID de Auth sea necesariamente
igual al ID de User.

Ejemplo:

```text
AUTH
id = 2
username = jugadorEP3

       |
       | authId = 2
       v

USER
id = 1
authId = 2
nombre = Jugador Vinculado
```

Funcionalidades:

- Asociar una cuenta Auth a un perfil User.
- Validar que la cuenta Auth exista.
- Evitar vincular una misma cuenta a dos perfiles.
- Consultar el perfil correspondiente al JWT.
- Impedir que una cuenta normal consulte el listado
  administrativo de perfiles.

#### Vincular una cuenta

```http
PUT /users/{id}/vincular-auth/{authId}
```

Esta operación está reservada a ADMIN.

#### Consultar el perfil propio

```http
GET /users/mi-perfil
```

Esta operación identifica la cuenta por el JWT
y consulta únicamente su perfil vinculado.

La vinculación es administrada explícitamente;
no se crea automáticamente al registrar una cuenta.

### 6.6 Gestión de videojuegos — Game

Se implementaron operaciones CRUD para administrar
los videojuegos registrados en el sistema.

Se añadieron controles de seguridad para permitir
la consulta pública de juegos y restringir las
operaciones de modificación al administrador.

### 6.7 Gestión de torneos — Tournament

Se implementaron operaciones CRUD de torneos.

Un torneo incluye información como:

- Nombre.
- Ubicación.
- Cantidad de equipos.
- Estado.
- Juego asociado.
- Plataforma.
- Fecha de cierre de inscripción.
- Fecha de inicio.
- Descripción.
- Imagen opcional.

Se incorporó validación de fechas para impedir
que el inicio del torneo sea anterior o igual
al cierre de inscripción.

Las consultas GET están habilitadas públicamente;
las modificaciones requieren permisos administrativos.

### 6.8 Gestión de equipos — Team

Se implementaron operaciones para:

- Registrar equipos.
- Consultar equipos.
- Actualizar equipos.
- Eliminar equipos.

Los equipos incluyen nombre, coach, juego asociado
y estado.

También se añadió una validación antes de eliminar
un equipo: el servicio consulta Registration para
comprobar si existen inscripciones asociadas.

Si el equipo tiene inscripciones, la eliminación
se rechaza con HTTP 409.

Si Registration no está disponible, la operación
se rechaza para evitar una eliminación sin comprobar
las dependencias.

### 6.9 Gestión de inscripciones — Registration

Se implementaron operaciones REST para administrar
las inscripciones relacionadas con las competiciones.

Este servicio también participa en las validaciones
realizadas por Team antes de eliminar equipos.

### 6.10 Gestión de partidas y resultados

**Match**

Permite registrar y administrar información
de las partidas.

**Result**

Permite registrar y administrar resultados
mediante operaciones REST.

### 6.11 Gestión de rankings

Ranking proporciona operaciones para administrar
los registros de clasificación del sistema.

Ruta principal:

```http
/rankings
```

### 6.12 Gestión de premios y notificaciones

**Prize**

Servicio encargado de administrar
registros de premios.

**Notification**

Servicio encargado de administrar
registros de notificaciones.

Ambos cuentan con operaciones REST y persistencia
configurable.

### 6.13 Gestión de sanciones — Sanction

Se implementó un servicio para administrar
sanciones asociadas a los perfiles de usuario.

Una sanción contiene:

- ID.
- ID del usuario.
- Motivo.
- Severidad.
- Estado.

Las operaciones administrativas están protegidas
mediante JWT y rol ADMIN.

También se añadieron las siguientes reglas:

**Validación de existencia**

Al crear o actualizar una sanción, Sanction
consulta User para comprobar que el perfil exista.

**Control de sanciones activas**

Se rechaza la creación o actualización que
provoque que un usuario tenga más de una
sanción con estado ACTIVA.

**Protección al eliminar usuarios**

User consulta Sanction antes de eliminar
un perfil.

La eliminación se rechaza si existen sanciones
asociadas, incluso cuando ya no están activas.

Estas comprobaciones ayudan a mantener la
consistencia entre los servicios.

---

## 7. Integración con React

El frontend de EP3 consume las API del backend
a través de Gateway.

Se implementaron funcionalidades de integración
para las áreas trabajadas durante el proyecto:

- Inicio de sesión con Auth.
- Manejo de sesión JWT.
- Consulta de juegos.
- Consulta de torneos.
- Consulta de detalles de torneos.
- Gestión de equipos e inscripciones.
- Administración de usuarios.
- Administración de sanciones.
- Consulta del perfil vinculado de la cuenta.
- Manejo de errores HTTP y permisos.

### Pantalla Mi cuenta

La pantalla Perfil muestra dos grupos de datos:

**Información de la cuenta**

Datos obtenidos de la sesión de Auth:

- Username.
- Rol.

**Mi perfil de jugador**

Datos obtenidos de User mediante:

```http
GET /users/mi-perfil
```

- Nombre.
- Nickname.
- Correo.
- Estado del perfil.

Se conserva una sección de perfiles anteriores
de EP2 para el administrador, como compatibilidad
con los registros locales del navegador.

La información histórica de EP2 almacenada en
localStorage no se considera automáticamente
migrada a la base de datos del backend.

---

## 8. Persistencia de datos

El proyecto utiliza H2 Database junto con
Spring Data JPA e Hibernate.

Se mantienen dos modalidades de ejecución.

### 8.1 Modo predeterminado: H2 en memoria

Cada microservicio conserva su archivo:

```text
application.properties
```

Este archivo utiliza una base H2 en memoria y
configuración de creación de tablas para el
entorno de desarrollo y las pruebas.

Ejemplo:

```properties
spring.datasource.url=jdbc:h2:mem:authdb
spring.jpa.hibernate.ddl-auto=create-drop
```

En este modo los registros no se conservan
después de detener el microservicio.

### 8.2 Modo persistente: H2 en archivos

Se agregó un perfil adicional:

```text
application-persistente.properties
```

Ejemplo en Auth:

```properties
spring.datasource.url=jdbc:h2:file:~/esports-arena-manager/authdb
spring.jpa.hibernate.ddl-auto=update
```

Este perfil permite conservar los registros
al reiniciar el microservicio utilizando la
misma base de datos.

Cada microservicio cuenta con su propia base:

| Servicio | Archivo de base de datos |
|---|---|
| Auth | `authdb.mv.db` |
| User | `userdb.mv.db` |
| Game | `gamedb.mv.db` |
| Team | `teamdb.mv.db` |
| Tournament | `tournamentdb.mv.db` |
| Registration | `registrationdb.mv.db` |
| Match | `matchdb.mv.db` |
| Result | `resultdb.mv.db` |
| Sanction | `sanctiondb.mv.db` |
| Prize | `prizedb.mv.db` |
| Notification | `notificationdb.mv.db` |
| Ranking | `rankingdb.mv.db` |

En Windows, utilizando la configuración actual,
los archivos se almacenan bajo:

```text
C:\Users\<USUARIO_WINDOWS>\esports-arena-manager\
```

Estos archivos no forman parte del repositorio.

### Activar persistencia

Desde la raíz del backend:

```powershell
mvn -pl msvc-auth spring-boot:run "-Dspring-boot.run.profiles=persistente"
```

Para User:

```powershell
mvn -pl msvc-user spring-boot:run "-Dspring-boot.run.profiles=persistente"
```

Para Tournament:

```powershell
mvn -pl msvc-tournament spring-boot:run "-Dspring-boot.run.profiles=persistente"
```

Para los demás servicios se utiliza el
mismo comando cambiando el nombre del módulo.

**Importante:** las bases en memoria no se
trasladan automáticamente a las persistentes.

Tampoco debe confundirse H2 persistente local
con una solución de almacenamiento de producción.

---

## 9. Requisitos para ejecutar el proyecto

Se requiere:

- JDK 21.
- Maven.
- Git.
- Conexión a Internet para descargar las
  dependencias inicialmente.
- Puertos correspondientes disponibles.
- Node.js y npm para ejecutar el frontend React.

Verificar las herramientas:

```powershell
java -version
mvn -version
git --version
```

Java y Maven deben utilizar Java 21.

---

## 10. Descargar el proyecto

Clonar el backend:

```powershell
git clone https://github.com/itskevin02/Ea-Sports-Backend.git
```

Ingresar al repositorio:

```powershell
cd Ea-Sports-Backend
```

Seleccionar la rama de integración:

```powershell
git switch develop
git pull origin develop
```

Compilar:

```powershell
mvn clean install
```

---

## 11. Variables de entorno

Auth permite configurar un administrador inicial
y la clave utilizada para firmar los JWT.

Ejemplo para PowerShell:

```powershell
$env:ADMIN_INICIAL_USERNAME = "adminLocal"
$env:ADMIN_INICIAL_PASSWORD = "CONTRASENA_LOCAL_SEGURA"
$env:JWT_SECRETO = "CAMBIAR_POR_UN_SECRETO_SEGURO_DE_AL_MENOS_32_BYTES"
```

La configuración de administrador inicial
solo crea la cuenta cuando todavía no existe
en la base de datos correspondiente.

El valor `JWT_SECRETO` debe ser el mismo en
todos los servicios que validan tokens JWT.

También existen configuraciones para las
comunicaciones entre microservicios:

```properties
AUTH_SERVICE_URL=http://localhost:8081
USER_SERVICE_URL=http://localhost:8083
SANCTION_SERVICE_URL=http://localhost:8089
```

Estos valores representan las direcciones locales
utilizadas por las validaciones entre servicios.
Las propiedades correspondientes permiten
usar valores predeterminados.

No deben publicarse contraseñas reales
ni secretos JWT en GitHub.

---

## 12. Iniciar los microservicios

Se recomienda ejecutar los servicios en
terminales PowerShell independientes.

### 12.1 Iniciar Eureka

```powershell
mvn -pl msvc-eureka spring-boot:run
```

Panel:

http://localhost:8761

### 12.2 Iniciar Auth

Con las variables de entorno necesarias
y el perfil persistente:

```powershell
mvn -pl msvc-auth spring-boot:run "-Dspring-boot.run.profiles=persistente"
```

### 12.3 Iniciar User

```powershell
mvn -pl msvc-user spring-boot:run "-Dspring-boot.run.profiles=persistente"
```

### 12.4 Iniciar Gateway

```powershell
mvn -pl msvc-gateway spring-boot:run
```

Gateway estará disponible en:

http://localhost:8080

### 12.5 Iniciar otros servicios

Ejemplo con Game:

```powershell
mvn -pl msvc-game spring-boot:run "-Dspring-boot.run.profiles=persistente"
```

Ejemplo con Team:

```powershell
mvn -pl msvc-team spring-boot:run "-Dspring-boot.run.profiles=persistente"
```

Ejemplo con Sanction:

```powershell
mvn -pl msvc-sanction spring-boot:run "-Dspring-boot.run.profiles=persistente"
```

Cada módulo debe iniciarse con el nombre
indicado en la tabla de microservicios.

No es obligatorio iniciar todos los servicios
para probar una función aislada, pero sí deben
estar disponibles los servicios de los cuales
dependa esa operación.

Después de iniciar un servicio, comprobar en
Eureka que aparezca con estado UP.

---

## 13. Ejemplos de peticiones HTTP

### Iniciar sesión

```http
POST http://localhost:8080/auth/login
Content-Type: application/json
```

```json
{
  "username": "adminLocal",
  "password": "CONTRASENA_LOCAL_SEGURA"
}
```

### Consultar juegos

```http
GET http://localhost:8080/games
```

### Consultar torneos

```http
GET http://localhost:8080/tournaments
```

### Consultar perfiles como administrador

```http
GET http://localhost:8080/users
Authorization: Bearer TOKEN_JWT
```

### Consultar el perfil propio

```http
GET http://localhost:8080/users/mi-perfil
Authorization: Bearer TOKEN_JWT
```

### Vincular una cuenta Auth con un perfil User

```http
PUT http://localhost:8080/users/1/vincular-auth/2
Authorization: Bearer TOKEN_JWT_ADMIN
```

Los identificadores son ejemplos y deben
sustituirse por los registros reales existentes.

---

## 14. Pruebas automatizadas

Se implementaron pruebas con:

- JUnit 5
- Mockito
- MockMvc
- Spring Boot Test

Se realizaron pruebas de:

- Servicios y operaciones CRUD.
- Inicio de sesión.
- Validación de JWT.
- Autorización por roles.
- Accesos sin token.
- Accesos sin permisos.
- Vinculación entre Auth y User.
- Consulta del perfil propio.
- Protección contra vinculaciones duplicadas.
- Validación de sanciones.
- Bloqueo de eliminaciones con dependencias.
- Validaciones de datos y reglas de negocio.

### Pruebas específicas

Durante el desarrollo se obtuvieron:

| Módulo | Resultado |
|---|---|
| Auth | 13 pruebas correctas |
| User | 25 pruebas correctas |
| Frontend React | 63 pruebas correctas |
| Backend completo | BUILD SUCCESS |

Los resultados indicados corresponden a las
ejecuciones realizadas durante el desarrollo
de EP3.

### Ejecutar todas las pruebas del backend

Desde la raíz:

```powershell
mvn clean test
```

### Probar Auth

```powershell
mvn -pl msvc-auth -am clean test
```

### Probar User

```powershell
mvn -pl msvc-user -am clean test
```

Las pruebas utilizan la configuración de
base de datos predeterminada, sin activar
el perfil persistente.

---

## 15. Pruebas de integración realizadas

Además de las pruebas automatizadas, se
realizaron comprobaciones HTTP mediante
PowerShell y Gateway.

Entre las pruebas realizadas destacan:

**Autenticación**

- Login administrativo correcto.
- Comprobación del JWT.
- Verificación del rol y `authId`.

**Seguridad**

- Respuesta HTTP 401 sin autenticación.
- Respuesta HTTP 403 sin permisos.
- Consulta de perfil propio mediante JWT.

**Vinculación Auth–User**

- Vinculación correcta: HTTP 200.
- Cuenta ya vinculada: HTTP 409.
- Usuario normal sin acceso al listado:
  HTTP 403.

**Reglas de negocio**

- Impedir eliminar equipos con inscripciones.
- Impedir eliminar usuarios con sanciones.
- Impedir sanciones activas duplicadas.
- Validar que los usuarios sancionados existan.

**Persistencia**

Se comprobó que los datos permanecieran
disponibles después de reiniciar:

- Auth.
- User.
- Tournament.
- Team.
- Sanction.

Los demás servicios cuentan con configuración
persistente, pero su persistencia no fue
comprobada individualmente mediante reinicios
en esta etapa.

---

## 16. Ejecución del frontend

El frontend se encuentra en un repositorio
independiente:

https://github.com/itskevin02/Ea-Sports-Frontend

Para ejecutar React:

```powershell
git clone https://github.com/itskevin02/Ea-Sports-Frontend.git
cd Ea-Sports-Frontend/frontend-react
npm install
```

Configurar la URL de la API mediante un archivo
`.env` en `frontend-react`:

```properties
VITE_API_URL=http://localhost:8080
```

Iniciar el frontend:

```powershell
npm run dev
```

Dirección local habitual:

http://localhost:5173

### Compilar el frontend

```powershell
npm run build
```

### Ejecutar pruebas del frontend

```powershell
npm test
```

La configuración de Gateway incluye el origen
local de Vite para permitir las peticiones
desde el navegador.

---

## 17. Documentación de API

Los microservicios que tienen configurado
Swagger / OpenAPI permiten consultar su
documentación individual.

Ejemplo de Auth:

http://localhost:8081/swagger-ui.html

Ejemplo de User:

http://localhost:8083/swagger-ui.html

Ejemplo de Tournament:

http://localhost:8085/swagger-ui.html

Cada servicio debe estar iniciado para consultar
su documentación.

---

## 18. Control de versiones y trabajo colaborativo

Durante el desarrollo se utilizó un flujo
de trabajo basado en Git y GitHub.

### Ramas

**main**

Rama destinada a versiones estables.

**develop**

Rama utilizada para integrar los avances.

**feature/**

Ramas utilizadas para implementar
funcionalidades nuevas.

**fix/**

Ramas utilizadas para corregir errores.

### Procedimiento utilizado

1. Actualizar `develop`.
2. Crear una rama para la tarea.
3. Implementar los cambios.
4. Ejecutar las pruebas correspondientes.
5. Revisar los archivos modificados.
6. Realizar el commit.
7. Subir la rama a GitHub.
8. Crear un Pull Request hacia `develop`.
9. Revisar y fusionar los cambios.

Este procedimiento permite mantener
evidencia de los avances y sus autores.

---

## 19. Evolución del proyecto

### Primera etapa

Se desarrolló la estructura inicial
del backend distribuido.

Incluyó:

- Creación de microservicios.
- Entidades y repositorios.
- Controladores REST.
- Operaciones CRUD.
- Configuración de H2.
- Pruebas iniciales.

### Segunda etapa

Se desarrolló la interfaz React y
las funcionalidades del frontend.

Se trabajó con:

- Componentes reutilizables.
- Formularios.
- Validaciones.
- Navegación.
- Gestión de información.
- Pruebas automatizadas.
- Persistencia local en el navegador.

### Evaluación Parcial 3

Se avanzó en la integración del sistema.

Principales incorporaciones:

- Gateway.
- Eureka.
- Integración React con API REST.
- Autenticación JWT.
- Gestión de roles.
- Seguridad en servicios seleccionados.
- Manejo de sesión en React.
- Integración administrativa.
- Validaciones entre microservicios.
- Identificador `authId`.
- Vinculación entre cuentas y perfiles.
- Consulta de perfil propio.
- Persistencia H2 mediante perfil adicional.
- Pruebas Maven y comprobaciones HTTP.
- Pull Requests de integración y corrección.

---

## 20. Limitaciones y consideraciones

El sistema se desarrolló con fines académicos.

La configuración actual está orientada
principalmente a la ejecución local.

Se deben considerar las siguientes limitaciones:

- H2 persistente guarda archivos en el
  computador donde se ejecuta cada servicio.
- Las bases de datos en memoria no se
  migran automáticamente a H2 persistente.
- El perfil `persistente` debe activarse
  explícitamente al iniciar los servicios.
- Los vínculos entre microservicios no
  equivalen a claves foráneas compartidas
  entre bases de datos independientes.
- Si se elimina una cuenta Auth, puede
  requerirse revisar su perfil asociado.
- Las funciones de seguridad implementadas
  no cubren de forma idéntica todas las API.
- Se requieren nuevas comprobaciones y
  configuraciones para un entorno productivo.
- No se considera acreditado un despliegue
  público de producción por el solo hecho
  de funcionar localmente.

Estas consideraciones permiten distinguir
el alcance académico del proyecto de una
implementación productiva.

---

## 21. Resultados de la Evaluación Parcial 3

Al finalizar las implementaciones descritas,
se consiguió:

- Integrar los servicios principales con React.
- Autenticar cuentas mediante JWT.
- Diferenciar permisos administrativos.
- Consultar perfiles vinculados a cuentas Auth.
- Validar relaciones de negocio entre servicios.
- Evitar determinadas operaciones inconsistentes.
- Mantener los registros al reiniciar los
  servicios probados con H2 persistente.
- Ejecutar satisfactoriamente las pruebas Maven.
- Conservar evidencia de avances mediante
  ramas, commits y Pull Requests.

El resultado es una plataforma académica
de gestión eSports que integra frontend,
backend distribuido, seguridad, persistencia
y pruebas automatizadas.

---

## 22. Estado del proyecto

**Estado:** Versión académica EP3 integrada en `develop`.

**Backend:** Compilación y pruebas globales exitosas.

**Frontend:** Integración con las API principales
y pruebas automatizadas exitosas.

**Persistencia:** Configurada para los 12
microservicios de negocio, con verificaciones
de reinicio en los servicios indicados.

**Despliegue:** Validado localmente.
La preparación de un entorno productivo
requiere configuración y validaciones adicionales.

---

## 23. Conclusión

eSports Arena Manager permitió aplicar
conocimientos de desarrollo Fullstack,
arquitectura de microservicios, APIs REST,
seguridad JWT, validaciones, persistencia,
pruebas automatizadas y trabajo colaborativo.

Durante EP3 se consiguió avanzar desde
servicios y componentes inicialmente separados
hacia una aplicación integrada, en la que
React consume información del backend y
los microservicios colaboran para cumplir
las reglas de negocio implementadas.

El proyecto mantiene una estructura organizada
y una base para futuras mejoras.
