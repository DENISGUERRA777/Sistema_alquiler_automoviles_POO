# 🚗 Sistema de Alquiler de Automóviles (POO)

Este proyecto es una aplicación de escritorio para la gestión y alquiler de vehículos, diseñada bajo el paradigma de **Programación Orientada a Objetos (POO)**. Permite administrar una flota de automóviles, gestionar clientes y procesar rentas de manera eficiente.

## 📋 Descripción

El sistema simula la lógica de negocio de una agencia de alquiler de autos. Se enfoca en el uso de clases, objetos, herencia y polimorfismo para modelar entidades del mundo real como vehículos, clientes y contratos de alquiler.

El objetivo principal es demostrar la aplicación de principios sólidos de POO para crear un software modular, escalable y fácil de mantener.

## 🚀 Características Principales

* **Gestión de Flota:** Alta, baja y modificación de vehículos (Autos, Camionetas, Motos, etc.).
* **Gestión de Clientes:** Registro y consulta de información de clientes.
* **Sistema de Rentas:**
    * Cálculo automático de costos por día/kilometraje.
    * Validación de disponibilidad de vehículos.
    * Generación de tickets o contratos básicos.
* **POO en Acción:** Uso de clases abstractas, interfaces y encapsulamiento.

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** [Java / JDK / Maven / Swing] 
* **IDE:** [NetBeans]
* **Persistencia:** [Base de Datos NoSQL (MongoDB)]

## 🧠 Patrones de Diseño
Este proyecto destaca por el uso del patrón DAO:

Interfaces DAO: Definen las operaciones estándar (Create, Read, Update, Delete) sin exponer detalles de la base de datos.

Implementación DAO: Contiene el código específico del driver de MongoDB, permitiendo que si en el futuro se cambia a SQL, solo se toque esta capa sin afectar la interfaz gráfica.

✒️ Autor
Denis Guerra - Desarrollador Principal -
