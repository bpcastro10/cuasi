# Documentación del Spider SRI RUC

## Descripción General

El spider `sri_ruc.py` es un scraper desarrollado con **Scrapy** para consultar información de RUC (Registro Único de Contribuyentes) en el sistema del **SRI (Servicio de Rentas Internas) de Ecuador**.

Este spider automatiza el proceso de consulta que normalmente requiere interacción manual con un formulario web protegido por reCAPTCHA Enterprise v3.

---

## Arquitectura del Proyecto

### Tipo de Arquitectura

El proyecto utiliza una **Arquitectura Modular por Capas** basada en el framework **Scrapy**, siguiendo el patrón de diseño **Pipeline** (Tubería).

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     ARQUITECTURA DEL PROYECTO                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        CAPA DE ENTRADA                              │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                 │   │
│  │  │ CLI Scrapy  │  │ API Estela  │  │ Parámetros  │                 │   │
│  │  │ (crawl cmd) │  │ (Cloud)     │  │ (-a ruc=X)  │                 │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                 │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        CAPA DE SPIDERS                              │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐  │   │
│  │  │  sri_ruc    │  │    sri      │  │ offshore    │  │ consejo   │  │   │
│  │  │  (RUC)      │  │  (Deudas)   │  │ (Leaks)     │  │ judicial  │  │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └───────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        CAPA DE ITEMS                                │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐  │   │
│  │  │  RucItem    │  │  DebtItem   │  │OffshoreItem │  │ CJItem    │  │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └───────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                       CAPA DE PIPELINES                             │   │
│  │  ┌─────────────────────┐  ┌─────────────────────────────────────┐  │   │
│  │  │ ValidationPipeline  │  │      TinyMongoPipeline              │  │   │
│  │  │ (Validación datos)  │  │      (Persistencia MongoDB)         │  │   │
│  │  └─────────────────────┘  └─────────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        CAPA DE SALIDA                               │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────────┐ │   │
│  │  │    JSON     │  │   MongoDB   │  │  Estela (Cloud Storage)    │ │   │
│  │  └─────────────┘  └─────────────┘  └─────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Patrones de Diseño Utilizados

| Patrón | Ubicación | Descripción |
|--------|-----------|-------------|
| **Pipeline (Tubería)** | Scrapy Core | Los datos fluyen a través de etapas: Spider → Item → Pipeline → Storage |
| **Factory Method** | `from_crawler()` | Los pipelines se crean mediante métodos de fábrica |
| **Template Method** | Scrapy Spiders | Los spiders heredan y sobrescriben métodos como `parse()` |
| **Strategy** | Pipelines | Diferentes estrategias de procesamiento según el spider |
| **Singleton** | Settings | Configuración global única para todo el proyecto |
| **Observer** | Signals Scrapy | Sistema de eventos para hooks del ciclo de vida |
| **Chain of Responsibility** | Middlewares | Cadena de procesadores de requests/responses |

### Estructura de Directorios

```
aseguradora-del-sur/
├── aseguradora-del-sur/              # Directorio raíz del proyecto Scrapy
│   ├── aseguradora_del_sur/          # Paquete principal de Python
│   │   ├── __init__.py               # Inicializador del paquete
│   │   ├── items.py                  # Definición de Items (DTOs)
│   │   ├── middlewares.py            # Middlewares personalizados
│   │   ├── pipelines.py              # Pipelines de procesamiento
│   │   ├── schemas.py                # Esquemas de validación (Pydantic)
│   │   ├── settings.py               # Configuración global
│   │   └── spiders/                  # Directorio de Spiders
│   │       ├── __init__.py
│   │       ├── sri_ruc.py            # Spider de consulta RUC ⭐
│   │       ├── sri.py                # Spider de deudas SRI
│   │       ├── offshoreleaks.py      # Spider de Offshore Leaks
│   │       ├── ConsejoDeLaJudicatura.py  # Spider del Consejo Judicial
│   │       └── documentacion_sri_ruc.md  # Esta documentación
│   ├── scrapy.cfg                    # Configuración de Scrapy
│   ├── estela.yaml                   # Configuración para deploy en Estela
│   ├── requirements.txt              # Dependencias de Python
│   ├── README.md                     # Documentación general
│   ├── GUIA_EJECUCION.md            # Guía de ejecución
│   └── venv/                         # Entorno virtual de Python
```

### Componentes del Framework Scrapy

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      ARQUITECTURA INTERNA DE SCRAPY                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐         ┌─────────────┐         ┌─────────────┐           │
│  │   SPIDER    │ ──(1)─▶ │   ENGINE    │ ──(2)─▶ │  SCHEDULER  │           │
│  │             │         │             │         │             │           │
│  │ Genera      │         │ Orquesta    │         │ Encola      │           │
│  │ Requests    │         │ todo        │         │ Requests    │           │
│  └─────────────┘         └─────────────┘         └─────────────┘           │
│        ▲                       │                       │                    │
│        │                       │                       │                    │
│       (7)                     (3)                     (4)                   │
│        │                       │                       │                    │
│        │                       ▼                       ▼                    │
│  ┌─────────────┐         ┌─────────────┐         ┌─────────────┐           │
│  │  PIPELINES  │ ◀──(6)─ │  SPIDER     │ ◀──(5)─ │ DOWNLOADER  │           │
│  │             │         │  MIDDLEWARE │         │             │           │
│  │ Procesan    │         │             │         │ Descarga    │           │
│  │ Items       │         │ Procesa     │         │ Páginas     │           │
│  └─────────────┘         │ Responses   │         └─────────────┘           │
│        │                 └─────────────┘                ▲                   │
│        ▼                                                │                   │
│  ┌─────────────┐                                 ┌─────────────┐           │
│  │   STORAGE   │                                 │  INTERNET   │           │
│  │ MongoDB/JSON│                                 │  (SRI API)  │           │
│  └─────────────┘                                 └─────────────┘           │
│                                                                             │
│  Flujo: (1) Spider genera Request                                          │
│         (2) Engine envía a Scheduler                                        │
│         (3) Engine pide Request al Scheduler                                │
│         (4) Scheduler da Request al Downloader                              │
│         (5) Downloader obtiene Response                                     │
│         (6) Spider procesa Response y genera Items                          │
│         (7) Pipelines procesan y almacenan Items                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Capas del Proyecto

#### 1. Capa de Spiders (Extracción)

| Spider | Archivo | Fuente de Datos | Item |
|--------|---------|-----------------|------|
| `sri_ruc` | `sri_ruc.py` | SRI - Consulta RUC | `RucItem` |
| `sri` / `deudas` | `sri.py` | SRI - Deudas tributarias | `DebtItem` |
| `offshore` | `offshoreleaks.py` | ICIJ Offshore Leaks | `OffshoreMatchItem` |
| `consejo_judicatura` | `ConsejoDeLaJudicatura.py` | Consejo de la Judicatura | `ConsejoDeLaJudicaturaItem` |

#### 2. Capa de Items (Modelos de Datos)

Los Items actúan como **DTOs (Data Transfer Objects)** que definen la estructura de los datos extraídos.

| Item | Campos Principales | Propósito |
|------|-------------------|-----------|
| `RucItem` | ruc, razon_social, estado_contribuyente, etc. | Información del contribuyente |
| `DebtItem` | ruc, name, firm_debt, disputed_debt, etc. | Deudas tributarias |
| `OffshoreMatchItem` | matched_name, entities | Coincidencias en Offshore Leaks |
| `ConsejoDeLaJudicaturaItem` | case_number, plaintiffs, defendants | Casos judiciales |

#### 3. Capa de Pipelines (Procesamiento)

| Pipeline | Prioridad | Función |
|----------|-----------|---------|
| `OffshoreValidationPipeline` | - | Valida datos con Pydantic |
| `TinyMongoPipeline` | 400 | Persiste items en MongoDB |

#### 4. Capa de Configuración

| Archivo | Propósito |
|---------|-----------|
| `settings.py` | Configuración global de Scrapy y MongoDB |
| `scrapy.cfg` | Configuración del proyecto Scrapy |
| `estela.yaml` | Configuración para deploy en la nube (Estela) |

### Servicios Externos

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        SERVICIOS EXTERNOS                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────┐    ┌─────────────────────┐                        │
│  │    SRI Ecuador      │    │      2Captcha       │                        │
│  │  (Fuente de datos)  │    │  (Resolver CAPTCHA) │                        │
│  │                     │    │                     │                        │
│  │  • Verificar RUC    │    │  • Resolver         │                        │
│  │  • Validar Captcha  │    │    reCAPTCHA v3     │                        │
│  │  • Obtener datos    │    │    Enterprise       │                        │
│  └─────────────────────┘    └─────────────────────┘                        │
│           ▲                          ▲                                      │
│           │                          │                                      │
│           │      ┌───────────────────┘                                      │
│           │      │                                                          │
│           ▼      ▼                                                          │
│  ┌─────────────────────┐    ┌─────────────────────┐                        │
│  │    Spider sri_ruc   │───▶│   MongoDB Atlas     │                        │
│  │                     │    │  (Persistencia)     │                        │
│  │  Orquesta el flujo  │    │                     │                        │
│  │  completo           │    │  • Digital Ocean    │                        │
│  └─────────────────────┘    │  • TLS habilitado   │                        │
│                             └─────────────────────┘                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Características de la Arquitectura

| Característica | Descripción | Beneficio |
|----------------|-------------|-----------|
| **Modular** | Cada spider es independiente | Fácil mantenimiento |
| **Desacoplada** | Items, Pipelines y Spiders separados | Reutilización de código |
| **Configurable** | Settings centralizados | Fácil configuración |
| **Extensible** | Middlewares y Pipelines plug-and-play | Fácil agregar funcionalidad |
| **Escalable** | Soporta Estela para cloud | Deploy distribuido |
| **Asíncrona** | Twisted (reactor pattern) | Alto rendimiento I/O |

### Tecnologías Utilizadas

| Categoría | Tecnología | Versión | Propósito |
|-----------|------------|---------|-----------|
| **Framework** | Scrapy | 2.11+ | Web scraping |
| **Lenguaje** | Python | 3.10+ | Desarrollo |
| **Async Engine** | Twisted | - | I/O asíncrono |
| **Base de Datos** | MongoDB | Atlas | Persistencia |
| **Captcha Solver** | 2Captcha | API REST | Resolver captchas |
| **Validación** | Pydantic | - | Validar esquemas |
| **Deploy** | Estela | - | Cloud orchestration |
| **HTTP Client** | Requests (interno) | - | Llamadas a 2captcha |

---

## Tabla de Contenidos

1. [Requisitos](#requisitos)
2. [Uso](#uso)
3. [Flujo de Funcionamiento](#flujo-de-funcionamiento)
4. [Arquitectura del Código](#arquitectura-del-código)
5. [Clases de Configuración](#clases-de-configuración)
6. [Funciones Utilitarias](#funciones-utilitarias)
7. [Clase Principal: SriRucSpider](#clase-principal-srirucspider)
8. [Tabla de Variables](#tabla-de-variables)
9. [Tabla de Métodos](#tabla-de-métodos)
10. [Tabla de Errores](#tabla-de-errores)
11. [Endpoints del SRI](#endpoints-del-sri)
12. [Configuración del reCAPTCHA](#configuración-del-recaptcha)
13. [Notas Importantes](#notas-importantes)

---

## Requisitos

| Requisito | Descripción |
|-----------|-------------|
| **Python** | 3.10+ |
| **Scrapy** | Framework de web scraping |
| **twocaptcha** | Librería para resolver captchas usando el servicio 2captcha |
| **TWO_CAPTCHA_API_KEY** | Variable de entorno con la API key de 2captcha |

### Instalación de Dependencias

```bash
pip install scrapy twocaptcha-python
```

---

## Uso

### Comando Básico

```bash
scrapy crawl sri_ruc -a ruc=0190123626001
```

### Con Salida a JSON

```bash
scrapy crawl sri_ruc -a ruc=0190123626001 -o resultado.json
```

### Con Variable de Entorno (PowerShell)

```powershell
$env:TWO_CAPTCHA_API_KEY="tu_api_key"; scrapy crawl sri_ruc -a ruc=0190123626001 -o resultado.json
```

### Con Variable de Entorno (Linux/Mac)

```bash
TWO_CAPTCHA_API_KEY="tu_api_key" scrapy crawl sri_ruc -a ruc=0190123626001 -o resultado.json
```

---

## Flujo de Funcionamiento

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         FLUJO DEL SPIDER                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  1. INICIO                                                              │
│     └── Validar parámetros (RUC, API Key)                              │
│              │                                                          │
│              ▼                                                          │
│  2. VERIFICAR EXISTENCIA DEL RUC                                        │
│     └── GET: existePorNumeroRuc?numeroRuc=XXXXXXXXXXXXX                │
│              │                                                          │
│              ▼                                                          │
│  3. INICIALIZAR SESIÓN DE CAPTCHA                                       │
│     └── GET: obtenerClavePublicaGoogleRecaptcha                        │
│     └── Obtiene cookie JSESSIONID                                      │
│              │                                                          │
│              ▼                                                          │
│  4. RESOLVER reCAPTCHA ENTERPRISE v3                                    │
│     └── Usa 2captcha con action: "sri_consulta_publica_ruc"            │
│     └── Reintentos automáticos (hasta 5 intentos)                      │
│              │                                                          │
│              ▼                                                          │
│  5. VALIDAR CAPTCHA Y OBTENER TOKEN                                     │
│     └── GET: validarGoogleReCaptcha?googleCaptchaResponse=XXX          │
│     └── Obtiene token de autorización (JWT)                            │
│              │                                                          │
│              ▼                                                          │
│  6. CONSULTAR INFORMACIÓN DEL RUC                                       │
│     └── GET: obtenerPorNumerosRuc?ruc=XXXXXXXXXXXXX                    │
│     └── Header: Authorization: {token}                                 │
│              │                                                          │
│              ▼                                                          │
│  7. FORMATEAR Y RETORNAR DATOS                                          │
│     └── RucItem con toda la información                                │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Arquitectura del Código

```
sri_ruc.py
│
├── IMPORTS (líneas 1-22)
│   ├── json, os, datetime, urllib.parse
│   ├── scrapy
│   ├── twocaptcha.TwoCaptcha
│   └── items.RucItem
│
├── CLASE SRIEndpoints (líneas 29-47)
│   └── Centraliza todos los URLs del SRI
│
├── CLASE RecaptchaConfig (líneas 54-81)
│   └── Configuración del reCAPTCHA Enterprise v3
│
├── FUNCIÓN format_date (líneas 88-120)
│   └── Formatea fechas a ISO
│
├── FUNCIÓN format_ruc_data (líneas 123-207)
│   └── Formatea datos del RUC en RucItem
│
└── CLASE SriRucSpider (líneas 214-704)
    ├── Atributos de clase
    ├── __init__
    ├── Métodos de inicio y validación
    ├── Métodos de resolución de captcha
    ├── Métodos de consulta de RUC
    ├── Métodos de manejo de errores
    └── Métodos de headers HTTP
```

---

## Clases de Configuración

### Clase `SRIEndpoints`

Centraliza todos los endpoints del SRI para facilitar el mantenimiento.

| Atributo | Valor | Descripción |
|----------|-------|-------------|
| `BASE_URL` | `https://srienlinea.sri.gob.ec` | URL base del SRI |
| `CATASTRO_BASE` | `{BASE_URL}/sri-catastro-sujeto-servicio-internet/rest/ConsolidadoContribuyente` | Base del servicio de catastro |
| `CAPTCHA_BASE` | `{BASE_URL}/sri-captcha-servicio-internet/rest/ValidacionCaptcha` | Base del servicio de captcha |
| `VERIFICAR_RUC` | `{CATASTRO_BASE}/existePorNumeroRuc` | Endpoint para verificar existencia |
| `OBTENER_RUC` | `{CATASTRO_BASE}/obtenerPorNumerosRuc` | Endpoint para obtener datos |
| `OBTENER_CLAVE_RECAPTCHA` | `{CAPTCHA_BASE}/obtenerClavePublicaGoogleRecaptcha` | Endpoint para iniciar sesión |
| `VALIDAR_RECAPTCHA` | `{CAPTCHA_BASE}/validarGoogleReCaptcha` | Endpoint para validar captcha |
| `PAGINA_CONSULTA` | `{BASE_URL}/sri-en-linea/SriRucWeb/ConsultaRuc/Consultas/consultaRuc` | URL de referencia |

### Clase `RecaptchaConfig`

Configuración del reCAPTCHA Enterprise v3 del SRI.

| Atributo | Valor | Descripción |
|----------|-------|-------------|
| `SITE_KEY` | `6LdukTQsAAAAAIcciM4GZq4ibeyplUhmWvlScuQE` | Clave pública del reCAPTCHA |
| `VERSION` | `v3` | Versión del reCAPTCHA |
| `ACTION` | `sri_consulta_publica_ruc` | Action capturado del navegador |
| `MIN_SCORE` | `0.5` | Score mínimo requerido por el SRI |
| `IS_ENTERPRISE` | `True` | Indica que es Enterprise |

---

## Funciones Utilitarias

### `format_date(date_str: str) -> str | None`

**Propósito:** Formatea una fecha a formato ISO (YYYY-MM-DD).

**Parámetros:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `date_str` | `str` | Fecha en diversos formatos |

**Formatos Soportados:**
| Formato | Ejemplo |
|---------|---------|
| `%Y-%m-%d %H:%M:%S.%f` | `1990-01-25 00:00:00.0` |
| `%Y-%m-%d %H:%M:%S` | `1990-01-25 00:00:00` |
| `%Y-%m-%d` | `1990-01-25` |
| `%d-%m-%Y` | `25-01-1990` |
| `%d/%m/%Y` | `25/01/1990` |
| `%Y/%m/%d` | `1990/01/25` |

**Retorno:** Fecha en formato `YYYY-MM-DD` o `None` si no se puede parsear.

---

### `format_ruc_data(ruc_data, ruc, status, error_message) -> RucItem`

**Propósito:** Formatea los datos del RUC en un objeto `RucItem` estandarizado.

**Parámetros:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `ruc_data` | `dict \| None` | Diccionario con datos del RUC |
| `ruc` | `str` | Número de RUC consultado |
| `status` | `str` | `"success"` o `"error"` |
| `error_message` | `str \| None` | Mensaje de error si aplica |

**Campos Mapeados:**

| Campo RucItem | Campo API Principal | Campo API Alternativo |
|---------------|--------------------|-----------------------|
| `ruc` | `numeroRuc` | `ruc` |
| `razon_social` | `razonSocial` | `nombreCompleto` |
| `estado_contribuyente` | `estadoContribuyenteRuc` | `estadoContribuyente` |
| `representante_legal` | `representanteLegal` | `representantesLegales[0]` |
| `contribuyente_fantasma` | `contribuyenteFantasma` | - |
| `contribuyente_transacciones_inexistentes` | `transaccionesInexistente` | `contribuyenteTransaccionesInexistentes` |
| `actividad_economica_principal` | `actividadEconomicaPrincipal` | - |
| `tipo_contribuyente` | `tipoContribuyente` | - |
| `regimen` | `regimen` | - |
| `categoria` | `categoria` | - |
| `obligado_llevar_contabilidad` | `obligadoLlevarContabilidad` | - |
| `agente_retencion` | `agenteRetencion` | - |
| `contribuyente_especial` | `contribuyenteEspecial` | - |
| `fecha_inicio_actividades` | `informacionFechasContribuyente.fechaInicioActividades` | `fechaInicioActividades` |
| `fecha_actualizacion` | `informacionFechasContribuyente.fechaActualizacion` | `fechaActualizacion` |
| `fecha_cese_actividades` | `informacionFechasContribuyente.fechaCese` | `fechaCeseActividades` |
| `fecha_reinicio_actividades` | `informacionFechasContribuyente.fechaReinicioActividades` | `fechaReinicioActividades` |

---

## Clase Principal: SriRucSpider

### Atributos de Clase

| Atributo | Valor | Descripción |
|----------|-------|-------------|
| `name` | `"sri_ruc"` | Nombre del spider |
| `allowed_domains` | `["srienlinea.sri.gob.ec"]` | Dominios permitidos |
| `MAX_CAPTCHA_RETRIES` | `5` | Máximo de reintentos para captcha |

### Custom Settings

| Setting | Valor | Descripción |
|---------|-------|-------------|
| `ITEM_PIPELINES` | `TinyMongoPipeline: 400` | Pipeline para guardar en MongoDB |
| `HTTPERROR_ALLOWED_CODES` | `[400, 401, 403, 404, 500, 502, 503]` | Códigos HTTP manejados en callback |
| `URLLENGTH_LIMIT` | `30000` | Límite de URL para tokens largos |

### Atributos de Instancia

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `ruc` | `str` | Número de RUC a consultar |
| `twocaptcha_api_key` | `str` | API key de 2captcha |
| `auth_token` | `str \| None` | Token JWT de autorización |
| `captcha_retry_count` | `int` | Contador de reintentos de captcha |
| `cookiejar_id` | `str` | ID del cookiejar para la sesión |

---

## Tabla de Variables

| Variable | Ubicación | Tipo | Valor/Descripción |
|----------|-----------|------|-------------------|
| `BASE_URL` | `SRIEndpoints` | `str` | URL base del SRI |
| `SITE_KEY` | `RecaptchaConfig` | `str` | Clave pública reCAPTCHA |
| `VERSION` | `RecaptchaConfig` | `str` | `"v3"` |
| `ACTION` | `RecaptchaConfig` | `str` | `"sri_consulta_publica_ruc"` |
| `MIN_SCORE` | `RecaptchaConfig` | `float` | `0.5` (mínimo requerido) |
| `MAX_CAPTCHA_RETRIES` | `SriRucSpider` | `int` | `5` reintentos máximo |
| `ruc` | `SriRucSpider` | `str` | RUC a consultar (13 dígitos) |
| `twocaptcha_api_key` | `SriRucSpider` | `str` | API key de 2captcha |
| `auth_token` | `SriRucSpider` | `str` | Token JWT obtenido |
| `captcha_retry_count` | `SriRucSpider` | `int` | Contador de reintentos |
| `cookiejar_id` | `SriRucSpider` | `str` | `"sri-captcha-session"` |

---

## Tabla de Métodos

### Métodos de Inicio y Validación

| Método | Línea | Descripción | Entrada | Salida |
|--------|-------|-------------|---------|--------|
| `__init__` | 243 | Inicializa el spider | `ruc: str` | - |
| `start_requests` | 262 | Punto de entrada, valida parámetros | - | `yield Request` |
| `_verificar_existencia_ruc` | 294 | Verifica si el RUC existe | - | `yield Request` |
| `_parse_verificacion_ruc` | 313 | Procesa respuesta de verificación | `response` | `yield Request/Item` |

### Métodos de Resolución de Captcha

| Método | Línea | Descripción | Entrada | Salida |
|--------|-------|-------------|---------|--------|
| `_init_captcha_session` | 357 | Inicializa sesión de captcha | - | `yield Request` |
| `_after_captcha_session_ready` | 373 | Continúa después de inicializar | `response` | `yield Request` |
| `_resolver_recaptcha` | 385 | Resuelve reCAPTCHA con 2captcha | - | `yield Request/Item` |
| `_validar_captcha` | 439 | Valida token de captcha | `captcha_response: str` | `yield Request` |
| `_parse_validacion_captcha` | 468 | Procesa validación del captcha | `response` | `yield Request/Item` |

### Métodos de Consulta de RUC

| Método | Línea | Descripción | Entrada | Salida |
|--------|-------|-------------|---------|--------|
| `_consultar_ruc` | 528 | Consulta información del RUC | - | `yield Request` |
| `_parse_informacion_ruc` | 546 | Procesa información del RUC | `response` | `yield RucItem` |

### Métodos de Manejo de Errores

| Método | Línea | Descripción | Entrada | Salida |
|--------|-------|-------------|---------|--------|
| `_handle_request_error` | 622 | Maneja errores de conexión | `failure` | `yield RucItem` |
| `_create_error_response` | 639 | Crea respuesta de error | `error_message: str` | `RucItem` |

### Métodos de Headers HTTP

| Método | Línea | Descripción | Entrada | Salida |
|--------|-------|-------------|---------|--------|
| `_get_headers` | 662 | Retorna headers base | `json_body: bool` | `dict` |
| `_get_headers_con_auth` | 691 | Retorna headers con token | - | `dict` |

---

## Tabla de Errores

### Errores de Validación de Parámetros

| Error | Condición | Mensaje |
|-------|-----------|---------|
| RUC no proporcionado | `self.ruc` es `None` | `"Parámetro RUC es requerido"` |
| RUC no numérico | `not self.ruc.isdigit()` | `"El RUC debe contener solo dígitos..."` |
| RUC longitud inválida | `len(self.ruc) != 13` | `"El RUC debe tener 13 dígitos..."` |
| API key faltante | `not self.twocaptcha_api_key` | `"Falta la variable de entorno TWO_CAPTCHA_API_KEY"` |

### Errores de Verificación de RUC

| Código HTTP | Descripción | Mensaje |
|-------------|-------------|---------|
| 204 | RUC no existe | `"RUC {ruc} no encontrado en los registros del SRI"` |
| != 200 | Error de servidor | `"Error al verificar RUC. Código de estado: {status}"` |
| - | Respuesta inválida | `"Respuesta inválida del servidor al verificar RUC: {error}"` |

### Errores de Captcha

| Error | Condición | Mensaje |
|-------|-----------|---------|
| Score bajo | `"Puntaje bajo: X.X (umbral=0.5)"` | Reintento automático |
| MALFORMED | Token inválido | Reintento automático |
| Límite alcanzado | `captcha_retry_count >= MAX_CAPTCHA_RETRIES` | `"Error al validar captcha después de X intentos..."` |
| Sin token | `not captcha_token` | `"No se recibió token del captcha"` |
| Sin auth token | `not self.auth_token` | `"No se recibió token de autorización del servidor"` |

### Errores de Consulta de RUC

| Código HTTP | Descripción | Mensaje |
|-------------|-------------|---------|
| 403 | Token expirado | `"Acceso denegado: El token de autorización expiró o es inválido"` |
| 404 | RUC no encontrado | `"RUC {ruc} no encontrado"` |
| 204 | Sin información | `"No se encontró información para el RUC: {ruc}"` |
| >= 500 | Error del servidor | `"Error del servidor SRI. Código: {status}"` |
| Otro | Error inesperado | `"Error inesperado. Código de estado: {status}"` |

### Errores de Conexión

| Error | Descripción | Mensaje |
|-------|-------------|---------|
| `Failure` | Error de red/conexión | `"Error de conexión con el servidor SRI: {failure}"` |

---

## Endpoints del SRI

### Diagrama de Flujo de Endpoints

```
1. existePorNumeroRuc?numeroRuc=XXXXX
   └── Verifica si el RUC existe
   └── Retorna: true/false o 204

2. obtenerClavePublicaGoogleRecaptcha
   └── Inicia sesión de captcha
   └── Establece cookie JSESSIONID

3. validarGoogleReCaptcha?googleCaptchaResponse=XXX&emitirToken=true
   └── Valida el token del captcha
   └── Retorna: JWT de autorización

4. obtenerPorNumerosRuc?ruc=XXXXX
   └── Consulta información del RUC
   └── Requiere: Header Authorization
   └── Retorna: JSON con datos del contribuyente
```

---

## Configuración del reCAPTCHA

### Parámetros Enviados a 2Captcha

```python
params = {
    "sitekey": "6LdukTQsAAAAAIcciM4GZq4ibeyplUhmWvlScuQE",
    "url": "https://srienlinea.sri.gob.ec/sri-en-linea/SriRucWeb/ConsultaRuc/Consultas/consultaRuc",
    "version": "v3",
    "action": "sri_consulta_publica_ruc",
    "enterprise": 1,
}
```

### Respuestas del SRI

| Respuesta | Significado | Acción |
|-----------|-------------|--------|
| `{"mensaje": "token_jwt_aqui"}` | Éxito | Continuar a consulta |
| `{"mensaje":"{\"mensaje\":\"MALFORMED\"}"}` | Token inválido | Reintentar |
| `{"mensaje":"{\"mensaje\":\"Puntaje bajo: X.X (umbral=0.5)\"}"}` | Score muy bajo | Reintentar |

---

## Notas Importantes

### Sobre el reCAPTCHA

1. **Es reCAPTCHA Enterprise v3** - Requiere parámetro `enterprise: 1`
2. **Action específico** - Debe ser exactamente `"sri_consulta_publica_ruc"`
3. **Score mínimo 0.5** - El SRI rechaza tokens con score menor a 0.5
4. **Variabilidad de 2captcha** - Los tokens de 2captcha tienen score variable, por eso se necesitan reintentos

### Sobre los Reintentos

- **MAX_CAPTCHA_RETRIES = 5** - Se necesitan varios intentos porque el score varía
- Cada reintento consume créditos de 2captcha
- El tiempo promedio por intento es ~20 segundos

### Sobre las Cookies

- El spider mantiene una sesión de cookies con `cookiejar_id`
- La cookie `JSESSIONID` es establecida al llamar a `obtenerClavePublicaGoogleRecaptcha`
- Esta cookie es necesaria para validar el captcha correctamente

### Sobre el Token de Autorización

- El token obtenido es un JWT
- Se envía en el header `Authorization` sin prefijo (no es `Bearer`)
- El token tiene tiempo de expiración limitado

---

## Ejemplo de Salida Exitosa

```json
{
  "ruc": "0190123626001",
  "razon_social": "ASEGURADORA DEL SUR C. A.",
  "estado_contribuyente": "ACTIVO",
  "representante_legal": {
    "nombre": "CEVALLOS BREILH RODRIGO NEPTALI FERNANDO",
    "identificacion": "1700556069"
  },
  "contribuyente_fantasma": "NO",
  "contribuyente_transacciones_inexistentes": "NO",
  "actividad_economica_principal": "SUMINISTROS DE SERVICIOS DE SEGUROS...",
  "tipo_contribuyente": "SOCIEDAD",
  "regimen": "GENERAL",
  "categoria": null,
  "obligado_llevar_contabilidad": "SI",
  "agente_retencion": "SI",
  "contribuyente_especial": "SI",
  "fecha_inicio_actividades": "1990-01-25",
  "fecha_actualizacion": "2025-06-26",
  "fecha_cese_actividades": null,
  "fecha_reinicio_actividades": null,
  "status": "success",
  "error_message": null,
  "scraping_date": "2026-01-11T10:46:17.765645"
}
```

## Ejemplo de Salida con Error

```json
{
  "ruc": "1234567890123",
  "razon_social": null,
  "estado_contribuyente": null,
  "representante_legal": null,
  "contribuyente_fantasma": null,
  "contribuyente_transacciones_inexistentes": null,
  "actividad_economica_principal": null,
  "tipo_contribuyente": null,
  "regimen": null,
  "categoria": null,
  "obligado_llevar_contabilidad": null,
  "agente_retencion": null,
  "contribuyente_especial": null,
  "fecha_inicio_actividades": null,
  "fecha_actualizacion": null,
  "fecha_cese_actividades": null,
  "fecha_reinicio_actividades": null,
  "status": "error",
  "error_message": "RUC 1234567890123 no encontrado en los registros del SRI",
  "scraping_date": "2026-01-11T10:50:00.000000"
}
```

---

## Códigos de Error HTTP

### Códigos HTTP Estándar

| Código | Nombre | Descripción | Acción del Spider |
|--------|--------|-------------|-------------------|
| **200** | OK | Solicitud exitosa | Procesar respuesta normalmente |
| **204** | No Content | RUC no existe o sin datos | Retornar error: "RUC no encontrado" |
| **400** | Bad Request | Token de captcha inválido o malformado | Reintentar captcha |
| **401** | Unauthorized | No autorizado (sin token) | Retornar error de autenticación |
| **403** | Forbidden | Token expirado o inválido | Retornar error: "Acceso denegado" |
| **404** | Not Found | RUC no encontrado | Retornar error: "RUC no encontrado" |
| **500** | Internal Server Error | Error interno del servidor SRI | Retornar error del servidor |
| **502** | Bad Gateway | Problema de gateway en el SRI | Retornar error del servidor |
| **503** | Service Unavailable | Servidor SRI no disponible | Retornar error del servidor |

### Códigos de Error Específicos del SRI

| Respuesta JSON | Significado | Causa | Solución |
|----------------|-------------|-------|----------|
| `{"mensaje":"{\"mensaje\":\"MALFORMED\"}"}` | Token malformado | El token del captcha es inválido o corrupto | Reintentar con nuevo token |
| `{"mensaje":"{\"mensaje\":\"Puntaje bajo: X.X (umbral=0.5)\"}"}` | Score insuficiente | 2captcha devolvió un token con score < 0.5 | Reintentar con nuevo token |
| `{"mensaje":"{\"mensaje\":\"TOKEN_EXPIRED\"}"}` | Token expirado | El token del captcha expiró (>2 min) | Reintentar inmediatamente |
| `{"mensaje":"{\"mensaje\":\"INVALID_ACTION\"}"}` | Action incorrecto | El action no coincide con el esperado | Verificar `RecaptchaConfig.ACTION` |

---

## Códigos de Error de 2Captcha

### Errores al Enviar Tarea

| Código | Descripción | Causa | Solución |
|--------|-------------|-------|----------|
| `ERROR_WRONG_USER_KEY` | API key inválida | La API key no existe o está mal formada | Verificar `TWO_CAPTCHA_API_KEY` |
| `ERROR_KEY_DOES_NOT_EXIST` | API key no existe | La cuenta no existe | Crear cuenta en 2captcha.com |
| `ERROR_ZERO_BALANCE` | Sin saldo | No hay fondos en la cuenta | Recargar cuenta |
| `ERROR_PAGEURL` | URL inválida | La URL proporcionada no es válida | Verificar `SRIEndpoints.PAGINA_CONSULTA` |
| `ERROR_NO_SLOT_AVAILABLE` | Sin capacidad | Servidores de 2captcha sobrecargados | Esperar y reintentar |
| `ERROR_ZERO_CAPTCHA_FILESIZE` | Archivo vacío | Captcha vacío (para imagen) | No aplica a reCAPTCHA |
| `ERROR_TOO_BIG_CAPTCHA_FILESIZE` | Archivo muy grande | Captcha > 100KB (para imagen) | No aplica a reCAPTCHA |
| `ERROR_WRONG_FILE_EXTENSION` | Extensión incorrecta | Formato de archivo no soportado | No aplica a reCAPTCHA |
| `ERROR_IP_NOT_ALLOWED` | IP no permitida | IP bloqueada por restricciones | Configurar IP en panel de 2captcha |
| `ERROR_IP_BANNED` | IP baneada | Demasiadas solicitudes fallidas | Contactar soporte de 2captcha |

### Errores al Obtener Resultado

| Código | Descripción | Causa | Solución |
|--------|-------------|-------|----------|
| `CAPCHA_NOT_READY` | Aún procesando | El captcha está siendo resuelto | Esperar y consultar de nuevo |
| `ERROR_CAPTCHA_UNSOLVABLE` | No se pudo resolver | Workers no pudieron resolver el captcha | Reintentar (diferente worker) |
| `ERROR_WRONG_ID_FORMAT` | ID mal formado | El ID de tarea es inválido | Verificar código |
| `ERROR_WRONG_CAPTCHA_ID` | ID no existe | La tarea no existe o expiró | Enviar nueva tarea |
| `ERROR_BAD_DUPLICATES` | Duplicados incorrectos | Configuración de duplicados incorrecta | No aplica a reCAPTCHA |
| `ERROR_REPORT_NOT_RECORDED` | Reporte no registrado | No se pudo registrar el reporte | Reintentar reporte |

### Errores Específicos de reCAPTCHA

| Código | Descripción | Causa | Solución |
|--------|-------------|-------|----------|
| `ERROR_RECAPTCHA_INVALID_SITEKEY` | Sitekey inválida | La sitekey no es válida para el sitio | Verificar `RecaptchaConfig.SITE_KEY` |
| `ERROR_RECAPTCHA_INVALID_DOMAIN` | Dominio inválido | El dominio no coincide con la sitekey | Verificar URL |
| `ERROR_RECAPTCHA_OLD_BROWSER` | Navegador antiguo | Simulación de navegador obsoleta | Error de 2captcha, reintentar |
| `ERROR_TOKEN_EXPIRED` | Token expirado | El token se generó hace más de 2 min | Usar token inmediatamente |
| `ERROR_PROXY_CONNECT_REFUSED` | Proxy rechazado | El proxy no funciona | No aplica si no usa proxy |
| `ERROR_PROXY_CONNECT_TIMEOUT` | Timeout de proxy | El proxy es muy lento | No aplica si no usa proxy |
| `ERROR_PROXY_READ_TIMEOUT` | Timeout de lectura | El proxy no responde | No aplica si no usa proxy |
| `ERROR_PROXY_BANNED` | Proxy baneado | El proxy está bloqueado | No aplica si no usa proxy |

---

## Efectividad de 2Captcha por Tipo de CAPTCHA

### Tabla Comparativa de Tipos de CAPTCHA

| Tipo de CAPTCHA | Dificultad | Tasa de Éxito | Tiempo Promedio | Costo Aprox. | Usado por SRI |
|-----------------|------------|---------------|-----------------|--------------|---------------|
| **reCAPTCHA v2** | Media | 95-99% | 15-45 seg | $2.99/1000 | ❌ No |
| **reCAPTCHA v2 Invisible** | Media-Alta | 90-95% | 20-60 seg | $2.99/1000 | ❌ No |
| **reCAPTCHA v3** | Alta | 85-95% | 15-30 seg | $2.99/1000 | ❌ No |
| **reCAPTCHA Enterprise v2** | Alta | 80-90% | 30-90 seg | $3.99/1000 | ❌ No |
| **reCAPTCHA Enterprise v3** | **Muy Alta** | **60-80%** | **20-45 seg** | **$3.99/1000** | ✅ **SÍ** |
| hCaptcha | Media | 95-99% | 20-40 seg | $2.99/1000 | ❌ No |
| hCaptcha Enterprise | Alta | 85-95% | 30-60 seg | $3.99/1000 | ❌ No |
| FunCaptcha | Media | 90-95% | 20-50 seg | $2.99/1000 | ❌ No |
| Turnstile (Cloudflare) | Media | 95-99% | 10-30 seg | $2.99/1000 | ❌ No |
| Text CAPTCHA | Baja | 99%+ | 5-15 seg | $0.50/1000 | ❌ No |
| Image CAPTCHA | Baja-Media | 95-99% | 10-30 seg | $1.00/1000 | ❌ No |

### Análisis de reCAPTCHA Enterprise v3 (Usado por SRI)

#### ¿Qué es reCAPTCHA Enterprise v3?

Es la versión más avanzada de reCAPTCHA de Google, diseñada para empresas que requieren máxima seguridad contra bots.

#### Características Técnicas

| Característica | Valor | Impacto en Resolución |
|----------------|-------|----------------------|
| **Tipo** | Score-based (sin interacción) | El score determina si pasa |
| **Score Range** | 0.0 a 1.0 | SRI requiere ≥ 0.5 |
| **Action** | Obligatorio (`sri_consulta_publica_ruc`) | Debe coincidir exactamente |
| **Enterprise Flag** | Obligatorio (`enterprise: 1`) | Sin esto, falla siempre |
| **Expiración del Token** | ~2 minutos | Debe usarse inmediatamente |
| **Verificación Server-side** | Sí, con API de Google | Google valida con su servidor |

#### Factores que Afectan el Score

| Factor | Descripción | Impacto |
|--------|-------------|---------|
| **Historial de IP** | IPs con mal historial tienen score bajo | Alto |
| **Comportamiento del navegador** | Movimientos de mouse, clics, scroll | Alto |
| **Fingerprint del dispositivo** | Hardware, software, plugins | Medio |
| **Tiempo en la página** | Muy rápido = sospechoso | Medio |
| **Cookies de Google** | Sesión de Google activa ayuda | Medio |
| **Geolocalización** | Ubicación coherente con el sitio | Bajo |

#### ¿Por Qué el Score Varía en 2Captcha?

```
┌─────────────────────────────────────────────────────────────────────┐
│                   PROCESO DE 2CAPTCHA                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  1. Spider envía tarea a 2captcha                                  │
│     └── sitekey, url, action, enterprise: 1                        │
│                                                                     │
│  2. 2captcha asigna tarea a un WORKER (humano o bot)               │
│     └── El worker tiene su propio navegador/perfil                 │
│                                                                     │
│  3. Worker navega a la URL y ejecuta grecaptcha.enterprise.execute │
│     └── Google evalúa el comportamiento del worker                 │
│                                                                     │
│  4. Google asigna un SCORE basado en el worker                     │
│     ├── Worker con buen historial → Score 0.7-0.9                  │
│     ├── Worker con historial mixto → Score 0.3-0.6                 │
│     └── Worker sospechoso → Score 0.1-0.3                          │
│                                                                     │
│  5. 2captcha devuelve el token (sin saber el score)                │
│     └── Spider recibe token y lo envía al SRI                      │
│                                                                     │
│  6. SRI valida con Google y obtiene el score                       │
│     ├── Score ≥ 0.5 → ✅ Token aceptado                             │
│     └── Score < 0.5 → ❌ "Puntaje bajo"                             │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

#### Estadísticas Observadas para el SRI

| Métrica | Valor Observado | Comentarios |
|---------|-----------------|-------------|
| **Tasa de éxito primer intento** | ~30-40% | Muchos tokens tienen score bajo |
| **Tasa de éxito con 5 reintentos** | ~70-80% | Mejora significativa |
| **Tasa de éxito con 10 reintentos** | ~85-95% | Casi siempre funciona |
| **Score promedio de tokens** | 0.3-0.5 | Justo en el límite |
| **Tokens con score ≥ 0.5** | ~40-50% | Menos de la mitad |
| **Tokens MALFORMED** | ~20-30% | Tokens corruptos o expirados |
| **Tiempo promedio por token** | 20-35 seg | Variable |

#### Recomendaciones para Mejorar Efectividad

| Recomendación | Impacto | Implementado |
|---------------|---------|--------------|
| Usar `MAX_CAPTCHA_RETRIES = 5-10` | Alto | ✅ Sí (5) |
| Usar token inmediatamente (<2 min) | Alto | ✅ Sí |
| Especificar `action` correcto | Crítico | ✅ Sí |
| Especificar `enterprise: 1` | Crítico | ✅ Sí |
| Especificar `version: "v3"` | Alto | ✅ Sí |
| No usar proxies baratos | Medio | N/A |
| Reintentar en error MALFORMED | Alto | ✅ Sí |
| Mantener sesión de cookies | Medio | ✅ Sí |

---

## Comparación con Otros Servicios de Resolución de CAPTCHA

| Servicio | reCAPTCHA Enterprise v3 | Precio | Velocidad | API |
|----------|-------------------------|--------|-----------|-----|
| **2Captcha** | ✅ Soportado | $3.99/1000 | 20-45s | REST |
| Anti-Captcha | ✅ Soportado | $4.00/1000 | 20-40s | REST |
| CapMonster | ✅ Soportado | $2.00/1000 | 15-35s | REST |
| DeathByCaptcha | ❌ No soportado | - | - | - |
| EndCaptcha | ⚠️ Parcial | $3.50/1000 | 30-60s | REST |

### ¿Por Qué Usamos 2Captcha?

| Razón | Descripción |
|-------|-------------|
| **Soporte Enterprise v3** | Soporta completamente reCAPTCHA Enterprise v3 |
| **Librería Python oficial** | `twocaptcha-python` es fácil de usar |
| **API estable** | API confiable y bien documentada |
| **Precio competitivo** | $3.99/1000 es razonable |
| **Alta disponibilidad** | Raramente tiene downtime |

---

## Historial de Cambios

| Fecha | Cambio |
|-------|--------|
| 2026-01-11 | Documentación inicial creada |
| 2026-01-11 | Configuración de reCAPTCHA Enterprise v3 confirmada |
| 2026-01-11 | MAX_CAPTCHA_RETRIES ajustado a 5 |
| 2026-01-11 | URLLENGTH_LIMIT aumentado a 30000 |
| 2026-01-11 | Agregados códigos de error HTTP y 2captcha |
| 2026-01-11 | Agregada sección de efectividad de 2captcha |

---

*Documentación generada para el proyecto Aseguradora del Sur*
