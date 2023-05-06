Table of Contents
- [Project (short-description)](#proiect-short-description)
  - [Tasks](#tasks)
  - [Bonus](#bonus)
  - [Note](#note)
- [Implementation](#implementation)
  - [Protobuf](#protobuf)
  - [Simple publication](#simple-publication)
  - [Complex Publication](#complex-publication)


# Proiect (short-description)
Implementati o arhitectura de sistem publish/subscribe, content-based, care sa fie capabila sa proceseze si secvente de publicatii grupate in ferestre, structurata in felul descris in Tasks.

## Tasks
- (5p) Generati un flux de publicatii care sa fie emis de un nod publisher. Publicatiile pot fi generate cu valori aleatoare pentru campuri folosind generatorul de date din tema practica.
- (10p) Implementati o retea (overlay) de brokeri (2-3) care sa notifice clienti (subscriberi) in functie de o filtrare bazata pe continutul publicatiilor, cu posibilitatea de a procesa inclusiv ferestre (secvente) de publicatii (exemplu mai jos).
- (5p) Simulati 3 noduri subscriber care se conecteaza la reteaua de brokeri si pot inregistra atat susbcriptii simple cat si subscriptii complexe ce necesita o filtrare pe fereastra de publicatii. Subscriptiile pot fi generate cu valori aleatoare pentru campuri folosind generatorul de date din tema practica, modificat pentru a genera si subscriptii pentru ferestre de publicatii (exemplu mai jos).
- (5p) Folositi un mecanism de serializare binara (exemplu - Google Protocol Buffers sau Thrift) pentru transmiterea publicatiilor de la nodul publisher la brokers.
- (10p) Realizati o evaluare a sistemului, masurand pentru inregistrarea a 10000 de subscriptii simple, urmatoarele statistici: a) cate publicatii se livreaza cu succes prin reteaua de brokeri intr-un interval continuu de feed de 3 minute, b) latenta medie de livrare a unei publicatii (timpul de la emitere pana la primire) pentru publicatiile trimise in acelasi interval, c) rata de potrivire (matching) pentru cazul in care subscriptiile generate contin pe unul dintre campuri doar operator de egalitate (100%) comparata cu situatia in care frecventa operatorului de egalitate pe campul respectiv este aproximativ un sfert (25%). Redactati un scurt raport de evaluare a solutiei.

<details>
    <summary>
        Exemple
    </summary>

    Exemplu filtrare subscriptii simple si subscriptii complexe (cu filtrare pe fereastra de publicatii):
    
    Subscriptie simpla: {(city,=,"Bucharest");(temp,>=,10);(wind,<,11)}
    In acest caz un subscriber va fi notificat cu toate publicatiile care au o potrivire pozitiva evaluata prin simpla comparatie a campurilor corespondente din subscriptie si publicatie.
    
    Subscriptie complexa: {(city,=,"Bucharest");(avg_temp,>,8.5);(avg_wind,<=,13)}
    Campurile "avg_" indica in exemplu un criteriu de medie pe o fereastra de publicatii. 
    Se va considera o dimensiune fixa a ferestrei ce va fi determinata pe baza unui contor de publicatii. 
    Dimensiunea ferestrea va fi data ca parametru de configurare a sistemului (ex. 10 publicatii pe fereastra). 
    Un subscriber va fi notificat printr-un mesaj specific in momentul in care apare o fereastra de publicatii in fluxul generat care va avea o potrivire cu respectivul criteriu. 
    In exemplul dat, cand ultimele 10 publicatii care redau starea meteo din Bucuresti au mediile de temperatura si vant dorite de un subscriber, i se va trimite un mesaj de notificare special de tip "meta-publicatie": {(city,=,"Bucharest");(conditions,=,true)}. 
    Se cere implementarea a cel putin un criteriu de procesare pe fereastra pentru un camp. 
    Criteriul poate fi la alegere (medie, maxim, etc.) iar modul de avans al ferestrei va fi tumbling window (fiecare fereastra va urma distinct in succesiune celei anterioare dupa completarea numarului de publicatii care o compun). 
    Nu se cere tratarea situatiilor de inordine a publicatiilor dintr-o fereastra.
</details>

## Bonus

- (5p) Implementati un mecanism avansat de rutare la inregistrarea subscriptiilor simple ce ar trebui sa fie distribuite in reteaua de brokeri (publicatiile vor trece prin mai multi brokeri pana la destinatie, fiecare ocupandu-se partial de rutare, si nu doar unul care contine toate subscriptiile si face un simplu match).
- (5p) Simulati si tratati (prin asigurare de suport in implementare) cazuri de caderi pe nodurile broker, care sa asigure ca nu se pierd notificari, inclusiv pentru cazul subscriptiilor complexe.
- (5-10p) Implementati o modalitate de filtrare a mesajelor care sa nu permita brokerilor accesul la continutul mesajelor (match pe subscriptii/publicatii criptate).

## Note
- Proiectul poate fi realizat in echipe de pana la 3 studenti si va fi prezentat la o data ce va fi stabilita in perioada de sesiune.
- Proiectul poate fi implementat utilizand orice limbaj sau platforma. In cazul in care se va folosi Apache Kafka in implementare, utilizarea acestei platforme va fi limitata doar pentru livrarea mesajelor, asigurandu-se conectarea cu implementarea ce va folosi o alta solutie pentru partea efectiva de serviciu de procesare a datelor (filtrarea bazata pe continut, stocare subscriptii, etc).
- Nodurile distincte (publisher, subscribers, brokers) pot fi simulate de exemplu prin procese separate rulate pe acelasi sistem.

# Implementation

## Protobuf

We are using the protoc command:

    protoc --java_out=<path> <filename.proto>
And this generates the required .java files that we'll be using in our project.


## Simple publication
    syntax = "proto3";

    option java_package = "org.project.models";
    option java_outer_classname = "ProtoSimplePublication";

    message SimplePublication {
        string uuid = 1;
        string station_id = 2;
        Location location = 3;
        string city = 4;
        double temperature = 5;
        double rain = 6;
        double wind = 7;
        string direction = 8;
        string date = 9;
    }

    message Location {
        double latitude = 1;
        double longitude = 2;
    }

- uuid: Unique identifier of the data.
- station_id: A string field for the unique identifier of the weather station.
- location - A message field containing latitude and longitude of the station.
- city: A string field for the city where the weather station is located.
- temperature: A double field for the current temperature reading in Celsius.
- rain: A double field for the current rain reading in millimeters.
- wind: A double field for the current wind speed reading in kilometers per hour.
- direction: A string field for the current wind direction reading.
- date: A string field for the date and time of the weather reading in ISO 8601 format.

## Complex Publication

    syntax = "proto3";

    option java_package = "org.project.models";
    option java_outer_classname = "ProtoComplexPublication";

    message ComplexPublication {
        string uuid = 1;
        string city = 2;
        Location location = 3;
        double avg_temperature = 4;
        double avg_rain = 5;
        double avg_wind = 6;
    }

    message Location {
        double latitude = 1;
        double longitude = 2;
    }

- uuid: Unique identifier of the data.
- city: This field is a string that represents the name of the city where the weather station is located.
- location: This field is a message type that contains the latitude and longitude coordinates of the weather station's location. It is defined by the Location message, which has two double fields named latitude and longitude.
- avg_temperature: This field is a double that represents the average temperature recorded by the weather station over a period of time, such as a day or a month.
- avg_rain: This field is a double that represents the average amount of rainfall recorded by the weather station over a period of time, such as a day or a month.
- avg_wind: This field is a double that represents the average wind speed recorded by the weather station over a period of time, such as a day or a month.
