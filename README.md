 Project Overview & Core Description

The Grand Vista Resort & Suite Management System is a production-grade, desktop-based Enterprise Resource Planning (ERP) application designed to automate complex, multi-tier hospitality operations. Built using **JavaFX** for its presentation layer and backed by a relational **MySQL database server**, the platform provides a robust ecosystem for managing client life cycles, room inventories, staff allocations, and real-time financial auditing.

To ensure long-term maintainability and prevent structural architectural decay, the application is strictly engineered around the **Model-View-Controller (MVC)** design pattern, achieving a complete **Separation of Concerns (SoC)** by isolating database transaction queries from GUI rendering logic.

 Key Architectural Engineering Features

* **Advanced Relational Integrity:** Built upon a highly normalized 6-table relational schema using strict indexing constraints (`PRIMARY KEY`, `FOREIGN KEY`, `NOT NULL`, `UNIQUE`) to prevent data redundancy and anomalies.
* **Transactional Reliability (Cascading & Restrictions):** Armed with `ON DELETE CASCADE` actions on the financial ledgers to automatically wipe out downstream receipts when a reservation parent row is removed, eliminating orphaned data clutter. Conversely, `FK RESTRICT` rules protect client and suite data from accidental deletion if they are tied to live, active bookings.
* **Automated Database Triggers:** Features an optimized database event-driven `TRIGGER` that dynamically intercepts new entries to mathematically compute stay durations and auto-inject room-tier cost multipliers into the `grand_total` column prior to insertion.
* **Streamlined Administrative Views:** Implements specialized global SQL `VIEWS` (such as `vw_Financial_Transaction_Summary`) to encapsulate heavy, multi-table structural `JOIN` operations. This abstracts backend query complexity, drastically simplifying the Java controller layer and allowing rapid administrative dashboard data retrieval.
* **Robust JavaFX Client Interface:** Designed with responsive UI components, fully styled dark-mode layouts, custom data entry validation scripts to handle edge-case runtime failures gracefully, and asynchronous TableView bindings for seamless data manipulation.
