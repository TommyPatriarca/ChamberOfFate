<?php

// Classe Lobby per rappresentare una singola lobby con nome e ID
class Lobby {
    public $lobbyName; // Nome della lobby
    public $id;        // ID della lobby

    // Costruttore per inizializzare un oggetto Lobby
    public function __construct($lobbyName, $id) {
        $this->lobbyName = $lobbyName;
        $this->id = $id;
    }

    // Metodo opzionale per rappresentare la lobby come stringa
    public function __toString() {
        return "lobbyName: $this->lobbyName, id: $this->id";
    }
}

// Funzione per caricare lobby da un file JSON
function newLobby($filePath) {
    if (!file_exists($filePath)) {
        return []; // Restituisce un array vuoto se il file non esiste
    }

    $jsonData = file_get_contents($filePath);
    $lobbyArray = json_decode($jsonData, true); // Decodifica in array associativo

    if ($lobbyArray === null) {
        throw new Exception("Errore nella decodifica del file JSON.");
    }

    // Converte ogni elemento dell'array in un oggetto Lobby
    $lobbyList = [];
    foreach ($lobbyArray as $lobbyData) {
        $lobbyList[] = new Lobby($lobbyData['lobbyName'], $lobbyData['id']);
    }

    return $lobbyList; // Restituisce un array di oggetti Lobby
}

// Funzione per salvare un array di oggetti Lobby in un file JSON
function salvaLobby($filePath, $lobbyList) {
    $lobbyArray = [];
    foreach ($lobbyList as $lobby) {
        $lobbyArray[] = [
            'lobbyName' => $lobby->lobbyName,
            'id' => $lobby->id
        ];
    }

    $jsonData = json_encode($lobbyArray, JSON_PRETTY_PRINT);
    if (file_put_contents($filePath, $jsonData) === false) {
        throw new Exception("Errore nel salvataggio del file JSON.");
    }
}

// Funzione per creare un file specifico per una lobby
function creaFileLobby($lobbyName, $id) {
    $fileName = $lobbyName . '.json'; // Nome file basato su lobbyName
    $lobbyData = [
        'lobbyName' => $lobbyName,
        'id' => $id,
        'created_at' => date('Y-m-d H:i:s') // Timestamp di creazione
    ];

    $jsonData = json_encode($lobbyData, JSON_PRETTY_PRINT);
    if (file_put_contents($fileName, $jsonData) === false) {
        throw new Exception("Errore nella creazione del file della lobby: $fileName.");
    }
}

// Definisce il percorso del file JSON
$lobbyFile = 'lobby.json';

// Abilita il reporting degli errori per debug
error_reporting(E_ALL);
ini_set('display_errors', 1);

try {
    $httpMethod = $_SERVER['REQUEST_METHOD'];

    if ($httpMethod === 'POST') {
        $azione = $_POST['azione'] ?? ''; // join o create
        $lobbyName = $_POST['lobbyName'] ?? '';
        $id = $_POST['id'] ?? '';
    } elseif ($httpMethod === 'GET') {
        $azione = $_GET['azione'] ?? ''; // join o create
        $lobbyName = $_GET['lobbyName'] ?? '';
        $id = $_GET['id'] ?? '';
    } else {
        throw new Exception('Metodo HTTP non supportato');
    }

    if (empty($azione) || empty($lobbyName) || empty($id)) {
        throw new Exception('Azione, lobbyName e id sono obbligatori');
    }

    $lobbyList = newLobby($lobbyFile);
    if ($_SERVER['REQUEST_METHOD'] === 'GET') {
        $azione = $_GET['azione'] ?? '';

        if ($azione === 'list') {
            $lobbyList = newLobby($lobbyFile); // Carica l'elenco delle lobby dal file
            header('Content-Type: application/json');
            echo json_encode($lobbyList);
            exit;
        }
    }elseif ($azione === 'join') {
        $lobbyTrovata = false;
        foreach ($lobbyList as $lobby) {
            if ($lobby->lobbyName === $lobbyName && $lobby->id === $id) {
                $lobbyTrovata = true;
                break;
            }
        }

        header('Content-Type: application/json');
        if ($lobbyTrovata) {
            echo json_encode(['status' => 'success', 'message' => 'Join riuscito']);
        } else {
            echo json_encode(['status' => 'fail', 'message' => 'Lobby non trovata']);
        }
    } elseif ($azione === 'create') {
          $lobbyEsistente = false;
          foreach ($lobbyList as $lobby) {
              if ($lobby->id === $id || $lobby->lobbyName === $lobbyName) {
                  $lobbyEsistente = true;
                  break;
              }
          }

          header('Content-Type: application/json');
          if ($lobbyEsistente) {
              echo json_encode(['status' => 'fail', 'message' => 'Lobby già esistente']);
          } else {
              // Aggiungi la nuova lobby all'elenco
              $lobbyList[] = new Lobby($lobbyName, $id);

              // Salva l'elenco delle lobby
              salvaLobby($lobbyFile, $lobbyList);

              // Crea un file specifico per la nuova lobby
              creaFileLobby($lobbyName, $id);

              echo json_encode(['status' => 'success', 'message' => 'Creazione riuscita']);
          }
      }


        header('Content-Type: application/json');
        if ($lobbyEsistente) {
            echo json_encode(['status' => 'fail', 'message' => 'Lobby già esistente']);
        } else {
            // Aggiungi la nuova lobby all'elenco
            $lobbyList[] = new Lobby($lobbyName, $id);

            // Salva l'elenco delle lobby
            salvaLobby($lobbyFile, $lobbyList);

            // Crea un file specifico per la nuova lobby
            creaFileLobby($lobbyName, $id);

            echo json_encode(['status' => 'success', 'message' => 'Creazione riuscita']);
        }
    } else {
        throw new Exception('Azione non valida. Usa "join" o "create".');
    }
} catch (Exception $e) {
    http_response_code(400);
    header('Content-Type: application/json');
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
}
?>
