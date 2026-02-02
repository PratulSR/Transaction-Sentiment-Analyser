# Transaction Sentiment Analyzer (MacquarieHack)

This Spring Boot application is designed to process financial transactions by analyzing the sentiment of their descriptions. It utilizes the **Google Cloud Natural Language API** to detect negative sentiment and automatically censors profanity or "bad words" from the transaction details.

This project appears to be a solution for a hackathon challenge (implied by the repository name), likely focusing on improving customer experience or compliance by monitoring and cleaning up transaction data.

## Features

-   **Sentiment Analysis**: Analyzes transaction descriptions to determine if the sentiment is positive, neutral, or negative using Google Cloud AI.
-   **Profanity Censorship**: Automatically filters out specific keywords defined in a configuration file (`bad_words.json`) if the sentiment is negative.
-   **REST API**: Provides endpoints to submit transactions for processing and retrieve flagged/processed transactions.
-   **Data Persistence**: Stores transaction data using MongoDB.

## Tech Stack

-   **Java 17**
-   **Spring Boot 3.1.2**
    -   Spring Web
    -   Spring Data MongoDB
    -   Spring Boot Starter ActiveMQ (JMS)
-   **Google Cloud Natural Language API**
-   **Gradle** (Build Tool)

## Prerequisites

Before running the application, ensure you have the following installed:

-   [Java 17 SDK](https://www.oracle.com/java/technologies/downloads/#java17)
-   [MongoDB](https://www.mongodb.com/try/download/community) (Running locally or accessible via URI)
-   [Google Cloud Platform Account](https://cloud.google.com/) with the Natural Language API enabled.

## Configuration

### Google Cloud Credentials
The application requires Google Cloud authentication.
1.  Create a Service Account in GCP with permissions to use the **Cloud Natural Language API**.
2.  Download the JSON key file.
3.  Set the `GOOGLE_APPLICATION_CREDENTIALS` environment variable to point to your key file:
    ```bash
    export GOOGLE_APPLICATION_CREDENTIALS="/path/to/your/service-account-file.json"
    ```

### Database
Ensure MongoDB is running on the default port or configure the connection string in `src/main/resources/application.properties` (or `application.yml`).

## Installation & Running

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd MacquarieHack
    ```

2.  **Build the project:**
    ```bash
    ./gradlew build
    ```

3.  **Run the application:**
    ```bash
    ./gradlew bootRun
    ```

## API Endpoints

### 1. Process a Transaction
Analyzes a transaction description, calculates a sentiment score, and censors bad words if the sentiment is negative.

-   **URL:** `/transaction/publish`
-   **Method:** `POST`
-   **Body:**
    ```json
    {
      "transactionId": "12345",
      "description": "This service is terrible and sucks",
      "amount": 100.00
    }
    ```
-   **Response:** Returns the transaction object with the `description` field updated (censored) if necessary.

### 2. Get Negative Transactions
Retrieves a list of transactions stored in the "negative" transaction repository.

-   **URL:** `/transaction`
-   **Method:** `GET`
-   **Response:** JSON array of transactions.

## Project Structure

-   `src/main/java/com/hackathon/controller`: REST implementations (`TransactionController`).
-   `src/main/java/com/hackathon/processor`: Core logic for calling Google Cloud API (`TransactionProcessor`).
-   `src/main/java/com/hackathon/model`: Data entities (`Transaction`, `TransactionNegative`).
-   `src/main/java/com/hackathon/repository`: MongoDB data access layers.
