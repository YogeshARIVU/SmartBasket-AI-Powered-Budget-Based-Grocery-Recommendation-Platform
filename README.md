# 🛒 SmartBasket
### AI-Powered Budget-Based Grocery Recommendation Platform

<p align="center">
  <b>Shop Smarter. Spend Wisely. Save More.</b>
</p>

SmartBasket is an AI-powered grocery budgeting platform designed to simplify monthly grocery shopping. It aims to help customers discover essential groceries, manage expenses and generate personalized shopping lists based on their available budget.

By combining **QR code-based shop access, real-time product information and AI-powered recommendations**, SmartBasket aims to make grocery planning smarter, simpler and more affordable.

---

## 💡 The Problem

Managing monthly grocery expenses can be challenging.

Customers often need to:
- Check product prices individually.
- Calculate quantities and total expenses manually.
- Compare products and affordable alternatives.
- Adjust their shopping lists to stay within budget.
- Keep track of essential household requirements.

**What if customers could simply enter their monthly budget and let an intelligent system recommend what they should buy?**

That's the idea behind SmartBasket.

## 🚀 Our Solution

SmartBasket aims to transform traditional grocery planning into a budget-aware shopping experience.

### 🧠 How It Works

1. **📱 Scan a QR Code** – Customers scan a participating grocery shop's QR code to access its product catalogue.
2. **💰 Set a Budget** – Enter the available monthly grocery budget.
3. **🤖 AI Recommendations** – Receive personalized grocery recommendations based on budget, preferences and essential needs.
4. **🛍️ Smart Shopping List** – View recommended products, quantities and individual prices.
5. **📊 Budget Tracking** – See estimated total spending and remaining balance.

### 🎯 Example

A customer with a monthly grocery budget of ₹3,000 could receive a recommended shopping list containing:

| Product | Suggested Quantity | Estimated Price |
|---|---:|---:|
| Milk | 1 Litre | ₹60 |
| Bread | 1 Packet | ₹40 |
| Rice | 5 KG | ₹300 |
| Vegetables | Selected items | ₹500 |
| Fruits | Selected items | ₹400 |
| Dal | 2 KG | ₹250 |
| Cooking Oil | 2 Litres | ₹300 |
| Other Essentials | As recommended | ₹900 |
| **Total** | | **₹2,750** |

<sub>Illustrative prices and quantities only; actual recommendations will depend on shop inventory, product prices and customer requirements.</sub>

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| ☕ Java 21 | Backend development |
| 🌱 Spring Boot 4 | Application framework |
| 🔗 Spring Web | REST API development |
| 🗄️ Spring Data JPA | Database operations |
| ⚙️ Hibernate | ORM |
| 🧪 H2 Database | Initial development database |
| 🐘 PostgreSQL | Planned production database |
| ⚛️ React | Planned frontend |
| 🤖 AI / LLM | Planned recommendation engine |

## 🏗️ Architecture

SmartBasket follows a **Feature-Based Modular Monolith Architecture**.

Each business feature is organized into independent packages containing its controllers, services, repositories, entities and DTOs.

```text
smartbasket-backend/
│
├── src/main/java/com/smartbasket/backend/
│   │
│   ├── category/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   │
│   ├── product/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   │
│   └── common/
│       └── exception/
│
└── src/main/resources/
    └── application.yml
```

## ✅ Current Implementation

The backend development is in progress.

**Implemented**
- Category Management – CRUD REST APIs.
- Product Management – CRUD REST APIs.
- Product and Category relationship using JPA.
- Request validation and centralized exception handling.
- H2 database integration.

**Planned**
- 🏪 Shop Management and QR Code Integration.
- 📦 Inventory and Stock Management.
- 👤 Customer and Budget Management.
- 🤖 AI-Powered Grocery Recommendations.
- 🛒 Smart Shopping List Generation.
- 📈 Purchase History and Budget Tracking.

## ⚙️ Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- IntelliJ IDEA or another Java IDE
- Postman for API testing

### 1. Clone the Repository

```bash
git clone https://github.com/YogeshARIVU/SmartBasket-AI-Powered-Budget-Based-Grocery-Recommendation-Platform.git
```

### 2. Navigate to the Project

```bash
cd SmartBasket-AI-Powered-Budget-Based-Grocery-Recommendation-Platform
```

### 3. Run the Application

**Windows:**

```bash
mvnw.cmd spring-boot:run
```

**Alternatively:**

```bash
mvn spring-boot:run
```

### 4. Access the Application

```text
Base URL: http://localhost:8081
```

## 🔌 REST API Endpoints

### 📂 Category Management

| HTTP Method | Endpoint | Description |
|---|---|---|
| POST | `/api/categories` | Create a category |
| GET | `/api/categories` | Retrieve all categories |
| GET | `/api/categories/{id}` | Retrieve category by ID |
| PUT | `/api/categories/{id}` | Update a category |
| DELETE | `/api/categories/{id}` | Delete a category |

### 🛍️ Product Management

| HTTP Method | Endpoint | Description |
|---|---|---|
| POST | `/api/products` | Create a product |
| GET | `/api/products` | Retrieve all products |
| GET | `/api/products/{id}` | Retrieve product by ID |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Delete a product |

## 🗄️ Database Configuration

The project currently uses an H2 in-memory database for initial development.

**H2 Database Console:**

```text
http://localhost:8081/h2-console
```

**Connection Details:**

```text
JDBC URL: jdbc:h2:mem:smartbasket
Username: sa
Password: (leave blank)
```

> ⚠️ The H2 database is in-memory. Data is reset when the application restarts.

## 🗺️ Development Roadmap

- [x] Project initialization and configuration
- [x] Category Management
- [x] Initial Product Management
- [ ] Complete Product Management
- [ ] Shop Management and QR Code Integration
- [ ] Inventory Management
- [ ] Customer and Budget Management
- [ ] AI Recommendation Engine
- [ ] Smart Shopping List
- [ ] Frontend Integration
- [ ] Testing and Deployment

## 🌟 Project Vision

Our vision is to make grocery shopping more intelligent and budget-conscious.

SmartBasket is not just about helping customers purchase products. It is about helping them understand what they need, how much they should buy and how to make the most of their monthly grocery budget.

**Better Planning. Smarter Recommendations. More Savings.**

---

### 👨‍💻 Developer

**Yogesh Arivazhagan**

- GitHub: [@YogeshARIVU](https://github.com/YogeshARIVU)

---

<p align="center">
  ⭐ If you find this project interesting, consider giving it a star!
  <br/>
  <b>SmartBasket – Making Every Grocery Budget Count.</b>
</p>
