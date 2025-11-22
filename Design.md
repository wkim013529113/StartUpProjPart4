# DocWeaver – Design Overview

This document describes the design of the DocWeaver prototype for three **Must Do** use cases:

1. Upload Document  
2. Extract Key Fields  
3. Approve Export

Each use case is implemented as a separate module and uses a different design pattern:

- **Module 1 – Upload**: Command pattern  
- **Module 2 – Extraction**: Strategy pattern  
- **Module 3 – Approval/Export**: State pattern  

The code is organized into layers and packages:

- `com.docweaver.domain` – Core business entities
- `com.docweaver.service` – Application services & repositories
- `com.docweaver.command` – Upload commands
- `com.docweaver.strategy` – Extraction strategies
- `com.docweaver.state` – Record lifecycle states
- `com.docweaver.demo` – Demo entry points

---

## 1. Architecture & Layers

### 1.1 Domain Layer

**Package:** `com.docweaver.domain`

Core entities:

- `Workspace` – Logical container for documents.
- `Document`
  - Fields: `id`, `workspaceId`, `filename`, `status`, `documentType`, `recordId`.
  - Status is tracked via `DocumentStatus` enum: `UPLOADED`, `PROCESSING`, `EXTRACTED`, `IN_REVIEW`, `READY_FOR_EXPORT`, `EXPORTED`.
- `ProcessingJob`
  - Represents queued work to classify & extract a document.
  - `JobType` enum includes `CLASSIFY_AND_EXTRACT`.
- `DocumentRecord`
  - Structured data extracted from a `Document`.
  - Contains `List<Field>` and `List<LineItem>`.
  - Also holds `RecordApprovalStatus` and a `RecordState` instance (State pattern).
- `Field` – Key/value pair with confidence and an anchor to the source document.
- `LineItem` – Per-line amounts (description, quantity, unitPrice, amount, currency).
- `DocumentType` – `INVOICE`, `RECEIPT`, `PURCHASE_ORDER`, `CONTRACT`.
- `RecordApprovalStatus` – `DRAFT`, `IN_REVIEW`, `READY_FOR_EXPORT`, `EXPORTED`.

This layer has **no dependency** on patterns or technical infrastructure. Patterns are implemented in separate packages that depend on the domain, not the other way around.

---

## 2. Module 1 – Upload (Command Pattern)

**Goal:** Implement “Upload Document” use case and enqueue processing.

**Key idea:** Encapsulate upload as a **Command** object so it can be queued, executed, and extended without changing UI or controllers.

**Classes (DEV1):**

- `Command`
  - Interface with `void execute()`.
- `UploadDocumentCommand`
  - Fields: `Workspace workspace`, `List<String> filenames`, `DocumentRepository`, `ProcessingJobQueue`.
  - `execute()`:
    - Validates file types.
    - Creates `Document` objects.
    - Marks them `PROCESSING`.
    - Stores them in `DocumentRepository`.
    - Creates corresponding `ProcessingJob` and enqueues into `ProcessingJobQueue`.
- `CommandDispatcher`
  - Maintains a queue of `Command`.
  - `submit()` and `runAll()` methods.

**Supporting Infrastructure:**

- `DocumentRepository`
  - In-memory store for `Document` (for demo and tests).
- `ProcessingJobQueue`
  - In-memory queue for `ProcessingJob`.

**Design benefits:**

- UI / controller never sees raw upload logic.
- Adding audit logging, retries, or asynchronous processing only requires changes around `Command` and `CommandDispatcher`, not the rest of the system.

---

## 3. Module 2 – Extract Key Fields (Strategy Pattern)

**Goal:** Convert documents into structured business data based on document type.

**Key idea:** Extraction rules differ per document type (invoice vs receipt vs PO vs contract). The **Strategy pattern** lets us swap extraction logic based on `DocumentType` without `if/else` spaghetti.

**Classes (DEV2):**

- `ExtractionStrategy`
  - Interface with `DocumentRecord extract(Document document)`.
- Concrete strategies:
  - `InvoiceExtractionStrategy`
  - `ReceiptExtractionStrategy`
  - `PurchaseOrderExtractionStrategy`
  - `ContractExtractionStrategy`
  - Each populates a `DocumentRecord` with fields and line items for that specific type.
- `ExtractionEngine`
  - Holds `Map<DocumentType, ExtractionStrategy>`.
  - `extract(Document doc)`:
    - Reads `doc.getDocumentType()`.
    - Looks up corresponding `ExtractionStrategy`.
    - Returns `DocumentRecord`.

**Integration Service:**

- `ExtractionJobProcessor`
  - Input: `ProcessingJob` from the upload module.
  - Steps:
    - Loads `Document` from `DocumentRepository`.
    - Calls `ExtractionEngine.extract(document)` to produce a `DocumentRecord`.
    - Saves `DocumentRecord` into `DocumentRecordRepository`.
    - Sets `document.recordId` and marks document `EXTRACTED`.

**Support:**

- `DocumentRecordRepository`
  - In-memory store for `DocumentRecord`.

**Design benefits:**

- New document types only require new strategy classes; existing code remains unchanged.
- The same `ExtractionEngine` works with different models (OCR, heuristic, ML) behind a single interface.

---

## 4. Module 3 – Approve & Export (State Pattern)

**Goal:** Allow reviewers to validate extracted fields, approve the record, and then mark it as exported, with clear rules per lifecycle state.

**Key idea:** Valid operations differ between DRAFT, IN_REVIEW, READY_FOR_EXPORT, and EXPORTED. The **State pattern** encodes those rules in separate classes instead of scattered conditionals.

**Classes (DEV3):**

- `RecordState` (interface)
  - `startReview()`
  - `approve()`
  - `markExported()`
  - `editField(String key, String newValue)`
- Concrete states:
  - `DraftRecordState`
  - `InReviewRecordState`
  - `ReadyForExportRecordState`
  - `ExportedRecordState`
- `DocumentRecord`
  - Holds `RecordApprovalStatus` and a `RecordState` reference.
  - Delegates behavior (`startReview`, `approve`, etc.) to the current `RecordState`.

**Application Service:**

- `RecordApprovalService`
  - APIs:
    - `startReview(recordId)`
    - `approveRecord(recordId)`
    - `markExported(recordId)`
    - `editField(recordId, key, newValue)`
  - Coordinates:
    - The `DocumentRecord` state machine.
    - Synchronization of `Document.status` with `RecordApprovalStatus`:
      - IN_REVIEW → `DocumentStatus.IN_REVIEW`
      - READY_FOR_EXPORT → `DocumentStatus.READY_FOR_EXPORT`
      - EXPORTED → `DocumentStatus.EXPORTED`.

**Design benefits:**

- Approval rules are centralized in State classes.
- Illegal actions (e.g., editing after export) are prevented by design, not by scattered checks.

---

## 5. Developer Responsibilities

- **DEV1 (25%) – Upload / Command**
  - `Command`, `UploadDocumentCommand`, `CommandDispatcher`, `DocumentRepository`, `ProcessingJobQueue`, `Document`, `DocumentStatus`, `Workspace`, `ProcessingJob`, `UploadDemoMain`, `UploadDocumentCommandTest`.

- **DEV2 (25%) – Extraction / Strategy**
  - `DocumentType`, `DocumentRecord`, `Field`, `LineItem`, `DocumentRecordRepository`, `ExtractionStrategy`, `ExtractionEngine`, `InvoiceExtractionStrategy`, `ReceiptExtractionStrategy`, `PurchaseOrderExtractionStrategy`, `ContractExtractionStrategy`, `ExtractionJobProcessor`, `ExtractionDemoMain`, strategy tests.

- **DEV3 (50%) – Approval / State + Integration**
  - `RecordApprovalStatus`, `RecordState`, all concrete RecordState classes, `RecordApprovalService`, `ApprovalDemoMain`, `RecordApprovalServiceTest`, plus coordination with Modules 1 and 2 in `FullFlowDemoMain`.

---

## 6. Integration & End-to-End Flow

The full flow is demonstrated in `FullFlowDemoMain`:

1. **Upload** (Command):
   - `UploadDocumentCommand` → `Document` in `PROCESSING` + `ProcessingJob` in queue.
2. **Extract** (Strategy):
   - `ExtractionJobProcessor` consumes `ProcessingJob`.
   - Uses `ExtractionEngine` to create `DocumentRecord`.
   - Marks `Document` as `EXTRACTED`.
3. **Approve/Export** (State):
   - `RecordApprovalService` transitions `DocumentRecord`:
     - `DRAFT` → `IN_REVIEW` → `READY_FOR_EXPORT` → `EXPORTED`.
   - `Document.status` is kept in sync for UI/workflow.

This design cleanly separates responsibilities, makes each module independently testable, and matches the original OOA/OOD use cases.
