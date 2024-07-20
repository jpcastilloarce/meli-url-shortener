# MELI URL Shortener

Este proyecto es un acortador de URL construido con Spring Boot. Utiliza Redis para el almacenamiento en caché.

## Características

- Acortar URLs largas
- Obtener la URL original de una URL corta
- Obtener estadísticas de uso de las URLs
- Manejo de solicitudes a gran escala
- Uso de Redis para almacenamiento en caché

## Requisitos Previos

- Java 17
- Gradle
- Docker y Docker Compose

## Configuración del Proyecto

### 1. Clonar el Repositorio

```sh
git clone https://github.com/tu-usuario/url-shortener.git
cd url-shortener
```

### 2. Construir el Proyecto
```sh
./gradlew clean build
```

### 3. Ejecutar la Aplicación con Docker Compose
```sh
docker-compose up --build
```

