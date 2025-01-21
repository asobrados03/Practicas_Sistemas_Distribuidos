# Práctica de Hilos en Java

Esta práctica utiliza hilos en Java para implementar un modelo consumidor donde varias instancias de `WordVowelCounter` contarán las vocales en palabras concurrentemente. La `BlockingQueue` permite la sincronización entre el hilo principal (productor) y los hilos consumidores.

## Objetivo

El objetivo es completar las secciones indicadas con `TODO` en el código para que los tests pasen exitosamente. En esta práctica, se aprenderá a:

- Usar `BlockingQueue` para gestionar la comunicación entre el hilo principal y los hilos consumidores.
- Crear hilos que procesen tareas concurrentemente.
- Verificar la implementación mediante tests unitarios.

## Descripción de Clases

### `WordVowelCounter`
Define la lógica para contar vocales en palabras individuales. Cada instancia extrae palabras de una `BlockingQueue` y almacena los resultados en otra `BlockingQueue` de resultados.

### `TextVowelCounter`
Esta clase organiza el procesamiento de palabras y la creación de hilos consumidores. Contiene el método `getVowels`, que:
1. Divide el texto de entrada en palabras.
2. Coloca las palabras en la `BlockingQueue` para que los hilos consumidores las procesen.
3. Controla el número de hilos en función del parámetro `parallelism` y suma el resultado final de conteo de vocales.

## Instrucciones

1. **Completar los `TODO`** en el código fuente, asegurando que la funcionalidad solicitada se implementa correctamente.
2. **Usar `BlockingQueue`**: [API](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html)
3. **Ejecutar los tests** para confirmar el funcionamiento correcto.

## Ejecución de Tests

Para ejecutar los tests:

```bash
mvn test
```

(O las herramientas disponibles en IDE)
