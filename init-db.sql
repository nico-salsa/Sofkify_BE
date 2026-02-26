-- Crear las bases de datos para cada microservicio
SELECT 'CREATE DATABASE sofkify_users'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sofkify_users')
\gexec

SELECT 'CREATE DATABASE sofkify_products_bd'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sofkify_products_bd')
\gexec

SELECT 'CREATE DATABASE sofkify_cars_bd'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sofkify_cars_bd')
\gexec

SELECT 'CREATE DATABASE sofkify_orders_bd'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sofkify_orders_bd')
\gexec

-- Dar permisos al usuario postgres
GRANT ALL PRIVILEGES ON DATABASE sofkify_users TO CURRENT_USER;
GRANT ALL PRIVILEGES ON DATABASE sofkify_products_bd TO CURRENT_USER;
GRANT ALL PRIVILEGES ON DATABASE sofkify_cars_bd TO CURRENT_USER;
GRANT ALL PRIVILEGES ON DATABASE sofkify_orders_bd TO CURRENT_USER;
