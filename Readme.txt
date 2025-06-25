Sistema de Gestión de Aeropuertos y Rutas de Vuelo
Desarrollado por: Alejandro Porras, Bryan Rojas y Caleb Hernández.
Descripción del Proyecto:
Sistema desarrollado en Java con JavaFX para simular la gestión de una red de aeropuertos de manera dinámica. Permite simular operaciones principales como compra de boletos, registro de pasajeros, control de vuelos y manejo de puertas de embarque.
Características Principales

✈️ Gestión completa de aeropuertos y vuelos
👥 Sistema de autenticación con roles (Admin/Usuario)
🔄 Simulación en tiempo real de operaciones aeroportuarias
📊 Generación de estadísticas y reportes
🗺️ Cálculo de rutas más cortas usando algoritmo de Dijkstra
💾 Persistencia de datos en JSON

Tecnologías Utilizadas

Lenguaje: Java 11+
Interfaz: JavaFX
Persistencia: JSON (Gson)
Patrones: MVC, Singleton, Factory

Estructuras de Datos Implementadas
1. Sistema de Seguridad

TDA: Circular Linked List
Uso: Gestión de usuarios y autenticación
Características: Encriptación de contraseñas, roles de usuario

2. Aeropuertos

TDA: Doubly Linked List
Uso: CRUD de aeropuertos
Características: Activar/desactivar, listar por estado

3. Pasajeros

TDA: Binary Search Tree AVL
Uso: Registro y búsqueda eficiente de pasajeros
Características: Búsqueda por cédula, historial de vuelos

4. Vuelos

TDA: Circular Doubly Linked List
Uso: Gestión de vuelos activos y completados
Características: Asignación de pasajeros, simulación de vuelo

5. Rutas entre Aeropuertos

TDA: Directed Singly Linked List Graph (ponderado)
Uso: Conexiones entre aeropuertos con distancias
Características: Algoritmo de Dijkstra para ruta más corta

6. Simulación de Operaciones

LinkedQueue: Colas de embarque por aeropuerto
LinkedStack: Historial de vuelos por avión
Uso: Simulación realista de operaciones aeroportuarias

Funcionalidades por Rol
Administrador

✅ Gestión completa de aeropuertos
✅ Gestión de vuelos y pasajeros
✅ Simulaciones del sistema
✅ Generación de estadísticas
✅ Exportación de reportes

Usuario Regular

✅ Consulta de vuelos disponibles
✅ Reservación de boletos
✅ Historial personal de vuelos
✅ Gestión de reservaciones

Instalación y Ejecución
Prerrequisitos

Java JDK 11 o superior
JavaFX SDK
IDE compatible (IntelliJ IDEA, Eclipse, NetBeans)

Pasos de Instalación

Clonar el repositorio

bashgit clone [URL_del_repositorio]
cd sistema-aeropuertos

Configurar JavaFX en el IDE
Compilar y ejecutar HelloApplication.java

Usuarios por Defecto

Administrador: admin / admin123
Usuario: user / user123

Estructura del Proyecto
src/
├── main/java/
│   ├── controller/          # Controladores JavaFX
│   ├── domain/             # Clases del dominio
│   │   ├── graph/          # Implementación de grafos
│   │   ├── list/           # Listas enlazadas
│   │   ├── queue/          # Colas
│   │   ├── stack/          # Pilas
│   │   ├── tree/           # Árboles AVL
│   │   └── security/       # Sistema de autenticación
│   ├── simulation/         # Simulación por consola
│   ├── util/              # Utilidades
│   └── ucr/project/       # Aplicación principal
└── resources/
    ├── ucr/project/       # Archivos FXML y CSS
    └── data/              # Archivos JSON de datos
Algoritmos Principales
Algoritmo de Dijkstra
Implementado para calcular la ruta más corta entre aeropuertos:

Complejidad: O(V² + E)
Usa grafos dirigidos ponderados
Considera distancias en kilómetros

Operaciones AVL Tree
Para gestión eficiente de pasajeros:

Inserción: O(log n)
Búsqueda: O(log n)
Balance automático

Simulación del Sistema
Primer Entregable (Semana 1-2)

✅ Interfaz gráfica básica
✅ Sistema de seguridad
✅ Carga de aeropuertos desde JSON
✅ Implementación de Dijkstra
✅ Simulación de red aérea por consola

Segundo Entregable (Semana 3-4)

✅ Colas de espera por aeropuerto
✅ Simulación completa de vuelos
✅ Generación de estadísticas
✅ Persistencia completa en JSON

Reportes y Estadísticas
El sistema genera los siguientes reportes:

📈 Top 5 aeropuertos con más vuelos salientes
🛣️ Rutas más utilizadas
👤 Pasajeros con más vuelos realizados
📊 Porcentaje de ocupación promedio por vuelo

Validaciones Implementadas

✅ Validación de campos obligatorios
✅ Validación de formatos de fecha
✅ Validación de campos numéricos
✅ Encriptación de contraseñas
✅ Control de acceso por roles

Persistencia de Datos
Estrategia de Manejo de Archivos

Al iniciar: Cargar datos desde JSON a estructuras TDA
Durante ejecución: Trabajar solo con estructuras en memoria
Al finalizar: Guardar cambios en archivos JSON

Archivos de Datos

airports.json - Lista de aeropuertos
passengers.json - Registro de pasajeros
flights.json - Información de vuelos
users.json - Usuarios del sistema

Reglas de Negocio
Inicio del Sistema

Cargar 15-20 aeropuertos iniciales
Generar rutas ponderadas aleatorias
Evitar ciclos en las rutas

Generación de Vuelos

Seleccionar 5 aeropuertos con más rutas
Crear vuelos aleatorios hacia otros destinos
Capacidad: 100, 150 o 200 pasajeros

Compra de Tiquetes

Selección de origen y destino
Encolar si no hay cupo disponible
Prohibir vuelos al mismo aeropuerto

Patrones de Diseño Utilizados

MVC: Separación de lógica, vista y control
Singleton: Para servicios únicos (autenticación)
Factory: Para creación de objetos complejos
Observer: Para notificaciones del sistema