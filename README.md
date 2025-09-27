# IoTProject2024 – Smart Wellness Village

This project was developed as the **final assignment** for the *Internet of Things (2023–2024)* course.  
It simulates a **smart wellness village**, using the **CoAP protocol** via the **Californium framework** in Java.

---

## Swimming Pools

There are **three smart swimming pools** in the village:

- **P1** – Olympic Pool  
- **P2** – Relaxing Lagoon  
- **P3** – Kids’ Pool

Each pool is equipped with:

-  **Temperature sensor** 
-  **Chlorine concentration sensor** 
-  **Heating pump**  
-  **Chlorine mixer** 

---

##  Turnstiles

- **Ein** (Entrance) and **Eout** (Exit) are modeled as **CoAP Servers**
- Support:
  - `POST` with visitor ID to simulate entry/exit
  - `GET` and `OBSERVE` to monitor the number of people

---

##  Visitors

- Modeled as **CoAP Clients**
- Interact with the turnstiles to enter and leave the wellness village

---

##  Manager (B)

- A **CoAP Client** that supervises pools and turnstiles
- Can intervene in case of:
  - Malfunctioning turnstiles (force opening)
  - Failing actuators (manually adjusting temperature/chlorine values)

---

##  Technologies Used

-  **Java**
-  **Californium (Eclipse IoT CoAP Framework)**
-  **JSON** for message payloads
