# HomeScout: Real Estate Listing and Filtering Website

A Spring Boot website written in **Java 17** that manages property listings and lets visitors
search and filter them using several criteria at once. Built to demonstrate object-oriented
programming concepts.

Student: *your name*   Roll number: *your roll number*   College: *your college*

## Features
- Browse listings with pagination (6 per page) and sorting
- Filter by city, type, price range, bedrooms, furnishing, amenities and keyword, combined with ALL (AND) or ANY (OR)
- Property detail page with estimated tax and a loan (EMI) calculator
- Login with three roles: Buyer, Agent, Admin
- Buyers save favourites and send enquiries
- Agents add, edit, delete and change the status of their own listings, with validation
- Admin manages every listing, sees statistics and downloads a CSV report
- Data is saved to the `data` folder and survives a restart
- A live "OOP concepts" page that shows each concept running on the real data

## Tech stack
Java 17, Spring Boot, Thymeleaf, Bootstrap 5, Maven, JUnit 5, IntelliJ IDEA.

## How to run
1. Open the project in IntelliJ IDEA and wait for Maven to finish.
2. Run `HomescoutWebApplication`.
3. Open http://localhost:8080
4. Run the tests: right-click `src/test/java` and choose Run 'All Tests'.

## Demo accounts (password for all: pass123)
| Email | Role |
|---|---|
| riya@mail.com | Buyer |
| arjun@agency.com | Agent |
| neha@agency.com | Agent |
| admin@estate.com | Admin |

## Project structure (package `com.homescout`)
| Package | Purpose |
|---|---|
| `model` | Property hierarchy, User hierarchy, Address, Enquiry, enums, interfaces |
| `filter` | `PropertyFilter` interface, concrete filters, `CompositeFilter`, `FilterCriteria` builder |
| `sort` | `SortStrategy` and four sorting strategies |
| `service` | `Catalog<T>`, singleton `PropertyCatalog`, `SearchService`, `UserService`, `EnquiryService`, `AuthService`, `DataStore`, `PersistenceService` |
| `util` | `Money`, `Validator`, `EmiCalculator`, `PropertyFactory`, `FileHandler`, `SampleData` |
| `exception` | Custom checked and unchecked exceptions |
| `controller` | Web controllers (they receive requests and choose the page) |
| `config` | Application setup and the access guard |
| `web` | Helper classes that carry data to the pages |

## Where each OOP concept lives
| Concept | Where |
|---|---|
| Encapsulation | `Property` (private fields, validated final setters), immutable `Address` |
| Inheritance: single, multilevel, hierarchical | `Land`; `Villa -> Residential -> Property`; `Residential`, `Commercial`, `Land` |
| Compile-time polymorphism | `SearchService.search(...)` overloads, `EmiCalculator.emi(...)`, `Address` and `Apartment` constructors, varargs in `addAmenities` |
| Runtime polymorphism | `calculateTax()`, `getDetails()`, `User.canManage()`, `User.getRole()` |
| Abstract classes | `Property`, `Residential`, `Commercial`, `User` |
| Interfaces, default and static methods | `PropertyFilter`, `Searchable`, `Bookmarkable`, `SortStrategy` |
| Composition, aggregation, association | `Property`-`Address`; `Agent` and `Buyer` lists; `Enquiry` |
| Generics | `Catalog<T extends Property>`, `UserService.getAllOfType` |
| Nested and anonymous classes | `FilterCriteria.Builder`, `StatsView.Bar`, anonymous `Comparator` |
| Exceptions | `exception` package, used in services and controllers |
| Serialization and file I/O | `FileHandler`, `DataStore` |
| Design patterns | Singleton, Factory, Strategy, Composite, Builder |

See them running live at http://localhost:8080/concepts

## Tests
42 unit tests cover validation, polymorphism, every filter, composite logic, the catalog, search, sorting,
pagination, login, the listing form, statistics and saving and loading data.

## Possible future work
Registration page, a real database (MySQL), password hashing with BCrypt, property photos, email notifications.