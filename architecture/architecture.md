# Architectuur Overzicht

## 1. Frontend (Angular)
- **Beschrijving**: Het frontend is gebouwd met Angular en vormt de interface voor de eindgebruikers. Het communiceert met de backend via de API Gateway.
- **Communicatie**: Synchronous communicatie met de API Gateway.

## 2. API Gateway
- **Beschrijving**: De API Gateway fungeert als de enige toegangspoort voor het frontend om verbinding te maken met de backend-services. Het stuurt binnenkomende verzoeken door naar de juiste microservice (Post, Review of Comment Service).
- **Communicatie**: Synchronous communicatie tussen de frontend en de microservices.

## 3. Microservices
- **Post Service**:
  - **Beschrijving**: Beheert het maken, lezen, updaten en verwijderen van berichten.
  - **Database**: Heeft een eigen PostgreSQL database (PostDB) voor het opslaan van gegevens die gerelateerd zijn aan berichten.
- **Review Service**:
  - **Beschrijving**: Beheert de reviews voor berichten.
  - **Database**: Heeft een eigen PostgreSQL database (ReviewDB).
- **Comment Service**:
  - **Beschrijving**: Beheert de reacties op berichten.
  - **Database**: Heeft een eigen PostgreSQL database (CommentDB).
- **Opmerking**: Elke service is een zelfstandige microservice met een eigen database, wat onafhankelijk schalen en ontwikkelen mogelijk maakt.

## 4. Communicatie Tussen Services
- **OpenFeign**:
  - **Beschrijving**: OpenFeign wordt gebruikt om op een eenvoudige manier HTTP-verzoeken tussen microservices te versturen.
  - **Communicatie**: Synchronous communicatie tussen de microservices.
- **Message Bus (RabbitMQ)**:
  - **Beschrijving**: RabbitMQ wordt gebruikt als message bus voor asynchrone communicatie tussen services. Dit stelt services in staat om berichten te verzenden en ontvangen zonder directe koppeling.
  - **Communicatie**: Asynchronous communicatie, voornamelijk voor gebeurtenissen die niet onmiddellijk een antwoord vereisen (bijvoorbeeld notificaties of updates).

## 5. Eureka Discovery Service
- **Beschrijving**: Alle microservices registreren zich bij de Eureka Discovery Service, waardoor ze elkaar dynamisch kunnen ontdekken. Dit ondersteunt schaalbaarheid en flexibiliteit in servicecommunicatie.

## 6. Config Service
- **Beschrijving**: De Config Service centraliseert het configuratiebeheer voor alle microservices, wat helpt om de configuratiegegevens consistent te houden en het onderhoud te vereenvoudigen.

## Overzicht van Communicatietypes
- **Synchronous Communicatie**: Wordt afgehandeld door de API Gateway en OpenFeign. De frontend en microservices communiceren direct met elkaar wanneer onmiddellijke respons vereist is.
- **Asynchronous Communicatie**: Wordt afgehandeld door RabbitMQ, dat een message bus biedt voor taken die geen onmiddellijke respons vereisen, zoals achtergrondverwerking of notificaties.