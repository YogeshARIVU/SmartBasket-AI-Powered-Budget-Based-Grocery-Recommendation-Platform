# 🛒 SmartBasket
### AI-Powered, QR-Based Budget Grocery Recommendation Platform

SmartBasket is a smart grocery budgeting platform designed to help customers plan essential grocery purchases within a monthly budget.

Customers can scan a shop's QR code, enter their monthly budget, and receive grocery recommendations based on product prices and shop inventory.

---

## 🎯 Project Objectives

- Help customers manage grocery expenses within a fixed budget.
- Recommend essential grocery products.
- Track shop-specific product availability.
- Calculate estimated shopping costs and remaining budgets.
- Simplify grocery planning through a QR-based experience.

## 🛠️ Technology Stack

| Category | Technologies |
|---|---|
| Language | Java 21 |
| Backend | Spring Boot 4 |
| Data Access | Spring Data JPA, Hibernate |
| API | REST APIs |
| Build Tool | Maven |
| Database | H2 |
| Validation | Jakarta Bean Validation |
| Testing | JUnit, Spring Boot Test |
| Version Control | Git, GitHub |

---

## ✅ Implemented Features

### 1. Category Management
- Create, retrieve, update, and delete categories.
- Validate category details.
- Manage product categories.

### 2. Product Management
- Complete Product CRUD operations.
- Associate products with categories.
- Validate product details and SKU uniqueness.
- Product listing with pagination.
- Search products by name.
- Filter and sort products.

### 3. Shop Management
- Manage shop information.
- Maintain individual shop details.
- Associate inventory with specific shops.

### 4. Customer Management
- Create and manage customer details.
- Customer information validation.
- Customer lookup operations.

### 5. Monthly Budget Management
- Create monthly budgets for customers.
- Associate budgets with specific shops.
- Track monthly spending.
- Calculate remaining budget.
- Maintain monthly budget records.

### 6. Inventory Management
- Maintain shop-specific product inventory.
- Track available product quantities.
- Configure minimum stock levels.
- Identify low-stock products.
- Support stock updates using stock movement types.

### 7. Grocery Recommendation System
- Generate recommendations based on customer budgets.
- Consider shop-specific product availability.
- Recommend active products with available stock.
- Calculate estimated grocery costs.
- Calculate remaining budget after recommendations.

**Note:** The current recommendation engine uses basic rule-based logic. AI/ML-based recommendations are planned for future development.

---

## 🏗️ Backend Architecture

```text
src/main/java/com/smartbasket/backend/
│
├── category/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── product/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── shop/
│
├── customer/
│
├── inventory/
│
├── recommendation/
│
└── common/
    └── exception/
```

The project follows a feature-based modular monolith architecture using Spring Boot.

---

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven
- Git

### Clone the Repository

```bash
git clone https://github.com/YogeshARIVU/SmartBasket-AI-Powered-Budget-Based-Grocery-Recommendation-Platform.git
```

### Navigate to the Project

```bash
cd SmartBasket-AI-Powered-Budget-Based-Grocery-Recommendation-Platform
```

### Build and Test

```bash
mvn clean test
```

### Run the Application

```bash
mvn spring-boot:run
```

The application currently runs on:

```text
http://localhost:8081
```

---

## 🔌 Recommendation API

Generate grocery recommendations for a customer and shop.

**Endpoint**

```http
POST /api/recommendations
```

**Request Body**

```json
{
  "customerId": 1,
  "shopId": 1
}
```

The recommendation API requires a monthly budget record for the current month and year, along with available inventory for the selected shop.

---

## 🔮 Future Enhancements

- [ ] Shopping List Management
- [ ] Add, update, and remove shopping list products
- [ ] Track purchased and pending grocery items
- [ ] Improved grocery recommendation algorithms
- [ ] AI/ML-based personalized recommendations
- [ ] QR Code Generation and Scanning
- [ ] Purchase History and Expense Tracking
- [ ] JWT Authentication and Authorization
- [ ] Role-Based Access Control
- [ ] PostgreSQL Database Integration
- [ ] React.js Frontend Application
- [ ] Docker Containerization
- [ ] Cloud Deployment

---

## 📌 Project Status

**Backend Development – In Progress**

Implemented modules: Category, Product, Shop, Customer, Monthly Budget, Inventory, and initial Grocery Recommendations.

---

## 👨‍💻 Developer

**Yogesh Arivazhagan**

- GitHub: https://github.com/YogeshARIVU
- LinkedIn: https://linkedin.com/in/yogesh-arivazhagan

---

⭐ If you find this project interesting, consider giving it a star!

*Built with Java, Spring Boot, and a vision for smarter grocery shopping.*
