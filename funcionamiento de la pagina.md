# 🚀 Funcionamiento de la Página — CreatorCV

CreatorCV no es solo una herramienta de edición de plantillas; es un ecosistema completo de **Ingeniería de Empleabilidad** impulsado por Inteligencia Artificial. Está diseñado para transformar perfiles informales en currículums de alto impacto que superan los filtros ATS y cautivan a los reclutadores de empresas Fortune 500.

---

## 🛠️ Funcionalidades Core (A Detalle)

### 1. 🤖 Redactor IA (Headhunter Virtual)
El corazón de la aplicación es un chat con una IA configurada con la personalidad de un **Headhunter Senior con 15 años de experiencia**.
- **Transformación de Contenido**: Convierte frases simples como *"hice una app"* en *"Lideré el desarrollo de una arquitectura escalable, incrementando el rendimiento en un 40%"*.
- **Comportamiento Proactivo**: Si detecta que falta información clave (ej. fechas o logros cuantitativos), la IA te preguntará de forma natural para completar el perfil.
- **Regeneración de Estilo**: Con un solo clic, puedes reescribir todo tu CV con un tono completamente diferente (más ejecutivo, más creativo, o más técnico).

### 2. 🎨 Clonación de Diseño por Imagen (Vision IA)
CreatorCV permite "inspirarse" en diseños existentes de forma inteligente.
- **Ingeniería Inversa de UI**: Sube una foto de un CV que te guste, y la IA clonará la estructura, colores y tipografía, inyectando automáticamente tus datos reales mediante marcadores dinámicos.
- **Análisis de Estilo**: Describe con palabras el estilo que buscas (ej: *"Quiero algo minimalista con tonos crema y tipografía serif elegante"*) y la IA generará una "Receta de Diseño" que configura el editor automáticamente.

### 3. 📎 Gestión y Análisis de Documentos (OCR + Extraction)
Sube tus diplomas, certificaciones o constancias laborales para que la IA haga el trabajo pesado por ti.
- **Extracción de Datos**: Al subir un PDF o imagen, la IA analiza el texto (vía PDFBox o Vision) y extrae cargos, períodos, instituciones y habilidades, permitiéndote añadirlos a tu CV con un solo botón.
- **Anexo de Soportes**: Los documentos subidos se convierten automáticamente en **hojas de soporte** adicionales en el PDF final, permitiendo que tu CV sea un portafolio verificable completo.

### 4. 🧩 Motor de Composición Modular
A diferencia de las plantillas estáticas tradicionales, CreatorCV usa un motor de composición:
- **Mezcla de Bloques**: Puedes elegir un encabezado tipo "Bold", una sección de experiencia "ATS Friendly" y una barra lateral de habilidades "Creative", creando miles de combinaciones únicas.
- **Control de Temas**: Ajuste dinámico de colores primarios, tipografías de Google Fonts, radio de bordes y espaciado en tiempo real.

### 5. 💾 Persistencia y Robustez
- **Sincronización Dual**: Los cambios se guardan automáticamente en el navegador (`localStorage`) y pueden persistirse en una base de datos centralizada (H2) mediante el backend.
- **Exportación Profesional**: Generación de PDF de alta calidad usando `html2pdf.js`, optimizado para impresión y lectura digital.

---

## 🏗️ Arquitectura Técnica

### Frontend (React + Vite)
- **Hooks Personalizados**: Lógica de negocio separada de la UI (`useCvData`, `useChatIA`).
- **Diseño Moderno**: Interfaz tipo "Glassmorphism" con animaciones fluidas y feedback visual constante.
- **Seguridad**: Implementación de `ErrorBoundary` global para asegurar que errores menores no detengan la experiencia del usuario.

### Backend (Spring Boot 3.4)
- **Capa de Servicios**: `AIService` centraliza la lógica de OpenRouter, optimizando el uso de tokens y manejando límites de velocidad.
- **Prompt Engineering**: `PromptBuilder` encapsula instrucciones de sistema altamente optimizadas para garantizar la calidad de la redacción.
- **Almacenamiento**: Gestión de archivos físicos para documentos adjuntos y base de datos relacional para perfiles de usuario.

---

## 🚀 Cómo Empezar

1. **Configuración**: Copia el archivo `.env.example` a `.env` en el frontend y añade tu `OPENROUTER_API_KEY`.
2. **Backend**: Ejecuta el servidor Java (puerto 8081).
3. **Frontend**: `npm install` seguido de `npm run dev` (puerto 5173).

---
*Desarrollado con enfoque en la excelencia visual y técnica.*
