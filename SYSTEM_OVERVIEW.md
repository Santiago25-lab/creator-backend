# CreatorCV - Arquitectura y Funcionamiento del Sistema

Este documento describe la estructura técnica y el flujo de datos del proyecto CreatorCV, integrando el backend en Spring Boot y el frontend en React.

## 1. Arquitectura General
El sistema sigue un modelo de cliente-servidor desacoplado:
- **Frontend**: Single Page Application (SPA) construida con React que gestiona la interfaz de usuario, la composición visual de plantillas y la exportación de documentos.
- **Backend**: API REST construida con Spring Boot que actúa como puente con servicios de Inteligencia Artificial y gestiona la lógica de negocio.

## 2. Flujo de Datos del CV
El "alma" del proyecto es un objeto JSON centralizado que contiene:
- `personalInfo`: Datos básicos, contacto y foto (en Base64).
- `experience`, `education`, `skills`, `languages`: Listas de logros del usuario.

### Flujo de Generación IA:
1. El usuario escribe una instrucción en el chat del frontend.
2. El frontend envía el estado actual del CV + el mensaje del usuario al endpoint `/api/cv/ai/generate`.
3. El backend (usando `OpenRouterService`) construye un prompt especializado y consulta a un modelo de lenguaje (como Claude o GPT).
4. El modelo devuelve un JSON con los datos mejorados.
5. El frontend recibe el JSON y actualiza la previsualización en tiempo real.

## 3. Motor de Composición (Templates)
CreatorCV utiliza un sistema modular para permitir diseños infinitos:
- **Registry**: Un catálogo de bloques (Headers, ExperienceBlocks, etc.).
- **Recipes**: Un objeto que define qué bloques componen una plantilla personalizada.
- **Persistence (Actual)**: Actualmente utiliza `localStorage` para guardar "Mis Diseños", almacenando solo la "receta" (IDs de los bloques elegidos).

## 4. Sistema de Exportación
- Se utiliza la librería `html2pdf.js`.
- El sistema captura el DOM de la hoja de vida (escalado a tamaño A4) y genera un PDF de alta resolución.
- Los documentos adjuntos se renderizan como páginas adicionales dentro del mismo PDF.

## 5. Próxima Evolución: Persistencia en la Nube (Supabase)
El siguiente paso consiste en sustituir el almacenamiento local por una infraestructura en la nube:
- **Auth**: Gestión de usuarios mediante Supabase Auth.
- **DB (PostgreSQL)**: Almacenamiento persistente de perfiles y diseños guardados vinculados al ID único de cada usuario (`uid`).
- **Storage**: Gestión de fotos de perfil y documentos adjuntos en buckets de Supabase.

---
*Documento generado por Antigravity AI - Mayo 2026*
