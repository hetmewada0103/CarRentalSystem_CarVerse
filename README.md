# 🚗 CarVerse - Car Rental System  

CarVerse is a **Java + MySQL-based Car Rental System** that allows customers to book, rent, return, and rate cars, while admins can manage cars, monitor rentals, and generate reports. It’s a **console-based project** built using **Object-Oriented Programming (OOP)** concepts, **JDBC**, and **custom data structures**.  

---

## ✨ Features  

### 👤 Customer Features  
- Register & Login (Email/Phone + Password)  
- Search available cars with filters (brand, type, price, seats, rating)  
- Book cars with trip details & cost calculation  
- View active and returned bookings  
- Cancel a booking (before trip starts)  
- Return cars with **real-time billing & late fee calculation**  
- Rate rented cars  
- Update profile details  
- Receive rental bill as a **file and email** (via Jakarta Mail API)  

### 🛠️ Admin Features  
- Secure Admin Login  
- Add new cars (via stored procedure)  
- View, update, and remove cars  
- View available, rented, and overdue cars  
- Update availability (if not currently booked)  
- View all registered customers (via custom linked list)  
- Generate system reports:  
  - Total cars  
  - Total rentals  
  - Available cars  
  - Currently rented cars  
  - Overdue rentals  
  - Cancelled & returned rentals  
  - Total revenue  

---

## 🏗️ Tech Stack  

- **Language:** Java 17+  
- **Database:** MySQL  
- **Libraries & Tools:**  
  - JDBC (Database Connectivity)  
  - Jakarta Mail API (Email bills)  
  - Stored Procedures & Functions in MySQL  
- **Custom Data Structures:**  
  - Linked List (for Customer records)  
  - Simple Hashing (for password storage)  

---

## 📂 Project Structure  

```text
CarVerse/
├── carverse.admin/       # Admin features
├── carverse.customer/    # Customer features
├── carverse.db/          # Database connection
├── carverse.main/        # Entry point + menus
├── carverse.model/       # Data structures & models
├── carverse.payment/     # Payment handling (Cash, Card, UPI)
├── carverse.rental/      # Rental cost calculation (Stored Procedure)
└── database/             # SQL scripts (tables, procedures, functions)
```
---

## 🗄️ Database Design  

**Main Tables:**  
- `admin` – Admin credentials  
- `customer` – Customer details  
- `car` – Car details (model, brand, type, seats, price, availability)  
- `bookings` – Active bookings (start/end time, cost, status)  
- `rental` – Returned rentals history  
- `ratings` – Customer ratings for cars  
- `payment` – Payment history & bill details  

**Stored Procedure:**  
- `addCar` → Insert new car  
- `calculate_rental_cost` → Compute rental hours, late fees, total cost  

**Function:**  
- `getAverageRating(car_id)` → Returns average rating for a car  

---

## ⚡ Setup Instructions  

### 1. Clone the Repository  
```bash
git clone https://github.com/yourusername/CarVerse.git
cd CarVerse
```

### 2. Setup MySQL Database
- Create a database carrental
- Import the SQL schema (src/carrental.sql)
- Add procedures & functions

### 3. Configure DB Connection
**Update DBConnect.java:**
```bash
static final String URL = "jdbc:mysql://localhost:3306/carrental";
static final String USER = "root";        // your MySQL username
static final String PASSWORD = " ";       // your MySQL password
```

### 4. Run the Project
- Open in IntelliJ / Eclipse
- Run CarVerse.java (main class)
- Use menus to explore customer & admin features

---

## 📸 Sample Outputs

**Customer Login** 
```bash
🔑   CarVerse - Customer Login
1️⃣  Login using Email & Password
2️⃣  Login using Phone & Password
3️⃣  Go Back
```

**Book a Car**
```bash
Enter Car ID: 5
Start Location: Ahmedabad
End Location: Surat
Start Date & Time: 2025-09-05 10:00
End Date & Time: 2025-09-05 15:00
✅ Car booked successfully!
⏱ Duration: 5 hours
💰 Total cost: ₹2500
```

**Billing Example**
```bash
=====================================
           🧾  RENTAL BILL  🧾
=====================================
📄 Booking ID      : 12
🚗 Car ID          : 5
🔤 Model           : Swift
🏷️ Brand           : Maruti
🚘 Type            : Sedan
🪑 Seats           : 4
-------------------------------------
⏱ Total Hours     : 5.0 hrs
💰 Base Price      : ₹2500.00
⏰ Late Hours      : 2.0 hrs
🔻 Late Fee        : ₹500.00
💳 Total Paid      : ₹3000.00
💳 Payment Method  : card
✅ Thank you for choosing CarVerse!
=====================================
```

---

## 🚀 Future Enhancements
- GUI with JavaFX / Swing
- SMS & WhatsApp notifications
- AI-powered dynamic pricing (based on demand & time)
- Online payment gateway integration
- Cloud deployment (AWS / Azure)
  
---

## 👤 Author

- **Prithvi Patel**  
  [@student-prithvipatel](https://github.com/student-prithvipatel)
- **Het Mewada**  
  [@hetmewada0103](https://github.com/hetmewada0103)
- **Vansh Gajjar**  
  [@vanshgajjar](https://github.com/vanshgajjar)
