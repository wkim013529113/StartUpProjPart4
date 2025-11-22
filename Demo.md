# DocWeaver Demo Script

This demo shows the 3 Must Do use cases for DocWeaver:

1. Upload Document  
2. Extract Key Fields  
3. Approve Export  

The demo is driven by the `FullFlowDemoMain` class.

---

## 1. How to Run

### Prerequisites

- JDK 17+ (project uses Java where `java.lang.Record` exists)
- Maven or IntelliJ IDEA

### Command Line

```bash
mvn -q exec:java -Dexec.mainClass="com.docweaver.demo.FullFlowDemoMain"
