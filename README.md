# 🛒 SmartBasket
### Smart Grocery Budgeting & Recommendation Platform

SmartBasket is a QR-based grocery budgeting platform built with Java and Spring Boot. It helps customers plan grocery purchases within a monthly budget by generating product recommendations based on shop inventory, product prices, and previous purchase history.

Customers can select a shop, set a monthly budget, receive grocery recommendations, manage shopping lists, track purchases, and monitor their spending through analytics and reports.

---

## 🎯 Project Objectives

- Help customers manage grocery expenses within a fixed monthly budget.
- Generate budget-aware grocery recommendations.
- Track shop-specific product availability and stock levels.
- Simplify grocery planning through shopping lists.
- Maintain purchase history and track completed purchases.
- Monitor monthly spending and category-wise expenses.
- Provide spending reports and monthly comparisons.

---

## 🛠️ Technology Stack

| Category | Technologies |
|---|---|
| Language | Java 21 |
| Backend Framework | Spring Boot 4 |
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
- Create, retrieve, update, and delete product categories.
- Validate category details.
- Organize products by category.

### 2. Product Management
- Complete Product CRUD operations.
- Associate products with categories.
- Validate product details and SKU uniqueness.
- Paginated product listing.
- Search products by name.
- Filter and sort products.

### 3. Shop Management
- Create and manage shop information.
- Maintain individual shop details.
- Associate inventory with specific shops.

### 4. Customer Management
- Create and manage customer details.
- Validate customer information.
- Retrieve customer records.

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
- Record stock movements.
- Support inventory updates through stock movement types.

### 7. Grocery Recommendation System
- Generate recommendations based on customer budgets.
- Consider shop-specific product availability.
- Recommend active products with available stock.
- Use previous purchase history to prioritize familiar products.
- Apply balanced product allocation within the available budget.
- Respect inventory stock limits.
- Calculate estimated grocery costs and remaining budget.

**Note:** The current recommendation engine uses rule-based logic. AI/ML-based personalization is a future enhancement.

### 8. Shopping List Management
- Create shopping lists for customers and shops.
- Add grocery products to shopping lists.
- Update shopping list items and quantities.
- Remove products from shopping lists.
- Track pending and purchased items.
- Manage purchase status.

### 9. Purchase History Management
- Record completed grocery purchases.
- Retrieve customer purchase history.
- Track purchased products and quantities.
- Maintain purchase timestamps.
- Integrate completed purchases with inventory updates.

### 10. Spending Analytics
- Generate monthly spending summaries.
- Calculate total spending.
- Analyze category-wise spending.
- Track grocery expenses across categories.
- Retrieve spending information for customers.

### 11. Spending Reports
- Generate monthly spending reports.
- Compare spending across months.
- Retrieve spending trends.
- Analyze monthly expense patterns.

---

## 🏗️ Backend Architecture

The application follows a feature-based modular monolith architecture.

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
├── shop/
├── customer/
├── inventory/
├── recommendation/
├── shoppinglist/
├── categoryanalytics/
├── monthlyspending/
├── spendingreports/
│
└── common/
    └── exception/
```

Each feature is organized into layers:

- **Controller:** Handles HTTP requests and responses.
- **Service:** Contains business logic.
- **Repository:** Handles database operations.
- **Entity:** Represents database tables.
- **DTO:** Transfers data between API layers.

---

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/YogeshARIVU/SmartBasket-AI-Powered-Budget-Based-Grocery-Recommendation-Platform.git
```

### 2. Navigate to the Project

```bash
cd SmartBasket-AI-Powered-Budget-Based-Grocery-Recommendation-Platform
```

### 3. Build and Test

```bash
mvn clean test
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

### 5. Access the Application

```text
Base URL: http://localhost:8081
H2 Console: http://localhost:8081/h2-console
```

The H2 database is currently configured for development and testing.

---

## 🔌 Sample API – Grocery Recommendations

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

The recommendation API:

1. Retrieves the customer's current monthly budget.
2. Calculates the available budget.
3. Retrieves the selected shop's inventory.
4. Considers previous purchase history.
5. Allocates product quantities within budget and stock limits.
6. Returns estimated costs and remaining budget.

A valid monthly budget and available shop inventory must exist before generating recommendations.

---

## 📊 Project Modules

| Module | Status |
|---|---|
| Category Management | Implemented |
| Product Management | Implemented |
| Shop Management | Implemented |
| Customer Management | Implemented |
| Monthly Budget Management | Implemented |
| Inventory Management | Implemented |
| Grocery Recommendations | Implemented |
| Shopping List Management | Implemented |
| Purchase History | Implemented |
| Monthly Spending Analytics | Implemented |
| Category-wise Spending Analytics | Implemented |
| Spending Reports | Implemented |

---

## 🔮 Future Enhancements

- [ ] AI/ML-based personalized grocery recommendations.
- [ ] Nutrition-aware grocery recommendations.
- [ ] QR Code Generation and Scanning.
- [ ] JWT Authentication and Authorization.
- [ ] Role-Based Access Control.
- [ ] PostgreSQL Database Integration.
- [ ] React.js Frontend Application.
- [ ] Docker Containerization.
- [ ] Cloud Deployment.

---

## 📌 Project Status

**Backend Development – In Progress**

Core backend modules for product management, inventory, budgeting, recommendations, shopping lists, purchase history, and spending analytics have been implemented.

Further development will focus on realistic test data, end-to-end integration testing, frontend development, and future enhancements.

---

## 👨‍💻 Developer

**Yogesh Arivazhagan**

- GitHub: https://github.com/YogeshARIVU
- LinkedIn: https://linkedin.com/in/yogesh-arivazhagan

---

⭐ If you find this project interesting, consider giving it a star!

*Built with Java 21, Spring Boot, and a vision for smarter grocery shopping.*
