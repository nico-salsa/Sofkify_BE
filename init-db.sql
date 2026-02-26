-- Script de inicialización para PostgreSQL
-- Este script se ejecuta automáticamente cuando se crea el contenedor por primera vez
-- La base de datos principal ya está creada por POSTGRES_DB

-- Verificar que estamos conectados a la base de datos correcta
SELECT current_database();

-- Crear extensiones útiles si es necesario
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Log de inicialización exitosa
DO $$
BEGIN
    RAISE NOTICE 'Base de datos % inicializada correctamente', current_database();
END $$;
