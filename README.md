# Session 15 - Microservices Order Tracking System

Hệ thống microservices theo dõi đơn hàng với luồng: **Order → Inventory → Delivery → Payment → Notification**.

## Architecture

| Service | Port | Database | Responsibility |
|---------|------|----------|----------------|
| order-service | 8081 | order_db | Tạo đơn hàng, cập nhật trạng thái |
| inventory-service | 8082 | inventory_db | Quản lý kho, trừ/hoàn tồn kho |
| notification-service | 8083 | - | Gửi thông báo cho người dùng |
| delivery-service | 8084 | delivery_db | Theo dõi vị trí đơn hàng |
| payment-service | 8085 | payment_db | Xử lý thanh toán |

## Prerequisites

- Java 17
- MySQL 8.0+
- Kafka 3.x
- Gradle (gradlew included)

## Quick Start

### 1. Start Infrastructure

```bash
docker-compose up -d
```

This starts:
- MySQL on `localhost:3306`
- Kafka on `localhost:9092`
- Kafka UI on `http://localhost:8080`

### 2. Run Services

Mở 5 terminal riêng, mỗi terminal chạy 1 service:

```bash
# Terminal 1 - Order Service
cd order_service
../gradlew bootRun

# Terminal 2 - Inventory Service
cd inventory-service
../gradlew bootRun

# Terminal 3 - Notification Service
cd notification-service
../gradlew bootRun

# Terminal 4 - Delivery Service
cd delivery-service
../gradlew bootRun

# Terminal 5 - Payment Service
cd payment-service
../gradlew bootRun
```

Hoặc chạy từ root project:
```bash
./gradlew :order_service:bootRun
./gradlew :inventory-service:bootRun
./gradlew :notification-service:bootRun
./gradlew :delivery-service:bootRun
./gradlew :payment-service:bootRun
```

## API Testing

### 1. Tạo đơn hàng

```bash
curl -X POST "http://localhost:8081/orders?userId=user-001" \
  -H "Content-Type: application/json" \
  -d '[
    {"productId": 1, "quantity": 2, "price": 15000000},
    {"productId": 2, "quantity": 1, "price": 25000000}
  ]'
```

Response: Order với status `PENDING`

### 2. Thanh toán thành công

```bash
curl -X POST "http://localhost:8085/payments?orderId=1&amount=55000000&success=true" \
  -H "Content-Type: application/json" \
  -d '[
    {"productId": 1, "quantity": 2, "price": 15000000},
    {"productId": 2, "quantity": 1, "price": 25000000}
  ]'
```

### 3. Thanh toán thất bại

```bash
curl -X POST "http://localhost:8085/payments?orderId=2&amount=10000000&success=false" \
  -H "Content-Type: application/json" \
  -d '[
    {"productId": 1, "quantity": 2, "price": 15000000}
  ]'
```

### 4. Cập nhật vị trí giao hàng

```bash
curl -X PUT "http://localhost:8084/deliveries/1/location?location=IN_TRANSIT"
```

## Luồng sự kiện Kafka

```
order-created → inventory-service (trừ kho)
             → notification-service (thông báo PENDING)

inventory-updated → order-service (cập nhật PREPARING/FAILED)
                  → notification-service (thông báo PREPARING)
                  → delivery-service (tạo delivery nếu thành công)

delivery-location-updated → notification-service (thông báo vị trí)

payment-result → order-service (cập nhật SUCCESS/CANCELED)
              → notification-service (thông báo kết quả)
              → inventory-service (hoàn kho nếu thất bại)
```

## Order Status Flow

```
PENDING → PREPARING → SUCCESS
   ↓         ↓
  FAILED   CANCELED (payment failed)
```

## Databases

5 databases riêng biệt:
- `order_db` - bảng `orders`, `order_detail`
- `inventory_db` - bảng `stock`
- `delivery_db` - bảng `deliveries`
- `payment_db` - bảng `payments`

JPA `ddl-auto: update` sẽ tự động tạo schema khi khởi động.

## Kafka Topics

| Topic | Producer | Consumer(s) |
|-------|----------|-------------|
| order-created | order-service | inventory-service, notification-service |
| inventory-updated | inventory-service | order-service, notification-service, delivery-service |
| delivery-location-updated | delivery-service | notification-service |
| payment-result | payment-service | order-service, notification-service, inventory-service |
