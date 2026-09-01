# FinTrack - Guía de Desarrollo e Instalación

## Arquitectura Integrada

FinTrack ahora funciona como una **aplicación monolítica**: el frontend React se compila automáticamente y se sirve desde Spring Boot.

## Inicio Rápido

### Requisitos Previos
- Java 21
- Node.js 18+ y npm
- MySQL 8.0+ (para producción)

### Startup (Una sola línea)

**OPCIÓN 1: Con interfaz gráfica (recomendado)**
```bash
# Windows - Abre VS Code terminal
gradlew bootRun
```

**OPCION 2: Desde PowerShell**
```powershell
cd c:\Users\Juan\Documents\GitHub\fintrack
& ".\gradlew.bat" bootRun
```

La aplicación estará disponible en: **http://localhost:8080**

## Flujo de Inicio

1. **Frontend Build** (automático)
   ```
   gradlew copyFrontendDist
   └─ npm run build (compila React)
   └─ Copia dist → src/main/resources/static/
   ```

2. **Backend Start**
   ```
   gradlew bootRun
   └─ Spring Boot inicia en puerto 8080
   └─ Sirve frontend desde /
   └─ Sirve API desde /api/**
   ```

## Desarrollo

### Frontend Only (Con hot reload)
```bash
cd frontend
npm run dev
```
API proxy está configurado en `vite.config.ts` para redirigir `/api` a `http://localhost:8080`

### Backend Only
```bash
gradlew bootRun
# Sin recompilación de frontend (usa archivos anteriores)
```

### Full Stack
```bash
gradlew bootRun
# Compila frontend + inicia backend
```

## Estructura de Carpetas

```
fintrack/
├── src/main/
│   ├── java/                    # Backend Spring Boot
│   └── resources/
│       ├── static/              # Frontend compilado (auto-generado)
│       └── application*.properties
├── frontend/
│   ├── src/                     # Source React + TypeScript
│   ├── dist/                    # Compilado (auto-generado)
│   └── package.json
├── build.gradle                 # Configuración Gradle con tareas frontend
└── .gitignore                   # Ignora dist/ y static/
```

## Tareas Gradle Disponibles

```bash
gradlew buildFrontend          # Solo compile React
gradlew copyFrontendDist       # Copy React dist a Spring Boot
gradlew build                  # Build JAR (con frontend compilado)
gradlew bootRun                # Inicia servidor (con frontend)
gradlew clean                  # Limpia todo (frontend + backend)
```

## Base de Datos

### Development (H2 en memoria)
```bash
gradlew bootRun
# Accede a consola: http://localhost:8080/h2-console
```

### Production (MySQL)
```bash
gradlew bootRun --args='--spring.profiles.active=mysql'
```

Asegúrate de que las variables de entorno estén configuradas:
```bash
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your_password"
```

## Configuración de Puertos

- **Frontend**: http://localhost:8080
- **API**: http://localhost:8080/api
- **H2 Console**: http://localhost:8080/h2-console

## Build para Producción

```bash
# Compila frontend + backend en un único JAR
gradlew clean build

# Ejecuta JAR
java -jar build/libs/fintrack-0.0.1-SNAPSHOT.jar
```

## Troubleshooting

### "npm not found"
Si npm no está en el PATH, el build.gradle intenta encontrarlo automáticamente. Si falla:
```powershell
# Verifica la ubicación
Get-Command npm | Select-Object -ExpandProperty Source
```

### Frontend no se actualiza después de cambios
```bash
cd frontend
npm run build                   # Compile manualmente
gradlew copyFrontendDist        # Copy a Spring Boot
gradlew bootRun                 # Restart server
```

### "Address already in use"
Puerto 8080 está ocupado:
```bash
# Cambiar puerto en application.properties
server.port=3000

# O matar el proceso
Get-Process java | Stop-Process -Force
```

## Notas Importantes

✅ **Frontend y Backend se construyen juntos**
✅ **Un solo JAR para production**
✅ **Hot reload disponible en desarrollo (npm run dev)**
✅ **API proxy automático en vite.config.ts**
❌ **NO ejecutar frontend y backend por separado** (a menos que sea desarrollo)

## Próximos Pasos

1. ✅ Estructura integrada completada
2. ⏳ Agregar filtros de transacciones
3. ⏳ Agregar búsqueda avanzada
4. ⏳ Mejorar validaciones backend
5. ⏳ Agregar recuperación de contraseña
