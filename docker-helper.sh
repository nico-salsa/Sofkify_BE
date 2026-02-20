#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Helper functions
print_header() {
    echo -e "\n${BLUE}=== $1 ===${NC}\n"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

# Command: help
cmd_help() {
    print_header "Sofkify Docker Helper - Comandos Disponibles"
    
    cat << EOF
${GREEN}BÁSICOS${NC}
  help                    Muestra este mensaje de ayuda
  up                      Inicia todos los contenedores (docker-compose up -d --build)
  down                    Detiene todos los contenedores (docker-compose down)
  ps                      Muestra estado de todos los contenedores

${GREEN}SERVICIOS${NC}
  logs SERVICE            Ver logs de un servicio (ej: logs user-service)
  restart SERVICE         Reinicia un servicio (ej: restart product-service)
  exec SERVICE CMD        Ejecuta comando en un servicio (ej: exec user-service sh)

${GREEN}MANTENIMIENTO${NC}
  clean                   Limpia todo (docker-compose down -v, elimina volúmenes)
  rebuild                 Reconstruye todas las imágenes sin cache
  prune                   Limpia imágenes y contenedores no utilizados

${GREEN}CONECTIVIDAD${NC}
  test-connectivity       Verifica conectividad entre servicios
  test-db                 Verifica conectividad a todas las BDs
  test-rabbitmq           Verifica conectividad a RabbitMQ

${GREEN}BASE DE DATOS${NC}
  db-backup               Realiza backup de todas las BDs

${GREEN}INFORMACIÓN${NC}
  info                    Muestra información general del stack
  logs-all                Ver logs de todos los servicios

${GREEN}SERVICIOS ESPECÍFICOS${NC}
  Servicios disponibles: user-service, product-service, order-service, cart-service
  Bases de datos: postgres-users, postgres-products, postgres-orders, postgres-carts
  Otros: rabbitmq

${BLUE}EJEMPLOS:${NC}
  $0 up
  $0 logs user-service
  $0 restart product-service
  $0 test-connectivity
  $0 clean
EOF
}

# Command: up
cmd_up() {
    print_header "Iniciando servicios Sofkify..."
    docker-compose up -d --build
    
    if [ $? -eq 0 ]; then
        print_success "Servicios iniciados correctamente"
        print_info "Esperando que los servicios se estabilicen..."
        sleep 5
        cmd_ps
    else
        print_error "Error al iniciar los servicios"
        exit 1
    fi
}

# Command: down
cmd_down() {
    print_header "Deteniendo servicios Sofkify..."
    docker-compose down
    
    if [ $? -eq 0 ]; then
        print_success "Servicios detenidos correctamente"
    else
        print_error "Error al detener los servicios"
        exit 1
    fi
}

# Command: ps
cmd_ps() {
    print_header "Estado de contenedores"
    docker-compose ps
}

# Command: logs
cmd_logs() {
    if [ -z "$1" ]; then
        print_error "Uso: $0 logs SERVICE_NAME"
        print_info "Servicios disponibles: user-service, product-service, order-service, cart-service, rabbitmq"
        exit 1
    fi
    docker-compose logs -f "$1"
}

# Command: logs-all
cmd_logs_all() {
    print_header "Logs de todos los servicios (Ctrl+C para salir)"
    docker-compose logs -f
}

# Command: restart
cmd_restart() {
    if [ -z "$1" ]; then
        print_error "Uso: $0 restart SERVICE_NAME"
        print_info "Servicios disponibles: user-service, product-service, order-service, cart-service, postgres-users, postgres-products, postgres-orders, postgres-carts, rabbitmq"
        exit 1
    fi
    
    print_header "Reiniciando $1..."
    docker-compose restart "$1"
    
    if [ $? -eq 0 ]; then
        print_success "$1 reiniciado correctamente"
    else
        print_error "Error al reiniciar $1"
        exit 1
    fi
}

# Command: clean
cmd_clean() {
    print_warning "Esto eliminará TODOS los contenedores, volúmenes y datos asociados"
    read -p "¿Estás seguro? (yes/no): " confirm
    
    if [ "$confirm" = "yes" ]; then
        print_header "Limpiando todo..."
        docker-compose down -v
        
        if [ $? -eq 0 ]; then
            print_success "Sistema limpiado completamente"
        else
            print_error "Error durante la limpieza"
            exit 1
        fi
    else
        print_info "Operación cancelada"
    fi
}

# Command: rebuild
cmd_rebuild() {
    print_warning "Esto reconstruirá todas las imágenes sin usar cache"
    read -p "¿Estás seguro? (yes/no): " confirm
    
    if [ "$confirm" = "yes" ]; then
        print_header "Reconstruyendo imágenes..."
        docker-compose build --no-cache
        
        if [ $? -eq 0 ]; then
            print_success "Imágenes reconstruidas correctamente"
        else
            print_error "Error durante la reconstrucción"
            exit 1
        fi
    else
        print_info "Operación cancelada"
    fi
}

# Command: prune
cmd_prune() {
    print_warning "Esto eliminará imágenes y contenedores no utilizados"
    read -p "¿Estás seguro? (yes/no): " confirm
    
    if [ "$confirm" = "yes" ]; then
        print_header "Limpiando imágenes y contenedores no utilizados..."
        docker system prune -f
        
        if [ $? -eq 0 ]; then
            print_success "Sistema purgado"
        else
            print_error "Error durante la purga"
            exit 1
        fi
    else
        print_info "Operación cancelada"
    fi
}

# Command: exec
cmd_exec() {
    if [ -z "$1" ] || [ -z "$2" ]; then
        print_error "Uso: $0 exec SERVICE_NAME COMMAND"
        print_info "Ejemplo: $0 exec user-service sh"
        exit 1
    fi
    docker-compose exec "$1" $2
}

# Command: test-connectivity
cmd_test_connectivity() {
    print_header "Verificando conectividad entre servicios"
    
    # Check if services are running
    local services=("user-service" "product-service" "order-service" "cart-service" "rabbitmq" "postgres-users" "postgres-products" "postgres-orders" "postgres-carts")
    
    for service in "${services[@]}"; do
        if docker-compose ps "$service" | grep -q "Up"; then
            print_success "$service está ejecutándose"
        else
            print_error "$service NO está ejecutándose"
        fi
    done
    
    echo ""
    print_info "Probando conectividad de red..."
    
    # Test inter-service connectivity
    docker-compose exec -T user-service ping -c 2 postgres-users > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        print_success "user-service → postgres-users: OK"
    else
        print_error "user-service → postgres-users: FALLO"
    fi
    
    docker-compose exec -T product-service ping -c 2 postgres-products > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        print_success "product-service → postgres-products: OK"
    else
        print_error "product-service → postgres-products: FALLO"
    fi
    
    docker-compose exec -T order-service ping -c 2 postgres-orders > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        print_success "order-service → postgres-orders: OK"
    else
        print_error "order-service → postgres-orders: FALLO"
    fi
    
    docker-compose exec -T cart-service ping -c 2 postgres-carts > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        print_success "cart-service → postgres-carts: OK"
    else
        print_error "cart-service → postgres-carts: FALLO"
    fi
    
    docker-compose exec -T user-service ping -c 2 rabbitmq > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        print_success "user-service → rabbitmq: OK"
    else
        print_error "user-service → rabbitmq: FALLO"
    fi
}

# Command: test-db
cmd_test_db() {
    print_header "Verificando conectividad a bases de datos"
    
    local dbs=("postgres-users" "postgres-products" "postgres-orders" "postgres-carts")
    
    for db in "${dbs[@]}"; do
        if docker-compose exec -T "$db" pg_isready -U sofkify > /dev/null 2>&1; then
            print_success "$db está disponible"
        else
            print_error "$db NO está disponible"
        fi
    done
}

# Command: test-rabbitmq
cmd_test_rabbitmq() {
    print_header "Verificando conectividad a RabbitMQ"
    
    if docker-compose exec -T rabbitmq rabbitmq-diagnostics -q ping > /dev/null 2>&1; then
        print_success "RabbitMQ está ejecutándose"
    else
        print_error "RabbitMQ NO está disponible"
    fi
    
    print_info "Accede al Management UI en: http://localhost:15672"
    print_info "Usuario: guest | Contraseña: guest"
}

# Command: info
cmd_info() {
    print_header "Información del stack Sofkify"
    
    cat << EOF
${GREEN}SERVICIOS${NC}
  • User Service:     http://localhost:8080
  • Product Service:  http://localhost:8081
  • Order Service:    http://localhost:8082
  • Cart Service:     http://localhost:8083

${GREEN}BASES DE DATOS${NC}
  • postgres-users:    localhost:5432 (sofkify_users)
  • postgres-products: localhost:5433 (sofkify_products_bd)
  • postgres-orders:   localhost:5435 (sofkify_orders_bd)
  • postgres-carts:    localhost:5434 (sofkify_cars_bd)
  
  Usuario: sofkify
  Contraseña: sofkify_secure_pass

${GREEN}RABBITMQ${NC}
  • AMQP:         localhost:5672
  • Management:   http://localhost:15672
  
  Usuario: guest
  Contraseña: guest

${GREEN}RED DOCKER${NC}
  • Nombre: sofkify-network
  • Driver: bridge
EOF
}

# Command: db-backup
cmd_db_backup() {
    print_header "Realizando backup de todas las bases de datos"
    
    local backup_dir="backups/$(date +%Y%m%d_%H%M%S)"
    mkdir -p "$backup_dir"
    
    local dbs=("postgres-users:sofkify_users" "postgres-products:sofkify_products_bd" "postgres-orders:sofkify_orders_bd" "postgres-carts:sofkify_cars_bd")
    
    for db_info in "${dbs[@]}"; do
        IFS=':' read -r container db_name <<< "$db_info"
        print_info "Haciendo backup de $db_name desde $container..."
        
        docker-compose exec -T "$container" pg_dump -U sofkify "$db_name" > "$backup_dir/${db_name}.sql"
        
        if [ $? -eq 0 ]; then
            print_success "Backup de $db_name completado"
        else
            print_error "Error en backup de $db_name"
        fi
    done
    
    print_success "Todos los backups completados en: $backup_dir"
}

# Main script logic
main() {
    if [ $# -eq 0 ]; then
        cmd_help
        exit 0
    fi
    
    cmd="$1"
    shift
    
    case "$cmd" in
        help)               cmd_help ;;
        up)                 cmd_up ;;
        down)               cmd_down ;;
        ps)                 cmd_ps ;;
        logs)               cmd_logs "$@" ;;
        logs-all)           cmd_logs_all ;;
        restart)            cmd_restart "$@" ;;
        clean)              cmd_clean ;;
        rebuild)            cmd_rebuild ;;
        prune)              cmd_prune ;;
        exec)               cmd_exec "$@" ;;
        test-connectivity)  cmd_test_connectivity ;;
        test-db)            cmd_test_db ;;
        test-rabbitmq)      cmd_test_rabbitmq ;;
        info)               cmd_info ;;
        db-backup)          cmd_db_backup ;;
        *)
            print_error "Comando no reconocido: $cmd"
            echo ""
            cmd_help
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"
