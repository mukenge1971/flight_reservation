# flight_reservation
# Application de réservation de billets d'avion.

- Explication

Classe Flight : Représente un vol avec des informations telles que le numéro de vol, le départ, la destination, les heures de départ et d'arrivée, et le nombre de sièges disponibles.
Classe Passenger : Représente un passager avec des informations telles que l'ID du passager, le nom et les informations de contact.
Classe Reservation : Représente une réservation avec des informations telles que l'ID de réservation, le passager, le vol et l'état de la réservation (annulée ou non).
Classe AirlineReservationSystem : La classe principale qui gère les réservations, les annulations et l'affichage des réservations. Elle utilise une liste de vols et de réservations pour stocker les données.
Fonctionnalités
Réservation et annulation des billets d’avion : Les utilisateurs peuvent réserver et annuler des billets d'avion.
Automatisation des fonctions du système aérien : Le système gère automatiquement les sièges disponibles et les réservations.
Gestion et routage des transactions : Les transactions de réservation et d'annulation sont gérées de manière centralisée.
Réponses rapides aux clients : Le système fournit des réponses immédiates aux utilisateurs.
Gestion des dossiers passagers et des transactions commerciales quotidiennes : Le système tient des dossiers passagers et des transactions commerciales quotidiennes.
Ce projet peut être étendu avec des fonctionnalités supplémentaires telles que la gestion des paiements, l'intégration avec des bases de données, et une interface utilisateur graphique.

- Détails techniques

Fichiers séparés : Chaque classe est placée dans un fichier séparé pour maintenir une structure
 de code propre et organisée.
Classe Flight : Représente un vol avec des informations telles que le numéro de vol, le départ, 
la destination, les heures de départ et d'arrivée, et le nombre de sièges disponibles.
Classe Passenger : Représente un passager avec des informations telles que l'ID du passager, 
le nom et les informations de contact.
Classe Reservation : Représente une réservation avec des informations telles que l'ID de réservation, 
le passager, le vol et l'état de la réservation (annulée ou non).
Classe AirlineReservationSystem : La classe principale qui gère les réservations, les annulations 
et l'affichage des réservations. Elle utilise une liste de vols et de réservations pour stocker les données.
Compilation et Exécution
Compilation : javac -d bin src/*.java

Exécution : java -cp "libs/sqlite-jdbc-3.46.1.0.jar:libs/slf4j-api-2.0.9.jar:libs/slf4j-simple-2.0.9.jar:bin" AirlineReservationSystem


Sans le dossier bin, on compile de la manière suivante :
- javac src/*.java

et lorsqu'on utilise pas une base de données
- java src.AirlineReservationSystem
